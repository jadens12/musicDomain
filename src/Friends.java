import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Friends {

    public static void followFriend(Connection conn, Scanner scanner, String username) throws SQLException {
        System.out.println("Please enter the email of the user you would like to follow.");
        String friendEmail = scanner.next();
        scanner.nextLine();
        boolean matches = friendEmail.matches(Interface.emailRegex);
        if (matches)
        {
            String friendUsername = getFriendUsername(conn, friendEmail);
            if (friendUsername !="")
            {
                PreparedStatement addFollow = conn.prepareStatement("INSERT INTO user_user VALUES (?, ?)");
                addFollow.setString(1, username);
                addFollow.setString(2, friendUsername);
                addFollow.executeUpdate();
                System.out.println("Friend " + friendUsername + " followed!\n");
            }
            else
            {
                System.out.println("User not found!\n");
            }


        }
        else
        {
            System.out.println("Invalid email format.\n");
        }
    }

    public static void unfollowFriend(Connection conn, Scanner scanner, String username) throws SQLException {
        System.out.println("Please enter the email of the user you would like to unfollow.");
        String friendEmail = scanner.next();
        scanner.nextLine();
        boolean matches = friendEmail.matches(Interface.emailRegex);
        if (matches)
        {
            String friendUsername = getFriendUsername(conn, friendEmail);
            if (friendUsername != "")
            {
                PreparedStatement unfollowFriend = conn.prepareStatement("DELETE FROM user_user WHERE (?,?");
                unfollowFriend.setString(1, username);
                unfollowFriend.setString(2, friendUsername);
                unfollowFriend.executeUpdate();
                System.out.println("Friend unfollowed.\n");
            }
            else
            {
                System.out.println("Friend not found!\n");
            }
        }
        else
        {
            System.out.println("Invalid email format.\n");
        }

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
