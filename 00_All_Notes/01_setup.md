# Java + Spring Boot Development Setup Guide (Windows + IntelliJ IDEA)

This guide provides a complete step-by-step process for setting up a Java and Spring Boot development environment on a fresh Windows installation using IntelliJ IDEA. It includes only the verified working steps and avoids unnecessary troubleshooting.

---

# Prerequisites

Before you begin, ensure you have:

* Windows 10 or Windows 11
* Internet connection
* Administrator privileges (recommended)

---

# Step 1: Install Java (JDK 17 LTS)

## Download JDK

Download **Eclipse Temurin JDK 17 (LTS)** from:

https://adoptium.net/

Select the following options:

| Option           | Value    |
| ---------------- | -------- |
| Operating System | Windows  |
| Architecture     | x64      |
| Package Type     | JDK      |
| Version          | 17 (LTS) |

---

## Install JDK

Install using the default settings.

Example installation directory:

```text
C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot
```

---

## Verify Installation

Open **Command Prompt** and run:

```bash
java -version
```

Expected output:

```text
openjdk version "17.x.x"
```

Next, verify the Java compiler:

```bash
javac -version
```

Expected output:

```text
javac 17.x.x
```

---

# Step 2: Install IntelliJ IDEA

Download IntelliJ IDEA from:

https://www.jetbrains.com/idea/download/

Install the latest version of either:

* IntelliJ IDEA Ultimate (recommended)
* IntelliJ IDEA Community Edition

Launch IntelliJ IDEA after installation.

---

# Step 3: Configure the JDK in IntelliJ

Navigate to:

```text
File
→ Project Structure
→ SDKs
```

If no JDK is configured:

1. Click **Add SDK**
2. Select the installed JDK directory, for example:

```text
C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot
```

Click **OK** to save.

---

# Step 4: Create a Spring Boot Project

> **Important:** Do **not** create a standard Java project. Use the Spring Boot project generator.

Navigate to:

```text
File
→ New
→ Project
→ Generators
→ Spring Boot
```

Fill in the project details:

| Field         | Value                   |
| ------------- | ----------------------- |
| Server URL    | start.spring.io         |
| Name          | myFirstJava             |
| Language      | Java                    |
| Build Tool    | Maven                   |
| Group         | com.rajbhar             |
| Artifact      | myFirstJava             |
| Package Name  | com.rajbhar.myfirstjava |
| JDK           | 17                      |
| Java Version  | 17                      |
| Packaging     | Jar                     |
| Configuration | YAML or Properties      |

Click **Next**.

---

# Step 5: Add Dependencies

Search for:

```text
Spring Web
```

Select:

* Spring Web

Click **Create** to generate the project.

---

# Step 6: Wait for Maven to Finish

After the project opens, IntelliJ will automatically:

* Import the Maven project
* Download dependencies
* Index the project

Wait until all background tasks have completed before running the application.

Do **not** run the project while Maven is still downloading dependencies.

---

# Step 7: Run the Application

Open:

```text
MyFirstJavaApplication.java
```

Run the application using either method:

* Click the green **▶** icon next to the `main()` method.
* Right-click the file and select:

```text
Run 'MyFirstJavaApplication'
```

---

# Verify the Application

If everything is configured correctly, IntelliJ should display a message similar to:

```text
Tomcat started on port(s): 8080
```

or

```text
Tomcat started on port(s): 8081
```

Open your browser and navigate to:

```text
http://localhost:8080
```

or

```text
http://localhost:8081
```

---

# Useful Commands

## Check Java Version

```bash
java -version
```

## Check Java Compiler

```bash
javac -version
```

## Check Maven Installation

```bash
mvn -version
```

## Build Using Maven Wrapper

```bash
.\mvnw clean install
```

---

# Recommended Software Versions

| Software      | Recommended Version                  |
| ------------- | ------------------------------------ |
| Java JDK      | 17 LTS                               |
| IntelliJ IDEA | Latest Ultimate or Community Edition |
| Spring Boot   | Latest Stable Version                |
| Build Tool    | Maven                                |

---

# Expected Project Structure

```text
myFirstJava
│
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .mvn
│
└── src
    └── main
        ├── java
        │   └── com
        │       └── rajbhar
        │           └── myfirstjava
        │               └── MyFirstJavaApplication.java
        │
        └── resources
            ├── application.properties
            └── application.yml
```

---

# Common Mistakes to Avoid

❌ Creating or importing the project as a standard Java project instead of a Spring Boot project.

❌ Running the application before Maven has finished downloading all dependencies.

❌ Assuming missing `org.springframework.boot` packages indicate a coding issue; in most cases, Maven dependency resolution has not completed.

❌ Treating the "Port 8080 was already in use" error as a Spring Boot issue. This simply means another application is already using port **8080**.

---

# Conclusion

Following this guide on a fresh Windows installation will provide a fully functional Java and Spring Boot development environment using IntelliJ IDEA.

Once the setup is complete, you can begin building REST APIs, web applications, and other Spring Boot projects with confidence.
