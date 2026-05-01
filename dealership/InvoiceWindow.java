package car.dealership;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.net.URL;

public class InvoiceWindow {

    JComboBox<String> comboCust, comboCar, comboStaff;
    JTextArea txtReceipt;
    JFrame frame;

    public InvoiceWindow() {
        frame = new JFrame("New Sales Invoice");
        frame.setSize(950, 600);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        // --- THEME ---
        Color bg = new Color(244, 241, 234); 
        Color btnColor = new Color(93, 64, 55); 
        Color titleColor = new Color(62, 39, 35); 

        frame.getContentPane().setBackground(bg);
        URL iconURL = getClass().getResource("/logo.png");
        if (iconURL != null) frame.setIconImage(new ImageIcon(iconURL).getImage());

        JLabel title = new JLabel("GENERATE SALE INVOICE", SwingConstants.CENTER);
        title.setBounds(0, 20, 900, 40);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(titleColor);
        frame.add(title);

        // --- LEFT PANEL ---
        JPanel panelLeft = new JPanel();
        panelLeft.setLayout(null);
        panelLeft.setBounds(40, 80, 400, 450);
        panelLeft.setBackground(Color.WHITE);
        panelLeft.setBorder(BorderFactory.createLineBorder(new Color(215, 204, 200)));

        panelLeft.add(createLabel("Select Customer:", 30, 30));
        comboCust = new JComboBox<>();
        styleCombo(comboCust, 30, 60);
        panelLeft.add(comboCust);

        panelLeft.add(createLabel("Select Vehicle:", 30, 120));
        comboCar = new JComboBox<>();
        styleCombo(comboCar, 30, 150);
        panelLeft.add(comboCar);

        panelLeft.add(createLabel("Salesperson:", 30, 210));
        comboStaff = new JComboBox<>();
        styleCombo(comboStaff, 30, 240);
        panelLeft.add(comboStaff);

        JButton btnGenerate = new JButton("CONFIRM SALE & PRINT");
        btnGenerate.setBounds(30, 350, 340, 50);
        btnGenerate.setBackground(btnColor);
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGenerate.setFocusPainted(false);
        panelLeft.add(btnGenerate);

        frame.add(panelLeft);

        // --- RIGHT PANEL (Receipt) ---
        txtReceipt = new JTextArea();
        txtReceipt.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Monospace for alignment
        txtReceipt.setEditable(false);
        txtReceipt.setText("\n  INVOICE PREVIEW  \n  Waiting for input...");
        
        JScrollPane scroll = new JScrollPane(txtReceipt);
        scroll.setBounds(480, 80, 400, 450);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(215, 204, 200)));
        frame.add(scroll);

        loadData(); 

        // --- PRINT LOGIC ---
        btnGenerate.addActionListener(e -> generateAndPrint());

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void generateAndPrint() {
        String cust = (String) comboCust.getSelectedItem();
        String carRaw = (String) comboCar.getSelectedItem(); 
        String staff = (String) comboStaff.getSelectedItem();

        if (cust == null || carRaw == null || staff == null) {
            JOptionPane.showMessageDialog(frame, "Please select all fields.");
            return;
        }

        String[] carParts = carRaw.split(" - Rs. ");
        String model = carParts[0];
        double price = Double.parseDouble(carParts[1]);

        // 1. Database Logic
        try {
            Connection con = getConnection();
            PreparedStatement pst = con.prepareStatement("INSERT INTO sales (customer_name, car_model, staff_name, total_price) VALUES (?, ?, ?, ?)");
            pst.setString(1, cust); pst.setString(2, model); pst.setString(3, staff); pst.setDouble(4, price);
            pst.executeUpdate();

            PreparedStatement pstUpd = con.prepareStatement("UPDATE cars SET qty = qty - 1 WHERE model = ?");
            pstUpd.setString(1, model);
            pstUpd.executeUpdate();
            con.close();

            // 2. Receipt Text
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String receipt = 
                "\n" +
                "            CAR DEALERSHIP       \n" +
                "     ===========================     \n" +
                "             SALE INVOICE       \n" +
                "     ===========================    \n\n" +
                "  Date: " + date + "\n" +
                "  Invoice ID: #" + System.currentTimeMillis() % 1000 + "\n\n" +
                "  CUSTOMER DETAILS:\n" +
                "  --------------------------------\n" +
                "  Name: " + cust + "\n\n" +
                "  VEHICLE INFORMATION:\n" +
                "  --------------------------------\n" +
                "  Model: " + model + "\n" +
                "  Price: Rs. " + String.format("%,.2f", price) + "\n\n" +
                "  SALES AGENT:\n" +
                "  --------------------------------\n" +
                "  Name: " + staff + "\n\n" +
                "  --------------------------------\n" +
                "  TOTAL: Rs. " + String.format("%,.2f", price) + "\n" +
                "  --------------------------------\n\n" +
                "       Authorized Signature         \n\n\n" +
                "     Thank you for your purchase!   \n" +
                "     Visit us again.                \n";

            txtReceipt.setText(receipt);
            
            // 3. ACTUAL PRINT COMMAND (Yeh line print dialog kholegi)
            boolean complete = txtReceipt.print(); 
            
            if (complete) {
                JOptionPane.showMessageDialog(frame, "Done! Invoice Sent to Printer.");
            } else {
                JOptionPane.showMessageDialog(frame, "Printing Cancelled.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
        }
    }

    // ... (Helpers same as before)
    private void loadData() {
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rsCust = stmt.executeQuery("SELECT name FROM customers");
            while (rsCust.next()) comboCust.addItem(rsCust.getString("name"));
            ResultSet rsStaff = stmt.executeQuery("SELECT name FROM staff");
            while (rsStaff.next()) comboStaff.addItem(rsStaff.getString("name"));
            ResultSet rsCar = stmt.executeQuery("SELECT model, price FROM cars WHERE qty > 0");
            while (rsCar.next()) comboCar.addItem(rsCar.getString("model") + " - Rs. " + rsCar.getString("price"));
            con.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private Connection getConnection() throws Exception {
        Properties prop = new Properties();
        prop.load(App.class.getClassLoader().getResourceAsStream("application.properties"));
        return DriverManager.getConnection(prop.getProperty("db.url"), prop.getProperty("db.username"), prop.getProperty("db.password"));
    }

    private void styleCombo(JComboBox<String> box, int x, int y) {
        box.setBounds(x, y, 340, 40); box.setBackground(Color.WHITE); box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel l = new JLabel(text); l.setBounds(x, y, 340, 20); l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(141, 110, 99)); return l;
    }
}