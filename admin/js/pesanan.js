const tabel = document.getElementById("tabel-pesanan");

function render() {
    const list = Pesanan.semua().slice().reverse();

    tabel.innerHTML = list.length
        ? list.map(function (p) {
            return `
                <tr>
                    <td>#${p.id}</td>
                    <td>${formatTanggal(p.tanggal)}</td>
                    <td>${p.items.length} item</td>
                    <td>${formatRupiah(p.total)}</td>
                    <td>${badgeStatus(p.status)}</td>
                    <td>
                        <button class="btn-aksi" data-detail="${p.id}" title="Detail">
                            <i class="fa-solid fa-eye"></i>
                        </button>
                        ${p.status !== "Selesai" ? `
                        <button class="btn-aksi" data-selesai="${p.id}" title="Tandai selesai">
                            <i class="fa-solid fa-check"></i>
                        </button>` : ""}
                        <button class="btn-aksi hapus" data-hapus="${p.id}" title="Hapus">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </td>
                </tr>`;
        }).join("")
        : `<tr><td colspan="6">Belum ada pesanan masuk dari customer.</td></tr>`;
}

function tampilkanDetail(id) {
    const p = Pesanan.semua().find(function (x) { return x.id === id; });
    if (!p) return;

    document.getElementById("judul-detail").textContent = "Pesanan #" + p.id;
    document.getElementById("isi-detail").innerHTML = `
        <p style="font-size:12px;color:#888;margin-top:0;">${formatTanggal(p.tanggal)}</p>
        <ul class="detail-list">
            ${p.items.map(function (i) {
                return `<li><span>${esc(i.nama)} x${i.jumlah}</span><span>${formatRupiah(i.harga * i.jumlah)}</span></li>`;
            }).join("")}
        </ul>
        <div class="detail-total"><span>Total</span><span>${formatRupiah(p.total)}</span></div>`;
    bukaModal("modal-detail");
}

tabel.addEventListener("click", function (event) {
    const detail = event.target.closest("[data-detail]");
    const selesai = event.target.closest("[data-selesai]");
    const hapus = event.target.closest("[data-hapus]");

    if (detail) tampilkanDetail(Number(detail.getAttribute("data-detail")));

    if (selesai) {
        Pesanan.ubah(Number(selesai.getAttribute("data-selesai")), { status: "Selesai" });
        render();
    }

    if (hapus && confirm("Hapus pesanan ini?")) {
        Pesanan.hapus(Number(hapus.getAttribute("data-hapus")));
        render();
    }
});

render();
