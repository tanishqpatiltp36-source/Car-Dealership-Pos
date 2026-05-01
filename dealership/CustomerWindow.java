package car.dealership;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Properties;
import java.net.URL;

public class CustomerWindow {

    DefaultTableModel tableModel;
    JTable table;
    int selectedId = -1;

    public CustomerWindow() {
        JFrame frame = new JFrame("Customer Directory");
        frame.setSize(850, 600);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        Color bg = new Color(244, 241, 234);
        Color btnColor = new Color(93, 64, 55);
        frame.getContentPane().setBackground(bg);
        URL iconURL = getClass().getResource("/logo.png");
        if (iconURL != null) frame.setIconImage(new ImageIcon(iconURL).getImage());

        // --- TOP PANEL ---
        JPanel panelTop = new JPanel(null);
        panelTop.setPreferredSize(new Dimension(800, 180));
        panelTop.setBackground(bg);
        panelTop.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(215, 204, 200)));

        JLabel title = new JLabel("Client Profiles");
        title.setBounds(40, 20, 200, 30);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(62, 39, 35));
        panelTop.add(title);

        panelTop.add(createLabel("Client Name", 40, 60));
        JTextField txtName = createInput(40, 85);
        panelTop.add(txtName);

        panelTop.add(createLabel("Phone Number", 300, 60));
        JTextField txtPhone = createInput(300, 85);
        panelTop.add(txtPhone);

        JButton btnAdd = createButton("SAVE", 560, 85, btnColor);
        JButton btnUpdate = createButton("UPDATE", 670, 85, new Color(141, 110, 99));
        JButton btnDelete = createButton("DELETE", 560, 130, new Color(180, 100, 100));
        btnDelete.setSize(220, 35); // Make delete wider

        panelTop.add(btnAdd); panelTop.add(btnUpdate); panelTop.add(btnDelete);
        frame.add(panelTop, BorderLayout.NORTH);

        // --- TABLE ---
        String[] cols = {"ID", "Client Name", "Contact Number"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setBackground(new Color(215, 204, 200));

        // Click Listener
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtPhone.setText(tableModel.getValueAt(row, 2).toString());
            }
        });

        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- ACTIONS ---
        btnAdd.addActionListener(e -> {
            executeQuery("INSERT INTO customers (name, phone) VALUES (?, ?)", txtName.getText(), txtPhone.getText(), -1);
            clear(txtName, txtPhone);
        });

        btnUpdate.addActionListener(e -> {
            if(selectedId == -1) return;
            executeQuery("UPDATE customers SET name=?, phone=? WHERE id=?", txtName.getText(), txtPhone.getText(), selectedId);
            clear(txtName, txtPhone);
        });

        btnDelete.addActionListener(e -> {
            if(selectedId == -1) return;
            executeQuery("DELETE FROM customers WHERE id=?", null, null, selectedId); // Special case for delete
            clear(txtName, txtPhone);
        });

        loadCustomers();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void executeQuery(String sql, String name, String phone, int id) {
        try {
            Connection con = getConnection();
            PreparedStatement pst = con.prepareStatement(sql);
            if(sql.startsWith("DELETE")) {
                pst.setInt(1, id);
            } else {
                pst.setString(1, name);
                pst.setString(2, phone);
                if(id != -1) pst.setInt(3, id);
            }
            pst.executeUpdate(); con.close(); loadCustomers();
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void loadCustomers() {
        tableModel.setRowCount(0);
        try {
            Connection con = getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM customers");
            while(rs.next()) tableModel.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getString("phone")});
            con.close();
        } catch (Exception e) {}
    }

    private void clear(JTextField n, JTextField p) { n.setText(""); p.setText(""); selectedId = -1; }
    
    private Connection getConnection() throws Exception {
        Properties prop = new Properties();
        prop.load(App.class.getClassLoader().getResourceAsStream("application.properties"));
        return DriverManager.getConnection(prop.getProperty("db.url"), prop.getProperty("db.username"), prop.getProperty("db.password"));
    }

    private JTextField createInput(int x, int y) {
        JTextField t = new JTextField(); t.setBounds(x, y, 240, 35);
        t.setBorder(BorderFactory.createLineBorder(new Color(215, 204, 200))); return t;
    }
    
    private JLabel createLabel(String t, int x, int y) {
        JLabel l = new JLabel(t); l.setBounds(x, y, 200, 20);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12)); l.setForeground(new Color(141, 110, 99)); return l;
    }

    private JButton createButton(String t, int x, int y, Color c) {
        JButton b = new JButton(t); b.setBounds(x, y, 100, 35);
        b.setBackground(c); b.setForeground(Color.WHITE); b.setFocusPainted(false); return b;
    }
}