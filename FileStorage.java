import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all file read/write operations for user data.
 * Data is stored in a simple comma-delimited text file: users.txt
 */
public class FileStorage {
    private static final String USERS_FILE = "users.txt";

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
}
