import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all file read/write operations for user, book, and issue data.
 * Data is stored in comma-delimited text files.
 */
public class FileStorage {
    private static final String USERS_FILE = "users.txt";
    private static final String BOOKS_FILE = "books.txt";
    private static final String ISSUES_FILE = "issues.txt";

    /**
     * Loads all users from users.txt into memory.
     * If the file doesn't exist yet, it is created empty and an empty list is returned.
     * Malformed lines are skipped with a warning instead of crashing the program.
     */
    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Warning: could not create users.txt -> " + e.getMessage());
            }
            return users; // empty list, nothing to load yet
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                User user = User.fromFileLine(line);
                if (user == null) {
                    System.out.println("Warning: skipping corrupt line " + lineNumber + " in users.txt");
                    continue;
                }
                users.add(user);
            }
        } catch (IOException e) {
            System.out.println("Error reading users.txt: " + e.getMessage());
        }

        return users;
    }

    /**
     * Saves the full list of users to users.txt, overwriting previous content.
     * Called after every registration so data is never lost between runs.
     */
    public void saveUsers(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                writer.write(user.toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving users.txt: " + e.getMessage());
        }
    }

    /**
     * Loads books from books.txt. Missing files are created; malformed lines
     * are skipped with a warning so one bad record does not stop the program.
     */
    public List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        File file = new File(BOOKS_FILE);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Warning: could not create books.txt -> " + e.getMessage());
            }
            return books;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                Book book = Book.fromFileLine(line);
                if (book == null) {
                    System.out.println("Warning: skipping corrupt line " + lineNumber + " in books.txt");
                    continue;
                }
                books.add(book);
            }
        } catch (IOException e) {
            System.out.println("Error reading books.txt: " + e.getMessage());
        }

        return books;
    }

    /** Saves all catalogue records after each book-state change. */
    public void saveBooks(List<Book> books) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            for (Book book : books) {
                writer.write(book.toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving books.txt: " + e.getMessage());
        }
    }

    /** Loads all issue records from issues.txt. */
    public List<IssueRecord> loadIssues() {
        List<IssueRecord> issues = new ArrayList<>();
        File file = new File(ISSUES_FILE);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Warning: could not create issues.txt -> " + e.getMessage());
            }
            return issues;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                IssueRecord issue = IssueRecord.fromFileLine(line);
                if (issue == null) {
                    System.out.println("Warning: skipping corrupt line " + lineNumber + " in issues.txt");
                    continue;
                }
                issues.add(issue);
            }
        } catch (IOException e) {
            System.out.println("Error reading issues.txt: " + e.getMessage());
        }
        return issues;
    }

    /** Saves all issue records after an issue or return action. */
    public void saveIssues(List<IssueRecord> issues) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ISSUES_FILE))) {
            for (IssueRecord issue : issues) {
                writer.write(issue.toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving issues.txt: " + e.getMessage());
        }
    }
}
