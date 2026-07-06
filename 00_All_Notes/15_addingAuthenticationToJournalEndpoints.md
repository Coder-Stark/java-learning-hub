# Securing Journal APIs with Authentication

Previously, journal operations required the **username** to be passed in the URL.

Example:

```http
GET    /journal/{userName}
POST   /journal/{userName}
PUT    /journal/id/{userName}/{journalId}
DELETE /journal/id/{userName}/{journalId}
```

This approach had a security problem.

A user could simply change the `userName` in the URL and potentially access or modify another user's journal entries.

Example:

```http
GET /journal/shivam
```

↓

```http
GET /journal/rahul
```

Although Spring Security authenticated the user, the application still trusted the username provided in the URL.

---

# Problem with Username in URL

Assume:

* Logged in user = **shivam**

If the client sends:

```http
GET /journal/rahul
```

the API would try to access Rahul's journals because it relied on the URL parameter.

This is a security vulnerability because the client controls the URL.

---

# New Approach

Instead of trusting the username supplied by the client, the application now retrieves the username from the authenticated session managed by Spring Security.

Flow:

```text
Client
   │
Username + Password
   │
   ▼
Spring Security
   │
Authentication Successful
   │
   ▼
Security Context
   │
   ▼
Controller
   │
Gets Logged-in Username
```

The username is now obtained from Spring Security rather than the request URL.

---

# Getting the Logged-in User

All secured controllers now retrieve the authenticated user using:

```java
Authentication authentication =
        SecurityContextHolder.getContext().getAuthentication();

String userName = authentication.getName();
```

### What happens?

1. User logs in using HTTP Basic Authentication.
2. Spring Security authenticates the user.
3. Authentication information is stored in the Security Context.
4. Controllers read the logged-in username directly from the Security Context.

This guarantees that every operation is performed on the authenticated user's data.

---

# API Changes

The username has been removed from all journal endpoints.

| Before                                      | After                            |
| ------------------------------------------- | -------------------------------- |
| `GET /journal/{userName}`                   | `GET /journal`                   |
| `POST /journal/{userName}`                  | `POST /journal`                  |
| `PUT /journal/id/{userName}/{journalId}`    | `PUT /journal/id/{journalId}`    |
| `DELETE /journal/id/{userName}/{journalId}` | `DELETE /journal/id/{journalId}` |

The client no longer needs to send the username.

Spring Security automatically determines which user is making the request.

---

# Updated Journal Controller

Every journal API now follows the same pattern:

```java
Authentication authentication =
        SecurityContextHolder.getContext().getAuthentication();

String userName = authentication.getName();
```

The retrieved username is then used to perform all journal operations.

---

# Get All Journal Entries

### Before

The username was passed through the URL.

```http
GET /journal/shivam
```

### Now

```http
GET /journal
```

The controller:

1. Retrieves the authenticated username.
2. Finds that user.
3. Returns only that user's journal entries.

Flow:

```text
Client
   │
GET /journal
   │
   ▼
Spring Security
   │
Authenticated User
   │
   ▼
Controller
   │
Find User
   │
   ▼
Return User's Journal Entries
```

---

# Create Journal Entry

### Before

```http
POST /journal/shivam
```

### Now

```http
POST /journal
```

The controller retrieves the logged-in username and passes it to the service.

Updated service call:

```java
journalEntryService.saveEntry(myEntry, userName);
```

The client no longer specifies the owner of the journal entry.

---

# Get Journal Entry by ID

A new ownership check has been added before returning a journal entry.

The controller now verifies whether the requested journal belongs to the authenticated user.

```java
user.getJournalEntries()
        .stream()
        .filter(...)
        .toList();
```

Only if the journal exists in the user's journal list is it fetched from the database.

Flow:

```text
Request
    │
    ▼
Authenticated User
    │
    ▼
Check User's Journal List
    │
 ┌──┴────────────┐
 │               │
Found         Not Found
 │               │
 ▼               ▼
Return         404
Journal
```

This prevents users from accessing journal entries belonging to other users.

---

# Update Journal Entry

### Before

```http
PUT /journal/id/{userName}/{journalId}
```

### Now

```http
PUT /journal/id/{journalId}
```

The update process now follows these steps:

1. Get the authenticated username.
2. Find the logged-in user.
3. Verify that the journal belongs to that user.
4. Update the journal.

If ownership verification fails:

```http
404 Not Found
```

is returned.

---

# Delete Journal Entry

### Before

```http
DELETE /journal/id/{userName}/{journalId}
```

### Now

```http
DELETE /journal/id/{journalId}
```

The service now returns a boolean:

```java
boolean removed =
        journalEntryService.deleteById(
                journalId,
                userName
        );
```

Meaning:

* `true` → Journal deleted successfully.
* `false` → Journal was not found or does not belong to the authenticated user.

This simplifies the controller logic.

---

# Updated Delete Flow

```text
Delete Request
      │
      ▼
Get Logged-in User
      │
      ▼
Verify Ownership
      │
 ┌────┴─────┐
 │          │
Yes        No
 │          │
 ▼          ▼
Delete     Return 404
Journal
```

---

# Improvements in `JournalEntryService`

## Save Operation

The service now saves the updated user using:

```java
userService.saveUser(user);
```

instead of:

```java
userService.saveNewUser(user);
```

### Why?

`saveNewUser()`:

* Encrypts the password.
* Assigns the default role.
* Intended only for user registration.

`saveUser()`:

* Simply saves an existing user.
* Does not modify passwords or roles.

This clearly separates **creating a new user** from **updating an existing user**.

---

## Delete Operation

The delete method now returns a boolean.

```java
public boolean deleteById(...)
```

instead of:

```java
public void deleteById(...)
```

This allows the controller to know whether a journal was actually deleted.

---

## Transaction Support

The delete operation is now also transactional.

```java
@Transactional
```

Both operations execute within the same transaction:

1. Remove the journal reference from the user.
2. Delete the journal document.

If either operation fails, the transaction is rolled back.

This keeps both collections consistent.

---

# User Service Changes

The user service now has two separate save methods.

## `saveNewUser()`

Used only when registering a new user.

Responsibilities:

* Encrypt password.
* Assign default role (`USER`).
* Save user.

---

## `saveUser()`

Used when updating an existing user.

Responsibilities:

* Save the updated user.
* Does not re-encode the password.
* Does not modify roles.

This separation avoids accidentally hashing an already encrypted password during updates.

---

# Public Controller Changes

User registration continues to remain public.

Endpoint:

```http
POST /public/create-user
```

The controller now calls:

```java
userService.saveNewUser(user);
```

instead of the generic save method.

This ensures every newly registered user:

* Receives an encrypted password.
* Gets the default role `USER`.

---

# Overall Authentication Flow

```text
Client
   │
Username + Password
   │
   ▼
Spring Security
   │
Authentication Successful
   │
   ▼
Security Context
   │
   ▼
Journal Controller
   │
Gets Logged-in Username
   │
   ▼
User Service
   │
   ▼
Journal Service
   │
   ▼
MongoDB
```

The client never supplies the username for journal operations.

The authenticated identity always comes from Spring Security.

---

# Benefits of These Changes

* Removed the need to send usernames in journal URLs.
* Prevented users from accessing another user's journals by changing the URL.
* Added ownership verification before viewing, updating, or deleting journal entries.
* Introduced separate methods for creating and updating users, avoiding unnecessary password re-encoding.
* Made journal deletion transactional to maintain consistency between the `users` and `journal_entries` collections.
* Improved the overall security by relying entirely on the authenticated user provided by Spring Security instead of client-supplied data.
