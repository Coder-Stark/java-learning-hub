# ORM, JPA, Hibernate, and Spring Data

When developing Java applications that interact with databases, several technologies work together. Since these terms are often confused, it's important to understand **what each one does** and **how they relate to one another**.

---

# The Big Picture

The following diagram shows the relationship between all the technologies.

```text
                Java Application
                       │
                       ▼
               Spring Data JPA
          (High-Level Abstraction)
                       │
                       ▼
                     JPA
          (Java Persistence API)
                       │
        Needs an Implementation
                       │
                       ▼
                  Hibernate
      (JPA Implementation / ORM Framework)
                       │
                       ▼
                     ORM
(Object Relational Mapping Technique)
                       │
                       ▼
              Relational Database
      (MySQL, PostgreSQL, Oracle, etc.)
```

For MongoDB, the flow is different.

```text
                Java Application
                       │
                       ▼
            Spring Data MongoDB
                       │
                       ▼
              MongoDB Java Driver
                       │
                       ▼
                    MongoDB
```

---

# What is ORM?

**ORM** stands for **Object Relational Mapping**.

It is a **technique**, not a framework or library.

ORM maps Java objects to database tables so that you can work with Java objects instead of writing SQL for every operation.

---

## Without ORM

Suppose you have the following table:

| id | name   | age |
| -- | ------ | --- |
| 1  | Shivam | 22  |

To retrieve data without ORM, you would:

* Execute an SQL query.
* Read the result.
* Manually create a Java object.
* Copy each column into the object.

This requires a lot of boilerplate code.

---

## With ORM

Suppose you have the following Java class:

```java
public class User {

    private Long id;
    private String name;
    private int age;

}
```

ORM automatically maps it to the database table.

| Java Object | Database Table |
| ----------- | -------------- |
| `User`      | `users`        |
| `id`        | `id`           |
| `name`      | `name`         |
| `age`       | `age`          |

Now you can simply write:

```java
userRepository.save(user);
```

instead of writing SQL manually.

---

# What is JPA?

**JPA** stands for **Java Persistence API**.

It is a **Java specification (API)** that defines a standard way to perform ORM.

JPA provides:

* Interfaces
* Annotations
* Rules for interacting with relational databases

JPA itself **does not contain any implementation**.

It only defines **what should happen**, not **how it happens**.

---

## Common JPA Annotations

Some commonly used JPA annotations include:

* `@Entity`
* `@Table`
* `@Id`
* `@Column`
* `@GeneratedValue`

These annotations describe how Java classes should be mapped to database tables.

---

# What is a Persistence Provider?

Since JPA is only a specification, it requires an implementation.

That implementation is called a **Persistence Provider**.

A persistence provider implements all the interfaces defined by JPA and performs the actual database operations.

Common JPA Persistence Providers include:

* Hibernate (Most Popular)
* EclipseLink
* OpenJPA

---

# What is Hibernate?

**Hibernate** is the most widely used **JPA Persistence Provider**.

It is also an **ORM Framework**.

Hibernate:

* Implements the JPA specification.
* Performs the actual ORM mapping.
* Generates SQL queries automatically.
* Executes database operations.

Example:

When you write:

```java
userRepository.save(user);
```

Hibernate internally generates SQL similar to:

```sql
INSERT INTO users (name, age)
VALUES ('Shivam', 22);
```

You don't write this SQL yourself.

---

# JPA vs Hibernate

| JPA                                 | Hibernate                           |
| ----------------------------------- | ----------------------------------- |
| Specification                       | Framework                           |
| Defines rules                       | Implements those rules              |
| Cannot work alone                   | Can work independently or with JPA  |
| Provides interfaces and annotations | Performs actual database operations |

Think of it like this:

```text
JPA
│
└── Implemented By
      │
      ├── Hibernate
      ├── EclipseLink
      └── OpenJPA
```

---

# What is Spring Data JPA?

**Spring Data JPA** is built on top of JPA.

It is **not** a JPA implementation.

Instead, it provides higher-level abstractions that reduce the amount of code developers need to write.

Spring Data JPA automatically generates repository implementations and common CRUD operations.

Example:

```java
public interface UserRepository extends JpaRepository<User, Long> {

}
```

Without writing any implementation, you immediately get methods like:

* `save()`
* `findAll()`
* `findById()`
* `deleteById()`
* `count()`

---

## Relationship Between Spring Data JPA and Hibernate

```text
Spring Data JPA
        │
        ▼
       JPA
        │
        ▼
   Hibernate
        │
        ▼
    Relational Database
```

Spring Data JPA still requires a JPA implementation such as Hibernate to perform the actual database operations.

---

# Is JPA Used with MongoDB?

**No.**

JPA is designed specifically for **Relational Databases (RDBMS)**.

Examples include:

* MySQL
* PostgreSQL
* Oracle
* SQL Server

It is **not** used with MongoDB.

---

# Spring Data MongoDB

MongoDB is a **NoSQL database**, so it does not use JPA.

Instead, Spring provides **Spring Data MongoDB**.

Spring Data MongoDB:

* Maps Java objects to MongoDB documents.
* Provides repository support.
* Simplifies CRUD operations.
* Uses the MongoDB Java Driver internally.

Example:

```java
public interface UserRepository extends MongoRepository<User, String> {

}
```

---

# JPA vs Spring Data MongoDB

| Feature        | Spring Data JPA             | Spring Data MongoDB   |
| -------------- | --------------------------- | --------------------- |
| Database Type  | Relational Database (RDBMS) | NoSQL Database        |
| Uses JPA       | Yes                         | No                    |
| Uses Hibernate | Yes (commonly)              | No                    |
| Stores Data As | Tables                      | Collections/Documents |

---

# Query Method DSL

Query Method DSL allows Spring Data to generate queries automatically based on method names.

Example:

```java
List<User> findByName(String name);

List<User> findByAge(int age);

List<User> findByNameAndAge(String name, int age);
```

Spring automatically creates the required query based on the method name.

No manual SQL is required.

This feature is available in both:

* Spring Data JPA
* Spring Data MongoDB

---

# Criteria API

The Criteria API provides a programmatic way to build queries dynamically.

It is useful when:

* Query conditions are optional.
* Filters change at runtime.
* Complex search functionality is required.

Instead of writing fixed queries, you build them using Java code.

It is more flexible than Query Method DSL but also more verbose.

---

# Query Method DSL vs Criteria API

| Query Method DSL              | Criteria API                         |
| ----------------------------- | ------------------------------------ |
| Simple and easy to use        | More flexible                        |
| Query is based on method name | Query is built programmatically      |
| Best for simple queries       | Best for complex and dynamic queries |
| Less code                     | More code                            |

---

# Complete Relationship Diagram

```text
ORM
│
├── Technique for mapping Java Objects ↔ Database Tables
│
▼
JPA
│
├── Java Specification (API)
│
▼
Persistence Provider
│
├── Hibernate
├── EclipseLink
└── OpenJPA
│
▼
Spring Data JPA
│
├── Simplifies CRUD Operations
├── Repository Support
└── Query Generation
│
▼
Relational Database
```

For MongoDB:

```text
MongoDB
│
▼
Spring Data MongoDB
│
├── Repository Support
├── CRUD Operations
├── Query Method DSL
└── Criteria API
```

---

# Summary

| Technology           | What Is It?                        | Used For                                                                  |
| -------------------- | ---------------------------------- | ------------------------------------------------------------------------- |
| ORM                  | Technique                          | Maps Java objects to database tables                                      |
| JPA                  | Specification (API)                | Defines the standard for ORM in Java                                      |
| Hibernate            | ORM Framework & JPA Implementation | Implements JPA and performs database operations                           |
| Persistence Provider | JPA Implementation                 | Executes the actual database interaction                                  |
| Spring Data JPA      | Spring Module                      | Simplifies working with JPA by providing repositories and CRUD operations |
| Spring Data MongoDB  | Spring Module                      | Simplifies working with MongoDB without using JPA                         |
| Query Method DSL     | Query Generation Technique         | Creates queries from repository method names                              |
| Criteria API         | Query Builder                      | Builds dynamic and complex queries programmatically                       |
