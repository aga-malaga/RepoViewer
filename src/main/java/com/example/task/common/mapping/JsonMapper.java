package com.example.task.common.mapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonMapper {

    private static final String PARSING_FROM_STRING_ERROR_FORMAT = "Error while parsing string %s to target class: %s";

    public <T> T toObject(String value, TypeReference<T> targetClass) {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(value, targetClass);
        } catch (JsonProcessingException exception) {
            String message = PARSING_FROM_STRING_ERROR_FORMAT.formatted(value, targetClass.toString());
            log.error(message);
            throw new RuntimeException(message, exception);
        }
    }
}