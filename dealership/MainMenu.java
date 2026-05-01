package car.dealership;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MainMenu {
    private JFrame frame;

    public MainMenu() {
        frame = new JFrame("Car Dealership System");
        frame.setSize(900, 600);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        
    //Theme
        Color bg = new Color(244, 241, 234); 
        Color titleColor = new Color(62, 39, 35); 
        frame.getContentPane().setBackground(bg);

        URL iconURL = MainMenu.class.getResource("/logo.png");
        if (iconURL != null) frame.setIconImage(new ImageIcon(iconURL).getImage());

        //HEADER
        JLabel title = new JLabel("DEALERSHIP DASHBOARD", SwingConstants.CENTER);
        title.setBounds(0, 30, 900, 40);
        title.setForeground(titleColor);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        frame.add(title);

        JLabel subtitle = new JLabel("A.Y. 2025-26 • Car Dealership Management", SwingConstants.CENTER);
        subtitle.setBounds(0, 70, 900, 20);
        subtitle.setForeground(new Color(121, 85, 72)); 
        subtitle.setFont(new Font("Serif", Font.ITALIC, 16)); 
        frame.add(subtitle);

        //BUTTONS
        int startX = 130; 
        int btnWidth = 200;
        int gap = 30;
        
        JButton btnAdd   = createTile("Add Car",   startX, 150, btnWidth);
        JButton btnView  = createTile("Inventory", startX + btnWidth + gap, 150, btnWidth);
        JButton btnSales = createTile("Sales",     startX + (btnWidth + gap)*2, 150, btnWidth); 

        JButton btnStaff = createTile("Staff",     startX + (btnWidth/2) + 15, 310, btnWidth);
        JButton btnCust  = createTile("Customers", startX + (btnWidth/2) + 15 + btnWidth + gap, 310, btnWidth);

        btnAdd.addActionListener(e -> new AddCarWindow());
        btnView.addActionListener(e -> new InventoryWindow());
        btnStaff.addActionListener(e -> new StaffWindow());
        btnCust.addActionListener(e -> new CustomerWindow());
        btnSales.addActionListener(e -> new SalesMenuWindow()); 

        frame.add(btnAdd); frame.add(btnView); frame.add(btnSales);
        frame.add(btnStaff); frame.add(btnCust);

        //BOTTOM TEXT (FOOTER)
        JLabel footer = new JLabel("JPR Project • Car Dealership Management System (2025-26)", SwingConstants.CENTER);
        // Positioned at the very bottom (y=530)
        footer.setBounds(0, 530, 900, 20); 
        footer.setForeground(new Color(160, 160, 160)); // Subtle grey color
        footer.setFont(new Font("Segoe UI", Font.BOLD, 12)); 
        frame.add(footer);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JButton createTile(String text, int x, int y, int width) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, width, 130);
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(62, 39, 35)); 
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(215, 204, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(239, 235, 233)); 
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }
}