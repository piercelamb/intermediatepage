# --- Person schema

# --- !Ups

CREATE TABLE person (
    id bigserial NOT NULL,
    ip varchar(255),
    city varchar(255),
    regionName varchar(255),
    country varchar(255),
    firstname varchar(255),
    lastname varchar(255),
    email varchar(255),
    CONSTRAINT person_pkey PRIMARY KEY (id)

);

# --- !Downs

DROP TABLE person;