import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Entry point for the Auth + File Persistence CLI module.
 * This is the standalone auth layer, ready to be plugged into the
 * full Library Book Tracker later.
 */
public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static AuthService authService = new AuthService(new FileStorage());

    public static void main(String[] args) {
        System.out.println("===== Library Book Tracker: Auth Module =====");

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

    /**
     * Shows the post-login menu. Only Logout and Exit are available here
     * since book management features are not part of this module yet.
     */
    private static void showLoggedInMenu() {
        System.out.println("\n===== Welcome, " + authService.getCurrentUser().getUsername()
                + " (" + authService.getCurrentUser().getRole() + ") =====");
        System.out.println("1. View My Profile\n2. Logout\n3. View All Users [ADMIN only]");
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
                System.out.println(authService.getCurrentUser());
                break;
            case 2:
                authService.logout();
                System.out.println("Logged out successfully.");
                break;
            case 3:
                if (authService.isAdmin()) {
                    for (User u : authService.getAllUsers()) {
                        System.out.println(u);
                    }
                } else {
                    System.out.println("Access denied: admin only.");
                }
                break;
            default:
                System.out.println("Invalid choice, try again.");
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
