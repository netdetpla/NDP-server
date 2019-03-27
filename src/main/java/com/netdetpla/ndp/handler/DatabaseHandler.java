package com.netdetpla.ndp.handler;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseHandler {
    private static Connection connection = null;
    private static String url = "";
    private static String driverName = "";
    private static String username = "";
    private static String password = "";

    static {
        try {
            Properties properties = new Properties();
            InputStream is = DatabaseHandler.class.getClassLoader().getResourceAsStream("settings.properties");
            assert is != null;
            properties.load(is);
            driverName = properties.getProperty("dbDriver");
            username = properties.getProperty("dbUserName");
            password = properties.getProperty("dbPassword");
            url = properties.getProperty("dbUrl");
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static Connection getConnection() {
        if (connection != null) {
            return connection;
        }
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static ResultSet executeQuery(String sql, String... args) {
        ResultSet result = null;
        try {
            PreparedStatement preparedStatement = generateQuery(sql, args);
            result = preparedStatement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean execute(String sql, String... args) {
        boolean result = false;
        try {
            PreparedStatement preparedStatement = generateQuery(sql, args);
            result = preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static PreparedStatement generateQuery(String sql, String[] args) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < args.length; i++) {
            preparedStatement.setString(i + 1, args[i]);
        }
        return preparedStatement;
    }
}
