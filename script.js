let keranjang = [];

const tombolPesan = document.querySelectorAll(".btn-pesan");
const btnKeranjang = document.getElementById("btn-keranjang");
const btnTutupKeranjang =
    document.getElementById("tutup-keranjang");

const keranjangPanel =
    document.getElementById("keranjang-panel");

const overlay =
    document.getElementById("overlay");

const isiKeranjang =
    document.getElementById("isi-keranjang");

const jumlahKeranjang =
    document.getElementById("jumlah-keranjang");

const totalHarga =
    document.getElementById("total-harga");

const btnCheckout =
    document.getElementById("btn-checkout");

const btnKatering =
    document.getElementById("btn-katering");

tombolPesan.forEach(function(tombol) {
    tombol.addEventListener("click", function() {

        const nama =
            tombol.getAttribute("data-nama");

        const harga =
            Number(tombol.getAttribute("data-harga"));

        const makananAda =
            keranjang.find(function(item) {
                return item.nama === nama;
            });

        if (makananAda) {
            makananAda.jumlah++;
        } else {
            keranjang.push({
                nama: nama,
                harga: harga,
                jumlah: 1
            });
        }

        tampilkanKeranjang();
        bukaKeranjang();

    });
});

function tampilkanKeranjang() {
    isiKeranjang.innerHTML = "";

    if (keranjang.length === 0) {
        isiKeranjang.innerHTML = `
            <p class="keranjang-kosong">
                Keranjang masih kosong 🌱
            </p>
        `;

        jumlahKeranjang.textContent = "0";
        totalHarga.textContent = "Rp 0";
        return;
    }

    let total = 0;
    let jumlahBarang = 0;

    keranjang.forEach(function(item, index) {

        const subtotal =
            item.harga * item.jumlah;
        total += subtotal;
        jumlahBarang += item.jumlah;
        isiKeranjang.innerHTML += `

            <div class="item-keranjang">
                <div class="item-info">
                    <h4>
                        ${item.nama}
                    </h4>
                    <p>
                        ${formatRupiah(item.harga)}
                    </p>
                </div>

                <div class="kontrol-jumlah">
                    <button
                        class="btn-jumlah"
                        onclick="kurangiJumlah(${index})">
                        −
                    </button>
                    <span>
                        ${item.jumlah}
                    </span>
                    <button
                        class="btn-jumlah"
                        onclick="tambahJumlah(${index})">
                        +
                    </button>
                    <button
                        class="btn-hapus"
                        onclick="hapusItem(${index})">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                </div>
            </div>
        `;
    });

    jumlahKeranjang.textContent =
        jumlahBarang;
    totalHarga.textContent =
        formatRupiah(total);

}

function tambahJumlah(index) {
    keranjang[index].jumlah++;
    tampilkanKeranjang();

}

function kurangiJumlah(index) {
    keranjang[index].jumlah--;

    if (keranjang[index].jumlah <= 0) {
        keranjang.splice(index, 1);
    }

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

btnKeranjang.addEventListener("click", function(event) {
    event.preventDefault();
    bukaKeranjang();
});

btnTutupKeranjang.addEventListener("click", function() {
    tutupKeranjang();
});

overlay.addEventListener("click", function() {
    tutupKeranjang();
});

btnKatering.addEventListener("click", function(event) {
    event.preventDefault();
    alert(
        "Layanan Katering 🌱\n\n" +
        "Kami menyediakan katering makanan sehat " +
        "untuk acara, kantor, dan berbagai kebutuhan lainnya.\n\n" +
        "Silakan hubungi restoran untuk informasi lebih lanjut."
    );
});

btnCheckout.addEventListener("click", function() {
    if (keranjang.length === 0) {
        alert(
            "Keranjang masih kosong. " +
            "Silakan pilih makanan terlebih dahulu."
        );
        return;
    }

    let total = 0;
    keranjang.forEach(function(item) {
        total += item.harga * item.jumlah;
    });
    alert(
        "Pesanan berhasil dibuat! 🌱\n\n" +
        "Total pembayaran: " +
        formatRupiah(total) +
        "\n\n" +
        "Terima kasih telah memesan."
    );

    keranjang = [];
    tampilkanKeranjang();
    tutupKeranjang();

});

function formatRupiah(angka) {
    return new Intl.NumberFormat(
        "id-ID",
        {
            style: "currency",
            currency: "IDR",
            minimumFractionDigits: 0
        }
    ).format(angka);
}
