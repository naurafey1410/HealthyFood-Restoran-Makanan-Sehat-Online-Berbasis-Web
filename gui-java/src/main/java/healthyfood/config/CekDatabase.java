package healthyfood.config;

import healthyfood.dao.AkunDAO;
import healthyfood.dao.ItemDAO;
import healthyfood.dao.OrderDAO;
import java.sql.*;
import java.util.Collections;
import java.util.List;

/**
 * Pengecekan database dari terminal (tanpa membuka GUI). Jalankan lewat cek-database.bat.
 * Yang diperiksa: koneksi, lima tabel, kolom Notif_dilihat, login akun contoh, dan alur pesanan
 * (simpan, ubah status, notifikasi, lalu dihapus lagi sehingga data tidak tertinggal).
 */
public final class CekDatabase {
    private static int gagal = 0;

    private CekDatabase() {}

    private static void hasil(boolean ok, String teks) {
        System.out.println((ok ? "  [OK]    " : "  [GAGAL] ") + teks);
        if (!ok) gagal++;
    }

    public static void main(String[] args) {
        System.out.println("== 1. Koneksi ==");
        try (Connection c = Database.buka()) {
            DatabaseMetaData md = c.getMetaData();
            hasil(true, "Terhubung ke database '" + c.getCatalog() + "' (" + md.getDatabaseProductName()
                    + " " + md.getDatabaseProductVersion() + ")");

            System.out.println("== 2. Tabel ==");
            String[] tabel = {"admin", "customer", "item", "orders", "order_item"};
            for (String t : tabel) {
                try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT COUNT(*) FROM " + t)) {
                    r.next();
                    hasil(true, t + " : " + r.getInt(1) + " baris");
                } catch (SQLException e) {
                    hasil(false, t + " tidak ada. Impor database/healthy_food.sql dulu.");
                }
            }

            System.out.println("== 3. Kolom notifikasi ==");
            try (ResultSet r = md.getColumns(c.getCatalog(), null, "orders", "Notif_dilihat")) {
                hasil(r.next(), "orders.Notif_dilihat"
                        + " (jika GAGAL: impor ulang healthy_food.sql terbaru)");
            }
        } catch (SQLException e) {
            hasil(false, "Tidak bisa terhubung: " + e.getMessage());
            System.out.println("\nPeriksa: MySQL sudah jalan? Database healthy_food sudah dibuat? "
                    + "USER/PASS di config/Database.java sudah benar?");
            System.exit(1);
        }

        try {
            System.out.println("== 4. Login akun contoh ==");
            hasil(AkunDAO.cekAdmin("dimas", "admin123"), "admin dimas / admin123");
            AkunDAO.Baris cust = AkunDAO.cekCustomer("ainiimup", "customer123");
            hasil(cust != null, "customer ainiimup / customer123");
            hasil(AkunDAO.cekCustomer("ainiimup", "salah") == null, "password salah ditolak");

            System.out.println("== 5. Menu (tabel item) ==");
            List<ItemDAO.Baris> menu = ItemDAO.ambilSemua();
            hasil(!menu.isEmpty(), menu.size() + " menu terbaca");

            if (cust != null && !menu.isEmpty()) {
                System.out.println("== 6. Alur pesanan (data uji dihapus lagi di akhir) ==");
                ItemDAO.Baris m = menu.get(0);
                int id = OrderDAO.simpan(cust.id, "QRIS", (int) m.harga * 2,
                        Collections.singletonList(new OrderDAO.ItemPesan(m.id, 2, (int) m.harga)));
                try {
                    OrderDAO.Baris o = OrderDAO.cari(id);
                    hasil(o != null && o.rincian.size() == 1 && o.rincian.get(0).jumlah == 2,
                            "pesanan #" + id + " tersimpan (orders + order_item)");
                    hasil(o != null && "Belum Dibayar".equals(o.statusBayar) && "Menunggu".equals(o.statusOrder),
                            "status awal Belum Dibayar / Menunggu");

                    OrderDAO.ubahStatus(id, "Dibayar", "Siap Diambil");
                    hasil(adaNotifikasi(cust.id, id), "admin mengubah ke Siap Diambil -> notifikasi customer muncul");

                    OrderDAO.tandaiDilihat(cust.id);
                    hasil(!adaNotifikasi(cust.id, id), "customer membuka Pesanan Saya -> notifikasi hilang");
                } finally {
                    OrderDAO.hapus(id);
                }
                hasil(OrderDAO.cari(id) == null, "data uji dibersihkan");
            }
        } catch (SQLException e) {
            hasil(false, "Query gagal: " + e.getMessage());
        }

        System.out.println();
        if (gagal == 0) System.out.println("SEMUA PENGECEKAN BERHASIL. Database siap dipakai aplikasi.");
        else System.out.println(gagal + " pengecekan GAGAL. Lihat baris [GAGAL] di atas.");
        System.exit(gagal == 0 ? 0 : 1);
    }

    /** true jika pesanan id ada di daftar notifikasi (Siap Diambil, belum dilihat) customer. */
    private static boolean adaNotifikasi(int customerId, int orderId) throws SQLException {
        for (OrderDAO.Baris b : OrderDAO.ambilSiapBelumDilihat(customerId)) if (b.id == orderId) return true;
        return false;
    }
}
