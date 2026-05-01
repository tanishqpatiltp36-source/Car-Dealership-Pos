package car.dealership;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.Properties;
import java.net.URL;

public class AddCarWindow {

    String selectedImagePath = null; 
    JLabel lblImagePreview; 

    public AddCarWindow() {
        JFrame frame = new JFrame("New Vehicle Entry");
        frame.setSize(600, 750); 
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        Color bg = new Color(244, 241, 234); 
        Color btnColor = new Color(93, 64, 55); 
        Color textColor = new Color(62, 39, 35); 

        frame.getContentPane().setBackground(bg);
        URL iconURL = getClass().getResource("/logo.png");
        if (iconURL != null) frame.setIconImage(new ImageIcon(iconURL).getImage());

        JLabel title = new JLabel("VEHICLE REGISTRATION", SwingConstants.CENTER);
        title.setBounds(0, 20, 600, 40);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(textColor);
        frame.add(title);

        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBounds(100, 70, 400, 600); 
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(215, 204, 200), 1));
        
        card.add(createLabel("Model Name", 30, 20));
        JTextField txtModel = createInput(30, 45);
        card.add(txtModel);

        // --- CHANGE: RS. LABEL ---
        card.add(createLabel("Price (Rs.)", 30, 95));
        JTextField txtPrice = createInput(30, 120);
        card.add(txtPrice);

        card.add(createLabel("Vehicle Description", 30, 170));
        JTextArea txtDesc = new JTextArea();
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setBackground(new Color(250, 250, 250));
        
        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        scrollDesc.setBounds(30, 195, 340, 70); 
        scrollDesc.setBorder(BorderFactory.createLineBorder(new Color(215, 204, 200)));
        card.add(scrollDesc);

        card.add(createLabel("Vehicle Image", 30, 280));
        
        JButton btnUpload = new JButton("Choose Image...");
        btnUpload.setBounds(30, 305, 340, 35);
        btnUpload.setBackground(new Color(239, 235, 233)); 
        btnUpload.setForeground(textColor);
        btnUpload.setFocusPainted(false);
        card.add(btnUpload);

        lblImagePreview = new JLabel("No Image Selected", SwingConstants.CENTER);
        lblImagePreview.setBounds(120, 350, 160, 100);
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.add(lblImagePreview);

        JButton btnAdd = new JButton("CONFIRM ENTRY");
        btnAdd.setBounds(30, 520, 340, 50);
        btnAdd.setBackground(btnColor);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setFocusPainted(false);
        card.add(btnAdd);

        frame.add(card);

        btnUpload.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedImagePath = selectedFile.getAbsolutePath();
                
                ImageIcon icon = new ImageIcon(selectedImagePath);
                Image img = icon.getImage().getScaledInstance(160, 100, Image.SCALE_SMOOTH);
                lblImagePreview.setIcon(new ImageIcon(img));
                lblImagePreview.setText("");
            }
        });

        btnAdd.addActionListener(e -> {
            try {
                Properties prop = new Properties();
                prop.load(App.class.getClassLoader().getResourceAsStream("application.properties"));
                Connection con = DriverManager.getConnection(prop.getProperty("db.url"), prop.getProperty("db.username"), prop.getProperty("db.password"));
                
                String sql = "INSERT INTO cars (model, price, qty, image_path, description) VALUES (?, ?, 1, ?, ?)";
                PreparedStatement pst = con.prepareStatement(sql);
                
                pst.setString(1, txtModel.getText());
                pst.setDouble(2, Double.parseDouble(txtPrice.getText()));
                pst.setString(3, selectedImagePath); 
                pst.setString(4, txtDesc.getText()); 
                
                pst.executeUpdate();
                con.close();
                JOptionPane.showMessageDialog(frame, "Vehicle Added Successfully!");
                
                txtModel.setText(""); txtPrice.setText(""); txtDesc.setText("");
                lblImagePreview.setIcon(null); lblImagePreview.setText("No Image");
                selectedImagePath = null;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setBounds(x, y, 340, 20);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(141, 110, 99));
        return l;
    }

    private JTextField createInput(int x, int y) {
        JTextField t = new JTextField();
        t.setBounds(x, y, 340, 40);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBackground(new Color(250, 250, 250));
        t.setBorder(BorderFactory.createLineBorder(new Color(215, 204, 200)));
        return t;
    }
}