package healthyfood.util;

/** Aturan validasi input. Setiap method mengembalikan pesan error, atau null jika data sudah benar. */
public final class Validasi {
    private Validasi() {}

    // === PENDAFTARAN CUSTOMER ===
    public static String daftarCustomer(String nama, String telp, String user, String pw) {
        if (nama.isEmpty() || telp.isEmpty() || user.isEmpty() || pw.isEmpty())
            return "Semua kolom wajib diisi.";
        if (!telp.matches("[0-9]{10,13}"))
            return "Nomor HP harus berupa angka dan terdiri dari 10-13 digit!";
        if (pw.length() < 8)
            return "Password minimal harus 8 karakter!";
        return null;
    }

    // === PESANAN CUSTOMER ===
    public static String pesanan(String nama, String telp, String alamat, String metode, boolean keranjangKosong) {
        if (keranjangKosong) return "Keranjang masih kosong!";
        // Pesanan diambil di restoran, jadi alamat tidak wajib
        if (nama.isEmpty() || telp.isEmpty())
            return "Nama dan nomor telepon wajib diisi!";
        if (!telp.matches("\\d{9,15}"))
            return "Nomor telepon harus berupa angka (9-15 digit).";
        if (metode == null)
            return "Pilih metode pembayaran terlebih dahulu!";
        return null;
    }

    // === PRODUK (form admin) ===
    public static String produk(String nama, String harga, boolean fotoWajib, boolean adaFoto) {
        if (nama.isEmpty() || harga.isEmpty())
            return "Nama dan harga produk wajib diisi.";
        try {
            if (Integer.parseInt(harga.replace(".", "").replace(",", "")) <= 0)
                return "Harga harus lebih dari 0.";
        } catch (NumberFormatException e) {
            return "Harga harus berupa angka, contoh: 28000.";
        }
        if (fotoWajib && !adaFoto)
            return "Pilih foto produk dulu.";
        return null;
    }
}
