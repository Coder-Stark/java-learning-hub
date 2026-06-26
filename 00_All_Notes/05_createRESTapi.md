# Building Your First REST API in Spring Boot

In this chapter, we'll build our first REST API using Spring Boot and understand how it compares to a typical Express.js application.

---

# What is a REST API?

**REST** stands for **Representational State Transfer**.

It is an architectural style used to enable communication between a **Client** and a **Server** over the **HTTP protocol**.

```text
Client (React / React Native / Browser / Postman)
                    │
            HTTP Request
                    │
                    ▼
          Spring Boot REST API
                    │
           Process Request
                    │
                    ▼
            HTTP Response
                    │
                    ▼
               Client
```

A REST API exposes endpoints that allow clients to perform operations such as:

* Create data
* Read data
* Update data
* Delete data

These operations are commonly referred to as **CRUD**.

---

# Common HTTP Methods

| HTTP Method | CRUD Operation | Purpose              |
| ----------- | -------------- | -------------------- |
| GET         | Read           | Retrieve data        |
| POST        | Create         | Create new data      |
| PUT         | Update         | Update existing data |
| DELETE      | Delete         | Delete existing data |

> **Note:** Your notes listed `POST` twice. The correct list is **GET, POST, PUT, DELETE**.

---

# REST API Flow

```text
React / React Native
        │
 GET /journal
        │
        ▼
Spring Boot Controller
        │
 Business Logic
        │
        ▼
Return JSON Response
        │
        ▼
Client Receives Data
```

---

# MERN vs Spring Boot Architecture

If you're transitioning from the MERN stack, the following mapping will help you understand the equivalent components in Spring Boot.

| MERN (Express.js) | Spring Boot                       | Purpose                     |
| ----------------- | --------------------------------- | --------------------------- |
| `server.js`       | `JournalApplication.java`         | Application entry point     |
| `app.js`          | Spring Boot Configuration         | Application configuration   |
| Routes            | Controller                        | Define API endpoints        |
| Controller        | Controller                        | Handle HTTP requests        |
| Service           | Service                           | Business logic              |
| Model (Mongoose)  | Entity                            | Represents database objects |
| MongoDB Queries   | Repository                        | Database interaction        |
| Middleware        | Security / Filters / Interceptors | Request processing          |
| `package.json`    | `pom.xml`                         | Project dependencies        |
| `.env`            | `application.properties`          | Configuration values        |

---

# Project Structure (Current Progress)

At this stage, the project contains the following structure:

```text
src
├── main
│   ├── java
│   │   └── org
│   │       └── rajbhar
│   │           └── journalapp
│   │               ├── JournalApplication.java
│   │               └── controller
│   │                   ├── HealthCheck.java
│   │                   └── JournalEntryController.java
│   │
│   └── resources
│       ├── static
│       ├── templates
│       └── application.properties
│
└── test
    └── java
        └── org
            └── rajbhar
                └── journalapp
                    └── JournalApplicationTests.java
```

---

# Entry Point of the Application

```java
@SpringBootApplication
public class JournalApplication {

    public static void main(String[] args) {
        SpringApplication.run(JournalApplication.class, args);
    }

}
```

### Purpose

This class starts the Spring Boot application.

When executed, it:

* Creates the Spring IoC Container
* Scans for Beans
* Starts the embedded Tomcat server
* Makes the REST APIs available

---

# Health Check API

A Health Check endpoint is commonly used to verify that the server is running correctly.

```java
@RestController
public class HealthCheck {

    @GetMapping("/health-check")
    public String healthCheck() {
        return "OK";
    }

}
```

### URL

```http
GET /health-check
```

### Response

```text
OK
```

---

# `@RestController`

```java
@RestController
public class HealthCheck {

}
```

`@RestController` tells Spring that:

* This class handles HTTP requests.
* The return value of each method should be sent directly as the HTTP response.
* Responses are automatically converted to JSON (or plain text when returning a `String`).

Without `@RestController`, Spring treats the class as a normal Java class and does not expose any REST endpoints.

---

# `@GetMapping`

```java
@GetMapping("/health-check")
```

Maps an HTTP **GET** request to a Java method.

When the client sends:

```http
GET /health-check
```

Spring executes:

```java
healthCheck()
```

and returns:

```text
OK
```

---

# Journal Entry Controller

```java
@RestController
@RequestMapping("/journal")
public class JournalEntryController {

}
```

This controller manages all Journal-related APIs.

---

# `@RequestMapping`

```java
@RequestMapping("/journal")
```

Defines the **base URL** for all endpoints inside the controller.

Instead of writing:

```java
@GetMapping("/journal")
@PostMapping("/journal")
@DeleteMapping("/journal")
```

you write:

```java
@RequestMapping("/journal")
```

and then:

```java
@GetMapping
@PostMapping
```

Spring automatically combines them.

Example:

```text
@RequestMapping("/journal")
        +
@GetMapping
        =
GET /journal
```

---

# Temporary Data Storage

```java
private Map<Long, JournalEntry> journalEntries = new HashMap<>();
```

Currently, no database is connected.

Instead, a `HashMap` is used as an **in-memory database**.

Think of it as:

```text
ID      JournalEntry
------------------------
1   ->  Entry 1
2   ->  Entry 2
3   ->  Entry 3
```

> **Note:** All data is lost when the application restarts because it exists only in memory.

---

# Available APIs

## 1. Get All Journal Entries

```java
@GetMapping
public List<JournalEntry> getAll() {
    return new ArrayList<>(journalEntries.values());
}
```

### URL

```http
GET /journal
```

### Purpose

Returns all journal entries.

---

## 2. Create a Journal Entry

```java
@PostMapping
public boolean createEntry(@RequestBody JournalEntry myEntry) {
    journalEntries.put(myEntry.getId(), myEntry);
    return true;
}
```

### URL

```http
POST /journal
```

### Annotation Used

```java
@RequestBody
```

`@RequestBody` converts the incoming JSON request into a Java object.

Example request:

```json
{
    "id": 1,
    "title": "My First Entry",
    "content": "Learning Spring Boot"
}
```

Spring automatically creates:

```java
JournalEntry myEntry
```

---

## 3. Get Journal Entry by ID

```java
@GetMapping("/id/{myId}")
public JournalEntry getJournalEntryById(@PathVariable Long myId) {
    return journalEntries.get(myId);
}
```

### URL

```http
GET /journal/id/1
```

### `@PathVariable`

Extracts values from the URL.

Example:

```text
GET /journal/id/10
```

becomes

```java
Long myId = 10;
```

---

## 4. Delete Journal Entry

```java
@DeleteMapping("/id/{myId}")
public JournalEntry deleteJournalEntryById(@PathVariable Long myId) {
    return journalEntries.remove(myId);
}
```

### URL

```http
DELETE /journal/id/1
```

Deletes the journal entry with the specified ID.

---

## 5. Update Journal Entry

```java
@PutMapping("/id/{myId}")
public JournalEntry updateJournalEntryById(
        @PathVariable Long myId,
        @RequestBody JournalEntry myEntry) {

    return journalEntries.put(myId, myEntry);
}
```

### URL

```http
PUT /journal/id/1
```

Updates the existing journal entry.

---

# API Summary

| Method | URL                | Purpose                        |
| ------ | ------------------ | ------------------------------ |
| GET    | `/health-check`    | Check if the server is running |
| GET    | `/journal`         | Retrieve all journal entries   |
| POST   | `/journal`         | Create a new journal entry     |
| GET    | `/journal/id/{id}` | Retrieve a journal entry by ID |
| PUT    | `/journal/id/{id}` | Update a journal entry by ID   |
| DELETE | `/journal/id/{id}` | Delete a journal entry by ID   |

---

# Spring Annotations Learned So Far

| Annotation               | Purpose                                       |
| ------------------------ | --------------------------------------------- |
| `@SpringBootApplication` | Starts the Spring Boot application            |
| `@RestController`        | Marks a class as a REST Controller            |
| `@RequestMapping`        | Defines a base URL for a controller           |
| `@GetMapping`            | Maps HTTP GET requests                        |
| `@PostMapping`           | Maps HTTP POST requests                       |
| `@PutMapping`            | Maps HTTP PUT requests                        |
| `@DeleteMapping`         | Maps HTTP DELETE requests                     |
| `@RequestBody`           | Converts JSON request body into a Java object |
| `@PathVariable`          | Extracts values from the URL path             |

---

# Current Project Status

At this point, you have successfully learned:

* ✅ Spring Boot project creation
* ✅ Application entry point
* ✅ REST API fundamentals
* ✅ Controllers
* ✅ Request mappings
* ✅ GET, POST, PUT, and DELETE APIs
* ✅ `@RequestBody`
* ✅ `@PathVariable`
* ✅ Using a `HashMap` as temporary storage

> **Next Step:** Replace the in-memory `HashMap` with a real database (such as MongoDB) using **Spring Data MongoDB** and a **Repository** layer.
