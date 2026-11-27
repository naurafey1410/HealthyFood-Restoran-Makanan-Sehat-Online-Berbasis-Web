package healthyfood.model;

import healthyfood.dao.AkunDAO;
import java.sql.SQLException;

/** Login dan pendaftaran akun. Data dibaca dari tabel admin dan customer (database healthy_food). */
public final class AkunService {

    /** Customer yang sedang login. */
    public static final class Customer {
        public final int id;
        public final String nama, username, telepon;
        Customer(int id, String nama, String username, String telepon) {
            this.id = id; this.nama = nama; this.username = username; this.telepon = telepon;
        }
    }

    private AkunService() {}

    public static boolean cekAdmin(String user, String pass) {
        try {
            return AkunDAO.cekAdmin(user, pass);
        } catch (SQLException e) {
            throw new IllegalStateException("Database tidak terhubung: " + e.getMessage(), e);
        }
    }

    /** Data customer jika login benar, null jika username atau password salah. */
    public static Customer cekCustomer(String user, String pass) {
        try {
            AkunDAO.Baris b = AkunDAO.cekCustomer(user, pass);
            return b == null ? null : new Customer(b.id, b.nama, b.username, b.telepon);
        } catch (SQLException e) {
            throw new IllegalStateException("Database tidak terhubung: " + e.getMessage(), e);
        }
    }

    /** false jika username sudah dipakai. */
    public static boolean daftar(String user, String pass, String nama, String telp) {
        try {
            return AkunDAO.daftarCustomer(nama, telp, user, pass);
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal menyimpan akun: " + e.getMessage(), e);
        }
    }
}
