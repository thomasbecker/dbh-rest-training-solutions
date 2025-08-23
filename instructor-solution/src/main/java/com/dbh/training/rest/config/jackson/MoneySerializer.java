package com.dbh.training.rest.config.jackson;

import com.dbh.training.rest.models.Money;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Custom serializer for Money objects.
 * 
 * Exercise 07: Jackson Advanced
 * Serializes Money as JSON with amount, currency, and formatted display
 */
public class MoneySerializer extends JsonSerializer<Money> {
    
    @Override
    public void serialize(Money value, JsonGenerator gen, 
                         SerializerProvider serializers) 
                         throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        
        gen.writeStartObject();
        gen.writeNumberField("amount", value.getAmount());
        gen.writeStringField("currency", 
                            value.getCurrency().getCurrencyCode());
        gen.writeStringField("formatted", value.getFormatted());
        gen.writeEndObject();
    }
}