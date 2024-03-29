CREATE TABLE customer (
    id int2 GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    "limit" int4 NOT NULL,
    balance int4 NOT NULL
);

CREATE TYPE transfer_type AS ENUM ('CREDIT', 'DEBIT');

CREATE TABLE transfer (
    id int4 GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    customer_id int2 NOT NULL,
    "type" transfer_type NOT NULL,
    value int4 NOT NULL,
    description varchar(10) NOT NULL,
    executed_at timestamp NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

INSERT INTO customer (id, "limit", balance) VALUES
    (1, 100000, 0),
    (2, 80000, 0),
    (3, 1000000, 0),
    (4, 10000000, 0),
    (5, 500000, 0);
