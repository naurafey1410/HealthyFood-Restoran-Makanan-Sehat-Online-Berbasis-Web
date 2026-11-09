package healthyfood.view;

import javax.swing.UIManager;

/** Pengaturan tampilan bersama. Metal dipakai supaya warna tombol tidak diabaikan Windows. */
public final class Tema {
    private Tema() {}

    public static void pakai() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) { }
    }
}
