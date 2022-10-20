import java.sql.*;
import java.util.Scanner; 
import java.util.InputMismatchException;

public class Interface {

    Connection conn;
    Statement st;
    Scanner scanner;

    String currentUsername;
    
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
        int hashPass = password.hashCode();

        PreparedStatement pst = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
        pst.setString(1, username);
        pst.setString(2, Integer.toString(hashPass));
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            java.sql.Date todayDate = new Date(System.currentTimeMillis());
            PreparedStatement pst2 = conn.prepareStatement("UPDATE users SET last_access_date = ? WHERE username = ?");
            pst2.setDate(1, todayDate);
            pst2.setString(2, username);
            pst2.executeUpdate();

            System.out.println("Logged in as " + username + "\n");
            currentUsername = username;
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
        System.out.println("Enter the number of your choice:\n"
         + "1) Create Collection\n"
         + "2) My Collections\n"
         + "3) Search\n"
         + "4) Friends\n"
         + "5) Listen\n"
         + "6) Quit");
        try {
            int i = scanner.nextInt();
            scanner.nextLine(); // throw away "\n"

            switch (i) {
                case 1:
                    createCollection();
                    break;
                case 2:
                    // view collections stuff?
                    // offer possibilities to play, rename, delete
                    // add and delete songs from collections
                    System.out.println("1) View collection\n"
                    + "2) Add song to collection\n"
                    + "3) Add album to collection\n"
                    + "4) Delete song from collection\n"
                    + "5) Delete album from collection");
                    int choice = scanner.nextInt();
                    scanner.nextLine();
                    switch (choice) {
                        case 1:
                            // do stuff
                            break;
                        case 2:
                            Collection.addSong(conn, scanner, currentUsername);
                            break;
                        case 3:
                            Collection.addAlbum(conn, scanner, currentUsername);
                            break;
                        case 4:
                            Collection.deleteSong(conn, scanner, currentUsername);
                            break;
                        case 5:
                            Collection.deleteAlbum(conn, scanner, currentUsername);
                            break;
                        default:
                            //do stuff
                    }
                    break;
                case 3:
                    // all search stuff: by song, by artist, by album, by genre
                    // allow users to add song or album to collection?
                    // also allow user to play song? i guess? not sure...
                case 4:
                    // friend stuff
                case 5:
                    System.out.println("1) Listen to a song\n" + "2) Listen to an album");
                    int listen = scanner.nextInt();
                    switch (listen) {
                        case 1:
                            System.out.println("Listening to a song!");
                            break;
                        case 2:
                            System.out.println("Listening to an album!");
                            break;
                        default:
                            System.out.println("Number entered is not a valid option!");
                            break;
                    }
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Number entered is not a valid option!");
                    break;
            }
        }
        catch (InputMismatchException e) {
            System.out.println("Please enter a number.");
            homeScreen();
        }
        }
    }

    public void createCollection() throws SQLException {
        String playlistName;
        do {
            System.out.print("Name of collection: ");
            playlistName = scanner.nextLine();
        } while (playlistName.equals(""));

        int newID = 0;
        ResultSet rs = st.executeQuery("SELECT COUNT(pid) FROM playlist");
        while (rs.next()) {
            newID = rs.getInt(1) +  1;
        }

        PreparedStatement pst = conn.prepareStatement("INSERT INTO playlist VALUES (?, ?, ?)");
        pst.setInt(1, newID);
        pst.setString(2, currentUsername);
        pst.setString(3, playlistName);
        pst.executeUpdate();

        System.out.println("Collection '" + playlistName + "' has been created!");
    }
}
