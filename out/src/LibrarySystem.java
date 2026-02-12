import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibrarySystem{
    private List<Users> users;
    private List<Items> items;
    private List<Loans> loans;

    public LibrarySystem() {
        this.users = new ArrayList<>();
        this.items = new ArrayList<>();
        this.loans = new ArrayList<>();
    }

    // Method to initialize users and items from CSV files
    public void initializeData(String usersCSVFile, String itemsCSVFile) {
        List<String[]> userData = CSVReader.readCSV(usersCSVFile);
        List<String[]> itemData = CSVReader.readCSV(itemsCSVFile);

        for (String[] userRow : userData) {
            Users user = new Users(userRow[0], userRow[1], userRow[2], userRow[3]);
            users.add(user);
        }

        for (String[] itemRow : itemData) {
            Items item = new Items(itemRow[0], itemRow[1], itemRow[2], itemRow[3], itemRow[4], itemRow[5]);
            items.add(item);
        }
    }

    // Method to read from LOANS.csv when the program starts
    public void readLoansFromFile(String loansCSVFile) {
        List<String[]> loanData = CSVReader.readCSV(loansCSVFile);

        for (String[] loanRow : loanData) {
            if (loanRow.length < 5) {
                System.out.println("Invalid data format in loans CSV file.");
                continue; // Skip this row and continue with the next one
            }

            String barcode = loanRow[0];
            String userId = loanRow[1];
            LocalDate issueDate = LocalDate.parse(loanRow[2]); // Assuming the date is stored in ISO format (yyyy-MM-dd)
            LocalDate dueDate = LocalDate.parse(loanRow[3]);   // Assuming the date is stored in ISO format (yyyy-MM-dd)
            int numberOfRenews = Integer.parseInt(loanRow[4]);

            Loans loan = new Loans(barcode, userId, issueDate, dueDate, numberOfRenews);
            loans.add(loan);
        }
    }


    // Method to write current loans to LOANS.csv when the program exits
    public void writeCurrentLoansToFile(String loansCSVFile) {
        try (FileWriter writer = new FileWriter(loansCSVFile)) {
            for (Loans loan : loans) {
                writer.write(loan.getBarcode() + "," + loan.getUserId() + "," +
                        loan.getIssueDate() + "," + loan.getDueDate() + "," +
                        loan.getNumberOfRenews() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to issue an item
    public void issueItem(String barcode, String userId, LocalDate currentDate) {
        // Check if user exists
        boolean userExists = users.stream().anyMatch(user -> user.getUserId().equals(userId));
        if (!userExists) {
            System.out.println("User with ID " + userId + " does not exist.");
            return;
        }

        // Find the item by barcode
        Items itemToIssue = getItemByBarcode(barcode);
        if (itemToIssue == null) {
            System.out.println("Item with barcode " + barcode + " does not exist.");
            return;
        }

        // Check if the item is already on loan
        if (isItemOnLoan(barcode)) {
            System.out.println("Item with barcode " + barcode + " is already on loan.");
            return;
        }

        // Calculate due date based on item type
        LocalDate dueDate;
        if (itemToIssue.getType().equalsIgnoreCase("Book")) {
            dueDate = currentDate.plusWeeks(4);
        } else if (itemToIssue.getType().equalsIgnoreCase("Multimedia")) {
            dueDate = currentDate.plusWeeks(1);
        } else {
            System.out.println("Invalid item type.");
            return;
        }

        // Create loan object and add to the list of loans
        Loans loan = new Loans(barcode, userId, currentDate, dueDate, 0);
        loans.add(loan);
        System.out.println("Item issued successfully.");
    }

    // Method to renew an existing loan
    public void renewLoan(String barcode, LocalDate currentDate) {
        // Find the loan by barcode
        Loans loanToRenew = loans.stream()
                .filter(loan -> loan.getBarcode().equals(barcode))
                .findFirst()
                .orElse(null);

        if (loanToRenew == null) {
            System.out.println("No loan found for item with barcode " + barcode);
            return;
        }

        // Check if the item type allows renewal
        Items item = getItemByBarcode(barcode);
        int maxRenews = item.getType().equalsIgnoreCase("Book") ? 3 : 2;
        if (loanToRenew.getNumberOfRenews() >= maxRenews) {
            System.out.println("Maximum number of renewals reached for this item.");
            return;
        }

        // Calculate new due date
        LocalDate newDueDate;
        if (item.getType().equalsIgnoreCase("Book")) {
            newDueDate = loanToRenew.getDueDate().plusWeeks(2);
        } else {
            newDueDate = loanToRenew.getDueDate().plusWeeks(1);
        }

        // Update loan details
        loanToRenew.setDueDate(newDueDate);
        loanToRenew.setNumberOfRenews(loanToRenew.getNumberOfRenews() + 1);
        System.out.println("Loan renewed successfully.");

        // Update the loan in the loans list
        // Find the index of the loan in the list
        int index = -1;
        for (int i = 0; i < loans.size(); i++) {
            if (loans.get(i).getBarcode().equals(barcode)) {
                index = i;
                break;
            }
        }
        // Replace the old loan with the updated one
        if (index != -1) {
            loans.set(index, loanToRenew);
        }
    }

    // Method to record the return of an item
    public void returnItem(String barcode) {
        boolean found = false;
        for (Loans loan : loans) {
            if (loan.getBarcode().equals(barcode)) {
                loans.remove(loan);
                found = true;
                break; // Exit the loop once the loan is found and removed
            }
        }
        if (found) {
            System.out.println("Item returned successfully.");
        } else {
            System.out.println("Item with barcode " + barcode + " not found.");
        }
    }
    // Method to view all items currently on loan
    public void viewCurrentLoans() {
        if (loans.isEmpty()) {
            System.out.println("No items currently on loan.");
            return;
        }

        System.out.println("Items currently on loan:");
        for (Loans loan : loans) {
            System.out.println("Barcode: " + loan.getBarcode() + ", User ID: " + loan.getUserId() +
                    ", Due Date: " + loan.getDueDate().toString()); // Ensure proper formatting here
        }
    }

    // Generate a report
    public void generateReport() {
        int totalBooks = getTotalBookLoans();
        int totalMultimedia = getTotalMultimediaLoans();
        double percentageRenewed = getPercentageOfRenewedLoans();
        System.out.println("Library Name: Your Library");
        System.out.println("Total number of Book loans: " + totalBooks);
        System.out.println("Total number of Multimedia loans: " + totalMultimedia);
        System.out.println("Percentage of loans renewed more than once: " + percentageRenewed + "%");
    }

    // Method to search for an item by barcode
    public void searchItem(String barcode) {
        Items item = getItemByBarcode(barcode);
        if (item != null) {
            System.out.println("Item found:");
            System.out.println("Barcode: " + item.getBarcode());
            System.out.println("Title: " + item.getTitle());
            System.out.println("Author/Artist: " + item.getAuthorArtist());
            System.out.println("Type: " + item.getType());
            System.out.println("Year: " + item.getYear());
            System.out.println("ISBN: " + item.getIsbn());
        } else {
            System.out.println("Item with barcode " + barcode + " not found.");
        }
    }

    // Helper method to get an item by barcode
    private Items getItemByBarcode(String barcode) {
        for (Items item : items) {
            if (item.getBarcode().equals(barcode)) {
                return item;
            }
        }
        return null;
    }

    // Helper method to check if an item is on loan
    private boolean isItemOnLoan(String barcode) {
        for (Loans loan : loans) {
            if (loan.getBarcode().equals(barcode)) {
                return true;
            }
        }
        return false;
    }

    // Helper methods for generating reports
    private int getTotalBookLoans() {
        int total = 0;
        for (Loans loan : loans) {
            if (getItemByBarcode(loan.getBarcode()).getType().equalsIgnoreCase("Book")) {
                total++;
            }
        }
        return total;
    }

    private int getTotalMultimediaLoans() {
        int total = 0;
        for (Loans loan : loans) {
            if (getItemByBarcode(loan.getBarcode()).getType().equalsIgnoreCase("Multimedia")) {
                total++;
            }
        }
        return total;
    }

    private double getPercentageOfRenewedLoans() {
        if (loans.isEmpty()) {
            return 0.0;
        }

        int renewedMoreThanOnce = 0;
        for (Loans loan : loans) {
            if (loan.getNumberOfRenews() > 1) {
                renewedMoreThanOnce++;
            }
        }

        return ((double) renewedMoreThanOnce / loans.size()) * 100;
    }
}
