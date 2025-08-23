package com.dbh.training.rest.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Money value object for financial amounts.
 * 
 * Exercise 07: Jackson Advanced
 * Used to demonstrate custom serializers/deserializers
 */
public class Money {
    private BigDecimal amount;
    private Currency currency;
    
    public Money() {
        // Default constructor for Jackson
    }
    
    public Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }
    
    public Money(BigDecimal amount, String currencyCode) {
        this.amount = amount;
        this.currency = Currency.getInstance(currencyCode);
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public Currency getCurrency() {
        return currency;
    }
    
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
    
    public String getFormatted() {
        if (amount == null || currency == null) {
            return "N/A";
        }
        return currency.getSymbol() + " " + 
               amount.setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) &&
               Objects.equals(currency, money.currency);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
    
    @Override
    public String toString() {
        return getFormatted();
    }
}