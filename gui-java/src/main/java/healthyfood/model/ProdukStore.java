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
        public final String deskripsi, protein, karbo, lemak, serat;

        public Produk(Object foto, String nama, String kalori, int harga, String grup, boolean bestSeller) {
            this(foto, nama, kalori, harga, grup, bestSeller,
                    "Menu makanan sehat dari Healthy Food. Nilai gizi merupakan perkiraan per porsi.",
                    "-", "-", "-", "-");
        }

        public Produk(Object foto, String nama, String kalori, int harga, String grup, boolean bestSeller,
                      String deskripsi, String protein, String karbo, String lemak, String serat) {
            this.foto = foto;
            this.nama = nama;
            this.kalori = kalori;
            this.harga = harga;
            this.grup = grup;
            this.bestSeller = bestSeller;
            this.deskripsi = deskripsi;
            this.protein = protein;
            this.karbo = karbo;
            this.lemak = lemak;
            this.serat = serat;
        }

        public String rupiah() { return "Rp " + String.format("%,d", harga).replace(',', '.'); }
    }

    // Data awal diselaraskan dengan PRODUK_AWAL & DETAIL_MENU pada proyek web (DPW)
    private static final List<Produk> PRODUK = new ArrayList<>();
    static {
        tambahAwal("salad.jpg", "🥗", "Vegetable Salad", "250 - 350 kkal", 22000, "-", true,
                "Salad sayuran segar yang cocok sebagai pilihan menu ringan dan kaya serat.",
                "5-8 g", "15-25 g", "8-15 g", "4-7 g");
        tambahAwal("steak_tempe.webp", "🍽", "Tempeh Steak", "280 - 380 kkal", 34000, "-", true,
                "Steak berbahan dasar tempe yang menawarkan protein nabati dengan cita rasa gurih.",
                "15-22 g", "20-35 g", "10-18 g", "5-8 g");
        tambahAwal("steak_jamur.jpg", "🍄", "Mushroom Steak", "320 - 420 kkal", 40000, "-", true,
                "Olahan jamur dengan tekstur lembut dan rasa gurih sebagai alternatif menu steak.",
                "8-14 g", "25-40 g", "12-20 g", "4-7 g");
        tambahAwal("mie_bayam.jpg", "🍜", "Green Spinach Noodles", "380 - 480 kkal", 28000, "-", true,
                "Mie dengan bayam sebagai variasi menu yang memadukan karbohidrat dan sayuran.",
                "8-14 g", "50-70 g", "6-14 g", "4-8 g");
        tambahAwal("kebab_sayur.jpg", "🥙", "Vegetable Kebab", "300 - 400 kkal", 22000, "-", true,
                "Kebab sayuran yang praktis dengan kombinasi sayuran dan kulit kebab.",
                "7-12 g", "35-50 g", "8-15 g", "5-8 g");
        tambahAwal("sup_ayam.jpg", "🍲", "Chicken Soup", "180 - 260 kkal", 30000, "-", true,
                "Sup ayam hangat dengan kuah dan bahan pelengkap yang cocok untuk hidangan ringan.",
                "15-25 g", "10-25 g", "5-12 g", "2-5 g");
        tambahAwal("grain_bowl.jpg", "🥣", "Grain Bowl", "450 - 550 kkal", 27000, "-", false,
                "Grain bowl berisi sumber karbohidrat dan pelengkap yang dapat memberi energi untuk aktivitas.",
                "10-18 g", "55-75 g", "10-18 g", "6-10 g");
        tambahAwal("salad_buah.jpg", "🥗", "Fruit Salad", "120 - 180 kkal", 23000, "-", false,
                "Salad buah dengan aneka potongan buah sebagai pilihan segar dengan rasa manis alami.",
                "2-5 g", "25-40 g", "1-8 g", "3-6 g");
        tambahAwal("salmon_panggang.jpg", "🐟", "Grilled Salmon", "400 - 500 kkal", 43000, "-", false,
                "Salmon panggang sebagai sumber protein dan lemak omega-3, disajikan sebagai menu utama.",
                "25-35 g", "5-20 g", "15-25 g", "1-4 g");
        tambahAwal("kebab_daging.webp", "🌯", "Chicken Caesar Wrap", "380 - 480 kkal", 27000, "-", false,
                "Wrap berisi ayam dan sayuran yang praktis untuk dinikmati sebagai menu makan.",
                "20-30 g", "35-50 g", "10-18 g", "4-7 g");
        tambahAwal("roti_salmon.jpg", "🥯", "Salmon & Cream Cheese Bagel", "350 - 450 kkal", 40000, "-", false,
                "Bagel dengan salmon dan cream cheese yang memadukan protein, karbohidrat, dan tekstur lembut.",
                "18-28 g", "35-50 g", "12-22 g", "2-5 g");
        tambahAwal("pasta_ayam.jpg", "🍝", "Grilled Chicken Pesto Pasta", "480 - 580 kkal", 30000, "-", false,
                "Pasta dengan ayam dan pesto untuk pilihan hidangan mengenyangkan dengan rasa herba.",
                "20-30 g", "55-75 g", "12-22 g", "3-7 g");
        tambahAwal("sarapan_sehat.jpg", "🍞", "Avocado Toast", "220 - 300 kkal", 22000, "sarapan", false,
                "Roti panggang dengan alpukat, cocok sebagai pilihan sarapan dengan lemak tak jenuh.",
                "5-9 g", "25-40 g", "10-18 g", "6-10 g");
        tambahAwal("sarapan_sehat.jpg", "🥣", "Oatmeal Fruit Bowl", "280 - 350 kkal", 26000, "sarapan", false,
                "Oatmeal dengan buah sebagai menu sarapan yang mengandung karbohidrat dan serat.",
                "7-12 g", "40-55 g", "5-12 g", "6-10 g");
        tambahAwal("sarapan_sehat.jpg", "🍳", "Egg & Toast Combo", "320 - 400 kkal", 28000, "sarapan", false,
                "Kombinasi telur dan roti panggang untuk sarapan praktis dengan sumber protein.",
                "12-20 g", "30-45 g", "10-18 g", "3-6 g");
    }

    private ProdukStore() {}

    // Gambar dari folder gambar; kalau tidak ada, emoji dipakai
    private static void tambahAwal(String file, String emoji, String nama, String kalori, int harga,
                                   String grup, boolean best, String deskripsi, String protein,
                                   String karbo, String lemak, String serat) {
        ImageIcon ikon = Gambar.ikon(file);
        Object foto = ikon != null ? ikon : emoji;
        PRODUK.add(new Produk(foto, nama, kalori, harga, grup, best, deskripsi, protein, karbo, lemak, serat));
    }

    public static synchronized List<Produk> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(PRODUK));
    }
    public static synchronized void tambah(Produk p) { PRODUK.add(p); }
    public static synchronized void ubah(int index, Produk p) { PRODUK.set(index, p); }
    public static synchronized void hapus(int index) { PRODUK.remove(index); }
}