# Bank API Service

This is a Spring Boot microservice that integrates with a bank's Open Banking API to fetch account and transaction data.

## Features

*   **Data Synchronization**: Fetches account and transaction data from the VBank Open Banking sandbox API.
*   **Incremental Sync**: Only fetches new transactions, avoiding duplicate data.
*   **Resilient**: Automatically retries failed API calls.
*   **Clean Architecture**: Separates API client logic from business logic for better maintainability and testing.

## Running the Application

1.  **Start PostgreSQL Database**:
    ```bash
    docker run --name bankdb -e POSTGROS_PASSWORD=secret -p 5432:5432 -d postgres
    ```

2.  **Create Database**:
    ```bash
    docker exec -it bankdb psql -U postgres -c "CREATE DATABASE bankdb"
    ```

3.  **Configure Credentials**:
    Open `src/main/resources/application.properties` and set your VBank API credentials:
    ```properties
    bank.client.id=your-team-id
    bank.client.suffix=your-client-suffix
    bank.client.secret=your-client-secret
    ```

4.  **Run the Application**:
    ```bash
    mvn spring-boot:run
    ```

## Testing the API

1.  **Trigger Data Synchronization**:
    This will fetch all accounts and transactions for the specified user.
    ```bash
    curl -X POST http://localhost:8080/api/v1/internal/sync/1
    ```

2.  **Get Accounts**:
    Retrieve all synced accounts for the user.
    ```bash
    curl http://localhost:8080/api/v1/accounts
    ```

3.  **Get Transactions**:
    Retrieve all synced transactions for the user.
    ```bash
    curl http://localhost:8080/api/v1/transactions
    ```

## Project Summary & Key Learnings

This project successfully integrates with the VBank Open Banking sandbox API. The development process involved significant debugging and reverse-engineering of the API's behavior, which often differed from its initial documentation.

Key challenges and solutions included:

*   **Authentication**: Discovered that the `/auth/bank-token` endpoint required credentials as query parameters on a POST request.
*   **Consent Management**: Correctly constructed the request for the `/account-consents/request` endpoint, including the `client_id` in the body and a `X-Requesting-Bank` header with the team ID.
*   **Data Parsing**: Handled nested data structures in the API responses for accounts and transactions (e.g., `data.account` and `data.transaction`).
*   **Pagination**: Implemented a loop to fetch all pages of transactions from the API.
*   **Data Persistence**: Implemented an "upsert" logic for accounts and an incremental sync for transactions to ensure data integrity and efficiency on subsequent runs.
*   **Code Quality**: Refactored the initial implementation to separate the `BankApiClient` from the `BankIntegrationService`, resulting in a cleaner, more maintainable, and testable codebase.

The final service is a robust and efficient data synchronization tool that successfully navigates the quirks of the VBank API. 