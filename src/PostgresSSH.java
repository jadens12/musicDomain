import com.jcraft.jsch.*;

import java.sql.*;
import java.util.Properties;

public class PostgresSSH {

    public static void main(String[] args) throws SQLException {

        if (args.length != 2) {
            System.out.println("Usage: java PostgresSSH username password");
            System.exit(0);
        }
        
        int lport = 5431;
        String rhost = "starbug.cs.rit.edu";
        int rport = 5432;
        String user = "jis6849"; //change to your username
        String password = "@Coolbeans12"; //change to your password
        String databaseName = "p32001_18"; //change to your database name

        String driverName = "org.postgresql.Driver";
        Connection conn = null;
        Session session = null;
        try {
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            session = jsch.getSession(user, rhost, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.setConfig("PreferredAuthentications","publickey,keyboard-interactive,password");
            session.connect();
            // System.out.println("Connected");
            int assigned_port = session.setPortForwardingL(lport, "localhost", rport);
            // System.out.println("Port Forwarded");

            // Assigned port could be different from 5432 but rarely happens
            String url = "jdbc:postgresql://localhost:"+ assigned_port + "/" + databaseName;

            // System.out.println("database Url: " + url);
            Properties props = new Properties();
            props.put("user", user);
            props.put("password", password);

            Class.forName(driverName);
            conn = DriverManager.getConnection(url, props);
            // System.out.println("Database connection established");

            // Do something with the database....
            Interface musicInterface = new Interface(conn);
            musicInterface.initialPrompt();
            

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Closing Database Connection");
                conn.close();
            }
            if (session != null && session.isConnected()) {
                System.out.println("Closing SSH Connection");
                session.disconnect();
            }
        }
    }

}
