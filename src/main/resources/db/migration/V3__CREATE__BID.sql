CREATE SEQUENCE IF NOT EXISTS project.bid_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE project.bid (
    id integer,
    amount integer NOT NULL,
    timestamp timestamp,
    product_id UUID REFERENCES project.product(id),

    PRIMARY KEY (id)
);