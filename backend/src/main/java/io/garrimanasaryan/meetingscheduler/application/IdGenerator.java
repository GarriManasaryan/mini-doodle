package io.garrimanasaryan.meetingscheduler.application;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class IdGenerator {
    public static String generate(@NotNull String prefix){
        return prefix.substring(0, 3) + "-" + UUID.randomUUID();
    }
}
