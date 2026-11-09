package healthyfood.latihan;

interface Otentikasi {
    boolean verifikasi(String inputKredensial);
}

abstract class SistemKeamanan {
    private String idPerangkat;
    private boolean statusAktif;

    public SistemKeamanan(String idPerangkat) {
        this.idPerangkat = idPerangkat;
        this.statusAktif = true;
    }

    public String getIdPerangkat() {
        return idPerangkat;
    }

    public boolean isStatusAktif() {
        return statusAktif;
    }

    public abstract void prosesAkses(String kredensial);
}

class BrankasKeuangan extends SistemKeamanan implements Otentikasi {
    private String passwordBrankas;
    private double saldoLokal;

    public BrankasKeuangan(String idPerangkat, String passwordAwal, double saldoAwal) {
        super(idPerangkat);
        this.passwordBrankas = passwordAwal;
        this.saldoLokal = saldoAwal;
    }

    @Override
    public boolean verifikasi(String inputKredensial) {
        return this.passwordBrankas.equals(inputKredensial);
    }

    @Override
    public void prosesAkses(String kredensial) {
        System.out.println("\n[BRANKAS " + getIdPerangkat() + "] Memproses otentikasi");
        if (verifikasi(kredensial)) {
            System.out.println("Akses Diterima! Password benar.");
            System.out.println("Isi Saldo Brankas: Rp " + String.format("%,.2f", saldoLokal));
        } else {
            System.out.println("Akses Ditolak! Password brankas salah.");
        }
    }

    public void ubahPassword(String passwordLama, String passwordBaru) {
        if (verifikasi(passwordLama)) {
            this.passwordBrankas = passwordBaru;
            System.out.println("Password brankas berhasil diperbarui.");
        } else {
            System.out.println("Password lama tidak valid.");
        }
    }
}

class PintuFisik extends SistemKeamanan implements Otentikasi {
    private String pinAkses;

    public PintuFisik(String idPerangkat, String pinAkses) {
        super(idPerangkat);
        this.pinAkses = pinAkses;
    }

    @Override
    public boolean verifikasi(String inputKredensial) {
        return this.pinAkses.equals(inputKredensial);
    }

    @Override
    public void prosesAkses(String kredensial) {
        System.out.println("\n[PINTU LAB " + getIdPerangkat() + "] Memindai PIN");
        if (verifikasi(kredensial)) {
            System.out.println("PIN Valid!");
        } else {
            System.out.println("PIN Salah!");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        BrankasKeuangan brankasFakultas = new BrankasKeuangan("BRK-01", "Rahasia123", 50000000.0);
        
        PintuFisik pintuLab = new PintuFisik("LAB-201", "4321");

        SistemKeamanan[] daftarPerangkat = new SistemKeamanan[2];
        daftarPerangkat[0] = brankasFakultas;
        daftarPerangkat[1] = pintuLab;

        System.out.println("SISTEM KEAMANAN DAN AKSES");
        System.out.println("=========================");
        System.out.println("\n--- UJI PERILAKU POLIMORFISME ---");
        brankasFakultas.prosesAkses("Rahasia123"); 
        pintuLab.prosesAkses("4321");             

        System.out.println("\n--- UJI AKSES KREDENSIAL SALAH ---");
        brankasFakultas.prosesAkses("SalahPass");
        pintuLab.prosesAkses("0000");

        System.out.println("\n--- UJI PERUBAHAN PASSWORD BRANKAS (ENKAPSULASI) ---");
        brankasFakultas.ubahPassword("Rahasia123", "Baru2026");
        brankasFakultas.prosesAkses("Baru2026");
    }
}