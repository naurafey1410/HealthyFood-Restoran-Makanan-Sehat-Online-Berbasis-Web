const tabel = document.getElementById("tabel-pesanan");

// ===== TABEL PESANAN =====
function render() {
    const list = Pesanan.semua().slice().reverse();

    tabel.innerHTML = list.length
        ? list.map(function (p) {
            return `
                <tr>
                    <td>#${p.id}</td>
                    <td>${formatTanggal(p.tanggal)}</td>
                    <td>${esc(p.pembeli.nama)}<br><small class="teks-kecil">${esc(p.pembeli.telp)}</small></td>
                    <td>${jumlahPorsi(p.items)}</td>
                    <td>${formatRupiah(p.total)}</td>
                    <td>${esc(p.metode)}</td>
                    <td>${badgeBayar(p.statusBayar)}</td>
                    <td>${badgeStatus(p.status)}</td>
                    <td class="kolom-aksi">
                        <button class="btn-aksi" data-detail="${p.id}" title="Detail">
                            <i class="fa-solid fa-eye"></i>
                        </button>
                        ${p.statusBayar !== STATUS_BAYAR.LUNAS ? `
                        <button class="btn-aksi lunas" data-lunas="${p.id}" title="Konfirmasi pembayaran lunas">
                            <i class="fa-solid fa-money-bill-wave"></i>
                        </button>` : ""}
                        ${p.status !== "Selesai" ? `
                        <button class="btn-aksi" data-selesai="${p.id}" title="Tandai pesanan selesai">
                            <i class="fa-solid fa-check"></i>
                        </button>` : ""}
                        <button class="btn-aksi hapus" data-hapus="${p.id}" title="Hapus">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </td>
                </tr>`;
        }).join("")
        : `<tr><td colspan="9">Belum ada pesanan masuk dari customer.</td></tr>`;
}

// ===== DETAIL PESANAN =====
function tampilkanDetail(id) {
    const p = cariPesanan(id);
    if (!p) return;

    document.getElementById("judul-detail").textContent = "Pesanan #" + p.id;
    document.getElementById("isi-detail").innerHTML = `
        <p class="detail-tanggal">${formatTanggal(p.tanggal)}</p>

        <div class="detail-pembeli">
            <p><b>Nama</b> ${esc(p.pembeli.nama)}</p>
            <p><b>Telepon</b> ${esc(p.pembeli.telp)}</p>
            <p><b>Alamat</b> ${esc(p.pembeli.alamat)}</p>
            <p><b>Metode</b> ${esc(p.metode)}</p>
            <p><b>Pembayaran</b> ${badgeBayar(p.statusBayar)}</p>
        </div>

        <ul class="detail-list">
            ${p.items.map(function (i) {
                return `<li><span>${esc(i.nama)} x${i.jumlah}</span><span>${formatRupiah(i.harga * i.jumlah)}</span></li>`;
            }).join("")}
        </ul>
        <div class="detail-total"><span>Total</span><span>${formatRupiah(p.total)}</span></div>`;
    bukaModal("modal-detail");
}

// ===== AKSI TABEL =====
tabel.addEventListener("click", function (event) {
    const detail = event.target.closest("[data-detail]");
    const lunas = event.target.closest("[data-lunas]");
    const selesai = event.target.closest("[data-selesai]");
    const hapus = event.target.closest("[data-hapus]");

    if (detail) tampilkanDetail(Number(detail.getAttribute("data-detail")));

    if (lunas) {
        ubahStatusBayar(Number(lunas.getAttribute("data-lunas")), STATUS_BAYAR.LUNAS);
        render();
    }

    if (selesai) {
        Pesanan.ubah(Number(selesai.getAttribute("data-selesai")), { status: "Selesai" });
        render();
    }

    if (hapus && confirm("Hapus pesanan ini?")) {
        Pesanan.hapus(Number(hapus.getAttribute("data-hapus")));
        render();
    }
});

pantauData(render);
render();
