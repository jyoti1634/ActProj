import java.sql.*;

public class CheckDb {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/accounting_db?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String pwd = "jyoti";
        try (Connection c = DriverManager.getConnection(url, user, pwd)) {
            DatabaseMetaData m = c.getMetaData();
            System.out.println("Product: " + m.getDatabaseProductName() + " " + m.getDatabaseProductVersion());
            System.out.println("Driver: " + m.getDriverName() + " " + m.getDriverVersion());
            try (Statement s = c.createStatement()) {
                var rs = s.executeQuery("SHOW TABLES");
                System.out.println("Tables:");
                while (rs.next()) {
                    System.out.println(" - " + rs.getString(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }
}
