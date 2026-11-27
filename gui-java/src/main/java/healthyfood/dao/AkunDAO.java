package healthyfood.dao;

import healthyfood.config.Database;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

/** Akses tabel admin dan customer (login dan pendaftaran akun) di database healthy_food. */
public final class AkunDAO {

    /** Satu baris tabel customer (tanpa password). */
    public static final class Baris {
        public final int id;
        public final String nama, username, telepon;

        public Baris(int id, String nama, String username, String telepon) {
            this.id = id; this.nama = nama; this.username = username; this.telepon = telepon;
        }
    }

    private AkunDAO() {}

    /** true jika username dan password admin cocok. */
    public static boolean cekAdmin(String username, String password) throws SQLException {
        String sql = "SELECT Password FROM admin WHERE Username = ?";
        try (Connection c = Database.buka(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            try (ResultSet r = p.executeQuery()) {
                // Password dibandingkan di Java (bukan di SQL) supaya huruf besar/kecil dibedakan
                return r.next() && cocok(password, r.getString(1));
            }
        }
    }

    /** Data customer jika login benar, atau null jika username/password salah. */
    public static Baris cekCustomer(String username, String password) throws SQLException {
        String sql = "SELECT Customer_ID, Name, Username, Phone, Password FROM customer WHERE Username = ?";
        try (Connection c = Database.buka(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next() || !cocok(password, r.getString(5))) return null;
                return new Baris(r.getInt(1), r.getString(2), r.getString(3), r.getString(4));
            }
        }
    }

    public static boolean usernameAda(String username) throws SQLException {
        try (Connection c = Database.buka();
             PreparedStatement p = c.prepareStatement("SELECT 1 FROM customer WHERE Username = ?")) {
            p.setString(1, username);
            try (ResultSet r = p.executeQuery()) { return r.next(); }
        }
    }

    /** Menyimpan customer baru. false jika username sudah dipakai. */
    public static boolean daftarCustomer(String nama, String telepon, String username, String password) throws SQLException {
        if (usernameAda(username)) return false;
        String sql = "INSERT INTO customer (Name, Username, Phone, Password) VALUES (?, ?, ?, ?)";
        try (Connection c = Database.buka(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, nama);
            p.setString(2, username);
            p.setString(3, telepon);
            p.setString(4, BCrypt.hashpw(password, BCrypt.gensalt()));   // password disimpan sebagai hash
            p.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) return false;   // username kembar (didaftarkan bersamaan)
            throw e;
        }
    }

    /** Membandingkan password dengan hash di database. Hash rusak atau kosong dianggap tidak cocok. */
    private static boolean cocok(String password, String hash) {
        try {
            return BCrypt.checkpw(password, hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
