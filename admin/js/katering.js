const tabel = document.getElementById("tabel-katering");
const form = document.getElementById("form-katering");
let idEdit = null;

function render() {
    const list = Katering.semua();

    tabel.innerHTML = list.length
        ? list.map(function (p) {
            return `
                <tr>
                    <td>${esc(p.nama)}</td>
                    <td>${esc(p.deskripsi)}</td>
                    <td>${formatRupiah(p.harga)}</td>
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
        : `<tr><td colspan="4">Belum ada paket katering.</td></tr>`;
}

document.getElementById("btn-tambah").addEventListener("click", function () {
    idEdit = null;
    form.reset();
    document.getElementById("judul-modal").textContent = "Tambah Paket";
    bukaModal("modal-katering");
});

form.addEventListener("submit", function (event) {
    event.preventDefault();

    const data = {
        nama: nilai("input-nama"),
        deskripsi: nilai("input-deskripsi"),
        harga: Number(nilai("input-harga"))
    };

    if (idEdit === null) {
        Katering.tambah(data);
    } else {
        Katering.ubah(idEdit, data);
    }

    tutupModal("modal-katering");
    render();
});

tabel.addEventListener("click", function (event) {
    const edit = event.target.closest("[data-edit]");
    const hapus = event.target.closest("[data-hapus]");

    if (edit) {
        const id = Number(edit.getAttribute("data-edit"));
        const p = Katering.semua().find(function (x) { return x.id === id; });
        if (!p) return;

        idEdit = id;
        document.getElementById("judul-modal").textContent = "Edit Paket";
        document.getElementById("input-nama").value = p.nama;
        document.getElementById("input-deskripsi").value = p.deskripsi;
        document.getElementById("input-harga").value = p.harga;
        bukaModal("modal-katering");
    }

    if (hapus && confirm("Hapus paket katering ini?")) {
        Katering.hapus(Number(hapus.getAttribute("data-hapus")));
        render();
    }
});

render();
