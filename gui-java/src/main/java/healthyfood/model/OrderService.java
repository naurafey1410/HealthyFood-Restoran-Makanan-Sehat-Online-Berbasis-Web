package healthyfood.model;

import healthyfood.dao.OrderDAO;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pesanan dan statusnya. Data disimpan di tabel orders dan order_item (database healthy_food).
 * Dipakai customer (buat pesanan, lihat status, notifikasi) dan admin (ubah status).
 */
public final class OrderService {
    private OrderService() {}

    // === STATUS PESANAN ===
    // Alur: Belum Dibayar -> Menunggu Konfirmasi (customer menekan "Saya sudah transfer")
    //       -> Dibayar -> Diproses -> Siap Diambil (admin, pesanan diambil di restoran)
    // Di database status dipisah: Payment_status dan Order_status (lihat pisah() dan gabung()).
    public static final String MENUNGGU_BAYAR = "Belum Dibayar";
    public static final String MENUNGGU_KONFIRMASI = "Menunggu Konfirmasi";
    public static final String DIBAYAR = "Dibayar";
    public static final String DIPROSES = "Diproses";
    public static final String SIAP_DIAMBIL = "Siap Diambil";

    /** Status berikutnya untuk tombol Ubah Status oleh admin. */
    public static String langkahBerikut(String status) {
        switch (status) {
            case MENUNGGU_KONFIRMASI: return DIBAYAR;
            case DIBAYAR: return DIPROSES;
            case DIPROSES: return SIAP_DIAMBIL;
            default: return status;
        }
    }

    /** Satu item di keranjang yang akan dipesan. */
    public static final class ItemPesan {
        public final int itemId, jumlah, harga;
        public ItemPesan(int itemId, int jumlah, int harga) { this.itemId = itemId; this.jumlah = jumlah; this.harga = harga; }
    }

    public static class Order {
        public final int id, customerId;
        public final String username, nama, telepon, metode;
        public final List<String> rincian;
        public final int total;
        public volatile String status;
        public final String tanggal;

        private Order(OrderDAO.Baris b) {
            this.id = b.id; this.customerId = b.customerId; this.username = b.username;
            this.nama = b.nama; this.telepon = b.telepon; this.metode = b.metode;
            this.total = (int) b.total;
            this.status = gabung(b.statusBayar, b.statusOrder);
            this.tanggal = new SimpleDateFormat("dd MMM yyyy HH:mm").format(b.tanggal);
            List<String> r = new ArrayList<>();
            for (OrderDAO.Rincian x : b.rincian) r.add(x.namaItem + " x" + x.jumlah + " = " + OrderService.rupiah((int) x.hargaTotal));
            this.rincian = Collections.unmodifiableList(r);
        }

        public String rupiah() { return OrderService.rupiah(total); }
    }

    static String rupiah(int angka) { return "Rp " + String.format("%,d", angka).replace(',', '.'); }

    // === PEMETAAN STATUS GUI <-> KOLOM DATABASE ===
    /** Status gabungan (dipakai tampilan) dari Payment_status dan Order_status. */
    static String gabung(String statusBayar, String statusOrder) {
        if (SIAP_DIAMBIL.equals(statusOrder)) return SIAP_DIAMBIL;
        if (DIPROSES.equals(statusOrder)) return DIPROSES;
        return statusBayar;   // Belum Dibayar | Menunggu Konfirmasi | Dibayar
    }

    /** {Payment_status, Order_status} untuk disimpan ke database. */
    static String[] pisah(String status) {
        switch (status) {
            case MENUNGGU_KONFIRMASI: return new String[]{MENUNGGU_KONFIRMASI, "Menunggu"};
            case DIBAYAR: return new String[]{DIBAYAR, "Menunggu"};
            case DIPROSES: return new String[]{DIBAYAR, DIPROSES};
            case SIAP_DIAMBIL: return new String[]{DIBAYAR, SIAP_DIAMBIL};
            default: return new String[]{MENUNGGU_BAYAR, "Menunggu"};
        }
    }

    // === BUAT PESANAN ===
    public static Order buat(int customerId, String metode, List<ItemPesan> items) {
        int total = 0;
        List<OrderDAO.ItemPesan> daftar = new ArrayList<>();
        for (ItemPesan it : items) {
            total += it.harga * it.jumlah;
            daftar.add(new OrderDAO.ItemPesan(it.itemId, it.jumlah, it.harga));
        }
        try {
            int id = OrderDAO.simpan(customerId, metode, total, daftar);
            return cari(id);
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal menyimpan pesanan: " + e.getMessage(), e);
        }
    }

    // === BACA ===
    /** Semua pesanan (untuk admin). Yang terbaru ada di akhir daftar. */
    public static List<Order> getOrders() {
        try {
            return ubah(OrderDAO.ambilSemua());
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal membaca pesanan: " + e.getMessage(), e);
        }
    }

    public static Order cari(int id) {
        try {
            OrderDAO.Baris b = OrderDAO.cari(id);
            return b == null ? null : new Order(b);
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal membaca pesanan: " + e.getMessage(), e);
        }
    }

    /** Semua pesanan milik customer ini, yang terbaru di atas. */
    public static List<Order> milik(int customerId) {
        try {
            List<Order> hasil = ubah(OrderDAO.ambilMilik(customerId));
            Collections.reverse(hasil);
            return hasil;
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal membaca pesanan: " + e.getMessage(), e);
        }
    }

    /** Pesanan customer yang sudah Siap Diambil tetapi notifikasinya belum dilihat. */
    public static List<Order> siapBelumDilihat(int customerId) {
        try {
            return ubah(OrderDAO.ambilSiapBelumDilihat(customerId));
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal membaca pesanan: " + e.getMessage(), e);
        }
    }

    private static List<Order> ubah(List<OrderDAO.Baris> baris) {
        List<Order> hasil = new ArrayList<>();
        for (OrderDAO.Baris b : baris) hasil.add(new Order(b));
        return hasil;
    }

    // === UBAH STATUS ===
    /** Dipanggil admin. Jika statusnya menjadi Siap Diambil, customer akan mendapat notifikasi. */
    public static void ubahStatus(Order o, String statusBaru) {
        String[] kolom = pisah(statusBaru);
        try {
            OrderDAO.ubahStatus(o.id, kolom[0], kolom[1]);
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal mengubah status pesanan: " + e.getMessage(), e);
        }
        o.status = statusBaru;
    }

    /** Customer menekan "Saya sudah transfer": menunggu verifikasi admin. */
    public static void konfirmasiTransfer(Order o) {
        ubahStatus(o, MENUNGGU_KONFIRMASI);
    }

    public static void tandaiDilihat(int customerId) {
        try {
            OrderDAO.tandaiDilihat(customerId);
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal memperbarui notifikasi: " + e.getMessage(), e);
        }
    }
}
