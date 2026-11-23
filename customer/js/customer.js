
const formRegister = document.getElementById("formRegister");

if (formRegister) {
    formRegister.addEventListener("submit", function(event) {
        const username = document.getElementById("username").value.trim();
        const phone = document.getElementById("phone").value.trim();
        const password = document.getElementById("password").value;

        const pesan = document.getElementById("pesanValidasi");

        const polaUsername = /^[a-zA-Z0-9._]{3,}$/;
        const polaPhone = /^[0-9]{11,15}$/;
        const polaHuruf = /[a-zA-Z]/;
        const polaAngka = /[0-9]/;

        pesan.textContent = "";

        if (!polaUsername.test(username)) {
            event.preventDefault();
            pesan.textContent = "Username minimal 3 karakter. Gunakan huruf, angka, titik, atau underscore.";
        }
        else if (!polaPhone.test(phone)) {
            event.preventDefault();
            pesan.textContent = "Nomor HP harus minimal 11 digit angka.";
        }
        else if (
            password.length < 8 ||
            !polaHuruf.test(password) ||
            !polaAngka.test(password)
        ) {
            event.preventDefault();
            pesan.textContent = "Password minimal 8 karakter dan wajib mengandung huruf serta angka.";
        }
    });
}

// Menampilkan pesan kesalahan dari PHP
const parameter = new URLSearchParams(window.location.search);
const error = parameter.get("error");

if (error) {
    const pesan = document.getElementById("pesanValidasi");

    const daftarPesan = {
        username: "Username minimal 3 karakter dan hanya boleh menggunakan huruf, angka, titik, atau underscore.",
        phone: "Nomor HP harus terdiri dari 11 sampai 15 digit angka.",
        password: "Password minimal 8 karakter dan wajib mengandung huruf serta angka.",
        duplicate: "Username atau nomor HP sudah digunakan.",
        failed: "Pendaftaran gagal. Silakan coba lagi."
    };

    pesan.textContent = daftarPesan[error] || "Terjadi kesalahan.";
}
