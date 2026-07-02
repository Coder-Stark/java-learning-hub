# Spring Security with MongoDB Authentication

Initially, Spring Security used the **default username and generated password** provided by Spring Boot.

After this implementation, authentication is now performed using **user credentials stored in the MongoDB database**.

This means every login request is validated against the application's `users` collection instead of Spring Boot's default in-memory user.

---

# Authentication Flow

The authentication flow is now as follows:

```text
Client
   │
   │ Username + Password
   ▼
Spring Security
   │
   ▼
UserDetailsServiceImpl
   │
   ▼
UserRepository
   │
   ▼
MongoDB
   │
   ▼
User Found ?
   │
 ┌─┴──────────────┐
 │                │
Yes              No
 │                │
 ▼                ▼
Password      UsernameNotFoundException
Verification
 │
 ▼
Authenticated
```

---

# Adding Spring Security

Spring Security support is enabled by adding the following dependency.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

After Maven downloads this dependency, Spring Boot automatically secures every endpoint.

Without any configuration:

* Every endpoint requires authentication.
* Spring Boot creates a default user.
* A random password is printed in the console.

---

# Why Custom Authentication?

The default user created by Spring Boot is only suitable for development.

In a real application:

* Users are stored in a database.
* Passwords are stored in encrypted form.
* Authentication should verify database credentials.

Our project now authenticates users from MongoDB.

```text
Before

Spring Security
        │
        ▼
Default User
(username/password)


After

Spring Security
        │
        ▼
MongoDB Users Collection
```

---

# Project Architecture

```text
Client
   │
Basic Authentication
   │
   ▼
Spring Security Filter Chain
   │
   ▼
DaoAuthenticationProvider
   │
   ▼
UserDetailsServiceImpl
   │
   ▼
UserRepository
   │
   ▼
MongoDB
```

---

# Spring Security Configuration

The application's security configuration is placed inside:

```text
config/
└── SpringSecurity.java
```

```java
@Configuration
public class SpringSecurity {

}
```

---

# `@Configuration`

```java
@Configuration
```

Marks the class as a Spring Configuration class.

Spring scans this class and registers every method annotated with `@Bean`.

---

# `SecurityFilterChain`

```java
@Bean
public SecurityFilterChain securityFilterChain(
        HttpSecurity http)
```

`SecurityFilterChain` is responsible for configuring how every incoming HTTP request should be processed.

It defines:

* Authentication
* Authorization
* Sessions
* CSRF
* Login mechanisms
* Logout
* Security filters

Every request passes through this filter chain before reaching the controller.

---

# Authorization Rules

```java
.authorizeHttpRequests(auth -> auth
        .requestMatchers("/journal/**", "/user/**")
        .authenticated()
        .anyRequest()
        .permitAll()
)
```

This defines which endpoints require authentication.

---

## Protected Endpoints

```text
/journal/**
/user/**
```

Examples:

```text
/journal
/journal/shivam
/journal/id/123

/user
```

Accessing these endpoints requires valid credentials.

---

## Public Endpoints

```java
.anyRequest().permitAll()
```

Every endpoint not matched previously becomes public.

Examples:

```text
/public/health-check

/public/create-user
```

These endpoints do not require authentication.

---

# HTTP Basic Authentication

```java
.httpBasic(httpBasic -> {})
```

Enables HTTP Basic Authentication.

When a protected endpoint is accessed:

* Browser displays a login popup.
* Postman requests username and password.
* Credentials are sent in the Authorization header.

Example:

```http
Authorization: Basic cmFtOnJhbQ==
```

Where

```text
ram:ram
```

is Base64 encoded.

---

# Stateless Sessions

```java
.sessionManagement(session ->
        session.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS
        )
)
```

The application is configured as **stateless**.

This means:

* No HTTP Session is created.
* Every request must send credentials.
* The server does not remember previous requests.

```text
Request 1

Username + Password
        │
        ▼
Authenticated


Request 2

Username + Password
        │
        ▼
Authenticated
```

Every request is authenticated independently.

This approach is commonly used for REST APIs.

---

# Disabling CSRF

```java
.csrf(csrf -> csrf.disable())
```

Initially the application returned **403 Forbidden** for POST, PUT and DELETE requests.

The issue was caused by CSRF protection.

Since this project is a REST API using HTTP Basic Authentication and not browser-based form login, CSRF protection is disabled.

Without disabling CSRF:

```text
POST
PUT
DELETE

↓

403 Forbidden
```

After disabling CSRF:

```text
POST
PUT
DELETE

↓

Request Processed Successfully
```

---

# Why Was CSRF Disabled?

CSRF protection is mainly required for browser applications that use cookies for authentication.

This project uses:

* HTTP Basic Authentication
* Stateless sessions
* REST APIs

Therefore disabling CSRF is appropriate in this context.

---

# Authentication Provider

```java
@Bean
public DaoAuthenticationProvider
authenticationProvider()
```

Spring Security delegates authentication to a `DaoAuthenticationProvider`.

Responsibilities:

* Load user details
* Verify passwords
* Authenticate users

Flow:

```text
Username + Password
        │
        ▼
DaoAuthenticationProvider
        │
        ▼
UserDetailsService
        │
        ▼
Database
```

---

# Password Encoder

```java
@Bean
public PasswordEncoder passwordEncoder() {

    return new BCryptPasswordEncoder();

}
```

Passwords should never be stored as plain text.

Instead, they are encrypted before being saved.

Example:

```text
Input Password

ram123
```

Stored in MongoDB:

```text
$2a$10$P2O....
```

Even if someone gains database access, the original password cannot be read.

---

# BCrypt Password Encoding

During user registration:

```java
user.setPassword(
        passwordEncoder.encode(
                user.getPassword()
        )
);
```

Example:

```text
Original Password

ram
        │
        ▼
BCrypt
        │
        ▼
$2a$10$L6m...
```

Only the encrypted password is stored.

---

# UserDetailsService

Spring Security does not know how users are stored.

It relies on an implementation of:

```java
UserDetailsService
```

Our implementation:

```text
UserDetailsServiceImpl
```

---

# `loadUserByUsername()`

```java
@Override
public UserDetails
loadUserByUsername(String username)
```

Whenever a user logs in, Spring automatically calls this method.

Flow:

```text
Login Request
       │
       ▼
loadUserByUsername()
       │
       ▼
UserRepository
       │
       ▼
MongoDB
```

---

# Building a Spring Security User

```java
return User.builder()
        .username(user.getUserName())
        .password(user.getPassword())
        .roles(...)
        .build();
```

This converts our application's `User` object into Spring Security's `UserDetails` object.

Spring Security then uses it for authentication.

---

# Roles

A new field has been added to the `User` entity.

```java
private List<String> roles;
```

Currently every new user receives:

```java
user.setRoles(Arrays.asList("USER"));
```

Example MongoDB document:

```json
{
    "userName": "ram",
    "password": "$2a$10$...",
    "roles": [
        "USER"
    ]
}
```

These roles can later be used for role-based authorization.

Examples:

```text
USER

ADMIN

MODERATOR
```

---

# Updated User Registration

User creation has been moved to a public controller.

Endpoint:

```http
POST /public/create-user
```

Example request:

```json
{
    "userName": "ram",
    "password": "ram"
}
```

During registration:

1. Password is encrypted.
2. Default role `"USER"` is assigned.
3. User is saved in MongoDB.

---

# Why Separate Public and Protected Controllers?

The application now separates public APIs from authenticated APIs.

```text
PublicController

/public/**
```

Accessible without login.

Examples:

* Health Check
* User Registration

---

```text
UserController

/user/**
```

Accessible only after authentication.

Examples:

* Update Profile
* Delete User

---

# Getting the Logged-in User

Instead of sending the username in the URL, Spring Security provides the authenticated user's information.

```java
Authentication authentication =
        SecurityContextHolder
                .getContext()
                .getAuthentication();
```

Current username:

```java
authentication.getName();
```

Example:

```text
Logged-in User

↓

ram
```

This prevents users from modifying another user's account simply by changing the URL.

---

# Updated User Endpoints

## Create User

Public endpoint.

```http
POST /public/create-user
```

Authentication required?

```text
No
```

---

## Update User

Protected endpoint.

```http
PUT /user
```

Authentication required?

```text
Yes
```

The logged-in user is obtained from the Security Context.

---

## Delete User

Protected endpoint.

```http
DELETE /user
```

Authentication required?

```text
Yes
```

The authenticated user's account is deleted.

---

# Complete Authentication Flow

```text
Client
   │
Username + Password
   │
   ▼
Spring Security Filter Chain
   │
   ▼
DaoAuthenticationProvider
   │
   ▼
UserDetailsServiceImpl
   │
   ▼
UserRepository
   │
   ▼
MongoDB
   │
User Found ?
   │
 ┌─┴───────────────┐
 │                 │
Yes               No
 │                 │
 ▼                 ▼
Verify Password   Authentication Failed
 │
 ▼
Authentication Success
 │
 ▼
Controller Executes
```

---

# Summary

| Component                         | Responsibility                                                      |
| --------------------------------- | ------------------------------------------------------------------- |
| `spring-boot-starter-security`    | Adds Spring Security support                                        |
| `SpringSecurity`                  | Central security configuration                                      |
| `SecurityFilterChain`             | Defines authentication and authorization rules                      |
| `DaoAuthenticationProvider`       | Authenticates users using database credentials                      |
| `UserDetailsServiceImpl`          | Loads user details from MongoDB                                     |
| `PasswordEncoder`                 | Encrypts passwords using BCrypt                                     |
| `BCryptPasswordEncoder`           | Hashes passwords before storage                                     |
| `SecurityContextHolder`           | Provides details of the currently authenticated user                |
| `roles`                           | Stores user roles such as `USER` and `ADMIN`                        |
| `SessionCreationPolicy.STATELESS` | Disables server-side HTTP sessions                                  |
| `csrf().disable()`                | Disables CSRF protection for this stateless REST API                |
| `PublicController`                | Exposes public endpoints such as health check and user registration |
| `UserController`                  | Exposes authenticated user management endpoints                     |
