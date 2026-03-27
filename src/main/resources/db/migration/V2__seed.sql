-- Insert default events
INSERT INTO events (name, date, location) VALUES ('Coldplay Concert', '2026-12-01 20:00:00', 'Mumbai Stadium');
INSERT INTO events (name, date, location) VALUES ('Tech Conference 2026', '2026-10-15 09:00:00', 'Bangalore Exhibition Centre');

-- Insert seats for Coldplay Concert (Event ID 1)
INSERT INTO seats (event_id, seat_number, status, version) VALUES (1, 'A1', 'AVAILABLE', 0);
INSERT INTO seats (event_id, seat_number, status, version) VALUES (1, 'A2', 'AVAILABLE', 0);
INSERT INTO seats (event_id, seat_number, status, version) VALUES (1, 'A3', 'AVAILABLE', 0);
INSERT INTO seats (event_id, seat_number, status, version) VALUES (1, 'B1', 'AVAILABLE', 0);
INSERT INTO seats (event_id, seat_number, status, version) VALUES (1, 'B2', 'AVAILABLE', 0);

-- Insert seats for Tech Conference (Event ID 2)
INSERT INTO seats (event_id, seat_number, status, version) VALUES (2, 'VIP-1', 'AVAILABLE', 0);
INSERT INTO seats (event_id, seat_number, status, version) VALUES (2, 'VIP-2', 'AVAILABLE', 0);
