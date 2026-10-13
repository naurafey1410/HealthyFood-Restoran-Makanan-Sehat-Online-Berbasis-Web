const KEY_AKTIF = "hf_pesanan_aktif";

const KEY_KERANJANG = KEY.keranjang;

// Baca keranjang tersimpan, lalu cocokkan dengan menu terbaru (produk yang sudah dihapus admin dibuang)
function muatKeranjang() {
    let tersimpan = [];
    try {
        const data = JSON.parse(localStorage.getItem(KEY_KERANJANG));
        if (Array.isArray(data)) tersimpan = data;
    } catch (e) {
        tersimpan = [];
    }

    const produk = Produk.semua();
    const hasil = [];

    tersimpan.forEach(function (item) {
        const p = item && produk.find(function (x) { return x.id === item.id; });
        const jumlah = Math.floor(Number(item && item.jumlah));
        if (!p || !(jumlah > 0)) return;
        hasil.push({ id: p.id, nama: p.nama, harga: p.harga, jumlah: jumlah });
    });

    return hasil;
}

function simpanKeranjang() {
    try {
        if (keranjang.length === 0) {
            localStorage.removeItem(KEY_KERANJANG);
        } else {
            localStorage.setItem(KEY_KERANJANG, JSON.stringify(keranjang));
        }
    } catch (e) {
        // penyimpanan penuh/diblokir: keranjang tetap jalan, hanya tidak tersimpan
    }
}

let keranjang = muatKeranjang();
let pesananAktifId = Number(sessionStorage.getItem(KEY_AKTIF)) || null;

const menuContainer = document.getElementById("menu-container");
const btnKeranjang = document.getElementById("btn-keranjang");
const btnTutupKeranjang = document.getElementById("tutup-keranjang");
const keranjangPanel = document.getElementById("keranjang-panel");
const overlay = document.getElementById("overlay");
const isiKeranjang = document.getElementById("isi-keranjang");
const jumlahKeranjang = document.getElementById("jumlah-keranjang");
const totalHarga = document.getElementById("total-harga");
const btnCheckout = document.getElementById("btn-checkout");

// ===== DETAIL MENU: deskripsi dan perkiraan informasi gizi =====
const detailMenuOverlay = document.getElementById("detail-menu-overlay");
const detailMenuGambar = document.getElementById("detail-menu-gambar");
const detailMenuNama = document.getElementById("detail-menu-nama");
const detailMenuDeskripsi = document.getElementById("detail-menu-deskripsi");
const detailMenuHarga = document.getElementById("detail-menu-harga");
const detailMenuGizi = document.getElementById("detail-menu-gizi");
const btnTutupDetailMenu = document.getElementById("tutup-detail-menu");
const btnPesanDetail = document.getElementById("detail-menu-pesan");
let detailProdukAktifId = null;

// Nilai gizi adalah perkiraan per porsi, bukan hasil uji laboratorium
const DETAIL_MENU = {
    1: { deskripsi: "Salad sayuran segar yang cocok sebagai pilihan menu ringan dan kaya serat.", protein: "5–8 g", karbo: "15–25 g", lemak: "8–15 g", serat: "4–7 g" },
    2: { deskripsi: "Steak berbahan dasar tempe yang menawarkan protein nabati dengan cita rasa gurih.", protein: "15–22 g", karbo: "20–35 g", lemak: "10–18 g", serat: "5–8 g" },
    3: { deskripsi: "Olahan jamur dengan tekstur lembut dan rasa gurih sebagai alternatif menu steak.", protein: "8–14 g", karbo: "25–40 g", lemak: "12–20 g", serat: "4–7 g" },
    4: { deskripsi: "Mie dengan bayam sebagai variasi menu yang memadukan karbohidrat dan sayuran.", protein: "8–14 g", karbo: "50–70 g", lemak: "6–14 g", serat: "4–8 g" },
    5: { deskripsi: "Kebab sayuran yang praktis dengan kombinasi sayuran dan kulit kebab.", protein: "7–12 g", karbo: "35–50 g", lemak: "8–15 g", serat: "5–8 g" },
    6: { deskripsi: "Sup ayam hangat dengan kuah dan bahan pelengkap yang cocok untuk hidangan ringan.", protein: "15–25 g", karbo: "10–25 g", lemak: "5–12 g", serat: "2–5 g" },
    7: { deskripsi: "Grain bowl berisi sumber karbohidrat dan pelengkap yang dapat memberi energi untuk aktivitas.", protein: "10–18 g", karbo: "55–75 g", lemak: "10–18 g", serat: "6–10 g" },
    8: { deskripsi: "Salad buah dengan aneka potongan buah sebagai pilihan segar dengan rasa manis alami.", protein: "2–5 g", karbo: "25–40 g", lemak: "1–8 g", serat: "3–6 g" },
    9: { deskripsi: "Salmon panggang sebagai sumber protein dan lemak omega-3, disajikan sebagai menu utama.", protein: "25–35 g", karbo: "5–20 g", lemak: "15–25 g", serat: "1–4 g" },
    10: { deskripsi: "Wrap berisi ayam dan sayuran yang praktis untuk dinikmati sebagai menu makan.", protein: "20–30 g", karbo: "35–50 g", lemak: "10–18 g", serat: "4–7 g" },
    11: { deskripsi: "Bagel dengan salmon dan cream cheese yang memadukan protein, karbohidrat, dan tekstur lembut.", protein: "18–28 g", karbo: "35–50 g", lemak: "12–22 g", serat: "2–5 g" },
    12: { deskripsi: "Pasta dengan ayam dan pesto untuk pilihan hidangan mengenyangkan dengan rasa herba.", protein: "20–30 g", karbo: "55–75 g", lemak: "12–22 g", serat: "3–7 g" },
    13: { deskripsi: "Roti panggang dengan alpukat, cocok sebagai pilihan sarapan dengan lemak tak jenuh.", protein: "5–9 g", karbo: "25–40 g", lemak: "10–18 g", serat: "6–10 g" },
    14: { deskripsi: "Oatmeal dengan buah sebagai menu sarapan yang mengandung karbohidrat dan serat.", protein: "7–12 g", karbo: "40–55 g", lemak: "5–12 g", serat: "6–10 g" },
    15: { deskripsi: "Kombinasi telur dan roti panggang untuk sarapan praktis dengan sumber protein.", protein: "12–20 g", karbo: "30–45 g", lemak: "10–18 g", serat: "3–6 g" }
};

function bukaDetailMenu(id) {
    const produk = Produk.semua().find(function (p) { return p.id === id; });
    if (!produk) return;
    const detail = DETAIL_MENU[id] || {
        deskripsi: "Menu makanan dari Healthy Food. Hubungi pengelola untuk mengetahui bahan dan informasi gizi lebih lengkap.",
        protein: "Bervariasi", karbo: "Bervariasi", lemak: "Bervariasi", serat: "Bervariasi"
    };

    detailProdukAktifId = produk.id;
    detailMenuGambar.src = produk.gambar;
    detailMenuGambar.alt = produk.nama;
    detailMenuNama.textContent = produk.nama;
    detailMenuDeskripsi.textContent = detail.deskripsi;
    detailMenuHarga.textContent = formatRupiah(produk.harga);
    detailMenuGizi.innerHTML = `
        <div class="gizi-item"><span>Kalori</span><strong>${esc(produk.kalori || "Tidak tersedia")}</strong></div>
        <div class="gizi-item"><span>Protein</span><strong>${esc(detail.protein)}</strong></div>
        <div class="gizi-item"><span>Karbohidrat</span><strong>${esc(detail.karbo)}</strong></div>
        <div class="gizi-item"><span>Lemak</span><strong>${esc(detail.lemak)}</strong></div>
        <div class="gizi-item"><span>Serat</span><strong>${esc(detail.serat)}</strong></div>`;
    detailMenuOverlay.classList.add("aktif");
    detailMenuOverlay.setAttribute("aria-hidden", "false");
}

function tutupDetailMenu() {
    detailMenuOverlay.classList.remove("aktif");
    detailMenuOverlay.setAttribute("aria-hidden", "true");
    detailProdukAktifId = null;
}

menuContainer.addEventListener("click", function (event) {
    const gambar = event.target.closest(".gambar-detail-menu");
    if (gambar) {
        bukaDetailMenu(Number(gambar.getAttribute("data-product-id")));
        return;
    }
});

menuContainer.addEventListener("keydown", function (event) {
    const gambar = event.target.closest(".gambar-detail-menu");
    if (gambar && (event.key === "Enter" || event.key === " ")) {
        event.preventDefault();
        bukaDetailMenu(Number(gambar.getAttribute("data-product-id")));
    }
});

btnTutupDetailMenu.addEventListener("click", tutupDetailMenu);
detailMenuOverlay.addEventListener("click", function (event) {
    if (event.target === detailMenuOverlay) tutupDetailMenu();
});
btnPesanDetail.addEventListener("click", function () {
    if (detailProdukAktifId === null) return;
    const tombolMenu = menuContainer.querySelector('.btn-pesan[data-id="' + detailProdukAktifId + '"]');
    tutupDetailMenu();
    if (tombolMenu) tombolMenu.click();
});
document.addEventListener("keydown", function (event) {
    if (event.key === "Escape") tutupDetailMenu();
});

function renderKonten() {
    const konten = getKonten();
    document.getElementById("tulisan-pembuka").textContent = konten.tulisanPembuka;
    document.getElementById("slogan-teks").innerHTML = esc(konten.slogan).replace(/\n/g, "<br>");
}

function kartu(p) {
    return `
        <div class="card">
            <img src="${esc(p.gambar)}" alt="${esc(p.nama)}" class="gambar-detail-menu" data-product-id="${p.id}" role="button" tabindex="0" title="Klik untuk melihat detail menu">
            <div class="card-content">
                <h3>${esc(p.nama)}</h3>
                <span>${formatRupiah(p.harga)}</span>
                <p>${esc(p.kalori)}</p>
                <div class="best${p.best ? "" : " kosong"}">Best Seller</div>
                <button class="btn-pesan" data-id="${p.id}">Order</button>
            </div>
        </div>`;
}

function kartuGrup(items) {
    return `
        <div class="card card-featured-triple">
            <img src="${esc(items[0].gambar)}" alt="${esc(items[0].grup)}" class="gambar-detail-menu" data-product-id="${items[0].id}" role="button" tabindex="0" title="Klik untuk melihat detail menu">
            <div class="triple-content-wrapper">
                ${items.map(function (p) {
                    return `
                    <div class="triple-item">
                        <div>
                            <h3>${esc(p.nama)}</h3>
                            <span>${formatRupiah(p.harga)}</span>
                            <p>${esc(p.kalori)}</p>
                        </div>
                        <button class="btn-pesan" data-id="${p.id}">Pesan</button>
                    </div>`;
                }).join("")}
            </div>
        </div>`;
}

// Best seller tampil paling atas, non-best seller di bawahnya (urutan di tiap kelompok tetap)
function urutkanBestSeller(list) {
    return list.filter(function (x) { return x.best; })
        .concat(list.filter(function (x) { return !x.best; }));
}

function renderMenu() {
    const produk = Produk.semua();
    const sudah = [];
    const daftarKartu = [];

    produk.forEach(function (p) {
        if (!p.grup) {
            daftarKartu.push({ best: !!p.best, html: kartu(p) });
            return;
        }
        if (sudah.includes(p.grup)) return;

        sudah.push(p.grup);
        const items = produk.filter(function (x) { return x.grup === p.grup; });

        // Kartu grup dianggap best seller jika salah satu anggotanya best seller
        daftarKartu.push(items.length > 1
            ? { best: items.some(function (x) { return x.best; }), html: kartuGrup(items) }
            : { best: !!p.best, html: kartu(p) });
    });

    menuContainer.innerHTML = urutkanBestSeller(daftarKartu).map(function (k) { return k.html; }).join("");
}

menuContainer.addEventListener("click", function (event) {
    const tombol = event.target.closest(".btn-pesan");
    if (!tombol) return;

    const id = Number(tombol.getAttribute("data-id"));
    const produk = Produk.semua().find(function (p) { return p.id === id; });
    if (!produk) return;

    const ada = keranjang.find(function (item) { return item.id === id; });

    if (ada) {
        ada.jumlah++;
    } else {
        keranjang.push({ id: produk.id, nama: produk.nama, harga: produk.harga, jumlah: 1 });
    }

    tampilkanKeranjang();
    bukaKeranjang();
});

function tampilkanKeranjang() {
    simpanKeranjang();

    if (keranjang.length === 0) {
        isiKeranjang.innerHTML = `<p class="keranjang-kosong">Keranjang masih kosong 🌱</p>`;
        jumlahKeranjang.textContent = "0";
        totalHarga.textContent = "Rp 0";
        return;
    }

    let total = 0;
    let jumlahBarang = 0;

    isiKeranjang.innerHTML = keranjang.map(function (item, index) {
        total += item.harga * item.jumlah;
        jumlahBarang += item.jumlah;

        return `
            <div class="item-keranjang">
                <div class="item-info">
                    <h4>${esc(item.nama)}</h4>
                    <p>${formatRupiah(item.harga)}</p>
                </div>
                <div class="kontrol-jumlah">
                    <button class="btn-jumlah" onclick="kurangiJumlah(${index})">−</button>
                    <span>${item.jumlah}</span>
                    <button class="btn-jumlah" onclick="tambahJumlah(${index})">+</button>
                    <button class="btn-hapus" onclick="hapusItem(${index})">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                </div>
            </div>`;
    }).join("");

    jumlahKeranjang.textContent = jumlahBarang;
    totalHarga.textContent = formatRupiah(total);
}

function tambahJumlah(index) {
    keranjang[index].jumlah++;
    tampilkanKeranjang();
}

function kurangiJumlah(index) {
    keranjang[index].jumlah--;
    if (keranjang[index].jumlah <= 0) keranjang.splice(index, 1);
    tampilkanKeranjang();
}

function hapusItem(index) {
    keranjang.splice(index, 1);
    tampilkanKeranjang();
}

function bukaKeranjang() {
    keranjangPanel.classList.add("aktif");
    overlay.classList.add("aktif");
}

function tutupKeranjang() {
    keranjangPanel.classList.remove("aktif");
    overlay.classList.remove("aktif");
}

btnKeranjang.addEventListener("click", function (event) {
    event.preventDefault();
    bukaKeranjang();
});

btnTutupKeranjang.addEventListener("click", tutupKeranjang);
overlay.addEventListener("click", tutupKeranjang);

// ===== CHECKOUT =====
btnCheckout.addEventListener("click", function () {
    if (keranjang.length === 0) {
        alert("Keranjang masih kosong. Silakan pilih makanan terlebih dahulu.");
        return;
    }

    const formPengiriman = document.getElementById("form-pengiriman");

    // Klik pertama: munculkan form
    if (!formPengiriman.classList.contains("aktif")) {
        formPengiriman.classList.add("aktif");
        btnCheckout.textContent = "Konfirmasi Pesanan";
        return;
    }

    // Klik kedua: validasi lalu simpan pesanan
    const nama = document.getElementById("input-nama").value.trim();
    const telp = document.getElementById("input-telp").value.trim();
    const alamat = document.getElementById("input-alamat").value.trim();
    const pilihan = document.querySelector('input[name="metode"]:checked');

    if (nama === "" || telp === "" || alamat === "") {
        alert("Mohon lengkapi Nama, Nomor Telepon, dan Alamat pengiriman terlebih dahulu! 🌱");
        return;
    }

    if (!/^\+?[0-9]{8,15}$/.test(telp.replace(/[\s-]/g, ""))) {
        alert("Nomor telepon tidak valid. Contoh: 08123456789");
        return;
    }

    if (!pilihan) {
        alert("Silakan pilih metode pembayaran (Transfer Bank atau QRIS).");
        return;
    }

    const total = keranjang.reduce(function (sum, item) {
        return sum + item.harga * item.jumlah;
    }, 0);

    const pesanan = buatPesanan(keranjang, total, { nama: nama, telp: telp, alamat: alamat }, pilihan.value);
    setPesananAktif(pesanan.id);

    // Reset keranjang & form
    keranjang = [];
    formPengiriman.classList.remove("aktif");
    btnCheckout.textContent = "Pesan Sekarang";
    document.getElementById("input-nama").value = "";
    document.getElementById("input-telp").value = "";
    document.getElementById("input-alamat").value = "";
    pilihan.checked = false;

    tampilkanKeranjang();
    tutupKeranjang();
    bukaOrderDiterima();
});

// ===== PESANAN AKTIF & STATUS PEMBAYARAN =====
const orderDiterimaEl = document.getElementById("order-diterima");
const detailPesananEl = document.getElementById("info-detail-pesanan");
const navStatusEl = document.getElementById("nav-status-pesanan");
const linkStatusEl = document.getElementById("btn-status-pesanan");
const teksStatusEl = document.getElementById("teks-status-pesanan");

const LANGKAH_BAYAR = ["Pembayaran", "Verifikasi Admin", "Lunas"];

function setPesananAktif(id) {
    pesananAktifId = id;
    if (id === null) {
        sessionStorage.removeItem(KEY_AKTIF);
    } else {
        sessionStorage.setItem(KEY_AKTIF, String(id));
    }
}

function ambilPesananAktif() {
    return pesananAktifId === null ? null : cariPesanan(pesananAktifId);
}

function langkahAktif(status) {
    if (status === STATUS_BAYAR.LUNAS) return 2;
    if (status === STATUS_BAYAR.MENUNGGU) return 1;
    return 0;
}

function htmlLangkah(status) {
    const aktif = langkahAktif(status);
    const lunas = status === STATUS_BAYAR.LUNAS;

    return `<ol class="langkah-bayar">${LANGKAH_BAYAR.map(function (nama, i) {
        const kelas = i < aktif || (lunas && i === aktif) ? "selesai" : i === aktif ? "aktif" : "";
        return `<li class="${kelas}"><span>${i + 1}</span>${nama}</li>`;
    }).join("")}</ol>`;
}

function htmlInstruksiBayar(pesanan) {
    const total = formatRupiah(pesanan.total);

    if (pesanan.metode === "QRIS") {
        return `
            <div class="kotak-bayar">
                <p class="bayar-judul">Scan QRIS untuk membayar</p>
                <img src="${esc(INFO_PEMBAYARAN.QRIS.gambar)}" class="gambar-qris" alt="QRIS">
                <p class="bayar-total">Total: <strong>${total}</strong></p>
            </div>`;
    }

    const rek = INFO_PEMBAYARAN.Transfer;
    return `
        <div class="kotak-bayar">
            <p class="bayar-judul">Transfer ke rekening berikut</p>
            <p class="bayar-bank">${esc(rek.bank)}</p>
            <p class="bayar-rek">${esc(rek.rekening)}</p>
            <p class="bayar-an">a.n. ${esc(rek.atasNama)}</p>
            <p class="bayar-total">Total: <strong>${total}</strong></p>
        </div>`;
}

function htmlBagianBayar(pesanan) {
    if (pesanan.statusBayar === STATUS_BAYAR.LUNAS) {
        return `<p class="info-bayar sukses">Pembayaran Anda sudah dikonfirmasi. Pesanan sedang diproses. 🌱</p>`;
    }

    if (pesanan.statusBayar === STATUS_BAYAR.MENUNGGU) {
        return `<p class="info-bayar">Pembayaran sedang diverifikasi admin. Status akan berubah otomatis di halaman ini.</p>`;
    }

    return `
        ${htmlInstruksiBayar(pesanan)}
        <button type="button" class="btn-checkout" id="btn-sudah-bayar">Saya Sudah Bayar</button>`;
}

function htmlRingkasan(pesanan) {
    const daftarItem = pesanan.items.map(function (item) {
        return `
            <div class="ringkasan-item">
                <span>${esc(item.nama)} <small>x${item.jumlah}</small></span>
                <strong>${formatRupiah(item.harga * item.jumlah)}</strong>
            </div>`;
    }).join("");

    return `
        <div class="ringkasan-pesanan">
            <div class="ringkasan-nomor">Pesanan #${esc(String(pesanan.id))}</div>

            <div class="ringkasan-data">
                <p><b>Nama</b> ${esc(pesanan.pembeli.nama)}</p>
                <p><b>Telepon</b> ${esc(pesanan.pembeli.telp)}</p>
                <p><b>Alamat</b> ${esc(pesanan.pembeli.alamat)}</p>
                <p><b>Metode</b> ${esc(pesanan.metode)}</p>
            </div>

            <div class="ringkasan-daftar">${daftarItem}</div>

            <div class="ringkasan-total">
                <span>Total</span>
                <strong>${formatRupiah(pesanan.total)}</strong>
            </div>
        </div>`;
}

function tampilkanPesanan(pesanan) {
    const lunas = pesanan.statusBayar === STATUS_BAYAR.LUNAS;

    document.getElementById("order-ikon").textContent = lunas ? "✅" : "🌱";
    document.getElementById("order-judul").textContent = lunas ? "Pembayaran Lunas!" : "Order Diterima!";
    document.getElementById("order-sub").textContent = lunas
        ? "Terima kasih, pembayaran Anda telah kami terima."
        : "Selesaikan pembayaran agar pesanan dapat diproses.";

    detailPesananEl.innerHTML = htmlLangkah(pesanan.statusBayar) + htmlRingkasan(pesanan) + htmlBagianBayar(pesanan);
}

// Sinkronkan indikator navbar dan popup dengan data terbaru di localStorage
function segarkanStatusPesanan() {
    const pesanan = ambilPesananAktif();

    if (!pesanan) {
        setPesananAktif(null);
        navStatusEl.hidden = true;
        orderDiterimaEl.classList.remove("aktif");
        return;
    }

    navStatusEl.hidden = false;
    teksStatusEl.textContent = "#" + pesanan.id + " · " + labelBayarSingkat(pesanan.statusBayar);
    linkStatusEl.title = "Pesanan #" + pesanan.id + " - " + pesanan.statusBayar;
    linkStatusEl.className = "status-pesanan " + kelasBayar(pesanan.statusBayar);

    if (orderDiterimaEl.classList.contains("aktif")) tampilkanPesanan(pesanan);
}

// ===== POP UP ORDER DITERIMA =====
function bukaOrderDiterima() {
    const pesanan = ambilPesananAktif();
    if (!pesanan) return;

    tampilkanPesanan(pesanan);
    orderDiterimaEl.classList.add("aktif");
    segarkanStatusPesanan();
}

function tutupOrderDiterima() {
    if (!orderDiterimaEl.classList.contains("aktif")) return;
    orderDiterimaEl.classList.remove("aktif");

    // Pesanan lunas yang sudah dilihat customer tidak perlu dipantau lagi
    const pesanan = ambilPesananAktif();
    if (pesanan && pesanan.statusBayar === STATUS_BAYAR.LUNAS) setPesananAktif(null);
    segarkanStatusPesanan();
}

document.getElementById("tutup-order-diterima").addEventListener("click", tutupOrderDiterima);
document.getElementById("btn-tutup-ok").addEventListener("click", tutupOrderDiterima);

// Klik area gelap di luar card
orderDiterimaEl.addEventListener("click", function (event) {
    if (event.target === orderDiterimaEl) tutupOrderDiterima();
});

// Tombol Esc
document.addEventListener("keydown", function (event) {
    if (event.key === "Escape") tutupOrderDiterima();
});

// Tombol "Saya Sudah Bayar" (dibuat dinamis, jadi memakai event delegation)
detailPesananEl.addEventListener("click", function (event) {
    if (!event.target.closest("#btn-sudah-bayar") || pesananAktifId === null) return;

    ubahStatusBayar(pesananAktifId, STATUS_BAYAR.MENUNGGU);
    segarkanStatusPesanan();
});

// Klik indikator di navbar untuk membuka kembali popup
linkStatusEl.addEventListener("click", function (event) {
    event.preventDefault();
    bukaOrderDiterima();
});

// Admin mengubah status di tab lain, tampilan customer ikut berubah
pantauData(function () {
    keranjang = muatKeranjang();
    tampilkanKeranjang();
    segarkanStatusPesanan();
    renderKonten();
    renderMenu();
});

// ===== INISIALISASI =====
renderKonten();
renderMenu();
tampilkanKeranjang();
segarkanStatusPesanan();
