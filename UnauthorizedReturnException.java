/** Thrown when a user tries to return a book issued to someone else. */
public class UnauthorizedReturnException extends Exception {
    public UnauthorizedReturnException(String message) {
        super(message);
    }
}
