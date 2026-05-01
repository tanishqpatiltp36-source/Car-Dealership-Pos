package car.dealership;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.Properties;
import java.net.URL;

public class InventoryWindow {

    JPanel gridPanel;
    JFrame frame;
    String newImagePath = null;

    public InventoryWindow() {
        frame = new JFrame("Premium Collection");
        frame.setSize(1100, 800);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        Color bg = new Color(244, 241, 234);
        Color headerColor = new Color(62, 39, 35);
        frame.getContentPane().setBackground(bg);
        
        URL iconURL = getClass().getResource("/logo.png");
        if (iconURL != null) frame.setIconImage(new ImageIcon(iconURL).getImage());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(bg);
        header.setPreferredSize(new Dimension(1100, 80));
        header.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("CURRENT SHOWROOM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(headerColor);
        header.add(title, BorderLayout.WEST);

        frame.add(header, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(0, 3, 30, 30));
        gridPanel.setBackground(bg);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(bg);
        frame.add(scrollPane, BorderLayout.CENTER);

        loadCars();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void loadCars() {
        gridPanel.removeAll();
        try {
            Connection con = getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM cars");
            while (rs.next()) {
                gridPanel.add(createCarCard(
                    rs.getInt("id"),
                    rs.getString("model"),
                    rs.getDouble("price"),
                    rs.getString("image_path"),
                    rs.getString("description"),
                    rs.getInt("qty") // Fetching qty as Integer
                ));
            }
            con.close();
        } catch (Exception e) { e.printStackTrace(); }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JPanel createCarCard(int id, String model, double price, String imgPath, String desc, int qty) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setPreferredSize(new Dimension(300, 480));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createMatteBorder(1, 1, 3, 1, new Color(215, 204, 200)));

        JLabel lblImage = new JLabel();
        lblImage.setBounds(10, 10, 280, 180);
        lblImage.setBackground(new Color(252, 252, 252));
        lblImage.setOpaque(true);
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);

        if (imgPath != null && !imgPath.isEmpty()) {
            ImageIcon icon = new ImageIcon(imgPath);
            Image img = icon.getImage();
            int imgW = img.getWidth(null);
            int imgH = img.getHeight(null);
            if (imgW > 0 && imgH > 0) {
                double imgAspect = (double) imgW / imgH;
                double boxAspect = (double) 280 / 180;
                int newW, newH;
                if (imgAspect > boxAspect) { newW = 280; newH = (int) (280 / imgAspect); }
                else { newH = 180; newW = (int) (180 * imgAspect); }
                lblImage.setIcon(new ImageIcon(img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH)));
            }
        } else {
            lblImage.setText("No Image");
        }
        card.add(lblImage);

        JLabel lblModel = new JLabel(model);
        lblModel.setBounds(20, 205, 260, 30);
        lblModel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblModel.setForeground(new Color(62, 39, 35));
        card.add(lblModel);

        // Price Label (Left Side)
        JLabel lblPrice = new JLabel("Rs. " + String.format("%,.0f", price));
        lblPrice.setBounds(20, 235, 140, 20);
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPrice.setForeground(new Color(141, 110, 99));
        card.add(lblPrice);

        // Quantity Label (Right Side)
        JLabel lblQtyDisplay = new JLabel("Stock: " + qty);
        lblQtyDisplay.setBounds(170, 235, 110, 20);
        lblQtyDisplay.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblQtyDisplay.setForeground(new Color(93, 64, 55));
        lblQtyDisplay.setHorizontalAlignment(SwingConstants.RIGHT);
        card.add(lblQtyDisplay);

        JTextArea txtDesc = new JTextArea(desc == null ? "No details provided." : desc);
        txtDesc.setBounds(20, 270, 260, 60);
        txtDesc.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        txtDesc.setForeground(Color.GRAY);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false);
        card.add(txtDesc);

        JButton btnEdit = new JButton("EDIT");
        btnEdit.setBounds(20, 350, 120, 35);
        btnEdit.setBackground(new Color(244, 241, 234));
        btnEdit.setForeground(new Color(62, 39, 35));
        btnEdit.setFocusPainted(false);
        
        JButton btnDelete = new JButton("DELETE");
        btnDelete.setBounds(160, 350, 120, 35);
        btnDelete.setBackground(new Color(180, 100, 100));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);

        btnDelete.addActionListener(e -> deleteCar(id));
        btnEdit.addActionListener(e -> showEditDialog(id, model, price, qty, desc, imgPath));

        card.add(btnEdit);
        card.add(btnDelete);

        return card;
    }

    private void deleteCar(int id) {
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this car?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection con = getConnection();
                con.prepareStatement("DELETE FROM cars WHERE id=" + id).executeUpdate();
                con.close();
                loadCars();
            } catch (Exception e) { JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage()); }
        }
    }

    private void showEditDialog(int id, String currentModel, double currentPrice, int currentQty, String currentDesc, String currentImgPath) {
        JDialog dialog = new JDialog(frame, "Edit Vehicle Details", true);
        dialog.setSize(450, 620); // Height increased to accommodate Qty
        dialog.setLayout(null);
        dialog.setLocationRelativeTo(frame);
        dialog.getContentPane().setBackground(new Color(244, 241, 234));

        newImagePath = currentImgPath;

        JLabel lblM = new JLabel("Model Name"); lblM.setBounds(30, 20, 200, 20); dialog.add(lblM);
        JTextField txtModel = new JTextField(currentModel); txtModel.setBounds(30, 45, 360, 35); dialog.add(txtModel);

        JLabel lblP = new JLabel("Price (Rs.)"); lblP.setBounds(30, 90, 200, 20); dialog.add(lblP);
        JTextField txtPrice = new JTextField(String.valueOf(currentPrice)); txtPrice.setBounds(30, 115, 360, 35); dialog.add(txtPrice);

        // New Quantity Slot
        JLabel lblQ = new JLabel("Quantity In Stock"); lblQ.setBounds(30, 160, 200, 20); dialog.add(lblQ);
        JTextField txtQty = new JTextField(String.valueOf(currentQty)); txtQty.setBounds(30, 185, 360, 35); dialog.add(txtQty);

        JLabel lblD = new JLabel("Description"); lblD.setBounds(30, 230, 200, 20); dialog.add(lblD);
        JTextArea txtDesc = new JTextArea(currentDesc); txtDesc.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(txtDesc); scroll.setBounds(30, 255, 360, 70); dialog.add(scroll);

        JLabel lblImgPreview = new JLabel();
        lblImgPreview.setBounds(30, 340, 120, 80);
        lblImgPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblImgPreview.setHorizontalAlignment(SwingConstants.CENTER);
        
        if (currentImgPath != null) {
            ImageIcon icon = new ImageIcon(currentImgPath);
            Image img = icon.getImage().getScaledInstance(120, 80, Image.SCALE_SMOOTH);
            lblImgPreview.setIcon(new ImageIcon(img));
        } else {
            lblImgPreview.setText("No Image");
        }
        dialog.add(lblImgPreview);

        JButton btnChangeImg = new JButton("Change Image");
        btnChangeImg.setBounds(160, 360, 150, 40);
        btnChangeImg.setBackground(new Color(230, 230, 230));
        dialog.add(btnChangeImg);

        btnChangeImg.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png"));
            if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                newImagePath = chooser.getSelectedFile().getAbsolutePath();
                ImageIcon icon = new ImageIcon(newImagePath);
                Image img = icon.getImage().getScaledInstance(120, 80, Image.SCALE_SMOOTH);
                lblImgPreview.setIcon(new ImageIcon(img));
            }
        });

        JButton btnSave = new JButton("SAVE CHANGES");
        btnSave.setBounds(30, 480, 360, 50);
        btnSave.setBackground(new Color(93, 64, 55));
        btnSave.setForeground(Color.WHITE);
        dialog.add(btnSave);

        btnSave.addActionListener(e -> {
            try {
                Connection con = getConnection();
                PreparedStatement pst = con.prepareStatement("UPDATE cars SET model=?, price=?, qty=?, description=?, image_path=? WHERE id=?");
                pst.setString(1, txtModel.getText());
                pst.setDouble(2, Double.parseDouble(txtPrice.getText()));
                pst.setInt(3, Integer.parseInt(txtQty.getText()));
                pst.setString(4, txtDesc.getText());
                pst.setString(5, newImagePath);
                pst.setInt(6, id);
                pst.executeUpdate();
                con.close();
                dialog.dispose();
                loadCars();
            } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage()); }
        });

        dialog.setVisible(true);
    }

    private Connection getConnection() throws Exception {
        Properties prop = new Properties();
        prop.load(App.class.getClassLoader().getResourceAsStream("application.properties"));
        return DriverManager.getConnection(prop.getProperty("db.url"), prop.getProperty("db.username"), prop.getProperty("db.password"));
    }
}