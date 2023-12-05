create table files
(
    id              int auto_increment
        primary key,
    contents        text         null,
    algorithm_name  varchar(255) null,
    dimension       int          null,
    function_number int          null,
    constraint uix_1
        unique (algorithm_name, dimension, function_number)
);

create index ix_files_id
    on files (id);

create table users
(
    id                int auto_increment
        primary key,
    email             varchar(255) null,
    password_hash     text         null,
    disabled          tinyint(1)   null,
    is_admin          tinyint(1)   null,
    verification_hash text         null,
    constraint email
        unique (email)
);

create index ix_users_id
    on users (id);

