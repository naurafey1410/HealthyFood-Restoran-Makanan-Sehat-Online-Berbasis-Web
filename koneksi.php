
<?php
$host = "localhost";
$user = "root";
$password = "";
$database = "healthy_food";

$koneksi = mysqli_connect($host, $user, $password, $database);

if (!$koneksi) {
    die("Koneksi database gagal: " . mysqli_connect_error());
}
?>
