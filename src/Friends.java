import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Friends {

    public static void followFriend(Connection conn, Scanner scanner, String username) throws SQLException {
        String friendEmail;
        while (true) {
            System.out.print("Please enter the email of the user you would like to follow: ");
            friendEmail = scanner.nextLine();

            if (!friendEmail.matches(Interface.emailRegex))
            {
                System.out.println("Invalid email format!");
                continue;
            }

            break;
        }

        String friendUsername = getFriendUsername(conn, friendEmail);
        if (friendUsername == "")
        {
            System.out.println("User not found!");
            return;
        }

        PreparedStatement checkFollowing = conn.prepareStatement("SELECT username1 FROM user_user WHERE username1 = ? AND username2 = ?");
        checkFollowing.setString(1, username);
        checkFollowing.setString(2, friendUsername);
        ResultSet rs = checkFollowing.executeQuery();
        if (rs.next()) {
            System.out.println("Already following " + friendUsername + "!");
            return;
        }

        PreparedStatement addFollow = conn.prepareStatement("INSERT INTO user_user VALUES (?, ?)");
        addFollow.setString(1, username);
        addFollow.setString(2, friendUsername);
        addFollow.executeUpdate();
        System.out.println("Friend " + friendUsername + " followed!");
    }

    public static void unfollowFriend(Connection conn, Scanner scanner, String username) throws SQLException {
        String friendEmail;
        while (true) {
            System.out.print("Please enter the email of the user you would like to unfollow: ");
            friendEmail = scanner.nextLine();

            if (!friendEmail.matches(Interface.emailRegex))
            {
                System.out.println("Invalid email format.");
                continue;
            }

            break;
        }

        String friendUsername = getFriendUsername(conn, friendEmail);
        if (friendUsername == "") {
            System.out.println("Friend not found!");
            return;
        }

        PreparedStatement checkFollowing = conn.prepareStatement("SELECT username1 FROM user_user WHERE username1 = ? AND username2 = ?");
        checkFollowing.setString(1, username);
        checkFollowing.setString(2, friendUsername);
        ResultSet rs = checkFollowing.executeQuery();
        if (!rs.next()) {
            System.out.println("No such friend with email '" + friendEmail + "'!");
            return;
        }
        
        PreparedStatement unfollowFriend = conn.prepareStatement("DELETE FROM user_user WHERE username1 = ? AND username2 = ?");
        unfollowFriend.setString(1, username);
        unfollowFriend.setString(2, friendUsername);
        unfollowFriend.executeUpdate();
        System.out.println("Friend " + friendUsername + " unfollowed.");
    }

    public static void viewFriends(Connection conn, Scanner scanner, String username) throws SQLException
    {
        if (username != "")
        {
            ArrayList<String> friendNames = new ArrayList<>();
            PreparedStatement viewFollowed = conn.prepareStatement("SELECT username2 FROM user_user WHERE username1 = ?");
            viewFollowed.setString(1, username);
            ResultSet rs = viewFollowed.executeQuery();

            while (rs.next())
            {
                friendNames.add(rs.getString("username2"));
            }
            for (String name : friendNames)
            {
                PreparedStatement getFriendEmail = conn.prepareStatement("SELECT username FROM users WHERE email = ?");
                getFriendEmail.setString(1, name);
                ResultSet emails = getFriendEmail.executeQuery();
                while (emails.next())
                {
                    System.out.println(rs.getString("email"));
                }
            }
            System.out.println("\n");
        }
        else
        {
            System.out.println("current user email invalid\n");
        }

    }

    public static String getFriendUsername(Connection conn, String email) throws SQLException {
        PreparedStatement getFriendEmail = conn.prepareStatement("SELECT username FROM users WHERE email = ?");
        getFriendEmail.setString(1, email);
        ResultSet rs = getFriendEmail.executeQuery();
        String friendUsername = "";
        while (rs.next())
        {
            friendUsername = rs.getString("username");
        }
        if (friendUsername != "")
        {
            return friendUsername;
        }
        else return "";
    }
}
