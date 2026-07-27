# Library Book Tracker

A command-line library tracker built in **pure Java** (no frameworks, no external libraries, no database). Users, catalogue records, and issue history are persisted locally in plain text files. Passwords are stored using SHA-256 hashing.

## Features

- User registration with role selection (`ADMIN` / `MEMBER`)
- Login with credential validation
- Passwords hashed with SHA-256 before storage — never stored in plain text
- Local file persistence (`users.txt`) — auto-created if missing, corrupt lines skipped safely
- Session handling (login / logout)
- Custom checked exceptions for clean error handling instead of crashes
- Admin-only book addition, quantity updates, and book removal
- Catalogue search and availability views
- Multiple copies per catalogue title, with available and issued counts
- Issue books to the logged-in user with a 14-day due date
- Return books using an issue ID; members can return only their own books
- Persistent issue records in `issues.txt`

## Requirements

- JDK 17 or higher

## Project Structure

```
library-book-tracker/
├── Main.java                          # CLI entry point and menu loop
├── User.java                          # User entity + file serialization
├── AuthService.java                   # Register, login, hashing, session/role logic
├── Book.java                          # Catalogue title and copy quantities
├── IssueRecord.java                   # Book issue/return record
├── Library.java                       # Catalogue and circulation logic
├── FileStorage.java                   # Reads/writes users.txt, books.txt, issues.txt
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
- After logging in, members and admins can issue available books, view their active issues, and return them. Admins can also manage the catalogue and view all active issues.

All registered users are saved to `users.txt` in the project directory, in the format:

```
username,hashedPassword,role
```

This file is created automatically on first run and reloaded every time the program starts, so accounts persist across sessions.

Books are saved in `books.txt` as:

```
id,title,author,totalCopies,availableCopies
```

The application also reads the earlier `id,title,author,isAvailable` book format and treats each old record as one copy. Issue records are stored in `issues.txt` as:

```
issueId,bookId,borrowerUsername,issueDate,dueDate,returnDate
```

## Exception Handling

| Scenario | Exception |
|----------|-----------|
| Duplicate username on registration | `UserAlreadyExistsException` |
| Wrong username or password on login | `InvalidCredentialsException` |
| Empty username on registration | `IllegalArgumentException` |
| File read/write failure | `IOException` (caught internally, shown as a warning) |
| Corrupt line in `users.txt` | Skipped with a warning, program continues loading |
| Attempting to issue an unavailable book | `BookNotAvailableException` |
| Returning another member's book | `UnauthorizedReturnException` |

## Roadmap

Possible next improvements include editable book details, due-date reminders, overdue reports, renewals, reservations, and safer admin-account creation.

## License

For personal / educational use.
