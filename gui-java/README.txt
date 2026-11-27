HEALTHY FOOD - APLIKASI DESKTOP (Java Swing + MySQL/MariaDB)

STRUKTUR (src/main/java/healthyfood)
- app     : App.java (titik awal)
- config  : Database.java (koneksi), CekDatabase.java (pengecekan dari terminal)
- dao     : ItemDAO, AkunDAO, OrderDAO (semua perintah SQL ada di sini)
- model   : ProdukStore (menu), AkunService (login/daftar), OrderService (pesanan + status),
            KontenStore (judul header, di memori)
- util    : Gambar, Tema, Validasi
- view    : seluruh tampilan GUI

ALUR DATA
  GUI -> Service/Store -> DAO -> database healthy_food
  Menu = tabel item | Akun = admin, customer | Pesanan = orders, order_item

CARA MENJALANKAN DARI TERMINAL (Windows, XAMPP di C:\xampp)
1. Jalankan MySQL      : C:\xampp\mysql_start.bat           (biarkan jendelanya terbuka)
2. Buat database      : C:\xampp\mysql\bin\mysql.exe -u root -e "CREATE DATABASE IF NOT EXISTS healthy_food CHARACTER SET utf8mb4"
3. Impor tabel (cmd)  : C:\xampp\mysql\bin\mysql.exe -u root healthy_food < ..\database\healthy_food.sql
4. Cek database       : cek-database.bat
5. Jalankan aplikasi  : run.bat

Akun contoh: admin dimas / admin123 ; customer ainiimup / customer123
