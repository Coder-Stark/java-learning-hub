# Lombok in Spring Boot

**Lombok** is a popular Java library that reduces boilerplate code by automatically generating common methods during compilation.

Instead of manually writing methods like:

* Getters
* Setters
* Constructors
* `toString()`
* `equals()`
* `hashCode()`

you simply add annotations, and Lombok generates the code automatically.

Lombok is widely used in **Spring Boot** applications to make Java classes cleaner and easier to maintain.

---

# Why Do We Need Lombok?

Consider the following Java class without Lombok.

```java id="wnqv3z"
public class JournalEntry {

    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
```

Although the class only has two fields, we already need to write multiple methods.

As the number of fields grows, the amount of boilerplate code increases significantly.

---

# Using Lombok

With Lombok, the same class becomes:

```java id="c8ks9x"
@Data
public class JournalEntry {

    private String title;
    private String content;

}
```

Lombok automatically generates all the required methods.

The class becomes much shorter and easier to read.

---

# How Does Lombok Work?

Lombok works during the **Java compilation process**.

It reads the annotations in your source code and generates the required methods before the `.class` files are created.

```text id="onwqzc"
JournalEntry.java
        │
        ▼
Java Compiler
        │
        ▼
Lombok Processes Annotations
        │
        ▼
Generates Required Methods
        │
        ▼
JournalEntry.class
```

The generated methods become part of the compiled bytecode.

This means there is **no runtime performance overhead**.

---

# Compilation Flow

```text id="kgstoa"
Java Source Code
        │
        ▼
Lombok Annotation Processor
        │
        ▼
Generate Getters
Generate Setters
Generate Constructors
Generate toString()
Generate equals()
Generate hashCode()
        │
        ▼
Java Compiler
        │
        ▼
Compiled .class File
```

When the application runs, these generated methods behave exactly like methods you wrote manually.

---

# Common Lombok Annotations

| Annotation                 | Purpose                                     |
| -------------------------- | ------------------------------------------- |
| `@Getter`                  | Generates getter methods                    |
| `@Setter`                  | Generates setter methods                    |
| `@ToString`                | Generates the `toString()` method           |
| `@EqualsAndHashCode`       | Generates `equals()` and `hashCode()`       |
| `@NoArgsConstructor`       | Generates a no-argument constructor         |
| `@AllArgsConstructor`      | Generates a constructor with all fields     |
| `@RequiredArgsConstructor` | Generates a constructor for required fields |
| `@Data`                    | Combines multiple commonly used annotations |

---

# `@Data`

`@Data` is one of the most commonly used Lombok annotations.

```java id="hdvq1v"
@Data
```

It is equivalent to writing:

```java id="k15vb2"
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
```

Instead of adding five annotations, you only need one.

---

# What Does `@Data` Generate?

Suppose you have the following class.

```java id="e3wkoc"
@Data
public class User {

    private String name;
    private int age;

}
```

Lombok automatically generates methods similar to:

```java id="w70h9s"
public String getName()

public void setName(String name)

public int getAge()

public void setAge(int age)

public String toString()

public boolean equals(Object obj)

public int hashCode()
```

These methods are added to the compiled `.class` file automatically.

---

# JournalEntry Entity Example

Your entity class:

```java id="0n8nr8"
@Document(collection = "journal_entries")
@Data
public class JournalEntry {

    @Id
    private ObjectId id;

    private String title;

    private String content;

    private LocalDateTime date;

}
```

---

# Understanding the Annotations

## `@Document`

```java id="xmnfhd"
@Document(collection = "journal_entries")
```

Marks this class as a MongoDB document.

MongoDB stores objects of this class inside the `journal_entries` collection.

Equivalent MongoDB collection:

```text id="mkk49e"
journal_entries
```

---

## `@Id`

```java id="hk0xjm"
@Id
private ObjectId id;
```

Marks the primary identifier for the MongoDB document.

MongoDB automatically generates an `ObjectId` if one is not provided.

Example document:

```json id="3vfl5u"
{
    "_id": ObjectId("685e8ab123456789abcd1234"),
    "title": "Learning Spring Boot",
    "content": "Lombok is useful.",
    "date": "2026-06-29T10:30:00"
}
```

The Java field:

```java id="n9s62w"
private ObjectId id;
```

is mapped to MongoDB's:

```text id="lqjqww"
_id
```

field.

---

# Without Lombok

Without Lombok, your entity would look like this:

```java id="7x79pb"
@Document(collection = "journal_entries")
public class JournalEntry {

    @Id
    private ObjectId id;

    private String title;
    private String content;
    private LocalDateTime date;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // More getters, setters,
    // toString(), equals(), hashCode()...
}
```

Even for a small class, a large amount of repetitive code is required.

---

# With Lombok

The same entity becomes:

```java id="eeb2jw"
@Document(collection = "journal_entries")
@Data
public class JournalEntry {

    @Id
    private ObjectId id;

    private String title;
    private String content;
    private LocalDateTime date;

}
```

The class is much cleaner while providing the same functionality.

---

# Before vs After Lombok

| Without Lombok              | With Lombok              |
| --------------------------- | ------------------------ |
| Manually write getters      | Automatically generated  |
| Manually write setters      | Automatically generated  |
| Manually write `toString()` | Automatically generated  |
| Manually write `equals()`   | Automatically generated  |
| Manually write `hashCode()` | Automatically generated  |
| More boilerplate code       | Cleaner and shorter code |

---

# Important Notes

* Lombok generates code **during compilation**, not at runtime.
* The generated methods become part of the compiled `.class` files.
* There is **no runtime performance penalty** for using Lombok.
* IDEs such as IntelliJ IDEA require the **Lombok plugin** and **Annotation Processing** to be enabled so that generated methods are recognized during development.

---

# Summary

| Annotation / Concept       | Purpose                                                                                          |
| -------------------------- | ------------------------------------------------------------------------------------------------ |
| `@Getter`                  | Generates getter methods                                                                         |
| `@Setter`                  | Generates setter methods                                                                         |
| `@ToString`                | Generates the `toString()` method                                                                |
| `@EqualsAndHashCode`       | Generates `equals()` and `hashCode()`                                                            |
| `@RequiredArgsConstructor` | Generates a constructor for required fields                                                      |
| `@Data`                    | Combines `@Getter`, `@Setter`, `@RequiredArgsConstructor`, `@ToString`, and `@EqualsAndHashCode` |
| `@Document`                | Maps the Java class to a MongoDB collection                                                      |
| `@Id`                      | Maps the field to MongoDB's `_id` field                                                          |
