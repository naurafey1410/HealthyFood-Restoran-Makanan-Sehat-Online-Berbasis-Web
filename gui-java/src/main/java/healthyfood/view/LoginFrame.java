package healthyfood.view;

import healthyfood.model.AkunService;
import javax.swing.*;
import java.awt.*;

/** Login admin. Berhasil masuk ke dashboard admin; tombol kembali ke layar pilihan. */
public class LoginFrame extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel alertLabel;

    public LoginFrame() {
        setLayout(new BorderLayout());

        JPanel kartu = Gaya.kartu();
        kartu.add(Gaya.lencana());
        kartu.add(Box.createVerticalStrut(20));
        kartu.add(Gaya.judul("Admin HealthyFood", 30));
        kartu.add(Box.createVerticalStrut(8));
        kartu.add(Gaya.sub("Masuk untuk mengelola restoran Anda"));
        kartu.add(Box.createVerticalStrut(28));

        usernameField = Gaya.fieldGelap();
        passwordField = Gaya.passwordGelap();
        kartu.add(Gaya.blokKolom("Username", usernameField));
        kartu.add(Box.createVerticalStrut(16));
        kartu.add(Gaya.blokKolom("Password", passwordField));
        kartu.add(Box.createVerticalStrut(10));

        alertLabel = new JLabel(" ", SwingConstants.CENTER);
        alertLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        alertLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        kartu.add(alertLabel);
        kartu.add(Box.createVerticalStrut(14));

        JButton loginButton = Gaya.tombolUtama("Masuk");
        JButton kembali = Gaya.tombolLink("← Kembali ke pilihan masuk");
        kartu.add(loginButton);
        kartu.add(Box.createVerticalStrut(22));
        kartu.add(kembali);

        kembali.addActionListener(e -> Navigasi.buka("pilihan", new LayarPilihan()));
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            boolean adminBenar;
            try {
                adminBenar = AkunService.cekAdmin(username, password);
            } catch (IllegalStateException ex) {
                alertLabel.setText("Database tidak terhubung. Jalankan MySQL lalu coba lagi.");
                alertLabel.setForeground(Color.RED);
                return;
            }
            if (adminBenar) {
                Navigasi.buka("admin", new DashboardProdukGUI());
            } else {
                alertLabel.setText("Username atau password salah!");
                alertLabel.setForeground(Color.RED);
                passwordField.setText("");
            }
        });

        Gaya.kunciLebar(kartu);
        add(Gaya.latar(kartu), BorderLayout.CENTER);
        SwingUtilities.invokeLater(() -> getRootPane().setDefaultButton(loginButton));
    }
}
