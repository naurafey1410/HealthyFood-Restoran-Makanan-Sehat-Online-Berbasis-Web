STRUKTUR GUI HEALTHY FOOD

Project ini sudah dipisahkan berdasarkan tanggung jawab class:
- healthyfood.app : titik awal aplikasi (App.java)
- healthyfood.view : seluruh class tampilan GUI (LoginFrame, DashboardProdukGUI,
  PembayaranGUI, StatusPesananFrame, Tugasgui)
- healthyfood.model : pengelolaan/penyimpanan data pesanan (OrderStore)
- healthyfood.latihan : program latihan keamanan yang terpisah (Main.java),
  bukan bagian dari alur GUI HealthyFood.

CARA MENJALANKAN
1. Buka folder GUI HEALTHY FOOD TERINTEGRASI di NetBeans sebagai project Java.
   Jika membuat project baru, salin folder healthyfood ke Source Packages.
2. Jalankan file healthyfood.app.App.java.

Jika menggunakan terminal dari folder project:
  javac -encoding UTF-8 -d out src/main/java/healthyfood/model/OrderStore.java src/main/java/healthyfood/view/*.java src/main/java/healthyfood/app/App.java
  java -cp out healthyfood.app.App

Main.java pada package healthyfood.latihan adalah program latihan terpisah dan tidak perlu dijalankan untuk membuka aplikasi GUI.
