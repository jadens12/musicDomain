import java.sql.*;
import java.util.Scanner;

public class Recommendation {
    Connection conn;
    Scanner scanner;
    String username;

    public Recommendation(Connection conn, Scanner scanner, String username) {
        this.conn = conn;
        this.scanner = scanner;
        this.username = username;
    }

    public void recommend() throws SQLException {
        String genreRecString = "SELECT DISTINCT song.title AS title, song_artist.artist_name AS artist, album.name AS album, song.length AS length, random()"
                + " FROM song_artist, album, album_song, song, user_song"
                + " WHERE user_song.username IN ("
                + " SELECT DISTINCT username FROM ("
                + " SELECT user_genre.name AS name, user_genre.listen_count AS listen_count, user_genre.username AS username, row_number() OVER ( PARTITION BY username ORDER BY listen_count DESC) AS rank"
                + " FROM ( SELECT genre_song.genre_name AS name, SUM(user_song.listens) AS listen_count, user_song.username AS username "
                + " FROM user_song, genre_song"
                + " WHERE user_song.sid = genre_song.sid AND user_song.username != ? GROUP BY user_song.username, genre_song.genre_name) AS user_genre) genre_ranks"
                + " WHERE genre_ranks.rank <= 3 AND name IN ("
                + " SELECT genre_song.genre_name FROM user_song, genre_song"
                + " WHERE user_song.username = ? AND user_song.sid = genre_song.sid GROUP BY genre_song.genre_name ORDER BY SUM(user_song.listens) DESC LIMIT 3))"
                + " AND song.sid = user_song.sid AND song_artist.sid = song.sid AND album_song.sid = song.sid AND album.aid = album_song.aid"
                + " AND song.sid NOT IN ( SELECT sid FROM user_song WHERE username = ?)"
                + " ORDER BY random() LIMIT 20";
        
        PreparedStatement genreRecQuery = conn.prepareStatement(genreRecString);
        genreRecQuery.setString(1, username);
        genreRecQuery.setString(2, username);
        genreRecQuery.setString(3, username);
        ResultSet genreRecResults = genreRecQuery.executeQuery();

        int i = 0;
        while(genreRecResults.next()){
            String title = genreRecResults.getString("title");
            String artist = genreRecResults.getString("artist");
            String album = genreRecResults.getString("album");
            String length = genreRecResults.getString("length");
            System.out.println("Song name: " + String.format("%-40s", title)
                    + " Artist name: " + String.format("%-30s", artist)
                    + " Album name: " + String.format("%-40s", album)
                    + " Song length: " + length + " seconds");
            i++;
        }

        if( i == 0){ // if user has no listening history or no users share top genres; gets random songs
            String randomSongsString = "SELECT song.title AS title, song_artist.artist_name AS artist, album.name AS album, song.length AS length, random()"
                    + " FROM song_artist, album, album_song, song, user_song"
                    + " WHERE song.sid = song_artist.sid AND album_song.sid = song.sid AND album.aid = album_song.aid"
                    + " ORDER BY random() LIMIT 20";
            
            PreparedStatement randomSongsQuery = conn.prepareStatement(randomSongsString);
            ResultSet randomSongsResult = randomSongsQuery.executeQuery();

            while(randomSongsResult.next()){
                String title = randomSongsResult.getString("title");
                String artist = randomSongsResult.getString("artist");
                String album = randomSongsResult.getString("album");
                String length = randomSongsResult.getString("length");
                System.out.println("Song name: " + String.format("%-40s", title)
                        + " Artist name: " + String.format("%-30s", artist)
                        + " Album name: " + String.format("%-40s", album)
                        + " Song length: " + length + " seconds");
            }
        }
    }

}
