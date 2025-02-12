package utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    private final String URL = "jdbc:mysql://localhost:3306/event_craft";
    private final String USER = "root";
    private final String PSW = "";
    private Connection connection;
    private static MyDataBase instance;

    private MyDataBase() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/event_craft", "root", "");
            System.out.println("Connected");
        } catch (SQLException var2) {
            SQLException e = var2;
            System.out.println(e.getMessage());
        }

    }

    public static MyDataBase getInstance() {
        if (instance == null) {
            instance = new MyDataBase();
        }

        return instance;
    }

    public Connection getConnection() {
        return this.connection;
    }
}
