package car.dealership;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class SalesMenuWindow {

    public SalesMenuWindow() {
        JFrame frame = new JFrame("Sales Operations");
        frame.setSize(500, 400);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        Color bg = new Color(244, 241, 234); 
        frame.getContentPane().setBackground(bg);
        URL iconURL = getClass().getResource("/logo.png");
        if (iconURL != null) frame.setIconImage(new ImageIcon(iconURL).getImage());

        JLabel title = new JLabel("SALES DEPARTMENT", SwingConstants.CENTER);
        title.setBounds(0, 40, 500, 30);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(62, 39, 35));
        frame.add(title);

        // Button 1: New Invoice
        JButton btnInvoice = createButton("GENERATE NEW INVOICE", 80, 100);
        btnInvoice.addActionListener(e -> {
            new InvoiceWindow(); // Opens the file we made previously
            frame.dispose(); // Close this menu
        });

        // Button 2: View History
        JButton btnReport = createButton("VIEW SALES REPORTS", 80, 180);
        btnReport.addActionListener(e -> {
            new SalesReportWindow(); // Opens the new report window
            frame.dispose();
        });

        frame.add(btnInvoice);
        frame.add(btnReport);
        
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private JButton createButton(String text, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 340, 60);
        btn.setBackground(new Color(93, 64, 55)); // Leather Brown
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        return btn;
    }
}