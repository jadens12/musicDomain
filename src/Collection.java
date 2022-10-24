import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Collection {

    public Collection() {
    }

    public static void createCollection(Connection conn, Scanner scanner, String username) throws SQLException {
        String playlistName;
        do {
            System.out.print("Name of collection: ");
            playlistName = scanner.nextLine();
        } while (playlistName.equals(""));

        int newID = 0;
        ResultSet rs = conn.createStatement().executeQuery("SELECT MAX(pid) FROM playlist");
        while (rs.next()) {
            newID = rs.getInt(1) +  1;
        }

        PreparedStatement pst = conn.prepareStatement("INSERT INTO playlist VALUES (?, ?, ?)");
        pst.setInt(1, newID);
        pst.setString(2, username);
        pst.setString(3, playlistName);
        pst.executeUpdate();

        System.out.println("Collection '" + playlistName + "' has been created!");
    }

    public static void displayAll(Connection conn, Scanner scanner, String username) throws SQLException {
        ArrayList<String> collectionNames = new ArrayList<>();

        PreparedStatement pst = conn.prepareStatement("SELECT name FROM playlist WHERE username = ?");
        pst.setString(1, username);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            collectionNames.add(rs.getString(1));
        }

        System.out.println("\nMy collections:");
        for (String name : collectionNames) {
            System.out.println(" - " + name);
        }
    }

    public static void renameCollection(Connection conn, Scanner scanner, String username) throws SQLException {
        System.out.print("\nEnter collection name: ");
        String collectionName = scanner.nextLine();
        PreparedStatement collectionQuery = conn.prepareStatement("SELECT pid FROM playlist WHERE name = ? and username = ?");
        collectionQuery.setString(1, collectionName);
        collectionQuery.setString(2, username);
        ResultSet collectionResult = collectionQuery.executeQuery();
        if (!collectionResult.next()) {
            System.out.println("Collection not found.");
            return;
        }
        int pid = collectionResult.getInt("pid");

        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();

        PreparedStatement nameQuery = conn.prepareStatement("SELECT pid FROM playlist WHERE name = ? and username = ?");
        nameQuery.setString(1, newName);
        nameQuery.setString(2, username);
        ResultSet nameResult = nameQuery.executeQuery();
        if (nameResult.next()) {
            System.out.println("There is already a collection with that name!");
        }
        else {
            PreparedStatement cNameUpdate = conn.prepareStatement("UPDATE playlist SET name = ? WHERE pid = ?");
            cNameUpdate.setString(1, newName);
            cNameUpdate.setInt(2, pid);
            cNameUpdate.executeUpdate();

            System.out.print("Collection renamed!");
        }
    }

    public static void deleteCollection(Connection conn, Scanner scanner, String username) throws SQLException {
        System.out.print("\nEnter collection name: ");
        String collectionName = scanner.nextLine();
        PreparedStatement collectionQuery = conn.prepareStatement("SELECT pid FROM playlist WHERE name = ? and username = ?");
        collectionQuery.setString(1, collectionName);
        collectionQuery.setString(2, username);
        ResultSet collectionResult = collectionQuery.executeQuery();
        if (!collectionResult.next()) {
            System.out.println("Collection not found.");
            return;
        }
        int pid = collectionResult.getInt("pid");

        while (true) {
            System.out.print("Are you sure you want to delete this collection? (Y/N): ");
            String confirm = scanner.nextLine();

            if (confirm.equals("Y")) {
                PreparedStatement collectionDelete = conn.prepareStatement("DELETE FROM playlist WHERE pid = ?");
                collectionDelete.setInt(1, pid);
                collectionDelete.executeUpdate();

                System.out.println("Collection deleted!");
                return;
            }
            else if (confirm.equals("N")) {
                return;
            }
            else {
                System.out.println("Please enter either Y or N to confirm.");
            }
        }
    }

    public static void addSong(Connection conn, Scanner scanner, String username) throws SQLException {
        System.out.println("Enter collection name: ");
        String collectionName = scanner.nextLine();
        PreparedStatement collectionQuery = conn.prepareStatement("SELECT pid FROM playlist WHERE name = ? and username = ?");
        collectionQuery.setString(1, collectionName);
        collectionQuery.setString(2, username);
        ResultSet collectionResult = collectionQuery.executeQuery();
        if (!collectionResult.next()) {
            System.out.println("Collection not found.");
            return;
        }
        int pid = collectionResult.getInt("pid");

        System.out.println("Enter song name: ");
        String songName = scanner.nextLine();
        PreparedStatement songQuery = conn.prepareStatement("SELECT sid FROM song WHERE title = ?");
        songQuery.setString(1, songName);
        ResultSet songResult = songQuery.executeQuery();
        if (!songResult.next()) {
            System.out.println("Song not found.");
            return;
        }
        int sid = songResult.getInt("sid");

        PreparedStatement insertQuery = conn.prepareStatement("INSERT INTO song_playlist (sid, pid) "
                + "SELECT ?, ? WHERE NOT EXISTS (SELECT * FROM song_playlist WHERE sid = ? and pid = ?)");
        insertQuery.setInt(1, sid);
        insertQuery.setInt(2, pid);
        insertQuery.setInt(3, sid);
        insertQuery.setInt(4, pid);
        if (insertQuery.executeUpdate() == 1) {
            System.out.println(songName + " successfully added to " + collectionName + "!");
        } else {
            System.out.println(songName + " already in " + collectionName);
        }
    }

    public static void addAlbum(Connection conn, Scanner scanner, String username) throws SQLException {
        System.out.println("Enter collection name: ");
        String collectionName = scanner.nextLine();
        PreparedStatement collectionQuery = conn.prepareStatement("SELECT pid FROM playlist WHERE name = ? and username = ?");
        collectionQuery.setString(1, collectionName);
        collectionQuery.setString(2, username);
        ResultSet collectionResult = collectionQuery.executeQuery();
        if (!collectionResult.next()) {
            System.out.println("Collection not found.");
            return;
        }
        int pid = collectionResult.getInt("pid");

        System.out.println("Enter album name: ");
        String albumName = scanner.nextLine();
        PreparedStatement albumQuery = conn.prepareStatement("SELECT aid FROM album WHERE name = ?");
        albumQuery.setString(1, albumName);
        ResultSet albumResult = albumQuery.executeQuery();
        if (!albumResult.next()) {
            System.out.println("Album not found.");
            return;
        }
        int aid = albumResult.getInt("aid");

        PreparedStatement findSongsQuery = conn.prepareStatement("SELECT sid FROM album_song WHERE aid = ?");
        findSongsQuery.setInt(1, aid);
        ResultSet songsResult = findSongsQuery.executeQuery();
        while (songsResult.next()) {
            int sid = songsResult.getInt(1);
            PreparedStatement songQuery = conn.prepareStatement("SELECT title FROM song WHERE sid = ?");
            songQuery.setInt(1, sid);
            ResultSet oneSongResult = songQuery.executeQuery();
            oneSongResult.next();
            String songName = oneSongResult.getString(1);
            PreparedStatement insertQuery = conn.prepareStatement("INSERT INTO song_playlist (sid, pid) "
                    + "SELECT ?, ? WHERE NOT EXISTS (SELECT * FROM song_playlist WHERE sid = ? and pid = ?)");
            insertQuery.setInt(1, sid);
            insertQuery.setInt(2, pid);
            insertQuery.setInt(3, sid);
            insertQuery.setInt(4, pid);
            if (insertQuery.executeUpdate() == 1) {
                System.out.println(songName + " successfully added to " + collectionName + "!");
            } else {
                System.out.println(songName + " already in " + collectionName);
            }
        }
    }

    public static void deleteSong(Connection conn, Scanner scanner, String username) throws SQLException {
        System.out.println("Enter collection name: ");
        String collectionName = scanner.nextLine();
        PreparedStatement collectionQuery = conn.prepareStatement("SELECT pid FROM playlist WHERE name = ? and username = ?");
        collectionQuery.setString(1, collectionName);
        collectionQuery.setString(2, username);
        ResultSet collectionResult = collectionQuery.executeQuery();
        if (!collectionResult.next()) {
            System.out.println("Collection not found.");
            return;
        }
        int pid = collectionResult.getInt("pid");

        System.out.println("Enter song name: ");
        String songName = scanner.nextLine();
        PreparedStatement findSongQuery = conn.prepareStatement("SELECT sid FROM song WHERE title = ?");
        findSongQuery.setString(1, songName);
        ResultSet songResult = findSongQuery.executeQuery();
        if (!songResult.next()) {
            System.out.println("Song not found.");
            return;
        }
        int sid = songResult.getInt("sid");

        PreparedStatement deleteSongQuery = conn.prepareStatement("DELETE FROM song_playlist WHERE sid = ? and pid = ?");
        deleteSongQuery.setInt(1, sid);
        deleteSongQuery.setInt(2, pid);
        if (deleteSongQuery.executeUpdate() == 1) {
            System.out.println(songName + " successfully deleted from " + collectionName + "!");
        } else {
            System.out.println(songName + " not in " + collectionName);
        }
    }

    public static void deleteAlbum(Connection conn, Scanner scanner, String username) throws SQLException {
        System.out.println("Enter collection name: ");
        String collectionName = scanner.nextLine();
        PreparedStatement collectionQuery = conn.prepareStatement("SELECT pid FROM playlist WHERE name = ? and username = ?");
        collectionQuery.setString(1, collectionName);
        collectionQuery.setString(2, username);
        ResultSet collectionResult = collectionQuery.executeQuery();
        if (!collectionResult.next()) {
            System.out.println("Collection not found.");
            return;
        }
        int pid = collectionResult.getInt("pid");

        System.out.println("Enter album name: ");
        String albumName = scanner.nextLine();
        PreparedStatement albumQuery = conn.prepareStatement("SELECT aid FROM album WHERE name = ?");
        albumQuery.setString(1, albumName);
        ResultSet albumResult = albumQuery.executeQuery();
        if (!albumResult.next()) {
            System.out.println("Album not found.");
            return;
        }
        int aid = albumResult.getInt("aid");

        PreparedStatement findSongsQuery = conn.prepareStatement("SELECT sid FROM album_song WHERE aid = ?");
        findSongsQuery.setInt(1, aid);
        ResultSet songsResult = findSongsQuery.executeQuery();
        while (songsResult.next()) {
            int sid = songsResult.getInt(1);
            PreparedStatement songQuery = conn.prepareStatement("SELECT title FROM song WHERE sid = ?");
            songQuery.setInt(1, sid);
            ResultSet oneSongResult = songQuery.executeQuery();
            oneSongResult.next();
            String songName = oneSongResult.getString(1);

            PreparedStatement deleteQuery = conn.prepareStatement("DELETE FROM song_playlist WHERE sid = ? and pid = ?");
            deleteQuery.setInt(1, sid);
            deleteQuery.setInt(2, pid);
            if (deleteQuery.executeUpdate() == 1) {
                System.out.println(songName + " successfully deleted from " + collectionName + "!");
            } else {
                System.out.println(songName + " not in " + collectionName);
            }
        }
    }

    public void getCollection() {
    }

    public void updateCollection() {
    }

    public void deleteCollection() {
    }

}
