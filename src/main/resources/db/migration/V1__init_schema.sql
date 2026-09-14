-- =============================================================================
-- 1. ESTRUTURA INICIAL DAS TABELAS DO VITAAUXILIUM (GERADO PELO HIBERNATE)
-- =============================================================================

create sequence revinfo_seq start with 1 increment by 50;

create table activities (activity_id uuid not null, primary key (activity_id));
create table activities_aud (rev integer not null, revtype smallint, activity_id uuid not null, primary key (rev, activity_id));

create table device_data (
                             accelerometer numeric(38,2) not null,
                             accelerometerx numeric(38,2) not null,
                             accelerometery numeric(38,2) not null,
                             accelerometerz numeric(38,2) not null,
                             alert integer not null,
                             bpm integer not null,
                             bpm_media integer not null,
                             bpm_valid integer not null,
                             chance_of_fall integer not null,
                             contact_sensor integer not null,
                             detected_fall integer not null,
                             gyroscope numeric(38,2) not null,
                             gyroscopex numeric(38,2) not null,
                             gyroscopey numeric(38,2) not null,
                             gyroscopez numeric(38,2) not null,
                             movement numeric(38,2) not null,
                             movement_level integer not null,
                             on_time integer not null,
                             oxygenation integer not null,
                             sensor_max30102_heart integer not null,
                             sensor_max30205_temperature integer not null,
                             sensor_mpu6050_fall integer not null,
                             spo2_valid integer not null,
                             temperature numeric(38,2) not null,
                             wifirssi integer not null,
                             date timestamp(6) not null,
                             device_data_id uuid not null,
                             device_id uuid not null,
                             primary key (device_data_id)
);

create table device_data_aud (accelerometer numeric(38,2), accelerometerx numeric(38,2), accelerometery numeric(38,2), accelerometerz numeric(38,2), alert integer, bpm integer, bpm_media integer, bpm_valid integer, chance_of_fall integer, contact_sensor integer, detected_fall integer, gyroscope numeric(38,2), gyroscopex numeric(38,2), gyroscopey numeric(38,2), gyroscopez numeric(38,2), movement numeric(38,2), movement_level integer, on_time integer, oxygenation integer, rev integer not null, revtype smallint, sensor_max30102_heart integer, sensor_max30205_temperature integer, sensor_mpu6050_fall integer, spo2_valid integer, temperature numeric(38,2), wifirssi integer, date timestamp(6), device_data_id uuid not null, primary key (rev, device_data_id));
create table devices (device_id uuid not null, environment_id uuid not null unique, device_name varchar(255) not null, device_token_hash varchar(255) not null unique, primary key (device_id));
create table environment_members (environment_member_id uuid not null, member_environment_id uuid not null, member_user_id uuid not null, primary key (environment_member_id), constraint uk_environment_member_user_environment unique (member_user_id, member_environment_id));
create table environment_members_aud (rev integer not null, revtype smallint, environment_member_id uuid not null, member_environment_id uuid, member_user_id uuid, primary key (rev, environment_member_id));
create table environments (is_active boolean not null, expiration_date timestamp(6) with time zone not null, environment_id uuid not null, environment_code varchar(255) not null unique, environment_name varchar(255) not null, primary key (environment_id));
create table environments_aud (is_active boolean, rev integer not null, revtype smallint, expiration_date timestamp(6) with time zone, environment_id uuid not null, environment_code varchar(255), environment_name varchar(255), primary key (rev, environment_id));
create table reminders (reminder_id uuid not null, primary key (reminder_id));
create table reminders_aud (rev integer not null, revtype smallint, reminder_id uuid not null, primary key (rev, reminder_id));
create table revinfo (rev integer not null, revtstmp bigint, primary key (rev));
create table subscription_plan (subscription_name smallint check ((subscription_name between 0 and 2)), plan_id uuid not null, primary key (plan_id));
create table users (active boolean not null, user_phone varchar(11) not null, active_ambient uuid, user_id uuid not null, user_name varchar(45) not null, user_password varchar(60), user_email varchar(100) not null unique, cpf varchar(255), user_picture varchar(255), user_profile varchar(255) not null check ((user_profile in ('FAMILY','CAREGIVER','ELDERLY'))), primary key (user_id));
create table users_aud (active boolean, rev integer not null, revtype smallint, user_phone varchar(11), active_ambient uuid, user_id uuid not null, user_name varchar(45), user_password varchar(60), user_email varchar(100), cpf varchar(255), user_picture varchar(255), user_profile varchar(255) check ((user_profile in ('FAMILY','CAREGIVER','ELDERLY'))), primary key (rev, user_id));
create table users_oauth (created_at timestamp(6) with time zone not null, oauth_id uuid not null, oauth_user_id uuid not null unique, oauth_provider varchar(255) not null check ((oauth_provider in ('GOOGLE'))), oauth_sub varchar(255) not null, primary key (oauth_id));

alter table if exists activities_aud add constraint FK1hpf6bko3m4d065p7mwowt8v1 foreign key (rev) references revinfo;
alter table if exists device_data add constraint FKqp5k1ic2hj0q907x8qlf1xh99 foreign key (device_id) references devices;
alter table if exists device_data_aud add constraint FK1y5dku6m22jg0u2u1y41h7q2m foreign key (rev) references revinfo;
alter table if exists devices add constraint FK2wkaef8iujx7gu60f8wft6kjm foreign key (environment_id) references environments;
alter table if exists environment_members add constraint FKlxxbc42bxctjjanvnsj56snro foreign key (member_environment_id) references environments;
alter table if exists environment_members add constraint FKdvbe4ra3tigkld6cpv7xvaukb foreign key (member_user_id) references users;
alter table if exists environment_members_aud add constraint FK7x7qkmovt6jea86qy20iyl9fp foreign key (rev) references revinfo;
alter table if exists environments_aud add constraint FKm5j6owk05if44ob179nl7fru1 foreign key (rev) references revinfo;
alter table if exists reminders_aud add constraint FKkjywy4p8sxvv1lsrlned1s12f foreign key (rev) references revinfo;
alter table if exists users_aud add constraint FKc4vk4tui2la36415jpgm9leoq foreign key (rev) references revinfo;
alter table if exists users_oauth add constraint FKj1yge53cov38we6jtixdf0koi foreign key (oauth_user_id) references users;

-- =============================================================================
-- 2. CONFIGURAÇÃO DO TIMESCALEDB NA TABELA DEVICE_DATA
-- =============================================================================

CREATE EXTENSION IF NOT EXISTS timescaledb;

-- Remove a PK isolada no UUID exigida pelo PostgreSQL padrão
ALTER TABLE device_data DROP CONSTRAINT device_data_pkey;

-- Converte a tabela device_data usando a coluna 'date' como tempo do TimescaleDB
SELECT create_hypertable('device_data', 'date');

-- Cria o índice auxiliar para buscas diretas por ID de telemetria
CREATE INDEX idx_device_data_id ON device_data (device_data_id);

-- =============================================================================
-- 3. CONFIGURANDO DEMAIS EXTENSÕES
-- =============================================================================

-- Cria extensão unaccent
CREATE EXTENSION IF NOT EXISTS unaccent;

-- Cria função para unaccent
CREATE OR REPLACE FUNCTION immutable_unaccent(text)
RETURNS text AS $$
    SELECT public.unaccent($1)
$$ LANGUAGE sql IMMUTABLE PARALLEL SAFE STRICT;

-- Cria index em users
CREATE INDEX IF NOT EXISTS idx_users_name_unaccent
ON users (immutable_unaccent(user_name) text_pattern_ops);