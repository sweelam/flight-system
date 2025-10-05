CREATE TABLE outbox (
    id serial primary key ,
    type varchar(255),
    payload json,
    status varchar(255),
    topic varchar(255) NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP
);