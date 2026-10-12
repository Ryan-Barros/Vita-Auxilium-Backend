create table activities
(
    is_active                     boolean      not null,
    activity_end                  timestamp(6) with time zone,
    activity_start                timestamp(6) with time zone,
    activity_id                   uuid         not null,
    category_id_categoria         uuid,
    environment_id_environment_id uuid,
    activity_description          varchar(255),
    activity_name                 varchar(255) not null,
    activity_status               varchar(255) not null check ((activity_status in ('NOT_STARTED', 'RUNNING', 'FINISHED'))),
    primary key (activity_id)
);

create table categories
(
    id_categoria   uuid         not null,
    name_categoria varchar(255) not null unique,
    primary key (id_categoria)
);

alter table if exists activities
    add constraint FK9d7aewl2hxty3lvonplt1qij
    foreign key (environment_id_environment_id)
    references environments;

alter table if exists activities
    add constraint FK88phtdpyalhynkpbvk1lmaygq
    foreign key (category_id_categoria)
    references categories;