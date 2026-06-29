# MongoDB Shell Commands

The MongoDB Shell (`mongosh`) provides commands to interact directly with MongoDB databases and collections.

This document covers the most commonly used commands for viewing databases, managing collections, and performing basic CRUD operations.

---

# MongoDB Hierarchy

Before learning the commands, it's important to understand MongoDB's hierarchy.

```text
MongoDB Server
│
├── Database 1
│   ├── Collection A
│   │   ├── Document 1
│   │   ├── Document 2
│   │   └── Document 3
│   │
│   └── Collection B
│
└── Database 2
    ├── Collection X
    └── Collection Y
```

### Terminology

| MongoDB    | Similar to SQL |
| ---------- | -------------- |
| Database   | Database       |
| Collection | Table          |
| Document   | Row / Record   |
| Field      | Column         |

---

# View All Databases

Displays all databases available on the MongoDB server.

### Command

```javascript
show dbs
```

### Example Output

```text
admin
config
journaldb
local
```

> **Note:** Newly created databases may not appear until they contain at least one collection with data.

---

# View Current Database

Displays the database currently in use.

### Command

```javascript
db
```

### Example Output

```text
journaldb
```

---

# View All Collections

Displays all collections inside the current database.

### Command

```javascript
show collections
```

### Example Output

```text
journalEntries
users
products
```

> **Note:** The correct MongoDB command is **`show collections`**, not `show collection`.

---

# Insert a Document

Adds a single document to a collection.

### Syntax

```javascript
db.<collection_name>.insertOne({
    field1: value1,
    field2: value2
})
```

### Example

```javascript
db.users.insertOne({
    name: "Shivam",
    age: 24
})
```

### Result

A new document is inserted into the `users` collection.

```json
{
    "_id": ObjectId("..."),
    "name": "Shivam",
    "age": 24
}
```

> **Note:** If the collection does not already exist, MongoDB automatically creates it.

---

# Retrieve All Documents

Returns all documents from a collection.

### Syntax

```javascript
db.<collection_name>.find()
```

### Example

```javascript
db.users.find()
```

### Example Output

```json
{ "_id": ..., "name": "Shivam", "age": 24 }
{ "_id": ..., "name": "Rahul", "age": 28 }
```

---

# Display Documents in Readable Format

Formats the output for better readability.

### Command

```javascript
db.<collection_name>.find().pretty()
```

### Example

```javascript
db.users.find().pretty()
```

### Example Output

```json
{
    "_id": ObjectId("..."),
    "name": "Shivam",
    "age": 24
}

{
    "_id": ObjectId("..."),
    "name": "Rahul",
    "age": 28
}
```

> **Note:** In modern versions of `mongosh`, the output is already formatted nicely, so `.pretty()` is generally unnecessary.

---

# Delete a Single Document

Deletes the first document that matches the specified condition.

### Syntax

```javascript
db.<collection_name>.deleteOne({
    field: value
})
```

### Example

```javascript
db.users.deleteOne({
    name: "Shivam"
})
```

Only the first matching document is removed.

---

# Delete Multiple Documents

Deletes all documents that match the specified condition.

### Syntax

```javascript
db.<collection_name>.deleteMany({
    field: value
})
```

### Example

```javascript
db.users.deleteMany({
    age: 24
})
```

All users with `age = 24` are deleted.

---

# Delete Every Document in a Collection

Deletes all documents while keeping the collection itself.

### Command

```javascript
db.<collection_name>.deleteMany({})
```

### Example

```javascript
db.users.deleteMany({})
```

After execution:

```text
users Collection
└── Empty
```

> **Note:** The collection still exists; only its documents are removed.

---

# CRUD Operations in MongoDB

| Operation | Command                       |
| --------- | ----------------------------- |
| Create    | `insertOne()`                 |
| Read      | `find()`                      |
| Update    | `updateOne()`, `updateMany()` |
| Delete    | `deleteOne()`, `deleteMany()` |

---

# Common MongoDB Shell Commands

| Command                      | Description                                     |
| ---------------------------- | ----------------------------------------------- |
| `show dbs`                   | Display all databases                           |
| `db`                         | Show the current database                       |
| `show collections`           | Display all collections in the current database |
| `db.users.insertOne({...})`  | Insert a single document                        |
| `db.users.find()`            | Retrieve all documents                          |
| `db.users.find().pretty()`   | Display documents in a formatted way            |
| `db.users.deleteOne({...})`  | Delete the first matching document              |
| `db.users.deleteMany({...})` | Delete all matching documents                   |
| `db.users.deleteMany({})`    | Delete every document in the collection         |

---

# Example Workflow

```javascript
// Check available databases
show dbs

// View current database
db

// View collections
show collections

// Insert a document
db.users.insertOne({
    name: "Shivam",
    age: 24
})

// Retrieve all documents
db.users.find()

// Display formatted output
db.users.find().pretty()

// Delete one document
db.users.deleteOne({
    name: "Shivam"
})

// Delete all documents
db.users.deleteMany({})
```
