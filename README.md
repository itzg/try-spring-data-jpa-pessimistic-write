## Context

Demonstrates the use of Spring Data JPA with [Jakarta Persistence locking](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/persist/persistence-locking/persistence-locking.html) with pessimistic write locks.

It implements the classic and naive bank account balance scenario to simulate the case of

1. Retrieving an account entity
2. Adding a deposit amount to the account's balance
3. Saving the entity back to the database

## Running test

The unit test in `me.itzg.app.ApplicationTests` runs with a single thread and then with multiple threads to demonstrate that the lock prevents concurrent deposit-writes to the account balance.

If you were to remove the `@Lock` from the method `me.itzg.app.db.AccountRepository.findAccountById` then the test will fail in the multi-threaded case.

## Running manually

Run the Spring Boot app in your favorite IDE or

```shell
./gradlew bootRun
```

It uses Spring Boot's [compose support](https://docs.spring.io/spring-boot/reference/features/dev-services.html#features.dev-services.docker-compose), so a MySQL container will be started when the application starts.

## Demo REST calls

Create an account

```http request
POST http://localhost:8080/accounts
```

Get an account

```http request
GET http://localhost:8080/accounts/1
```

Submit a deposit

```http request
POST http://localhost:8080/accounts/1/_deposit
Content-Type: application/x-www-form-urlencoded

amount=12.25
```