import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Records one borrowed copy of a book. */
public class IssueRecord {
    private final int id;
    private final int bookId;
    private final String borrowerUsername;
    private final LocalDate issueDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public IssueRecord(int id, int bookId, String borrowerUsername,
                       LocalDate issueDate, LocalDate dueDate, LocalDate returnDate) {
        this.id = id;
        this.bookId = bookId;
        this.borrowerUsername = borrowerUsername;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public int getId() {
        return id;
    }

    public int getBookId() {
        return bookId;
    }

    public String getBorrowerUsername() {
        return borrowerUsername;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public void markReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /** Format: id,bookId,borrowerUsername,issueDate,dueDate,returnDate */
    public String toFileLine() {
        return id + "," + bookId + "," + borrowerUsername + "," + issueDate + ","
                + dueDate + "," + (returnDate == null ? "" : returnDate);
    }

    public static IssueRecord fromFileLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 6) {
            return null;
        }

        try {
            int id = Integer.parseInt(parts[0].trim());
            int bookId = Integer.parseInt(parts[1].trim());
            String username = parts[2].trim();
            LocalDate issueDate = LocalDate.parse(parts[3].trim());
            LocalDate dueDate = LocalDate.parse(parts[4].trim());
            LocalDate returnDate = parts[5].trim().isEmpty() ? null
                    : LocalDate.parse(parts[5].trim());
            if (id <= 0 || bookId <= 0 || username.isEmpty() || dueDate.isBefore(issueDate)
                    || (returnDate != null && returnDate.isBefore(issueDate))) {
                return null;
            }
            return new IssueRecord(id, bookId, username, issueDate, dueDate, returnDate);
        } catch (NumberFormatException | DateTimeParseException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        String status = isReturned() ? "Returned on " + returnDate
                : "Issued | Due: " + dueDate;
        return "Issue ID: " + id + " | Book ID: " + bookId + " | Borrower: "
                + borrowerUsername + " | Issued: " + issueDate + " | " + status;
    }
}
