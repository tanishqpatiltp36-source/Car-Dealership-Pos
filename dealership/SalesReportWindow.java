package car.dealership;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Properties;
import java.net.URL;

public class SalesReportWindow {

    public SalesReportWindow() {
        JFrame frame = new JFrame("Financial Sales Report");
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        Color bg = new Color(244, 241, 234);
        frame.getContentPane().setBackground(bg);
        URL iconURL = getClass().getResource("/logo.png");
        if (iconURL != null) frame.setIconImage(new ImageIcon(iconURL).getImage());

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(bg);
        header.setPreferredSize(new Dimension(900, 80));
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("SALES HISTORY LOG");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(62, 39, 35));
        header.add(title, BorderLayout.WEST);

        // Revenue Label
        JLabel lblRevenue = new JLabel("Total Revenue: Rs. 0");
        lblRevenue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblRevenue.setForeground(new Color(0, 100, 0)); // Dark Green for money
        header.add(lblRevenue, BorderLayout.EAST);
        
        frame.add(header, BorderLayout.NORTH);

        // --- TABLE ---
        String[] cols = {"Sale ID", "Customer Name", "Vehicle Model", "Salesperson", "Date", "Amount (Rs.)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setBackground(new Color(215, 204, 200));
        table.getTableHeader().setForeground(new Color(62, 39, 35));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        frame.add(scroll, BorderLayout.CENTER);

        // --- LOAD DATA ---
        double total = 0;
        try {
            Properties prop = new Properties();
            prop.load(App.class.getClassLoader().getResourceAsStream("application.properties"));
            Connection con = DriverManager.getConnection(prop.getProperty("db.url"), prop.getProperty("db.username"), prop.getProperty("db.password"));
            
            // Order by latest sale first
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM sales ORDER BY date DESC");
            
            while(rs.next()) {
                double amount = rs.getDouble("total_price");
                total += amount; // Calculate Total
                
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("customer_name"),
                    rs.getString("car_model"),
                    rs.getString("staff_name"),
                    rs.getString("date"),
                    String.format("%,.2f", amount)
                });
            }
            con.close();
            
            // Update Total Label
            lblRevenue.setText("Total Revenue: Rs. " + String.format("%,.2f", total));
            
        } catch (Exception e) { e.printStackTrace(); }

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}