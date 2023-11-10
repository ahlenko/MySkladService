package com.example.myskladservice.processing.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MS_SQLConnector extends Exception{
    private static String classe = "net.sourceforge.jtds.jdbc.Driver";
    private static String database = "MySkladService";
    private static String password = "02lbvf105";
    private static String ip = "192.140.0.118";
    private static String username = "sa";
    private static String port = "57611";
    private static MS_SQLConnector conect;

    public Connection connection = null;
    private static String url = "jdbc:jtds:sqlserver://" + ip + ":" + port + "/" + database;

    private MS_SQLConnector() throws SQLException {
        try { Class.forName(classe);
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static MS_SQLConnector getConect() throws SQLException {
        conect = new MS_SQLConnector(); return conect;
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) connection.close();
    }
}
