# Diakon Bank API Testing Guide

This document provides a set of `curl` commands to test the various services of the Diakon Bank application.

## Authentication Service (`auth-service` on port 8080)

### 1. Register a new user

```bash
curl -X POST http://192.168.1.13:8080/api/v1/auth/register \
-H "Content-Type: application/json" \
-d '{
    "username": "testuser",
    "password": "password"
}'
```

### 2. Log in to get a JWT token

This command will return a JWT token that you'll need to use for authenticated endpoints.

```bash
curl -X POST http://192.168.1.13:8080/api/v1/auth/login \
-H "Content-Type: application/json" \
-d '{
    "username": "testuser",
    "password": "password"
}'
```

After running this, you'll get a response like: `{"token":"your_jwt_token_here"}`. Copy that token!

### 3. Connect a bank account

You'll need to replace `YOUR_JWT_TOKEN` with the token you received from the login step.

```bash
export JWT_TOKEN="YOUR_JWT_TOKEN"

curl -X POST http://192.168.1.13:8080/api/v1/auth/connect-bank \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $JWT_TOKEN" \
-d '{
    "bankLogin": "client_id",
    "bankPassword": "client_secret",
    "bank": "VBANK"
}'
```
> Note: Supported banks are `VBANK`, `SBANK`, `ABANK`.


## Bank API Service (`bank-api-service` on port 6666)

These endpoints require an authentication token.

### 1. Get accounts

```bash
export JWT_TOKEN="YOUR_JWT_TOKEN"

curl -X GET http://192.168.1.13:6666/api/v1/bank/accounts \
-H "Authorization: Bearer $JWT_TOKEN"
```

### 2. Get transactions

```bash
export JWT_TOKEN="YOUR_JWT_TOKEN"

curl -X GET http://192.168.1.13:6666/api/v1/bank/transactions \
-H "Authorization: Bearer $JWT_TOKEN"
```

## Financial Pulse Service (`financial-pulse-service` on port 8083)

These endpoints require the user's ID to be passed in the `X-User-Id` header.

### 1. Get accounts from Pulse

```bash
curl -X GET http://192.168.1.13:8083/api/v1/pulse/accounts \
-H "X-User-Id: 1"
```

### 2. Get transactions from Pulse

```bash
curl -X GET http://192.168.1.13:8083/api/v1/pulse/transactions \
-H "X-User-Id: 1"
``` 