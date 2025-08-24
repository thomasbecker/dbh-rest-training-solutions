package com.dbh.training.rest.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.dbh.training.rest.serializers.PrioritySerializer;

/**
 * Priority levels for todos.
 * 
 * Exercise 09 Solution: Complete Priority enum with custom serializer
 */
@JsonSerialize(using = PrioritySerializer.class)
public enum Priority {
    HIGH(3),
    MEDIUM(2),
    LOW(1);
    
    private final int level;
    
    Priority(int level) {
        this.level = level;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getColor() {
        switch (this) {
            case HIGH:
                return "#FF0000";
            case MEDIUM:
                return "#FFA500";
            case LOW:
                return "#00FF00";
            default:
                return "#808080";
        }
    }
}