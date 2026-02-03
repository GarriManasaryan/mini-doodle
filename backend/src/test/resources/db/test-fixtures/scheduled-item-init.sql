insert into dm_user (
    id,
    name,
    email,
    zone_id,
    created_at,
    updated_at,
    created_by,
    updated_by,
    is_deleted
) values (
 'user-2',
 'Second User',
 'user2@example.com',
 'UTC',
 '2026-02-03T09:00:00Z',
 '2026-02-03T09:00:00Z',
 'user-1',
 'user-1',
 false
)
on conflict (id) do nothing;


insert into dm_timeslot (
    id, calendar_id, title, description, is_busy_by_user,
    created_at, updated_at, created_by, updated_by, is_deleted
)
values (
           'ts-1', 'calendar-1', 'Available', null, false,
           '2026-02-03T10:00:00Z', '2026-02-03T10:00:00Z', 'user-1', 'user-1', false
       )
on conflict (id) do nothing;

insert into dm_timeslot_allowed_type (
    scheduled_timeslot_id, allowed_item_type
)
values ('ts-1', 'MEETING')
on conflict do nothing;
