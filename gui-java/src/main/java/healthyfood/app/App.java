package healthyfood.app;

import healthyfood.view.LoginCustomerFrame;
import healthyfood.view.Tema;
import javax.swing.SwingUtilities;

/** Titik awal aplikasi HealthyFood: mulai dari login customer. */
public class App {
    public static void main(String[] args) {
        Tema.pakai();
        SwingUtilities.invokeLater(() -> new LoginCustomerFrame());
    }
}
