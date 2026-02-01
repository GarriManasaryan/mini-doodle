package io.garrimanasaryan.meetingscheduler.domain.repo;

import io.garrimanasaryan.meetingscheduler.domain.calendar.Calendar;
import io.garrimanasaryan.meetingscheduler.domain.common.BaseRepo;
import io.garrimanasaryan.meetingscheduler.domain.user.User;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface UserRepo extends BaseRepo<User> {
    List<User> all(@NotNull String userId);
}
