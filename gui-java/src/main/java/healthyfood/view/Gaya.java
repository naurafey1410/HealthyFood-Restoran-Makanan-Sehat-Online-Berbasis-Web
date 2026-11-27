package healthyfood.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;

/** Gaya bersama untuk tiga halaman login: latar bercak warna, kartu kaca, kolom isian, dan tombol. */
final class Gaya {
    static final Color HIJAU_TUA = new Color(14, 84, 22);
    static final Color HIJAU = new Color(40, 160, 60);
    static final Color TEKS_HIJAU = new Color(18, 82, 30);
    static final Color TEKS_SUB = new Color(44, 112, 56);
    static final int LEBAR_KARTU = 640;

    private Gaya() {}

    // === LATAR: gradasi hijau muda dengan bercak warna lembut ===
    static JPanel latar(JComponent isi) {
        JPanel p = latarKosong(new GridBagLayout());
        p.add(isi);
        return p;
    }

    /** Panel dengan latar gradasi (tanpa isi); layout diberikan dari luar. */
    static JPanel latarKosong(LayoutManager lm) {
        return new JPanel(lm) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setPaint(new GradientPaint(0, 0, new Color(206, 236, 210), w, h, new Color(238, 248, 228)));
                g2.fillRect(0, 0, w, h);
                float r = Math.max(w, h) * 0.38f;
                bercak(g2, w * 0.88f, h * 0.12f, r, new Color(110, 200, 120, 190));
                bercak(g2, w * 0.12f, h * 0.92f, r, new Color(214, 242, 120, 210));
                bercak(g2, w * 0.92f, h * 0.82f, r * 0.9f, new Color(140, 214, 196, 180));
                g2.dispose();
            }
        };
    }

    /** Sidebar dengan gradasi hijau tua ke hijau sedang. */
    static JPanel panelGradasi(LayoutManager lm) {
        return new JPanel(lm) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(14, 84, 22), 0, getHeight(), new Color(64, 162, 84)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
    }

    /** Kartu putih dengan sudut membulat dan garis tepi lembut. */
    static JPanel kartuPutih(LayoutManager lm, int pad) {
        JPanel k = new JPanel(lm) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 242));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 26, 26));
                g2.setColor(new Color(222, 232, 214));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 26, 26));
                g2.dispose();
            }
        };
        k.setOpaque(false);
        k.setBorder(new EmptyBorder(pad, pad, pad, pad));
        return k;
    }

    private static void bercak(Graphics2D g2, float x, float y, float r, Color c) {
        Color tepi = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0);
        g2.setPaint(new RadialGradientPaint(x, y, r, new float[]{0f, 1f}, new Color[]{c, tepi}));
        g2.fill(new Rectangle2D.Float(x - r, y - r, 2 * r, 2 * r));
    }

    // === KARTU KACA: putih transparan dengan sudut besar ===
    static JPanel kartu() {
        JPanel k = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 205));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 44, 44));
                g2.setColor(new Color(255, 255, 255, 245));
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 3, getHeight() - 3, 44, 44));
                g2.dispose();
            }
        };
        k.setOpaque(false);
        k.setLayout(new BoxLayout(k, BoxLayout.Y_AXIS));
        k.setBorder(new EmptyBorder(44, 64, 44, 64));
        return k;
    }

    /** Mengunci lebar kartu ke LEBAR_KARTU; tinggi mengikuti isi. Dipanggil setelah semua isi ditambahkan. */
    static void kunciLebar(JPanel k) {
        k.setPreferredSize(new Dimension(LEBAR_KARTU, k.getPreferredSize().height));
    }

    // === LENCANA: lingkaran hijau gradasi berisi huruf H ===
    static JComponent lencana() {
        JComponent c = new JComponent() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, HIJAU, getWidth(), getHeight(), HIJAU_TUA));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 30));
                FontMetrics fm = g2.getFontMetrics();
                String t = "H";
                g2.drawString(t, (getWidth() - fm.stringWidth(t)) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        c.setPreferredSize(new Dimension(84, 84));
        c.setMaximumSize(new Dimension(84, 84));
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
        return c;
    }

    static JLabel judul(String teks, int ukuran) {
        JLabel l = new JLabel(teks, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, ukuran));
        l.setForeground(TEKS_HIJAU);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    static JLabel sub(String teks) {
        JLabel l = new JLabel(teks, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        l.setForeground(TEKS_SUB);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    static JLabel label(String teks) {
        JLabel l = new JLabel(teks);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(TEKS_HIJAU);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // === KOLOM ISIAN: kapsul putih dengan kotak gelap di dalamnya ===
    static JPanel kolom(JComponent isian) {
        JPanel p = kolomDasar(isian);
        if (isian instanceof JPasswordField) {
            JPasswordField pf = (JPasswordField) isian;
            JButton mata = tombolMata();
            mata.addActionListener(e -> pf.setEchoChar(pf.getEchoChar() == 0 ? '\u2022' : (char) 0));
            p.add(mata, BorderLayout.EAST);
        }
        return p;
    }

    /** Tombol ikon mata: tampilkan atau sembunyikan password (digambar, tanpa emoji). */
    static JButton tombolMata() {
        JButton b = new JButton() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(1.8f));
                g2.setColor(TEKS_HIJAU);
                int w = getWidth(), h = getHeight();
                double cx = w / 2.0, cy = h / 2.0;
                Path2D.Double mata = new Path2D.Double();
                mata.moveTo(cx - 9, cy);
                mata.quadTo(cx, cy - 8, cx + 9, cy);
                mata.quadTo(cx, cy + 8, cx - 9, cy);
                g2.draw(mata);
                g2.draw(new Ellipse2D.Double(cx - 3.5, cy - 3.5, 7, 7));
                if (Boolean.TRUE.equals(getClientProperty("tampil"))) {
                    g2.draw(new Line2D.Double(cx - 9, cy + 8, cx + 9, cy - 8));   // garis coret: password terlihat
                }
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(44, 44));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> b.putClientProperty("tampil", !Boolean.TRUE.equals(b.getClientProperty("tampil"))));
        b.addActionListener(e -> b.repaint());
        return b;
    }

    private static JPanel kolomDasar(JComponent isian) {
        JPanel p = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 235));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 22, 22));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(8, 8, 8, 8));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setPreferredSize(new Dimension(500, 60));
        p.setMaximumSize(new Dimension(500, 60));
        p.add(isian, BorderLayout.CENTER);
        return p;
    }

    /** Label di atas kolom isian, dalam blok selebar 500 piksel (sejajar dengan tombol). */
    static JPanel blokKolom(String teks, JComponent isian) {
        JPanel blok = new JPanel();
        blok.setOpaque(false);
        blok.setLayout(new BoxLayout(blok, BoxLayout.Y_AXIS));
        blok.setAlignmentX(Component.CENTER_ALIGNMENT);
        blok.setPreferredSize(new Dimension(500, 90));
        blok.setMaximumSize(new Dimension(500, 90));
        blok.add(label(teks));
        blok.add(Box.createVerticalStrut(6));
        blok.add(kolom(isian));
        return blok;
    }

    static JTextField fieldGelap() {
        JTextField f = new JTextField();
        estilGelap(f);
        return f;
    }

    static JPasswordField passwordGelap() {
        JPasswordField f = new JPasswordField();
        estilGelap(f);
        return f;
    }

    private static void estilGelap(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        f.setBackground(new Color(28, 28, 28));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(new EmptyBorder(10, 14, 10, 14));
    }

    // === TOMBOL ===
    static JButton tombolUtama(String teks) {
        JButton b = new JButton(teks) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, HIJAU, getWidth(), getHeight(), HIJAU_TUA));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 22, 22));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        estilTombol(b, 17, Color.WHITE);
        b.setPreferredSize(new Dimension(500, 54));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        return b;
    }

    static JButton tombolPutih(String teks) {
        JButton b = new JButton(teks) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 235));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 22, 22));
                g2.setColor(new Color(190, 210, 180));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 22, 22));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        estilTombol(b, 16, TEKS_HIJAU);
        b.setPreferredSize(new Dimension(500, 54));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        return b;
    }

    static JButton tombolLink(String teks) {
        JButton b = new JButton(teks);
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setForeground(new Color(30, 110, 45));
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static void estilTombol(JButton b, int ukuran, Color warna) {
        b.setFont(new Font("Segoe UI", Font.BOLD, ukuran));
        b.setForeground(warna);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Kartu pilihan besar (judul dan keterangan), dipakai di layar pilihan. */
    static JButton tombolPilihan(String judul, String keterangan, boolean utama) {
        JButton b = new JButton() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D bentuk = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 22, 22);
                if (utama) g2.setPaint(new GradientPaint(0, 0, HIJAU, getWidth(), getHeight(), HIJAU_TUA));
                else g2.setColor(new Color(255, 255, 255, 240));
                g2.fill(bentuk);
                g2.setColor(utama ? Color.WHITE : TEKS_HIJAU);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.drawString(judul, 26, getHeight() / 2 - 2);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                g2.setColor(utama ? new Color(220, 245, 220) : TEKS_SUB);
                g2.drawString(keterangan, 26, getHeight() / 2 + 19);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                g2.drawString(">", getWidth() - 40, getHeight() / 2 + 8);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(520, 78));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
