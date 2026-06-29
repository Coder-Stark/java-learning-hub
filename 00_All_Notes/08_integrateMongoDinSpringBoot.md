# Connecting Spring Boot with MongoDB

To enable MongoDB support in a Spring Boot application, two main steps are required:

1. Add the MongoDB dependency.
2. Configure the MongoDB connection.

Once these steps are completed, Spring Boot can communicate with the MongoDB database.

---

# Step 1: Add the MongoDB Dependency

Spring Boot uses **Spring Data MongoDB** to interact with MongoDB.

Add the following dependency to the `pom.xml` file.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

### What does this dependency provide?

It automatically adds support for:

* MongoDB Java Driver
* Spring Data MongoDB
* Repository support (`MongoRepository`)
* Object-Document Mapping (ODM)
* Automatic configuration for MongoDB

Since the project uses **Maven**, all required libraries are downloaded automatically.

---

# Maven Automatically Downloads Dependencies

After adding the dependency, Maven downloads all required JAR files.

You can trigger the download by:

* Reloading the Maven project in IntelliJ.
* Running:

```bash
mvn clean install
```

or

```bash
mvn compile
```

No manual installation of MongoDB libraries is required.

---

# Step 2: Configure the MongoDB Connection

Spring Boot reads database configuration from:

```text
src/main/resources/application.properties
```

---

# Local MongoDB Configuration

If MongoDB is installed on your local machine, configure it as follows:

```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=journal
```

### Property Explanation

| Property                       | Description                            |
| ------------------------------ | -------------------------------------- |
| `spring.data.mongodb.host`     | MongoDB server hostname                |
| `spring.data.mongodb.port`     | MongoDB server port (default: `27017`) |
| `spring.data.mongodb.database` | Database name to connect to            |

---

# Connection URI (Recommended)

Instead of specifying the host, port, and database separately, you can provide a single MongoDB connection URI.

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/journal
```

This is equivalent to:

```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=journal
```

Using the URI format is generally recommended because it is simpler and scales better for more advanced configurations.

---

# Cloud MongoDB Configuration

When using a cloud-hosted MongoDB service (such as MongoDB Atlas), you'll receive a connection string.

Example:

```properties
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@cluster0.xxxxxx.mongodb.net/journal
```

Replace:

* `<username>` → MongoDB username
* `<password>` → MongoDB password
* `cluster0.xxxxxx.mongodb.net` → Your cluster address
* `journal` → Database name

Spring Boot automatically connects using this URI.

---

# Connection URI Format

### Local MongoDB

```text
mongodb://<host>:<port>/<database>
```

Example:

```text
mongodb://localhost:27017/journal
```

---

### Cloud MongoDB (Atlas)

```text
mongodb+srv://<username>:<password>@<cluster-address>/<database>
```

Example:

```text
mongodb+srv://shivam:password123@cluster0.mongodb.net/journal
```

---

# How Spring Boot Connects to MongoDB

```text
Spring Boot Application
           │
           ▼
application.properties
           │
           ▼
Spring Data MongoDB
           │
           ▼
MongoDB Java Driver
           │
           ▼
MongoDB Server
```

When the application starts:

1. Spring Boot reads the MongoDB configuration.
2. Creates a MongoDB client.
3. Establishes a connection to the database.
4. Makes the connection available throughout the application.

---

# Local vs Cloud Configuration

| Local MongoDB                   | Cloud MongoDB                               |
| ------------------------------- | ------------------------------------------- |
| Runs on your own computer       | Runs on a remote server                     |
| `localhost` is used as the host | Uses a cluster URL                          |
| No internet required            | Internet connection required                |
| Ideal for development           | Ideal for production and team collaboration |

---

# Common Configuration Properties

| Property                       | Purpose                            |
| ------------------------------ | ---------------------------------- |
| `spring.data.mongodb.host`     | MongoDB host name                  |
| `spring.data.mongodb.port`     | MongoDB port number                |
| `spring.data.mongodb.database` | Database name                      |
| `spring.data.mongodb.uri`      | Complete MongoDB connection string |

---

# Important Notes

* The correct property prefix is **`spring.data.mongodb`**, **not** `spring.mongodb`.
* Use **either** the individual properties (`host`, `port`, `database`) **or** the `spring.data.mongodb.uri` property. The URI is generally preferred because it keeps the configuration concise and supports authentication and cloud deployments.
* The MongoDB server must be running before the Spring Boot application starts; otherwise, the application will fail to connect.
