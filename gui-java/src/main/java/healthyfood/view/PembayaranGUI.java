package healthyfood.view;

import healthyfood.model.Gambar;
import healthyfood.model.OrderStore;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Halaman pembayaran setelah pesanan dibuat: ringkasan, metode bayar, dan konfirmasi. */
public class PembayaranGUI extends JPanel {

    // === UKURAN & LATAR (ubah angka di sini) ===
    private static final int LEBAR_KARTU = 640;       // lebar card putih (piksel)
    private static final int JARAK_LUAR = 24;         // jarak card ke tepi halaman (piksel)
    private static final Color WARNA_LATAR = new Color(240, 244, 234);  // warna area di luar card

    // === WARNA ===
    private static final Color GREEN = new Color(46, 110, 44);
    private static final Color DARK_GREEN = new Color(38, 95, 36);
    private static final Color TEKS = new Color(55, 61, 54);
    private static final Color MUTED = new Color(113, 122, 112);
    private static final Color KOTAK_RINGKASAN = new Color(245, 249, 237);
    private static final Color KOTAK_BAYAR = new Color(252, 253, 246);
    private static final Color GARIS_HIJAU = new Color(153, 204, 84);
    private static final Color GARIS_ABU = new Color(214, 220, 208);
    private static final Color YELLOW = new Color(255, 241, 197);

    private final OrderStore.Order order;
    private int step = 1;
    private JPanel stepper;
    private JLabel statusLabel;
    private JButton action;

    public PembayaranGUI() { this(null); }

    public PembayaranGUI(OrderStore.Order order) {
        this.order = order;
        setLayout(new BorderLayout());
        setBackground(WARNA_LATAR);

        // === KARTU PUTIH DI TENGAH ===
        JPanel kartu = new JPanel();
        kartu.setLayout(new BoxLayout(kartu, BoxLayout.Y_AXIS));
        kartu.setBackground(Color.WHITE);
        kartu.setBorder(new EmptyBorder(28, 36, 28, 36));

        kartu.add(judul("Order Diterima!", 27, Font.BOLD, DARK_GREEN));
        kartu.add(Box.createVerticalStrut(6));
        kartu.add(judul("Selesaikan pembayaran agar pesanan dapat diproses.", 15, Font.PLAIN, new Color(85, 91, 83)));
        kartu.add(Box.createVerticalStrut(20));

        stepper = new JPanel(new GridLayout(1, 3, 8, 0));
        stepper.setOpaque(false);
        stepper.setAlignmentX(Component.CENTER_ALIGNMENT);
        stepper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        kartu.add(stepper);
        kartu.add(Box.createVerticalStrut(22));

        kartu.add(kotakRingkasan());
        kartu.add(Box.createVerticalStrut(18));
        kartu.add(kotakBayar());
        kartu.add(Box.createVerticalStrut(14));

        statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setForeground(MUTED);
        kartu.add(statusLabel);
        kartu.add(Box.createVerticalStrut(14));

        // === TOMBOL KONFIRMASI ===
        action = new JButton("Saya sudah transfer");
        stylePrimary(action);
        action.setAlignmentX(Component.CENTER_ALIGNMENT);
        action.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        action.addActionListener(e -> klikKonfirmasi());
        kartu.add(action);

        // Lebar kartu tetap 640 piksel, tingginya mengikuti isi
        kartu.setPreferredSize(new Dimension(LEBAR_KARTU, kartu.getPreferredSize().height));

        JPanel tengah = new JPanel(new GridBagLayout());
        tengah.setOpaque(false);
        tengah.setBorder(new EmptyBorder(JARAK_LUAR, JARAK_LUAR, JARAK_LUAR, JARAK_LUAR));
        tengah.add(kartu);

        add(tengah, BorderLayout.CENTER);
        updateStepper();
    }

    // === AKSI TOMBOL: customer hanya bisa kirim ke verifikasi admin ===
    private void klikKonfirmasi() {
        if (step == 1) {
            step = 2;
            if (order != null) order.status = OrderStore.MENUNGGU_VERIF;
            statusLabel.setText("<html><div style='text-align:center'>Pembayaran sedang diverifikasi admin.<br>Status pesanan: Verifikasi.</div></html>");
            statusLabel.setOpaque(true);
            statusLabel.setBackground(YELLOW);
            statusLabel.setBorder(new EmptyBorder(12, 8, 12, 8));
            action.setText("Tutup");
            updateStepper();
        } else {
            // Tutup jendela pembayaran, kembali ke halaman customer
            Window jendela = SwingUtilities.getWindowAncestor(this);
            if (jendela != null) jendela.dispose();
        }
    }

    // === RINGKASAN PESANAN (grid: baris dihitung eksplisit) ===
    private JPanel kotakRingkasan() {
        JPanel kotak = new JPanel(new GridBagLayout());
        kotak.setBackground(KOTAK_RINGKASAN);
        kotak.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GARIS_HIJAU, 1),
                new EmptyBorder(16, 18, 16, 18)));
        kotak.setAlignmentX(Component.CENTER_ALIGNMENT);

        int r = 0;
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = r++; c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 12, 0);
        JLabel badge = new JLabel("  " + (order == null ? "Pesanan" : "Pesanan #" + order.id) + "  ");
        badge.setOpaque(true);
        badge.setBackground(GREEN);
        badge.setForeground(Color.WHITE);
        badge.setFont(new Font("SansSerif", Font.BOLD, 14));
        badge.setBorder(new EmptyBorder(6, 8, 6, 8));
        kotak.add(badge, c);

        r = barisInfo(kotak, r, "Nama", order == null ? "Contoh Customer" : order.nama);
        r = barisInfo(kotak, r, "Telepon", order == null ? "-" : order.telepon);
        r = barisInfo(kotak, r, "Alamat", order == null ? "-" : order.alamat);
        r = barisInfo(kotak, r, "Metode", order == null ? "Transfer" : order.metode);

        r = garis(kotak, r);
        if (order == null) r = barisTeks(kotak, r, "Contoh pesanan Rp 28.000");
        else for (String item : order.rincian) r = barisTeks(kotak, r, item);
        r = garis(kotak, r);

        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = r; c.anchor = GridBagConstraints.WEST; c.weightx = 1;
        c.insets = new Insets(4, 0, 0, 0);
        kotak.add(teksLabel("Total", 15, Font.BOLD, TEKS), c);
        c = new GridBagConstraints();
        c.gridx = 1; c.gridy = r; c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(4, 0, 0, 0);
        kotak.add(teksLabel(order == null ? "Rp 28.000" : order.rupiah(), 16, Font.BOLD, GREEN), c);

        kotak.setMaximumSize(new Dimension(Integer.MAX_VALUE, kotak.getPreferredSize().height));
        return kotak;
    }

    // Baris "Label   Nilai"; mengembalikan nomor baris berikutnya
    private int barisInfo(JPanel kotak, int r, String label, String nilai) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = r; c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(2, 0, 2, 18);
        kotak.add(teksLabel(label, 14, Font.BOLD, DARK_GREEN), c);
        c = new GridBagConstraints();
        c.gridx = 1; c.gridy = r; c.anchor = GridBagConstraints.WEST; c.weightx = 1;
        c.insets = new Insets(2, 0, 2, 0);
        kotak.add(teksLabel(nilai, 14, Font.PLAIN, TEKS), c);
        return r + 1;
    }

    private int barisTeks(JPanel kotak, int r, String teks) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = r; c.gridwidth = 2; c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(3, 0, 3, 0);
        kotak.add(teksLabel(teks, 14, Font.PLAIN, TEKS), c);
        return r + 1;
    }

    private int garis(JPanel kotak, int r) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = r; c.gridwidth = 2; c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 0, 10, 0);
        JSeparator garis = new JSeparator();
        garis.setForeground(GARIS_ABU);
        kotak.add(garis, c);
        return r + 1;
    }

    // === BAGIAN BAYAR: QRIS atau rekening bank ===
    private JPanel kotakBayar() {
        JPanel kotak = new JPanel();
        kotak.setLayout(new BoxLayout(kotak, BoxLayout.Y_AXIS));
        kotak.setBackground(KOTAK_BAYAR);
        kotak.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GARIS_ABU, 1),
                new EmptyBorder(18, 18, 18, 18)));
        kotak.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (order != null && "QRIS".equals(order.metode)) {
            kotak.add(teksTengah("Scan QRIS untuk membayar", 15, Font.BOLD, DARK_GREEN));
            kotak.add(Box.createVerticalStrut(12));
            kotak.add(gambarQris());
        } else {
            kotak.add(teksTengah("Transfer ke rekening berikut", 15, Font.BOLD, DARK_GREEN));
            kotak.add(Box.createVerticalStrut(10));
            kotak.add(teksTengah("Bank Mandiri", 15, Font.BOLD, TEKS));
            kotak.add(Box.createVerticalStrut(4));
            kotak.add(teksTengah("1234567890", 28, Font.BOLD, GREEN));
            kotak.add(Box.createVerticalStrut(4));
            kotak.add(teksTengah("a.n. Healthy Restaurant", 14, Font.PLAIN, TEKS));
        }

        kotak.add(Box.createVerticalStrut(14));
        JSeparator garis = new JSeparator();
        garis.setForeground(GARIS_ABU);
        garis.setAlignmentX(Component.CENTER_ALIGNMENT);
        kotak.add(garis);
        kotak.add(Box.createVerticalStrut(12));
        kotak.add(teksTengah("Total: " + (order == null ? "Rp 28.000" : order.rupiah()), 17, Font.BOLD, GREEN));

        kotak.setMaximumSize(new Dimension(Integer.MAX_VALUE, kotak.getPreferredSize().height));
        return kotak;
    }

    // Gambar QRIS dari gambar/qris.png (cek juga di hf2/gambar/qris.png)
    private JLabel gambarQris() {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        ImageIcon ikon = Gambar.ikon("qris.png");
        if (ikon != null) {
            label.setIcon(new ImageIcon(ikon.getImage().getScaledInstance(220, 220, Image.SCALE_SMOOTH)));
        } else {
            label.setText("<html><center>Gambar QRIS belum ada.<br>Simpan di gambar/qris.png</center></html>");
            label.setForeground(MUTED);
        }
        return label;
    }

    // === STEPPER 1-2-3 ===
    private void updateStepper() {
        stepper.removeAll();
        String[] labels = {"Pembayaran", "Verifikasi Admin", "Lunas"};
        for (int i = 1; i <= 3; i++) {
            JPanel item = new JPanel();
            item.setOpaque(false);
            item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));

            JLabel circle = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            circle.setOpaque(true);
            circle.setPreferredSize(new Dimension(32, 32));
            circle.setMaximumSize(new Dimension(32, 32));
            circle.setAlignmentX(Component.CENTER_ALIGNMENT);
            circle.setFont(new Font("SansSerif", Font.BOLD, 14));
            circle.setForeground(Color.WHITE);
            circle.setBackground(i < step ? GREEN : (i == step ? new Color(230, 166, 0) : new Color(222, 224, 221)));

            JLabel text = new JLabel(labels[i - 1]);
            text.setAlignmentX(Component.CENTER_ALIGNMENT);
            text.setFont(new Font("SansSerif", i == step ? Font.BOLD : Font.PLAIN, 12));
            text.setForeground(i <= step ? DARK_GREEN : MUTED);

            item.add(circle);
            item.add(Box.createVerticalStrut(5));
            item.add(text);
            stepper.add(item);
        }
        stepper.revalidate();
        stepper.repaint();
    }

    // === HELPER TAMPILAN ===
    private JLabel judul(String teks, int ukuran, int gaya, Color warna) {
        JLabel l = new JLabel(teks);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setFont(new Font("SansSerif", gaya, ukuran));
        l.setForeground(warna);
        return l;
    }

    private JLabel teksTengah(String teks, int ukuran, int gaya, Color warna) {
        JLabel l = teksLabel(teks, ukuran, gaya, warna);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JLabel teksLabel(String teks, int ukuran, int gaya, Color warna) {
        JLabel l = new JLabel(teks);
        l.setFont(new Font("SansSerif", gaya, ukuran));
        l.setForeground(warna);
        return l;
    }

    private void stylePrimary(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(GREEN);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(12, 16, 12, 16));
    }
}
