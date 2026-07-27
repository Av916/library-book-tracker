/**
 * Represents a book in the library catalogue.
 */
public class Book {
    private final int id;
    private final String title;
    private final String author;
    private int totalCopies;
    private int availableCopies;

    public Book(int id, String title, String author, int totalCopies, int availableCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public int getIssuedCopies() {
        return totalCopies - availableCopies;
    }

    public void issueCopy() throws BookNotAvailableException {
        if (!isAvailable()) {
            throw new BookNotAvailableException("No copies of this book are currently available.");
        }
        availableCopies--;
    }

    public void returnCopy() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    public void setTotalCopies(int totalCopies) {
        if (totalCopies < getIssuedCopies()) {
            throw new IllegalArgumentException("Total copies cannot be lower than the number currently issued.");
        }
        this.availableCopies = totalCopies - getIssuedCopies();
        this.totalCopies = totalCopies;
    }

    /**
     * Converts this book to the file format: id,title,author,totalCopies,availableCopies.
     */
    public String toFileLine() {
        return id + "," + title + "," + author + "," + totalCopies + "," + availableCopies;
    }

    /**
     * Parses a persisted book. Invalid or malformed data returns null.
     */
    public static Book fromFileLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 4 && parts.length != 5) {
            return null;
        }

        try {
            int id = Integer.parseInt(parts[0].trim());
            String title = parts[1].trim();
            String author = parts[2].trim();
            if (id <= 0 || title.isEmpty() || author.isEmpty()) {
                return null;
            }

            // Supports the old id,title,author,isAvailable format so existing
            // catalogue data remains usable after quantities are introduced.
            if (parts.length == 4) {
                if (!"true".equalsIgnoreCase(parts[3].trim())
                        && !"false".equalsIgnoreCase(parts[3].trim())) {
                    return null;
                }
                return new Book(id, title, author, 1,
                        Boolean.parseBoolean(parts[3].trim()) ? 1 : 0);
            }

            int totalCopies = Integer.parseInt(parts[3].trim());
            int availableCopies = Integer.parseInt(parts[4].trim());
            if (totalCopies < 0 || availableCopies < 0 || availableCopies > totalCopies) {
                return null;
            }
            return new Book(id, title, author, totalCopies, availableCopies);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        String status = isAvailable() ? "Available" : "Unavailable";
        return "ID: " + id + " | Title: " + title + " | Author: " + author
                + " | Copies: " + availableCopies + "/" + totalCopies + " available"
                + " | Status: " + status;
    }
}
