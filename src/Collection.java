import java.sql.*;
import java.util.Scanner;


public class Collection {
    

    public Collection() { }

    public static void addSong(Connection conn, Scanner scanner, String username) throws SQLException{
        System.out.println("Enter collection name: ");
        String collectionName = scanner.nextLine();
        PreparedStatement collectionQuery = conn.prepareStatement("SELECT pid FROM playlist WHERE name = ? and username = ?");
        collectionQuery.setString(1, collectionName);
        collectionQuery.setString(2, username);
        ResultSet collectionResult = collectionQuery.executeQuery();
        if(!collectionResult.next()){
            System.out.println("Collection not found.");
            return;
        }
        int pid = collectionResult.getInt("pid");

        System.out.println("Enter song name: ");
        String songName = scanner.nextLine();
        PreparedStatement songQuery = conn.prepareStatement("SELECT sid FROM song WHERE title = ?");
        songQuery.setString(1, songName);
        ResultSet songResult = songQuery.executeQuery();
        if(!songResult.next()){
            System.out.println("Song not found.");
            return;
        }
        int sid = songResult.getInt("sid");

        PreparedStatement insertQuery = conn.prepareStatement("INSERT INTO song_playlist (sid, pid) VALUES (?, ?)");
        insertQuery.setInt(1, sid);
        insertQuery.setInt(2, pid);
        if( insertQuery.executeUpdate() == 1 ){
            System.out.println(songName + " successfully added to " + collectionName + "!");
        }
    }

    public void addAlbum(){ }

    public void deleteSong(){ }

    public void deleteAlbum(){ }

    public void getCollection(){ }

    public void updateCollection() { }

    public void deleteCollection() { }

    
}
