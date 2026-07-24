/**
 * Represents a registered user of the system.
 * Password is stored only in hashed form (SHA-256).
 */
public class User {
    private String username;
    private String hashedPassword;
    private String role; // "ADMIN" or "MEMBER"

    public User(String username, String hashedPassword, String role) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getRole() {
        return role;
    }

    /**
     * Converts this user into a single delimited line for file storage.
     * Format: username,hashedPassword,role
     */
    public String toFileLine() {
        return username + "," + hashedPassword + "," + role;
    }

    /**
     * Parses a delimited file line back into a User object.
     * Returns null if the line is malformed (caller should skip it).
     */
    public static User fromFileLine(String line) {
        String[] parts = line.split(",");
        if (parts.length != 3) {
            return null;
        }
        return new User(parts[0], parts[1], parts[2]);
    }

    @Override
    public String toString() {
        return "Username: " + username + " | Role: " + role;
    }
}
