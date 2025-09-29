/*
 * Complete Library Management System
 * Prototype for KENT LMS
 * Assessment 3 - Group Project
 */
package groupproject;

import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

// LMS Class - Main Program 
public class LibraryManagementSystem {
    private static Scanner sc = new Scanner(System.in);
    private static Library library = new Library();

    public static void main(String[] args) {
        preloadData();
        printHeader();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    borrowBookMenu();
                    break;
                case "2":
                    returnBookMenu();
                    break;
                case "3":
                    library.displayBooks();
                    break;
                case "4":
                    registerMemberMenu();
                    break;
                case "5":
                    library.displayMembers();
                    break;
                case "6":
                    library.displayTransactions();
                    break;
                case "7":
                    System.out.println("Exiting... Thank you for using LMS!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1-7.");
            }
            
        }
        sc.close();
    }

    private static void preloadData() {
        //  some books
        library.addBook(new Book("ISBN001", "Java Basics", "Author A", "OODP", 39.99));
        library.addBook(new Book("ISBN002", "History of Coding", "Author B", "OODB", 29.99));
        library.addBook(new Book("ISBN003", "Kent Australia IT Guide", "Kent Institute", "IT", 49.99));
        library.addBook(new Book("ISBN002", "Database Management", "Author C", "DBFN", 29.99));

        //  some members
        library.registerMember(new Member("k241054", "Santosh Silwal", "Regular"));
        library.registerMember(new Member("k241108", "Shacham Bhandari", "Regular"));
        library.registerMember(new Member("k230898", "Prasanta Kandel", "Regular"));
        library.registerMember(new Member("k240814", "Ansh Shrestha", "Regular"));
    }

    private static void printHeader() {
        String stars = "";
        for (int i = 0; i < 70; i++) {
            stars += "=";
        }
        System.out.println(stars);
        System.out.println("\tWELCOME TO LIBRARY MANAGEMENT SYSTEM");
        System.out.println("\n\tDeveloped by Group D: Team Barbie \n");
        System.out.println("\tMembers: ");
        
        System.out.println("\n \tSENG - Software Engineering");
        System.out.println("\tAssessment 3 - Group Project");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy \t HH:mm:ss");
        System.out.println("\tDate: " + LocalDateTime.now().format(dtf));
        System.out.println(stars);
    }

    private static void printMenu() {
        System.out.println("\nPlease select an option:");
        System.out.println("1. Borrow Book");
        System.out.println("2. Return Book");
        System.out.println("3. Show Books");
        System.out.println("4. Register New Member");
        System.out.println("5. Show Members");
        System.out.println("6. Show Transactions");
        System.out.println("7. Exit");
        System.out.print("Your choice: ");
    }

    private static void borrowBookMenu() {
        System.out.println("\n--- Borrow Book ---");

        System.out.print("Enter Student ID: ");
        String memberID = sc.nextLine().trim();
        Member member = library.findMember(memberID);

        if (member == null) {
            System.out.println("Member not found. Please register before borrowing.");
            return;
        }

        library.displayBooks();
        System.out.print("Enter ISBN of the book to borrow: ");
        String isbn = sc.nextLine().trim();

        Book book = library.findBook(isbn);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        if (!member.canBorrowMore()) {
            System.out.println("Borrowing limit reached. Return a book before borrowing a new one.");
            return;
        }

        boolean success = library.borrowBook(memberID, isbn);
        if (success) {
            System.out.println("Book borrowed successfully.");
        } else {
            System.out.println("Borrowing failed.");
        }
    }

    private static void returnBookMenu() {
        System.out.println("\n--- Return Book ---");

        System.out.print("Enter Student ID: ");
        String memberID = sc.nextLine().trim();
        Member member = library.findMember(memberID);

        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        System.out.print("Enter ISBN of the book to return: ");
        String isbn = sc.nextLine().trim();

        Book book = library.findBook(isbn);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        System.out.print("Enter days overdue (0 if none): ");
        int daysOverdue = readInt();

        double fee = library.returnBook(memberID, isbn, daysOverdue);
        if (fee < 0) {
            System.out.println("Return failed. Book might not be borrowed by this member.");
        } else {
            System.out.printf("Returned successfully. Late fee: $%.2f\n", fee);
        }
    }

    private static void registerMemberMenu() {
        System.out.println("\n--- Register New Member ---");

        System.out.print("Enter Student ID: ");
        String id = sc.nextLine().trim();

        if (library.findMember(id) != null) {
            System.out.println("Student ID already exists. Please use a unique ID.");
            return;
        }

        System.out.print("Enter Member Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter Membership Type (Regular / Premium / Guest): ");
        String type = sc.nextLine().trim();

        if (!type.equalsIgnoreCase("Regular") && !type.equalsIgnoreCase("Premium") && !type.equalsIgnoreCase("Guest")) {
            System.out.println("Invalid membership type. Defaulting to Regular.");
            type = "Regular";
        }

        Member newMember = new Member(id, name, type);
        library.registerMember(newMember);
        System.out.println("Member registered successfully!");
    }

    private static int readInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            sc.next();
        }
        int val = sc.nextInt();
        sc.nextLine(); // consume newline left by nextInt()
        return val;
    }
}

// Library Class 
class Library {
    private ArrayList<Book> books = new ArrayList<>();
    private ArrayList<Member> members = new ArrayList<>();
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private int nextTransactionID = 1001;

    public void addBook(Book book) {
        if (book != null) {
            books.add(book);
        }
    }

    public void registerMember(Member member) {
        if (member != null) {
            members.add(member);
        }
    }

    public Book findBook(String ISBN) {
        for (Book b : books) {
            if (b.getISBN().equalsIgnoreCase(ISBN)) {
                return b;
            }
        }
        return null;
    }

    public Member findMember(String id) {
        for (Member m : members) {
            if (m.getMemberID().equalsIgnoreCase(id)) {
                return m;
            }
        }
        return null;
    }
    public boolean removeBook(String ISBN) {
		return false;  }
    public boolean removeMember(int memberID) {
		return false;  }

    // Transaction class  getters:
    public int getTransactionID() { return getTransactionID(); }
    public Member getMember() { return findMember(null); }
    public Book getBook() { return findBook(null); }
    public LocalDate getBorrowDate() { return getBorrowDate(); }
    public LocalDate getReturnDate() { return getReturnDate(); }

    public boolean borrowBook(String memberID, String ISBN) {
        Member m = findMember(memberID);
        Book b = findBook(ISBN);

        if (m != null && b != null) {
            if (m.addBook(b)) {
                transactions.add(new Transaction(nextTransactionID++, m, b, LocalDate.now()));
                return true;
            }
        }
        return false;
    }

    public double returnBook(String memberID, String ISBN, int daysOverdue) {
        Member m = findMember(memberID);
        Book b = findBook(ISBN);

        if (m != null && b != null) {
            if (m.removeBook(b)) {
                return m.calculateLateFee1(b, daysOverdue);
            }
        }
        return -1;
    }

    public void displayBooks() {
        System.out.println("\n--- Available Books ---");
        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }
        for (Book b : books) {
            System.out.println(b);
        }
    }

    public void displayMembers() {
        System.out.println("\n--- Registered Members ---");
        if (members.isEmpty()) {
            System.out.println("No members registered.");
            return;
        }
        for (Member m : members) {
            System.out.println(m);
        }
    }

    public void displayTransactions() {
        System.out.println("\n--- Transactions ---");
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        for (Transaction t : transactions) {
            System.out.println(t);
            System.out.println("----------------------");
        }
    }
}

// Transaction Class 
class Transaction {
    private int transactionID;
    private Member member;
    private Book book;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    public Transaction(int transactionID, Member member, Book book, LocalDate borrowDate) {
        this.transactionID = transactionID;
        this.member = member;
        this.book = book;
        this.borrowDate = borrowDate;
        this.returnDate = calculateDueDate();
    }

    public LocalDate calculateDueDate() {
        String type = member.getMembershipType().toLowerCase();
        if (type.equals("premium")) {
            return borrowDate.plusWeeks(4);
        } else if (type.equals("guest")) {
            return borrowDate.plusWeeks(1);
        } else {
            return borrowDate.plusWeeks(2);
        }
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(returnDate);
    }

    public int getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return (int) returnDate.until(LocalDate.now()).getDays();
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "Transaction ID: " + transactionID + "\n" +
                member.toString() + "\n" +
                book.toString() + "\n" +
                "Borrowed On: " + borrowDate.format(fmt) + " | Due On: " + returnDate.format(fmt) + " | Status: " + (isOverdue() ? "OVERDUE" : "ACTIVE");
    }
}

// Member Class 
class Member {
    private String memberID;
    private String name;
    private String membershipType;
    private ArrayList<Book> borrowedBooks = new ArrayList<>();

    public Member() {
        this("", "", "Regular");
    }

    public Member(String memberID, String name, String membershipType) {
        this.memberID = memberID;
        this.name = name;
        this.membershipType = membershipType;
    }

    public String getMemberID() {
        return memberID;
    }

    public String getName() {
        return name;
    }
    public double calculateLateFee1(Book book, int daysOverdue) {
		return daysOverdue;
        // existing implementation
    }

    public String getMembershipType() {
        return membershipType;
    }

    public int getBorrowingLimit() {
        String type = membershipType.toLowerCase();
        if (type.equals("premium")) {
            return 5;
        } else if (type.equals("guest")) {
            return 1;
        } else {
            return 3;
        }
    }

    public boolean addBook(Book book) {
        if (book == null) {
            return false;
        }
        if (borrowedBooks.contains(book)) {
            return false;
            
        }
        if (book.isAlreadyBorrowedBy(memberID)) {
            System.out.println("This book is already borrowed by you.");
            return false;
        }
        if (borrowedBooks.size() >= getBorrowingLimit()) {
            return false;
        }
        borrowedBooks.add(book);
        return true;
    }

    public boolean removeBook(Book book) {
        return borrowedBooks.remove(book);
    }

    public double calculateLateFee(Book book, int daysOverdue) {
        if (book == null || daysOverdue <= 0) {
            return 0.0;
        }
        double fee = book.getPrice() * 0.05 * daysOverdue;
        return Math.round(fee * 100.0) / 100.0;
    }

    public ArrayList<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public boolean canBorrowMore() {
        return borrowedBooks.size() < getBorrowingLimit();
    }

 
    public String toString() {
        return "ID: " + memberID + " | Name: " + name + " | Membership: " + membershipType + " | Borrowed: " + borrowedBooks.size() + "/" + getBorrowingLimit();
    }
}

// Book Class 
class Book {
    private String ISBN;
    private String title;
    private String author;
    private String category;
    private double price;

    public Book() {
        ISBN = "";
        title = "";
        author = "";
        category = "";
        price = 0.0;
    }

    public boolean isAlreadyBorrowedBy(String memberID) {
		// TODO  method stub
		return false;
	}

	public Book(String ISBN, String title, String author, String category, double price) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.category = category;
        this.price = price;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    
    public String toString() {
        return "ISBN: " + ISBN + " | Title: " + title + " | Author: " + author + " | Category: " + category + String.format(" | Price: $%.2f", price);
    }

   
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Book))
            return false;
        Book other = (Book) obj;
        return ISBN.equalsIgnoreCase(other.ISBN);
    }
}
