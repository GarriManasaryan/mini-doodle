package io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Component
public class PgJsonOperations {
    private final ObjectMapper objectMapper;

    public PgJsonOperations(ObjectMapper objectMapper) {
        this.objectMapper = JsonMapper.builder().findAndAddModules().build();
    }

    @Nullable
    public <T> T deserialize(@NotNull String json, @NotNull TypeReference<T> type){
        return objectMapper.readValue(json, type);
    }

    @Nullable
    public String serialize(@NotNull Object o){
        return objectMapper.writeValueAsString(o == null ? List.of() : o);

    }

}
