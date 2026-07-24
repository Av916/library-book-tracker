/** Thrown when a book title or author cannot be saved safely. */
public class InvalidBookDataException extends Exception {
    public InvalidBookDataException(String message) {
        super(message);
    }
}
