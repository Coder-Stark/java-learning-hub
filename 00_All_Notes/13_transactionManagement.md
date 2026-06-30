# Transaction Management in Spring Boot (`@Transactional`)

When an application performs **multiple database operations** as part of a single business operation, it is important that **either all operations succeed or none of them do**.

This is achieved using **Transactions**.

Spring provides transaction management through the `@Transactional` annotation.

---

# What is a Transaction?

A **Transaction** is a sequence of one or more database operations that are treated as a **single unit of work**.

A transaction guarantees that:

* Either **all operations are completed successfully**
* Or **all operations are rolled back**

There is no partial success.

---

# Why Are Transactions Needed?

Suppose creating a journal entry involves two database operations.

```text id="gldaf6"
Operation 1
Save Journal Entry

        │

Operation 2
Update User's journalEntries List
```

Both operations together represent a **single business operation**:

> "Create a journal entry for a user."

If one succeeds but the other fails, the database becomes inconsistent.

---

# The Problem Without Transactions

Consider the following sequence.

```text id="sgsvti"
Step 1
Save Journal Entry
        │
        ▼
Success ✅
        │
        ▼
Step 2
Update User
        │
        ▼
Failure ❌
```

Database state:

```text id="ihpobd"
journal_entries Collection

✔ Journal Saved


users Collection

✘ Journal Reference Missing
```

Now the journal exists, but the user does not reference it.

This is called **Data Inconsistency**.

---

# Real Example

Suppose the database initially contains:

```text id="q5yeok"
User

Shivam

Journal Entries

None
```

You attempt to create a journal.

Step 1:

```text id="2iy8nk"
Save Journal

Success
```

Step 2:

```text id="9f3rsy"
Update User

Failure
```

Result:

```text id="8rjlwm"
journal_entries

Journal 1 ✔


users

Shivam
journalEntries = [ ]
```

The journal has no owner.

This inconsistent state should never happen.

---

# Solution: `@Transactional`

Spring solves this problem using the `@Transactional` annotation.

```java id="qv9l6x"
@Transactional
public void saveEntry(
        JournalEntry journalEntry,
        String userName) {

}
```

This tells Spring:

> "Treat everything inside this method as a single transaction."

---

# How `@Transactional` Works

```text id="fhh3eq"
Method Starts
      │
      ▼
Transaction Begins
      │
      ▼
Execute All Database Operations
      │
      ▼
All Successful?
      │
  ┌───┴────┐
  │        │
 Yes       No
  │        │
Commit   Rollback
```

There are only two possible outcomes.

* **Commit**
* **Rollback**

---

# Atomicity

`@Transactional` provides the **Atomicity** property of ACID.

Atomicity means:

> **All operations succeed or none of them succeed.**

There is no partial execution.

Example:

```text id="l14l7h"
Journal Saved
+
User Updated

✔ Commit
```

or

```text id="q3m2a0"
Journal Saved
+
User Update Failed

✘ Rollback Everything
```

---

# ACID Properties

Transactions follow the **ACID** principles.

| Property        | Meaning                                                            |
| --------------- | ------------------------------------------------------------------ |
| **Atomicity**   | All operations succeed or all fail together                        |
| **Consistency** | Database remains in a valid state before and after the transaction |
| **Isolation**   | Concurrent transactions do not interfere with each other           |
| **Durability**  | Once committed, data is permanently stored                         |

In this example, the focus is on **Atomicity**.

---

# Enabling Transaction Management

Spring Boot transaction management is enabled using:

```java id="tvtdht"
@EnableTransactionManagement
```

```java id="75ljvf"
@SpringBootApplication
@EnableTransactionManagement
public class JournalApplication {

}
```

This tells Spring to detect and process `@Transactional` methods.

---

# Configuring a Transaction Manager

Since the application uses MongoDB, Spring needs a transaction manager.

```java id="vnd9fc"
@Bean
public PlatformTransactionManager add(
        MongoDatabaseFactory dbFactory) {

    return new MongoTransactionManager(dbFactory);

}
```

---

## Why is a Transaction Manager Needed?

The transaction manager is responsible for:

* Starting transactions
* Committing transactions
* Rolling back transactions

Flow:

```text id="m6lcgb"
@Transactional
        │
        ▼
MongoTransactionManager
        │
        ▼
MongoDB
```

Without a transaction manager, Spring cannot manage transactions.

---

# JournalEntryService Before Transaction

Originally, the method looked like this:

```java id="q3qkri"
public void saveEntry(
        JournalEntry journalEntry,
        String userName) {

    User user =
            userService.findByUserName(userName);

    journalEntry.setDate(LocalDateTime.now());

    JournalEntry saved =
            journalEntryRepository.save(journalEntry);

    user.getJournalEntries().add(saved);

    userService.saveEntry(user);

}
```

If the last line failed:

```java id="1ppgjx"
userService.saveEntry(user);
```

the journal had already been stored.

Result:

```text id="4grw0q"
journal_entries

✔ Saved


users

✘ Not Updated
```

This produced inconsistent data.

---

# JournalEntryService After Transaction

The method now uses `@Transactional`.

```java id="o66hke"
@Transactional
public void saveEntry(
        JournalEntry journalEntry,
        String userName) {

    try {

        User user =
                userService.findByUserName(userName);

        journalEntry.setDate(LocalDateTime.now());

        JournalEntry saved =
                journalEntryRepository.save(journalEntry);

        user.getJournalEntries().add(saved);

        userService.saveEntry(user);

    } catch (Exception e) {

        throw new RuntimeException(e);

    }

}
```

Now both database operations belong to a single transaction.

---

# Testing Atomicity

To verify transaction behavior, an error was intentionally introduced.

```java id="9od7tz"
user.setUserName(null);
```

The `userName` field has a unique index and should not be `null`, so saving the user fails.

Execution flow:

```text id="ym8g7r"
Save Journal
      │
      ▼
Success
      │
      ▼
Set Username = null
      │
      ▼
Save User
      │
      ▼
Exception
```

---

# Without `@Transactional`

```text id="sl6jql"
Save Journal
      │
      ▼
✔ Success
      │
      ▼
Save User
      │
      ▼
❌ Failure
```

Final database state:

```text id="nn4b6x"
journal_entries

✔ Journal Exists


users

✘ Journal Reference Missing
```

This leaves the database inconsistent.

---

# With `@Transactional`

```text id="sah2j3"
Save Journal
      │
      ▼
✔ Success
      │
      ▼
Save User
      │
      ▼
❌ Failure
      │
      ▼
Rollback
```

Final database state:

```text id="tfvj5d"
journal_entries

Nothing Saved


users

Nothing Changed
```

Spring automatically rolls back every database operation performed within the transaction.

---

# Complete Transaction Flow

```text id="1yyjl6"
Client
      │
      ▼
Controller
      │
      ▼
@Transactional Method
      │
      ▼
Begin Transaction
      │
      ▼
Save Journal Entry
      │
      ▼
Update User
      │
      ▼
Any Exception?
      │
 ┌────┴────┐
 │         │
 No       Yes
 │         │
 ▼         ▼
Commit   Rollback
```

---

# Exception Handling

Inside the method:

```java id="rqkhrz"
catch (Exception e) {

    throw new RuntimeException(e);

}
```

Why rethrow the exception?

Spring only rolls back a transaction when an exception propagates out of the transactional method.

If the exception is caught and suppressed:

```java id="n13rzs"
catch (Exception e) {

    System.out.println("Error");

}
```

Spring assumes the method completed successfully and commits the transaction.

By rethrowing the exception:

```java id="sdlnpk"
throw new RuntimeException(e);
```

Spring detects the failure and performs a rollback.

---

# Summary

| Concept                           | Description                                                       |
| --------------------------------- | ----------------------------------------------------------------- |
| Transaction                       | A group of database operations treated as one unit of work        |
| `@Transactional`                  | Executes all database operations within a single transaction      |
| Atomicity                         | Either all operations succeed or all are rolled back              |
| Commit                            | Permanently saves all changes                                     |
| Rollback                          | Reverts every change made during the transaction                  |
| `@EnableTransactionManagement`    | Enables Spring's transaction management support                   |
| `MongoTransactionManager`         | Manages transactions for MongoDB                                  |
| `PlatformTransactionManager`      | Spring's abstraction for transaction managers                     |
| `throw new RuntimeException(...)` | Ensures Spring detects the failure and rolls back the transaction |
