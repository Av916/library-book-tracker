import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

/**
 * Owns catalogue operations and persists every book-state change.
 */
public class Library {
    private final FileStorage fileStorage;
    private final List<Book> books;
    private final List<IssueRecord> issues;
    private Book lastDeletedBook = null;
    private static final int LOAN_PERIOD_DAYS = 14;

    public Library(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
        this.books = fileStorage.loadBooks();
        this.issues = fileStorage.loadIssues();
    }

    public Book addBook(String title, String author, int quantity) throws InvalidBookDataException {
        validateBookData(title, author, quantity);
        Book book = new Book(nextBookId(), title.trim(), author.trim(), quantity, quantity);
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
        if (book.getIssuedCopies() > 0) {
            throw new BookNotAvailableException("Cannot remove a book while copies are issued.");
        }
        lastDeletedBook = book;
        books.remove(book);
        fileStorage.saveBooks(books);
    }

    public IssueRecord issueBook(int bookId, String borrowerUsername)
            throws BookNotFoundException, BookNotAvailableException {
        if (borrowerUsername == null || borrowerUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Borrower username is required.");
        }

        Book book = findBookById(bookId);
        book.issueCopy();
        LocalDate issueDate = LocalDate.now();
        IssueRecord issue = new IssueRecord(nextIssueId(), bookId, borrowerUsername.trim(),
                issueDate, issueDate.plusDays(LOAN_PERIOD_DAYS), null);
        issues.add(issue);
        fileStorage.saveBooks(books);
        fileStorage.saveIssues(issues);
        return issue;
    }

    public void returnBook(int issueId, String username, boolean isAdmin)
            throws IssueNotFoundException, UnauthorizedReturnException {
        IssueRecord issue = findIssueById(issueId);
        if (issue.isReturned()) {
            throw new IllegalStateException("This book has already been returned.");
        }
        if (!isAdmin && !issue.getBorrowerUsername().equalsIgnoreCase(username)) {
            throw new UnauthorizedReturnException("You can return only books issued to your account.");
        }

        Book book;
        try {
            book = findBookById(issue.getBookId());
        } catch (BookNotFoundException e) {
            throw new IllegalStateException("The book for this issue record no longer exists.");
        }
        book.returnCopy();
        issue.markReturned(LocalDate.now());
        fileStorage.saveBooks(books);
        fileStorage.saveIssues(issues);
    }

    public List<IssueRecord> getMyActiveIssues(String username) {
        List<IssueRecord> myIssues = new ArrayList<>();
        for (IssueRecord issue : issues) {
            if (!issue.isReturned() && issue.getBorrowerUsername().equalsIgnoreCase(username)) {
                myIssues.add(issue);
            }
        }
        return myIssues;
    }

    public List<IssueRecord> getAllActiveIssues() {
        List<IssueRecord> activeIssues = new ArrayList<>();
        for (IssueRecord issue : issues) {
            if (!issue.isReturned()) {
                activeIssues.add(issue);
            }
        }
        return activeIssues;
    }

    public void updateQuantity(int bookId, int newQuantity) throws BookNotFoundException {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        Book book = findBookById(bookId);
        book.setTotalCopies(newQuantity);
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

    private IssueRecord findIssueById(int issueId) throws IssueNotFoundException {
        for (IssueRecord issue : issues) {
            if (issue.getId() == issueId) {
                return issue;
            }
        }
        throw new IssueNotFoundException("No issue record found with ID " + issueId + ".");
    }

    private int nextIssueId() {
        int highestId = 0;
        for (IssueRecord issue : issues) {
            if (issue.getId() > highestId) {
                highestId = issue.getId();
            }
        }
        return highestId + 1;
    }

    private void validateBookData(String title, String author, int quantity) throws InvalidBookDataException {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidBookDataException("Title cannot be empty.");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new InvalidBookDataException("Author cannot be empty.");
        }
        if (title.contains(",") || author.contains(",")) {
            throw new InvalidBookDataException("Title and author cannot contain commas.");
        }
        if (quantity < 1) {
            throw new InvalidBookDataException("Quantity must be at least 1.");
        }
    }

    public boolean undoDelete(){
        if (lastDeletedBook == null){
            return false;
        }
        books.add(lastDeletedBook);
        fileStorage.saveBooks(books);
        lastDeletedBook = null;
        return true;
    }
}
