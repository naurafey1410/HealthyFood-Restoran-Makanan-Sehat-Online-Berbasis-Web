package healthyfood.view;

import healthyfood.model.OrderStore;
import healthyfood.model.ProdukStore;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

/** GUI Healthy Restaurant - Menu, Keranjang, dan Form Pesanan */
public class Tugasgui extends JPanel {

    private static final Color HIJAU_TUA = new Color(0x265F24), HIJAU_BTN = new Color(0x2E6E2C),
            HIJAU_MUDA = new Color(0xD4EC9A), HIJAU_BG = new Color(0xE4EFD0),
            MERAH = new Color(0xD32F2F), ABU = new Color(0xCCCCCC), GARIS = new Color(0xE5E5E5);

    private static class Item {
        String nama; int harga, qty = 1;
        Item(String nama, int harga) { this.nama = nama; this.harga = harga; }
    }

    private final ArrayList<Item> keranjang = new ArrayList<>();
    private final DecimalFormat fmt;
    private final JPanel panelItem = new JPanel(), panelForm = new JPanel();
    private final JLabel lblTotal = new JLabel("Rp 0");
    private final JTextField txtNama = new JTextField(), txtTelepon = new JTextField();
    private final JTextArea txtAlamat = new JTextArea(3, 20);
    private final JToggleButton btnTransfer = new JToggleButton("🏦 Transfer Bank"), btnQris = new JToggleButton("▦ QRIS");
    private final ButtonGroup bg = new ButtonGroup();
    private JScrollPane scrollIsi;
    private JPanel gridMenu;

    public Tugasgui() { this(null, null); }

    // === KONSTRUKTOR: nama & telepon terisi otomatis jika customer sudah login ===
    public Tugasgui(String namaCustomer, String telpCustomer) {
        DecimalFormatSymbols sym = new DecimalFormatSymbols(Locale.US);
        sym.setGroupingSeparator('.');
        fmt = new DecimalFormat("#,###", sym);

        setLayout(new BorderLayout());
        add(buatPanelMenu(), BorderLayout.CENTER);
        add(buatPanelPesanan(), BorderLayout.EAST);
        if (namaCustomer != null) {
            txtNama.setText(namaCustomer); txtNama.setForeground(Color.BLACK);
            txtTelepon.setText(telpCustomer); txtTelepon.setForeground(Color.BLACK);
        }
        refreshKeranjang();
    }

    // ================= PANEL MENU (KIRI) =================
    private JPanel buatPanelMenu() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(HIJAU_BG);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(teks("Healthy Food", Font.BOLD, 28, HIJAU_TUA));
        header.add(teks("Start your journey toward a healthy lifestyle by consuming foods rich in fiber and vitamins for a brilliant future", Font.PLAIN, 14, Color.BLACK));
        root.add(header, BorderLayout.NORTH);

        // === TOMBOL LOGIN ADMIN (kecil, di bawah) ===
        JButton adminLogin = tombolHijau("Login Admin");
        adminLogin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        adminLogin.setBorder(new EmptyBorder(6, 14, 6, 14));
        adminLogin.addActionListener(e -> Navigasi.buka("login-admin", new LoginFrame()));
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(16, 0, 0, 0));
        footer.add(adminLogin);
        root.add(footer, BorderLayout.SOUTH);
        
        // === DAFTAR MENU (dari ProdukStore, sama dengan halaman admin) ===
        gridMenu = new JPanel(new GridLayout(0, 3, 20, 20));
        gridMenu.setOpaque(false);
        muatMenu();
        JScrollPane scrollMenu = new JScrollPane(gridMenu);
        scrollMenu.setBorder(null);
        scrollMenu.setOpaque(false);
        scrollMenu.getViewport().setOpaque(false);
        scrollMenu.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scrollMenu, BorderLayout.CENTER);

        return root;
    }

    private void muatMenu() {
        gridMenu.removeAll();
        for (ProdukStore.Produk p : ProdukStore.getAll()) gridMenu.add(buatKartuMenu(p));
        gridMenu.revalidate();
        gridMenu.repaint();
    }

    private JPanel buatKartuMenu(ProdukStore.Produk p) {
        String nama = p.nama, kalori = p.kalori;
        int harga = p.harga;
        JPanel kartu = new JPanel();
        kartu.setLayout(new BoxLayout(kartu, BoxLayout.Y_AXIS));
        kartu.setBackground(Color.WHITE);
        kartu.setBorder(new EmptyBorder(20, 18, 18, 18));

        JLabel gambar;
        if (p.foto instanceof ImageIcon) {
            Image img = ((ImageIcon) p.foto).getImage()
                    .getScaledInstance(130, 130, Image.SCALE_SMOOTH);
            gambar = kiri(new JLabel(new ImageIcon(img), SwingConstants.CENTER));
        } else {
            gambar = kiri(new JLabel(String.valueOf(p.foto), SwingConstants.CENTER));
        }
        
        gambar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        gambar.setOpaque(true);
        gambar.setBackground(new Color(0xF1F5E8));
        gambar.setPreferredSize(new Dimension(100, 150));
        gambar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel lblKalori = teks(kalori, Font.PLAIN, 12, Color.DARK_GRAY);
        JButton btnOrder = tombolHijau("Order");
        btnOrder.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnOrder.addActionListener(e -> tambahKeKeranjang(nama, harga));

        susun(kartu, gambar, 12, teks(nama, Font.BOLD, 18, HIJAU_TUA), 4,
                teks(rp(harga), Font.BOLD, 18, HIJAU_TUA), 10, lblKalori, 14, btnOrder);
        return kartu;
    }

    // ================= PANEL KANAN =================
    private JPanel buatPanelPesanan() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(430, 720));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HIJAU_MUDA);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel judul = new JLabel("🛒 Pesanan");
        judul.setFont(new Font("Segoe UI Emoji", Font.BOLD, 22));
        judul.setForeground(HIJAU_TUA);
        JButton btnTutup = tombolIkon("X", HIJAU_TUA, 18);
        btnTutup.addActionListener(e -> Navigasi.buka("login-customer", new LoginCustomerFrame()));
        header.add(judul, BorderLayout.WEST);
        header.add(btnTutup, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Isi: daftar item + form (tersembunyi dulu)
        panelItem.setLayout(new BoxLayout(panelItem, BoxLayout.Y_AXIS));
        panelItem.setBackground(Color.WHITE);
        buatPanelForm();
        panelForm.setVisible(false);

        JPanel isi = new JPanel(new BorderLayout());
        isi.setBackground(Color.WHITE);
        isi.add(panelItem, BorderLayout.NORTH);
        isi.add(panelForm, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(isi, BorderLayout.NORTH);
        wrapper.setBackground(Color.WHITE);

        scrollIsi = new JScrollPane(wrapper);
        scrollIsi.setBorder(null);
        scrollIsi.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollIsi.getVerticalScrollBar().setUnitIncrement(14);
        panel.add(scrollIsi, BorderLayout.CENTER);

        // Bawah: total + tombol konfirmasi
        JPanel bawah = new JPanel();
        bawah.setLayout(new BoxLayout(bawah, BoxLayout.Y_AXIS));
        bawah.setBackground(Color.WHITE);
        bawah.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, GARIS), new EmptyBorder(16, 20, 20, 20)));

        JPanel barisTotal = new JPanel(new BorderLayout());
        barisTotal.setOpaque(false);
        barisTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setForeground(HIJAU_TUA);
        barisTotal.add(new JLabel("Total"), BorderLayout.WEST);
        barisTotal.add(lblTotal, BorderLayout.EAST);

        JButton btnKonfirmasi = tombolHijau("Konfirmasi Pesanan");
        btnKonfirmasi.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnKonfirmasi.setBackground(new Color(0x2E6E2C));
        btnKonfirmasi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnKonfirmasi.setPreferredSize(new Dimension(100, 48));
        btnKonfirmasi.addActionListener(e -> {
            if (keranjang.isEmpty()) { pesan("Keranjang masih kosong!"); return; }
            if (!panelForm.isVisible()) {            // klik pertama: tampilkan form
                panelForm.setVisible(true);
                panelForm.revalidate();
                SwingUtilities.invokeLater(() -> {
                    JScrollBar sb = scrollIsi.getVerticalScrollBar();
                    sb.setValue(sb.getMaximum());
                });
            } else selesaikanPesanan();              // klik berikutnya: proses
        });
        susun(bawah, barisTotal, 12, btnKonfirmasi);
        panel.add(bawah, BorderLayout.SOUTH);
        return panel;
    }

    // ---- Form data pemesan ----
    private void buatPanelForm() {
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(new EmptyBorder(18, 20, 18, 20));

        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        txtAlamat.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane spAlamat = kiri(new JScrollPane(txtAlamat));
        spAlamat.setBorder(new LineBorder(ABU, 1, true));
        spAlamat.setPreferredSize(new Dimension(100, 90));
        spAlamat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        bg.add(btnTransfer);
        bg.add(btnQris);
        for (JToggleButton b : new JToggleButton[]{btnTransfer, btnQris}) {
            b.setFocusPainted(false);
            b.setBackground(Color.WHITE);
            b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            b.setBorder(new LineBorder(ABU, 1, true));
            b.addActionListener(e -> warnaBayar());
        }
        JPanel pnlBayar = kiri(new JPanel(new GridLayout(1, 2, 10, 0)));
        pnlBayar.setOpaque(false);
        pnlBayar.setPreferredSize(new Dimension(100, 42));
        pnlBayar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        pnlBayar.add(btnTransfer);
        pnlBayar.add(btnQris);

        susun(panelForm,
                teks("Nama Pemesan:", Font.BOLD, 14, Color.BLACK), 6, fieldDenganHint(txtNama, "Masukkan nama Anda..."), 12,
                teks("Nomor Telepon:", Font.BOLD, 14, Color.BLACK), 6, fieldDenganHint(txtTelepon, "Contoh: 08123456789"), 12,
                teks("Alamat Lengkap:", Font.BOLD, 14, Color.BLACK), 6, spAlamat, 12,
                teks("Metode Pembayaran:", Font.BOLD, 14, Color.BLACK), 6, pnlBayar);
    }

    private void warnaBayar() {
        btnTransfer.setBackground(btnTransfer.isSelected() ? HIJAU_MUDA : Color.WHITE);
        btnQris.setBackground(btnQris.isSelected() ? HIJAU_MUDA : Color.WHITE);
    }

    // ================= LOGIKA =================
    private void tambahKeKeranjang(String nama, int harga) {
        for (Item it : keranjang)
            if (it.nama.equals(nama)) { it.qty++; refreshKeranjang(); return; }
        keranjang.add(new Item(nama, harga));
        refreshKeranjang();
    }

    private void refreshKeranjang() {
        panelItem.removeAll();
        if (keranjang.isEmpty()) {
            JLabel kosong = new JLabel("🛒 Keranjang masih kosong", SwingConstants.CENTER);
            kosong.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            kosong.setForeground(Color.GRAY);
            kosong.setPreferredSize(new Dimension(100, 120));
            kosong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            panelItem.add(kosong);
        }
        int total = 0;
        for (Item it : keranjang) {
            total += it.harga * it.qty;
            panelItem.add(buatBarisItem(it));
        }
        lblTotal.setText(rp(total));
        panelItem.revalidate();
        panelItem.repaint();
    }

    private JPanel buatBarisItem(Item it) {
        JPanel baris = new JPanel(new BorderLayout());
        baris.setBackground(Color.WHITE);
        baris.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, GARIS), new EmptyBorder(14, 20, 14, 20)));
        baris.setPreferredSize(new Dimension(100, 78));
        baris.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        info.add(new JLabel(it.nama) {{ setFont(new Font("Segoe UI", Font.BOLD, 15)); }});
        info.add(teks(rp(it.harga), Font.BOLD, 14, HIJAU_TUA));

        JButton minus = tombolBulat("-"), plus = tombolBulat("+");
        JButton hapus = tombolIkon("🗑", MERAH, 16);
        hapus.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        JLabel qty = new JLabel(String.valueOf(it.qty), SwingConstants.CENTER);
        qty.setPreferredSize(new Dimension(28, 28));

        minus.addActionListener(e -> { if (--it.qty < 1) keranjang.remove(it); refreshKeranjang(); });
        plus.addActionListener(e -> { it.qty++; refreshKeranjang(); });
        hapus.addActionListener(e -> { keranjang.remove(it); refreshKeranjang(); });

        JPanel kontrol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        kontrol.setOpaque(false);
        for (JComponent c : new JComponent[]{minus, qty, plus, hapus}) kontrol.add(c);

        baris.add(info, BorderLayout.WEST);
        baris.add(kontrol, BorderLayout.EAST);
        return baris;
    }

    private void selesaikanPesanan() {
        String nama = isiField(txtNama), telp = isiField(txtTelepon), alamat = txtAlamat.getText().trim();

        if (nama.isEmpty() || telp.isEmpty() || alamat.isEmpty()) {
            pesan("Nama, nomor telepon, dan alamat wajib diisi!"); return;
        }
        if (!telp.matches("\\d{9,15}")) {
            pesan("Nomor telepon harus berupa angka (9-15 digit)."); return;
        }
        if (bg.getSelection() == null) {
            pesan("Pilih metode pembayaran terlebih dahulu!"); return;
        }

        String metode = btnTransfer.isSelected() ? "Transfer Bank" : "QRIS";
        java.util.List<String> rincian = new java.util.ArrayList<>();
        int total = 0;
        for (Item it : keranjang) {
            int sub = it.harga * it.qty;
            total += sub;
            rincian.add(it.nama + " x" + it.qty + " = " + rp(sub));
        }
        OrderStore.Order order = OrderStore.add(nama, telp, alamat, metode, rincian, total);
        JOptionPane.showMessageDialog(this, "Pesanan berhasil dibuat!\nNomor pesanan: #" + order.id +
                "\nTotal: " + order.rupiah() + "\nAnda akan diarahkan ke halaman pembayaran.",
                "Pesanan Berhasil", JOptionPane.INFORMATION_MESSAGE);
        Navigasi.buka("pembayaran", new PembayaranGUI(order));

        // Reset
        keranjang.clear();
        resetHint(txtNama);
        resetHint(txtTelepon);
        txtAlamat.setText("");
        bg.clearSelection();
        warnaBayar();
        refreshKeranjang();
        panelForm.setVisible(false);
    }

    // ================= HELPER =================
    private String rp(int angka) { return "Rp " + fmt.format(angka); }

    private void pesan(String teks) {
        JOptionPane.showMessageDialog(this, teks, "Informasi", JOptionPane.WARNING_MESSAGE);
    }

    /** Tambah komponen; Integer = jarak vertikal. */
    private void susun(JPanel p, Object... xs) {
        for (Object x : xs) p.add(x instanceof Integer ? Box.createVerticalStrut((Integer) x) : (Component) x);
    }

    private <T extends JComponent> T kiri(T c) { c.setAlignmentX(Component.LEFT_ALIGNMENT); return c; }

    private JLabel teks(String t, int style, int size, Color warna) {
        JLabel l = kiri(new JLabel(t));
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(warna);
        return l;
    }

    private void resetHint(JTextField f) {
        f.setText((String) f.getClientProperty("hint"));
        f.setForeground(Color.GRAY);
    }

    /** Isi field; placeholder dianggap kosong. */
    private String isiField(JTextField f) {
        String t = f.getText().trim();
        return t.equals(f.getClientProperty("hint")) ? "" : t;
    }

    /** Text field dengan placeholder abu-abu. */
    private JTextField fieldDenganHint(JTextField f, String hint) {
        f.putClientProperty("hint", hint);
        resetHint(f);
        f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(ABU, 1, true), new EmptyBorder(8, 10, 8, 10)));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setPreferredSize(new Dimension(100, 42));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (f.getText().equals(hint)) { f.setText(""); f.setForeground(Color.BLACK); }
            }
            @Override public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) resetHint(f);
            }
        });
        return f;
    }

    private JButton tombolHijau(String t) {
        JButton b = kiri(new JButton(t));
        b.setBackground(HIJAU_BTN);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Tombol tanpa latar (untuk X dan ikon hapus). */
    private JButton tombolIkon(String t, Color warna, int size) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, size));
        b.setForeground(warna);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton tombolBulat(String t) {
        JButton b = new JButton(t);
        b.setPreferredSize(new Dimension(30, 30));
        b.setMargin(new Insets(0, 0, 0, 0));
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setBackground(HIJAU_MUDA);
        b.setForeground(HIJAU_TUA);
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(HIJAU_MUDA, 1, true));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
