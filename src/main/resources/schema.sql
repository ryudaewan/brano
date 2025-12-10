create table users
(
    uid        bigint primary key,
    name       varchar(255) not null,
    email      varchar(255) not null Unique,
    password   varchar(1000),
    created_at timestamp    not null,
    updated_at timestamp,
    deleted_at timestamp
);

CREATE TABLE messages
(
    message_id      int PRIMARY KEY,
    locale          VARCHAR(100)  NOT NULL,
    message_key     VARCHAR(255)  NOT NULL,
    message_content varchar(1000) NOT NULL,
    created_at      timestamp     not null,
    updated_at      timestamp,
    deleted_at      timestamp,
    CONSTRAINT unique_locale_key UNIQUE (locale, message_key)
);
