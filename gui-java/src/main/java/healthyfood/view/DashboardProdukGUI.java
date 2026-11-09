package healthyfood.view;

import healthyfood.model.OrderStore;
import healthyfood.model.ProdukStore;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class DashboardProdukGUI extends JFrame {
    private final Color GREEN = new Color(46, 110, 44);
    private final Color DARK_GREEN = new Color(38, 95, 36);
    private final Color PALE = new Color(244, 248, 232);
    private final Color LIME = new Color(232, 243, 206);
    private final Color MUTED = new Color(113, 122, 112);
    private final CardLayout contentLayout = new CardLayout();
    private final JPanel content = new JPanel(contentLayout);
    private JTable productTable;

    public DashboardProdukGUI() {
        setTitle("HealthyFood - Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 760);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        getContentPane().setBackground(PALE);
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
        JPanel side = new JPanel(new BorderLayout());
        side.setBackground(GREEN);
        side.setPreferredSize(new Dimension(225, 0));
        side.setBorder(new EmptyBorder(22, 16, 18, 16));

        JLabel brand = new JLabel("🌿 HealthyFood");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("SansSerif", Font.BOLD, 22));
        brand.setBorder(new EmptyBorder(5, 0, 28, 0));
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
        // === LIHAT HALAMAN CUSTOMER (tutup jendela ini tidak menutup admin) ===
        JButton lihatCustomer = new JButton("↗  Lihat Halaman Customer");
        styleSidebarLink(lihatCustomer);
        lihatCustomer.addActionListener(e -> {
            Tugasgui t = new Tugasgui();
            t.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            t.setVisible(true);
        });
        JButton logout = new JButton("⇥  Logout");
        styleSidebarLink(logout);
        logout.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        bottom.add(new JSeparator());
        bottom.add(Box.createVerticalStrut(12));
        bottom.add(lihatCustomer);
        bottom.add(logout);
        side.add(bottom, BorderLayout.SOUTH);
        return side;
    }

    private JButton navButton(String text, String page) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        b.setPreferredSize(new Dimension(190, 45));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(new Font("SansSerif", Font.BOLD, 15));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(0, 15, 0, 8));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setBackground(GREEN);
        b.setForeground(Color.WHITE);
        b.addActionListener(e -> showPage(page));
        return b;
    }

    private void styleSidebarLink(JButton b) {
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setForeground(new Color(235, 248, 214));
        b.setBackground(GREEN);
        b.setBorder(new EmptyBorder(5, 0, 5, 0));
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JPanel pageBase() {
        JPanel p = new JPanel(new BorderLayout(0, 22));
        p.setBackground(PALE);
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

    private JPanel buildDashboardPage() {
        JPanel page = pageBase();
        page.add(heading("Dashboard", "Ringkasan aktivitas restoran Anda."), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 28));
        center.setOpaque(false);
        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
        stats.setOpaque(false);
        stats.add(statCard("🍲", "15", "Total Produk"));
        stats.add(statCard("▤", "1", "Total Pesanan"));
        stats.add(statCard("⌛", "1", "Perlu Verifikasi"));
        stats.add(statCard("＄", "Rp 0", "Pendapatan (Lunas)"));

        JPanel orders = whiteCard();
        orders.setLayout(new BorderLayout(0, 18));
        JLabel title = new JLabel("Pesanan Terbaru");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(DARK_GREEN);
        orders.add(title, BorderLayout.NORTH);
        String[] cols = {"NO. PESANAN", "TANGGAL", "PEMBELI", "TOTAL", "PEMBAYARAN"};
        Object[][] data = {{"#1", "21 Okt 2026 08.11", "Nauu", "Rp 28.000", "Menunggu Verifikasi"}};
        JTable table = new JTable(data, cols);
        styleTable(table);
        orders.add(new JScrollPane(table), BorderLayout.CENTER);

        center.add(stats, BorderLayout.NORTH);
        center.add(orders, BorderLayout.CENTER);
        page.add(center, BorderLayout.CENTER);
        return page;
    }

    private JPanel statCard(String icon, String value, String label) {
        JPanel card = whiteCard();
        card.setLayout(new BorderLayout(14, 0));
        JPanel iconBox = new JPanel(new GridBagLayout());
        iconBox.setBackground(LIME);
        iconBox.setPreferredSize(new Dimension(56, 56));
        JLabel i = new JLabel(icon);
        i.setFont(new Font("SansSerif", Font.BOLD, 24));
        i.setForeground(DARK_GREEN);
        iconBox.add(i);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel v = new JLabel(value);
        v.setFont(new Font("SansSerif", Font.BOLD, 25));
        v.setForeground(DARK_GREEN);
        JLabel l = new JLabel(label);
        l.setForeground(MUTED);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        text.add(v);
        text.add(l);
        card.add(iconBox, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        return card;
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
        String[] columns = {"FOTO", "NAMA", "KALORI", "HARGA", "GRUP", "BEST SELLER"};
        Object[][] rows = new Object[0][columns.length];   // diisi dari ProdukStore
        DefaultTableModel model = new DefaultTableModel(rows, columns) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable = new JTable(model);
        muatProduk();
        styleTable(productTable);
        productTable.setRowHeight(54);
        JScrollPane scroll = new JScrollPane(productTable);
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
                ProdukStore.hapus(row);
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
    private OrderStore.Order pesananTerpilih() {
        int row = ordersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pesanan dulu di tabel.");
            return null;
        }
        int id = Integer.parseInt(ordersModel.getValueAt(ordersTable.convertRowIndexToModel(row), 0)
                .toString().replace("#", "").trim());
        return OrderStore.cari(id);
    }

    // Satu tombol untuk semua langkah: Menunggu Verifikasi -> Lunas -> Diproses -> Selesai
    private void lanjutkanStatus() {
        OrderStore.Order o = pesananTerpilih();
        if (o == null) return;
        if (o.status.equals(OrderStore.MENUNGGU_BAYAR)) {
            JOptionPane.showMessageDialog(this, "Pesanan ini belum dibayar customer.");
            return;
        }
        o.status = OrderStore.langkahBerikut(o.status);
        refreshOrders();
    }

    // === WARNA KOLOM STATUS ===
    private DefaultTableCellRenderer rendererStatus() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, false, false, r, c);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setOpaque(true);
                String s = String.valueOf(v);
                if (s.equals(OrderStore.DIPROSES)) {
                    l.setBackground(new Color(220, 235, 255)); l.setForeground(new Color(30, 80, 150));
                } else if (s.equals(OrderStore.SELESAI)) {
                    l.setBackground(new Color(210, 245, 220)); l.setForeground(new Color(30, 110, 55));
                } else if (s.equals(OrderStore.LUNAS)) {
                    l.setBackground(new Color(231, 241, 220)); l.setForeground(new Color(30, 110, 55));
                } else {
                    l.setBackground(new Color(255, 243, 205)); l.setForeground(new Color(150, 100, 0));
                }
                return l;
            }
        };
    }

    // === HALAMAN PESANAN (tabel + tombol status) ===
    private JPanel buildOrdersPage() {
        JPanel page = pageBase();
        page.add(heading("Pesanan", "Pantau pesanan dan status pembayaran pelanggan."), BorderLayout.NORTH);
        JPanel card = whiteCard();
        card.setLayout(new BorderLayout());

        String[] cols = {"NO. PESANAN", "TANGGAL", "PEMBELI", "TOTAL", "METODE", "STATUS"};
        ordersModel = new DefaultTableModel(new Object[0][cols.length], cols) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        ordersTable = new JTable(ordersModel);
        styleTable(ordersTable);
        ordersTable.getColumnModel().getColumn(5).setCellRenderer(rendererStatus());
        card.add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        // === TOMBOL STATUS ===
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton ubah = new JButton("Ubah Status Pesanan");
        stylePrimary(ubah);
        ubah.addActionListener(e -> lanjutkanStatus());
        actions.add(ubah);
        card.add(actions, BorderLayout.SOUTH);

        page.add(card, BorderLayout.CENTER);
        refreshOrders();
        return page;
    }

    // Isi ulang tabel pesanan dari OrderStore (dipanggil tiap halaman Pesanan dibuka)
    private void refreshOrders() {
        ordersModel.setRowCount(0);
        for (OrderStore.Order o : OrderStore.getOrders()) {
            ordersModel.addRow(new Object[]{"#" + o.id, o.tanggal,
                o.nama + " - " + o.telepon, o.rupiah(), o.metode, o.status});
        }
    }

    private JPanel buildContentPage() {
        JPanel page = pageBase();
        page.add(heading("Konten", "Area pengelolaan konten HealthyFood."), BorderLayout.NORTH);
        JPanel card = whiteCard();
        card.setLayout(new BorderLayout());
        JLabel note = new JLabel("<html><div style='padding:8px'>Ini merupakan contoh tampilan GUI. Pengelolaan konten bisa ditambahkan di sini.</div></html>");
        note.setFont(new Font("SansSerif", Font.PLAIN, 16));
        card.add(note, BorderLayout.NORTH);
        page.add(card, BorderLayout.CENTER);
        return page;
    }

    private JPanel whiteCard() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 239, 228), 1),
                new EmptyBorder(22, 22, 22, 22)));
        return p;
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
        JCheckBox best = new JCheckBox("Tandai sebagai Best Seller", editing && lama.bestSeller);
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
        c.gridx = 1; c.gridy++; form.add(best, c);

        int result = JOptionPane.showConfirmDialog(this, form,
                editing ? "Edit Produk" : "Tambah Produk",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            if (name.getText().trim().isEmpty() || price.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama dan harga produk wajib diisi.");
                return;
            }
            int harga;
            try {
                harga = Integer.parseInt(price.getText().trim().replace(".", "").replace(",", ""));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga harus berupa angka, contoh: 28000.");
                return;
            }
            ProdukStore.Produk baru = new ProdukStore.Produk(editing ? lama.foto : " ",
                    name.getText().trim(), calories.getText().trim(), harga,
                    (String) group.getSelectedItem(), best.isSelected());
            if (editing) ProdukStore.ubah(row, baru);
            else ProdukStore.tambah(baru);
            muatProduk();
        }
    }

    // === ISI TABEL PRODUK DARI ProdukStore (dipanggil tiap ada perubahan) ===
    private void muatProduk() {
        DefaultTableModel m = (DefaultTableModel) productTable.getModel();
        m.setRowCount(0);
        for (ProdukStore.Produk p : ProdukStore.getAll()) {
            m.addRow(new Object[]{p.foto, p.nama, p.kalori, p.rupiah(), p.grup, p.bestSeller ? "Ya" : "Tidak"});
        }
    }

    private void showPage(String page) {
        if (page.equals("orders")) refreshOrders();
        contentLayout.show(content, page);
    }

    public static void main(String[] args) {
        Tema.pakai();
        SwingUtilities.invokeLater(() -> new DashboardProdukGUI().setVisible(true));
    }
}
