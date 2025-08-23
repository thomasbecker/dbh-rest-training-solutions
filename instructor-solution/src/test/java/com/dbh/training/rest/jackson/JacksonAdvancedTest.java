package com.dbh.training.rest.jackson;

import com.dbh.training.rest.config.JacksonConfig;
import com.dbh.training.rest.config.jackson.MoneyDeserializer;
import com.dbh.training.rest.config.jackson.MoneySerializer;
import com.dbh.training.rest.config.jackson.ThirdPartyUserMixIn;
import com.dbh.training.rest.dto.Views;
import com.dbh.training.rest.models.Money;
import com.dbh.training.rest.models.User;
import com.dbh.training.rest.thirdparty.ThirdPartyUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Jackson Advanced features
 * 
 * Exercise 07: Jackson Advanced
 * Tests custom serializers, JSON Views, and Mix-ins
 */
public class JacksonAdvancedTest {
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    public void setUp() {
        // Use the same ObjectMapper configuration as the application
        JacksonConfig config = new JacksonConfig();
        objectMapper = config.getContext(null);
    }
    
    // ===== Money Serializer/Deserializer Tests =====
    
    @Test
    public void testMoneySerializa

() throws Exception {
        Money money = new Money(new BigDecimal("1250.50"), Currency.getInstance("EUR"));
        
        String json = objectMapper.writeValueAsString(money);
        JsonNode root = objectMapper.readTree(json);
        
        assertEquals(1250.50, root.get("amount").asDouble(), 0.01);
        assertEquals("EUR", root.get("currency").asText());
        assertEquals("€ 1250.50", root.get("formatted").asText());
    }
    
    @Test
    public void testMoneyDeserialization() throws Exception {
        String json = "{\"amount\": 999.99, \"currency\": \"USD\"}";
        
        Money money = objectMapper.readValue(json, Money.class);
        
        assertNotNull(money);
        assertEquals(new BigDecimal("999.99"), money.getAmount());
        assertEquals(Currency.getInstance("USD"), money.getCurrency());
        assertEquals("$ 999.99", money.getFormatted());
    }
    
    @Test
    public void testMoneyRoundTrip() throws Exception {
        Money original = new Money(new BigDecimal("2500.75"), Currency.getInstance("GBP"));
        
        String json = objectMapper.writeValueAsString(original);
        Money deserialized = objectMapper.readValue(json, Money.class);
        
        assertEquals(original.getAmount(), deserialized.getAmount());
        assertEquals(original.getCurrency(), deserialized.getCurrency());
    }
    
    @Test
    public void testUserWithMoney() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("rich_user");
        user.setEmail("rich@example.com");
        user.setFirstName("Rich");
        user.setLastName("User");
        user.setAccountBalance(new Money(new BigDecimal("5000.00"), Currency.getInstance("EUR")));
        
        String json = objectMapper.writeValueAsString(user);
        JsonNode root = objectMapper.readTree(json);
        
        assertNotNull(root.get("account_balance"));
        JsonNode balance = root.get("account_balance");
        assertEquals(5000.00, balance.get("amount").asDouble(), 0.01);
        assertEquals("EUR", balance.get("currency").asText());
        assertEquals("€ 5000.00", balance.get("formatted").asText());
    }
    
    // ===== JSON Views Tests =====
    
    @Test
    public void testPublicView() throws Exception {
        User user = createTestUser();
        
        // Write with Public view
        String json = objectMapper
            .writerWithView(Views.Public.class)
            .writeValueAsString(user);
        
        JsonNode root = objectMapper.readTree(json);
        
        // Public fields should be present
        assertNotNull(root.get("user_id"));
        assertNotNull(root.get("user_name"));
        assertNotNull(root.get("email_address"));
        assertNotNull(root.get("first_name"));
        assertNotNull(root.get("last_name"));
        
        // Internal/Admin fields should be absent
        assertNull(root.get("birth_date"));
        assertNull(root.get("created_at"));
        assertNull(root.get("account_status"));
        assertNull(root.get("roles"));
        assertNull(root.get("last_login"));
        assertNull(root.get("account_balance"));
        
        // Password should never be visible
        assertNull(root.get("password"));
    }
    
    @Test
    public void testInternalView() throws Exception {
        User user = createTestUser();
        
        // Write with Internal view
        String json = objectMapper
            .writerWithView(Views.Internal.class)
            .writeValueAsString(user);
        
        JsonNode root = objectMapper.readTree(json);
        
        // Public fields (inherited)
        assertNotNull(root.get("user_id"));
        assertNotNull(root.get("user_name"));
        
        // Internal fields
        assertNotNull(root.get("birth_date"));
        assertNotNull(root.get("created_at"));
        assertNotNull(root.get("account_status"));
        assertNotNull(root.get("roles"));
        assertNotNull(root.get("account_balance"));
        
        // Admin-only fields should be absent
        assertNull(root.get("last_login"));
        
        // Password should never be visible
        assertNull(root.get("password"));
    }
    
    @Test
    public void testAdminView() throws Exception {
        User user = createTestUser();
        
        // Write with Admin view
        String json = objectMapper
            .writerWithView(Views.Admin.class)
            .writeValueAsString(user);
        
        JsonNode root = objectMapper.readTree(json);
        
        // All fields except password should be present
        assertNotNull(root.get("user_id"));
        assertNotNull(root.get("user_name"));
        assertNotNull(root.get("birth_date"));
        assertNotNull(root.get("created_at"));
        assertNotNull(root.get("account_status"));
        assertNotNull(root.get("roles"));
        assertNotNull(root.get("last_login"));
        assertNotNull(root.get("account_balance"));
        
        // Password should never be visible
        assertNull(root.get("password"));
    }
    
    // ===== Mix-ins Tests =====
    
    @Test
    public void testThirdPartyUserMixIn() throws Exception {
        ThirdPartyUser thirdPartyUser = new ThirdPartyUser(
            "john_doe",
            "internal-12345",
            "secret-api-key-xyz",
            "john@example.com"
        );
        
        String json = objectMapper.writeValueAsString(thirdPartyUser);
        JsonNode root = objectMapper.readTree(json);
        
        // Fields renamed by mix-in
        assertNotNull(root.get("user_name"));
        assertEquals("john_doe", root.get("user_name").asText());
        assertNotNull(root.get("email_address"));
        assertEquals("john@example.com", root.get("email_address").asText());
        
        // Fields hidden by mix-in
        assertNull(root.get("internalId"));
        assertNull(root.get("apiKey"));
        assertNull(root.get("internal_id"));
        assertNull(root.get("api_key"));
    }
    
    @Test
    public void testMixInPreventsSecretLeak() throws Exception {
        ThirdPartyUser user = new ThirdPartyUser();
        user.setUsername("test");
        user.setEmail("test@example.com");
        user.setInternalId("SECRET_ID");
        user.setApiKey("SECRET_KEY");
        
        String json = objectMapper.writeValueAsString(user);
        
        // Ensure secrets are not in JSON
        assertFalse(json.contains("SECRET_ID"));
        assertFalse(json.contains("SECRET_KEY"));
        assertTrue(json.contains("test"));
        assertTrue(json.contains("test@example.com"));
    }
    
    // Helper method to create test user
    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test_user");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("secret123");  // Should never appear in JSON
        user.setBirthDate(LocalDate.of(1990, 5, 15));
        user.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));
        user.setStatus(com.dbh.training.rest.models.AccountStatus.ACTIVE);
        user.setRoles(Arrays.asList("USER", "ADMIN"));
        user.setLastLogin(LocalDateTime.of(2024, 2, 1, 14, 0));
        user.setAccountBalance(new Money(new BigDecimal("1000.00"), Currency.getInstance("USD")));
        return user;
    }
}