import java.sql.*;
import java.util.Scanner; 

public class Interface {

    Connection conn;
    Statement st;
    Scanner scanner;
    
    public Interface(Connection conn) throws SQLException {
        this.conn = conn;
        this.st = this.conn.createStatement();
        this.scanner = new Scanner(System.in);
    }
    
    public void InitialPrompt() throws SQLException {
        System.out.println("Enter the number of your choice:\n"
         + "1) Login with username/password\n"
         + "2) Create a new account");
        int i = scanner.nextInt();
        scanner.nextLine(); // throw away "\n"

        if (i == 1) {
            Login();
        }
        else if (i == 2) {
            // account creation...
        }
    }

    public void Login() throws SQLException {
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
            HomeScreen();
        }
        else {
            System.out.println("Incorrect login!");
            Login();
        }
    }

    public void HomeScreen() {
        // main menu for app...
    }
}
