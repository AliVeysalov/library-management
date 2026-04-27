# 📚 Library Management System

A RESTful API built with Spring Boot for managing a library system with role-based access control.

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3.2**
- **Spring Security + JWT**
- **Hibernate / JPA**
- **H2 Database**
- **Swagger / OpenAPI**
- **Docker**
- **Maven**

## 👥 Roles

| Role | Permissions |
|------|------------|
| ADMIN | Full access |
| LIBRARIAN | Manage books, borrow/return |
| MEMBER | Search books, view history |

## 🔌 API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login |

### Books
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/books | Get all books |
| GET | /api/books/{id} | Get book by ID |
| GET | /api/books/search | Search books |
| POST | /api/books | Add new book (ADMIN/LIBRARIAN) |
| PUT | /api/books/{id} | Update book (ADMIN/LIBRARIAN) |
| DELETE | /api/books/{id} | D
