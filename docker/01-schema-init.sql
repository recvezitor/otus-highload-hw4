CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS gin_trgm_ops;

DROP SCHEMA IF EXISTS otus_highload CASCADE;
CREATE SCHEMA otus_highload;
set search_path to otus_highload;

CREATE TABLE person
(
    id          uuid PRIMARY KEY                     DEFAULT gen_random_uuid(),
    first_name  varchar(1024)               NOT NULL,
    second_name varchar(1024)               NOT NULL,
    birthdate   date,
    biography   TEXT,
    city        varchar(1024),
    password    varchar                     NOT NULL default 123,
    created_at  timestamp without time zone not null default now(),
    updated_at  timestamp without time zone
);

CREATE TABLE post
(
    id         uuid PRIMARY KEY                     DEFAULT gen_random_uuid(),
    from_user  uuid                        NOT NULL,
    to_user    uuid,
    text       TEXT,
    created_at timestamp without time zone not null default now(),
    updated_at timestamp without time zone,
    CONSTRAINT fk_post_from_user FOREIGN KEY (from_user) REFERENCES person (id)
);


CREATE TABLE friend_request
(
    user_id    uuid                        NOT NULL,
    friend_id  uuid                        NOT NULL,
    created_at timestamp without time zone not null default now(),
    updated_at timestamp without time zone,
    PRIMARY KEY (user_id, friend_id),
    CONSTRAINT fk_fr_user_id FOREIGN KEY (user_id) REFERENCES person (id),
    CONSTRAINT fk_fr_friend_id FOREIGN KEY (friend_id) REFERENCES person (id)
);

CREATE INDEX index_users_full_name ON otus_highload.person using gin (first_name gin_trgm_ops, second_name gin_trgm_ops);