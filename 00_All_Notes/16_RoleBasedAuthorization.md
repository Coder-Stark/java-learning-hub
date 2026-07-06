# Role-Based Authorization in Spring Security

Previously, every authenticated user had the same level of access.

Once a user successfully logged in, they could access all protected endpoints.

This is suitable for small applications but not for real-world systems where different users require different permissions.

To solve this, **Role-Based Authorization** has been implemented.

Now, access to specific endpoints depends on the user's assigned role.

---

# Authentication vs Authorization

These two concepts are often confused.

| Authentication               | Authorization                                 |
| ---------------------------- | --------------------------------------------- |
| Verifies **who the user is** | Determines **what the user is allowed to do** |
| Checks username and password | Checks user roles and permissions             |
| Happens first                | Happens after successful authentication       |

Example:

```text id="1sd3jx"
Login
 │
 ▼
Authentication
 │
 ▼
User Verified
 │
 ▼
Authorization
 │
 ▼
Can User Access This Resource?
```

---

# Before Role-Based Authorization

Previously, all authenticated users could access the same protected endpoints.

```text id="f2byga"
Authenticated User
        │
        ▼
/journal/**
/user/**
```

Every logged-in user had identical access.

---

# After Role-Based Authorization

Now different users have different permissions.

```text id="zj1obv"
                 User
                  │
      ┌───────────┴───────────┐
      │                       │
      ▼                       ▼
   ROLE_USER             ROLE_ADMIN
      │                       │
      ▼                       ▼
 /journal/**           /admin/**
 /user/**
```

Only administrators can access admin APIs.

---

# Understanding Roles

A role represents a user's level of permission.

Example roles:

```text id="2c2lgx"
USER

ADMIN

MODERATOR

MANAGER
```

A single user may have one or more roles.

Example MongoDB document:

```json id="m67b6k"
{
    "userName": "shivam",
    "roles": [
        "USER",
        "ADMIN"
    ]
}
```

---

# Spring Security Authorization Rules

Authorization rules are configured inside the `SecurityFilterChain`.

Updated configuration:

```java id="cxgajk"
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/journal/**", "/user/**")
        .authenticated()

    .requestMatchers("/admin/**")
        .hasRole("ADMIN")

    .anyRequest()
        .permitAll()
)
```

---

# Endpoint Access

The application now has three types of endpoints.

## Public Endpoints

```text id="bt19ho"
/public/**
```

Authentication required?

```text id="nbyy6u"
No
```

Anyone can access these APIs.

Examples:

* Create User
* Health Check

---

## Authenticated Endpoints

```text id="xvqgpa"
/journal/**
/user/**
```

Authentication required?

```text id="3mj3lf"
Yes
```

Any authenticated user can access these endpoints.

---

## Admin Endpoints

```text id="5rwg4t"
/admin/**
```

Authentication required?

```text id="rqjapm"
Yes
```

Role required?

```text id="qtcn65"
ADMIN
```

Only users with the **ADMIN** role can access these endpoints.

---

# `hasRole("ADMIN")`

```java id="pocgbo"
.requestMatchers("/admin/**")
.hasRole("ADMIN")
```

This tells Spring Security:

> Allow access only if the authenticated user has the **ADMIN** role.

Flow:

```text id="w9mtb5"
Request
   │
   ▼
Authenticated?
   │
 ┌─┴────────┐
 │          │
No         Yes
 │          │
 ▼          ▼
401     Has ADMIN Role?
              │
          ┌───┴────┐
          │        │
         No       Yes
          │        │
          ▼        ▼
         403    Access Granted
```

---

# Role Naming in Spring Security

In the database, roles are stored without the `ROLE_` prefix.

Example:

```json id="4kq8dt"
{
    "roles": [
        "USER",
        "ADMIN"
    ]
}
```

When using:

```java id="8n4hyh"
.hasRole("ADMIN")
```

Spring Security automatically checks for:

```text id="tlyvcw"
ROLE_ADMIN
```

internally.

Therefore, you only specify:

```java id="rq0u0m"
.hasRole("ADMIN")
```

instead of:

```java id="ee95h4"
.hasRole("ROLE_ADMIN")
```

---

# Creating an Admin User

A new controller has been added.

```text id="rgydjt"
AdminController
```

This controller contains APIs that are only accessible by administrators.

---

# Admin Controller

```java id="skdf8j"
@RestController
@RequestMapping("/admin")
public class AdminController {

}
```

Every endpoint in this controller begins with:

```text id="7i7cv8"
/admin
```

Because of the security configuration, these endpoints require the **ADMIN** role.

---

# Get All Users

Endpoint:

```http id="f0ikva"
GET /admin/all-users
```

Implementation:

```java id="zcy4ew"
return ResponseEntity.ok(all);
```

This is equivalent to:

```java id="k3qtvf"
return new ResponseEntity<>(
        all,
        HttpStatus.OK
);
```

If no users are found:

```java id="wgu94q"
return ResponseEntity
        .notFound()
        .build();
```

which is equivalent to:

```java id="iv10pp"
return new ResponseEntity<>(
        HttpStatus.NOT_FOUND
);
```

The `ResponseEntity` utility methods make the code shorter and more readable.

---

# Create Admin User

Endpoint:

```http id="p9duq7"
POST /admin/create-admin-user
```

Implementation:

```java id="h6u4mb"
userService.saveAdmin(user);
```

This creates a new administrator account.

---

# Changes in User Service

A new method has been introduced.

```java id="vhohyx"
public void saveAdmin(User user)
```

Its responsibilities are:

* Encrypt the password.
* Assign both `USER` and `ADMIN` roles.
* Save the user.

Updated code:

```java id="3jvjlwm"
user.setPassword(
        passwordEncoder.encode(
                user.getPassword()
        )
);

user.setRoles(
        Arrays.asList(
                "USER",
                "ADMIN"
        )
);

userRepository.save(user);
```

---

# User vs Admin Creation

## Normal User

```java id="hytdn0"
saveNewUser(user);
```

Assigned roles:

```text id="ht0zkw"
USER
```

MongoDB document:

```json id="vxbls5"
{
    "userName": "ram",
    "roles": [
        "USER"
    ]
}
```

---

## Admin User

```java id="idqgaj"
saveAdmin(user);
```

Assigned roles:

```text id="5n38nv"
USER
ADMIN
```

MongoDB document:

```json id="2m07hm"
{
    "userName": "admin",
    "roles": [
        "USER",
        "ADMIN"
    ]
}
```

Administrators also receive the `USER` role so they can access normal user endpoints in addition to admin endpoints.

---

# Authorization Flow

```text id="llzzye"
Client
   │
Username + Password
   │
   ▼
Authentication
   │
   ▼
User Loaded
   │
   ▼
Roles Loaded
   │
   ▼
SecurityFilterChain
   │
   ▼
Endpoint Requires ADMIN?
   │
 ┌─┴──────────────┐
 │                │
No              Yes
 │                │
 ▼                ▼
Access      Has ADMIN Role?
Granted            │
              ┌────┴─────┐
              │          │
             No         Yes
              │          │
              ▼          ▼
             403      Access Granted
```

---

# HTTP Responses

Depending on the authentication and authorization status, different HTTP status codes are returned.

| Status Code          | Meaning                                                   |
| -------------------- | --------------------------------------------------------- |
| **200 OK**           | Request completed successfully                            |
| **401 Unauthorized** | User is not authenticated                                 |
| **403 Forbidden**    | User is authenticated but does not have the required role |
| **404 Not Found**    | Requested resource was not found                          |

Example:

```text id="y2x67o"
Logged in as USER

↓

GET /admin/all-users

↓

403 Forbidden
```

Whereas:

```text id="epr9ik"
Logged in as ADMIN

↓

GET /admin/all-users

↓

200 OK
```

---

# Overall Security Architecture

```text id="d0jlwm"
                    Client
                      │
                      ▼
            Username + Password
                      │
                      ▼
             Spring Security
                      │
               Authentication
                      │
                      ▼
          UserDetailsServiceImpl
                      │
                      ▼
                  MongoDB
                      │
               User + Roles
                      │
                      ▼
          SecurityFilterChain
                      │
          Authorization Rules
                      │
      ┌───────────────┼────────────────┐
      │               │                │
      ▼               ▼                ▼
  /public/**     /journal/**      /admin/**
  Permit All     Authenticated     ADMIN Role
```

---

# Benefits of Role-Based Authorization

* Different users can have different levels of access.
* Sensitive administrative APIs are protected.
* Normal users cannot access administrator-only resources.
* Authorization is centralized inside the Spring Security configuration.
* The application is easier to extend with additional roles such as `MODERATOR`, `MANAGER`, or `SUPER_ADMIN` in the future.
