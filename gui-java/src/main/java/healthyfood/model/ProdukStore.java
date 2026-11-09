package healthyfood.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Daftar produk bersama: dipakai halaman admin (kelola) dan halaman customer (menu). */
public final class ProdukStore {

    public static final class Produk {
        public final String foto, nama, kalori, grup;
        public final int harga;
        public final boolean bestSeller;

        public Produk(String foto, String nama, String kalori, int harga, String grup, boolean bestSeller) {
            this.foto = foto; this.nama = nama; this.kalori = kalori;
            this.harga = harga; this.grup = grup; this.bestSeller = bestSeller;
        }

        public String rupiah() { return "Rp " + String.format("%,d", harga).replace(',', '.'); }
    }

    // === DATA AWAL (sama dengan tabel admin sebelumnya) ===
    private static final List<Produk> PRODUK = new ArrayList<>();
    static {
        PRODUK.add(new Produk(" ", "Vegetable Salad", "250 - 350 kkal", 22000, "-", true));
        PRODUK.add(new Produk(" ", "Tempeh Steak", "280 - 380 kkal", 34000, "-", true));
        PRODUK.add(new Produk(" ", "Mushroom Steak", "320 - 420 kkal", 40000, "-", true));
        PRODUK.add(new Produk(" ", "Green Spinach Noodles", "380 - 480 kkal", 28000, "-", true));
        PRODUK.add(new Produk(" ", "Vegetable Kebab", "300 - 400 kkal", 22000, "-", true));
        PRODUK.add(new Produk(" ", "Chicken Soup", "180 - 260 kkal", 30000, "-", true));
        PRODUK.add(new Produk(" ", "Grain Bowl", "450 - 550 kkal", 27000, "-", false));
        PRODUK.add(new Produk(" ", "Grilled Salmon", "400 - 500 kkal", 43000, "-", false));
    }

    private ProdukStore() {}

    public static synchronized List<Produk> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(PRODUK));
    }
    public static synchronized void tambah(Produk p) { PRODUK.add(p); }
    public static synchronized void ubah(int index, Produk p) { PRODUK.set(index, p); }
    public static synchronized void hapus(int index) { PRODUK.remove(index); }
}
