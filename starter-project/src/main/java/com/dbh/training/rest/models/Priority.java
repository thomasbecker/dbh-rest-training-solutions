package com.dbh.training.rest.models;

/**
 * Priority levels for todos.
 * 
 * TODO Exercise 09: This enum is complete, but you'll create a custom
 * Jackson serializer for it in Task 4.1
 */
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