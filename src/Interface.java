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
                    // account creation...
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
        String user = scanner.nextLine();
        System.out.print("Enter password: ");
        String pass = scanner.nextLine();

        PreparedStatement pst = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
        pst.setString(1, user);
        pst.setString(2, pass);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            System.out.println("Logged in as " + user + "\n");
            // set access date?
            homeScreen();
        }
        else {
            System.out.println("Incorrect login!");
            login();
        }
    }

    public void homeScreen() {
        // main menu for app...
    }
}
