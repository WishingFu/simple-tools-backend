package com.wishingfu.tools.fileshare.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public record FileEvent(String fileId, EventType type) {

    public enum EventType {
        ADD, MODIFIED, DELETED
    }

    public String toJsonString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    public static FileEvent addEvent(String fileId) {
        return new FileEvent(fileId, EventType.ADD);
    }

    public static FileEvent modifiedEvent(String fileId) {
        return new FileEvent(fileId, EventType.MODIFIED);
    }

    public static FileEvent deleteEvent(String fileId) {
        return new FileEvent(fileId, EventType.DELETED);
    }

}
