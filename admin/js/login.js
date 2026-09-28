const ADMIN_USERNAME = "admin";
const ADMIN_PASSWORD = "admin123";
const SESSION_KEY = "hf_admin_login";

const formLogin = document.getElementById("form-login");
const pesanError = document.getElementById("pesan-error");

if (sessionStorage.getItem(SESSION_KEY) === "true") {
    window.location.href = "dashboard.html";
}

formLogin.addEventListener("submit", function (event) {
    event.preventDefault();

    const username = document.getElementById("input-username").value.trim();
    const password = document.getElementById("input-password").value;

    if (username === ADMIN_USERNAME && password === ADMIN_PASSWORD) {
        sessionStorage.setItem(SESSION_KEY, "true");
        window.location.href = "dashboard.html";
    } else {
        pesanError.textContent = "Username atau password salah.";
    }
});
