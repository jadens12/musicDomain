// Used for "Listening" to songs or albums

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Listen {
    
    public static void listenSong(Connection conn, Scanner scanner, String song, String username) throws SQLException {
        int sid = getSongID(conn, song);
        if (sid != -1){
            PreparedStatement checkListen = conn.prepareStatement("SELECT username FROM user_song WHERE sid = ? AND username = ?");
            checkListen.setInt(1, sid);
            checkListen.setString(2, username);
            ResultSet rs = checkListen.executeQuery();
            if (!rs.next()) {
                PreparedStatement addListen = conn.prepareStatement("INSERT INTO user_song VALUES (?, ?)");
                addListen.setInt(1, sid);
                addListen.setString(2, username);
                addListen.executeUpdate();
            }
            System.out.println("You listened to the song " + song + "!");
        }
        else {
            System.out.println("That is not a valid song name!");
        }
    }

    public static void listenAlbum(Connection conn, Scanner scanner, String album, String username) throws SQLException {
        int aid = getAlbumID(conn, album);
        if (aid != -1){
            PreparedStatement getAlbumSongs = conn.prepareStatement("SELECT sid FROM album_song WHERE aid = ?");
            getAlbumSongs.setInt(1, aid);
            ResultSet rs = getAlbumSongs.executeQuery();
            ArrayList<String> songNames = new ArrayList<>();
            while (rs.next()){
                int sid = rs.getInt("sid");
                PreparedStatement getSongName = conn.prepareStatement("SELECT title FROM song WHERE sid = ?");
                getSongName.setInt(1, sid);
                ResultSet rs2 = getSongName.executeQuery();
                if (rs2.next()){
                    songNames.add(rs2.getString("title"));
                }
            }
            System.out.println("You are now listening to the album " + album + ":");
            for (int i = 0; i < songNames.size(); i++){
                listenSong(conn, scanner, songNames.get(i), username);
            }
        }
        else {
            System.out.println("That is not a valid album name!");
        }
    }

    private static int getAlbumID(Connection conn, String album) throws SQLException {
        PreparedStatement getAlbumID = conn.prepareStatement("SELECT aid FROM album WHERE name = ?");
        getAlbumID.setString(1, album);
        ResultSet rs = getAlbumID.executeQuery();
        int albumID = -1;
        while (rs.next())
        {
            albumID = Integer.parseInt(rs.getString("aid"));
        }
        if (albumID != -1)
        {
            return albumID;
        }
        else return -1;
    }
    private static int getSongID(Connection conn, String song) throws SQLException {
        PreparedStatement getSongID = conn.prepareStatement("SELECT sid FROM song WHERE title = ?");
        getSongID.setString(1, song);
        ResultSet rs = getSongID.executeQuery();
        int songID = -1;
        while (rs.next())
        {
            songID = Integer.parseInt(rs.getString("sid"));
        }
        if (songID != -1)
        {
            return songID;
        }
        else return -1;
    }
}
