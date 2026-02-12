import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        LibrarySystem librarySystem = new LibrarySystem();
        librarySystem.initializeData("USERS.csv", "ITEMS.csv");
        librarySystem.readLoansFromFile("LOANS.csv"); // Reading loans from file
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Issue an item");
            System.out.println("2. Renew a loan");
            System.out.println("3. Return an item");
            System.out.println("4. View current loans");
            System.out.println("5. Generate report");
            System.out.println("6. Search for an item");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter item barcode: ");
                    String barcode = scanner.nextLine();
                    System.out.print("Enter user ID: ");
                    String userId = scanner.nextLine();
                    librarySystem.issueItem(barcode, userId, LocalDate.now());
                    break;
                case 2:
                    System.out.print("Enter item barcode: ");
                    barcode = scanner.nextLine();
                    librarySystem.renewLoan(barcode, LocalDate.now());
                    break;
                case 3:
                    System.out.print("Enter item barcode: ");
                    barcode = scanner.nextLine();
                    librarySystem.returnItem(barcode);
                    break;
                case 4:
                    librarySystem.viewCurrentLoans();
                    break;
                case 5:
                    librarySystem.generateReport();
                    break;
                case 6:
                    System.out.print("Enter item barcode: ");
                    barcode = scanner.nextLine();
                    librarySystem.searchItem(barcode);
                    break;
                case 7:
                    librarySystem.writeCurrentLoansToFile("LOANS.csv"); // Writing loans to file
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 7.");
            }
        }
    }
}
