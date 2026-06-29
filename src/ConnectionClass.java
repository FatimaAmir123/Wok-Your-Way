import java.sql.*;

class ConnectionClass {
    private static final String URL  = "jdbc:mysql://localhost:3306/rms"
                                     + "?useSSL=false&serverTimezone=UTC"
                                     + "&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "Pakistan1947!";

    private static Connection connection = null;
    private ConnectionClass() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("[DB] Connected.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] Driver not found. Add mysql-connector-j.jar to Libraries.");
        } catch (SQLException e) {
            System.err.println("[DB] " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try { if (connection != null && !connection.isClosed()) connection.close(); }
        catch (SQLException e) { e.printStackTrace(); }
    }
}
