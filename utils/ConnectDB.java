package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    // 4 thông số DRIVER , URL, USERNAME, PASSWORD;
    public static final String DRIVER = "org.postgresql.Driver";
    public static final String URL = "jdbc:postgresql://localhost:5432/db_cinema_booking";
    public static final String USERNAME = "postgres";
    public static final String PASSWORD = "Hzthinh2412*";

    // Tạo ra 1 phương thức để kết nối đến database
    public static Connection openConnection() {
        try {
            // Khai bao driver voi chuong trinh java
            Class.forName(DRIVER);

            // mo ket noi den database
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
