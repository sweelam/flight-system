CREATE TABLE outbox (
    id serial primary key ,
    type varchar(255),
    payload text,
    status varchar(255),
    created_at timestamp DEFAULT CURRENT_TIMESTAMP
);