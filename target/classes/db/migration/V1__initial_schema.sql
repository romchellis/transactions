CREATE TABLE users
(
    id      SERIAL PRIMARY KEY,
    balance INT,
    version INTEGER default 0
);
