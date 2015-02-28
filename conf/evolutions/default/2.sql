# --- Account schema

# --- !Ups

CREATE TABLE account (
    id         bigserial NOT NULL PRIMARY KEY,
    email      varchar NOT NULL UNIQUE,
    password   varchar NOT NULL,
    name       varchar NOT NULL,
    role       varchar NOT NULL
);

# --- !Downs

DROP TABLE account;