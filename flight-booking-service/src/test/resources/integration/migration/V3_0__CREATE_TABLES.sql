CREATE TABLE bookings (
    booking_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    flight_id INT NOT NULL,
    booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_flight_id ON bookings(flight_id);
CREATE INDEX idx_bookings_status ON bookings(status);


CREATE TABLE outbox (
                        id serial primary key ,
                        type varchar(255),
                        payload json,
                        status varchar(255),
                        topic varchar(255) NOT NULL,
                        created_at timestamp DEFAULT CURRENT_TIMESTAMP
);