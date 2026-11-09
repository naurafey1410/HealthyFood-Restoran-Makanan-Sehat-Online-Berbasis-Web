package healthyfood.view;

import healthyfood.model.OrderStore;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PembayaranGUI extends JFrame {
    private final Color GREEN = new Color(46, 110, 44);
    private final Color DARK_GREEN = new Color(38, 95, 36);
    private final Color PALE = new Color(244, 248, 232);
    private final Color LIME = new Color(232, 243, 206);
    private final Color MUTED = new Color(113, 122, 112);
    private final Color YELLOW = new Color(255, 241, 197);
    private int step = 1;
    private JPanel mainCard;
    private JPanel stepper;
    private JLabel statusLabel;
    private final OrderStore.Order order;

    public PembayaranGUI() { this(null); }

    public PembayaranGUI(OrderStore.Order order) {
        this.order = order;
        setTitle("Healthy Restaurant - Pembayaran");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 790);
        setMinimumSize(new Dimension(620, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0, 0, 0, 35));
        setLayout(new GridBagLayout());

        mainCard = new JPanel(new BorderLayout(0, 14));
        mainCard.setBackground(Color.WHITE);
        mainCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 239, 228), 1),
                new EmptyBorder(24, 26, 22, 26)));
        mainCard.setPreferredSize(new Dimension(590, 700));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Order Diterima!");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(DARK_GREEN);
        JLabel subtitle = new JLabel("Selesaikan pembayaran agar pesanan dapat diproses.");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(new Color(85, 91, 83));
        top.add(title);
        top.add(Box.createVerticalStrut(6));
        top.add(subtitle);
        top.add(Box.createVerticalStrut(18));
        stepper = new JPanel(new GridLayout(1, 3, 8, 0));
        stepper.setOpaque(false);
        top.add(stepper);
        mainCard.add(top, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(orderSummary());
        body.add(Box.createVerticalStrut(18));
        body.add(paymentDetails());
        body.add(Box.createVerticalStrut(14));
        statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setForeground(MUTED);
        body.add(statusLabel);
        mainCard.add(body, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 1, 12, 0));
        buttons.setOpaque(false);
        JButton action = new JButton("Saya sudah transfer");
        stylePrimary(action);
        // === TOMBOL BAYAR: customer hanya bisa kirim ke verifikasi admin ===
        action.addActionListener(e -> {
            if (step == 1) {
                step = 2;
                if (order != null) order.status = "Menunggu Verifikasi";
                statusLabel.setText("<html><div style='text-align:center'>Pembayaran sedang diverifikasi admin.<br>Status pesanan: Verifikasi.</div></html>");
                statusLabel.setOpaque(true);
                statusLabel.setBackground(YELLOW);
                statusLabel.setBorder(new EmptyBorder(12, 8, 12, 8));
                action.setText("Tutup");
                updateStepper();
            } else {
                dispose();
            }
        });
        buttons.add(action);
        mainCard.add(buttons, BorderLayout.SOUTH);

        add(mainCard);
        updateStepper();
    }

    private JPanel orderSummary() {
        JPanel box = new JPanel();
        box.setBackground(new Color(245, 249, 237));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(153, 204, 84), 1),
                new EmptyBorder(14, 15, 14, 15)));
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));

        JLabel badge = new JLabel("  Pesanan #3  ");
        badge.setOpaque(true);
        badge.setBackground(GREEN);
        badge.setForeground(Color.WHITE);
        badge.setFont(new Font("SansSerif", Font.BOLD, 14));
        badge.setBorder(new EmptyBorder(5, 5, 5, 5));
        badge.setAlignmentX(Component.RIGHT_ALIGNMENT);
        box.add(badge);
        box.add(Box.createVerticalStrut(10));

        addInfoRow(box, "Nama",     order == null ? "Contoh Customer" : order.nama);
        addInfoRow(box, "Telepon",  order == null ? "-" : order.telepon);
        addInfoRow(box, "Alamat",   order == null ? "-" : order.alamat);
        addInfoRow(box, "Metode",   order == null ? "Transfer" : order.metode);
        box.add(Box.createVerticalStrut(10));
        box.add(new JSeparator());
        box.add(Box.createVerticalStrut(10));
        if (order == null) addPriceRow(box, "Contoh pesanan", "Rp 28.000");
        else for (String item : order.rincian) addPriceRow(box, item, "");
        box.add(Box.createVerticalStrut(10));
        box.add(new JSeparator());
        box.add(Box.createVerticalStrut(10));
        addPriceRow(box, "Total", order == null ? "Rp 28.000" : order.rupiah());
        return box;
    }

    private void addInfoRow(JPanel parent, String key, String value) {
        JPanel row = new JPanel(new BorderLayout(20, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        JLabel k = new JLabel(key);
        k.setFont(new Font("SansSerif", Font.BOLD, 14));
        k.setForeground(DARK_GREEN);
        JLabel v = new JLabel(value);
        v.setFont(new Font("SansSerif", Font.PLAIN, 14));
        v.setForeground(new Color(55, 61, 54));
        row.add(k, BorderLayout.WEST);
        row.add(v, BorderLayout.CENTER);
        parent.add(row);
    }

    private void addPriceRow(JPanel parent, String left, String right) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(left);
        l.setFont(new Font("SansSerif", Font.PLAIN, 14));
        l.setForeground(new Color(55, 61, 54));
        JLabel r = new JLabel(right);
        r.setFont(new Font("SansSerif", Font.BOLD, 15));
        r.setForeground(GREEN);
        row.add(l, BorderLayout.WEST);
        row.add(r, BorderLayout.EAST);
        parent.add(row);
    }

    private JPanel paymentDetails() {
        JPanel box = new JPanel();
        box.setBackground(new Color(252, 253, 246));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207, 232, 159), 1),
                new EmptyBorder(16, 16, 16, 16)));
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Transfer ke rekening berikut");
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setFont(new Font("SansSerif", Font.BOLD, 15));
        heading.setForeground(DARK_GREEN);
        JLabel bank = new JLabel("Bank Mandiri");
        bank.setAlignmentX(Component.CENTER_ALIGNMENT);
        bank.setFont(new Font("SansSerif", Font.BOLD, 15));
        bank.setForeground(new Color(62, 67, 60));
        JLabel account = new JLabel("1234567890");
        account.setAlignmentX(Component.CENTER_ALIGNMENT);
        account.setFont(new Font("SansSerif", Font.BOLD, 29));
        account.setForeground(GREEN);
        JLabel owner = new JLabel("a.n. Healthy Restaurant");
        owner.setAlignmentX(Component.CENTER_ALIGNMENT);
        owner.setFont(new Font("SansSerif", Font.PLAIN, 14));
        owner.setForeground(new Color(62, 67, 60));
        JLabel total = new JLabel("Total: Rp 28.000");
        total.setAlignmentX(Component.CENTER_ALIGNMENT);
        total.setFont(new Font("SansSerif", Font.BOLD, 17));
        total.setForeground(GREEN);
        box.add(heading);
        box.add(Box.createVerticalStrut(10));
        box.add(bank);
        box.add(Box.createVerticalStrut(3));
        box.add(account);
        box.add(Box.createVerticalStrut(3));
        box.add(owner);
        box.add(Box.createVerticalStrut(12));
        box.add(new JSeparator());
        box.add(Box.createVerticalStrut(12));
        box.add(total);
        return box;
    }

    private void updateStepper() {
        stepper.removeAll();
        String[] labels = {"Pembayaran", "Verifikasi Admin", "Lunas"};
        for (int i = 1; i <= 3; i++) {
            JPanel item = new JPanel();
            item.setOpaque(false);
            item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
            JLabel circle = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            circle.setOpaque(true);
            circle.setPreferredSize(new Dimension(32, 32));
            circle.setMaximumSize(new Dimension(32, 32));
            circle.setAlignmentX(Component.CENTER_ALIGNMENT);
            circle.setFont(new Font("SansSerif", Font.BOLD, 14));
            circle.setForeground(Color.WHITE);
            circle.setBackground(i < step ? GREEN : (i == step ? new Color(230, 166, 0) : new Color(222, 224, 221)));
            JLabel text = new JLabel(labels[i - 1]);
            text.setAlignmentX(Component.CENTER_ALIGNMENT);
            text.setFont(new Font("SansSerif", i == step ? Font.BOLD : Font.PLAIN, 12));
            text.setForeground(i <= step ? DARK_GREEN : MUTED);
            item.add(circle);
            item.add(Box.createVerticalStrut(5));
            item.add(text);
            stepper.add(item);
        }
        stepper.revalidate();
        stepper.repaint();
    }

    private void stylePrimary(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(GREEN);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(12, 16, 12, 16));
    }

    private void styleSecondary(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(new Color(231, 241, 220));
        b.setForeground(DARK_GREEN);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(12, 16, 12, 16));
    }

    public static void main(String[] args) {
        Tema.pakai();
        SwingUtilities.invokeLater(() -> new PembayaranGUI().setVisible(true));
    }
}
