-- =====================================================================
-- HealthyFood - skrip tabel untuk phpMyAdmin
-- Cara pakai: pilih database healthy_food di panel kiri dulu,
-- lalu tab SQL > tempel isi file ini > Kirim.
-- Skrip ini aman dijalankan ulang (tabel lama dihapus dulu).
-- =====================================================================
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS admin;
-- ---------------------------------------------------------------
-- ADMIN
-- ---------------------------------------------------------------
CREATE TABLE admin (
  Admin_ID  INT AUTO_INCREMENT PRIMARY KEY,
  Username  VARCHAR(50)  NOT NULL UNIQUE,
  Password  VARCHAR(100) NOT NULL
);
INSERT INTO admin (Username, Password) VALUES ('dimas', '$2a$10$lU4dP6U8m752W8bz2X5ZW.ZC3jTEvD1i1sgJbVOUbVVzMdqN/iNt6');

-- ---------------------------------------------------------------
-- CUSTOMER (nama, nomor HP, username, password)
-- ---------------------------------------------------------------
CREATE TABLE customer (
  Customer_ID INT AUTO_INCREMENT PRIMARY KEY,
  Name        VARCHAR(100) NOT NULL,
  Username    VARCHAR(50)  NOT NULL UNIQUE,
  Phone       VARCHAR(20)  NOT NULL,
  Password    VARCHAR(100) NOT NULL
);
INSERT INTO customer (Name, Username, Phone, Password)
VALUES ('Najriah Nur Aini Sinaga', 'ainiimup', '082260476154', '$2a$10$RwwbUSCvaJXBS91oP00iFuQwFPMBTpZa9OsI/KOcmf10ar9k5Y142');

-- ---------------------------------------------------------------
-- ITEM (produk menu)
-- Calories: teks rentang kalori; Deskripsi dan Protein/Karbo/Lemak/Serat untuk halaman Gizi
-- Image: nama file foto di folder gambar (kosong = pakai emoji cadangan)
-- Best_seller: 1 = ditampilkan sebagai Recommended
-- ---------------------------------------------------------------
CREATE TABLE item (
  Item_ID     INT AUTO_INCREMENT PRIMARY KEY,
  Item_name   VARCHAR(100)  NOT NULL,
  Calories    VARCHAR(30)   NOT NULL,
  Price       DECIMAL(10,2) NOT NULL CHECK (Price > 0),
  Image       VARCHAR(255)  NULL,
  Best_seller TINYINT(1)    NOT NULL DEFAULT 0,
  Deskripsi   TEXT          NULL,
  Protein     VARCHAR(20)   NULL,
  Karbo       VARCHAR(20)   NULL,
  Lemak       VARCHAR(20)   NULL,
  Serat       VARCHAR(20)   NULL
);
INSERT INTO item (Item_name, Calories, Price, Image, Best_seller, Deskripsi, Protein, Karbo, Lemak, Serat) VALUES
('Vegetable Salad', '250 - 350 kkal', 22000.00, 'salad.jpg', 1, 'Salad sayuran segar yang cocok sebagai pilihan menu ringan dan kaya serat.', '5-8 g', '15-25 g', '8-15 g', '4-7 g'),
('Tempeh Steak', '280 - 380 kkal', 34000.00, 'steak_tempe.jpg', 1, 'Steak berbahan dasar tempe yang menawarkan protein nabati dengan cita rasa gurih.', '15-22 g', '20-35 g', '10-18 g', '5-8 g'),
('Mushroom Steak', '320 - 420 kkal', 40000.00, 'steak_jamur.jpg', 1, 'Olahan jamur dengan tekstur lembut dan rasa gurih sebagai alternatif menu steak.', '8-14 g', '25-40 g', '12-20 g', '4-7 g'),
('Green Spinach Noodles', '380 - 480 kkal', 28000.00, 'mie_bayam.jpg', 1, 'Mie dengan bayam sebagai variasi menu yang memadukan karbohidrat dan sayuran.', '8-14 g', '50-70 g', '6-14 g', '4-8 g'),
('Vegetable Kebab', '300 - 400 kkal', 22000.00, 'kebab_sayur.jpg', 1, 'Kebab sayuran yang praktis dengan kombinasi sayuran dan kulit kebab.', '7-12 g', '35-50 g', '8-15 g', '5-8 g'),
('Chicken Soup', '180 - 260 kkal', 30000.00, 'sup_ayam.jpg', 1, 'Sup ayam hangat dengan kuah dan bahan pelengkap yang cocok untuk hidangan ringan.', '15-25 g', '10-25 g', '5-12 g', '2-5 g'),
('Grain Bowl', '450 - 550 kkal', 27000.00, 'grain_bowl.jpg', 0, 'Grain bowl berisi sumber karbohidrat dan pelengkap yang dapat memberi energi untuk aktivitas.', '10-18 g', '55-75 g', '10-18 g', '6-10 g'),
('Fruit Salad', '120 - 180 kkal', 23000.00, 'salad_buah.jpg', 0, 'Salad buah dengan aneka potongan buah sebagai pilihan segar dengan rasa manis alami.', '2-5 g', '25-40 g', '1-8 g', '3-6 g'),
('Grilled Salmon', '400 - 500 kkal', 43000.00, 'salmon_panggang.jpg', 0, 'Salmon panggang sebagai sumber protein dan lemak omega-3, disajikan sebagai menu utama.', '25-35 g', '5-20 g', '15-25 g', '1-4 g'),
('Chicken Caesar Wrap', '380 - 480 kkal', 27000.00, 'kebab_daging.jpeg', 0, 'Wrap berisi ayam dan sayuran yang praktis untuk dinikmati sebagai menu makan.', '20-30 g', '35-50 g', '10-18 g', '4-7 g'),
('Salmon & Cream Cheese Bagel', '350 - 450 kkal', 40000.00, 'roti_salmon.jpg', 0, 'Bagel dengan salmon dan cream cheese yang memadukan protein, karbohidrat, dan tekstur lembut.', '18-28 g', '35-50 g', '12-22 g', '2-5 g'),
('Grilled Chicken Pesto Pasta', '480 - 580 kkal', 30000.00, 'pasta_ayam.jpg', 0, 'Pasta dengan ayam dan pesto untuk pilihan hidangan mengenyangkan dengan rasa herba.', '20-30 g', '55-75 g', '12-22 g', '3-7 g'),
('Avocado Toast', '220 - 300 kkal', 22000.00, 'sarapan_sehat.jpg', 0, 'Roti panggang dengan alpukat, cocok sebagai pilihan sarapan dengan lemak tak jenuh.', '5-9 g', '25-40 g', '10-18 g', '6-10 g'),
('Oatmeal Fruit Bowl', '280 - 350 kkal', 26000.00, 'sarapan_sehat.jpg', 0, 'Oatmeal dengan buah sebagai menu sarapan yang mengandung karbohidrat dan serat.', '7-12 g', '40-55 g', '5-12 g', '6-10 g'),
('Egg & Toast Combo', '320 - 400 kkal', 28000.00, 'sarapan_sehat.jpg', 0, 'Kombinasi telur dan roti panggang untuk sarapan praktis dengan sumber protein.', '12-20 g', '30-45 g', '10-18 g', '3-6 g');

-- ---------------------------------------------------------------
-- ORDERS (pesanan) - status dipisah sesuai ERD
--   Payment_status : Belum Dibayar | Menunggu Konfirmasi | Dibayar
--   Order_status   : Menunggu | Diproses | Siap Diambil
--   Notif_dilihat  : 0 = customer belum melihat notifikasi "Siap Diambil", 1 = sudah dilihat
-- Pesanan selalu berasal dari customer yang sudah login.
-- Password admin dan customer disimpan sebagai hash bcrypt.
-- ---------------------------------------------------------------
CREATE TABLE orders (
  Order_ID       INT AUTO_INCREMENT PRIMARY KEY,
  Customer_ID    INT NOT NULL,
  Order_Date     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  Total          DECIMAL(10,2) NOT NULL,
  Payment_method VARCHAR(50) NOT NULL,
  Payment_status VARCHAR(30) NOT NULL DEFAULT 'Belum Dibayar',
  Order_status   VARCHAR(30) NOT NULL DEFAULT 'Menunggu',
  Notif_dilihat  TINYINT(1)  NOT NULL DEFAULT 0,
  FOREIGN KEY (Customer_ID) REFERENCES customer (Customer_ID)
);

-- ---------------------------------------------------------------
-- ORDER_ITEM (rincian item per pesanan)
-- ---------------------------------------------------------------
CREATE TABLE order_item (
  OrderItem_ID INT AUTO_INCREMENT PRIMARY KEY,
  Order_ID     INT NOT NULL,
  Item_ID      INT NOT NULL,
  Quantity     INT NOT NULL CHECK (Quantity > 0),
  Unit_price   DECIMAL(10,2) NOT NULL,
  Total_price  DECIMAL(10,2) NOT NULL,
  FOREIGN KEY (Order_ID) REFERENCES orders (Order_ID) ON DELETE CASCADE,
  FOREIGN KEY (Item_ID)  REFERENCES item (Item_ID)
);