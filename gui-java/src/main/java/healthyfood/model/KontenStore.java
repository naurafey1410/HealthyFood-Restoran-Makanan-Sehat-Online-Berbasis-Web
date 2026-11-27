package healthyfood.model;

/** Judul dan deskripsi di header halaman customer. Bisa diubah dari menu Konten (tersimpan di memori). */
public final class KontenStore {
    private static final String JUDUL_AWAL = "Healthy Food";
    private static final String DESKRIPSI_AWAL = "Pilih menu sehat favoritmu hari ini";
    private static String judul = JUDUL_AWAL;
    private static String deskripsi = DESKRIPSI_AWAL;

    private KontenStore() {}

    public static synchronized String getJudul() { return judul; }
    public static synchronized String getDeskripsi() { return deskripsi; }

    public static synchronized void simpan(String j, String d) {
        judul = j;
        deskripsi = d;
    }

    public static synchronized void kembalikan() {
        judul = JUDUL_AWAL;
        deskripsi = DESKRIPSI_AWAL;
    }
}
