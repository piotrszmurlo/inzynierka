create table files
(
    id              int auto_increment
        primary key,
    contents        text         null,
    algorithm_name  varchar(255) null,
    dimension       int          null,
    function_number int          null,
    constraint files_pk
        unique (algorithm_name, function_number, dimension)
);

create index ix_files_id
    on files (id);

