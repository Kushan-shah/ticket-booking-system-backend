import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Counter, Trend } from 'k6/metrics';

// ─── Custom Metrics ───────────────────────────────────────────────
const bookingSuccess  = new Counter('booking_success_total');
const bookingConflict = new Counter('booking_conflict_total');
const bookingLatency  = new Trend('booking_lock_latency', true);

// ─── Test Scenarios ───────────────────────────────────────────────
export const options = {
    scenarios: {

        // Scenario 1: Seat Contention Race (Core Optimistic Locking Validation)
        seat_contention: {
            executor: 'shared-iterations',
            vus: 50,
            iterations: 100,
            maxDuration: '30s',
            exec: 'seatContention',
        },

        // Scenario 2: API Throughput Under Sustained Load
        sustained_load: {
            executor: 'constant-vus',
            vus: 20,
            duration: '15s',
            startTime: '5s',   // Start after contention test completes
            exec: 'sustainedLoad',
        },

        // Scenario 3: Spike Test (Sudden Traffic Burst)
        spike_test: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '3s',  target: 30 },  // Ramp up to 30 users in 3s
                { duration: '5s',  target: 30 },  // Hold at 30 for 5s
                { duration: '2s',  target: 0 },   // Ramp down
            ],
            startTime: '22s',   // Start after sustained load
            exec: 'spikeTest',
        },
    },
    thresholds: {
        'http_req_duration': ['p(95)<500'],         // 95% of requests under 500ms
        'booking_lock_latency': ['p(90)<300'],      // 90% of lock attempts under 300ms
    },
};

// ─── Setup: Register and Login ────────────────────────────────────
export function setup() {
    const BASE = 'http://localhost:8080';
    const params = { headers: { 'Content-Type': 'application/json' } };

    const email = `k6-full-${Date.now()}@test.com`;
    const creds = JSON.stringify({ email, password: 'Password123!', role: 'USER' });

    http.post(`${BASE}/auth/register`, creds, params);
    const loginRes = http.post(`${BASE}/auth/login`, creds, params);

    return { token: loginRes.json('token'), base: BASE };
}

// ─── Scenario 1: Seat Contention Race ────────────────────────────
// All 50 VUs fight for the EXACT same seat simultaneously.
// Expected: Exactly 1 success, all others get 409 Conflict.
export function seatContention(data) {
    const payload = JSON.stringify({ eventId: 1, seatNumber: 'B1' });
    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${data.token}`,
        },
    };

    const res = http.post(`${data.base}/bookings/lock-seat`, payload, params);
    bookingLatency.add(res.timings.duration);

    check(res, {
        '[Contention] is success (201)':    (r) => r.status === 201,
        '[Contention] is conflict (409)':   (r) => r.status === 409,
        '[Contention] no crash (500)':      (r) => r.status !== 500,
    });

    if (res.status === 201) bookingSuccess.add(1);
    if (res.status === 409) bookingConflict.add(1);

    sleep(0.1);
}

// ─── Scenario 2: Sustained Load (Read-heavy) ─────────────────────
// 20 VUs continuously hit read endpoints for 15 seconds.
// Validates: Caching, EntityGraph, Pagination under pressure.
export function sustainedLoad(data) {
    const params = {
        headers: { 'Content-Type': 'application/json' },
    };

    group('GET /events (paginated + cached)', () => {
        const res = http.get(`${data.base}/events?page=0&size=10`, params);
        check(res, {
            '[Sustained] events 200 OK': (r) => r.status === 200,
            '[Sustained] events < 300ms': (r) => r.timings.duration < 300,
        });
    });

    group('GET /events/1 (single event)', () => {
        const res = http.get(`${data.base}/events/1`, params);
        check(res, {
            '[Sustained] event detail 200 OK': (r) => r.status === 200,
        });
    });

    sleep(0.5);
}

// ─── Scenario 3: Spike Test (Auth Endpoint Resilience) ───────────
// Ramps from 0 to 30 VUs instantly, hammering the auth endpoint.
// Validates: Server stability under sudden traffic bursts.
export function spikeTest(data) {
    const params = { headers: { 'Content-Type': 'application/json' } };
    const email = `spike-${__VU}-${__ITER}-${Date.now()}@test.com`;
    const creds = JSON.stringify({ email, password: 'Password123!', role: 'USER' });

    group('POST /auth/register (spike)', () => {
        const res = http.post(`${data.base}/auth/register`, creds, params);
        check(res, {
            '[Spike] register 2xx':  (r) => r.status >= 200 && r.status < 300,
            '[Spike] register < 500ms': (r) => r.timings.duration < 500,
        });
    });

    group('POST /auth/login (spike)', () => {
        const res = http.post(`${data.base}/auth/login`, creds, params);
        check(res, {
            '[Spike] login 200 OK':  (r) => r.status === 200,
            '[Spike] login < 500ms': (r) => r.timings.duration < 500,
        });
    });

    sleep(0.3);
}
