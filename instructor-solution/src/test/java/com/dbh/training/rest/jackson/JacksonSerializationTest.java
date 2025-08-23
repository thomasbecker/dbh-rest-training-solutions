package com.dbh.training.rest.jackson;

import com.dbh.training.rest.config.JacksonConfig;
import com.dbh.training.rest.models.User;
import com.dbh.training.rest.models.Address;
import com.dbh.training.rest.models.AccountStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Jackson serialization/deserialization
 * 
 * Exercise 06: Jackson Basics
 * Tests ObjectMapper configuration and DTO Jackson annotations
 */
public class JacksonSerializationTest {
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    public void setUp() {
        // Use the same ObjectMapper configuration as the application
        JacksonConfig config = new JacksonConfig();
        objectMapper = config.getContext(null);
    }
    
    @Test
    public void testUserSerialization() throws Exception {
        // Create a user DTO with all fields
        User user = new User();
        user.setId(1L);
        user.setUsername("test_user");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("secret123");
        user.setBirthDate(LocalDate.of(1990, 5, 15));
        user.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30, 0));
        user.setStatus(AccountStatus.ACTIVE);
        user.setRoles(new HashSet<>(Arrays.asList("USER", "ADMIN")));
        
        // Serialize to JSON
        String json = objectMapper.writeValueAsString(user);
        
        // Parse JSON to verify structure
        JsonNode root = objectMapper.readTree(json);
        
        // Verify custom field names
        assertNotNull(root.get("user_id"));
        assertEquals(1, root.get("user_id").asLong());
        
        assertNotNull(root.get("user_name"));
        assertEquals("test_user", root.get("user_name").asText());
        
        assertNotNull(root.get("email_address"));
        assertEquals("test@example.com", root.get("email_address").asText());
        
        // Verify password is excluded
        assertNull(root.get("password"), "Password should not be serialized");
        
        // Verify date formatting
        assertEquals("1990-05-15", root.get("birth_date").asText());
        assertEquals("2024-01-15T10:30:00", root.get("created_at").asText());
        
        // Verify enum serialization
        assertEquals("active", root.get("account_status").asText());
        
        // Verify array serialization
        assertTrue(root.get("roles").isArray());
        assertEquals(2, root.get("roles").size());
    }
    
    @Test
    public void testUserDeserialization() throws Exception {
        String json = "{"
            + "\"user_id\": 42,"
            + "\"user_name\": \"john_doe\","
            + "\"email_address\": \"john@example.com\","
            + "\"first_name\": \"John\","
            + "\"last_name\": \"Doe\","
            + "\"password\": \"ignored_password\","  // Should be ignored
            + "\"birth_date\": \"1985-03-20\","
            + "\"created_at\": \"2024-01-10T15:45:30\","
            + "\"account_status\": \"suspended\","
            + "\"roles\": [\"USER\", \"MODERATOR\"]"
            + "}";
        
        // Deserialize from JSON
        User user = objectMapper.readValue(json, User.class);
        
        // Verify fields are mapped correctly
        assertEquals(42L, user.getId());
        assertEquals("john_doe", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(LocalDate.of(1985, 3, 20), user.getBirthDate());
        assertEquals(LocalDateTime.of(2024, 1, 10, 15, 45, 30), user.getCreatedAt());
        assertEquals(AccountStatus.SUSPENDED, user.getStatus());
        assertEquals(new HashSet<>(Arrays.asList("USER", "MODERATOR")), user.getRoles());
        
        // Password should be null due to @JsonIgnore
        assertNull(user.getPasswordHash(), "Password hash should be ignored during deserialization");
    }
    
    @Test
    public void testAddressSerialization() throws Exception {
        Address address = new Address();
        address.setStreetLine1("123 Main Street");
        address.setStreetLine2("Suite 100");
        address.setCity("Berlin");
        address.setState("BE");
        address.setPostalCode("10115");
        address.setCountryCode("DE");
        
        // Serialize to JSON
        String json = objectMapper.writeValueAsString(address);
        
        // Parse and verify
        JsonNode root = objectMapper.readTree(json);
        
        assertEquals("123 Main Street", root.get("street_line_1").asText());
        assertEquals("Suite 100", root.get("street_line_2").asText());
        assertEquals("Berlin", root.get("city").asText());
        assertEquals("BE", root.get("state").asText());
        assertEquals("10115", root.get("postal_code").asText());
        assertEquals("DE", root.get("country_code").asText());
    }
    
    @Test
    public void testAddressDeserialization() throws Exception {
        String json = "{"
            + "\"street_line_1\": \"456 Oak Avenue\","
            + "\"street_line_2\": \"Floor 3\","
            + "\"city\": \"Munich\","
            + "\"state\": \"BY\","
            + "\"postal_code\": \"80331\","
            + "\"country_code\": \"DE\""
            + "}";
        
        Address address = objectMapper.readValue(json, Address.class);
        
        assertEquals("456 Oak Avenue", address.getStreetLine1());
        assertEquals("Floor 3", address.getStreetLine2());
        assertEquals("Munich", address.getCity());
        assertEquals("BY", address.getState());
        assertEquals("80331", address.getPostalCode());
        assertEquals("DE", address.getCountryCode());
    }
    
    @Test
    public void testNullFieldExclusion() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("minimal_user");
        user.setEmail("minimal@example.com");
        user.setFirstName("Minimal");
        user.setLastName("User");
        // Leave other fields null
        
        String json = objectMapper.writeValueAsString(user);
        JsonNode root = objectMapper.readTree(json);
        
        // Required fields should be present
        assertNotNull(root.get("user_id"));
        assertNotNull(root.get("user_name"));
        assertNotNull(root.get("email_address"));
        assertNotNull(root.get("first_name"));
        assertNotNull(root.get("last_name"));
        
        // Null fields should be excluded due to NON_NULL inclusion
        assertNull(root.get("birth_date"));
        assertNull(root.get("created_at"));
        assertNull(root.get("account_status"));
        // roles is initialized as empty HashSet now, not null
        assertNotNull(root.get("roles"));
        assertTrue(root.get("roles").isArray());
        assertEquals(0, root.get("roles").size());
        assertNull(root.get("primary_address"));
        assertNull(root.get("billing_address"));
    }
    
    @Test
    public void testUserWithNestedAddress() throws Exception {
        User user = new User();
        user.setId(5L);
        user.setUsername("address_user");
        user.setEmail("address@example.com");
        user.setFirstName("Address");
        user.setLastName("User");
        
        Address primaryAddress = new Address();
        primaryAddress.setStreetLine1("789 Pine Road");
        primaryAddress.setCity("Hamburg");
        primaryAddress.setPostalCode("20095");
        primaryAddress.setCountryCode("DE");
        user.setPrimaryAddress(primaryAddress);
        
        String json = objectMapper.writeValueAsString(user);
        JsonNode root = objectMapper.readTree(json);
        
        // Verify nested address is serialized
        JsonNode addressNode = root.get("primary_address");
        assertNotNull(addressNode);
        assertEquals("789 Pine Road", addressNode.get("street_line_1").asText());
        assertEquals("Hamburg", addressNode.get("city").asText());
        assertEquals("20095", addressNode.get("postal_code").asText());
        assertEquals("DE", addressNode.get("country_code").asText());
    }
    
    @Test
    public void testCollectionSerialization() throws Exception {
        User user = new User();
        user.setId(10L);
        user.setUsername("roles_user");
        user.setEmail("roles@example.com");
        user.setFirstName("Roles");
        user.setLastName("User");
        user.setRoles(new HashSet<>(Arrays.asList("ADMIN", "USER", "MODERATOR", "VIEWER")));
        
        String json = objectMapper.writeValueAsString(user);
        JsonNode root = objectMapper.readTree(json);
        
        JsonNode rolesNode = root.get("roles");
        assertTrue(rolesNode.isArray());
        assertEquals(4, rolesNode.size());
        
        // Convert to set for comparison since Sets don't guarantee order
        HashSet<String> actualRoles = new HashSet<>();
        for (JsonNode roleNode : rolesNode) {
            actualRoles.add(roleNode.asText());
        }
        assertEquals(new HashSet<>(Arrays.asList("ADMIN", "USER", "MODERATOR", "VIEWER")), actualRoles);
    }
    
    @Test
    public void testDateTimeFormats() throws Exception {
        String json = "{"
            + "\"user_id\": 99,"
            + "\"user_name\": \"date_test\","
            + "\"email_address\": \"date@test.com\","
            + "\"first_name\": \"Date\","
            + "\"last_name\": \"Test\","
            + "\"birth_date\": \"2000-12-25\","
            + "\"created_at\": \"2024-12-31T23:59:59\""
            + "}";
        
        User user = objectMapper.readValue(json, User.class);
        
        assertEquals(LocalDate.of(2000, 12, 25), user.getBirthDate());
        assertEquals(LocalDateTime.of(2024, 12, 31, 23, 59, 59), user.getCreatedAt());
    }
    
    @Test
    public void testRoundTripSerialization() throws Exception {
        // Create a complete user DTO
        User original = new User();
        original.setId(100L);
        original.setUsername("roundtrip");
        original.setEmail("roundtrip@example.com");
        original.setFirstName("Round");
        original.setLastName("Trip");
        original.setBirthDate(LocalDate.of(1995, 6, 15));
        original.setCreatedAt(LocalDateTime.of(2024, 2, 29, 12, 0, 0));
        original.setStatus(AccountStatus.ACTIVE);
        original.setRoles(new HashSet<>(Arrays.asList("USER")));
        
        Address address = new Address();
        address.setStreetLine1("999 Test Lane");
        address.setCity("Frankfurt");
        address.setPostalCode("60311");
        address.setCountryCode("DE");
        original.setPrimaryAddress(address);
        
        // Serialize and deserialize
        String json = objectMapper.writeValueAsString(original);
        User deserialized = objectMapper.readValue(json, User.class);
        
        // Verify all fields match
        assertEquals(original.getId(), deserialized.getId());
        assertEquals(original.getUsername(), deserialized.getUsername());
        assertEquals(original.getEmail(), deserialized.getEmail());
        assertEquals(original.getFirstName(), deserialized.getFirstName());
        assertEquals(original.getLastName(), deserialized.getLastName());
        assertEquals(original.getBirthDate(), deserialized.getBirthDate());
        assertEquals(original.getCreatedAt(), deserialized.getCreatedAt());
        assertEquals(original.getStatus(), deserialized.getStatus());
        assertEquals(original.getRoles(), deserialized.getRoles());
        
        // Verify nested address
        assertNotNull(deserialized.getPrimaryAddress());
        assertEquals(original.getPrimaryAddress().getStreetLine1(), 
                     deserialized.getPrimaryAddress().getStreetLine1());
        assertEquals(original.getPrimaryAddress().getCity(), 
                     deserialized.getPrimaryAddress().getCity());
        assertEquals(original.getPrimaryAddress().getPostalCode(), 
                     deserialized.getPrimaryAddress().getPostalCode());
        assertEquals(original.getPrimaryAddress().getCountryCode(), 
                     deserialized.getPrimaryAddress().getCountryCode());
    }
}