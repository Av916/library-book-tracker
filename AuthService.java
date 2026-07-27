import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Handles registration, login, password hashing, and the current session.
 * Delegates all persistence to FileStorage.
 */
public class AuthService {
    private FileStorage fileStorage;
    private List<User> users;
    private User currentUser; // null when nobody is logged in

    public AuthService(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
        this.users = fileStorage.loadUsers();
    }

    /**
     * Registers a new user with the given username, plain password, and role.
     * Throws UserAlreadyExistsException if the username is already taken.
     */
    public void register(String username, String plainPassword, String role)
            throws UserAlreadyExistsException {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (username.contains(",")) {
            throw new IllegalArgumentException("Username cannot contain commas.");
        }

        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                throw new UserAlreadyExistsException("Username '" + username + "' is already taken.");
            }
        }

        String hashed = hashPassword(plainPassword);
        User newUser = new User(username, hashed, role);
        users.add(newUser);
        fileStorage.saveUsers(users);
    }

    /**
     * Attempts to log in with the given username and plain password.
     * On success, sets this as the current session user.
     * Throws InvalidCredentialsException if username doesn't exist or password is wrong.
     */
    public void login(String username, String plainPassword) throws InvalidCredentialsException {
        String hashed = hashPassword(plainPassword);

        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                if (u.getHashedPassword().equals(hashed)) {
                    currentUser = u;
                    return;
                } else {
                    throw new InvalidCredentialsException("Incorrect password.");
                }
            }
        }
        throw new InvalidCredentialsException("No account found with username '" + username + "'.");
    }

    /**
     * Clears the current session. Does not touch stored data.
     */
    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<User> getAllUsers() {
        return users;
    }
    public boolean isAdmin() {
        return currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Hashes a plain-text password using SHA-256 so raw passwords are
     * never stored or compared directly.
     */
    private String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plainPassword.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            // SHA-256 and UTF-8 are always available on a standard JVM,
            // so this should never actually happen.
            throw new RuntimeException("Hashing failed: " + e.getMessage());
        }
    }
}
