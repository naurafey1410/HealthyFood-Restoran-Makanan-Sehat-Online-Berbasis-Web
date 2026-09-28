let keranjang = [];

const menuContainer = document.getElementById("menu-container");
const btnKeranjang = document.getElementById("btn-keranjang");
const btnTutupKeranjang = document.getElementById("tutup-keranjang");
const keranjangPanel = document.getElementById("keranjang-panel");
const overlay = document.getElementById("overlay");
const isiKeranjang = document.getElementById("isi-keranjang");
const jumlahKeranjang = document.getElementById("jumlah-keranjang");
const totalHarga = document.getElementById("total-harga");
const btnCheckout = document.getElementById("btn-checkout");
const btnKatering = document.getElementById("btn-katering");

function renderKonten() {
    const konten = getKonten();
    document.getElementById("tulisan-pembuka").textContent = konten.tulisanPembuka;
    document.getElementById("slogan-teks").innerHTML = esc(konten.slogan).replace(/\n/g, "<br>");
}

function kartu(p) {
    return `
        <div class="card">
            <img src="${esc(p.gambar)}" alt="${esc(p.nama)}">
            <div class="card-content">
                <h3>${esc(p.nama)}</h3>
                <p>${esc(p.kalori)}</p>
                <span>${formatRupiah(p.harga)}</span>
                <div class="best${p.best ? "" : " kosong"}">Best Seller</div>
                <button class="btn-pesan" data-id="${p.id}">Pesan</button>
            </div>
        </div>`;
}

function kartuGrup(items) {
    return `
        <div class="card card-featured-triple">
            <img src="${esc(items[0].gambar)}" alt="${esc(items[0].grup)}">
            <div class="triple-content-wrapper">
                ${items.map(function (p) {
                    return `
                    <div class="triple-item">
                        <div>
                            <h3>${esc(p.nama)}</h3>
                            <p>${esc(p.kalori)}</p>
                            <span>${formatRupiah(p.harga)}</span>
                        </div>
                        <button class="btn-pesan" data-id="${p.id}">Pesan</button>
                    </div>`;
                }).join("")}
            </div>
        </div>`;
}

function renderMenu() {
    const produk = Produk.semua();
    const sudah = [];

    menuContainer.innerHTML = produk.map(function (p) {
        if (!p.grup) return kartu(p);
        if (sudah.includes(p.grup)) return "";

        sudah.push(p.grup);
        const items = produk.filter(function (x) { return x.grup === p.grup; });
        return items.length > 1 ? kartuGrup(items) : kartu(p);
    }).join("");
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

btnKatering.addEventListener("click", function (event) {
    event.preventDefault();

    const paket = Katering.semua();
    let teks = "Layanan Katering 🌱\n\n";

    if (paket.length === 0) {
        teks += "Belum ada paket katering tersedia saat ini.\n\n";
    } else {
        paket.forEach(function (p) {
            teks += "• " + p.nama + " - " + formatRupiah(p.harga) + "/porsi\n  " + p.deskripsi + "\n\n";
        });
    }

    alert(teks + "Silakan hubungi restoran untuk informasi lebih lanjut.");
});

// ===== CHECKOUT (hanya satu listener) =====
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

    // Klik kedua: validasi lalu konfirmasi
    const nama = document.getElementById("input-nama").value.trim();
    const telp = document.getElementById("input-telp").value.trim();
    const alamat = document.getElementById("input-alamat").value.trim();

    if (nama === "" || telp === "" || alamat === "") {
        alert("Mohon lengkapi Nama, Nomor Telepon, dan Alamat pengiriman terlebih dahulu! 🌱");
        return;
    }

    let total = 0;
    keranjang.forEach(function (item) {
        total += item.harga * item.jumlah;
    });

    const pesanan = buatPesanan(keranjang, total);

    // Desain pesanan: daftar menu yang dipesan
    const daftarItem = keranjang.map(function (item) {
        return `
            <div class="ringkasan-item">
                <span>${esc(item.nama)} <small>x${item.jumlah}</small></span>
                <strong>${formatRupiah(item.harga * item.jumlah)}</strong>
            </div>`;
    }).join("");

    document.getElementById("info-detail-pesanan").innerHTML = `
        <div class="ringkasan-pesanan">
            <div class="ringkasan-nomor">Pesanan #${esc(String(pesanan.id))}</div>

            <div class="ringkasan-data">
                <p><b>Nama</b> ${esc(nama)}</p>
                <p><b>Telepon</b> ${esc(telp)}</p>
                <p><b>Alamat</b> ${esc(alamat)}</p>
            </div>

            <div class="ringkasan-daftar">${daftarItem}</div>

            <div class="ringkasan-total">
                <span>Total</span>
                <strong>${formatRupiah(total)}</strong>
            </div>
        </div>`;

    bukaOrderDiterima();

    // Reset keranjang & form
    keranjang = [];
    formPengiriman.classList.remove("aktif");
    btnCheckout.textContent = "Pesan Sekarang";
    document.getElementById("input-nama").value = "";
    document.getElementById("input-telp").value = "";
    document.getElementById("input-alamat").value = "";

    tampilkanKeranjang();
    tutupKeranjang();
});

// ===== POP UP ORDER DITERIMA =====
const orderDiterimaEl = document.getElementById("order-diterima");

function bukaOrderDiterima() {
    orderDiterimaEl.classList.add("aktif");
}

function tutupOrderDiterima() {
    orderDiterimaEl.classList.remove("aktif");
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

renderKonten();
renderMenu();