
<?php
require_once '../koneksi.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    header("Location: register.html");
    exit;
}

$nama = trim($_POST['nama']);
$username = trim($_POST['username']);
$phone = trim($_POST['phone']);
$password = $_POST['password'];

// Validasi username
if (!preg_match('/^[a-zA-Z0-9._]{3,}$/', $username)) {
    header("Location: register.html?error=username");
    exit;
}

// Validasi nomor HP
if (!preg_match('/^[0-9]{11,15}$/', $phone)) {
    header("Location: register.html?error=phone");
    exit;
}

// Validasi password
if (
    strlen($password) < 8 ||
    !preg_match('/[a-zA-Z]/', $password) ||
    !preg_match('/[0-9]/', $password)
) {
    header("Location: register.html?error=password");
    exit;
}

// Periksa username dan nomor HP
$cek = mysqli_prepare(
    $koneksi,
    "SELECT Customer_ID FROM customer
     WHERE Username = ? OR Phone = ?"
);

mysqli_stmt_bind_param($cek, "ss", $username, $phone);
mysqli_stmt_execute($cek);

$hasil = mysqli_stmt_get_result($cek);

if (mysqli_num_rows($hasil) > 0) {
    header("Location: register.html?error=duplicate");
    exit;
}

// Hash password
$password_hash = password_hash($password, PASSWORD_DEFAULT);

// Simpan customer
$simpan = mysqli_prepare(
    $koneksi,
    "INSERT INTO customer (Name, Username, Phone, Password)
     VALUES (?, ?, ?, ?)"
);

mysqli_stmt_bind_param(
    $simpan,
    "ssss",
    $nama,
    $username,
    $phone,
    $password_hash
);

if (mysqli_stmt_execute($simpan)) {
    header("Location: login.html?status=success");
    exit;
} else {
    header("Location: register.html?error=failed");
    exit;
}
?>
