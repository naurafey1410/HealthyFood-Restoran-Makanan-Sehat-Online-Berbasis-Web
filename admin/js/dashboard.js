function render() {
    const pesanan = Pesanan.semua();
    const lunas = pesanan.filter(function (p) { return p.statusBayar === STATUS_BAYAR.LUNAS; });
    const pendapatan = lunas.reduce(function (sum, p) { return sum + p.total; }, 0);
    const perluVerifikasi = pesanan.filter(function (p) { return p.statusBayar === STATUS_BAYAR.MENUNGGU; }).length;

    document.getElementById("stat-produk").textContent = Produk.semua().length;
    document.getElementById("stat-pesanan").textContent = pesanan.length;
    document.getElementById("stat-verifikasi").textContent = perluVerifikasi;
    document.getElementById("stat-pendapatan").textContent = formatRupiah(pendapatan);

    const terbaru = pesanan.slice().reverse().slice(0, 5);

    document.getElementById("tabel-terbaru").innerHTML = terbaru.length
        ? terbaru.map(function (p) {
            return `
                <tr>
                    <td>#${p.id}</td>
                    <td>${formatTanggal(p.tanggal)}</td>
                    <td>${esc(p.pembeli.nama)}</td>
                    <td>${formatRupiah(p.total)}</td>
                    <td>${badgeBayar(p.statusBayar)}</td>
                </tr>`;
        }).join("")
        : `<tr><td colspan="5">Belum ada pesanan masuk.</td></tr>`;
}

pantauData(render);
render();
