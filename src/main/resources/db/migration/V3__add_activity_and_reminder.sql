create table categories
(
    id_categoria   uuid         not null primary key,
    name_categoria varchar(255) not null unique
);

create table activities
(
    activity_id           uuid         not null primary key,
    environment_id        uuid         not null references environments (environment_id),
    category_id_categoria uuid         references categories (id_categoria),
    activity_name         varchar(255) not null,
    activity_description  varchar(255),
    activity_start        timestamp(6),
    activity_end          timestamp(6),
    activity_status       varchar(255) not null
        check (activity_status in ('NOT_STARTED', 'RUNNING', 'FINISHED')),
    is_active             boolean      not null default true
);
create index idx_activities_environment on activities (environment_id);

create table activities_aud
(
    activity_id           uuid    not null,
    rev                   integer not null references revinfo (rev),
    revtype               smallint,
    environment_id        uuid,
    category_id_categoria uuid,
    activity_name         varchar(255),
    activity_description  varchar(255),
    activity_start        timestamp(6),
    activity_end          timestamp(6),
    activity_status       varchar(255),
    is_active             boolean,
    primary key (rev, activity_id)
);

create table reminders
(
    reminder_id                 uuid         not null primary key,
    environment_environment_id  uuid references environments (environment_id),
    category_id_categoria       uuid references categories (id_categoria),
    reminder_tittle             varchar(90)  not null,
    reminder_description        varchar(255),
    reminder_date               timestamp(6) not null,
    reminder_loop               boolean,
    is_active                   boolean
);

create table reminders_aud
(
    reminder_id                 uuid    not null,
    rev                         integer not null references revinfo (rev),
    revtype                     smallint,
    environment_environment_id  uuid,
    category_id_categoria       uuid,
    reminder_tittle             varchar(90),
    reminder_description        varchar(255),
    reminder_date               timestamp(6),
    reminder_loop               boolean,
    is_active                   boolean,
    primary key (rev, reminder_id)
);

create table humor
(
    humor_id             uuid         not null primary key,
    activity_activity_id uuid unique references activities (activity_id),
    humor                varchar(255) not null,
    humor_intensity      integer      not null,
    observation          varchar(255),
    registration_time    timestamp(6) with time zone
);