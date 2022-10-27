import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Search {
    
    Connection conn;
    Scanner scanner;

    public Search(Connection conn, Scanner scanner){
        this.conn = conn;
        this.scanner = scanner;
    }

    public void searchByName() throws SQLException{

        ArrayList<String> songArtist = new ArrayList<>();

        System.out.println("Enter song title: ");
        String songTitle = scanner.nextLine();
        PreparedStatement songQuery = conn.prepareStatement("SELECT sid, length FROM song WHERE title = ? ORDER BY title ASC");
        songQuery.setString(1, songTitle);
        ResultSet songResult = songQuery.executeQuery();
        if (!songResult.next()) {
            System.out.println("Song not found.");
            return;
        }

        while (songResult.next()){
            int sid = songResult.getInt("sid");
            int length = songResult.getInt("length");

            PreparedStatement ArtistSongQuery = conn.prepareStatement("SELECT name FROM song_artist WHERE sid = ? ORDER BY name ASC");
            ArtistSongQuery.setInt(1, sid);
            ResultSet ArtistSongResult = ArtistSongQuery.executeQuery();

            while(ArtistSongResult.next()){
                String artistName = ArtistSongResult.getString("name");
                songArtist.add(artistName);
            }

            PreparedStatement albumSongQuery = conn.prepareStatement("SELECT aid FROM song_album WHERE sid = ?");
            albumSongQuery.setInt(1, sid);
            ResultSet albumSongResult = albumSongQuery.executeQuery();

            while(albumSongResult.next()){
                int aid = albumSongResult.getInt("aid");

                PreparedStatement albumQuery = conn.prepareStatement("SELECT name FROM album WHERE aid = ?");
                albumQuery.setInt(1, aid);
                ResultSet albumResult = albumQuery.executeQuery();

                String albumName = albumResult.getString("name");

                for (String name : songArtist){
                    System.out.println("Song name: " + songTitle);
                    System.out.println("Artist name: " + name);
                    System.out.println("Album name: " + albumName);
                    System.out.println("Song length" + length);
                }
            }
        }
    }   

    public void searchbyArtist() throws SQLException{

        System.out.println("Enter artist name: ");
        String artistName = scanner.nextLine();

        PreparedStatement ArtistQuery = conn.prepareStatement("SELECT name FROM artist WHERE name = ? ORDER BY name ASC");
        ArtistQuery.setString(1, artistName);
        ResultSet artistResult = ArtistQuery.executeQuery();
        if (!artistResult.next()){
            System.out.println("Artist not found");
            return;
        }
        String name = artistResult.getString("name");

        PreparedStatement ArtistSongQuery = conn.prepareStatement("SELECT sid FROM song_artist WHERE name = ?");
        ArtistSongQuery.setString(1, name);
        ResultSet ArtistSongResult = ArtistSongQuery.executeQuery();

        while (ArtistSongResult.next()){
            int sid = ArtistSongResult.getInt("sid");

            PreparedStatement SongQuery = conn.prepareStatement("SELECT title, length FROM song WHERE sid = ? ORDER BY title ASC");
            SongQuery.setInt(1, sid);
            ResultSet SongResult = SongQuery.executeQuery();

            String title = SongResult.getString("title");
            int length = SongResult.getInt("length");
                
            PreparedStatement albumSongQuery = conn.prepareStatement("SELECT aid FROM song_album WHERE sid = ?");
            albumSongQuery.setInt(1, sid);
            ResultSet albumSongResult = albumSongQuery.executeQuery();
    
            while(albumSongResult.next()){
                int aid = albumSongResult.getInt("aid");
    
                PreparedStatement albumQuery = conn.prepareStatement("SELECT name FROM album WHERE aid = ?");
                albumQuery.setInt(1, aid);
                ResultSet albumResult = albumQuery.executeQuery();
    
                String albumName = albumResult.getString("name");
    
                System.out.println("Song name: " + title);
                System.out.println("Artist name: " + name);
                System.out.println("Album name: " + albumName);
                System.out.println("Song length" + length);
            }     
        }
    }

    public void searchbyAlbum() throws SQLException{

        System.out.println("Enter album name: ");
        String albumName = scanner.nextLine();

        PreparedStatement albumQuery = conn.prepareStatement("SELECT aid FROM album WHERE name = ?");
        albumQuery.setString(1, albumName);
        ResultSet albumResult = albumQuery.executeQuery();
        if (!albumResult.next()){
            System.out.println("Album not found");
            return;
        }

        while(albumResult.next()){
            int aid = albumResult.getInt("aid");

            PreparedStatement albumSongQuery = conn.prepareStatement("SELECT sid FROM song_album WHERE aid = ?");
            albumSongQuery.setInt(1, aid);
            ResultSet albumSongResult = albumSongQuery.executeQuery();

            while (albumSongResult.next()){
                int sid = albumSongResult.getInt("sid");

                PreparedStatement SongQuery = conn.prepareStatement("SELECT title, length FROM song WHERE sid = ? ORDER BY title ASC");
                SongQuery.setInt(1, sid);
                ResultSet SongResult = SongQuery.executeQuery();

                String title = SongResult.getString("title");
                int length = SongResult.getInt("length");

                PreparedStatement ArtistSongQuery = conn.prepareStatement("SELECT name FROM song_artist WHERE sid = ? ORDER BY name ASC");
                ArtistSongQuery.setInt(1, sid);
                ResultSet ArtistSongResult = ArtistSongQuery.executeQuery();

                while (ArtistSongResult.next()){
                    String name = ArtistSongResult.getString("name");

                    System.out.println("Song name: " + title);
                    System.out.println("Artist name: " + name);
                    System.out.println("Album name: " + albumName);
                    System.out.println("Song length: " + length);
                }
            }
        }
    }

    public void searchbyGenre() throws SQLException{

        System.out.println("Enter genre: ");
        String genre = scanner.nextLine();

        PreparedStatement GenreQuery = conn.prepareStatement("SELECT name FROM genre WHERE name = ?");
        GenreQuery.setString(1, genre);
        ResultSet GenreResult = GenreQuery.executeQuery();
        if (!GenreResult.next()){
            System.out.println("Genre not found");
            return;
        }

        genre = GenreResult.getString("name");

        PreparedStatement genreSongQuery = conn.prepareStatement("SELECT sid FROM song_genre WHERE name = ?");
        genreSongQuery.setString(1, genre);
        ResultSet genreSongResult = genreSongQuery.executeQuery();

        while (genreSongResult.next()){
            int sid = genreSongResult.getInt("sid");

            PreparedStatement songQuery = conn.prepareStatement("SELECT title, length FROM song WHERE sid = ? ORDER BY title ASC");
            songQuery.setInt(1, sid);
            ResultSet songResult = songQuery.executeQuery();

            String title = songResult.getString("title");
            int length = songResult.getInt("length");

            PreparedStatement albumSongQuery = conn.prepareStatement("SELECT aid FROM song_album WHERE sid = ?");
            albumSongQuery.setInt(1, sid);
            ResultSet albumSongResult = albumSongQuery.executeQuery();

            while (albumSongResult.next()){
                int aid = albumSongResult.getInt("aid");

                PreparedStatement albumQuery = conn.prepareStatement("SELECT name FROM album WHERE aid = ?");
                albumQuery.setInt(1, aid);
                ResultSet albumResult = albumQuery.executeQuery();

                String albumName = albumResult.getString("name");

                PreparedStatement ArtistSongQuery = conn.prepareStatement("SELECT name FROM song_artist WHERE sid = ? ORDER BY name ASC");
                ArtistSongQuery.setInt(1, sid);
                ResultSet ArtistSongResult = ArtistSongQuery.executeQuery();

                while (ArtistSongResult.next()){
                    String name = ArtistSongResult.getString("name");

                    System.out.println("Song name: " + title);
                    System.out.println("Artist name: " + name);
                    System.out.println("Album name: " + albumName);
                    System.out.println("Song length: " + length);
                }
            }
        }
    }
}
