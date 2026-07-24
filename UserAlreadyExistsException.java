/**
 * Thrown when a registration attempt uses a username that is already taken.
 */
public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
