import org.flywaydb.core.Flyway;

public class FlywayRunner {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/accounting_db";
        String user = "root";
        String pwd = "jyoti";
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(url, user, pwd)
                    .baselineOnMigrate(true)
                    .load();
            var res = flyway.migrate();
            System.out.println("Migrations applied: " + res.migrationsExecuted);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }
}
