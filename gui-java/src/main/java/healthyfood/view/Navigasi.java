package healthyfood.view;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/** Satu jendela utama. Semua halaman tampil bergantian di dalamnya (CardLayout). */
public final class Navigasi {
    private static final CardLayout KARTU = new CardLayout();
    private static final JPanel WADAH = new JPanel(KARTU);
    private static final Map<String, Component> HALAMAN = new HashMap<>();

    private Navigasi() {}

    // === BUAT JENDELA UTAMA (sekali, dari App) ===
    public static void mulai() {
        JFrame jendela = new JFrame("HealthyFood");
        jendela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jendela.setSize(1250, 760);
        jendela.setLocationRelativeTo(null);
        jendela.setExtendedState(JFrame.MAXIMIZED_BOTH);
        jendela.add(WADAH);
        jendela.setVisible(true);
    }

    // === TAMPILKAN HALAMAN (halaman dengan key sama diganti dengan yang baru) ===
    public static void buka(String key, JPanel halaman) {
        Component lama = HALAMAN.get(key);
        if (lama != null) WADAH.remove(lama);
        Component isi = halaman;
        HALAMAN.put(key, isi);
        WADAH.add(isi, key);
        KARTU.show(WADAH, key);
        WADAH.revalidate();
        WADAH.repaint();
    }
}
