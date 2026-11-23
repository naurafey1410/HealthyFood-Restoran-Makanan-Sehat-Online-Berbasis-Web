
<?php
session_start();
require_once '../koneksi.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    header("Location: login.html");
    exit;
}

$phone = trim($_POST['phone']);
$password = $_POST['password'];

// Mencari customer berdasarkan nomor HP
$query = mysqli_prepare(
    $koneksi,
    "SELECT Customer_ID, Name, Username, Phone, Password
     FROM customer WHERE Phone = ?"
);

mysqli_stmt_bind_param($query, "s", $phone);
mysqli_stmt_execute($query);

$hasil = mysqli_stmt_get_result($query);
$customer = mysqli_fetch_assoc($hasil);

// Memeriksa password
if ($customer && password_verify($password, $customer['Password'])) {

    session_regenerate_id(true);

    $_SESSION['Customer_ID'] = $customer['Customer_ID'];
    $_SESSION['Name'] = $customer['Name'];
    $_SESSION['Username'] = $customer['Username'];

    header("Location: ../restoran.html");
    exit;

} else {
    header("Location: login.html?error=login");
    exit;
}
?>
