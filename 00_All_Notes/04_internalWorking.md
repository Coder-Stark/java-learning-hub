# Spring Core Concepts: IoC Container, Beans, Components, and `@SpringBootApplication`

Understanding these concepts is essential before building applications with Spring Boot. They form the foundation of how Spring manages objects and dependencies.

---

# What is Spring?

**Spring** is a Java framework that simplifies enterprise application development.

Instead of manually creating and managing objects, Spring manages them for you using the **IoC (Inversion of Control) Container**.

---

# IoC (Inversion of Control)

## Definition

**IoC (Inversion of Control)** is a design principle where the responsibility of creating, configuring, and managing objects is transferred from the programmer to the Spring Framework.

Instead of writing:

```java
Student student = new Student();
```

Spring automatically creates and manages the object.

---

## Think of IoC as a Box

Imagine IoC as a large storage box.

```
                IoC Container
        +-------------------------+
        |                         |
        |   Student Object        |
        |   Teacher Object        |
        |   UserService Object    |
        |   ProductService Object |
        |   UserRepository Object |
        |                         |
        +-------------------------+
```

Every object managed by Spring is stored inside this container.

Whenever your application needs one of these objects, Spring provides it automatically.

---

# What is an IoC Container?

The **IoC Container** is the core component of Spring responsible for:

* Creating objects
* Configuring objects
* Managing the lifecycle of objects
* Injecting dependencies between objects

Instead of the developer manually creating objects using `new`, Spring creates and manages them.

---

# ApplicationContext

`ApplicationContext` is the most commonly used implementation of the **IoC Container**.

It provides advanced features such as:

* Bean management
* Dependency Injection
* Event handling
* Property loading
* Resource management

In Spring Boot, the `ApplicationContext` is created automatically when the application starts.

---

# What is a Bean?

A **Bean** is simply an object that is created and managed by the Spring IoC Container.

### Normal Java Object

```java
Student student = new Student();
```

This is **not** a Spring Bean because you created it manually.

---

### Spring Bean

```java
@Component
public class Student {

}
```

Since Spring creates this object, it becomes a **Spring Bean**.

> **Definition:** Every object managed by the Spring IoC Container is called a **Bean**.

---

# `@Component`

`@Component` tells Spring that the class should be managed by the IoC Container.

Example:

```java
@Component
public class MyComponent {

}
```

When the application starts:

1. Spring scans the class.
2. Creates its object.
3. Stores the object inside the IoC Container.
4. Makes it available for Dependency Injection.

---

## Without `@Component`

```java
public class MyComponent {

}
```

Spring completely ignores this class.

No object is created automatically.

---

## With `@Component`

```java
@Component
public class MyComponent {

}
```

Spring automatically creates an object like:

```java
MyComponent myComponent = new MyComponent();
```

and stores it inside the IoC Container.

You never need to create it manually.

---

# Important Note

The IoC Container **does not register every class** in your project.

It only registers classes that Spring recognizes as Beans.

These include classes annotated with stereotypes such as:

* `@Component`
* `@Service`
* `@Repository`
* `@Controller`
* `@RestController`
* `@Configuration`

All of these are ultimately treated as Spring Beans.

---

# `@SpringBootApplication`

Every Spring Boot project contains one main class.

Example:

```java
@SpringBootApplication
public class MyFirstJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyFirstJavaApplication.class, args);
    }

}
```

> **Important:** `@SpringBootApplication` should be added **only once**, on the main application class.

---

# What Does `@SpringBootApplication` Do?

`@SpringBootApplication` is a combination of three annotations.

```java
@SpringBootApplication
=
@Configuration
+
@EnableAutoConfiguration
+
@ComponentScan
```

Instead of writing three annotations separately, Spring Boot provides a single annotation.

---

## 1. `@Configuration`

Marks the class as a source of Spring configuration.

It tells Spring:

> "This class contains configuration information for the application."

Example:

```java
@Configuration
public class AppConfig {

}
```

---

## 2. `@EnableAutoConfiguration`

Automatically configures Spring Boot based on the dependencies available in the project.

Example:

If you add this dependency:

```xml
spring-boot-starter-web
```

Spring Boot automatically configures:

* Embedded Tomcat
* Spring MVC
* Jackson JSON support
* Dispatcher Servlet

You don't need to configure these manually.

This is one of Spring Boot's biggest advantages.

---

## 3. `@ComponentScan`

Automatically scans the project for Spring Beans.

It looks for classes annotated with:

* `@Component`
* `@Service`
* `@Repository`
* `@Controller`
* `@RestController`

and registers them inside the IoC Container.

Without Component Scan, Spring wouldn't know which classes to manage.

---

# Base Package

The package containing the main application class is known as the **Base Package**.

Example:

```java
package com.rajbhar.myfirstjava;
```

```
com.rajbhar.myfirstjava
        │
        ├── controller
        ├── service
        ├── repository
        ├── entity
        ├── config
        └── MyFirstJavaApplication
```

By default, Spring scans:

* The base package
* All of its sub-packages

---

## Example

```
com.rajbhar.myfirstjava
│
├── controller
├── service
├── repository
└── entity
```

All these packages are scanned automatically.

However:

```
com.otherpackage
```

will **not** be scanned because it is outside the base package.

To scan additional packages, you must explicitly configure `@ComponentScan`.

---

# Complete Flow

```
Application Starts
        │
        ▼
@SpringBootApplication
        │
        ▼
@ComponentScan
        │
        ▼
Finds @Component, @Service, @Repository...
        │
        ▼
Creates Objects (Beans)
        │
        ▼
Stores Them in IoC Container
        │
        ▼
ApplicationContext Manages All Beans
```

---

# Interview Questions

### What is IoC?

A design principle where Spring controls the creation and management of objects instead of the developer.

---

### What is an IoC Container?

A container responsible for creating, configuring, storing, and managing Spring Beans.

---

### What is a Bean?

A Bean is an object that is created and managed by the Spring IoC Container.

---

### What is `ApplicationContext`?

`ApplicationContext` is the most commonly used implementation of the Spring IoC Container.

---

### What does `@Component` do?

It tells Spring to create an object of the class and register it as a Bean inside the IoC Container.

---

### Can every class become a Bean?

No. Only classes recognized by Spring (such as those annotated with `@Component`, `@Service`, `@Repository`, `@Controller`, etc.) are automatically registered as Beans.

---

### What are the three annotations included in `@SpringBootApplication`?

* `@Configuration`
* `@EnableAutoConfiguration`
* `@ComponentScan`

---

### What is the Base Package?

The package where the main Spring Boot application class resides. Spring automatically scans this package and all of its sub-packages for Beans.
