package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.resource;

import io.garrimanasaryan.meetingscheduler.application.UserService;
import io.garrimanasaryan.meetingscheduler.domain.user.User;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.mapper.UserMapper;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.user.UserBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.user.UserCreationRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.user.UserUpdateRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backoffice/users")
public class UserController extends BaseController<
        User,
        UserService,
        UserBackofficeModel,
        UserCreationRequest,
        UserUpdateRequest,
        UserMapper
        > {

    public UserController(UserService service, UserMapper mapper) {
        super(service, mapper);
    }
}
