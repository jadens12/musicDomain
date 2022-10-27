import java.sql.*;
import java.util.Scanner; 
import java.util.InputMismatchException;

public class Interface {

    Connection conn;
    Scanner scanner;

    String currentUsername;

    Collection myCollections;
    
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
            String saltValue;

            if (!username.matches("^[a-zA-Z0-9]+$") || !password.matches("^[a-zA-Z0-9]+$")) {
                System.out.println("Invalid login!");
                continue;
            }

            PreparedStatement pst = conn.prepareStatement("SELECT salt FROM users WHERE username = ?");
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (!rs.next()) {
                System.out.println("Incorrect login!");
                continue;
            }
            
            saltValue = rs.getString(1);
            int hashPass = (password + saltValue).hashCode();

            PreparedStatement pst2 = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            pst2.setString(1, username);
            pst2.setString(2, Integer.toString(hashPass));
            ResultSet rs2 = pst2.executeQuery();

            if (!rs2.next()) {
                System.out.println("Incorrect login!");
                continue;
            }
            
            java.sql.Date todayDate = new Date(System.currentTimeMillis());
            PreparedStatement pst3 = conn.prepareStatement("UPDATE users SET last_access_date = ? WHERE username = ?");
            pst3.setDate(1, todayDate);
            pst3.setString(2, username);
            pst3.executeUpdate();

            System.out.println("Logged in as " + username);
            this.currentUsername = username;
            myCollections = new Collection(conn, scanner, currentUsername);
            homeScreen();
            return;
        }
    }

    public void accountCreation() throws SQLException {
        // check if username taken
        String newUsername;
        while (true) {
            System.out.print("Create a username: ");
            newUsername = scanner.nextLine();

            if (!newUsername.matches("^[a-zA-Z0-9]+$")) {
                System.out.println("Usernames can only contain letters or numbers!");
                continue;
            }

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
        while (true) {
            System.out.print("Create a password: ");
            newPassword = scanner.nextLine();

            if (!newPassword.matches("^[a-zA-Z0-9]+$")) {
                System.out.println("Passwords can only contain letters or numbers!");
                continue;
            }

            break;
        }
        String saltValue = getSaltValue();
        int hashPass = (newPassword + saltValue).hashCode();

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

        PreparedStatement pst = conn.prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        pst.setString(1, email);
        pst.setString(2, newUsername);
        pst.setString(3, Integer.toString(hashPass));
        pst.setString(4, name[0]);
        pst.setString(5, name[1]);
        java.sql.Date todayDate = new Date(System.currentTimeMillis());
        pst.setDate(6, todayDate);
        pst.setDate(7, todayDate);
        pst.setString(8, saltValue);
        pst.executeUpdate();

        System.out.println("Account '" + newUsername + "' has been created!");
        this.currentUsername = newUsername;
        myCollections = new Collection(conn, scanner, currentUsername);
        homeScreen();
    }

    public String getSaltValue() {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        + "0123456789"
        + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of 10
        StringBuilder salt = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {
            // generate a random number between
            // 0 to 10 variable length
            int index = (int) (AlphaNumericString.length() * Math.random());

            // add Character one by one in end of sb
            salt.append(AlphaNumericString.charAt(index));
        }

        return salt.toString();
    }

    public void homeScreen() throws SQLException{
        while(true) {
            System.out.println("\nEnter the number of your choice:\n"
            + "1) My Collections\n"
            + "2) Search\n"
            + "3) Friends\n"
            + "4) Listen\n"
            + "0) Quit");

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
                case 0:
                    return;
                case 1:
                    collection();
                    break;
                case 2:
                    // all search stuff: by song, by artist, by album, by genre
                    // allow users to add song or album to collection?
                    // also allow user to play song? i guess? not sure...
                    break;
                case 3:
                    friendMenu();
                    break;
                case 4:
                    listen();
                    break;
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
            + "3) Rename collection\n"
            + "4) Delete collection\n"
            + "5) Add song to collection\n"
            + "6) Add album to collection\n"
            + "7) Delete song from collection\n"
            + "8) Delete album from collection\n"
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
                    myCollections.createCollection();
                    break;
                case 2:
                    myCollections.displayAll();
                    break;
                case 3:
                    myCollections.renameCollection();
                    break;
                case 4:
                    myCollections.deleteCollection();
                    break;
                case 5:
                    myCollections.addSong();
                    break;
                case 6:
                    myCollections.addAlbum();
                    break;
                case 7:
                    myCollections.deleteSong();
                    break;
                case 8:
                    myCollections.deleteAlbum();
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
            + "0) Back");

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
                case 0:
                    return;
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
                default:
                    System.out.println("Number entered is not a valid option!");
                    break;
            }
        }
    }
    public void friendMenu() throws SQLException {
        boolean wantsHome = false;
        while (wantsHome == false)
        {
            System.out.println("Please make a selection: ");
            System.out.println("1) Follow a Friend\t 2) Unfollow a friend");
            System.out.println("3) View Friends\t 4) Return home");
            int selection = Integer.parseInt(scanner.next());
            scanner.nextLine();
            if (selection == 1)
            {
                followFriend();

            }
            else if (selection == 2)
            {
                unfollowFriend();
            }
            else if (selection == 3)
            {
                viewFriends();
            }
            else if (selection == 4)
            {
                wantsHome = true;
            }
            else {
                System.out.println("Invalid input detected.");
            }
        }
        homeScreen();
    }
    public void followFriend() throws SQLException {
        String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
        System.out.println("Please enter the email of the user you would like to follow.");
        String friendEmail = scanner.next();
        scanner.nextLine();
        boolean matches = friendEmail.matches(regex);
        if (matches)
        {
            String currentEmail = this.getCurrentUserEmail();

            if (currentEmail != "")
            {
                PreparedStatement addFollow = conn.prepareStatement("INSERT INTO user_user VALUES (?, ?)");
                addFollow.setString(1, currentEmail);
                addFollow.setString(2, friendEmail);
                addFollow.executeUpdate();
                System.out.println("Friend " + friendEmail + " followed!\n");
            }
        }
        else
        {
            System.out.println("Invalid email format.\n");
        }
    }

    public void unfollowFriend() throws SQLException {
        String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
        System.out.println("Please enter the email of the user you would like to unfollow.");
        String friendEmail = scanner.next();
        scanner.nextLine();
        boolean matches = friendEmail.matches(regex);
        if (matches)
        {
            String currentEmail = this.getCurrentUserEmail();

            if (currentEmail != "")
            {
                PreparedStatement unfollowFriend = conn.prepareStatement("DELETE FROM user_user WHERE (?,?");
                unfollowFriend.setString(1, currentEmail);
                unfollowFriend.setString(2, friendEmail);
                unfollowFriend.executeUpdate();
                System.out.println("Friend unfollowed.\n");
            }
            else
            {
                System.out.println("current user email invalid\n");
            }
        }
        else
        {
            System.out.println("Invalid email format.\n");
        }

    }

    public void viewFriends() throws SQLException
    {
        String currentEmail = this.getCurrentUserEmail();
        if (currentEmail != "")
        {
            PreparedStatement viewFollowed = conn.prepareStatement("SELECT username2 FROM user_user WHERE username1 = ?");
            viewFollowed.setString(1, currentEmail);
            ResultSet rs = viewFollowed.executeQuery();

            while (rs.next())
            {
                System.out.println(rs.getString("username2"));
            }
            System.out.println("\n");
        }
        else
        {
            System.out.println("current user email invalid\n");
        }

    }

    public String getCurrentUserEmail() throws SQLException
    {
        PreparedStatement getUser = conn.prepareStatement("SELECT email FROM users WHERE username = ?");
        getUser.setString(1, this.currentUsername);
        ResultSet rs = getUser.executeQuery();

        String currentEmail ="";
        while(rs.next())
        {
            currentEmail = rs.getString("email");
        }
        return currentEmail;
    }
}
