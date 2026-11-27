package healthyfood.view;

import healthyfood.util.Gambar;
import healthyfood.model.KontenStore;
import healthyfood.util.Validasi;
import healthyfood.model.OrderService;
import healthyfood.model.ProdukStore;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class DashboardProdukGUI extends JPanel {
    private final Color GREEN = new Color(46, 110, 44);
    private final Color DARK_GREEN = new Color(38, 95, 36);
    private final Color PALE = new Color(244, 248, 232);
    private final Color LIME = new Color(232, 243, 206);
    private final Color MUTED = new Color(113, 122, 112);
    private final CardLayout contentLayout = new CardLayout();
    private final JPanel content = new JPanel(contentLayout);
    private JTable productTable;

    public DashboardProdukGUI() {
        setBackground(PALE);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);

        content.setBackground(PALE);
        content.add(buildDashboardPage(), "dashboard");
        content.add(buildProductsPage(), "products");
        content.add(buildOrdersPage(), "orders");
        content.add(buildContentPage(), "content");
        add(content, BorderLayout.CENTER);

        showPage("dashboard");
    }

    // === SIDEBAR (navbar kiri) ===
    private JPanel buildSidebar() {
        JPanel side = Gaya.panelGradasi(new BorderLayout());
        side.setPreferredSize(new Dimension(225, 0));
        side.setBorder(new EmptyBorder(22, 16, 18, 16));

        // === LOGO (gambar/logo.png) di atas kotak terang agar tulisan hijaunya terbaca ===
        JPanel brand = new JPanel(new BorderLayout());
        brand.setBackground(PALE);
        brand.setBorder(new EmptyBorder(12, 12, 12, 12));
        ImageIcon logo = Gambar.ikon("logo.png");
        if (logo != null) {
            Image kecil = logo.getImage().getScaledInstance(180, -1, Image.SCALE_SMOOTH);
            brand.add(new JLabel(new ImageIcon(kecil)), BorderLayout.CENTER);
        } else {
            JLabel teks = new JLabel("🌿 HealthyFood");
            teks.setForeground(GREEN);
            teks.setFont(new Font("SansSerif", Font.BOLD, 20));
            brand.add(teks, BorderLayout.CENTER);
        }
        side.add(brand, BorderLayout.NORTH);

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.add(navButton("◉   Dashboard", "dashboard"));
        nav.add(Box.createVerticalStrut(8));
        nav.add(navButton("☘   Produk", "products"));
        nav.add(Box.createVerticalStrut(8));
        nav.add(navButton("▤   Pesanan", "orders"));
        nav.add(Box.createVerticalStrut(8));
        nav.add(navButton("☑   Konten", "content"));
        side.add(nav, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        JButton logout = new JButton("⇥  Logout");
        styleSidebarLink(logout);
        logout.addActionListener(e -> Navigasi.buka("pilihan", new LayarPilihan()));
        bottom.add(new JSeparator());
        bottom.add(Box.createVerticalStrut(12));
        bottom.add(logout);
        side.add(bottom, BorderLayout.SOUTH);
        return side;
    }

    private final java.util.List<JButton> navBtns = new java.util.ArrayList<>();

    private JButton navButton(String text, String page) {
        JButton b = new JButton(text) {
            protected void paintComponent(Graphics g) {
                if (Boolean.TRUE.equals(getClientProperty("aktif"))) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 235));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        b.setPreferredSize(new Dimension(190, 45));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(new Font("SansSerif", Font.BOLD, 15));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(0, 15, 0, 8));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setForeground(Color.WHITE);
        b.putClientProperty("page", page);
        navBtns.add(b);
        b.addActionListener(e -> showPage(page));
        return b;
    }

    // Tombol bawah sidebar (Logout): ukuran dan gaya sama dengan menu navigasi
    private void styleSidebarLink(JButton b) {
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        b.setPreferredSize(new Dimension(190, 45));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setForeground(Color.WHITE);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setBorder(new EmptyBorder(0, 15, 0, 8));
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 15));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JPanel pageBase() {
        JPanel p = Gaya.latarKosong(new BorderLayout(0, 22));
        p.setBorder(new EmptyBorder(30, 34, 30, 34));
        return p;
    }

    private JPanel heading(String title, String subtitle) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 28));
        t.setForeground(DARK_GREEN);
        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("SansSerif", Font.PLAIN, 15));
        s.setForeground(MUTED);
        p.add(t);
        p.add(Box.createVerticalStrut(4));
        p.add(s);
        return p;
    }

    // Isi nilai kartu statistik dan tabel pesanan terbaru (diperbarui dari data)
    // === DASHBOARD: kartu statistik, grafik, dan pesanan terbaru ===
    private JLabel lblTotalPesanan, lblPendapatan, lblTotalProduk, lblPerluProses;
    private JPanel panelTerbaru;
    private DonutStatus donut;
    private BarHarga bar;

    private JPanel buildDashboardPage() {
        JPanel page = pageBase();
        page.add(heading("Dashboard", "Ringkasan aktivitas restoran Anda."), BorderLayout.NORTH);

        JPanel isi = new JPanel();
        isi.setOpaque(false);
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));

        // Baris kartu statistik
        lblTotalPesanan = new JLabel("0");
        lblPendapatan = new JLabel("Rp 0");
        lblTotalProduk = new JLabel("0");
        lblPerluProses = new JLabel("0");
        JPanel stat = new JPanel(new GridLayout(1, 4, 16, 0));
        stat.setOpaque(false);
        stat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        stat.add(kartuStat("Total pesanan", lblTotalPesanan, "Semua pesanan masuk",
                new Color(46, 160, 70), new Color(14, 84, 22), Color.WHITE));
        stat.add(kartuStat("Pendapatan", lblPendapatan, "Dari pesanan yang sudah dibayar",
                new Color(40, 140, 60), new Color(10, 70, 20), Color.WHITE));
        stat.add(kartuStat("Total produk", lblTotalProduk, "Menu yang tampil ke customer",
                new Color(255, 255, 255, 230), new Color(220, 240, 215), TEKS_GELAP()));
        stat.add(kartuStat("Perlu verifikasi", lblPerluProses, "Menunggu konfirmasi admin",
                new Color(255, 240, 185), new Color(250, 225, 140), new Color(110, 75, 0)));
        isi.add(stat);
        isi.add(Box.createVerticalStrut(18));

        // Baris grafik
        donut = new DonutStatus();
        bar = new BarHarga();
        JPanel grafik = new JPanel(new GridLayout(1, 2, 16, 0));
        grafik.setOpaque(false);
        grafik.setPreferredSize(new Dimension(0, 300));
        grafik.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        grafik.add(judulKartu("Status pesanan", donut));
        grafik.add(judulKartu("Harga menu (ribu rupiah)", bar));
        isi.add(grafik);
        isi.add(Box.createVerticalStrut(18));

        // Pesanan terbaru
        panelTerbaru = new JPanel();
        panelTerbaru.setOpaque(false);
        panelTerbaru.setLayout(new BoxLayout(panelTerbaru, BoxLayout.Y_AXIS));
        JPanel kartuTerbaru = Gaya.kartuPutih(new BorderLayout(), 22);
        JLabel judulTerbaru = new JLabel("Pesanan terbaru");
        judulTerbaru.setFont(new Font("SansSerif", Font.BOLD, 16));
        judulTerbaru.setForeground(DARK_GREEN);
        judulTerbaru.setBorder(new EmptyBorder(0, 0, 10, 0));
        kartuTerbaru.add(judulTerbaru, BorderLayout.NORTH);
        kartuTerbaru.add(panelTerbaru, BorderLayout.CENTER);
        isi.add(kartuTerbaru);

        page.add(isi, BorderLayout.CENTER);
        segarkanDashboard();
        return page;
    }

    private static Color TEKS_GELAP() { return new Color(30, 70, 35); }

    private JPanel kartuStat(String judul, JLabel nilai, String sub, Color a, Color b, Color teks) {
        JPanel k = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, a, getWidth(), getHeight(), b));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 26, 26));
                g2.dispose();
            }
        };
        k.setOpaque(false);
        k.setBorder(new EmptyBorder(16, 20, 16, 20));
        JPanel t = new JPanel();
        t.setOpaque(false);
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        JLabel j = new JLabel(judul);
        j.setFont(new Font("SansSerif", Font.PLAIN, 13));
        j.setForeground(teks);
        nilai.setFont(new Font("SansSerif", Font.BOLD, 26));
        nilai.setForeground(teks);
        JLabel s = new JLabel(sub);
        s.setFont(new Font("SansSerif", Font.PLAIN, 12));
        s.setForeground(teks);
        t.add(j);
        t.add(Box.createVerticalStrut(4));
        t.add(nilai);
        t.add(Box.createVerticalStrut(2));
        t.add(s);
        k.add(t, BorderLayout.CENTER);
        return k;
    }

    private JPanel judulKartu(String judul, JComponent isi) {
        JPanel k = Gaya.kartuPutih(new BorderLayout(), 20);
        JLabel j = new JLabel(judul);
        j.setFont(new Font("SansSerif", Font.BOLD, 16));
        j.setForeground(DARK_GREEN);
        j.setBorder(new EmptyBorder(0, 0, 8, 0));
        k.add(j, BorderLayout.NORTH);
        k.add(isi, BorderLayout.CENTER);
        return k;
    }

    // Label status berwarna (dipakai di pesanan terbaru dan halaman Pesanan)
    private JLabel pillStatus(String s) {
        JLabel l = new JLabel(s, SwingConstants.CENTER);
        l.setOpaque(true);
        Color bg = new Color(255, 240, 190), fg = new Color(130, 90, 0);
        if (OrderService.MENUNGGU_BAYAR.equals(s)) { bg = new Color(252, 228, 228); fg = new Color(160, 40, 40); }
        else if (OrderService.MENUNGGU_KONFIRMASI.equals(s)) { bg = new Color(255, 226, 190); fg = new Color(160, 90, 0); }
        else if (OrderService.DIPROSES.equals(s)) { bg = new Color(220, 235, 255); fg = new Color(30, 80, 150); }
        else if (OrderService.SIAP_DIAMBIL.equals(s)) { bg = new Color(210, 245, 220); fg = new Color(30, 110, 55); }
        l.setBackground(bg);
        l.setForeground(fg);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setBorder(new EmptyBorder(5, 12, 5, 12));
        return l;
    }

    // Donut status pesanan: jumlah per status
    static final class DonutStatus extends JPanel {
        private java.util.List<OrderService.Order> data = java.util.Collections.emptyList();

        DonutStatus() { setOpaque(false); }

        // Data diisi dari segarkanDashboard supaya paintComponent tidak membaca database tiap digambar ulang
        void setData(java.util.List<OrderService.Order> data) { this.data = data; }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            String[] nama = {OrderService.MENUNGGU_BAYAR, OrderService.MENUNGGU_KONFIRMASI, OrderService.DIBAYAR, OrderService.DIPROSES, OrderService.SIAP_DIAMBIL};
            String[] label = {"Belum dibayar", "Konfirmasi", "Dibayar", "Diproses", "Siap diambil"};
            Color[] warna = {new Color(230, 90, 90), new Color(250, 150, 60), new Color(245, 200, 70), new Color(70, 140, 230), new Color(40, 160, 70)};
            int[] n = new int[5];
            int total = 0;
            for (OrderService.Order o : data)
                for (int i = 0; i < 5; i++) if (nama[i].equals(o.status)) { n[i]++; total++; }

            int d = Math.min(getWidth(), getHeight() - 60) - 24;
            int x = (getWidth() - d) / 2, y = 6;
            if (total == 0) {
                g2.setColor(new Color(225, 232, 220));
                g2.setStroke(new BasicStroke(24));
                g2.drawOval(x + 12, y + 12, d - 24, d - 24);
            } else {
                float mulai = 90;
                for (int i = 0; i < 5; i++) {
                    float sapuan = 360f * n[i] / total;
                    if (n[i] > 0) {
                        g2.setColor(warna[i]);
                        g2.setStroke(new BasicStroke(24, BasicStroke.CAP_BUTT, BasicStroke.CAP_BUTT));
                        g2.draw(new Arc2D.Float(x + 12, y + 12, d - 24, d - 24, mulai, -sapuan, Arc2D.OPEN));
                    }
                    mulai -= sapuan;
                }
            }
            g2.setColor(new Color(30, 70, 35));
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            String tengah = String.valueOf(total);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(tengah, (getWidth() - fm.stringWidth(tengah)) / 2, y + d / 2 + 8);

            // Keterangan di bawah donut
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            int lx = 16, ly = getHeight() - 14;
            for (int i = 0; i < 5; i++) {
                g2.setColor(warna[i]);
                g2.fillOval(lx, ly - 9, 10, 10);
                g2.setColor(new Color(60, 80, 62));
                String t = label[i] + " " + n[i];
                g2.drawString(t, lx + 14, ly);
                lx += 14 + g2.getFontMetrics().stringWidth(t) + 14;
            }
            g2.dispose();
        }
    }

    // Grafik batang harga menu (maksimal 8 produk pertama)
    static final class BarHarga extends JPanel {
        BarHarga() { setOpaque(false); }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            java.util.List<ProdukStore.Produk> daftar = ProdukStore.getAll();
            int n = Math.min(daftar.size(), 8);
            if (n == 0) { g2.dispose(); return; }
            int maks = 1;
            for (ProdukStore.Produk p : daftar) maks = Math.max(maks, p.harga);
            int h = getHeight() - 26, w = getWidth();
            int slot = w / n, bw = Math.max(16, slot - 22);
            for (int i = 0; i < n; i++) {
                ProdukStore.Produk p = daftar.get(i);
                int bh = (int) ((h - 10) * p.harga / (double) maks);
                int x = i * slot + (slot - bw) / 2, y = h - bh;
                g2.setPaint(new GradientPaint(0, y, Gaya.HIJAU, 0, h, Gaya.HIJAU_TUA));
                g2.fill(new RoundRectangle2D.Float(x, y, bw, bh, 12, 12));
                String kata = p.nama.split(" ")[0];
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.setColor(new Color(44, 112, 56));
                int tw = g2.getFontMetrics().stringWidth(kata);
                g2.drawString(kata, i * slot + (slot - tw) / 2, h + 18);
            }
            g2.dispose();
        }
    }

    // === ANGKA DASHBOARD & PESANAN TERBARU: diambil dari ProdukStore dan OrderService ===
    private void segarkanDashboard() {
        java.util.List<OrderService.Order> semua = OrderService.getOrders();
        int perluProses = 0;
        long pendapatan = 0;
        for (OrderService.Order o : semua) {
            if (OrderService.MENUNGGU_KONFIRMASI.equals(o.status)) perluProses++;
            if (OrderService.DIBAYAR.equals(o.status) || OrderService.DIPROSES.equals(o.status)
                    || OrderService.SIAP_DIAMBIL.equals(o.status))
                pendapatan += o.total;
        }
        lblTotalProduk.setText(String.valueOf(ProdukStore.getAll().size()));
        lblTotalPesanan.setText(String.valueOf(semua.size()));
        lblPerluProses.setText(String.valueOf(perluProses));
        lblPendapatan.setText("Rp " + String.format("%,d", pendapatan).replace(',', '.'));
        if (donut != null) { donut.setData(semua); donut.repaint(); }
        if (bar != null) bar.repaint();

        // 5 pesanan terakhir, yang terbaru di atas
        if (panelTerbaru != null) {
            panelTerbaru.removeAll();
            for (int i = semua.size() - 1; i >= Math.max(0, semua.size() - 5); i--) {
                OrderService.Order o = semua.get(i);
                JPanel baris = new JPanel(new BorderLayout(12, 0));
                baris.setOpaque(false);
                baris.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
                baris.setBorder(new EmptyBorder(6, 0, 6, 0));
                JLabel info = new JLabel("#" + o.id + "   " + o.tanggal + "   " + o.nama + "   " + o.rupiah());
                info.setFont(new Font("SansSerif", Font.PLAIN, 14));
                info.setForeground(new Color(48, 55, 47));
                baris.add(info, BorderLayout.CENTER);
                baris.add(pillStatus(o.status), BorderLayout.EAST);
                panelTerbaru.add(baris);
            }
            panelTerbaru.revalidate();
            panelTerbaru.repaint();
        }
    }

    private JPanel buildProductsPage() {
        JPanel page = pageBase();
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(heading("Kelola Produk", "Perubahan di sini langsung tampil di halaman customer."), BorderLayout.WEST);
        JButton add = new JButton("+  Tambah Produk");
        stylePrimary(add);
        add.addActionListener(e -> showProductEditor(null));
        top.add(add, BorderLayout.EAST);
        page.add(top, BorderLayout.NORTH);

        JPanel tableCard = whiteCard();
        tableCard.setLayout(new BorderLayout(0, 10));
        String[] columns = {"FOTO", "NAMA", "KALORI", "HARGA", "GRUP", "RECOMMENDED"};
        Object[][] rows = new Object[0][columns.length];   // diisi dari ProdukStore
        DefaultTableModel model = new DefaultTableModel(rows, columns) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable = new JTable(model);
        productTable.getColumnModel().getColumn(0).setCellRenderer(rendererFoto());
        muatProduk();
        styleTable(productTable);
        gayaKartu(productTable);
        JScrollPane scroll = new JScrollPane(productTable);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(null);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        tableCard.add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton edit = new JButton("✎  Edit Produk Terpilih");
        styleSecondary(edit);
        edit.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Pilih satu produk di tabel terlebih dahulu.");
            } else {
                showProductEditor(row);
            }
        });
        JButton delete = new JButton("Hapus");
        styleSecondary(delete);
        delete.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row < 0) JOptionPane.showMessageDialog(this, "Pilih produk yang ingin dihapus.");
            else if (JOptionPane.showConfirmDialog(this, "Hapus produk ini?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    ProdukStore.hapus(row);
                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
                muatProduk();
            }
        });
        actions.add(edit);
        actions.add(delete);
        tableCard.add(actions, BorderLayout.SOUTH);
        page.add(tableCard, BorderLayout.CENTER);
        return page;
    }

    private DefaultTableModel ordersModel;
    private JTable ordersTable;

    // === UBAH STATUS PESANAN (tombol di halaman Pesanan) ===
    private OrderService.Order pesananTerpilih() {
        int row = ordersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pesanan dulu di tabel.");
            return null;
        }
        int id = Integer.parseInt(ordersModel.getValueAt(ordersTable.convertRowIndexToModel(row), 0)
                .toString().replace("#", "").trim());
        return OrderService.cari(id);
    }

    // Satu tombol untuk semua langkah: Dibayar -> Diproses -> Siap Diambil
    private void lanjutkanStatus() {
        OrderService.Order o = pesananTerpilih();
        if (o == null) return;
        if (o.status.equals(OrderService.MENUNGGU_BAYAR)) {
            JOptionPane.showMessageDialog(this, "Pesanan ini belum dibayar customer.");
            return;
        }
        try {
            OrderService.ubahStatus(o, OrderService.langkahBerikut(o.status));   // Menunggu Konfirmasi -> Dibayar -> Diproses -> Siap Diambil
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }
        refreshOrders();
        if (OrderService.SIAP_DIAMBIL.equals(o.status)) {
            JOptionPane.showMessageDialog(this, "Pesanan #" + o.id + " ditandai Siap Diambil.\n"
                    + "Customer akan melihat notifikasi di halaman pesanannya.");
        }
    }

    // === WARNA KOLOM STATUS ===
    private DefaultTableCellRenderer rendererStatus() {
        return new KartuCell() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String st = String.valueOf(v);
                Color fg = new Color(130, 90, 0);
                if (st.equals(OrderService.MENUNGGU_BAYAR)) fg = new Color(160, 40, 40);
                else if (st.equals(OrderService.MENUNGGU_KONFIRMASI)) fg = new Color(160, 90, 0);
                else if (st.equals(OrderService.DIPROSES)) fg = new Color(30, 80, 150);
                else if (st.equals(OrderService.SIAP_DIAMBIL)) fg = new Color(30, 110, 55);
                l.setForeground(fg);
                l.setFont(new Font("SansSerif", Font.BOLD, 13));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                return l;
            }
        };
    }

    // Tabel dengan baris berbentuk kartu (tanpa garis kisi, jarak antar baris)
    private void gayaKartu(JTable t) {
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 12));
        t.setRowHeight(64);
        t.setOpaque(true);
        t.setBackground(Color.WHITE);
        t.setBorder(null);
        t.getTableHeader().setOpaque(true);
        t.getTableHeader().setBackground(Color.WHITE);
        t.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            {
                setOpaque(true);
                setBackground(Color.WHITE);
                setFont(new Font("SansSerif", Font.BOLD, 12));
                setForeground(MUTED);
                setBorder(new EmptyBorder(0, 14, 0, 14));
            }
        });
        t.setDefaultRenderer(Object.class, new KartuCell());
    }

    // Sel tabel tampil sebagai bagian dari kartu putih membulat (satu baris = satu kartu)
    static class KartuCell extends DefaultTableCellRenderer {
        private int kolom, jumlahKolom;
        private boolean dipilih;

        KartuCell() {
            setOpaque(false);
            setBorder(new EmptyBorder(0, 14, 0, 14));
        }

        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, v, false, false, r, c);
            kolom = c;
            jumlahKolom = t.getColumnCount();
            dipilih = sel;
            setForeground(new Color(30, 60, 35));
            if (v instanceof Icon) { setIcon((Icon) v); setText(""); }
            else setIcon(null);
            return this;
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), ar = 18;
            g2.clip(new Rectangle(0, 0, w, h));
            boolean kiri = kolom == 0, kanan = kolom == jumlahKolom - 1;
            float x0 = kiri ? 0 : -ar, x1 = kanan ? w : w + ar;
            g2.setColor(new Color(255, 255, 255, 240));
            g2.fill(new RoundRectangle2D.Float(x0, 0, x1 - x0, h - 1, ar, ar));
            if (dipilih) {
                g2.setColor(new Color(60, 160, 80));
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Float(x0 + 1, 1, x1 - x0 - 2, h - 3, ar, ar));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // === HALAMAN PESANAN: filter status, tabel, dan tombol ubah status ===
    private String filterStatus = null;   // null = tampilkan semua
    private JPanel panelFilter;

    private JPanel buildOrdersPage() {
        JPanel page = pageBase();
        page.add(heading("Pesanan", "Pantau pesanan dan status pembayaran pelanggan."), BorderLayout.NORTH);

        JPanel isi = new JPanel(new BorderLayout(0, 16));
        isi.setOpaque(false);
        panelFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelFilter.setOpaque(false);
        isi.add(panelFilter, BorderLayout.NORTH);

        JPanel card = Gaya.kartuPutih(new BorderLayout(0, 14), 22);
        String[] cols = {"NO. PESANAN", "TANGGAL", "PEMBELI", "TOTAL", "METODE", "STATUS"};
        ordersModel = new DefaultTableModel(new Object[0][cols.length], cols) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        ordersTable = new JTable(ordersModel);
        styleTable(ordersTable);
        gayaKartu(ordersTable);
        ordersTable.getColumnModel().getColumn(5).setCellRenderer(rendererStatus());
        JScrollPane gulir = new JScrollPane(ordersTable);
        gulir.setBorder(null);
        gulir.getViewport().setOpaque(true);
        gulir.getViewport().setBackground(Color.WHITE);
        card.add(gulir, BorderLayout.CENTER);

        // Tombol ubah status (Dibayar -> Diproses -> Siap Diambil)
        JPanel aksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        aksi.setOpaque(false);
        JButton ubah = Gaya.tombolUtama("Ubah Status Pesanan");
        ubah.setPreferredSize(new Dimension(230, 42));
        ubah.setMaximumSize(new Dimension(230, 42));
        ubah.addActionListener(e -> lanjutkanStatus());
        aksi.add(ubah);
        card.add(aksi, BorderLayout.SOUTH);

        isi.add(card, BorderLayout.CENTER);
        page.add(isi, BorderLayout.CENTER);
        refreshOrders();
        return page;
    }

    // Isi ulang tabel pesanan sesuai filter, dan perbarui angka di tiap filter
    private void refreshOrders() {
        ordersModel.setRowCount(0);
        java.util.List<OrderService.Order> semua = OrderService.getOrders();
        for (OrderService.Order o : semua) {
            if (filterStatus != null && !filterStatus.equals(o.status)) continue;
            ordersModel.addRow(new Object[]{"#" + o.id, o.tanggal, o.nama + " - " + o.telepon,
                    o.rupiah(), o.metode, o.status});
        }
        if (panelFilter != null) buatFilter(semua);
    }

    private void buatFilter(java.util.List<OrderService.Order> semua) {
        panelFilter.removeAll();
        String[] status = {null, OrderService.MENUNGGU_BAYAR, OrderService.MENUNGGU_KONFIRMASI, OrderService.DIBAYAR, OrderService.DIPROSES, OrderService.SIAP_DIAMBIL};
        String[] label = {"Semua", "Belum Bayar", "Konfirmasi", "Dibayar", "Diproses", "Siap Diambil"};
        for (int i = 0; i < status.length; i++) {
            final String s = status[i];
            int jumlah = 0;
            for (OrderService.Order o : semua) if (s == null || s.equals(o.status)) jumlah++;
            boolean aktif = (s == null && filterStatus == null) || (s != null && s.equals(filterStatus));
            JButton b = aktif ? Gaya.tombolUtama(label[i] + "  " + jumlah) : Gaya.tombolPutih(label[i] + "  " + jumlah);
            b.setFont(new Font("SansSerif", Font.BOLD, 13));
            b.setFont(new Font("SansSerif", Font.BOLD, 12));
            b.setPreferredSize(new Dimension(128, 36));
            b.setMaximumSize(new Dimension(128, 36));
            b.addActionListener(e -> { filterStatus = s; refreshOrders(); });
            panelFilter.add(b);
        }
        panelFilter.revalidate();
        panelFilter.repaint();
    }

    // === HALAMAN KONTEN: ubah teks header halaman customer ===
    private JLabel labelForm(String teks) {
        JLabel l = new JLabel(teks);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(DARK_GREEN);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel buildContentPage() {
        JPanel page = pageBase();

        JPanel kepala = new JPanel(new BorderLayout());
        kepala.setOpaque(false);
        kepala.add(heading("Konten", "Ubah teks yang tampil di halaman customer."), BorderLayout.WEST);
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tombol.setOpaque(false);
        JButton kembalikan = Gaya.tombolPutih("Kembalikan");
        JButton simpan = Gaya.tombolUtama("Simpan perubahan");
        for (JButton b : new JButton[]{kembalikan, simpan}) {
            b.setFont(new Font("SansSerif", Font.BOLD, 13));
            b.setPreferredSize(new Dimension(170, 40));
            b.setMaximumSize(new Dimension(170, 40));
        }
        tombol.add(kembalikan);
        tombol.add(simpan);
        kepala.add(tombol, BorderLayout.EAST);
        page.add(kepala, BorderLayout.NORTH);

        // Pratinjau: langsung berubah saat teks diketik
        JLabel pratJudul = new JLabel(KontenStore.getJudul(), SwingConstants.CENTER);
        pratJudul.setFont(new Font("SansSerif", Font.BOLD, 30));
        pratJudul.setForeground(DARK_GREEN);
        JLabel pratDesk = new JLabel(KontenStore.getDeskripsi(), SwingConstants.CENTER);
        pratDesk.setFont(new Font("SansSerif", Font.PLAIN, 15));
        pratDesk.setForeground(new Color(44, 90, 50));
        JLabel tag = new JLabel("  Pratinjau  ");
        tag.setOpaque(true);
        tag.setBackground(GREEN);
        tag.setForeground(Color.WHITE);
        tag.setFont(new Font("SansSerif", Font.BOLD, 12));
        tag.setBorder(new EmptyBorder(5, 8, 5, 8));
        JPanel tagWadah = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tagWadah.setOpaque(false);
        tagWadah.add(tag);
        JPanel pratinjau = Gaya.kartuPutih(new BorderLayout(0, 12), 24);
        pratinjau.add(tagWadah, BorderLayout.NORTH);
        JPanel tengahPrat = new JPanel();
        tengahPrat.setOpaque(false);
        tengahPrat.setLayout(new BoxLayout(tengahPrat, BoxLayout.Y_AXIS));
        pratJudul.setAlignmentX(Component.CENTER_ALIGNMENT);
        pratDesk.setAlignmentX(Component.CENTER_ALIGNMENT);
        tengahPrat.add(pratJudul);
        tengahPrat.add(Box.createVerticalStrut(6));
        tengahPrat.add(pratDesk);
        pratinjau.add(tengahPrat, BorderLayout.CENTER);

        // Form isian
        JTextField fJudul = new JTextField(KontenStore.getJudul());
        fJudul.setFont(new Font("SansSerif", Font.PLAIN, 15));
        fJudul.setBackground(new Color(243, 248, 238));
        fJudul.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 215, 192)), new EmptyBorder(8, 10, 8, 10)));
        fJudul.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        fJudul.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextArea fDesk = new JTextArea(KontenStore.getDeskripsi(), 3, 40);
        fDesk.setLineWrap(true);
        fDesk.setWrapStyleWord(true);
        fDesk.setFont(new Font("SansSerif", Font.PLAIN, 15));
        fDesk.setBackground(new Color(243, 248, 238));
        fDesk.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 215, 192)), new EmptyBorder(8, 10, 8, 10)));
        JPanel form = Gaya.kartuPutih(new BorderLayout(0, 8), 24);
        JPanel isiForm = new JPanel();
        isiForm.setOpaque(false);
        isiForm.setLayout(new BoxLayout(isiForm, BoxLayout.Y_AXIS));
        JLabel lJudul = new JLabel("Judul utama");
        lJudul.setFont(new Font("SansSerif", Font.BOLD, 13));
        lJudul.setForeground(DARK_GREEN);
        lJudul.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lDesk = new JLabel("Deskripsi di bawah judul");
        lDesk.setFont(new Font("SansSerif", Font.BOLD, 13));
        lDesk.setForeground(DARK_GREEN);
        lDesk.setAlignmentX(Component.LEFT_ALIGNMENT);
        isiForm.add(lJudul);
        isiForm.add(Box.createVerticalStrut(6));
        isiForm.add(fJudul);
        isiForm.add(Box.createVerticalStrut(14));
        isiForm.add(lDesk);
        isiForm.add(Box.createVerticalStrut(6));
        JScrollPane gulirDesk = new JScrollPane(fDesk);
        gulirDesk.setBorder(null);
        gulirDesk.setAlignmentX(Component.LEFT_ALIGNMENT);
        gulirDesk.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        isiForm.add(gulirDesk);

        form.add(isiForm, BorderLayout.CENTER);

        javax.swing.event.DocumentListener perbarui = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { pratinjau(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { pratinjau(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { pratinjau(); }
            private void pratinjau() {
                pratJudul.setText(fJudul.getText());
                pratDesk.setText(fDesk.getText());
            }
        };
        fJudul.getDocument().addDocumentListener(perbarui);
        fDesk.getDocument().addDocumentListener(perbarui);

        simpan.addActionListener(e -> {
            String j = fJudul.getText().trim(), d = fDesk.getText().trim();
            if (j.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Judul utama tidak boleh kosong.");
                return;
            }
            KontenStore.simpan(j, d);
            JOptionPane.showMessageDialog(this, "Perubahan tersimpan. Buka halaman customer untuk melihatnya.");
        });
        kembalikan.addActionListener(e -> {
            KontenStore.kembalikan();
            fJudul.setText(KontenStore.getJudul());
            fDesk.setText(KontenStore.getDeskripsi());
        });

        JPanel tengah = new JPanel(new BorderLayout(0, 18));
        tengah.setOpaque(false);
        tengah.add(pratinjau, BorderLayout.NORTH);
        tengah.add(form, BorderLayout.CENTER);
        page.add(tengah, BorderLayout.CENTER);
        return page;
    }

    private JPanel whiteCard() {
        return Gaya.kartuPutih(new FlowLayout(), 22);
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setForeground(new Color(48, 55, 47));
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(237, 240, 234));
        table.setShowVerticalLines(false);
        table.setRowHeight(48);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(231, 244, 204));
        table.setSelectionForeground(DARK_GREEN);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setForeground(MUTED);
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
    }

    private void stylePrimary(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(GREEN);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(12, 18, 12, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondary(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(new Color(239, 245, 229));
        b.setForeground(DARK_GREEN);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9, 13, 9, 13));
    }

    private void showProductEditor(Integer row) {
        boolean editing = row != null;
        ProdukStore.Produk lama = editing ? ProdukStore.getAll().get(row) : null;
        JTextField name = new JTextField(editing ? lama.nama : "", 22);
        JTextField calories = new JTextField(editing ? lama.kalori : "", 22);
        JTextField price = new JTextField(editing ? String.valueOf(lama.harga) : "", 22);
        JComboBox<String> group = new JComboBox<>(new String[]{"-", "Makanan utama", "Snack", "Minuman"});
        if (editing) group.setSelectedItem(lama.grup);
        // === FOTO PRODUK: dipilih dari komputer, disalin ke folder gambar saat disimpan ===
        java.io.File[] fotoSumber = new java.io.File[1];
        ImageIcon[] fotoTampil = new ImageIcon[1];
        if (editing && lama.foto instanceof ImageIcon) fotoTampil[0] = Gambar.ikonIsi((ImageIcon) lama.foto, 160, 110);
        JLabel pratinjau = new JLabel("Belum ada foto", SwingConstants.CENTER);
        pratinjau.setPreferredSize(new Dimension(160, 110));
        pratinjau.setOpaque(true);
        pratinjau.setBackground(new Color(0xF1F5E8));
        if (fotoTampil[0] != null) pratinjau.setIcon(fotoTampil[0]);
        JButton pilihFoto = new JButton("Pilih Gambar...");
        styleSecondary(pilihFoto);
        pilihFoto.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Gambar (JPG, PNG, GIF, BMP)", "jpg", "jpeg", "png", "gif", "bmp"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File pilih = fc.getSelectedFile();
                ImageIcon ikon = Gambar.muatFile(pilih);
                if (ikon == null) {
                    JOptionPane.showMessageDialog(this, "File tidak bisa dibaca. Pilih file JPG atau PNG.");
                    return;
                }
                fotoSumber[0] = pilih;
                fotoTampil[0] = Gambar.ikonIsi(ikon, 160, 110);
                pratinjau.setIcon(fotoTampil[0]);
                pratinjau.setText("");
            }
        });
        JPanel panelFoto = new JPanel(new BorderLayout(10, 0));
        panelFoto.setOpaque(false);
        panelFoto.add(pratinjau, BorderLayout.WEST);
        panelFoto.add(pilihFoto, BorderLayout.CENTER);

        JCheckBox best = new JCheckBox("Tandai sebagai Recommended", editing && lama.bestSeller);
        // === DATA GIZI (ditampilkan di tombol Gizi customer) ===
        JTextArea deskripsi = new JTextArea(editing ? lama.deskripsi : "", 3, 22);
        deskripsi.setLineWrap(true);
        deskripsi.setWrapStyleWord(true);
        JTextField protein = new JTextField(editing ? lama.protein : "", 22);
        JTextField karbo = new JTextField(editing ? lama.karbo : "", 22);
        JTextField lemak = new JTextField(editing ? lama.lemak : "", 22);
        JTextField serat = new JTextField(editing ? lama.serat : "", 22);
        protein.setToolTipText("contoh: 5-8 g");
        karbo.setToolTipText("contoh: 15-25 g");
        lemak.setToolTipText("contoh: 8-15 g");
        serat.setToolTipText("contoh: 4-7 g");
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(7, 7, 7, 7);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Nama produk"), c);
        c.gridx = 1; form.add(name, c);
        c.gridx = 0; c.gridy++; form.add(new JLabel("Rentang kalori"), c);
        c.gridx = 1; form.add(calories, c);
        c.gridx = 0; c.gridy++; form.add(new JLabel("Harga (angka)"), c);
        c.gridx = 1; form.add(price, c);
        c.gridx = 0; c.gridy++; form.add(new JLabel("Grup"), c);
        c.gridx = 1; form.add(group, c);
        c.gridx = 0; c.gridy++; form.add(new JLabel("Foto"), c);
        c.gridx = 1; form.add(panelFoto, c);
        c.gridx = 1; c.gridy++; form.add(best, c);
        c.gridx = 0; c.gridy++; form.add(new JLabel("Deskripsi"), c);
        c.gridx = 1; form.add(new JScrollPane(deskripsi), c);
        c.gridx = 0; c.gridy++; form.add(new JLabel("Protein"), c);
        c.gridx = 1; form.add(protein, c);
        c.gridx = 0; c.gridy++; form.add(new JLabel("Karbohidrat"), c);
        c.gridx = 1; form.add(karbo, c);
        c.gridx = 0; c.gridy++; form.add(new JLabel("Lemak"), c);
        c.gridx = 1; form.add(lemak, c);
        c.gridx = 0; c.gridy++; form.add(new JLabel("Serat"), c);
        c.gridx = 1; form.add(serat, c);

        int result = JOptionPane.showConfirmDialog(this, form,
                editing ? "Edit Produk" : "Tambah Produk",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String errProduk = Validasi.produk(name.getText().trim(), price.getText().trim(),
                    !editing, fotoSumber[0] != null);
            if (errProduk != null) {
                JOptionPane.showMessageDialog(this, errProduk);
                return;
            }
            int harga = Integer.parseInt(price.getText().trim().replace(".", "").replace(",", ""));
            Object foto = editing ? lama.foto : fotoTampil[0];
            String namaFile = editing ? lama.namaFile : null;
            if (fotoSumber[0] != null) {
                try {
                    java.io.File tersimpan = Gambar.salin(fotoSumber[0]);
                    foto = Gambar.muatFile(tersimpan);
                    namaFile = tersimpan.getName();
                } catch (java.io.IOException ex) {
                    JOptionPane.showMessageDialog(this, "Gagal menyalin foto: " + ex.getMessage());
                    return;
                }
            }
            ProdukStore.Produk baru = new ProdukStore.Produk(foto, namaFile,
                    name.getText().trim(), calories.getText().trim(), harga,
                    (String) group.getSelectedItem(), best.isSelected(),
                    deskripsi.getText().trim(), protein.getText().trim(), karbo.getText().trim(),
                    lemak.getText().trim(), serat.getText().trim());
            if (editing) ProdukStore.ubah(row, baru);
            else ProdukStore.tambah(baru);
            muatProduk();
        }
    }

    // === ISI TABEL PRODUK DARI ProdukStore (dipanggil tiap ada perubahan) ===
    // Kolom foto: tampilkan gambar kecil, atau emoji jika tidak ada gambar
    private DefaultTableCellRenderer rendererFoto() {
        return new KartuCell() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, false, false, r, c);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                if (v instanceof Icon) { l.setIcon((Icon) v); l.setText(""); }
                else { l.setIcon(null); l.setText(String.valueOf(v)); }
                return l;
            }
        };
    }

    private void muatProduk() {
        DefaultTableModel m = (DefaultTableModel) productTable.getModel();
        m.setRowCount(0);
        for (ProdukStore.Produk p : ProdukStore.getAll()) {
            m.addRow(new Object[]{p.foto instanceof ImageIcon ? Gambar.ikonIsi((ImageIcon) p.foto, 40, 40) : p.foto, p.nama, p.kalori, p.rupiah(), p.grup, p.bestSeller ? "Ya" : "Tidak"});
        }
    }

    private void showPage(String page) {
        // Tandai tombol sidebar yang sedang aktif
        for (JButton nb : navBtns) {
            boolean aktif = page.equals(nb.getClientProperty("page"));
            nb.putClientProperty("aktif", aktif);
            nb.setForeground(aktif ? DARK_GREEN : Color.WHITE);
            nb.repaint();
        }
        if (page.equals("dashboard")) segarkanDashboard();
        if (page.equals("orders")) refreshOrders();
        contentLayout.show(content, page);
    }
}
