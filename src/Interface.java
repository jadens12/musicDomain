import java.sql.*;
import java.util.Scanner; 
import java.util.InputMismatchException;

public class Interface {

    Connection conn;
    Scanner scanner;

    String currentUsername;
    
    public Interface(Connection conn) throws SQLException {
        this.conn = conn;
        this.scanner = new Scanner(System.in);
    }
    
    public void initialPrompt() throws SQLException {
        while (true) {
            System.out.println("\nEnter the number of your choice:\n"
            + "1) Login with username/password\n"
            + "2) Create a new account");

            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // throw away "\n"
            }
            catch (InputMismatchException e) {
                System.out.println("Please enter a number.");
                scanner.next();
                continue;
            }

            switch (choice) {
                case 1:
                    login();
                    return;
                case 2:
                    accountCreation();
                    return;
                default:
                    System.out.println("Number entered is not a valid option!");
                    break;
            }
        }
    }

    public void login() throws SQLException {
        while (true) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            int hashPass = password.hashCode();

            PreparedStatement pst = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            pst.setString(1, username);
            pst.setString(2, Integer.toString(hashPass));
            ResultSet rs = pst.executeQuery();

            if (!rs.next()) {
                System.out.println("Incorrect login!");
                continue;
            }
            else {
                java.sql.Date todayDate = new Date(System.currentTimeMillis());
                PreparedStatement pst2 = conn.prepareStatement("UPDATE users SET last_access_date = ? WHERE username = ?");
                pst2.setDate(1, todayDate);
                pst2.setString(2, username);
                pst2.executeUpdate();

                System.out.println("Logged in as " + username);
                currentUsername = username;
                homeScreen();
                return;
            }
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
        int hashPass = newPassword.hashCode();

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
        pst.setString(3, Integer.toString(hashPass));
        pst.setString(4, name[0]);
        pst.setString(5, name[1]);
        java.sql.Date todayDate = new Date(System.currentTimeMillis());
        pst.setDate(6, todayDate);
        pst.setDate(7, todayDate);
        pst.executeUpdate();

        System.out.println("Account '" + newUsername + "' has been created!");
        currentUsername = newUsername;
        homeScreen();
    }

    public void homeScreen() throws SQLException{
        while(true) {
            System.out.println("\nEnter the number of your choice:\n"
            + "1) My Collections\n"
            + "2) Search\n"
            + "3) Friends\n"
            + "4) Listen\n"
            + "5) Quit");

            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // throw away "\n"
            }
            catch (InputMismatchException e) {
                System.out.println("Please enter a number.");
                scanner.next();
                continue;
            }

            switch (choice) {
                case 1:
                    collection();
                    break;
                case 2:
                    // all search stuff: by song, by artist, by album, by genre
                    // allow users to add song or album to collection?
                    // also allow user to play song? i guess? not sure...
                    break;
                case 3:
                    // friend stuff
                    break;
                case 4:
                    listen();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Number entered is not a valid option!");
                    break;
            }
        }
    }

    public void collection() throws SQLException {
        while (true) {
            System.out.println("\nMy Collections:\n"
            + "1) Create collection\n"
            + "2) List all collections\n"
            + "3) View collection\n"
            + "4) Rename collection\n"
            + "5) Delete collection\n"
            + "6) Add song to collection\n"
            + "7) Add album to collection\n"
            + "8) Delete song from collection\n"
            + "9) Delete album from collection\n"
            + "0) Back");

            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            }
            catch (InputMismatchException e) {
                System.out.println("Please enter a number.");
                scanner.next();
                continue;
            }
            
            switch (choice) {
                case 0:
                    return;
                case 1:
                    Collection.createCollection(conn, scanner, currentUsername);
                    break;
                case 2:
                    Collection.displayAll(conn, scanner, currentUsername);
                    break;
                case 3:
                    // view collection...
                    break;
                case 4:
                    Collection.renameCollection(conn, scanner, currentUsername);
                    break;
                case 5:
                    Collection.deleteCollection(conn, scanner, currentUsername);
                    break;
                case 6:
                    Collection.addSong(conn, scanner, currentUsername);
                    break;
                case 7:
                    Collection.addAlbum(conn, scanner, currentUsername);
                    break;
                case 8:
                    Collection.deleteSong(conn, scanner, currentUsername);
                    break;
                case 9:
                    Collection.deleteAlbum(conn, scanner, currentUsername);
                    break;
                default:
                    System.out.println("Number entered is not a valid option!");
                    break;
            }
        }
    }

    /// For listening to a song or an album
    /// User types in name of song or name of album
    /// Each song is added to User-Song relation table
    public void listen() throws SQLException {
        while (true) {
            System.out.println("\nListen:\n"
            + "1) Listen to a song\n"
            + "2) Listen to an album\n"
            + "3) Back");

            int listen;
            try {
                listen = scanner.nextInt();
                scanner.nextLine();
            }
            catch (InputMismatchException e) {
                System.out.println("Please enter a number.");
                scanner.next();
                continue;
            }

            switch (listen) {
                case 1:
                    System.out.print("Enter song name: ");
                    String songName = scanner.nextLine();
                    Listen.listenSong(conn, scanner, songName);
                    break;
                case 2:
                    System.out.print("Enter album name: ");
                    String albumName = scanner.nextLine();
                    Listen.listenAlbum(conn, scanner, albumName);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Number entered is not a valid option!");
                    break;
            }
        }
    }
}
