import java.time.LocalDate;

public class Loans {
    private String barcode;
    private String userId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private int numberOfRenews;

    public Loans(String barcode, String userId, LocalDate issueDate, LocalDate dueDate, int numberOfRenews) {
        this.barcode = barcode;
        this.userId = userId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.numberOfRenews = numberOfRenews;
    }

    // Getters
    public String getBarcode() {
        return barcode;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public int getNumberOfRenews() {
        return numberOfRenews;
    }
    public void setDueDate(LocalDate newDueDate) {
        this.dueDate = newDueDate;
    }

    public void setNumberOfRenews(int numberOfRenews) {
        this.numberOfRenews = numberOfRenews;
    }
}