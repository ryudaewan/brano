create table users (uid serial primary key
  , name varchar(255) not null
  , email varchar(255) not null Unique
  , password varchar(1000)
  , created_at timestamp not null
  , updated_at timestamp
  , deleted_at timestamp
);
