/** Thrown when an operation requires an available book but it is borrowed. */
public class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String message) {
        super(message);
    }
}
