/** Thrown when a book ID does not exist in the catalogue. */
public class BookNotFoundException extends Exception {
    public BookNotFoundException(String message) {
        super(message);
    }
}
