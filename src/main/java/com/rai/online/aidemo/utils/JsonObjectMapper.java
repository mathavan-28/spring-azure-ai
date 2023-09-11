package com.rai.online.aidemo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rai.online.aidemo.exceptions.SpringAIDemoException;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;

import static com.rai.online.aidemo.exceptions.SpringAIDemoErrorCode.E2004;


@Slf4j
public class JsonObjectMapper {

    private final ObjectReader objectReader = new ObjectMapper().reader();
    private final ObjectWriter objectWriter = new ObjectMapper().writer();

    public String writeValueAsString(Object value) {
        try {
            return objectWriter.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("Cannot construct JSON from value - {}", value);
            throw new SpringAIDemoException(E2004, "Cannot construct JSON");
        }
    }

    public <T> T readValue(String object, Class<T> type) {
        try {
            return objectReader.readValue(object, type);
        } catch (IOException e) {
            log.error("Cannot parse JSON from type - {}, object - {}", type, object);
            throw new SpringAIDemoException(E2004, "Can't parse JSON");
        }
    }

    public <T> T readValue(String object, TypeReference<T> valueTypeRef) {
        try {

            return objectReader.readValue(objectReader.createParser(object), valueTypeRef);
        } catch (IOException e) {
            log.error("Cannot parse JSON from type - {}, object - {}", valueTypeRef, object);
            throw new SpringAIDemoException(E2004, "Can't parse JSON");
        }
    }
}
