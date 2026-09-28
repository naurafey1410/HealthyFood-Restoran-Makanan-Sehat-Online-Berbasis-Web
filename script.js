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

btnCheckout.addEventListener("click", function () {
    if (keranjang.length === 0) {
        alert("Keranjang masih kosong. Silakan pilih makanan terlebih dahulu.");
        return;
    }

    let total = 0;
    keranjang.forEach(function (item) {
        total += item.harga * item.jumlah;
    });

    const pesanan = buatPesanan(keranjang, total);

    alert(
        "Pesanan berhasil dibuat! 🌱\n\n" +
        "Nomor pesanan: #" + pesanan.id + "\n" +
        "Total pembayaran: " + formatRupiah(total) + "\n\n" +
        "Terima kasih telah memesan."
    );

    keranjang = [];
    tampilkanKeranjang();
    tutupKeranjang();
});

renderKonten();
renderMenu();