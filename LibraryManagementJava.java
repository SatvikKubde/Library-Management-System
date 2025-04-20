// Java Version of Library Management System ✨📚

import java.io.*;
import java.util.*;
import java.text.*;

// --------------------------- 📖 Book Class ---------------------------
class Book {
    String title;
    String author;
    int bookId;
    int copies;

    Book(String t, String a, int id, int c) {
        title = t;
        author = a;
        bookId = id;
        copies = c;
    }

    boolean isAvailable() {
        return copies > 0;
    }

    void borrowBook() throws Exception {
        if (!isAvailable()) throw new Exception("❌ Book not available!");
        copies--;
    }

    void returnBook() {
        copies++;
    }

    void displayBook() {
        System.out.println("📘 ID: " + bookId + " | 📕 Title: " + title + " | ✍️ Author: " + author + " | 📦 Copies Available: " + copies);
    }
}

// --------------------------- 👤 Base User Class ---------------------------
abstract class User {
    protected String name;
    protected int userId;

    User(String n, int id) {
        name = n;
        userId = id;
    }

    void showUser() {
        System.out.println("🆔 User ID: " + userId + " | 👤 Name: " + name);
    }

    int getId() { return userId; }

    abstract String getType();

    String getName() { return name; }
}

class Student extends User {
    Student(String n, int id) {
        super(n, id);
    }

    @Override
    String getType() {
        return "Student 🎓";
    }
}

class Librarian extends User {
    Librarian(String n, int id) {
        super(n, id);
    }

    @Override
    String getType() {
        return "Librarian 📚";
    }
}

// --------------------------- 🏛️ Library Class ---------------------------
final class Library {
    private final List<Book> books = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final Map<Integer, Set<Integer>> borrowMap = new HashMap<>();

    Library() {
        loadBooksFromFile();
        loadUsersFromFile();
        loadBorrowMapFromFile();
    }

    void loadBooksFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("; ");
                int id = Integer.parseInt(parts[0].split("=")[1]);
                String title = parts[1].split("=")[1];
                String author = parts[2].split("=")[1];
                int copies = Integer.parseInt(parts[3].split("=")[1]);
                books.add(new Book(title, author, id, copies));
            }
        } catch (IOException e) {
            System.err.println("Error loading books from file: " + e.getMessage());
        }
    }

    void saveBooksToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("books.txt"))) {
            for (Book b : books) {
                pw.println("Book ID=" + b.bookId + "; Title=" + b.title + "; Author=" + b.author + "; Copies=" + b.copies);
            }
        } catch (IOException e) {
            System.err.println("Error saving books: " + e.getMessage());
        }
    }

    void loadUsersFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("; ");
                int id = Integer.parseInt(parts[0].split("=")[1]);
                String name = parts[1].split("=")[1];
                String type = parts[2].split("=")[1];
                if (type.contains("Student")) users.add(new Student(name, id));
                else users.add(new Librarian(name, id));
            }
        } catch (IOException e) {
            System.err.println("Error loading users from file: " + e.getMessage());
        }
    }

    void saveUsersToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("users.txt"))) {
            for (User u : users) {
                pw.println("User ID=" + u.getId() + "; Name=" + u.getName() + "; Type=" + u.getType());
            }
        } catch (IOException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
    }

    void saveBorrowMapToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("borrow_map.txt"))) {
            for (Map.Entry<Integer, Set<Integer>> entry : borrowMap.entrySet()) {
                int userId = entry.getKey();
                for (int bookId : entry.getValue()) {
                    pw.println(userId + ":" + bookId);
                }
            }
        } catch (IOException ignored) {}
    }

    void loadBorrowMapFromFile() {
        File file = new File("borrow_map.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length != 2) continue;
                int userId = Integer.parseInt(parts[0]);
                int bookId = Integer.parseInt(parts[1]);
                borrowMap.putIfAbsent(userId, new HashSet<>());
                borrowMap.get(userId).add(bookId);
            }
        }catch (IOException e) {
                System.err.println("Failed to load/save borrowMap: " + e.getMessage());
            }
        }

    void addBook() {
        Scanner sc = new Scanner(System.in);
        System.out.print("📘 Enter Book ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.print("📕 Enter Title: ");
        String title = sc.nextLine();
        System.out.print("✍️ Enter Author: ");
        String author = sc.nextLine();

        for (Book b : books) {
            if (b.bookId == id) {
                if (b.title.equalsIgnoreCase(title)) {
                    System.out.print("🔁 Book already exists with same ID and title. How many copies to increase? ");
                    int moreCopies = sc.nextInt();
                    b.copies += moreCopies;
                    saveBooksToFile();
                    System.out.println("✅ Copies updated successfully!");
                } else {
                    System.out.println("⚠️ Book ID already exists!");
                }
                return;
            } else if (b.title.equalsIgnoreCase(title)) {
                System.out.println("⚠️ Book already exists with Title: \"" + b.title + "\" and Book ID: " + b.bookId);
                return;
            }
        }

        System.out.print("📦 Enter No. of Copies: ");
        int copies = sc.nextInt();
        books.add(new Book(title, author, id, copies));
        saveBooksToFile();
        System.out.println("✅ Book added successfully!");
    }

    void displayBooks() {
        if (books.isEmpty()) {
            System.out.println("📭 No books in library.");
            return;
        }
        System.out.println("\n📚 Available Books:");
        for (Book b : books) b.displayBook();
    }

    void addUser() {
        Scanner sc = new Scanner(System.in);
        System.out.print("🆔 Enter User ID: ");
        int id = sc.nextInt();

        for (User u : users) {
            if (u.userId == id) {
                System.out.print("🔁 User ID already exists! Do you want to retry with a different ID? (yes/no): ");
                sc.nextLine(); // consume newline
                String response = sc.nextLine();
                if (response.equalsIgnoreCase("yes")) {
                    addUser(); // recursively retry
                }
                return;
            }
        }

        sc.nextLine(); // consume newline
        System.out.print("👤 Enter Name: ");
        String name = sc.nextLine();

        for (User u : users) {
            if (u.name.equalsIgnoreCase(name)) {
                System.out.println("⚠️ User already exists with ID: " + u.userId);
                return;
            }
        }

        System.out.print("🎓 Are you a Student or a Librarian? ");
        String type = sc.nextLine();
        if (type.equalsIgnoreCase("student")) {
            users.add(new Student(name, id));
        } else if (type.equalsIgnoreCase("librarian")) {
            users.add(new Librarian(name, id));
        } else {
            System.out.println("❌ Invalid user type!");
            return;
        }

        saveUsersToFile();
        System.out.println("✅ User added successfully!");
    }


    User findUser(int id) throws Exception {
        for (User u : users)
            if (u.getId() == id) return u;
        throw new Exception("❌ User not found!");
    }

    Book findBook(int id) throws Exception {
        for (Book b : books)
            if (b.bookId == id) return b;
        throw new Exception("❌ Book not found!");
    }

    void logTransaction(String action, User user, Book book) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("borrow_log.txt", true))) {
            pw.println("[" + action + "] UserID: " + user.getId() + " | Name: " + user.getName() + " | BookID: " + book.bookId + " | Title: " + book.title);
        } catch (IOException ignored) {}
    }

    void borrowBook() {
        Scanner sc = new Scanner(System.in);
        System.out.print("👤 Enter User ID: ");
        int uid = sc.nextInt();
        System.out.print("📘 Enter Book ID to borrow: ");
        int bid = sc.nextInt();
        try {
            User user = findUser(uid);
            Book book = findBook(bid);
            if (borrowMap.containsKey(uid) && borrowMap.get(uid).contains(bid)) {
                System.out.println("⚠️ You have already borrowed this book!"); return;
            }
            book.borrowBook();
            borrowMap.putIfAbsent(uid, new HashSet<>());
            borrowMap.get(uid).add(bid);
            saveBorrowMapToFile();
            saveBooksToFile();
            logTransaction("Borrow", user, book);

            // Save due date
            try (PrintWriter pw = new PrintWriter(new FileWriter("borrow_due.txt", true))) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 7); // 7-day due
                String dueDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
                pw.println(uid + ":" + bid + ":" + dueDate);
            }

            System.out.println("✅ " + user.getType() + " " + uid + " borrowed book ID " + bid + ". Due in 7 days!");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    void returnBook() {
        Scanner sc = new Scanner(System.in);
        System.out.print("👤 Enter User ID: ");
        int uid = sc.nextInt();
        System.out.print("📘 Enter Book ID to return: ");
        int bid = sc.nextInt();
        try {
            User user = findUser(uid);
            Book book = findBook(bid);
            if (!borrowMap.containsKey(uid) || !borrowMap.get(uid).contains(bid)) {
                System.out.println("⚠️ You did not borrow this book."); return;
            }
            book.returnBook();
            borrowMap.get(uid).remove(bid);
            saveBorrowMapToFile();
            saveBooksToFile();
            logTransaction("Return", user, book);

            // Fine calculation
            File dueFile = new File("borrow_due.txt");
            File temp = new File("temp.txt");

            try (BufferedReader br = new BufferedReader(new FileReader(dueFile));
                 PrintWriter pw = new PrintWriter(new FileWriter(temp))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(":");
                    int id = Integer.parseInt(parts[0]);
                    int bookId = Integer.parseInt(parts[1]);
                    String dueDate = parts[2];

                    if (id == uid && bookId == bid) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date due = sdf.parse(dueDate);
                        Date today = new Date();
                        long diff = today.getTime() - due.getTime();
                        long daysLate = diff / (1000 * 60 * 60 * 24);
                        if (daysLate > 0) {
                            long fine = daysLate * 10;
                            System.out.println("💰 Book returned late by " + daysLate + " days. Fine: ₹" + fine);
                        } else {
                            System.out.println("✅ Book returned on time. No fine.");
                        }
                    } else {
                        pw.println(line);
                    }
                }
            }
            dueFile.delete();
            temp.renameTo(dueFile);

            System.out.println("✅ " + user.getType() + " " + uid + " returned book ID " + bid);
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    void showBorrowHistory() {
        try (BufferedReader br = new BufferedReader(new FileReader("borrow_log.txt"))) {
            String line;
            System.out.println("\n📜 Borrow History:");
            while ((line = br.readLine()) != null) {
                System.out.println("📝 " + line);
            }
        } catch (IOException ignored) {}
    }

    void showUsers() {
        if (users.isEmpty()) {
            System.out.println("📭 No users in system.");
            return;
        }
        for (User u : users) {
            u.showUser();
            System.out.println("🎭 Type: " + u.getType() + "\n");
        }
    }

    void searchBook() {
        Scanner sc = new Scanner(System.in);
        System.out.print("🔍 Enter title or author keyword to search: ");
        String keyword = sc.nextLine().toLowerCase();
        boolean found = false;

        for (Book b : books) {
            if (b.title.toLowerCase().contains(keyword) || b.author.toLowerCase().contains(keyword)) {
                b.displayBook();
                found = true;
            }
        }

        if (!found) {
            System.out.println("❌ No books found matching \"" + keyword + "\".");
        }
    }

}

// --------------------------- 🚀 Main ---------------------------
public class LibraryManagementJava {
    public static void main(String[] args) {
        Library lib = new Library();
        Scanner sc = new Scanner(System.in);
        int choice;
        System.out.println("🌟 === Welcome to the Library Management System === 🌟");

        do {
            System.out.println("\n📋 ------ MENU ------");
            System.out.println("1️⃣. Add Book");
            System.out.println("2️⃣. Display Books");
            System.out.println("3️⃣. Add User");
            System.out.println("4️⃣. Show Users");
            System.out.println("5️⃣. Borrow Book");
            System.out.println("6️⃣. Return Book");
            System.out.println("7️⃣. Show Borrow History");
            System.out.println("8️⃣. Search Book");
            System.out.println("9️⃣. Exit");

            while (true) {
                System.out.print("👉 Enter your choice: ");
                try {
                    choice = sc.nextInt();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("❌ Invalid input! Please enter a number (1 to 9).");
                    sc.nextLine();
                }
            }

            switch (choice) {
                case 1 -> lib.addBook();
                case 2 -> lib.displayBooks();
                case 3 -> lib.addUser();
                case 4 -> lib.showUsers();
                case 5 -> lib.borrowBook();
                case 6 -> lib.returnBook();
                case 7 -> lib.showBorrowHistory();
                case 8 -> lib.searchBook();
                case 9 -> System.out.println("👋 Thank you! See you again!");
                default -> System.out.println("❌ Invalid choice! Try again.");
            }

        } while (choice != 9);

        sc.close();
    }
}
