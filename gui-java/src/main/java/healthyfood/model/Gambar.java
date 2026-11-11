package healthyfood.model;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.io.File;

/** Mencari file gambar di folder "gambar". Bisa dijalankan dari folder gui-java atau dari root repo. */
public final class Gambar {

    // === LOKASI YANG DICEK, BERURUTAN ===
    private static final String[] FOLDER = {"gambar/", "../gambar/", "gui-java/gambar/", "hf2/gambar/"};

    private Gambar() {}

    /** File gambar dengan nama ini, atau null jika tidak ada. */
    public static File cari(String nama) {
        for (String folder : FOLDER) {
            File f = new File(folder + nama);
            if (f.exists()) return f;
        }
        // Cari ke atas dari folder kerja, supaya tetap ketemu jika dijalankan dari folder lain
        File dir = new File("").getAbsoluteFile();
        for (int i = 0; i < 8 && dir != null; i++, dir = dir.getParentFile()) {
            File f = new File(new File(dir, "gambar"), nama);
            if (f.exists()) return f;
            File g = new File(new File(new File(dir, "gui-java"), "gambar"), nama);
            if (g.exists()) return g;
        }
        return null;
    }

    // Folder tempat foto produk disimpan (folder "gambar" yang sudah ada, atau dibuat)
    public static File folderSimpan() {
        for (String folder : FOLDER) {
            File d = new File(folder);
            if (d.isDirectory()) return d;
        }
        File d = new File("gambar");
        d.mkdirs();
        return d;
    }

    // Salin foto dari komputer ke folder gambar. Nama dibuat unik agar tidak menimpa file lain
    public static File salin(File sumber) throws IOException {
        String nama = System.currentTimeMillis() + "_" + sumber.getName().replaceAll("[^A-Za-z0-9._-]", "_");
        File tujuan = new File(folderSimpan(), nama);
        Files.copy(sumber.toPath(), tujuan.toPath());
        return tujuan;
    }

    // Ikon dari file langsung (tanpa mencari folder). Null jika tidak bisa dibaca
    public static ImageIcon muatFile(File f) {
        try {
            BufferedImage img = ImageIO.read(f);
            return img == null ? null : new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    // Potong gambar agar mengisi kotak w x h penuh (tanpa area kosong), dipusatkan
    public static ImageIcon ikonIsi(ImageIcon ikon, int w, int h) {
        int asliW = ikon.getIconWidth(), asliH = ikon.getIconHeight();
        BufferedImage src = new BufferedImage(asliW, asliH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g0 = src.createGraphics();
        g0.drawImage(ikon.getImage(), 0, 0, null);
        g0.dispose();

        double skala = Math.max((double) w / asliW, (double) h / asliH);
        int sw = (int) Math.ceil(asliW * skala), sh = (int) Math.ceil(asliH * skala);
        BufferedImage hasil = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = hasil.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, (w - sw) / 2, (h - sh) / 2, sw, sh, null);
        g.dispose();
        return new ImageIcon(hasil);
    }

    // Ikon gambar, atau null jika file tidak ada atau tidak bisa dibaca
    public static ImageIcon ikon(String nama) {
        File f = cari(nama);
        if (f == null) return null;
        try {
            BufferedImage img = ImageIO.read(f);
            return img == null ? null : new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }
}
