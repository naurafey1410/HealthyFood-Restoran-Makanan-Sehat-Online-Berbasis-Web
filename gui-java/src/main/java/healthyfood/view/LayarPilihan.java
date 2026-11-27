package healthyfood.view;

import javax.swing.*;
import java.awt.*;

/** Layar pertama: pilih masuk sebagai customer atau admin. */
public class LayarPilihan extends JPanel {

    public LayarPilihan() {
        setLayout(new BorderLayout());

        JPanel kartu = Gaya.kartu();
        kartu.add(Gaya.lencana());
        kartu.add(Box.createVerticalStrut(22));
        kartu.add(Gaya.judul("Selamat datang di", 30));
        kartu.add(Gaya.judul("HealthyFood", 30));
        kartu.add(Box.createVerticalStrut(10));
        kartu.add(Gaya.sub("Silakan pilih cara masuk"));
        kartu.add(Box.createVerticalStrut(34));

        JButton customer = Gaya.tombolPilihan("Masuk sebagai Customer", "Pesan makanan sehat", true);
        JButton admin = Gaya.tombolPilihan("Masuk sebagai Admin", "Kelola restoran", false);
        customer.addActionListener(e -> Navigasi.buka("login-customer", new LoginCustomerFrame()));
        admin.addActionListener(e -> Navigasi.buka("login-admin", new LoginFrame()));
        kartu.add(customer);
        kartu.add(Box.createVerticalStrut(14));
        kartu.add(admin);

        Gaya.kunciLebar(kartu);
        add(Gaya.latar(kartu), BorderLayout.CENTER);
    }
}
