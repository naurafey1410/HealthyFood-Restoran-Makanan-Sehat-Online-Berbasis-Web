package healthyfood.app;

import healthyfood.view.LayarPilihan;
import healthyfood.view.Navigasi;
import healthyfood.util.Tema;
import javax.swing.SwingUtilities;

//Titik awal aplikasi HealthyFood: satu jendela, halaman berganti di dalamnya
public class App {
    public static void main(String[] args) {
        Tema.pakai();
        SwingUtilities.invokeLater(() -> {
            Navigasi.mulai();
            Navigasi.buka("pilihan", new LayarPilihan());
        });
    }
}
