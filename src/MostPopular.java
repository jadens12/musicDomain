import java.sql.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class MostPopular {
    Connection conn;
    Scanner scanner;
    String username;

    public MostPopular(Connection conn, Scanner scanner, String username) {
        this.conn = conn;
        this.scanner = scanner;
        this.username = username;
    }

    private void printSongs(ResultSet results) throws SQLException {
        System.out.println();
        int i = 1;
        while(results.next()){
            String title = results.getString(1);
            String artist = results.getString(2);
            String album = results.getString(3);
            String length = results.getString(4);
            String listenCount = results.getString(5);

            System.out.println(String.format("%-3s", i++) + "Song name: " + String.format("%-40s", title) 
            + " Artist name: " + String.format("%-30s", artist) 
            + " Album name: " + String.format("%-40s", album) 
            + " Song length: " + length + " seconds"
            + "\tListen count: " + listenCount);
        }
        System.out.println();
    }

    public void popularLastMonth() throws SQLException{
        String queryString = "SELECT song.title, song_artist.artist_name, album.name, song.length, songs_listened.listen_count"
                + " FROM song, song_artist, album, album_song,"
                + " ( SELECT sid, SUM(listens) AS listen_count FROM user_song WHERE listen_date > ? GROUP BY sid ) songs_listened"
                + " WHERE song.sid = songs_listened.sid AND song_artist.sid = song.sid AND album_song.sid = song.sid AND album.aid = album_song.aid"
                + " ORDER BY songs_listened.listen_count DESC, song.title ASC, song_artist.artist_name ASC LIMIT 50";
        PreparedStatement query = conn.prepareStatement(queryString);
        java.sql.Date todayDate = new Date(System.currentTimeMillis() - 2629800000L);
        query.setDate(1, todayDate);

        ResultSet results = query.executeQuery();
        printSongs(results);
    }   

    public void popularAmongFriends() throws SQLException{
        String queryString = "SELECT song.title, song_artist.artist_name, album.name, song.length, songs_listened.listen_count"
                + " FROM song, song_artist, album, album_song,"
                + " ( SELECT sid, SUM(listens) AS listen_count FROM user_song WHERE username IN ( SELECT username2 FROM user_user WHERE username1 = ?) GROUP BY sid ) songs_listened"
                + " WHERE song.sid = songs_listened.sid AND song_artist.sid = song.sid AND album_song.sid = song.sid AND album.aid = album_song.aid"
                + " ORDER BY songs_listened.listen_count DESC, song.title ASC, song_artist.artist_name ASC LIMIT 50";
        PreparedStatement query = conn.prepareStatement(queryString);
        query.setString(1, username);

        ResultSet results = query.executeQuery();
        printSongs(results);
    }

    public void popularGenresThisMonth() throws SQLException {
        String queryString = "SELECT genre_song.genre_name, SUM(songs_listened.listen_count) AS genre_listen_count FROM genre_song, "
        + "( SELECT sid, SUM(listens) AS listen_count FROM user_song WHERE listen_date > ? GROUP BY sid ) songs_listened"
        + " WHERE genre_song.sid = songs_listened.sid GROUP BY genre_song.genre_name ORDER BY genre_listen_count DESC LIMIT 5";
        PreparedStatement query = conn.prepareStatement(queryString);
        
        //create the first date of month
        java.sql.Date todayDate = new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(todayDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        GregorianCalendar gregCal = new GregorianCalendar(year, month, 1);
        java.sql.Date startDate = new Date(gregCal.getTimeInMillis());

        query.setDate(1, startDate);
        ResultSet results = query.executeQuery();

        System.out.println("\nTop genres of the past month: ");
        int rank = 1;
        while (results.next()) {
            System.out.println(rank + ") " + results.getString(1));
            rank++;
        }
    }
}
