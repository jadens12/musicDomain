import java.sql.*;
import java.util.Scanner;

public class Collection {

    public Collection() {
    }

    public static void addSong(Connection conn, Scanner scanner, String username) throws SQLException {
        System.out.println("Enter collection name: ");
        String collectionName = scanner.nextLine();
        PreparedStatement collectionQuery = conn
                .prepareStatement("SELECT pid FROM playlist WHERE name = ? and username = ?");
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

        PreparedStatement insertQuery = conn.prepareStatement("INSERT INTO song_playlist (sid, pid) VALUES (?, ?)");
        insertQuery.setInt(1, sid);
        insertQuery.setInt(2, pid);
        if (insertQuery.executeUpdate() == 1) {
            System.out.println(songName + " successfully added to " + collectionName + "!");
        } else {
            System.out.println("Error: unable to add " + songName + " to " + collectionName);
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
            PreparedStatement insertQuery = conn.prepareStatement("INSERT INTO song_playlist (sid, pid) VALUES (?, ?)");
            insertQuery.setInt(1, sid);
            insertQuery.setInt(2, pid);
            if (insertQuery.executeUpdate() == 1) {
                System.out.println(songName + " successfully added to " + collectionName + "!");
            } else {
                System.out.println("Error: unable to add " + songName + " to " + collectionName);
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
        if (!songResult.next()){
            System.out.println("Song not found.");
            return;
        }
        int sid = songResult.getInt("sid");

        PreparedStatement deleteSongQuery = conn.prepareStatement("DELETE FROM song_playlist WHERE sid = ? and pid = ?");
        deleteSongQuery.setInt(1, sid);
        deleteSongQuery.setInt(2, pid);
        if (deleteSongQuery.executeUpdate() == 1) {
            System.out.println(songName + "successfully deleted from " + collectionName + "!");
        }
        else {
            System.out.println("Error: unable to delete " + songName + " from " + collectionName);
        }
    }

    public void deleteAlbum() {
    }

    public void getCollection() {
    }

    public void updateCollection() {
    }

    public void deleteCollection() {
    }

}
