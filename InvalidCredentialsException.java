/**
 * Thrown when a login attempt fails due to a wrong username or password.
 */
public class InvalidCredentialsException extends Exception {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
