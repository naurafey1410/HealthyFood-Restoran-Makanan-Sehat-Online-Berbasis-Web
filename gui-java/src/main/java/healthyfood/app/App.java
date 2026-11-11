package healthyfood.app;

import healthyfood.view.LoginCustomerFrame;
import healthyfood.view.Navigasi;
import healthyfood.view.Tema;
import javax.swing.SwingUtilities;

//Titik awal aplikasi HealthyFood: satu jendela, halaman berganti di dalamnya
public class App {
    public static void main(String[] args) {
        Tema.pakai();
        SwingUtilities.invokeLater(() -> {
            Navigasi.mulai();
            Navigasi.buka("login-customer", new LoginCustomerFrame());
        });
    }
}
