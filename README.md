# Library Book Tracker — Auth Module

A command-line authentication module built in **pure Java** (no frameworks, no external libraries, no database). User accounts are persisted locally in a plain text file, and passwords are stored using SHA-256 hashing. This module is the first building block of a larger Library Book Tracker CLI project.

## Features

- User registration with role selection (`ADMIN` / `MEMBER`)
- Login with credential validation
- Passwords hashed with SHA-256 before storage — never stored in plain text
- Local file persistence (`users.txt`) — auto-created if missing, corrupt lines skipped safely
- Session handling (login / logout)
- Custom checked exceptions for clean error handling instead of crashes

## Requirements

- JDK 17 or higher

## Project Structure

```
library-auth/
├── Main.java                          # CLI entry point and menu loop
├── User.java                          # User entity + file serialization
├── AuthService.java                   # Register, login, hashing, session/role logic
├── FileStorage.java                   # Reads/writes users.txt
├── UserAlreadyExistsException.java    # Thrown on duplicate username
├── InvalidCredentialsException.java   # Thrown on wrong username/password
├── .gitignore
└── README.md
```

## How to Run

1. Compile all source files:
   ```bash
   javac *.java
   ```
2. Run the program:
   ```bash
   java Main
   ```

## Usage

On launch, you'll see:

```
===== Library Book Tracker: Auth Module =====

1. Login
2. Register
3. Exit
```

- **Register**: choose a username, password, and role (`ADMIN` or `MEMBER`). Duplicate usernames are rejected.
- **Login**: enter your username and password. Wrong credentials are rejected with a clear message.
- After logging in, you can view your profile or log out.

All registered users are saved to `users.txt` in the project directory, in the format:

```
username,hashedPassword,role
```

This file is created automatically on first run and reloaded every time the program starts, so accounts persist across sessions.

## Exception Handling

| Scenario | Exception |
|----------|-----------|
| Duplicate username on registration | `UserAlreadyExistsException` |
| Wrong username or password on login | `InvalidCredentialsException` |
| Empty username on registration | `IllegalArgumentException` |
| File read/write failure | `IOException` (caught internally, shown as a warning) |
| Corrupt line in `users.txt` | Skipped with a warning, program continues loading |

## Roadmap

This module is designed to plug into the full Library Book Tracker:

- Book entity + `Library.java` for add/borrow/return/search/remove
- Role-based menu gating (`ADMIN`-only actions like add/remove books)
- `books.txt` persistence alongside `users.txt`

## License

For personal / educational use.
