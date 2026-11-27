package healthyfood.dao;

import healthyfood.config.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Akses tabel orders dan order_item di database healthy_food. */
public final class OrderDAO {

    /** Satu item yang akan disimpan ke order_item. */
    public static final class ItemPesan {
        public final int itemId, jumlah, harga;
        public ItemPesan(int itemId, int jumlah, int harga) { this.itemId = itemId; this.jumlah = jumlah; this.harga = harga; }
    }

    /** Satu baris order_item beserta nama menunya. */
    public static final class Rincian {
        public final String namaItem;
        public final int jumlah;
        public final double hargaTotal;
        Rincian(String namaItem, int jumlah, double hargaTotal) {
            this.namaItem = namaItem; this.jumlah = jumlah; this.hargaTotal = hargaTotal;
        }
    }

    /** Satu baris tabel orders, digabung dengan data customer dan rincian itemnya. */
    public static final class Baris {
        public final int id, customerId;
        public final String username, nama, telepon, metode, statusBayar, statusOrder;
        public final Timestamp tanggal;
        public final double total;
        public final boolean notifDilihat;
        public final List<Rincian> rincian = new ArrayList<>();

        Baris(int id, int customerId, String username, String nama, String telepon, Timestamp tanggal,
              double total, String metode, String statusBayar, String statusOrder, boolean notifDilihat) {
            this.id = id; this.customerId = customerId; this.username = username; this.nama = nama;
            this.telepon = telepon; this.tanggal = tanggal; this.total = total; this.metode = metode;
            this.statusBayar = statusBayar; this.statusOrder = statusOrder; this.notifDilihat = notifDilihat;
        }
    }

    private static final String DARI_ORDERS =
            " FROM orders o JOIN customer c ON c.Customer_ID = o.Customer_ID ";

    private OrderDAO() {}

    // === TULIS ===

    /** Menyimpan pesanan baru (orders + order_item) dalam satu transaksi. Mengembalikan Order_ID. */
    public static int simpan(int customerId, String metode, int total, List<ItemPesan> items) throws SQLException {
        String sqlOrder = "INSERT INTO orders (Customer_ID, Total, Payment_method, Payment_status, Order_status, Notif_dilihat) "
                + "VALUES (?, ?, ?, 'Belum Dibayar', 'Menunggu', 0)";
        String sqlItem = "INSERT INTO order_item (Order_ID, Item_ID, Quantity, Unit_price, Total_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = Database.buka()) {
            c.setAutoCommit(false);
            try {
                int id;
                try (PreparedStatement p = c.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                    p.setInt(1, customerId);
                    p.setInt(2, total);
                    p.setString(3, metode);
                    p.executeUpdate();
                    try (ResultSet k = p.getGeneratedKeys()) { k.next(); id = k.getInt(1); }
                }
                try (PreparedStatement p = c.prepareStatement(sqlItem)) {
                    for (ItemPesan it : items) {
                        p.setInt(1, id);
                        p.setInt(2, it.itemId);
                        p.setInt(3, it.jumlah);
                        p.setInt(4, it.harga);
                        p.setInt(5, it.harga * it.jumlah);
                        p.addBatch();
                    }
                    p.executeBatch();
                }
                c.commit();
                return id;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** Mengubah status. Notif_dilihat dikembalikan ke 0 supaya notifikasi "siap diambil" muncul lagi. */
    public static void ubahStatus(int orderId, String statusBayar, String statusOrder) throws SQLException {
        String sql = "UPDATE orders SET Payment_status = ?, Order_status = ?, Notif_dilihat = 0 WHERE Order_ID = ?";
        try (Connection c = Database.buka(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, statusBayar);
            p.setString(2, statusOrder);
            p.setInt(3, orderId);
            p.executeUpdate();
        }
    }

    /** Customer sudah melihat notifikasi pesanannya yang Siap Diambil. */
    public static void tandaiDilihat(int customerId) throws SQLException {
        String sql = "UPDATE orders SET Notif_dilihat = 1 WHERE Customer_ID = ? AND Order_status = 'Siap Diambil'";
        try (Connection c = Database.buka(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, customerId);
            p.executeUpdate();
        }
    }

    /** Menghapus pesanan beserta rinciannya (order_item ikut terhapus lewat ON DELETE CASCADE). */
    public static void hapus(int orderId) throws SQLException {
        try (Connection c = Database.buka(); PreparedStatement p = c.prepareStatement("DELETE FROM orders WHERE Order_ID = ?")) {
            p.setInt(1, orderId);
            p.executeUpdate();
        }
    }

    // === BACA (urut dari Order_ID terkecil; yang terbaru ada di akhir) ===

    public static List<Baris> ambilSemua() throws SQLException {
        return ambil("");
    }

    public static List<Baris> ambilMilik(int customerId) throws SQLException {
        return ambil("WHERE o.Customer_ID = ?", customerId);
    }

    public static Baris cari(int orderId) throws SQLException {
        List<Baris> hasil = ambil("WHERE o.Order_ID = ?", orderId);
        return hasil.isEmpty() ? null : hasil.get(0);
    }

    public static List<Baris> ambilSiapBelumDilihat(int customerId) throws SQLException {
        return ambil("WHERE o.Customer_ID = ? AND o.Order_status = 'Siap Diambil' AND o.Notif_dilihat = 0", customerId);
    }

    private static List<Baris> ambil(String where, int... param) throws SQLException {
        String sqlOrder = "SELECT o.Order_ID, o.Customer_ID, c.Username, c.Name, c.Phone, o.Order_Date, o.Total, "
                + "o.Payment_method, o.Payment_status, o.Order_status, o.Notif_dilihat"
                + DARI_ORDERS + where + " ORDER BY o.Order_ID";
        String sqlItem = "SELECT oi.Order_ID, i.Item_name, oi.Quantity, oi.Total_price "
                + "FROM order_item oi JOIN item i ON i.Item_ID = oi.Item_ID "
                + "JOIN orders o ON o.Order_ID = oi.Order_ID JOIN customer c ON c.Customer_ID = o.Customer_ID "
                + where + " ORDER BY oi.OrderItem_ID";
        List<Baris> hasil = new ArrayList<>();
        Map<Integer, Baris> peta = new HashMap<>();
        try (Connection c = Database.buka()) {
            try (PreparedStatement p = c.prepareStatement(sqlOrder)) {
                isi(p, param);
                try (ResultSet r = p.executeQuery()) {
                    while (r.next()) {
                        Baris b = new Baris(r.getInt(1), r.getInt(2), r.getString(3), r.getString(4), r.getString(5),
                                r.getTimestamp(6), r.getDouble(7), r.getString(8), r.getString(9), r.getString(10),
                                r.getInt(11) == 1);
                        hasil.add(b);
                        peta.put(b.id, b);
                    }
                }
            }
            try (PreparedStatement p = c.prepareStatement(sqlItem)) {
                isi(p, param);
                try (ResultSet r = p.executeQuery()) {
                    while (r.next()) {
                        Baris b = peta.get(r.getInt(1));
                        if (b != null) b.rincian.add(new Rincian(r.getString(2), r.getInt(3), r.getDouble(4)));
                    }
                }
            }
        }
        return hasil;
    }

    private static void isi(PreparedStatement p, int[] param) throws SQLException {
        for (int i = 0; i < param.length; i++) p.setInt(i + 1, param[i]);
    }
}
