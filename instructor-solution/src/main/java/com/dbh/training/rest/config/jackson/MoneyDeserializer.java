package com.dbh.training.rest.config.jackson;

import com.dbh.training.rest.models.Money;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * Custom deserializer for Money objects.
 * 
 * Exercise 07: Jackson Advanced  
 * Deserializes Money from JSON with amount and currency fields
 */
public class MoneyDeserializer extends JsonDeserializer<Money> {
    
    @Override
    public Money deserialize(JsonParser p, DeserializationContext ctxt) 
            throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        
        if (node == null || node.isNull()) {
            return null;
        }
        
        BigDecimal amount = node.get("amount").decimalValue();
        String currency = node.get("currency").asText();
        
        return new Money(amount, Currency.getInstance(currency));
    }
}