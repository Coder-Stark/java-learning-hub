# Connecting Users and Journal Entries Using `@DBRef`

Initially, the **User** and **JournalEntry** modules worked independently.

```text
User Collection
      │
      └── Users

JournalEntry Collection
      │
      └── Journal Entries
```

There was **no relationship** between them.

This meant:

* Any user could retrieve every journal entry.
* Journal entries were not associated with a specific user.
* There was no ownership of journal entries.

To solve this, we established a relationship between **User** and **JournalEntry**.

---

# Before the Relationship

The application stored users and journal entries separately.

```text
users Collection

┌──────────────────────┐
│ Shivam               │
│ Rahul                │
│ Aman                 │
└──────────────────────┘


journal_entries Collection

┌──────────────────────┐
│ Journal 1            │
│ Journal 2            │
│ Journal 3            │
└──────────────────────┘
```

There was no way to determine which journal belonged to which user.

---

# After the Relationship

A user now stores references to their journal entries.

```text
users Collection

Shivam
│
├── JournalEntry ObjectId(101)
├── JournalEntry ObjectId(102)
└── JournalEntry ObjectId(103)


Rahul
│
├── JournalEntry ObjectId(201)
└── JournalEntry ObjectId(202)
```

The actual journal documents continue to reside in the `journal_entries` collection.

```text
journal_entries Collection

ObjectId(101)
ObjectId(102)
ObjectId(103)
ObjectId(201)
ObjectId(202)
```

---

# Database Relationship

```text
             User
              │
              │ One User
              │
              ▼
      Multiple Journal Entries
```

This represents a **One-to-Many Relationship**.

* One User
* Multiple Journal Entries

---

# `@DBRef`

The relationship is established using:

```java
@DBRef
private List<JournalEntry> journalEntries = new ArrayList<>();
```

Instead of storing complete journal documents inside the user document, MongoDB stores references.

Example:

```json
{
    "_id": "...",
    "userName": "shivam",

    "journalEntries": [
        ObjectId("101"),
        ObjectId("102"),
        ObjectId("103")
    ]
}
```

---

# Updated Request Flow

Earlier:

```text
Client
   │
   ▼
Journal Controller
   │
   ▼
Journal Repository
   │
   ▼
Journal Collection
```

Now:

```text
Client
   │
   ▼
Journal Controller
   │
   ▼
Journal Service
   │
   ├──────────────► User Collection
   │
   └──────────────► Journal Collection
```

The service coordinates updates across both collections.

---

# Updated Journal Controller

The controller now receives a **username** whenever journal operations are performed.

Instead of:

```http
POST /journal
```

the endpoint becomes:

```http
POST /journal/{userName}
```

Example:

```http
POST /journal/shivam
```

Now every journal entry is associated with a specific user.

---

# Get All Journal Entries of a User

```java
@GetMapping("/{userName}")
```

### Endpoint

```http
GET /journal/shivam
```

---

## Flow

```text
Client
      │
GET /journal/shivam
      │
      ▼
Controller
      │
      ▼
Find User
      │
      ▼
Return user's journalEntries list
```

Implementation:

```java
User user = userService.findByUserName(userName);

List<JournalEntry> all =
        user.getJournalEntries();
```

Instead of querying the `journal_entries` collection directly, the journals are obtained from the user's reference list.

---

# Create a Journal Entry

```java
@PostMapping("/{userName}")
```

### Endpoint

```http
POST /journal/shivam
```

The username identifies the owner of the new journal entry.

---

# Save Flow

The following method performs the complete save operation.

```java
public void saveEntry(
        JournalEntry journalEntry,
        String userName)
```

---

## Step 1

Find the user.

```java
User user =
        userService.findByUserName(userName);
```

---

## Step 2

Set the creation date.

```java
journalEntry.setDate(LocalDateTime.now());
```

---

## Step 3

Save the journal entry.

```java
JournalEntry saved =
        journalEntryRepository.save(journalEntry);
```

MongoDB stores the document inside:

```text
journal_entries
```

---

## Step 4

Add the saved journal to the user's list.

```java
user.getJournalEntries().add(saved);
```

---

## Step 5

Save the updated user.

```java
userService.saveEntry(user);
```

Now MongoDB updates the user's references.

---

# Complete Save Flow

```text
POST /journal/shivam
        │
        ▼
Controller
        │
        ▼
JournalEntryService
        │
        ▼
Find User
        │
        ▼
Save JournalEntry
        │
        ▼
Add JournalEntry Reference
to User
        │
        ▼
Update User
```

Both collections remain synchronized.

---

# Delete a Journal Entry

Updated endpoint:

```http
DELETE /journal/id/shivam/{journalId}
```

---

## Delete Flow

Deleting a journal now requires two operations.

### Step 1

Find the user.

```java
User user =
        userService.findByUserName(userName);
```

---

### Step 2

Remove the journal reference.

```java
user.getJournalEntries()
        .removeIf(
            x -> x.getId().equals(id)
        );
```

The `removeIf()` method removes every element that satisfies the condition.

Equivalent logic:

```text
For each JournalEntry

If JournalEntry.id == requested id

Remove it
```

---

### Step 3

Save the updated user.

```java
userService.saveEntry(user);
```

---

### Step 4

Delete the journal document.

```java
journalEntryRepository.deleteById(id);
```

---

# Complete Delete Flow

```text
Delete Request
       │
       ▼
Find User
       │
       ▼
Remove Journal Reference
       │
       ▼
Update User
       │
       ▼
Delete Journal Document
```

---

# Update Journal Entry

The update endpoint also includes the username.

```http
PUT /journal/id/shivam/{journalId}
```

The controller:

1. Finds the journal entry.
2. Updates the modified fields.
3. Saves the updated journal.

```java
oldEntry.setTitle(...);

oldEntry.setContent(...);

journalEntryService.saveEntry(oldEntry);
```

Since only the journal document changes, the user document does not require any updates.

---

# Why Are Two Collections Updated?

When a journal is created:

```text
journal_entries Collection
        ▲
        │
Save Journal
        │
        ▼
users Collection
```

When a journal is deleted:

```text
users Collection
        ▲
Remove Reference
        │
        ▼
journal_entries Collection
```

Keeping both collections synchronized prevents invalid references.

---

# Updated API Endpoints

| Method | Endpoint                             | Description                                 |
| ------ | ------------------------------------ | ------------------------------------------- |
| GET    | `/journal/{userName}`                | Get all journal entries belonging to a user |
| POST   | `/journal/{userName}`                | Create a journal entry for a user           |
| GET    | `/journal/id/{journalId}`            | Get a journal entry by its ID               |
| PUT    | `/journal/id/{userName}/{journalId}` | Update a user's journal entry               |
| DELETE | `/journal/id/{userName}/{journalId}` | Delete a user's journal entry               |

---

# Role of Each Layer

| Layer                        | Responsibility                                                                                   |
| ---------------------------- | ------------------------------------------------------------------------------------------------ |
| **JournalEntryControllerV2** | Receives HTTP requests and delegates work to the service layer.                                  |
| **JournalEntryService**      | Coordinates business logic and keeps the `users` and `journal_entries` collections synchronized. |
| **UserService**              | Retrieves and updates user information.                                                          |
| **UserRepository**           | Performs database operations on the `users` collection.                                          |
| **JournalEntryRepository**   | Performs database operations on the `journal_entries` collection.                                |

---

# Overall Architecture

```text
                Client
                   │
                   ▼
      JournalEntryControllerV2
                   │
                   ▼
          JournalEntryService
             │            │
             │            │
             ▼            ▼
        UserService   JournalRepository
             │
             ▼
      UserRepository
             │
             ▼
          MongoDB

Collections

users
│
├── userName
├── password
└── journalEntries
     │
     ├── ObjectId(...)
     ├── ObjectId(...)
     └── ObjectId(...)

journal_entries
│
├── ObjectId(...)
├── ObjectId(...)
└── ObjectId(...)
```
