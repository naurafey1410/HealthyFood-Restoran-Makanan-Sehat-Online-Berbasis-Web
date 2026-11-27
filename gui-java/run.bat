@echo off
REM Kompilasi dan jalankan HealthyFood dari folder gui-java (Windows)
javac -encoding UTF-8 -cp "lib\*" -d out -sourcepath src\main\java src\main\java\healthyfood\app\App.java
if errorlevel 1 (
  echo Kompilasi gagal. Periksa pesan error di atas.
  pause
  exit /b 1
)
java -cp "out;lib\*" healthyfood.app.App
