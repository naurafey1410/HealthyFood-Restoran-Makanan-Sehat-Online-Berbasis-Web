
<?php
session_start();

header('Content-Type: application/json');

if (isset($_SESSION['Customer_ID'])) {
    echo json_encode(["login" => true]);
} else {
    echo json_encode(["login" => false]);
}
?>
