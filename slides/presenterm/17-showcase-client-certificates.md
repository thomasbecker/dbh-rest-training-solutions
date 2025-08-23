---
title: "Showcase: Client Certificate Authentication"
author: Thomas Becker
theme:
  name: dark
---

# Client Certificate Authentication

## Mutual TLS (mTLS) for REST APIs

Duration: 15 minutes

<!--
speaker_note: |
  This is a showcase demonstration only.
  Students will not implement this - just observe.
  Focus on when and why to use client certificates.
-->

<!-- end_slide -->

---

## What is Client Certificate Authentication?

**Two-way SSL/TLS verification**

```
┌──────────┐  Certificate  ┌──────────┐
│  Client  │───────────────►│  Server  │
│          │◄───────────────│          │
└──────────┘  Certificate  └──────────┘
```

Both parties verify each other's identity

<!-- pause -->

**Stronger than API keys or tokens**
- Certificate cannot be easily copied
- Built into TLS handshake
- No passwords transmitted

<!--
speaker_note: |
  Explain that this happens at the TLS layer,
  before HTTP even starts.
  Much more secure than application-level auth.
-->

<!-- end_slide -->

---

## When to Use Client Certificates?

### Perfect for:
- **B2B APIs** - Partner integrations
- **Microservices** - Service-to-service auth
- **High-security** - Banking, healthcare
- **Zero-trust networks** - Every connection verified

<!-- pause -->

### Not ideal for:
- Public APIs
- Mobile applications
- Browser-based clients
- Quick prototypes

<!--
speaker_note: |
  Emphasize that this is for scenarios where
  you control both client and server.
  Not practical for public APIs.
-->

<!-- end_slide -->

---

## Certificate Generation Demo

### 1. Create Certificate Authority (CA)

```bash
# Generate CA private key
openssl genrsa -out ca-key.pem 4096

# Generate CA certificate
openssl req -new -x509 -days 365 \
  -key ca-key.pem \
  -out ca-cert.pem \
  -subj "/CN=DBH-CA"
```

<!-- pause -->

### 2. Create Client Certificate

```bash
# Generate client private key
openssl genrsa -out client-key.pem 4096

# Generate certificate signing request
openssl req -new -key client-key.pem \
  -out client.csr \
  -subj "/CN=api-client"
```

<!--
speaker_note: |
  Live demo: Show actual certificate generation.
  Explain each step briefly.
  Don't spend too much time on OpenSSL details.
-->

<!-- end_slide -->

---

## Jersey Server Configuration

### Enable Client Certificate Validation

```java
@ApplicationPath("/api")
public class SecureJerseyConfig extends ResourceConfig {
    
    public SecureJerseyConfig() {
        // Enable SSL client authentication
        property("jersey.config.server.ssl.clientAuth", "REQUIRED");
        
        // Configure truststore with CA certificate
        property("jersey.config.server.ssl.trustStore", 
                 "/path/to/truststore.jks");
        property("jersey.config.server.ssl.trustStorePassword", 
                 "changeit");
    }
}
```

<!-- pause -->

### Extract Certificate in Resource

```java
@Context
private SecurityContext securityContext;

@GET
public Response getSecure() {
    X509Certificate cert = (X509Certificate) 
        securityContext.getUserPrincipal();
    
    String clientDN = cert.getSubjectDN().getName();
    // CN=api-client, O=Partner, C=DE
    
    return Response.ok("Authenticated: " + clientDN).build();
}
```

<!--
speaker_note: |
  Show how Jersey extracts the certificate.
  The DN (Distinguished Name) identifies the client.
  Can map this to roles/permissions in your system.
-->

<!-- end_slide -->

---

## Jetty Server with mTLS

```java
public static Server createSecureServer(int port) {
    Server server = new Server();
    
    // Configure SSL
    SslContextFactory.Server sslContextFactory = 
        new SslContextFactory.Server();
    sslContextFactory.setKeyStorePath("server-keystore.jks");
    sslContextFactory.setKeyStorePassword("password");
    
    // Enable client certificate validation
    sslContextFactory.setNeedClientAuth(true);
    sslContextFactory.setTrustStorePath("truststore.jks");
    sslContextFactory.setTrustStorePassword("password");
    
    // Create HTTPS connector
    ServerConnector https = new ServerConnector(server,
        new SslConnectionFactory(sslContextFactory, "http/1.1"),
        new HttpConnectionFactory());
    https.setPort(port);
    
    server.setConnectors(new Connector[] { https });
    return server;
}
```

<!--
speaker_note: |
  This is how you configure Jetty for mTLS.
  NeedClientAuth=true forces certificate validation.
  The truststore contains accepted CA certificates.
-->

<!-- end_slide -->

---

## Testing with curl

### With Client Certificate

```bash
# Successful request with certificate
curl -k \
  --cert client-cert.pem \
  --key client-key.pem \
  https://localhost:8443/api/secure

# Response: 200 OK
# "Authenticated: CN=api-client"
```

<!-- pause -->

### Without Certificate

```bash
# Fails without certificate
curl -k https://localhost:8443/api/secure

# Response: 
# curl: (56) SSL peer certificate or SSH remote key 
# was not OK
```

<!--
speaker_note: |
  Live demo: Show both successful and failed requests.
  The -k flag ignores self-signed certificate warnings.
  In production, use proper CA-signed certificates.
-->

<!-- end_slide -->

---

## Java Client Example

```java
public class SecureRestClient {
    
    public static Client createMTLSClient() {
        // Load client certificate
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream("client.p12"), 
                     "password".toCharArray());
        
        // Configure SSL context
        SSLContext sslContext = SSLContext.getInstance("TLS");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(keyStore, "password".toCharArray());
        
        sslContext.init(kmf.getKeyManagers(), null, null);
        
        // Create Jersey client with SSL
        return ClientBuilder.newBuilder()
            .sslContext(sslContext)
            .build();
    }
}
```

<!-- pause -->

```java
// Use the client
Client client = createMTLSClient();
Response response = client
    .target("https://api.example.com/secure")
    .request()
    .get();
```

<!--
speaker_note: |
  Show how Java clients can use certificates.
  The PKCS12 format combines cert and key.
  Jersey Client handles the TLS handshake.
-->

<!-- end_slide -->

---

## Certificate Management Best Practices

### Security Considerations

- **Short validity periods** - Rotate frequently (30-90 days)
- **Secure key storage** - Use HSM or key vaults
- **Certificate revocation** - Implement CRL or OCSP
- **Unique certificates** - One per client/service

<!-- pause -->

### Operational Considerations

- **Automated renewal** - Before expiration
- **Monitoring** - Alert on expiring certificates
- **Backup keys** - Encrypted and secure
- **Documentation** - Certificate inventory

<!--
speaker_note: |
  Certificate management is the hard part.
  Need good processes and automation.
  Consider using tools like Vault or cert-manager.
-->

<!-- end_slide -->

---

## Pros and Cons

### ✅ Advantages

- **Highest security** - Mutual authentication
- **No passwords** - Certificate-based
- **Performance** - No additional auth calls
- **Standards-based** - Works everywhere

<!-- pause -->

### ❌ Challenges

- **Complex setup** - CA, certificates, keystores
- **Certificate lifecycle** - Renewal, revocation
- **Client distribution** - How to securely deliver
- **Debugging difficulty** - TLS errors are cryptic

<!--
speaker_note: |
  Be honest about the complexity.
  It's powerful but requires commitment.
  Good for high-security scenarios.
-->

<!-- end_slide -->

---

## Integration with Existing Auth

### Layered Security Approach

```java
@Path("/api/critical")
public class CriticalResource {
    
    @GET
    @RequiresCertificate  // TLS client cert
    @RolesAllowed("ADMIN") // JWT role check
    public Response getCriticalData() {
        // Two layers of authentication:
        // 1. Client certificate (who you are)
        // 2. JWT token (what you can do)
        
        return Response.ok(criticalData).build();
    }
}
```

<!-- pause -->

**Best Practice**: Combine with JWT for fine-grained authorization

<!--
speaker_note: |
  You can combine mTLS with JWT.
  Certificate proves identity.
  JWT provides roles/permissions.
  Defense in depth approach.
-->

<!-- end_slide -->

---

## Demo: Live Certificate Authentication

### What I'll Show:

1. ✅ Generate certificates on the fly
2. ✅ Start server with mTLS enabled
3. ✅ Successful request with certificate
4. ❌ Failed request without certificate
5. 🔍 Examine certificate details in request

<!-- pause -->

*[Instructor performs live demonstration]*

<!--
speaker_note: |
  Time for live demo - 3-4 minutes.
  Keep it simple and focused.
  Have backup slides if demo fails.
-->

<!-- end_slide -->

---

## When You're Ready for This

### Prerequisites Before Implementation:

- Strong understanding of TLS/SSL
- Certificate management strategy
- Operational processes in place
- Testing environment with certificates

<!-- pause -->

### Start Simple:

1. Begin with self-signed certificates in dev
2. Test with single client certificate
3. Add certificate validation gradually
4. Move to CA-signed certificates in production

<!--
speaker_note: |
  Don't rush into client certificates.
  Start with simpler auth methods first.
  Add mTLS when security requirements demand it.
-->

<!-- end_slide -->

---

## Resources for Self-Study

### 📚 Further Reading

- **OWASP**: Transport Layer Protection Cheat Sheet
- **Jersey**: SSL Configuration Guide
- **Let's Encrypt**: Free CA for testing
- **OpenSSL**: Certificate cookbook

### 🛠️ Tools

- **mkcert**: Local CA for development
- **KeyStore Explorer**: GUI for Java keystores
- **Wireshark**: Debug TLS handshakes

### 💡 Next Steps

- Try mkcert for local development
- Implement in a test project
- Consider HashiCorp Vault for production

<!--
speaker_note: |
  Provide resources but don't overwhelm.
  Suggest starting with mkcert for simplicity.
  This is advanced - they should master basics first.
-->

<!-- end_slide -->

---

## Key Takeaways

### Remember:

- 🔐 **Strongest API authentication** available
- 🤝 **Both parties verified** (mutual TLS)
- 🏢 **Best for B2B** and microservices
- ⚠️ **Complex to manage** at scale

<!-- pause -->

### Consider mTLS when:

- Security is paramount
- You control both client and server
- You have certificate management infrastructure
- Compliance requires it (PCI DSS, etc.)

<!-- pause -->

**Questions about client certificates?**

<!--
speaker_note: |
  Quick recap and check for questions.
  Transition to next showcase (OpenAPI).
  Keep time in mind - 15 minutes total.
-->

<!-- end_slide -->