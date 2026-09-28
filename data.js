const KEY = {
    produk: "hf_produk",
    pesanan: "hf_pesanan",
    katering: "hf_katering",
    konten: "hf_konten"
};

const PRODUK_AWAL = [
    { id: 1, nama: "Vegetable Salad", kalori: "250 - 350 kkal", harga: 22000, gambar: "gambar/salad.jpg", grup: "", best: true },
    { id: 2, nama: "Tempeh Steak", kalori: "280 - 380 kkal", harga: 34000, gambar: "gambar/steak_tempe.webp", grup: "", best: true },
    { id: 3, nama: "Mushroom Steak", kalori: "320 - 420 kkal", harga: 40000, gambar: "gambar/steak_jamur.jpg", grup: "", best: true },
    { id: 4, nama: "Green Spinach Noodles", kalori: "380 - 480 kkal", harga: 28000, gambar: "gambar/mie_bayam.jpg", grup: "", best: true },
    { id: 5, nama: "Vegetable Kebab", kalori: "300 - 400 kkal", harga: 22000, gambar: "gambar/kebab_sayur.jpg", grup: "", best: true },
    { id: 6, nama: "Chicken Soup", kalori: "180 - 260 kkal", harga: 30000, gambar: "gambar/sup_ayam.jpg", grup: "", best: true },
    { id: 7, nama: "Grain Bowl", kalori: "450 - 550 kkal", harga: 27000, gambar: "gambar/grain_bowl.jpg", grup: "", best: false },
    { id: 8, nama: "Fruit Salad", kalori: "120 - 180 kkal", harga: 23000, gambar: "gambar/salad_buah.jpg", grup: "", best: false },
    { id: 9, nama: "Grilled Salmon", kalori: "400 - 500 kkal", harga: 43000, gambar: "gambar/salmon_panggang.jpg", grup: "", best: false },
    { id: 10, nama: "Chicken Caesar Wrap", kalori: "380 - 480 kkal", harga: 27000, gambar: "gambar/kebab_daging.webp", grup: "", best: false },
    { id: 11, nama: "Smoked Salmon & Cream Cheese Bagel", kalori: "350 - 450 kkal", harga: 40000, gambar: "gambar/roti_salmon.jpg", grup: "", best: false },
    { id: 12, nama: "Grilled Chicken Pesto Pasta", kalori: "480 - 580 kkal", harga: 30000, gambar: "gambar/pasta_ayam.jpg", grup: "", best: false },
    { id: 13, nama: "Avocado Toast", kalori: "220 - 300 kkal", harga: 22000, gambar: "gambar/sarapan_sehat.jpg", grup: "sarapan", best: false },
    { id: 14, nama: "Oatmeal Fruit Bowl", kalori: "280 - 350 kkal", harga: 26000, gambar: "gambar/sarapan_sehat.jpg", grup: "sarapan", best: false },
    { id: 15, nama: "Egg & Toast Combo", kalori: "320 - 400 kkal", harga: 28000, gambar: "gambar/sarapan_sehat.jpg", grup: "sarapan", best: false }
];

const KATERING_AWAL = [
    { id: 1, nama: "Paket Kantor", deskripsi: "Katering harian untuk kebutuhan kantor, minimal 10 porsi.", harga: 25000 },
    { id: 2, nama: "Paket Acara", deskripsi: "Katering untuk acara, ulang tahun, dan gathering dengan menu yang bisa disesuaikan.", harga: 35000 }
];

const KONTEN_AWAL = {
    tulisanPembuka: "Welcome To",
    slogan: "Start your journey toward a healthy lifestyle by consuming foods\nrich in fiber and vitamins for a brilliant future"
};

function baca(key, awal) {
    try {
        const raw = localStorage.getItem(key);
        return raw ? JSON.parse(raw) : awal;
    } catch (e) {
        return awal;
    }
}

function tulis(key, data) {
    localStorage.setItem(key, JSON.stringify(data));
}

function crud(key, awal) {
    return {
        semua() {
            return baca(key, awal);
        },
        tambah(data) {
            const list = this.semua();
            data.id = list.reduce((m, x) => Math.max(m, x.id), 0) + 1;
            list.push(data);
            tulis(key, list);
            return data;
        },
        ubah(id, data) {
            tulis(key, this.semua().map(x => x.id === id ? { ...x, ...data, id } : x));
        },
        hapus(id) {
            tulis(key, this.semua().filter(x => x.id !== id));
        }
    };
}

const Produk = crud(KEY.produk, PRODUK_AWAL);
const Katering = crud(KEY.katering, KATERING_AWAL);
const Pesanan = crud(KEY.pesanan, []);

function buatPesanan(items, total) {
    return Pesanan.tambah({
        tanggal: new Date().toISOString(),
        items: items,
        total: total,
        status: "Baru"
    });
}

function getKonten() {
    return baca(KEY.konten, KONTEN_AWAL);
}

function simpanKonten(konten) {
    tulis(KEY.konten, konten);
}

function esc(teks) {
    return String(teks).replace(/[&<>"']/g, function (c) {
        return { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c];
    });
}

function formatRupiah(angka) {
    return new Intl.NumberFormat("id-ID", {
        style: "currency",
        currency: "IDR",
        minimumFractionDigits: 0
    }).format(angka);
}

function formatTanggal(iso) {
    const t = new Date(iso);
    return t.toLocaleDateString("id-ID", { day: "2-digit", month: "short", year: "numeric" }) +
        " " + t.toLocaleTimeString("id-ID", { hour: "2-digit", minute: "2-digit" });
}
