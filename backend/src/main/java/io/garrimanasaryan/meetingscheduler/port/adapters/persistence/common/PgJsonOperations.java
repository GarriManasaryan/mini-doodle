package io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class PgJsonOperations {
    private final ObjectMapper objectMapper;

    public PgJsonOperations(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T deserialize(@NotNull String json, @NotNull TypeReference<T> type){
        return objectMapper.readValue(json, type);
    }

    public String serialize(@NotNull Object o){
        return objectMapper.writeValueAsString(o == null ? List.of() : o);

    }

}
