const pesanan = Pesanan.semua();
const pendapatan = pesanan.reduce(function (sum, p) { return sum + p.total; }, 0);

document.getElementById("stat-produk").textContent = Produk.semua().length;
document.getElementById("stat-pesanan").textContent = pesanan.length;
document.getElementById("stat-katering").textContent = Katering.semua().length;
document.getElementById("stat-pendapatan").textContent = formatRupiah(pendapatan);

const terbaru = pesanan.slice().reverse().slice(0, 5);

document.getElementById("tabel-terbaru").innerHTML = terbaru.length
    ? terbaru.map(function (p) {
        return `
            <tr>
                <td>#${p.id}</td>
                <td>${formatTanggal(p.tanggal)}</td>
                <td>${formatRupiah(p.total)}</td>
                <td>${badgeStatus(p.status)}</td>
            </tr>`;
    }).join("")
    : `<tr><td colspan="4">Belum ada pesanan masuk.</td></tr>`;
