package healthyfood.view;

import healthyfood.model.AkunStore;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel alertLabel;

    public LoginFrame() {
        setTitle("HealthyFood - Login Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 540);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 248, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 10));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel judul = new JLabel("Admin HealthyFood", SwingConstants.CENTER);
        judul.setFont(new Font("Segoe UI", Font.BOLD, 25));
        judul.setForeground(new Color(25, 80, 35));

        JLabel subtitle = new JLabel(
            "Masuk untuk mengelola restoran Anda",
            SwingConstants.CENTER
        );
        subtitle.setForeground(Color.GRAY);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton loginButton = new JButton("Masuk");
        loginButton.setBackground(new Color(46, 110, 44));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setFocusPainted(false);

        alertLabel = new JLabel(" ", SwingConstants.CENTER);
        alertLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton kembaliButton = new JButton("← Kembali ke login customer");
        kembaliButton.setForeground(new Color(15, 100, 35));
        kembaliButton.setBorderPainted(false);
        kembaliButton.setContentAreaFilled(false);
        kembaliButton.setFocusPainted(false);

        form.add(judul);
        form.add(subtitle);
        form.add(new JLabel("Username"));
        form.add(usernameField);
        form.add(new JLabel("Password"));
        form.add(passwordField);
        form.add(alertLabel);
        form.add(loginButton);
        form.add(kembaliButton);
        kembaliButton.addActionListener(e -> {
            new LoginCustomerFrame();
            dispose();
        });

        panel.add(form);
        add(panel);

        // === AKSI LOGIN ADMIN ===
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (AkunStore.cekAdmin(username, password)) {

                new DashboardProdukGUI().setVisible(true);
                dispose();

            } else {
                alertLabel.setText("Username atau password salah!");
                alertLabel.setForeground(Color.RED);
                passwordField.setText("");
            }
        });

        getRootPane().setDefaultButton(loginButton);
        setVisible(true);
    }

    public static void main(String[] args) {
        Tema.pakai();
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
