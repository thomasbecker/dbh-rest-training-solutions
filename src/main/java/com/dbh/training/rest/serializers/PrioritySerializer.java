package com.dbh.training.rest.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.dbh.training.rest.models.Priority;

import java.io.IOException;

/**
 * Custom Jackson serializer for Priority enum.
 * 
 * Exercise 09 Task 4.1 Solution: Outputs priority with additional metadata
 * 
 * Example output:
 * {
 *   "value": "HIGH",
 *   "level": 3,
 *   "color": "#FF0000"
 * }
 */
public class PrioritySerializer extends JsonSerializer<Priority> {
    
    @Override
    public void serialize(Priority priority, JsonGenerator gen, SerializerProvider serializers) 
            throws IOException {
        
        gen.writeStartObject();
        gen.writeStringField("value", priority.name());
        gen.writeNumberField("level", priority.getLevel());
        gen.writeStringField("color", priority.getColor());
        gen.writeEndObject();
    }
}