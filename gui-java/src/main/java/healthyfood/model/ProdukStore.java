package healthyfood.model;

import healthyfood.dao.ItemDAO;
import healthyfood.util.Gambar;
import javax.swing.ImageIcon;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Daftar produk. Dibaca dari tabel item (database healthy_food). Dipakai admin dan customer. */
public final class ProdukStore {

    public static final class Produk {
        public final int id;                 // Item_ID di database (0 jika belum tersimpan)
        public final Object foto;            // ImageIcon jika gambar ada, atau emoji cadangan
        public final String namaFile;        // nama file foto di folder gambar (untuk disimpan ke database)
        public final String nama, kalori, grup;
        public final int harga;
        public final boolean bestSeller;
        public final String deskripsi, protein, karbo, lemak, serat;   // untuk tombol Gizi

        public Produk(Object foto, String nama, String kalori, int harga, String grup, boolean bestSeller) {
            this(0, foto, null, nama, kalori, harga, grup, bestSeller, "", "", "", "", "");
        }

        public Produk(Object foto, String namaFile, String nama, String kalori, int harga, String grup, boolean bestSeller) {
            this(0, foto, namaFile, nama, kalori, harga, grup, bestSeller, "", "", "", "", "");
        }

        public Produk(Object foto, String namaFile, String nama, String kalori, int harga, String grup, boolean bestSeller,
                      String deskripsi, String protein, String karbo, String lemak, String serat) {
            this(0, foto, namaFile, nama, kalori, harga, grup, bestSeller, deskripsi, protein, karbo, lemak, serat);
        }

        Produk(int id, Object foto, String namaFile, String nama, String kalori, int harga, String grup, boolean bestSeller,
               String deskripsi, String protein, String karbo, String lemak, String serat) {
            this.id = id; this.foto = foto; this.namaFile = namaFile; this.nama = nama; this.kalori = kalori;
            this.harga = harga; this.grup = grup; this.bestSeller = bestSeller;
            this.deskripsi = deskripsi; this.protein = protein; this.karbo = karbo; this.lemak = lemak; this.serat = serat;
        }

        public String rupiah() { return "Rp " + String.format("%,d", harga).replace(',', '.'); }
    }

    private static final String EMOJI_CADANGAN = "🍽";
    private static List<Produk> cache = null;

    private ProdukStore() {}

    /** Membaca ulang dari database. Dipanggil saat pertama kali dan setiap ada perubahan. */
    private static synchronized void muat() {
        try {
            List<Produk> baru = new ArrayList<>();
            for (ItemDAO.Baris b : ItemDAO.ambilSemua()) {
                ImageIcon ikon = b.gambar == null || b.gambar.isEmpty() ? null : Gambar.ikon(b.gambar);
                Object foto = ikon != null ? ikon : EMOJI_CADANGAN;
                baru.add(new Produk(b.id, foto, b.gambar, b.nama, b.kalori, (int) b.harga, "-", b.recommended,
                        nz(b.deskripsi), nz(b.protein), nz(b.karbo), nz(b.lemak), nz(b.serat)));
            }
            cache = baru;
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal membaca produk dari database: " + e.getMessage(), e);
        }
    }

    private static String nz(String s) { return s == null ? "" : s; }

    public static synchronized List<Produk> getAll() {
        if (cache == null) muat();
        return Collections.unmodifiableList(new ArrayList<>(cache));
    }

    public static synchronized void tambah(Produk p) {
        try {
            ItemDAO.tambah(p.nama, p.kalori, p.harga, p.namaFile, p.bestSeller, p.deskripsi, p.protein, p.karbo, p.lemak, p.serat);
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal menyimpan produk: " + e.getMessage(), e);
        }
        muat();
    }

    public static synchronized void ubah(int index, Produk p) {
        int id = getAll().get(index).id;
        try {
            ItemDAO.ubah(id, p.nama, p.kalori, p.harga, p.namaFile, p.bestSeller, p.deskripsi, p.protein, p.karbo, p.lemak, p.serat);
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal mengubah produk: " + e.getMessage(), e);
        }
        muat();
    }

    public static synchronized void hapus(int index) {
        int id = getAll().get(index).id;
        try {
            ItemDAO.hapus(id);
        } catch (SQLException e) {
            throw new IllegalStateException("Produk tidak bisa dihapus (mungkin sudah pernah dipesan).", e);
        }
        muat();
    }
}
