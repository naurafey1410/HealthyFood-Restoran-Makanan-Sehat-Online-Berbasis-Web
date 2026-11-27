package healthyfood.dao;

import healthyfood.config.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Akses tabel item (produk menu) di database healthy_food. */
public final class ItemDAO {

    /** Satu baris tabel item. */
    public static final class Baris {
        public final int id;
        public final String nama, kalori, gambar;
        public final double harga;
        public final boolean recommended;
        public final String deskripsi, protein, karbo, lemak, serat;

        public Baris(int id, String nama, String kalori, double harga, String gambar, boolean recommended,
                     String deskripsi, String protein, String karbo, String lemak, String serat) {
            this.id = id; this.nama = nama; this.kalori = kalori;
            this.harga = harga; this.gambar = gambar; this.recommended = recommended;
            this.deskripsi = deskripsi; this.protein = protein; this.karbo = karbo; this.lemak = lemak; this.serat = serat;
        }
    }

    private ItemDAO() {}

    public static List<Baris> ambilSemua() throws SQLException {
        List<Baris> hasil = new ArrayList<>();
        String sql = "SELECT Item_ID, Item_name, Calories, Price, Image, Best_seller, Deskripsi, Protein, Karbo, Lemak, Serat FROM item ORDER BY Item_ID";
        try (Connection c = Database.buka(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {
            while (r.next()) {
                hasil.add(new Baris(r.getInt(1), r.getString(2), r.getString(3), r.getDouble(4),
                        r.getString(5), r.getInt(6) == 1,
                        r.getString(7), r.getString(8), r.getString(9), r.getString(10), r.getString(11)));
            }
        }
        return hasil;
    }

    public static void tambah(String nama, String kalori, double harga, String gambar, boolean recommended,
                              String deskripsi, String protein, String karbo, String lemak, String serat) throws SQLException {
        String sql = "INSERT INTO item (Item_name, Calories, Price, Image, Best_seller, Deskripsi, Protein, Karbo, Lemak, Serat) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = Database.buka(); PreparedStatement p = c.prepareStatement(sql)) {
            isiParameter(p, nama, kalori, harga, gambar, recommended);
            p.setString(6, deskripsi);
            p.setString(7, protein);
            p.setString(8, karbo);
            p.setString(9, lemak);
            p.setString(10, serat);
            p.executeUpdate();
        }
    }

    public static void ubah(int id, String nama, String kalori, double harga, String gambar, boolean recommended,
                            String deskripsi, String protein, String karbo, String lemak, String serat) throws SQLException {
        String sql = "UPDATE item SET Item_name = ?, Calories = ?, Price = ?, Image = ?, Best_seller = ?, "
                   + "Deskripsi = ?, Protein = ?, Karbo = ?, Lemak = ?, Serat = ? WHERE Item_ID = ?";
        try (Connection c = Database.buka(); PreparedStatement p = c.prepareStatement(sql)) {
            isiParameter(p, nama, kalori, harga, gambar, recommended);
            p.setString(6, deskripsi);
            p.setString(7, protein);
            p.setString(8, karbo);
            p.setString(9, lemak);
            p.setString(10, serat);
            p.setInt(11, id);
            p.executeUpdate();
        }
    }

    /** Gagal jika produk sudah pernah dipesan (ada di order_item). */
    public static void hapus(int id) throws SQLException {
        try (Connection c = Database.buka(); PreparedStatement p = c.prepareStatement("DELETE FROM item WHERE Item_ID = ?")) {
            p.setInt(1, id);
            p.executeUpdate();
        }
    }

    private static void isiParameter(PreparedStatement p, String nama, String kalori, double harga,
                                     String gambar, boolean recommended) throws SQLException {
        p.setString(1, nama);
        p.setString(2, kalori);
        p.setDouble(3, harga);
        p.setString(4, gambar);
        p.setInt(5, recommended ? 1 : 0);
    }
}
