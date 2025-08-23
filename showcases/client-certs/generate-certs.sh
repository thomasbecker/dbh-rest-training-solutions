#!/bin/bash

# Certificate Generation Script for Demo
# This creates self-signed certificates for demonstration purposes only
# DO NOT use in production!

echo "🔐 Generating certificates for mTLS demo..."

# Create directories
mkdir -p ca client server

# Generate CA private key
echo "📝 Creating Certificate Authority..."
openssl genrsa -out ca/ca-key.pem 4096

# Generate CA certificate
openssl req -new -x509 -days 365 \
  -key ca/ca-key.pem \
  -out ca/ca-cert.pem \
  -subj "/C=DE/ST=Bayern/L=Munich/O=DBH-Training/CN=DBH-CA"

echo "✅ CA created: ca/ca-cert.pem"

# Generate server private key
echo "📝 Creating server certificate..."
openssl genrsa -out server/server-key.pem 4096

# Generate server certificate request
openssl req -new \
  -key server/server-key.pem \
  -out server/server.csr \
  -subj "/C=DE/ST=Bayern/L=Munich/O=DBH-Training/CN=localhost"

# Sign server certificate with CA
openssl x509 -req -days 365 \
  -in server/server.csr \
  -CA ca/ca-cert.pem \
  -CAkey ca/ca-key.pem \
  -CAcreateserial \
  -out server/server-cert.pem

echo "✅ Server certificate created: server/server-cert.pem"

# Generate client private key
echo "📝 Creating client certificate..."
openssl genrsa -out client/client-key.pem 4096

# Generate client certificate request
openssl req -new \
  -key client/client-key.pem \
  -out client/client.csr \
  -subj "/C=DE/ST=Bayern/L=Munich/O=DBH-Training/CN=api-client"

# Sign client certificate with CA
openssl x509 -req -days 365 \
  -in client/client.csr \
  -CA ca/ca-cert.pem \
  -CAkey ca/ca-key.pem \
  -CAcreateserial \
  -out client/client-cert.pem

echo "✅ Client certificate created: client/client-cert.pem"

# Create Java keystore for server
echo "📦 Creating Java keystores..."
openssl pkcs12 -export \
  -in server/server-cert.pem \
  -inkey server/server-key.pem \
  -out server/server.p12 \
  -name server \
  -passout pass:changeit

keytool -importkeystore \
  -srckeystore server/server.p12 \
  -srcstoretype pkcs12 \
  -srcstorepass changeit \
  -destkeystore server/server-keystore.jks \
  -deststorepass changeit \
  -noprompt 2>/dev/null

# Create truststore with CA cert
keytool -import \
  -file ca/ca-cert.pem \
  -alias ca \
  -keystore server/truststore.jks \
  -storepass changeit \
  -noprompt 2>/dev/null

echo "✅ Keystores created:"
echo "   - server/server-keystore.jks (server certificate)"
echo "   - server/truststore.jks (CA certificate)"

# Create client PKCS12 for Java clients
openssl pkcs12 -export \
  -in client/client-cert.pem \
  -inkey client/client-key.pem \
  -out client/client.p12 \
  -name client \
  -passout pass:changeit

echo "✅ Client PKCS12 created: client/client.p12"

echo ""
echo "🎉 All certificates generated successfully!"
echo ""
echo "📋 Quick test commands:"
echo ""
echo "1. Test with curl:"
echo "   curl --cert client/client-cert.pem --key client/client-key.pem https://localhost:8443/api/secure"
echo ""
echo "2. View certificate details:"
echo "   openssl x509 -in client/client-cert.pem -text -noout"
echo ""
echo "3. Java system properties for server:"
echo "   -Djavax.net.ssl.keyStore=server/server-keystore.jks"
echo "   -Djavax.net.ssl.keyStorePassword=changeit"
echo "   -Djavax.net.ssl.trustStore=server/truststore.jks"
echo "   -Djavax.net.ssl.trustStorePassword=changeit"