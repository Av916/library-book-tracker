/**
 * Represents a book in the library catalogue.
 */
public class Book {
    private final int id;
    private final String title;
    private final String author;
    private boolean available;

    public Book(int id, String title, String author, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = available;
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
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Converts this book to the file format: id,title,author,isAvailable.
     */
    public String toFileLine() {
        return id + "," + title + "," + author + "," + available;
    }

    /**
     * Parses a persisted book. Invalid or malformed data returns null.
     */
    public static Book fromFileLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 4) {
            return null;
        }

        try {
            int id = Integer.parseInt(parts[0].trim());
            String title = parts[1].trim();
            String author = parts[2].trim();
            if (id <= 0 || title.isEmpty() || author.isEmpty()
                    || (!"true".equalsIgnoreCase(parts[3].trim())
                    && !"false".equalsIgnoreCase(parts[3].trim()))) {
                return null;
            }
            return new Book(id, title, author, Boolean.parseBoolean(parts[3].trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        String status = available ? "Available" : "Borrowed";
        return "ID: " + id + " | Title: " + title + " | Author: " + author
                + " | Status: " + status;
    }
}
