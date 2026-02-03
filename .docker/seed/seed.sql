insert into dm_user (id, name, email, zone_id,
                     created_at, updated_at,
                     created_by, updated_by,
                     is_deleted)
values ('usr-11111111-1111-1111-1111-111111111111',
        'Alice Manager',
        'alice@example.com',
        'UTC',
        now(), now(),
        'usr-11111111-1111-1111-1111-111111111111',
        'usr-11111111-1111-1111-1111-111111111111',
        false),
       ('usr-22222222-2222-2222-2222-222222222222',
        'Bob Subject',
        'bob@example.com',
        'UTC',
        now(), now(),
        'usr-11111111-1111-1111-1111-111111111111',
        'usr-11111111-1111-1111-1111-111111111111',
        false);

insert into dm_calendar (id,
                         managed_by_user_id,
                         subject_user_id,
                         title,
                         description,
                         type,
                         created_at,
                         updated_at,
                         created_by,
                         updated_by,
                         is_deleted)
values ('cld-aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'usr-11111111-1111-1111-1111-111111111111',
        'usr-22222222-2222-2222-2222-222222222222',
        'Bob Work Calendar',
        'Primary work calendar',
        'PERSONAL',
        now(), now(),
        'usr-11111111-1111-1111-1111-111111111111',
        'usr-11111111-1111-1111-1111-111111111111',
        false);

insert into dm_calendar_working_hour (calendar_id,
                                      day_of_week,
                                      start_at,
                                      duration)
values ('cld-aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 1, '09:00', 'PT8H'),
       ('cld-aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 2, '09:00', 'PT8H'),
       ('cld-aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 3, '09:00', 'PT8H'),
       ('cld-aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 4, '09:00', 'PT8H'),
       ('cld-aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 5, '09:00', 'PT8H');

insert into dm_timeslot (id,
                         calendar_id,
                         title,
                         description,
                         is_busy_by_user,
                         created_at,
                         updated_at,
                         created_by,
                         updated_by,
                         is_deleted)
values ('tms-cf425dae-f498-44bb-9b61-c127eb4e815e',
        'cld-aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'Morning availability',
        'Available for meetings',
        false,
        now(), now(),
        'usr-11111111-1111-1111-1111-111111111111',
        'usr-11111111-1111-1111-1111-111111111111',
        false);

insert into dm_timeslot_allowed_type
values ('tms-cf425dae-f498-44bb-9b61-c127eb4e815e', 'MEETING'),
       ('tms-cf425dae-f498-44bb-9b61-c127eb4e815e', 'FOCUS_TIME');

insert into dm_timeslot_single_timing (scheduled_timeslot_id,
                                       start_at,
                                       duration)
values ('tms-cf425dae-f498-44bb-9b61-c127eb4e815e',
        now() + interval '1 day' + interval '9 hours',
        'PT2H');

insert into dm_timeslot (id,
                         calendar_id,
                         title,
                         description,
                         is_busy_by_user,
                         created_at,
                         updated_at,
                         created_by,
                         updated_by,
                         is_deleted)
values ('tms-aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee',
        'cld-aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'Weekly Focus Block',
        'Deep work time',
        false,
        now(), now(),
        'usr-11111111-1111-1111-1111-111111111111',
        'usr-11111111-1111-1111-1111-111111111111',
        false);

insert into dm_timeslot_allowed_type
values ('tms-aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee', 'FOCUS_TIME');

insert into dm_timeslot_recurred_timing (scheduled_timeslot_id,
                                         start_date,
                                         start_time,
                                         duration,
                                         zone_id,
                                         recurrence_frequency,
                                         recurrence_every,
                                         rule_details)
values ('tms-aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee',
        current_date,
        '14:00',
        'PT1H',
        'UTC',
        'WEEKLY',
        1,
        jsonb_build_object('days', jsonb_build_array(2, 4)));

insert into dm_scheduled_item (id,
                               calendar_id,
                               organizer_user_id,
                               title,
                               description,
                               is_cancelled,
                               created_at,
                               updated_at,
                               created_by,
                               updated_by,
                               is_deleted)
values ('sci-11111111-aaaa-bbbb-cccc-111111111111',
        'cld-aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'usr-11111111-1111-1111-1111-111111111111',
        '1:1 Sync',
        'Weekly sync meeting',
        false,
        now(), now(),
        'usr-11111111-1111-1111-1111-111111111111',
        'usr-11111111-1111-1111-1111-111111111111',
        false);

insert into dm_scheduled_item_single_timing (scheduled_item_id,
                                             start_at,
                                             duration)
values ('sci-11111111-aaaa-bbbb-cccc-111111111111',
        now() + interval '1 day' + interval '9 hours',
        'PT1H');

insert into dm_meeting_details (scheduled_item_id,
                                zoom_link)
values ('sci-11111111-aaaa-bbbb-cccc-111111111111',
        'https://zoom.us/j/123456789');

insert into dm_meeting_participant
values ('sci-11111111-aaaa-bbbb-cccc-111111111111',
        'usr-22222222-2222-2222-2222-222222222222',
        true);

insert into dm_scheduled_item (id,
                               calendar_id,
                               organizer_user_id,
                               title,
                               description,
                               is_cancelled,
                               created_at,
                               updated_at,
                               created_by,
                               updated_by,
                               is_deleted)
values ('sci-22222222-aaaa-bbbb-cccc-222222222222',
        'cld-aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'usr-22222222-2222-2222-2222-222222222222',
        'Focus Session',
        'No interruptions',
        false,
        now(), now(),
        'usr-22222222-2222-2222-2222-222222222222',
        'usr-22222222-2222-2222-2222-222222222222',
        false);

insert into dm_scheduled_item_recurred_timing (scheduled_item_id,
                                               start_date,
                                               start_time,
                                               duration,
                                               zone_id,
                                               recurrence_frequency,
                                               recurrence_every,
                                               rule_details)
values ('sci-22222222-aaaa-bbbb-cccc-222222222222',
        current_date,
        '14:00',
        'PT1H',
        'UTC',
        'WEEKLY',
        1,
        jsonb_build_object('days', jsonb_build_array(2, 4)));

insert into dm_focus_time_details (scheduled_item_id,
                                   focus_link)
values ('sci-22222222-aaaa-bbbb-cccc-222222222222',
        'https://notion.so/focus-session');
