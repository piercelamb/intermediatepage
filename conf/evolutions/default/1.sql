# --- Person schema

# --- !Ups

CREATE TABLE person (
    id bigserial NOT NULL,
    ip varchar(255),
    city varchar(255),
    regionName varchar(255),
    regioncode varchar(255),
    country varchar(255),
    countrycode varchar(255),
    firstname varchar(255),
    middlename varchar(255),
    lastname varchar(255),
    nameraw varchar(255),
    email varchar(255),
    companyraw varchar(255),
    twitterid varchar(255),
    screenname varchar(255),
    followercount bigint,
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE person;