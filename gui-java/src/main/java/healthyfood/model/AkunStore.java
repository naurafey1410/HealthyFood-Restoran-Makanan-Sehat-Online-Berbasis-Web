package healthyfood.model;

import java.util.LinkedHashMap;
import java.util.Map;

/** Akun login demo. Akun customer baru hanya tersimpan selama aplikasi berjalan. */
public final class AkunStore {
    // === AKUN ADMIN (hardcoded, demo) ===
    private static final String ADMIN_USER = "dimas";
    private static final String ADMIN_PASS = "admin123";

    // === AKUN CUSTOMER: username -> {password, nama, telepon} ===
    private static final Map<String, String[]> CUSTOMER = new LinkedHashMap<>();
    static {
        CUSTOMER.put("customer", new String[]{"customer123", "Pelanggan Demo", "081234567890"});
    }

    private AkunStore() {}

    public static boolean cekAdmin(String user, String pass) {
        return ADMIN_USER.equals(user) && ADMIN_PASS.equals(pass);
    }

    /** Mengembalikan {password, nama, telepon} jika cocok, null jika tidak. */
    public static synchronized String[] cekCustomer(String user, String pass) {
        String[] akun = CUSTOMER.get(user);
        return (akun != null && akun[0].equals(pass)) ? akun : null;
    }

    public static synchronized boolean daftar(String user, String pass, String nama, String telp) {
        if (CUSTOMER.containsKey(user)) return false;
        CUSTOMER.put(user, new String[]{pass, nama, telp});
        return true;
    }
}
