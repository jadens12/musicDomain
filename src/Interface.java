import java.sql.*;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Interface {

    final static String emailRegex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
    
    Connection conn;
    Scanner scanner;

    String currentUsername;

    Collection myCollections;
    Search search;
    MostPopular popular;
    
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

            PreparedStatement pst2 = conn.prepareStatement("SELECT username FROM users WHERE username = ? AND password = ?");
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
            search = new Search(conn, scanner);
            popular = new MostPopular(conn, scanner, username);
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

            PreparedStatement pst = conn.prepareStatement("SELECT username FROM users WHERE username = ?");
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

        // check if email taken
        String email;
        while (true) {
            System.out.print("Enter your email: ");
            email = scanner.nextLine();

            if (!email.matches(emailRegex)) {
                System.out.println("Please enter a valid email!");
                continue;
            }

            PreparedStatement pst = conn.prepareStatement("SELECT username FROM users WHERE email = ?");
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                System.out.println("Email already associated with another user!");
            }
            else {
                break;
            }
        }

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
            + "5) Popular songs\n"
            + "6) My Profile\n"
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
                    search();
                    break;
                case 3:
                    friendMenu();
                    break;
                case 4:
                    listen();
                    break;
                case 5:
                    popularMenu();
                    break;
                case 6:
                    showProfile();
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
            + "3) Listen to a collection\n"
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
                    Listen.listenSong(conn, scanner, songName, this.currentUsername);
                    break;
                case 2:
                    System.out.print("Enter album name: ");
                    String albumName = scanner.nextLine();
                    Listen.listenAlbum(conn, scanner, albumName, this.currentUsername);
                    break;
                case 3:
                    System.out.print("Enter collection name: ");
                    String collectionName = scanner.nextLine();
                    Listen.listenCollection(conn, scanner, collectionName, this.currentUsername);
                    break;
                default:
                    System.out.println("Number entered is not a valid option!");
                    break;
            }
        }
    }
    public void friendMenu() throws SQLException {
        while (true)
        {
            System.out.println("\nFriends: \n"
            + "1) Follow a Friend\n"
            + "2) Unfollow a Friend\n"
            + "3) View Friends\n"
            + "0) Back");

            int selection;
            try {
                selection = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Please enter a number.");
                scanner.next();
                continue;
            }

            switch (selection) {
                case 0:
                    return;
                case 1:
                    Friends.followFriend(conn, scanner, this.currentUsername);
                    break;
                case 2:
                    Friends.unfollowFriend(conn, scanner, this.currentUsername);
                    break;
                case 3:
                    Friends.viewFriends(conn, scanner, this.currentUsername);
                    break;
                default:
                    System.out.println("Number entered is not a valid option!");
                    break;
            }
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
    
    public void search() throws SQLException{

        while(true){
            System.out.println("Enter a number of your choice: \n" +
            "1) Search by song name \n" +
            "2) Search by artist name \n" + 
            "3) Search by album name \n" + 
            "4) Search by genre\n" +
            "0) Back");

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
                    search.searchByName();
                    break;
                case 2:
                    search.searchbyArtist();
                    break;
                case 3:
                    search.searchbyAlbum();
                    break;
                case 4:
                    search.searchbyGenre();
                    break;
                default:
                    System.out.println("Number entered is not a valid option!");
                    break;
            }
        }
    }

    public void popularMenu() throws SQLException{
        while(true){
            System.out.println("\nEnter a number of your choice: \n" +
            "1) Top 50 songs in the last month \n" +
            "2) Top 50 songs among my friends\n" +
            "3) Top 5 genres of the month\n" +
            "0) Back");

            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            }
            catch (InputMismatchException e){
                System.out.println("Please enter a number.");
                scanner.next();
                continue;
            }
            switch (choice) {
                case 0:
                    return;
                case 1:
                    popular.popularLastMonth();
                    break;
                case 2:
                    // popular among friends
                    break;
                case 3:
                    popular.popularGenresThisMonth();
                    break;
                default:
                    System.out.println("Number entered is not a valid option!");
                    break;
            }
        }
    }

    public void showProfile() throws SQLException {
        System.out.println("\n" + currentUsername + "'s profile:");

        // number of collections
        PreparedStatement collectionQuery = conn.prepareStatement("SELECT COUNT(pid) FROM playlist WHERE username = ?");
        collectionQuery.setString(1, currentUsername);
        ResultSet collectionResult = collectionQuery.executeQuery();
        if (!collectionResult.next()) {
            System.out.println("Unable to get user data!");
            return;
        }
        int collectionCount = collectionResult.getInt(1);
        while (collectionResult.next()) {}
        System.out.println("\tNumber of collections: " + collectionCount);

        // number of followers
        PreparedStatement followerQuery = conn.prepareStatement("SELECT COUNT(username1) FROM user_user WHERE username2 = ?");
        followerQuery.setString(1, currentUsername);
        ResultSet followerResult = followerQuery.executeQuery();
        if (!followerResult.next()) {
            System.out.println("Unable to get user data!");
            return;
        }
        int followerCount = followerResult.getInt(1);
        System.out.println("\tFollowers: " + followerCount);

        // number of following
        PreparedStatement followingQuery = conn.prepareStatement("SELECT COUNT(username2) FROM user_user WHERE username1 = ?");
        followingQuery.setString(1, currentUsername);
        ResultSet followingResult = followingQuery.executeQuery();
        if (!followingResult.next()) {
            System.out.println("Unable to get user data!");
            return;
        }
        int followingCount = followingResult.getInt(1);
        System.out.println("\tFollowing: " + followingCount);
    }
}
