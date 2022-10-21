// Used for "Listening" to songs or albums

import java.sql.*;
import java.util.Scanner;

public class Listen {
    
    public static void listenSong(Connection conn, Scanner scanner, String song){
        System.out.println("Listening to " + song + "!");
    }

    public static void listenAlbum(Connection conn, Scanner scanner, String album){
        System.out.println("Listening to " + album + "!");
    }
}
