# Maven Build Lifecycle Commands

This document explains the most commonly used Maven commands and when to use each one during a Java or Spring Boot project's development lifecycle.

---

# Maven Build Lifecycle

## 1. Validate the Project

Checks whether the project is correctly configured and verifies that all required information is available.

### Command

```bash
mvn validate
```

### Purpose

* Validates the project structure
* Ensures the `pom.xml` is correct
* Verifies that all necessary configuration is present

---

## 2. Compile the Source Code

Compiles all Java source files into bytecode (`.class` files).

### Command

```bash
mvn compile
```

### Purpose

* Compiles the project's source code
* Stores compiled classes inside the `target/` directory

---

## 3. Run Unit Tests

Executes all unit tests located in the project.

### Command

```bash
mvn test
```

### Purpose

* Runs all test cases
* Reports test results
* Stops the build if any test fails

---

## 4. Package the Project

Packages the compiled application into a distributable format.

### Command

```bash
mvn package
```

### Purpose

* Compiles the project
* Runs tests
* Creates a JAR (or WAR) file inside the `target/` directory

Example output:

```text
target/
└── myFirstJava-0.0.1-SNAPSHOT.jar
```

---

## 5. Run the Packaged Application

After packaging the project, navigate to the `target` directory and execute the generated JAR file.

### Navigate to the Target Directory

```bash
ls
cd target
```

### Run the Application

```bash
java -jar <project-name>.jar
```

Example:

```bash
java -jar myFirstJava-0.0.1-SNAPSHOT.jar
```

---

## 6. Install the Project to the Local Maven Repository

Installs the packaged artifact into your local Maven repository (`.m2`).

### Command

```bash
mvn install
```

### Purpose

* Compiles the project
* Runs tests
* Packages the project
* Copies the generated artifact to the local Maven repository

This allows other local Maven projects to use it as a dependency.

---

## 7. Clean the Project

Removes all files generated during previous Maven builds.

### Command

```bash
mvn clean
```

### Purpose

* Deletes the `target/` directory
* Removes compiled classes and generated JAR/WAR files
* Ensures the next build starts from a clean state

---

# Maven Lifecycle Flow

```text
validate
    ↓
compile
    ↓
test
    ↓
package
    ↓
install
```

> **Note:** `mvn clean` is independent of the build lifecycle and is typically run before a fresh build (e.g., `mvn clean install`).

---

# Commonly Used Commands

| Command                        | Description                                         |
| ------------------------------ | --------------------------------------------------- |
| `mvn validate`                 | Validate project configuration                      |
| `mvn compile`                  | Compile source code                                 |
| `mvn test`                     | Run unit tests                                      |
| `mvn package`                  | Create a JAR/WAR package                            |
| `java -jar <project-name>.jar` | Run the packaged application                        |
| `mvn install`                  | Install the package into the local Maven repository |
| `mvn clean`                    | Delete the `target/` directory and build artifacts  |

---

# Recommended Development Workflow

During development, the following commands are commonly used:

```bash
mvn clean
mvn compile
mvn test
mvn package
java -jar target/<project-name>.jar
```

For a complete build and local installation:

```bash
mvn clean install
```

# Pro Tip : 
## Any Project having pom.xml -> maven project