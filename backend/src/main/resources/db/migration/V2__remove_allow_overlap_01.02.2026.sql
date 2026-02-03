alter table dm_timeslot drop column allow_overlap;
alter table dm_timeslot drop column type;
alter table dm_calendar drop column allow_overlap;
alter table dm_timeslot_recurred_timing rename column recurrency_frequency to recurrence_frequency;
alter table dm_scheduled_item_recurred_timing rename column recurrency_frequency to recurrence_frequency;
