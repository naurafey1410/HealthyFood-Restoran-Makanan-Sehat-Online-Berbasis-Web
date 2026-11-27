package healthyfood.view;

import healthyfood.util.Gambar;
import healthyfood.model.AkunService;
import healthyfood.model.OrderService;
import healthyfood.model.KontenStore;
import healthyfood.model.ProdukStore;
import healthyfood.util.Validasi;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
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
        int itemId;
        Item(int itemId, String nama, int harga) { this.itemId = itemId; this.nama = nama; this.harga = harga; }
    }

    private final ArrayList<Item> keranjang = new ArrayList<>();
    private JPanel panelKeranjang;   // panel keranjang: tersembunyi sampai ikon keranjang diklik
    private JButton btnKeranjang;    // tombol keranjang: menampilkan jumlah item
    private final DecimalFormat fmt;
    private final JPanel panelItem = new JPanel(), panelForm = new JPanel();
    private final JLabel lblTotal = new JLabel("Rp 0");
    private final JTextField txtNama = new JTextField(), txtTelepon = new JTextField();
    private final JTextArea txtAlamat = new JTextArea(3, 20);
    private final JToggleButton btnTransfer = new JToggleButton("🏦 Transfer Bank"), btnQris = new JToggleButton("▦ QRIS");
    private final ButtonGroup bg = new ButtonGroup();
    private JScrollPane scrollIsi;
    private JPanel gridMenu;

    private final int customerId;            // Customer_ID akun yang login
    private JLabel lblBanner;                // banner notifikasi "pesanan siap diambil"
    private JPanel panelBanner;
    private JButton btnPesananSaya;
    private javax.swing.Timer pemeriksa;     // cek status pesanan berkala

    // === KONSTRUKTOR: halaman customer hanya dibuka setelah login; nama & telepon terisi otomatis ===
    public Tugasgui(AkunService.Customer akun) {
        this.customerId = akun.id;
        String namaCustomer = akun.nama, telpCustomer = akun.telepon;
        DecimalFormatSymbols sym = new DecimalFormatSymbols(Locale.US);
        sym.setGroupingSeparator('.');
        fmt = new DecimalFormat("#,###", sym);

        setLayout(new BorderLayout());
        add(buatPanelMenu(), BorderLayout.CENTER);
        panelKeranjang = buatPanelPesanan();
        panelKeranjang.setVisible(false);
        add(panelKeranjang, BorderLayout.EAST);
        if (namaCustomer != null) {
            txtNama.setText(namaCustomer); txtNama.setForeground(Color.BLACK);
            txtTelepon.setText(telpCustomer); txtTelepon.setForeground(Color.BLACK);
        }
        refreshKeranjang();

        // Cek status pesanan tiap 3 detik selama halaman ini tampil
        pemeriksa = new javax.swing.Timer(3000, e -> segarkanNotifikasi());
        segarkanNotifikasi();
        SwingUtilities.invokeLater(this::popupSiapDiambil);   // saat baru masuk, beri tahu jika ada yang siap
    }

    @Override public void addNotify() { super.addNotify(); if (pemeriksa != null) pemeriksa.start(); }
    @Override public void removeNotify() { if (pemeriksa != null) pemeriksa.stop(); super.removeNotify(); }

    // ================= PANEL MENU (KIRI) =================
    private JPanel buatPanelMenu() {
        JPanel root = Gaya.latarKosong(new BorderLayout());
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        // === HEADER: panel gradasi berisi judul, tombol Keranjang, dan Logout ===
        JPanel header = Gaya.panelGradasi(new BorderLayout());
        header.setBorder(new EmptyBorder(18, 26, 18, 26));
        JPanel judul = new JPanel(new GridLayout(2, 1));
        judul.setOpaque(false);
        judul.add(teks(KontenStore.getJudul(), Font.BOLD, 28, Color.WHITE));
        judul.add(teks(KontenStore.getDeskripsi(), Font.PLAIN, 14, new Color(225, 245, 225)));
        for (Component c : judul.getComponents()) ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
        header.add(judul, BorderLayout.CENTER);

        // === BANNER NOTIFIKASI: muncul saat admin menandai pesanan Siap Diambil ===
        panelBanner = new JPanel(new BorderLayout(12, 0));
        panelBanner.setBackground(new Color(0xFFF1C5));
        panelBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE6A600)), new EmptyBorder(10, 16, 10, 16)));
        lblBanner = new JLabel(" ");
        lblBanner.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBanner.setForeground(new Color(0x265F24));
        JButton lihat = new JButton("Lihat pesanan");
        lihat.setFocusPainted(false);
        lihat.addActionListener(e -> bukaPesananSaya());
        panelBanner.add(lblBanner, BorderLayout.CENTER);
        panelBanner.add(lihat, BorderLayout.EAST);
        panelBanner.setVisible(false);

        JPanel atas = new JPanel(new BorderLayout(0, 10));
        atas.setOpaque(false);
        atas.add(header, BorderLayout.NORTH);
        atas.add(panelBanner, BorderLayout.CENTER);
        root.add(atas, BorderLayout.NORTH);

        // === TOMBOL LOGIN ADMIN (kecil, di bawah) ===
        JButton logout = Gaya.tombolPutih("Logout");
        ukuranHeader(logout);
        logout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logout.setBorder(new EmptyBorder(6, 14, 6, 14));
        // Logout: kembali ke login customer; login admin tidak lagi ada di halaman ini
        logout.addActionListener(e -> Navigasi.buka("pilihan", new LayarPilihan()));
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 0, 0, 0));
        // === TOMBOL KERANJANG: buka/tutup panel keranjang ===
        btnKeranjang = Gaya.tombolPutih("Keranjang (0)");
        ukuranHeader(btnKeranjang);
        btnKeranjang.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnKeranjang.setBorder(new EmptyBorder(6, 14, 6, 14));
        btnKeranjang.addActionListener(e -> {
            panelKeranjang.setVisible(!panelKeranjang.isVisible());
            getParent().revalidate();
        });
        btnPesananSaya = Gaya.tombolPutih("Pesanan Saya");
        ukuranHeader(btnPesananSaya);
        btnPesananSaya.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPesananSaya.setBorder(new EmptyBorder(6, 14, 6, 14));
        btnPesananSaya.addActionListener(e -> bukaPesananSaya());
        footer.add(btnPesananSaya);
        footer.add(btnKeranjang);
        footer.add(logout);
        header.add(footer, BorderLayout.EAST);
        
        // === DAFTAR MENU (dari ProdukStore, sama dengan halaman admin) ===
        gridMenu = new PanelMenu();
        gridMenu.setOpaque(false);
        muatMenu();
        JScrollPane scrollMenu = new JScrollPane(gridMenu);
        scrollMenu.setBorder(null);
        scrollMenu.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
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

    // Panel menu yang mengikuti lebar jendela; kartu tersusun berderet dan turun ke bawah
    private static final class PanelMenu extends JPanel implements Scrollable {
        private static final int JARAK = 16;
        PanelMenu() { super(new FlowLayout(FlowLayout.LEFT, JARAK, JARAK)); setOpaque(false); }

        // Tinggi dihitung dari lebar jendela, supaya semua baris kartu ikut terlihat
        @Override public Dimension getPreferredSize() {
            int lebar = getWidth();
            if (lebar == 0 && getParent() != null) lebar = getParent().getWidth();
            if (lebar == 0) lebar = 600;
            int x = JARAK, y = JARAK, tinggiBaris = 0;
            for (Component c : getComponents()) {
                Dimension d = c.getPreferredSize();
                if (x + d.width > lebar - JARAK && x > JARAK) {
                    x = JARAK;
                    y += tinggiBaris + JARAK;
                    tinggiBaris = 0;
                }
                x += d.width + JARAK;
                tinggiBaris = Math.max(tinggiBaris, d.height);
            }
            return new Dimension(lebar, y + tinggiBaris + JARAK);
        }
        public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        public int getScrollableUnitIncrement(Rectangle r, int orientasi, int arah) { return 16; }
        public int getScrollableBlockIncrement(Rectangle r, int orientasi, int arah) { return 120; }
        public boolean getScrollableTracksViewportWidth() { return true; }
        public boolean getScrollableTracksViewportHeight() { return false; }
    }

    private static final int LEBAR_KARTU_MENU = 210, TINGGI_FOTO_MENU = 130;

    private JPanel buatKartuMenu(ProdukStore.Produk p) {
        String nama = p.nama, kalori = p.kalori;
        int harga = p.harga;
        ImageIcon ikon = p.foto instanceof ImageIcon
                ? Gambar.ikonIsi((ImageIcon) p.foto, LEBAR_KARTU_MENU - 6, TINGGI_FOTO_MENU) : null;
        String cadangan = String.valueOf(p.foto);

        // Kartu putih dengan bayangan lembut dan sudut membulat
        JPanel kartu = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(40, 90, 50, 22));
                g2.fill(new RoundRectangle2D.Float(3, 5, getWidth() - 6, getHeight() - 6, 26, 26));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 6, getHeight() - 6, 26, 26));
                g2.dispose();
            }
        };
        kartu.setOpaque(false);

        // Foto dengan sudut atas membulat; label Best seller di pojok kiri atas
        JComponent foto = new JComponent() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.clip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() + 30, 26, 26));
                if (ikon != null) {
                    g2.drawImage(ikon.getImage(), 0, 0, null);
                } else {
                    g2.setColor(new Color(0xF1F5E8));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(HIJAU_TUA);
                    g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(cadangan, (getWidth() - fm.stringWidth(cadangan)) / 2,
                            (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                }
                g2.setClip(null);
                if (p.bestSeller) {
                    g2.setColor(new Color(255, 214, 102));
                    g2.fill(new RoundRectangle2D.Float(12, 12, 116, 24, 24, 24));
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    g2.setColor(new Color(120, 70, 0));
                    g2.drawString("Recommended", 22, 28);
                }
                g2.dispose();
            }
        };
        foto.setPreferredSize(new Dimension(LEBAR_KARTU_MENU - 6, TINGGI_FOTO_MENU));
        kartu.add(foto, BorderLayout.NORTH);

        // Isi kartu: nama, harga, kalori, lalu tombol Gizi dan Order
        JPanel isi = new JPanel();
        isi.setOpaque(false);
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));
        isi.setBorder(new EmptyBorder(12, 12, 14, 12));

        JButton btnInfo = Gaya.tombolPutih("Gizi");
        ukuranPil(btnInfo);
        btnInfo.addActionListener(e -> tampilkanInfoGizi(p));

        JButton btnOrder = Gaya.tombolUtama("Order");
        ukuranPil(btnOrder);
        btnOrder.addActionListener(e -> tambahKeKeranjang(p.id, nama, harga));

        JPanel barisTombol = new JPanel(new GridLayout(1, 2, 8, 0));
        barisTombol.setOpaque(false);
        barisTombol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        barisTombol.add(btnInfo);
        barisTombol.add(btnOrder);

        susun(isi, teks(nama, Font.BOLD, 15, HIJAU_TUA), 2, teks(rp(harga), Font.BOLD, 14, HIJAU_TUA), 2,
                teks(kalori, Font.PLAIN, 11, Color.DARK_GRAY), 10, barisTombol);

        kartu.add(isi, BorderLayout.CENTER);
        kartu.setPreferredSize(new Dimension(LEBAR_KARTU_MENU, kartu.getPreferredSize().height));
        return kartu;
    }

    // Tombol di header customer (Keranjang dan Logout)
    private void ukuranHeader(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(150, 38));
        b.setMaximumSize(new Dimension(150, 38));
        b.setBorder(new EmptyBorder(0, 10, 0, 10));
    }

    // Tombol kecil berbentuk kapsul untuk kartu menu
    private void ukuranPil(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(80, 34));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
    }

    // ================= PANEL KANAN =================
    private JPanel buatPanelPesanan() {
        JPanel panel = Gaya.latarKosong(new BorderLayout());
        panel.setPreferredSize(new Dimension(430, 720));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 20, 12, 20));
        JLabel judul = new JLabel("Pesanan");
        judul.setFont(new Font("Segoe UI", Font.BOLD, 22));
        judul.setForeground(HIJAU_TUA);
        header.add(judul, BorderLayout.WEST);
        panel.add(header, BorderLayout.NORTH);

        // Isi: daftar item + form (tersembunyi dulu)
        panelItem.setLayout(new BoxLayout(panelItem, BoxLayout.Y_AXIS));
        panelItem.setOpaque(false);
        buatPanelForm();
        panelForm.setVisible(false);

        JPanel isi = new JPanel(new BorderLayout());
        isi.setOpaque(false);
        isi.add(panelItem, BorderLayout.NORTH);
        isi.add(panelForm, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(isi, BorderLayout.NORTH);
        wrapper.setBackground(Color.WHITE);

        scrollIsi = new JScrollPane(wrapper);
        scrollIsi.setBorder(null);
        scrollIsi.setOpaque(false);
        scrollIsi.getViewport().setOpaque(false);
        scrollIsi.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollIsi.getVerticalScrollBar().setUnitIncrement(14);
        panel.add(scrollIsi, BorderLayout.CENTER);

        // Bawah: total + tombol konfirmasi
        JPanel bawah = new JPanel();
        bawah.setLayout(new BoxLayout(bawah, BoxLayout.Y_AXIS));
        bawah.setOpaque(false);
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

        for (JTextField f : new JTextField[]{txtNama, txtTelepon}) {
            f.setPreferredSize(new Dimension(100, 40));
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(ABU, 1, true), new EmptyBorder(6, 10, 6, 10)));
        }
        susun(panelForm,
                teks("Nama Pemesan:", Font.BOLD, 14, Color.BLACK), 6, fieldDenganHint(txtNama, "Masukkan nama Anda..."), 12,
                teks("Nomor Telepon:", Font.BOLD, 14, Color.BLACK), 6, fieldDenganHint(txtTelepon, "Contoh: 08123456789"), 12,
                teks("Metode Pembayaran:", Font.BOLD, 14, Color.BLACK), 6, pnlBayar);
    }

    private void warnaBayar() {
        btnTransfer.setBackground(btnTransfer.isSelected() ? HIJAU_MUDA : Color.WHITE);
        btnQris.setBackground(btnQris.isSelected() ? HIJAU_MUDA : Color.WHITE);
    }

    // ================= LOGIKA =================
    private void tambahKeKeranjang(int itemId, String nama, int harga) {
        for (Item it : keranjang)
            if (it.nama.equals(nama)) { it.qty++; refreshKeranjang(); return; }
        keranjang.add(new Item(itemId, nama, harga));
        refreshKeranjang();
    }

    private void refreshKeranjang() {
        if (btnKeranjang != null) {
            int jumlah = 0;
            for (Item it : keranjang) jumlah += it.qty;
            btnKeranjang.setText("Keranjang (" + jumlah + ")");
        }
        panelItem.removeAll();
        if (keranjang.isEmpty()) {
            JLabel kosong = new JLabel("Keranjang masih kosong", SwingConstants.CENTER);
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

        String metodeDipilih = bg.getSelection() == null ? null : "pilih";
        String err = Validasi.pesanan(nama, telp, alamat, metodeDipilih, keranjang.isEmpty());
        if (err != null) { pesan(err); return; }

        String metode = btnTransfer.isSelected() ? "Transfer Bank" : "QRIS";
        java.util.List<OrderService.ItemPesan> items = new java.util.ArrayList<>();
        for (Item it : keranjang) items.add(new OrderService.ItemPesan(it.itemId, it.qty, it.harga));
        OrderService.Order order;
        try {
            order = OrderService.buat(customerId, metode, items);   // disimpan ke tabel orders dan order_item
        } catch (IllegalStateException ex) {
            pesan(ex.getMessage());
            return;
        }
        bukaJendelaPembayaran(order);

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

    // ================= NOTIFIKASI PESANAN SIAP DIAMBIL =================
    /** Perbarui banner dan tombol "Pesanan Saya" sesuai status terbaru dari admin. */
    private void segarkanNotifikasi() {
        if (panelBanner == null) return;
        java.util.List<OrderService.Order> siap;
        try {
            siap = OrderService.siapBelumDilihat(customerId);
        } catch (IllegalStateException ex) {
            return;   // database sedang tidak terjangkau; coba lagi pada pengecekan berikutnya
        }
        if (siap.isEmpty()) {
            panelBanner.setVisible(false);
            btnPesananSaya.setText("Pesanan Saya");
        } else {
            StringBuilder nomor = new StringBuilder();
            for (OrderService.Order o : siap) nomor.append(nomor.length() == 0 ? "" : ", ").append("#").append(o.id);
            lblBanner.setText("Pesanan " + nomor + " sudah siap! Silakan ambil di Jl. Cipta Karya Ujung.");
            panelBanner.setVisible(true);
            btnPesananSaya.setText("Pesanan Saya (" + siap.size() + ")");
        }
        panelBanner.getParent().revalidate();
    }

    /** Dialog sekali saat customer baru masuk, jika ada pesanan siap diambil yang belum dilihat. */
    private void popupSiapDiambil() {
        java.util.List<OrderService.Order> siap;
        try {
            siap = OrderService.siapBelumDilihat(customerId);
        } catch (IllegalStateException ex) {
            return;
        }
        if (siap.isEmpty()) return;
        StringBuilder sb = new StringBuilder("Kabar baik! Pesanan berikut sudah siap diambil:\n\n");
        for (OrderService.Order o : siap) sb.append("  \u2022 Pesanan #").append(o.id).append(" (").append(o.rupiah()).append(")\n");
        sb.append("\nAmbil di: Jl. Cipta Karya Ujung");
        JOptionPane.showMessageDialog(this, sb.toString(), "Pesanan Siap Diambil", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Daftar semua pesanan customer beserta statusnya. Membukanya menandai notifikasi sudah dilihat. */
    private void bukaPesananSaya() {
        String[] kolom = {"No", "Tanggal", "Pesanan", "Total", "Status"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        java.util.List<OrderService.Order> milik;
        try {
            milik = OrderService.milik(customerId);
            OrderService.tandaiDilihat(customerId);
        } catch (IllegalStateException ex) {
            pesan(ex.getMessage());
            return;
        }
        for (OrderService.Order o : milik) {
            String status = OrderService.SIAP_DIAMBIL.equals(o.status) ? "\u2714 Siap Diambil" : o.status;
            model.addRow(new Object[]{"#" + o.id, o.tanggal, String.join("; ", o.rincian), o.rupiah(), status});
        }
        JTable tabel = new JTable(model);
        tabel.setRowHeight(30);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(320);
        JScrollPane gulir = new JScrollPane(tabel);
        gulir.setPreferredSize(new Dimension(780, 320));
        JLabel info = new JLabel(model.getRowCount() == 0 ? "Belum ada pesanan." : "Status diperbarui otomatis oleh admin.");
        JPanel isi = new JPanel(new BorderLayout(0, 8));
        isi.add(info, BorderLayout.NORTH);
        isi.add(gulir, BorderLayout.CENTER);

        segarkanNotifikasi();
        JOptionPane.showMessageDialog(this, isi, "Pesanan Saya", JOptionPane.PLAIN_MESSAGE);
    }

    // ================= HELPER =================
    private String rp(int angka) { return "Rp " + fmt.format(angka); }

    // === JENDELA PEMBAYARAN: terpisah dari halaman customer, ukurannya seperti panel ===
    private void bukaJendelaPembayaran(OrderService.Order order) {
        Window pemilik = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(pemilik, "Pembayaran - Pesanan #" + order.id, Dialog.ModalityType.APPLICATION_MODAL);
        JScrollPane gulir = new JScrollPane(new PembayaranGUI(order));
        gulir.setBorder(null);
        gulir.getVerticalScrollBar().setUnitIncrement(16);
        dlg.setContentPane(gulir);
        int tinggi = Math.min(780, Toolkit.getDefaultToolkit().getScreenSize().height - 120);
        dlg.setSize(740, tinggi);
        dlg.setResizable(false);
        dlg.setLocationRelativeTo(pemilik);
        dlg.setVisible(true);   // tertutup saat tombol Tutup ditekan
    }

    private void pesan(String teks) {
        JOptionPane.showMessageDialog(this, teks, "Informasi", JOptionPane.WARNING_MESSAGE);
    }

    /** Tambah komponen; Integer = jarak vertikal. */
    private void susun(JPanel p, Object... xs) {
        for (Object x : xs) p.add(x instanceof Integer ? Box.createVerticalStrut((Integer) x) : (Component) x);
    }

    private <T extends JComponent> T kiri(T c) { c.setAlignmentX(Component.LEFT_ALIGNMENT); return c; }

    private JLabel teks(String t, int style, int size, Color warna) {
        JLabel l = new JLabel(t, SwingConstants.CENTER);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
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

    private void tampilkanInfoGizi(ProdukStore.Produk p) {
        Window pemilik = SwingUtilities.getWindowAncestor(this);
        String html = "<html><body style='width:320px;font-family:Segoe UI,sans-serif;'>"
                + "<h3 style='margin:0 0 6px 0;color:#265F24;'>" + p.nama + "</h3>"
                + "<p style='margin:0 0 10px 0;color:#444;'>" + p.deskripsi + "</p>"
                + "<b>Perkiraan Nilai Gizi (per porsi):</b>"
                + "<ul style='margin:6px 0 10px 18px;padding:0;'>"
                + "<li><b>Kalori:</b> " + p.kalori + "</li>"
                + "<li><b>Protein:</b> " + p.protein + "</li>"
                + "<li><b>Karbohidrat:</b> " + p.karbo + "</li>"
                + "<li><b>Lemak:</b> " + p.lemak + "</li>"
                + "<li><b>Serat:</b> " + p.serat + "</li>"
                + "</ul>"
                + "<small style='color:#666;'>Informasi gizi merupakan perkiraan per porsi dan dapat berbeda sesuai bahan serta pengolahan.</small>"
                + "</body></html>";
        JOptionPane.showMessageDialog(pemilik, html, "Informasi Gizi - " + p.nama, JOptionPane.INFORMATION_MESSAGE);
    }
}
