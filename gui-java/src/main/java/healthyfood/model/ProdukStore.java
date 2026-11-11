package healthyfood.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ImageIcon;

// Daftar produk bersama: dipakai halaman admin (kelola) dan halaman customer (menu)
public final class ProdukStore {

    public static final class Produk {
        public final Object foto;      // ImageIcon jika gambar ditemukan, atau emoji sebagai cadangan
        public final String nama, kalori, grup;
        public final int harga;
        public final boolean bestSeller;

        public Produk(Object foto, String nama, String kalori, int harga, String grup, boolean bestSeller) {
            this.foto = foto; this.nama = nama; this.kalori = kalori;
            this.harga = harga; this.grup = grup; this.bestSeller = bestSeller;
        }

        public String rupiah() { return "Rp " + String.format("%,d", harga).replace(',', '.'); }
    }

    // Data awal
    private static final List<Produk> PRODUK = new ArrayList<>();
    static {
        tambahAwal("salad.jpg", "🥗", "Vegetable Salad", "250 - 350 kkal", 22000, true);
        tambahAwal("steak_tempe.jpg", "🍽", "Tempeh Steak", "280 - 380 kkal", 34000, true);
        tambahAwal("steak_jamur.jpg", "🍄", "Mushroom Steak", "320 - 420 kkal", 40000, true);
        tambahAwal("mie_bayam.jpg", "🍜", "Green Spinach Noodles", "380 - 480 kkal", 28000, true);
        tambahAwal("kebab_sayur.jpg", "🥙", "Vegetable Kebab", "300 - 400 kkal", 22000, true);
        tambahAwal("sup_ayam.jpg", "🍲", "Chicken Soup", "180 - 260 kkal", 30000, true);
        tambahAwal("grain_bowl.jpg", "🥣", "Grain Bowl", "450 - 550 kkal", 27000, false);
        tambahAwal("salmon_panggang.jpg", "🐟", "Grilled Salmon", "400 - 500 kkal", 43000, false);
    }

    private ProdukStore() {}

    // Gambar dari folder gambar; kalau tidak ada, emoji dipakai
    private static void tambahAwal(String file, String emoji, String nama, String kalori, int harga, boolean best) {
        ImageIcon ikon = Gambar.ikon(file);
        Object foto = ikon != null ? ikon : emoji;
        PRODUK.add(new Produk(foto, nama, kalori, harga, "-", best));
    }

    public static synchronized List<Produk> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(PRODUK));
    }
    public static synchronized void tambah(Produk p) { PRODUK.add(p); }
    public static synchronized void ubah(int index, Produk p) { PRODUK.set(index, p); }
    public static synchronized void hapus(int index) { PRODUK.remove(index); }
}
