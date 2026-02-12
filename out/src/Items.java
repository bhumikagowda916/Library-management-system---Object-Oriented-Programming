public class Items {
    private String barcode;
    private String authorArtist;
    private String title;
    private String type;
    private int year;
    private String isbn;

    public Items(String barcode, String authorArtist, String title, String type, String year, String isbn) {
        this.barcode = barcode;
        this.authorArtist = authorArtist;
        this.title = title;
        this.type = type;
        try {
            this.year = Integer.parseInt(year); // Parse year to int
        } catch (NumberFormatException e) {
            System.out.println("Invalid year format for item with barcode " + barcode + ". Setting year to 0.");
            this.year = 0; // Or set it to a default value as needed
        }
        this.isbn = isbn;
    }

    // Getters
    public String getBarcode() {
        return barcode;
    }

    public String getAuthorArtist() {
        return authorArtist;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public int getYear() {
        return year;
    }

    public String getIsbn() {
        return isbn;
    }
}
