CREATE SEQUENCE IF NOT EXISTS project.category_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE project.category (
    id integer,
    name text NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE project.product (
    id uuid,
    name text NOT NULL,
    description text,
    starting_price integer,
    category_id INTEGER REFERENCES project.category(id),

    PRIMARY KEY (id)
);