const tabel = document.getElementById("tabel-produk");
const form = document.getElementById("form-produk");
let idEdit = null;

function render() {
    const list = Produk.semua();

    tabel.innerHTML = list.length
        ? list.map(function (p) {
            return `
                <tr>
                    <td><img src="../${esc(p.gambar)}" class="foto-mini" alt=""></td>
                    <td>${esc(p.nama)}</td>
                    <td>${esc(p.kalori)}</td>
                    <td>${formatRupiah(p.harga)}</td>
                    <td>${p.grup ? esc(p.grup) : "-"}</td>
                    <td>${p.best ? '<span class="badge status-selesai">Ya</span>' : "-"}</td>
                    <td>
                        <button class="btn-aksi" data-edit="${p.id}" title="Edit">
                            <i class="fa-solid fa-pen"></i>
                        </button>
                        <button class="btn-aksi hapus" data-hapus="${p.id}" title="Hapus">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </td>
                </tr>`;
        }).join("")
        : `<tr><td colspan="7">Belum ada produk.</td></tr>`;
}

document.getElementById("btn-tambah").addEventListener("click", function () {
    idEdit = null;
    form.reset();
    document.getElementById("judul-modal").textContent = "Tambah Produk";
    bukaModal("modal-produk");
});

form.addEventListener("submit", function (event) {
    event.preventDefault();

    const data = {
        nama: nilai("input-nama"),
        kalori: nilai("input-kalori"),
        harga: Number(nilai("input-harga")),
        gambar: nilai("input-gambar"),
        grup: nilai("input-grup"),
        best: document.getElementById("input-best").checked
    };

    if (idEdit === null) {
        Produk.tambah(data);
    } else {
        Produk.ubah(idEdit, data);
    }

    tutupModal("modal-produk");
    render();
});

tabel.addEventListener("click", function (event) {
    const edit = event.target.closest("[data-edit]");
    const hapus = event.target.closest("[data-hapus]");

    if (edit) {
        const id = Number(edit.getAttribute("data-edit"));
        const p = Produk.semua().find(function (x) { return x.id === id; });
        if (!p) return;

        idEdit = id;
        document.getElementById("judul-modal").textContent = "Edit Produk";
        document.getElementById("input-nama").value = p.nama;
        document.getElementById("input-kalori").value = p.kalori;
        document.getElementById("input-harga").value = p.harga;
        document.getElementById("input-gambar").value = p.gambar;
        document.getElementById("input-grup").value = p.grup || "";
        document.getElementById("input-best").checked = !!p.best;
        bukaModal("modal-produk");
    }

    if (hapus && confirm("Hapus produk ini dari menu customer?")) {
        Produk.hapus(Number(hapus.getAttribute("data-hapus")));
        render();
    }
});

render();
