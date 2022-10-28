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

    private void printSong(String title, String artist, String album, int length) throws SQLException{
        PreparedStatement query = conn.prepareStatement("SELECT COUNT (user_song.sid) FROM user_song, song WHERE song.title = ? AND user_song.sid = song.sid");
        query.setString(1, title);
        ResultSet results = query.executeQuery();
        results.next();
        int listenCount = results.getInt(1);

        System.out.println("Song name: " + String.format("%-40s", title) 
        + " Artist name: " + String.format("%-30s", artist) 
        + " Album name: " + String.format("%-40s", album) 
        + " Song length: " + length + " seconds"
        + " \tListen count: " + listenCount);
    }

    private void printByYear(ResultSet results) throws SQLException{
        String currentYear = "";
        while(results.next()){
            String year = results.getString(5);
            if(!currentYear.equals(year)){
                currentYear = year;
                System.out.println("\nYear: " + year);
            }
            printSong(results.getString(1), results.getString(2), results.getString(3), results.getInt(4));
        }
        System.out.println();
    }

    private void sortByGenre(String queryString, String toAdd, int sort) throws SQLException{
        String addSelect = ", genre_song.genre_name";
        String addFrom = ", genre_song";
        String addWhere = " AND genre_song.sid = song.sid";
        String orderGenre = " ORDER BY genre_song.genre_name " + (sort==1 ? "ASC" : "DESC") + ", song.title ASC, song_artist.artist_name ASC";

        StringBuilder finalString = new StringBuilder();
        finalString.append(queryString.substring(0, queryString.lastIndexOf("FROM")-1));
        finalString.append(addSelect);
        finalString.append(queryString.substring(queryString.lastIndexOf("FROM")-1, queryString.indexOf("WHERE")-1));
        finalString.append(addFrom);
        finalString.append(queryString.substring(queryString.indexOf("WHERE")-1));
        finalString.append(addWhere);
        finalString.append(orderGenre);

        PreparedStatement query = conn.prepareStatement(finalString.toString());
        query.setString(1, toAdd);
        ResultSet results = query.executeQuery();

        String currentGenre = "";
        while(results.next()){
            String genre = results.getString(6);
            if(!currentGenre.equals(genre)){
                currentGenre = genre;
                System.out.println("\nGenre: " + genre);
            }
            printSong(results.getString(1), results.getString(2), results.getString(3), results.getInt(4));
        }
        System.out.println();
    }

    public void executeSongsQuery(String queryString, String toAdd, String type) throws SQLException{
        String orderDefault = " ORDER BY song.title ASC, song_artist.artist_name ASC";
        String orderSongname = " ORDER BY song.title";
        String orderArtistname = " ORDER BY song_artist.artist_name";
        String orderYear = " ORDER BY EXTRACT(YEAR FROM song.release_date)";
        String orderGenre = " ORDER BY genre_song.genre_name";
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

            switch(choice){
                case 0:
                    return;
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
                default:
                    System.out.println("Number entered is not a valid option!");
                    continue;
            }

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

            if(choice == 3 && !type.equals("genre")){
                sortByGenre(queryString, toAdd, sChoice);
                continue;
            }

            currentSort += (sChoice == 1 ? ascend : descend);
            currentSort += orderAdd;

            PreparedStatement sQuery = conn.prepareStatement(queryString + currentSort);
            sQuery.setString(1, toAdd);
            ResultSet sResults = sQuery.executeQuery();

            if(choice == 4){
                printByYear(sResults);
                continue;
            }
            
            while(sResults.next()){
                printSong(sResults.getString(1), sResults.getString(2), sResults.getString(3), sResults.getInt(4));
            }
            System.out.println();
        }
    }

    public void searchByName() throws SQLException{
        System.out.println("Enter song title: ");
        String songTitle = scanner.nextLine();
        String query = "SELECT DISTINCT song.title, song_artist.artist_name, album.name, song.length, EXTRACT(YEAR FROM song.release_date)" 
                + " FROM song, song_artist, album, album_song"
                + " WHERE song.title = ? AND song_artist.sid = song.sid AND album_song.sid = song.sid AND album.aid = album_song.aid";
        executeSongsQuery(query, songTitle, "song");
    }   

    public void searchbyArtist() throws SQLException{
        System.out.println("Enter artist name: ");
        String artistName = scanner.nextLine();
        String query = "SELECT DISTINCT song.title, song_artist.artist_name, album.name, song.length, EXTRACT(YEAR FROM song.release_date)" 
                + " FROM song, song_artist, album, album_song"
                + " WHERE song_artist.artist_name = ? AND song.sid = song_artist.sid AND album_song.sid = song.sid AND album.aid = album_song.aid";
        executeSongsQuery(query, artistName, "artist");
    }

    public void searchbyAlbum() throws SQLException{
        System.out.println("Enter album name: ");
        String albumName = scanner.nextLine();
        String query = "SELECT DISTINCT song.title, song_artist.artist_name, album.name, song.length, EXTRACT(YEAR FROM song.release_date)" 
                + " FROM song, song_artist, album, album_song"
                + " WHERE album.name = ? AND album_song.aid = album.aid AND song.sid = album_song.sid AND song_artist.sid = song.sid";
        executeSongsQuery(query, albumName, "album");
    }

    public void searchbyGenre() throws SQLException{
        System.out.println("Enter genre: ");
        String genre = scanner.nextLine();
        String query = "SELECT DISTINCT song.title, song_artist.artist_name, album.name, song.length, EXTRACT(YEAR FROM song.release_date), genre_song.genre_name" 
                + " FROM song, song_artist, album, genre_song, album_song"
                + " WHERE genre_song.genre_name = ? AND song.sid = genre_song.sid AND song_artist.sid = song.sid AND album_song.sid = song.sid AND album.aid = album_song.aid";
        executeSongsQuery(query, genre, "genre");
    }
}