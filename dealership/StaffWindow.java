package car.dealership;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Properties;
import java.net.URL;

public class StaffWindow {

    DefaultTableModel tableModel;
    JTable table;
    int selectedId = -1; // Tracks which staff member is selected

    public StaffWindow() {
        JFrame frame = new JFrame("Staff Management Suite");
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        
        Color bg = new Color(244, 241, 234);
        Color sidebarBg = Color.WHITE;
        Color btnColor = new Color(93, 64, 55);
        Color titleColor = new Color(62, 39, 35);
        
        frame.getContentPane().setBackground(bg);
        URL iconURL = getClass().getResource("/logo.png");
        if (iconURL != null) frame.setIconImage(new ImageIcon(iconURL).getImage());

        // --- LEFT SIDEBAR ---
        JPanel formPanel = new JPanel();
        formPanel.setPreferredSize(new Dimension(350, 0));
        formPanel.setBackground(sidebarBg);
        formPanel.setLayout(null);
        formPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(215, 204, 200)));

        JLabel title = new JLabel("Staff Actions");
        title.setBounds(30, 40, 250, 30);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(titleColor);
        formPanel.add(title);

        JTextField txtName = createInput(formPanel, "Full Name", 100);
        JTextField txtRole = createInput(formPanel, "Job Role", 180);
        JTextField txtSalary = createInput(formPanel, "Salary (Rs.)", 260);

        // Buttons
        JButton btnAdd = createButton("ADD NEW", 30, 340, btnColor);
        JButton btnUpdate = createButton("UPDATE SELECTED", 30, 400, new Color(141, 110, 99)); // Lighter brown
        JButton btnDelete = createButton("DELETE SELECTED", 30, 460, new Color(180, 100, 100)); // Red

        formPanel.add(btnAdd);
        formPanel.add(btnUpdate);
        formPanel.add(btnDelete);
        frame.add(formPanel, BorderLayout.WEST);

        // --- TABLE ---
        String[] cols = {"ID", "Name", "Role", "Salary (Rs.)"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setBackground(new Color(215, 204, 200));
        
        // CLICK LISTENER: Fill inputs when row clicked
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtRole.setText(tableModel.getValueAt(row, 2).toString());
                txtSalary.setText(tableModel.getValueAt(row, 3).toString());
            }
        });

        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- BUTTON LOGIC ---
        btnAdd.addActionListener(e -> {
            runQuery("INSERT INTO staff (name, role, salary) VALUES (?, ?, ?)", txtName.getText(), txtRole.getText(), txtSalary.getText());
            clearForm(txtName, txtRole, txtSalary);
        });

        btnUpdate.addActionListener(e -> {
            if(selectedId == -1) { JOptionPane.showMessageDialog(frame, "Select a row first!"); return; }
            try {
                Connection con = getConnection();
                PreparedStatement pst = con.prepareStatement("UPDATE staff SET name=?, role=?, salary=? WHERE id=?");
                pst.setString(1, txtName.getText()); pst.setString(2, txtRole.getText()); 
                pst.setDouble(3, Double.parseDouble(txtSalary.getText())); pst.setInt(4, selectedId);
                pst.executeUpdate(); con.close(); loadStaff(); clearForm(txtName, txtRole, txtSalary);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        btnDelete.addActionListener(e -> {
            if(selectedId == -1) { JOptionPane.showMessageDialog(frame, "Select a row first!"); return; }
            try {
                Connection con = getConnection();
                con.prepareStatement("DELETE FROM staff WHERE id=" + selectedId).executeUpdate();
                con.close(); loadStaff(); clearForm(txtName, txtRole, txtSalary);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        loadStaff();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void runQuery(String sql, String n, String r, String s) {
        try {
            Connection con = getConnection();
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, n); pst.setString(2, r); pst.setDouble(3, Double.parseDouble(s));
            pst.executeUpdate(); con.close(); loadStaff();
        } catch (Exception ex) { JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage()); }
    }

    private void loadStaff() {
        tableModel.setRowCount(0);
        try {
            Connection con = getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM staff");
            while(rs.next()) tableModel.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getString("role"), rs.getDouble("salary")});
            con.close();
        } catch (Exception e) {}
    }

    private void clearForm(JTextField n, JTextField r, JTextField s) {
        n.setText(""); r.setText(""); s.setText(""); selectedId = -1;
    }

    private JTextField createInput(JPanel p, String label, int y) {
        JLabel l = new JLabel(label); l.setBounds(30, y, 200, 20); l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(141, 110, 99)); p.add(l);
        JTextField t = new JTextField(); t.setBounds(30, y + 25, 280, 35);
        t.setBorder(BorderFactory.createLineBorder(new Color(215, 204, 200))); p.add(t);
        return t;
    }

    private JButton createButton(String text, int x, int y, Color c) {
        JButton b = new JButton(text); b.setBounds(x, y, 280, 45); b.setBackground(c); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); return b;
    }

    private Connection getConnection() throws Exception {
        Properties prop = new Properties();
        prop.load(App.class.getClassLoader().getResourceAsStream("application.properties"));
        return DriverManager.getConnection(prop.getProperty("db.url"), prop.getProperty("db.username"), prop.getProperty("db.password"));
    }
}