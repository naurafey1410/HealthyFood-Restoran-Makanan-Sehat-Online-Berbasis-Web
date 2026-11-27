package healthyfood.view;

import healthyfood.model.AkunService;
import healthyfood.util.Validasi;
import javax.swing.*;
import java.awt.*;

/** Login customer. Setelah berhasil, masuk ke halaman pemesanan. Ada juga pendaftaran akun baru. */
public class LoginCustomerFrame extends JPanel {

    private final JTextField txtUser = Gaya.fieldGelap();
    private final JPasswordField txtPass = Gaya.passwordGelap();
    private final JLabel lblInfo = new JLabel(" ", SwingConstants.CENTER);

    public LoginCustomerFrame() {
        setLayout(new BorderLayout());

        JPanel kartu = Gaya.kartu();
        kartu.add(Gaya.lencana());
        kartu.add(Box.createVerticalStrut(20));
        kartu.add(Gaya.judul("Login Customer", 30));
        kartu.add(Box.createVerticalStrut(8));
        kartu.add(Gaya.sub("Masuk untuk memesan makanan sehat"));
        kartu.add(Box.createVerticalStrut(28));

        kartu.add(Gaya.blokKolom("Username", txtUser));
        kartu.add(Box.createVerticalStrut(16));
        kartu.add(Gaya.blokKolom("Password", txtPass));
        kartu.add(Box.createVerticalStrut(10));

        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        kartu.add(lblInfo);
        kartu.add(Box.createVerticalStrut(14));

        JButton masuk = Gaya.tombolUtama("Masuk");
        JButton daftar = Gaya.tombolPutih("Daftar Akun Baru");
        JButton kembali = Gaya.tombolLink("← Kembali ke pilihan masuk");
        kartu.add(masuk);
        kartu.add(Box.createVerticalStrut(12));
        kartu.add(daftar);
        kartu.add(Box.createVerticalStrut(22));
        kartu.add(kembali);

        masuk.addActionListener(e -> prosesMasuk());
        daftar.addActionListener(e -> dialogDaftar());
        kembali.addActionListener(e -> Navigasi.buka("pilihan", new LayarPilihan()));
        SwingUtilities.invokeLater(() -> getRootPane().setDefaultButton(masuk));

        Gaya.kunciLebar(kartu);
        add(Gaya.latar(kartu), BorderLayout.CENTER);
    }

    // === LOGIKA LOGIN ===
    private void prosesMasuk() {
        AkunService.Customer akun;
        try {
            akun = AkunService.cekCustomer(txtUser.getText().trim(), new String(txtPass.getPassword()));
        } catch (IllegalStateException ex) {
            lblInfo.setForeground(Color.RED);
            lblInfo.setText("Database tidak terhubung. Jalankan MySQL lalu coba lagi.");
            return;
        }
        if (akun == null) {
            lblInfo.setForeground(Color.RED);
            lblInfo.setText("Username atau password salah!");
            txtPass.setText("");
            return;
        }
        Navigasi.buka("customer", new Tugasgui(akun));
    }

    // === DIALOG DAFTAR AKUN ===
    private void dialogDaftar() {
        JTextField nama = kotakDialog(), telp = kotakDialog(), user = kotakDialog();
        JPasswordField pass = new JPasswordField();
        pass.setPreferredSize(new Dimension(230, 34));
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 12));
        p.add(new JLabel("Nama")); p.add(nama);
        p.add(new JLabel("No. HP")); p.add(telp);
        p.add(new JLabel("Username")); p.add(user);
        p.add(new JLabel("Password")); p.add(pass);
        JPanel wadah = new JPanel(new GridBagLayout());
        wadah.setPreferredSize(new Dimension(480, 340));
        wadah.add(p);

        if (JOptionPane.showConfirmDialog(this, wadah, "Daftar Akun Customer",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        String u = user.getText().trim();
        String pw = new String(pass.getPassword());
        String noHP = telp.getText().trim();
        String err = Validasi.daftarCustomer(nama.getText().trim(), noHP, u, pw);
        if (err != null) {
            JOptionPane.showMessageDialog(this, err);
            return;
        }
        boolean tersimpan;
        try {
            tersimpan = AkunService.daftar(u, pw, nama.getText().trim(), noHP);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }
        if (!tersimpan) {
            JOptionPane.showMessageDialog(this, "Username sudah dipakai.");
            return;
        }
        lblInfo.setForeground(new Color(30, 110, 55));
        lblInfo.setText("Akun berhasil dibuat. Silakan masuk.");
        txtUser.setText(u);
        txtPass.setText("");
    }

    // Kotak isian persegi panjang untuk dialog daftar
    private JTextField kotakDialog() {
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(230, 34));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 205, 182)), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return f;
    }
}
