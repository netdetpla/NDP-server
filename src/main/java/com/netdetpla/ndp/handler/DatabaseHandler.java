package com.netdetpla.ndp.handler;

import org.springframework.stereotype.Component;
import java.sql.*;

@Component
public class DatabaseHandler {
    private static Connection connection = null;
    private static String url = "jdbc:mysql://192.168.226.11:3306/ndp?serverTimezone=GMT%2B8";
	private static String drivername = "com.mysql.cj.jdbc.Driver";
	private static String username = "root";
	private static String password = "password" ;

    static {
        try {
            Class.forName(drivername);
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
            Class.forName(drivername);
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
