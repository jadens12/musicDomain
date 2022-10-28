import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Search {
    
    Connection conn;
    Scanner scanner;

    public Search(Connection conn, Scanner scanner){
        this.conn = conn;
        this.scanner = scanner;
    }

    public void printSong(String title, String artist, String album, int length){
        System.out.println("Song name: " + String.format("%-40s", title) + " Artist name: "
        + String.format("%-25s", artist) + " Album name: " + String.format("%-40s", album) + " Song length: " + length + " seconds");
    }

    public void executeSongsQuery(String queryString, String toAdd) throws SQLException{
        String orderDefault = " ORDER BY song.title ASC, song_artist.artist_name ASC";
        String orderSongname = " ORDER BY song.title";
        String orderArtistname = " ORDER BY song_artist.artist_name";
        String orderGenre = " ORDER BY genre_song.genre_name";
        String orderYear = " ORDER BY EXTRACT(YEAR FROM song.release_date)";
        String ascend = " ASC";
        String descend = " DESC";
        String orderAdd = " , song.title ASC, song_artist.artist_name ASC";

        PreparedStatement query = conn.prepareStatement(queryString + orderDefault);
        query.setString(1, toAdd);
        ResultSet results = query.executeQuery();

        int i=0;
        while(results.next()){
            printSong(results.getString(1), results.getString(2), results.getString(3), results.getInt(4));
            i++;
        }
        System.out.println();
        if(i==0){
            System.out.println("Not found.");
            return;
        }

        while(true){
            System.out.println("Enter a number to sort: \n"
            + "1) Sort by song name\n"
            + "2) Sort by artist name\n"
            + "3) Sort by genre\n"
            + "4) Sort by release year\n"
            + "0) Back");

            int choice;
            String currentSort = "";
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            }
            catch (InputMismatchException e) {
                System.out.println("Please enter a number.");
                scanner.next();
                continue;
            }
            if(choice > 4 || choice < 0){
                System.out.println("Number entered is not a valid option!");
                continue;
            }
            if(choice == 0) return;

            System.out.println("Sort ascending (1) or descending (2)? ");
            int sChoice;
            try {
                sChoice = scanner.nextInt();
                scanner.nextLine();
            }
            catch (InputMismatchException e) {
                System.out.println("Please enter a number.");
                scanner.next();
                continue;
            }
            if(sChoice != 1 && sChoice != 2){
                System.out.println("Number entered is not a valid option!");
                continue;
            }

            switch(choice){
                case 1:
                    currentSort = orderSongname;
                    break;
                case 2:
                    currentSort = orderArtistname;
                    break;
                case 3:
                    currentSort = orderGenre;
                    break;
                case 4:
                    currentSort = orderYear;
                    break;
            }
            currentSort += (sChoice == 1 ? ascend : descend);
            currentSort += orderAdd;

            PreparedStatement sQuery = conn.prepareStatement(queryString + currentSort);
            sQuery.setString(1, toAdd);
            ResultSet sResults = sQuery.executeQuery();

            
            while(sResults.next()){
                printSong(sResults.getString(1), sResults.getString(2), sResults.getString(3), sResults.getInt(4));
            }
            System.out.println();
        }
    }

    public void searchByName() throws SQLException{
        System.out.println("Enter song title: ");
        String songTitle = scanner.nextLine();
        String query = "SELECT DISTINCT song.title, song_artist.artist_name, album.name, song.length, genre_song.genre_name, EXTRACT(YEAR FROM song.release_date)" 
                + " FROM song, song_artist, album, genre_song, album_song"
                + " WHERE song.title = ? AND song_artist.sid = song.sid AND album_song.sid = song.sid AND album.aid = album_song.aid AND genre_song.sid = song.sid";
        executeSongsQuery(query, songTitle);
    }   

    public void searchbyArtist() throws SQLException{
        System.out.println("Enter artist name: ");
        String artistName = scanner.nextLine();
        String query = "SELECT DISTINCT song.title, song_artist.artist_name, album.name, song.length, genre_song.genre_name, EXTRACT(YEAR FROM song.release_date)" 
                + " FROM song, song_artist, album, genre_song, album_song"
                + " WHERE song_artist.artist_name = ? AND song.sid = song_artist.sid AND album_song.sid = song.sid AND album.aid = album_song.aid AND genre_song.sid = song.sid";
        executeSongsQuery(query, artistName);
    }

    public void searchbyAlbum() throws SQLException{
        System.out.println("Enter album name: ");
        String albumName = scanner.nextLine();
        String query = "SELECT DISTINCT song.title, song_artist.artist_name, album.name, song.length, genre_song.genre_name, EXTRACT(YEAR FROM song.release_date)" 
                + " FROM song, song_artist, album, genre_song, album_song"
                + " WHERE album.name = ? AND album_song.aid = album.aid AND song.sid = album_song.sid AND song_artist.sid = song.sid AND genre_song.sid = song.sid";
        executeSongsQuery(query, albumName);
    }

    public void searchbyGenre() throws SQLException{
        System.out.println("Enter genre: ");
        String genre = scanner.nextLine();
        String query = "SELECT DISTINCT song.title, song_artist.artist_name, album.name, song.length, genre_song.genre_name, EXTRACT(YEAR FROM song.release_date)" 
                + " FROM song, song_artist, album, genre_song, album_song"
                + " WHERE genre_song.genre_name = ? AND song.sid = genre_song.sid AND song_artist.sid = song.sid AND album_song.sid = song.sid AND album.aid = album_song.aid";
        executeSongsQuery(query, genre);
    }
}