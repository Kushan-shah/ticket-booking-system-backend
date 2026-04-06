import http from 'k6/http';
import { check, sleep } from 'k6';

// Test Configuration
export const options = {
    scenarios: {
        concurrent_booking: {
            executor: 'shared-iterations',
            vus: 50, // 50 users hammering the server simultaneously
            iterations: 100, // 100 total attempts
            maxDuration: '10s',
        },
    },
};

// The setup phase runs once before the main test.
// We use it to get a valid JWT token.
export function setup() {
    const url = 'http://localhost:8080';
    
    // 1. Create a dummy user for testing
    const registerPayload = JSON.stringify({
        email: `k6-test-${Date.now()}@test.com`,
        password: 'Password123!',
        role: 'USER'
    });
    
    const params = {
        headers: { 'Content-Type': 'application/json' },
    };
    
    http.post(`${url}/auth/register`, registerPayload, params);
    
    // 2. Login to get the JWT
    const loginRes = http.post(`${url}/auth/login`, registerPayload, params);
    
    // Return the token so all Virtual Users (VUs) can use it
    return { token: loginRes.json('token') };
}

// The main test execution
export default function (data) {
    const url = 'http://localhost:8080/bookings/lock-seat';
    const payload = JSON.stringify({
        eventId: 1, // Must exist in your DB (from V2__seed.sql)
        seatNumber: "A3"   // MUST be the exact same seat to force a concurrency race condition
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${data.token}`,
        },
    };

    const res = http.post(url, payload, params);

    // If Optimistic Locking is working perfectly:
    // Exactly ONE request will succeed (201 Created or 200 OK)
    // ALL other requests will fail with 409 Conflict (OptimisticLockingFailureException)
    check(res, {
        'is success (201)': (r) => r.status === 201,
        'is conflict (409)': (r) => r.status === 409,
        'no server crashes (500)': (r) => r.status !== 500,
    });

    // Small sleep to simulate network jitter
    sleep(0.1);
}
