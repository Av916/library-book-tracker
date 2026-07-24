import java.util.ArrayList;
import java.util.List;

/**
 * Owns catalogue operations and persists every book-state change.
 */
public class Library {
    private final FileStorage fileStorage;
    private final List<Book> books;

    public Library(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
        this.books = fileStorage.loadBooks();
    }

    public Book addBook(String title, String author) throws InvalidBookDataException {
        validateBookData(title, author);
        Book book = new Book(nextBookId(), title.trim(), author.trim(), true);
        books.add(book);
        fileStorage.saveBooks(books);
        return book;
    }

    public List<Book> getAvailableBooks() {
        List<Book> availableBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.isAvailable()) {
                availableBooks.add(book);
            }
        }
        return availableBooks;
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    public List<Book> searchBooks(String query) {
        List<Book> matches = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return matches;
        }

        String normalizedQuery = query.trim().toLowerCase();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(normalizedQuery)
                    || book.getAuthor().toLowerCase().contains(normalizedQuery)) {
                matches.add(book);
            }
        }
        return matches;
    }

    public void removeBook(int id) throws BookNotFoundException, BookNotAvailableException {
        Book book = findBookById(id);
        if (!book.isAvailable()) {
            throw new BookNotAvailableException("Cannot remove a borrowed book.");
        }
        books.remove(book);
        fileStorage.saveBooks(books);
    }

    private Book findBookById(int id) throws BookNotFoundException {
        for (Book book : books) {
            if (book.getId() == id) {
                return book;
            }
        }
        throw new BookNotFoundException("No book found with ID " + id + ".");
    }

    private int nextBookId() {
        int highestId = 0;
        for (Book book : books) {
            if (book.getId() > highestId) {
                highestId = book.getId();
            }
        }
        return highestId + 1;
    }

    private void validateBookData(String title, String author) throws InvalidBookDataException {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidBookDataException("Title cannot be empty.");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new InvalidBookDataException("Author cannot be empty.");
        }
        if (title.contains(",") || author.contains(",")) {
            throw new InvalidBookDataException("Title and author cannot contain commas.");
        }
    }
}
