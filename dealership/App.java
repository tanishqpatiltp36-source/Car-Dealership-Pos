package car.dealership;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.InputStream;
import javax.swing.SwingUtilities;

public class App {
    // This allows every window in your app to use one single connection
    public static Connection con;

    public static void main(String[] args) {
        try {
            // 1. Load Settings
            Properties prop = new Properties();
            InputStream input = App.class.getClassLoader().getResourceAsStream("application.properties");
            if (input == null) throw new Exception("application.properties not found!");
            prop.load(input);

            // 2. Connect to Database
            System.out.println("Starting Database...");
            con = DriverManager.getConnection(
                prop.getProperty("db.url"), 
                prop.getProperty("db.username"), 
                prop.getProperty("db.password")
            );

            // 3. Launch GUI (The professional way)
            SwingUtilities.invokeLater(() -> new MainMenu());

        } catch (Exception e) {
            System.err.println("Critical Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}