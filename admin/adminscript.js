const SESSION_KEY = "hf_admin_login";

if (sessionStorage.getItem(SESSION_KEY) !== "true") {
    window.location.replace("login.html");
}

const MENU_ADMIN = [
    ["dashboard.html", "fa-gauge", "Dashboard"],
    ["produk.html", "fa-bowl-food", "Produk"],
    ["pesanan.html", "fa-receipt", "Pesanan"],
    ["katering.html", "fa-truck-fast", "Katering"],
    ["konten.html", "fa-pen-to-square", "Konten"]
];

const halamanSekarang = window.location.pathname.split("/").pop();

document.getElementById("sidebar").innerHTML = `
    <div class="sidebar-brand">
        <i class="fa-solid fa-leaf"></i>
        <span>HealthyFood</span>
    </div>

    <nav class="sidebar-nav">
        ${MENU_ADMIN.map(function (m) {
            return `<a href="${m[0]}" class="sidebar-link${m[0] === halamanSekarang ? " aktif" : ""}">
                <i class="fa-solid ${m[1]}"></i> <span>${m[2]}</span>
            </a>`;
        }).join("")}
    </nav>

    <div class="sidebar-footer">
        <a href="../restoran.html" target="_blank">
            <i class="fa-solid fa-arrow-up-right-from-square"></i> <span>Lihat Website</span>
        </a>
        <button class="btn-logout" id="btn-logout">
            <i class="fa-solid fa-right-from-bracket"></i> <span>Logout</span>
        </button>
    </div>`;

document.getElementById("btn-logout").addEventListener("click", function () {
    sessionStorage.removeItem(SESSION_KEY);
    window.location.href = "login.html";
});

function bukaModal(id) {
    document.getElementById(id).classList.add("aktif");
}

function tutupModal(id) {
    document.getElementById(id).classList.remove("aktif");
}

document.addEventListener("click", function (event) {
    const penutup = event.target.closest("[data-tutup]");
    if (penutup) penutup.closest(".modal-overlay").classList.remove("aktif");
});

function nilai(id) {
    return document.getElementById(id).value.trim();
}

function badgeStatus(status) {
    return `<span class="badge ${status === "Selesai" ? "status-selesai" : "status-baru"}">${esc(status)}</span>`;
}
