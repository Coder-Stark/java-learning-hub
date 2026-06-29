# HTTP Status Codes and `ResponseEntity` in Spring Boot

When a client sends an HTTP request to a Spring Boot application, the server responds with:

* A **Status Code**
* Optional **Headers**
* A **Response Body**

Spring Boot provides the `ResponseEntity` class to customize all three parts of the HTTP response.

---

# HTTP Response Structure

Every HTTP response consists of three main parts.

```text
HTTP Response
│
├── Status Code
├── Headers
└── Body
```

Example:

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
    "id": 1,
    "title": "Learning Spring Boot"
}
```

---

# What is an HTTP Status Code?

An **HTTP Status Code** is a **3-digit numeric code** returned by the server to indicate the result of an HTTP request.

It helps the client understand whether the request was:

* Successful
* Failed
* Redirected
* Invalid
* Blocked due to permissions

---

# Categories of HTTP Status Codes

HTTP status codes are divided into **five categories** based on their first digit.

| Category | Meaning       |
| -------- | ------------- |
| **1xx**  | Informational |
| **2xx**  | Success       |
| **3xx**  | Redirection   |
| **4xx**  | Client Error  |
| **5xx**  | Server Error  |

---

# 1xx - Informational

These codes indicate that the request has been received and processing is continuing.

Example:

| Code    | Meaning             |
| ------- | ------------------- |
| **100** | Continue            |
| **101** | Switching Protocols |

> These status codes are rarely returned in REST APIs.

---

# 2xx - Success

The request was successfully processed.

| Status Code        | Meaning                                | Typical Use |
| ------------------ | -------------------------------------- | ----------- |
| **200 OK**         | Request completed successfully         | GET, PUT    |
| **201 Created**    | Resource created successfully          | POST        |
| **204 No Content** | Request succeeded but no response body | DELETE      |

Example:

```http
GET /journal

HTTP/1.1 200 OK
```

---

# 3xx - Redirection

The client needs to take additional action to complete the request.

| Status Code | Meaning                    |
| ----------- | -------------------------- |
| **301**     | Moved Permanently          |
| **302**     | Found (Temporary Redirect) |
| **304**     | Not Modified               |

These are commonly used by web browsers rather than REST APIs.

---

# 4xx - Client Errors

The problem is caused by the client.

| Status Code | Meaning      |
| ----------- | ------------ |
| **400**     | Bad Request  |
| **401**     | Unauthorized |
| **403**     | Forbidden    |
| **404**     | Not Found    |

### Difference Between 401 and 403

| Status Code          | Meaning                                                                    |
| -------------------- | -------------------------------------------------------------------------- |
| **401 Unauthorized** | User is not authenticated (login required).                                |
| **403 Forbidden**    | User is authenticated but does not have permission to access the resource. |

Example:

```http
GET /journal/id/999

HTTP/1.1 404 Not Found
```

---

# 5xx - Server Errors

The request was valid, but the server failed while processing it.

| Status Code | Meaning               |
| ----------- | --------------------- |
| **500**     | Internal Server Error |
| **502**     | Bad Gateway           |
| **503**     | Service Unavailable   |

Example:

```http
HTTP/1.1 500 Internal Server Error
```

---

# What is `ResponseEntity`?

`ResponseEntity` is a class provided by the Spring Framework that allows complete control over an HTTP response.

Using `ResponseEntity`, you can customize:

* Response Body
* HTTP Status Code
* HTTP Headers

Instead of simply returning data, you return a complete HTTP response.

---

# Why Use `ResponseEntity`?

Without `ResponseEntity`, Spring returns the object with a default status code (usually **200 OK**).

Example:

```java
@GetMapping
public JournalEntry getEntry() {
    return journalEntry;
}
```

Response:

```http
200 OK
```

You have no control over the status code.

---

With `ResponseEntity`:

```java
@GetMapping
public ResponseEntity<JournalEntry> getEntry() {
    return new ResponseEntity<>(journalEntry, HttpStatus.OK);
}
```

Now you explicitly control the response.

---

# Structure of `ResponseEntity`

```java
ResponseEntity<T>
```

Where:

* `T` = Type of response body

Examples:

```java
ResponseEntity<String>

ResponseEntity<JournalEntry>

ResponseEntity<List<JournalEntry>>

ResponseEntity<Object>

ResponseEntity<?>
```

---

# Constructors

## Return Body + Status

```java
new ResponseEntity<>(body, HttpStatus.OK);
```

Example:

```java
return new ResponseEntity<>(journalEntry, HttpStatus.OK);
```

---

## Return Only Status

```java
new ResponseEntity<>(HttpStatus.NOT_FOUND);
```

Example:

```java
return new ResponseEntity<>(HttpStatus.NOT_FOUND);
```

No response body is returned.

---

# Understanding the Generic Type

Example:

```java
public ResponseEntity<JournalEntry> createEntry(...)
```

means:

```text
Response Body
      │
      ▼
JournalEntry
```

Example:

```java
return new ResponseEntity<>(journalEntry, HttpStatus.CREATED);
```

Response:

```http
HTTP/1.1 201 Created

{
    "id": "...",
    "title": "...",
    "content": "..."
}
```

---

# Why `ResponseEntity<?>`?

Sometimes a method may return different types of responses.

Example:

```java
if (success) {
    return new ResponseEntity<>(journalEntry, HttpStatus.OK);
}

return new ResponseEntity<>(HttpStatus.NOT_FOUND);
```

One response contains a `JournalEntry`.

The other contains no body.

Using:

```java
ResponseEntity<?>
```

allows both responses.

The wildcard (`?`) means **any type of response body**.

---

# `ResponseEntity` in the Journal Application

## 1. Get All Journal Entries

```java
@GetMapping
public ResponseEntity<?> getAll() {

    List<JournalEntry> all = journalEntryService.getAll();

    if (all != null && !all.isEmpty()) {
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
}
```

### Possible Responses

#### Data Found

```http
HTTP/1.1 200 OK
```

Returns:

```json
[
    {
        "title": "Entry 1"
    },
    {
        "title": "Entry 2"
    }
]
```

---

#### No Data Found

```http
HTTP/1.1 404 Not Found
```

No response body.

---

## 2. Create Journal Entry

```java
@PostMapping
public ResponseEntity<JournalEntry> createEntry(
        @RequestBody JournalEntry myEntry) {

    try {

        myEntry.setDate(LocalDateTime.now());

        journalEntryService.saveEntry(myEntry);

        return new ResponseEntity<>(myEntry,
                HttpStatus.CREATED);

    } catch (Exception e) {

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

}
```

### Success

```http
HTTP/1.1 201 Created
```

Returns the created journal entry.

---

### Failure

```http
HTTP/1.1 400 Bad Request
```

This usually indicates that the client sent invalid data.

---

## 3. Get Journal Entry by ID

```java
@GetMapping("/id/{myId}")
public ResponseEntity<JournalEntry> getJournalEntryById(
        @PathVariable ObjectId myId) {

    Optional<JournalEntry> journalEntry =
            journalEntryService.findById(myId);

    if (journalEntry.isPresent()) {
        return new ResponseEntity<>(
                journalEntry.get(),
                HttpStatus.OK);
    }

    return new ResponseEntity<>(HttpStatus.NOT_FOUND);

}
```

### Success

```http
200 OK
```

Returns the journal entry.

---

### Failure

```http
404 Not Found
```

The requested journal entry does not exist.

---

## 4. Delete Journal Entry

```java
@DeleteMapping("/id/{myId}")
public ResponseEntity<?> deleteJournalEntryById(
        @PathVariable ObjectId myId) {

    JournalEntry journalEntry =
            journalEntryService.findById(myId).orElse(null);

    if (journalEntry != null) {

        journalEntryService.deleteById(myId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    return new ResponseEntity<>(HttpStatus.NOT_FOUND);

}
```

### Success

```http
204 No Content
```

The journal entry is deleted successfully.

No response body is returned.

---

### Failure

```http
404 Not Found
```

The requested journal entry does not exist.

---

## 5. Update Journal Entry

```java
@PutMapping("/id/{myId}")
public ResponseEntity<?> updateJournalEntryById(
        @PathVariable ObjectId myId,
        @RequestBody JournalEntry newEntry) {

    JournalEntry oldEntry =
            journalEntryService.findById(myId).orElse(null);

    if (oldEntry != null) {

        oldEntry.setTitle(
                newEntry.getTitle() != null &&
                !newEntry.getTitle().isEmpty()
                ? newEntry.getTitle()
                : oldEntry.getTitle());

        oldEntry.setContent(
                newEntry.getContent() != null &&
                !newEntry.getContent().isEmpty()
                ? newEntry.getContent()
                : oldEntry.getContent());

        journalEntryService.saveEntry(oldEntry);

        return new ResponseEntity<>(oldEntry,
                HttpStatus.OK);

    }

    return new ResponseEntity<>(HttpStatus.NOT_FOUND);

}
```

### Success

```http
200 OK
```

Returns the updated journal entry.

---

### Failure

```http
404 Not Found
```

The requested journal entry was not found.

---

# Status Codes Used in the Journal Application

| API                       | Success Status     | Failure Status      |
| ------------------------- | ------------------ | ------------------- |
| GET `/journal`            | **200 OK**         | **404 Not Found**   |
| POST `/journal`           | **201 Created**    | **400 Bad Request** |
| GET `/journal/id/{id}`    | **200 OK**         | **404 Not Found**   |
| PUT `/journal/id/{id}`    | **200 OK**         | **404 Not Found**   |
| DELETE `/journal/id/{id}` | **204 No Content** | **404 Not Found**   |

---

# When to Use Common Status Codes

| Status Code                   | When to Use                                         |
| ----------------------------- | --------------------------------------------------- |
| **200 OK**                    | Data retrieved or updated successfully              |
| **201 Created**               | New resource created successfully                   |
| **204 No Content**            | Resource deleted successfully with no response body |
| **400 Bad Request**           | Invalid request data from the client                |
| **401 Unauthorized**          | User is not authenticated                           |
| **403 Forbidden**             | User lacks permission to perform the action         |
| **404 Not Found**             | Requested resource does not exist                   |
| **500 Internal Server Error** | Unexpected server-side error                        |
