# Creating a User Module in Spring Boot with MongoDB

In this section, we create a **User module** that stores user information in MongoDB.

The implementation follows the layered architecture used in Spring Boot:

```text
Client
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
MongoDB
```

Each layer has a specific responsibility, making the application easier to maintain and extend.

---

# Project Structure

The following files are created for the User module.

```text
org
└── rajbhar
    └── journalapp
        ├── controller
        │   └── UserController.java
        │
        ├── service
        │   └── UserService.java
        │
        ├── repository
        │   └── UserRepository.java
        │
        └── entity
            └── User.java
```

---

# Request Flow

Whenever a client sends a request, it passes through the following layers.

```text
Client
   │
HTTP Request
   │
   ▼
UserController
   │
Calls
   │
   ▼
UserService
   │
Calls
   │
   ▼
UserRepository
   │
Spring Data MongoDB
   │
   ▼
MongoDB
```

Each layer has a single responsibility.

---

# User Entity

The `User` class represents a MongoDB document stored in the **users** collection.

```java
@Document(collection = "users")
@Data
public class User {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    @NonNull
    private String userName;

    @NonNull
    private String password;

    @DBRef
    private List<JournalEntry> journalEntries = new ArrayList<>();

}
```

---

# `@Document`

```java
@Document(collection = "users")
```

Marks the class as a MongoDB document.

Spring Data MongoDB stores objects of this class inside the **users** collection.

```text
MongoDB
│
└── users
```

---

# `@Id`

```java
@Id
private ObjectId id;
```

Marks the primary identifier of the document.

MongoDB automatically generates an `ObjectId` when a new document is inserted.

Example:

```json
{
    "_id": ObjectId("685f0f7e3c2d8d76b12c3456")
}
```

---

# `@Indexed`

```java
@Indexed(unique = true)
private String userName;
```

Creates an index on the `userName` field.

Since `unique = true`, MongoDB does not allow duplicate usernames.

Example:

Valid documents:

```text
shivam
rahul
aman
```

Invalid:

```text
shivam
shivam
```

MongoDB throws a duplicate key error.

---

# `@NonNull`

```java
@NonNull
private String userName;
```

Indicates that the field cannot be `null`.

If Lombok-generated constructors receive a `null` value for this field, a `NullPointerException` is thrown.

> **Note:** `@NonNull` is a Lombok annotation. It is **not** a database validation annotation. To enforce validation on incoming API requests, annotations such as `@NotNull` or `@NotBlank` from Jakarta Validation are typically used.

---

# `@DBRef`

```java
@DBRef
private List<JournalEntry> journalEntries;
```

`@DBRef` creates a reference between MongoDB documents.

Instead of embedding complete `JournalEntry` documents inside a user document, MongoDB stores references to them.

Relationship:

```text
User
 │
 │ 1
 │
 ▼
JournalEntry
```

A single user can be associated with multiple journal entries.

---

## Without `@DBRef`

Each journal entry would be stored inside the user document.

```text
User
 ├── username
 ├── password
 └── journalEntries
      ├── Entry 1
      ├── Entry 2
      └── Entry 3
```

---

## With `@DBRef`

Only references are stored.

```text
User
├── username
├── password
└── journalEntries
      ├── ObjectId("...")
      ├── ObjectId("...")
      └── ObjectId("...")
```

The actual journal entries remain in the `journal_entries` collection.

---

# User Repository

```java
public interface UserRepository
        extends MongoRepository<User, ObjectId> {

    User findByUserName(String userName);

}
```

---

# Why Extend `MongoRepository`?

By extending `MongoRepository`, Spring automatically provides common database operations.

Without writing any implementation, you get methods such as:

* `save()`
* `findAll()`
* `findById()`
* `deleteById()`
* `count()`
* `existsById()`

This is one of the major advantages of Spring Data MongoDB.

---

# Custom Query Method

```java
User findByUserName(String userName);
```

This method is **not implemented manually**.

Spring Data MongoDB generates the query automatically using **Query Method DSL**.

Equivalent MongoDB query:

```javascript
db.users.find({
    userName: "shivam"
})
```

---

# User Service

The service layer contains the application's business logic.

```text
Controller
      │
      ▼
Service
      │
      ▼
Repository
```

---

## Get All Users

```java
public List<User> getAll() {
    return userRepository.findAll();
}
```

Returns every user from the database.

---

## Save User

```java
public void saveEntry(User user) {
    userRepository.save(user);
}
```

Saves a new user or updates an existing user.

Spring decides whether to insert or update based on the presence of the document's `id`.

---

## Find User by ID

```java
public Optional<User> findById(ObjectId id) {
    return userRepository.findById(id);
}
```

Returns an `Optional<User>` because the requested user may or may not exist.

---

## Delete User

```java
public void deleteById(ObjectId id) {
    userRepository.deleteById(id);
}
```

Deletes a user using its MongoDB `ObjectId`.

---

## Find User by Username

```java
public User findByUserName(String userName) {
    return userRepository.findByUserName(userName);
}
```

Uses the custom repository method to retrieve a user by username.

---

# Why `@Component`?

```java
@Component
public class UserService {
}
```

`@Component` registers `UserService` as a Spring Bean.

This allows Spring to automatically inject it wherever it is needed using `@Autowired`.

> **Note:** Although `@Component` works correctly, `@Service` is generally preferred for service classes because it better expresses the class's role.

---

# User Controller

The controller handles incoming HTTP requests and delegates work to the service layer.

```text
HTTP Request
      │
      ▼
UserController
      │
      ▼
UserService
```

---

# Get All Users

```java
@GetMapping
public List<User> getAllUsers() {
    return userService.getAll();
}
```

### Endpoint

```http
GET /user
```

Returns all users stored in the database.

---

# Create User

```java
@PostMapping
public void createUser(
        @RequestBody User user) {

    userService.saveEntry(user);

}
```

### Endpoint

```http
POST /user
```

Accepts a JSON request body and saves a new user.

Example request:

```json
{
    "userName": "shivam",
    "password": "123456"
}
```

---

# Update User

```java
@PutMapping("/{userName}")
public ResponseEntity<?> updateUser(
        @RequestBody User user,
        @PathVariable String userName) {

    User userInDb =
            userService.findByUserName(userName);

    if (userInDb != null) {

        userInDb.setUserName(user.getUserName());
        userInDb.setPassword(user.getPassword());

        userService.saveEntry(userInDb);
    }

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);

}
```

### Endpoint

```http
PUT /user/{userName}
```

Example:

```http
PUT /user/shivam
```

The controller:

1. Finds the existing user.
2. Updates the fields.
3. Saves the updated document.

---

# CRUD Flow

```text
Create User
      │
      ▼
POST /user
      │
      ▼
UserController
      │
      ▼
UserService
      │
      ▼
UserRepository.save()
      │
      ▼
MongoDB
```

The same flow is followed for read, update, and delete operations.

---

# Summary

| Component                 | Responsibility                                                     |
| ------------------------- | ------------------------------------------------------------------ |
| `User`                    | Represents a MongoDB document in the `users` collection            |
| `@Document`               | Maps the class to a MongoDB collection                             |
| `@Id`                     | Maps the primary identifier to MongoDB's `_id` field               |
| `@Indexed(unique = true)` | Creates a unique index on the field                                |
| `@NonNull`                | Prevents `null` values in Lombok-generated constructors            |
| `@DBRef`                  | Stores references to documents in another collection               |
| `UserRepository`          | Handles database operations                                        |
| `UserService`             | Contains business logic                                            |
| `UserController`          | Handles HTTP requests and responses                                |
| `findByUserName()`        | Custom query method generated automatically by Spring Data MongoDB |
