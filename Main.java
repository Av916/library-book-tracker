import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the Library Book Tracker CLI.
 */
public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static FileStorage fileStorage = new FileStorage();
    private static AuthService authService = new AuthService(fileStorage);
    private static Library library = new Library(fileStorage);

    public static void main(String[] args) {
        System.out.println("===== Library Book Tracker =====");

        boolean running = true;
        while (running) {
            if (!authService.isLoggedIn()) {
                running = showEntryMenu();
            } else {
                showLoggedInMenu();
            }
        }

        System.out.println("Goodbye!");
        scanner.close();
    }

    /**
     * Shows the entry screen (Login / Register / Exit).
     * Returns false if the user chose to exit the program.
     */
    private static boolean showEntryMenu() {
        System.out.println("\n1. Login\n2. Register\n3. Exit");
        System.out.print("Enter choice: ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return true;
        }

        switch (choice) {
            case 1:
                handleLogin();
                return true;
            case 2:
                handleRegister();
                return true;
            case 3:
                return false;
            default:
                System.out.println("Invalid choice, try again.");
                return true;
        }
    }

    private static void showLoggedInMenu() {
        System.out.println("\n===== Welcome, " + authService.getCurrentUser().getUsername()
                + " (" + authService.getCurrentUser().getRole() + ") =====");
        System.out.println("1. Add Book [ADMIN only]\n2. View Available Books"
                + "\n3. View All Books\n4. Search Books\n5. Remove Book [ADMIN only]"
                + "\n6. View My Profile\n7. View All Users [ADMIN only]"
                + "\n8. Logout\n9. Exit");
        System.out.print("Enter choice: ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return;
        }

        switch (choice) {
            case 1:
                handleAddBook();
                break;
            case 2:
                displayBooks(library.getAvailableBooks(), "No books are currently available.");
                break;
            case 3:
                displayBooks(library.getAllBooks(), "The catalogue is empty.");
                break;
            case 4:
                handleSearchBooks();
                break;
            case 5:
                handleRemoveBook();
                break;
            case 6:
                System.out.println(authService.getCurrentUser());
                break;
            case 7:
                handleViewAllUsers();
                break;
            case 8:
                authService.logout();
                System.out.println("Logged out successfully.");
                break;
            case 9:
                System.out.println("Goodbye!");
                scanner.close();
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice, try again.");
        }
    }

    private static void handleAddBook() {
        if (!requireAdmin()) {
            return;
        }

        System.out.print("Book title: ");
        String title = scanner.nextLine();
        System.out.print("Book author: ");
        String author = scanner.nextLine();

        try {
            Book book = library.addBook(title, author);
            System.out.println("Book added successfully: " + book);
        } catch (InvalidBookDataException e) {
            System.out.println("Could not add book: " + e.getMessage());
        }
    }

    private static void handleSearchBooks() {
        System.out.print("Search by title or author: ");
        String query = scanner.nextLine();
        displayBooks(library.searchBooks(query), "No matching books found.");
    }

    private static void handleRemoveBook() {
        if (!requireAdmin()) {
            return;
        }

        System.out.print("Book ID to remove: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            library.removeBook(id);
            System.out.println("Book removed successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid book ID.");
        } catch (BookNotFoundException | BookNotAvailableException e) {
            System.out.println("Could not remove book: " + e.getMessage());
        }
    }

    private static void handleViewAllUsers() {
        if (!requireAdmin()) {
            return;
        }
        for (User user : authService.getAllUsers()) {
            System.out.println(user);
        }
    }

    private static boolean requireAdmin() {
        if (!authService.isAdmin()) {
            System.out.println("Access denied: admin only.");
            return false;
        }
        return true;
    }

    private static void displayBooks(List<Book> books, String emptyMessage) {
        if (books.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        for (Book book : books) {
            System.out.println(book);
        }
    }

    private static void handleRegister() {
        System.out.print("Choose a username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Choose a password: ");
        String password = scanner.nextLine();
        System.out.print("Role (ADMIN/MEMBER): ");
        String role = scanner.nextLine().trim().toUpperCase();

        if (!role.equals("ADMIN") && !role.equals("MEMBER")) {
            System.out.println("Invalid role. Defaulting to MEMBER.");
            role = "MEMBER";
        }

        try {
            authService.register(username, password, role);
            System.out.println("Registration successful! You can now log in.");
        } catch (UserAlreadyExistsException e) {
            System.out.println("Registration failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            authService.login(username, password);
            System.out.println("Login successful!");
        } catch (InvalidCredentialsException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }
}
