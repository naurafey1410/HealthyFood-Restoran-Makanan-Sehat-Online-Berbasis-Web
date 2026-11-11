package healthyfood.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Penyimpanan data bersama selama aplikasi HealthyFood berjalan
public final class OrderStore {
    private static final List<Order> ORDERS = new ArrayList<>();
    private static int nextId = 1;
    private OrderStore() {}

    public static synchronized Order add(String nama, String telepon, String alamat,
            String metode, List<String> rincian, int total) {
        Order order = new Order(nextId++, nama, telepon, alamat, metode, rincian, total);
        ORDERS.add(order);
        return order;
    }
    public static synchronized List<Order> getOrders() {
        return Collections.unmodifiableList(new ArrayList<>(ORDERS));
    }
    // === STATUS PESANAN ===
    public static final String MENUNGGU_BAYAR = "Menunggu Pembayaran";
    public static final String MENUNGGU_VERIF = "Menunggu Verifikasi";
    public static final String LUNAS = "Lunas";
    public static final String DIPROSES = "Diproses";
    public static final String SELESAI = "Selesai";

    public static String langkahBerikut(String status) {
        switch (status) {
            case MENUNGGU_VERIF: return LUNAS;
            case LUNAS: return DIPROSES;
            case DIPROSES: return SELESAI;
            default: return status;
        }
    }

    public static synchronized Order cari(int id) {
        for (Order o : ORDERS) if (o.id == id) return o;
        return null;
    }

    public static class Order {
        public final int id;
        public final String nama, telepon, alamat, metode;
        public final List<String> rincian;
        public final int total;
        public String status = "Menunggu Pembayaran";
        public final String tanggal = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm").format(new java.util.Date());
        private Order(int id, String nama, String telepon, String alamat, String metode, List<String> rincian, int total) {
            this.id=id; this.nama=nama; this.telepon=telepon; this.alamat=alamat; this.metode=metode;
            this.rincian=Collections.unmodifiableList(new ArrayList<>(rincian)); this.total=total;
        }
        public String rupiah() { return "Rp " + String.format("%,d", total).replace(',', '.'); }
    }
}
