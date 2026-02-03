insert into dm_user (id, name, email, zone_id,
                     created_at, updated_at,
                     created_by, updated_by, is_deleted)
values ('user-1', 'Test User', 'user1@test.com', 'UTC',
        '2026-02-03T08:00:00Z', '2026-02-03T08:00:00Z',
        'user-1', 'user-1', false),
       ('user-2', 'Second User', 'user2@test.com', 'UTC',
        '2026-02-03T08:00:00Z', '2026-02-03T08:00:00Z',
        'user-1', 'user-1', false)
on conflict (id) do nothing;

insert into dm_calendar (id, managed_by_user_id, subject_user_id,
                         title, description, type,
                         created_at, updated_at,
                         created_by, updated_by, is_deleted)
values ('calendar-1',
        'user-1',
        'user-1',
        'Main Calendar',
        null,
        'PERSONAL',
        '2026-02-03T08:00:00Z',
        '2026-02-03T08:00:00Z',
        'user-1',
        'user-1',
        false)
on conflict (id) do nothing;

insert into dm_calendar_working_hour (calendar_id, day_of_week, start_at, duration)
values ('calendar-1', 1, '09:00', 'PT8H'),
       ('calendar-1', 2, '09:00', 'PT8H'),
       ('calendar-1', 3, '09:00', 'PT8H'),
       ('calendar-1', 4, '09:00', 'PT8H'),
       ('calendar-1', 5, '09:00', 'PT8H')
on conflict do nothing;

insert into dm_timeslot (id, calendar_id, title, description,
                         is_busy_by_user,
                         created_at, updated_at,
                         created_by, updated_by, is_deleted)
values ('ts-1',
        'calendar-1',
        'Available',
        null,
        false,
        '2026-02-03T08:00:00Z',
        '2026-02-03T08:00:00Z',
        'user-1',
        'user-1',
        false)
on conflict (id) do nothing;

insert into dm_timeslot_allowed_type (scheduled_timeslot_id, allowed_item_type)
values ('ts-1', 'MEETING')
on conflict do nothing;

insert into dm_timeslot_single_timing (scheduled_timeslot_id, start_at, duration)
values ('ts-1',
        '2026-02-03T09:00:00Z',
        interval '8 hours')
on conflict do nothing;

insert into dm_timeslot (id, calendar_id, title, description,
                         is_busy_by_user,
                         created_at, updated_at,
                         created_by, updated_by, is_deleted)
values ('ts-rec',
        'calendar-1',
        'Recurring availability',
        null,
        false,
        '2026-02-03T08:00:00Z',
        '2026-02-03T08:00:00Z',
        'user-1',
        'user-1',
        false)
on conflict (id) do nothing;

insert into dm_timeslot_allowed_type (scheduled_timeslot_id, allowed_item_type)
values ('ts-rec', 'MEETING')
on conflict do nothing;

insert into dm_timeslot_recurred_timing (scheduled_timeslot_id,
                                         start_date,
                                         start_time,
                                         zone_id,
                                         duration,
                                         recurrence_frequency,
                                         recurrence_every,
                                         rule_details)
values ('ts-rec',
        '2026-02-03',
        '10:00',
        'UTC',
        interval '1 hour',
        'DAILY',
        1,
        '{}'::jsonb)
on conflict do nothing;

insert into dm_scheduled_item (id, calendar_id, organizer_user_id,
                               title, description,
                               is_cancelled,
                               created_at, updated_at,
                               created_by, updated_by, is_deleted)
values ('cancelled-item',
        'calendar-1',
        'user-1',
        'Cancelled meeting',
        null,
        true,
        '2026-02-03T08:00:00Z',
        '2026-02-03T08:00:00Z',
        'user-1',
        'user-1',
        false)
on conflict (id) do nothing;

insert into dm_scheduled_item_single_timing (scheduled_item_id, start_at, duration)
values ('cancelled-item',
        '2026-02-03T14:00:00Z',
        interval '1 hour')
on conflict do nothing;