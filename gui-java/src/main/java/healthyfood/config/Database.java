package healthyfood.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Koneksi ke database MySQL "healthy_food" (XAMPP).
 * Ubah USER dan PASS jika password root di XAMPP sudah diganti.
 */
public final class Database {
    private static final String URL = "jdbc:mariadb://localhost:3306/healthy_food?useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASS = "";

    private Database() {}

    /** Membuka koneksi baru. Pemanggil wajib menutupnya (try-with-resources). */
    public static Connection buka() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /** Tes koneksi: menampilkan jumlah produk dari tabel item. Jalankan langsung untuk mengecek. */
    public static void main(String[] args) {
        try (Connection c = buka();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT COUNT(*) FROM item")) {
            r.next();
            System.out.println("Koneksi berhasil. Jumlah produk: " + r.getInt(1));
        } catch (SQLException e) {
            System.out.println("Koneksi GAGAL: " + e.getMessage());
        }
    }
}
