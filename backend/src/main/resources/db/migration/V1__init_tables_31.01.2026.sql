create table dm_user
(
    id                        varchar(40)                  not null,
    name                      varchar(256)                 not null,
    email                     varchar(256)                 not null,
    zone_id                   varchar(256)                 not null,
    created_at                timestamp with time zone     not null,
    updated_at                timestamp with time zone     not null,
    created_by                varchar(40)                  not null,
    updated_by                varchar(40)                  not null,
    is_deleted                boolean                      not null,
    primary key (id),
    unique (email)
);

create index ix_user_created_by on dm_user(created_by);
create index ix_user_updated_by on dm_user(updated_by);

create table dm_calendar
(
    id                        varchar(40)                  not null,
    managed_by_user_id        varchar(40)                  not null,
    subject_user_id           varchar(40)                  not null,
    title                     varchar(256)                 not null,
    description               text                                 ,
    type                      varchar(56)                  not null,
    allow_overlap             boolean                      not null,
    created_at                timestamp with time zone     not null,
    updated_at                timestamp with time zone     not null,
    created_by                varchar(40)                  not null,
    updated_by                varchar(40)                  not null,
    is_deleted                boolean                      not null,
    primary key (id),
    foreign key (created_by) references dm_user (id),
    foreign key (updated_by) references dm_user (id),
    foreign key (managed_by_user_id) references dm_user (id),
    foreign key (subject_user_id) references dm_user (id)
);
create unique index ux_calendar_manager_subject_active
    on dm_calendar(managed_by_user_id, subject_user_id)
    where is_deleted = false;
create index ix_calendar_managed_by_user_id
    on dm_calendar(managed_by_user_id)
    where is_deleted = false;
create index ix_calendar_subject_user_id
    on dm_calendar(subject_user_id)
    where is_deleted = false;
create index ix_calendar_created_by on dm_calendar(created_by);
create index ix_calendar_updated_by on dm_calendar(updated_by);

create table dm_calendar_working_hour
(
    calendar_id               varchar(40)                  not null,
    day_of_week               smallint                     not null,
    start_at                  time                         not null,
    duration                  interval                     not null,
    primary key (calendar_id, day_of_week, start_at),
    foreign key (calendar_id) references dm_calendar (id)
);
create index ix_calendar_working_hour_calendar_id on dm_calendar_working_hour(calendar_id);

create table dm_scheduled_item(
    id                        varchar(40)                  not null,
    calendar_id               varchar(40)                  not null,
    organizer_user_id         varchar(40)                  not null,
    title                     varchar(256)                 not null,
    description               text                                 ,
    is_cancelled              boolean                      not null,
    type                      varchar(56)                  not null,
    created_at                timestamp with time zone     not null,
    updated_at                timestamp with time zone     not null,
    created_by                varchar(40)                  not null,
    updated_by                varchar(40)                  not null,
    is_deleted                boolean                      not null,
    primary key (id),
    foreign key (created_by) references dm_user (id),
    foreign key (updated_by) references dm_user (id),
    foreign key (calendar_id) references dm_calendar (id),
    foreign key (organizer_user_id) references dm_user (id)
);

create index ix_scheduled_item_calendar_id
    on dm_scheduled_item(calendar_id)
    where is_deleted = false;
create index ix_scheduled_item_organizer_user_id
    on dm_scheduled_item(organizer_user_id)
    where is_deleted = false;
create index ix_scheduled_item_created_by on dm_scheduled_item(created_by);
create index ix_scheduled_item_updated_by on dm_scheduled_item(updated_by);

create table dm_scheduled_item_single_timing(
   scheduled_item_id          varchar(40)                  not null,
   start_at                   timestamp with time zone     not null,
   duration                   interval                     not null,
   primary key (scheduled_item_id),
   foreign key (scheduled_item_id) references dm_scheduled_item (id)
);

create index ix_scheduled_item_single_id on dm_scheduled_item_single_timing(scheduled_item_id);

create table dm_scheduled_item_recurred_timing(
    scheduled_item_id         varchar(40)                  not null,
    start_date                date                         not null,
    start_time                time                         not null,
    duration                  interval                     not null,
    zone_id                   varchar(256)                 not null,
    recurrency_frequency      varchar(32)                  not null,
    recurrence_every          smallint                     not null,
    rule_details              jsonb                        not null,
    primary key (scheduled_item_id),
    foreign key (scheduled_item_id) references dm_scheduled_item (id)
);

create index ix_scheduled_item_recurred_id on dm_scheduled_item_recurred_timing(scheduled_item_id);

create table dm_meeting_details(
    scheduled_item_id         varchar(40)                  not null,
    zoom_link                 text                                 ,
    primary key (scheduled_item_id),
    foreign key (scheduled_item_id) references dm_scheduled_item (id)
);

create index ix_meeting_id on dm_meeting_details(scheduled_item_id);

create table dm_meeting_participant(
    scheduled_item_id         varchar(40)                  not null,
    user_id                   varchar(40)                  not null,
    accepted                  boolean                      not null,
    primary key (scheduled_item_id, user_id),
    foreign key (scheduled_item_id) references dm_scheduled_item (id),
    foreign key (user_id) references dm_user (id)
);

create index ix_meeting_participant_id on dm_meeting_participant(scheduled_item_id);
create index ix_meeting_participant_user_id on dm_meeting_participant(user_id);

create table dm_focus_time_details(
    scheduled_item_id         varchar(40)                  not null,
    focus_link                text                         not null,
    primary key (scheduled_item_id),
    foreign key (scheduled_item_id) references dm_scheduled_item (id)
);

create index ix_focus_time_id on dm_focus_time_details(scheduled_item_id);


create table dm_timeslot(
    id                        varchar(40)                  not null,
    calendar_id               varchar(40)                  not null,
    title                     varchar(256)                 not null,
    description               text                                 ,
    allow_overlap             boolean                      not null,
    is_busy_by_user           boolean                      not null,
    type                      varchar(56)                  not null,
    created_at                timestamp with time zone     not null,
    updated_at                timestamp with time zone     not null,
    created_by                varchar(40)                  not null,
    updated_by                varchar(40)                  not null,
    is_deleted                boolean                      not null,
    primary key (id),
    foreign key (created_by) references dm_user (id),
    foreign key (updated_by) references dm_user (id),
    foreign key (calendar_id) references dm_calendar (id)
);

create index ix_timeslots_calendar_id
    on dm_timeslot(calendar_id)
    where is_deleted = false;

create index ix_timeslots_created_by on dm_timeslot(created_by);
create index ix_timeslots_updated_by on dm_timeslot(updated_by);


create table dm_timeslot_allowed_type
(
    scheduled_timeslot_id     varchar(40)                  not null,
    allowed_item_type         varchar(56)                  not null,
    primary key (scheduled_timeslot_id, allowed_item_type),
    foreign key (scheduled_timeslot_id) references dm_timeslot (id)
);

create index ix_timeslot_allowed_type_id on dm_timeslot_allowed_type (scheduled_timeslot_id);

create table dm_timeslot_single_timing(
    scheduled_timeslot_id      varchar(40)                  not null,
    start_at                   timestamp with time zone     not null,
    duration                   interval                     not null,
    primary key (scheduled_timeslot_id),
    foreign key (scheduled_timeslot_id) references dm_timeslot (id)
);

create index ix_timeslot_single_id on dm_timeslot_single_timing(scheduled_timeslot_id);

create table dm_timeslot_recurred_timing(
    scheduled_timeslot_id     varchar(40)                  not null,
    start_date                date                         not null,
    start_time                time                         not null,
    duration                  interval                     not null,
    zone_id                   varchar(256)                 not null,
    recurrency_frequency      varchar(32)                  not null,
    recurrence_every          smallint                     not null,
    rule_details              jsonb                        not null,
    primary key (scheduled_timeslot_id),
    foreign key (scheduled_timeslot_id) references dm_timeslot (id)
);

create index ix_timeslot_recurred_id on dm_timeslot_recurred_timing(scheduled_timeslot_id);
