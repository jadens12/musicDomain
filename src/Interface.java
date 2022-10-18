import java.sql.*;
import java.util.Scanner; 
import java.util.InputMismatchException;

public class Interface {

    Connection conn;
    Statement st;
    Scanner scanner;
    
    public Interface(Connection conn) throws SQLException {
        this.conn = conn;
        this.st = this.conn.createStatement();
        this.scanner = new Scanner(System.in);
    }
    
    public void initialPrompt() throws SQLException {
        System.out.println("Enter the number of your choice:\n"
         + "1) Login with username/password\n"
         + "2) Create a new account");
        try {
            int i = scanner.nextInt();
            scanner.nextLine(); // throw away "\n"

            switch (i) {
                case 1:
                    login();
                    break;
                case 2:
                    accountCreation();
                    break;
                default:
                    System.out.println("Nubmer entered is not a valid option!");
                    break;
            }
        }
        catch (InputMismatchException e) {
            System.out.println("Please enter a number.");
            initialPrompt();
        }
    }

    public void login() throws SQLException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        PreparedStatement pst = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
        pst.setString(1, username);
        pst.setString(2, password);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            java.sql.Date todayDate = new Date(System.currentTimeMillis());
            PreparedStatement pst2 = conn.prepareStatement("UPDATE users SET last_access_date = ? WHERE username = ?");
            pst2.setDate(1, todayDate);
            pst2.setString(2, username);
            pst2.executeUpdate();

            System.out.println("Logged in as " + username + "\n");
            homeScreen();
        }
        else {
            System.out.println("Incorrect login!");
            login();
        }
    }

    public void accountCreation() throws SQLException {
        // check if username taken
        String newUsername;
        while (true) {
            do {
                System.out.print("Create a username: ");
                newUsername = scanner.nextLine();
            } while (newUsername.equals(""));

            PreparedStatement pst = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
            pst.setString(1, newUsername);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                System.out.println("Username is taken!");
            }
            else {
                break;
            }
        }

        String newPassword;
        do {
            System.out.print("Create a password: ");
            newPassword = scanner.nextLine();
        } while (newPassword.equals(""));

        String email;
        do {
            System.out.print("Enter your email: ");
            email = scanner.nextLine();
        } while (email.equals(""));

        String name[] = new String[2];
        do {
            System.out.print("Enter your first and last name: ");
            name = scanner.nextLine().split(" ", 2);
        } while (name[0].equals("") && name[1].equals(""));

        PreparedStatement pst = conn.prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?)");
        pst.setString(1, email);
        pst.setString(2, newUsername);
        pst.setString(3, newPassword);
        pst.setString(4, name[0]);
        pst.setString(5, name[1]);
        java.sql.Date todayDate = new Date(System.currentTimeMillis());
        pst.setDate(6, todayDate);
        pst.setDate(7, todayDate);
        pst.executeUpdate();

        System.out.println("Account '" + newUsername + "' has been created!");
        homeScreen();
    }

    public void homeScreen() {
        // main menu for app...
    }
}
