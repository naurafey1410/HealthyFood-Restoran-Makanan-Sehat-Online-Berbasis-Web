package healthyfood.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ImageIcon;

public final class ProdukStore {

    public static final class Produk {
        public final Object foto; 
        public final String nama, kalori, grup;
        public final int harga;
        public final boolean bestSeller;

        public Produk(Object foto, String nama, String kalori, int harga, String grup, boolean bestSeller) {
            this.foto = foto; this.nama = nama; this.kalori = kalori;
            this.harga = harga; this.grup = grup; this.bestSeller = bestSeller;
        }

        public String rupiah() { return "Rp " + String.format("%,d", harga).replace(',', '.'); }
    }

    private static final List<Produk> PRODUK = new ArrayList<>();
    static {
        PRODUK.add(new Produk(new ImageIcon("gambar/salad.jpg"), "Vegetable Salad", "250 - 350 kkal", 22000, "-", true));
        PRODUK.add(new Produk(new ImageIcon("gambar/steak_tempeh.jpg"), "Tempeh Steak", "280 - 380 kkal", 34000, "-", true));
        PRODUK.add(new Produk(new ImageIcon("gambar/steak_jamur.jpg"), "Mushroom Steak", "320 - 420 kkal", 40000, "-", true));
        PRODUK.add(new Produk(new ImageIcon("gambar/mie_bayam.jpg"), "Green Spinach Noodles", "380 - 480 kkal", 28000, "-", true));
        PRODUK.add(new Produk(new ImageIcon("gambar/kebab_sayur.jpg"), "Vegetable Kebab", "300 - 400 kkal", 22000, "-", true));
        PRODUK.add(new Produk(new ImageIcon("gambar/sup_ayam.jpg"), "Chicken Soup", "180 - 260 kkal", 30000, "-", true));
        PRODUK.add(new Produk(new ImageIcon("gambar/grain_bowl.jpg"), "Grain Bowl", "450 - 550 kkal", 27000, "-", false));
        PRODUK.add(new Produk(new ImageIcon("gambar/salmon_panggang.jpg"), "Grilled Salmon", "400 - 500 kkal", 43000, "-", false));
    }

    private ProdukStore() {}

    public static synchronized List<Produk> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(PRODUK));
    }
    public static synchronized void tambah(Produk p) { PRODUK.add(p); }
    public static synchronized void ubah(int index, Produk p) { PRODUK.set(index, p); }
    public static synchronized void hapus(int index) { PRODUK.remove(index); }

    private static ImageIcon muat(String path) {
        java.io.File f = new java.io.File(path);
        System.out.println("[GAMBAR] dicari di: " + f.getAbsolutePath());
        System.out.println("[GAMBAR] file ada? " + f.exists());
        try {
            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(f);
            if (img == null) {
                System.out.println("[GAMBAR] format tidak dikenali (bukan JPG/PNG asli)");
                return new ImageIcon();
            }
            System.out.println("[GAMBAR] berhasil dibaca: " + img.getWidth() + "x" + img.getHeight());
            return new ImageIcon(img);
        } catch (Exception e) {
            System.out.println("[GAMBAR] error: " + e);
            return new ImageIcon();
        }
    }
}
