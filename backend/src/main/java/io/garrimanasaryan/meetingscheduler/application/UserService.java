package io.garrimanasaryan.meetingscheduler.application;

import io.garrimanasaryan.meetingscheduler.domain.calendar.Calendar;
import io.garrimanasaryan.meetingscheduler.domain.repo.CalendarRepo;
import io.garrimanasaryan.meetingscheduler.domain.repo.UserRepo;
import io.garrimanasaryan.meetingscheduler.domain.user.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService extends BaseService<User, UserRepo>{

    public UserService(UserRepo repo) {
        super(repo);
    }

    public void save(@NotNull User entity) {
        repo.update(entity);
    }

}
