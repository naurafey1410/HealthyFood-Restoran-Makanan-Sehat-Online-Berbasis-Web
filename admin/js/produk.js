const tabel = document.getElementById("tabel-produk");
const form = document.getElementById("form-produk");
const gridGambar = document.getElementById("pilih-gambar");
const inputGambar = document.getElementById("input-gambar");
const pesanGambar = document.getElementById("pesan-gambar");
let idEdit = null;

// Tampilkan thumbnail gambar; gambar produk lama di luar daftar tetap ikut tampil agar tidak hilang
function renderPilihanGambar(terpilih) {
    const daftar = DAFTAR_GAMBAR.slice();
    if (terpilih && !daftar.includes(terpilih)) daftar.unshift(terpilih);

    inputGambar.value = terpilih || "";
    pesanGambar.classList.remove("error");

    gridGambar.innerHTML = daftar.map(function (path) {
        const nama = path.split("/").pop().replace(/\.[^.]+$/, "").replace(/[_-]/g, " ");
        return `
            <button type="button" class="pilih-gambar${path === terpilih ? " terpilih" : ""}" data-gambar="${esc(path)}" title="${esc(nama)}">
                <img src="../${esc(path)}" alt="${esc(nama)}" loading="lazy">
                <i class="fa-solid fa-check"></i>
            </button>`;
    }).join("");
}

gridGambar.addEventListener("click", function (event) {
    const tombol = event.target.closest("[data-gambar]");
    if (!tombol) return;

    inputGambar.value = tombol.getAttribute("data-gambar");
    pesanGambar.classList.remove("error");

    gridGambar.querySelectorAll(".pilih-gambar").forEach(function (el) {
        el.classList.toggle("terpilih", el === tombol);
    });
});

function render() {
    // Urutan sama dengan menu customer: best seller di atas
    const semua = Produk.semua();
    const list = semua.filter(function (p) { return p.best; })
        .concat(semua.filter(function (p) { return !p.best; }));

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
    renderPilihanGambar("");
    document.getElementById("judul-modal").textContent = "Tambah Produk";
    bukaModal("modal-produk");
});

form.addEventListener("submit", function (event) {
    event.preventDefault();

    if (!inputGambar.value) {
        pesanGambar.textContent = "Pilih satu gambar untuk produk ini.";
        pesanGambar.classList.add("error");
        return;
    }

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
        renderPilihanGambar(p.gambar);
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
