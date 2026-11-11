package healthyfood.view;

import healthyfood.model.AkunStore;
import javax.swing.*;
import java.awt.*;

/** Halaman login customer. Setelah berhasil, customer masuk ke halaman pemesanan. */
public class LoginCustomerFrame extends JPanel {

    private final JTextField txtUser = new JTextField();
    private final JPasswordField txtPass = new JPasswordField();
    private final JLabel lblInfo = new JLabel(" ", SwingConstants.CENTER);

    // === TAMPILAN ===
    public LoginCustomerFrame() {
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 248, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 10));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel judul = new JLabel("Login Customer", SwingConstants.CENTER);
        judul.setFont(new Font("Segoe UI", Font.BOLD, 25));
        judul.setForeground(new Color(25, 80, 35));
        JLabel sub = new JLabel("Masuk untuk memesan makanan sehat", SwingConstants.CENTER);
        sub.setForeground(Color.GRAY);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton masuk = tombol("Masuk", new Color(46, 110, 44), Color.WHITE);
        JButton daftar = tombol("Daftar Akun Baru", new Color(239, 245, 229), new Color(15, 86, 10));
        JButton admin = tombol("Login sebagai Admin", new Color(239, 245, 229), new Color(15, 86, 10));

        form.add(judul);
        form.add(sub);
        form.add(new JLabel("Username"));
        form.add(txtUser);
        form.add(new JLabel("Password"));
        form.add(txtPass);
        form.add(lblInfo);
        form.add(masuk);
        form.add(daftar);
        form.add(admin);
        panel.add(form);
        add(panel);

        // === AKSI TOMBOL ===
        masuk.addActionListener(e -> prosesMasuk());
        daftar.addActionListener(e -> dialogDaftar());
        admin.addActionListener(e -> Navigasi.buka("login-admin", new LoginFrame()));
        SwingUtilities.invokeLater(() -> getRootPane().setDefaultButton(masuk));
    }

    // === LOGIKA LOGIN ===
    private void prosesMasuk() {
        String[] akun = AkunStore.cekCustomer(txtUser.getText().trim(), new String(txtPass.getPassword()));
        if (akun == null) {
            lblInfo.setForeground(Color.RED);
            lblInfo.setText("Username atau password salah!");
            txtPass.setText("");
            return;
        }
        Navigasi.buka("customer", new Tugasgui(akun[1], akun[2]));
    }

    // === DIALOG DAFTAR AKUN ===
    private void dialogDaftar() {
        JTextField nama = new JTextField(), telp = new JTextField(), user = new JTextField();
        JPasswordField pass = new JPasswordField();
        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.add(new JLabel("Nama")); p.add(nama);
        p.add(new JLabel("No. HP")); p.add(telp);
        p.add(new JLabel("Username")); p.add(user);
        p.add(new JLabel("Password")); p.add(pass);

        if (JOptionPane.showConfirmDialog(this, p, "Daftar Akun Customer",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        String u = user.getText().trim();
        String pw = new String(pass.getPassword());
        if (nama.getText().trim().isEmpty() || telp.getText().trim().isEmpty()
                || u.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua kolom wajib diisi.");
            return;
        }
        // Validasi nomor HP: angka, 10-13 digit
        String noHP = telp.getText().trim();
        if (!noHP.matches("[0-9]{10,13}")) {
            JOptionPane.showMessageDialog(this, "Nomor HP harus berupa angka dan terdiri dari 10-13 digit!");
            return;
        }
        // Validasi password minimal 8 karakter
        if (pw.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password minimal harus 8 karakter!");
            return;
        }
        if (!AkunStore.daftar(u, pw, nama.getText().trim(), noHP)) {
            JOptionPane.showMessageDialog(this, "Username sudah dipakai.");
            return;
        }
        lblInfo.setForeground(new Color(30, 110, 55));
        lblInfo.setText("Akun berhasil dibuat. Silakan masuk.");
        txtUser.setText(u);
        txtPass.setText("");
    }

    private JButton tombol(String teks, Color bg, Color fg) {
        JButton b = new JButton(teks);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        return b;
    }
}
