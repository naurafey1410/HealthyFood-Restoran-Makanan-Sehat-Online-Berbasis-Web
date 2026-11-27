@echo off
REM Cek koneksi database dan alur pesanan dari terminal (Windows), dijalankan dari folder gui-java
javac -encoding UTF-8 -cp "lib\*" -d out -sourcepath src\main\java src\main\java\healthyfood\config\CekDatabase.java
if errorlevel 1 (
  echo Kompilasi gagal. Periksa pesan error di atas.
  pause
  exit /b 1
)
java -cp "out;lib\*" healthyfood.config.CekDatabase
