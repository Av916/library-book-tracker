import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Records one borrowed copy of a book. */
public class IssueRecord {
    private final int id;
    private final int bookId;
    private final String borrowerUsername;
    private final LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private int renewalCount;

    public IssueRecord(int id, int bookId, String borrowerUsername,
                       LocalDate issueDate, LocalDate dueDate, LocalDate returnDate) {
        this(id, bookId, borrowerUsername, issueDate, dueDate, returnDate, 0);
    }

    public IssueRecord(int id, int bookId, String borrowerUsername,
                       LocalDate issueDate, LocalDate dueDate, LocalDate returnDate,
                       int renewalCount) {
        this.id = id;
        this.bookId = bookId;
        this.borrowerUsername = borrowerUsername;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.renewalCount = renewalCount;
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

    public int getRenewalCount() {
        return renewalCount;
    }

    public void markReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void renew(int extraDays) {
        dueDate = dueDate.plusDays(extraDays);
        renewalCount++;
    }

    /** Format: id,bookId,borrowerUsername,issueDate,dueDate,returnDate,renewalCount */
    public String toFileLine() {
        return id + "," + bookId + "," + borrowerUsername + "," + issueDate + ","
                + dueDate + "," + (returnDate == null ? "" : returnDate) + "," + renewalCount;
    }

    public static IssueRecord fromFileLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 6 && parts.length != 7) {
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
            int renewalCount = parts.length == 7 ? Integer.parseInt(parts[6].trim()) : 0;
            if (id <= 0 || bookId <= 0 || username.isEmpty() || dueDate.isBefore(issueDate)
                    || (returnDate != null && returnDate.isBefore(issueDate)) || renewalCount < 0) {
                return null;
            }
            return new IssueRecord(id, bookId, username, issueDate, dueDate, returnDate,
                    renewalCount);
        } catch (NumberFormatException | DateTimeParseException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        String status = isReturned() ? "Returned on " + returnDate
                : "Issued | Due: " + dueDate;
        return "Issue ID: " + id + " | Book ID: " + bookId + " | Borrower: "
                + borrowerUsername + " | Issued: " + issueDate + " | " + status
                + " | Renewals: " + renewalCount;
    }
}
