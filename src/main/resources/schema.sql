create table users
(
    uid        bigint primary key,
    name       varchar(255) not null,
    email      varchar(255) not null Unique,
    password   varchar(1000),
    created_at timestamp    not null,
    created_by varchar(255),
    updated_at timestamp,
    updated_by varchar(255),
    deleted_at timestamp,
    deleted_by varchar(255)
);

CREATE TABLE messages
(
    message_id      int PRIMARY KEY,
    locale          VARCHAR(100)  NOT NULL,
    message_key     VARCHAR(255)  NOT NULL,
    message_content varchar(1000) NOT NULL,
    created_at timestamp    not null,
    created_by varchar(255),
    updated_at timestamp,
    updated_by varchar(255),
    deleted_at timestamp,
    deleted_by varchar(255)
    CONSTRAINT unique_locale_key UNIQUE (locale, message_key)
);
