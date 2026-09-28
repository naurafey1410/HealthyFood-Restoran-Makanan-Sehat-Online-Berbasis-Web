const form = document.getElementById("form-konten");
const pesanSukses = document.getElementById("pesan-sukses");
const konten = getKonten();

document.getElementById("input-pembuka").value = konten.tulisanPembuka;
document.getElementById("input-slogan").value = konten.slogan;

form.addEventListener("submit", function (event) {
    event.preventDefault();

    simpanKonten({
        tulisanPembuka: nilai("input-pembuka"),
        slogan: nilai("input-slogan")
    });

    pesanSukses.style.display = "block";
    setTimeout(function () {
        pesanSukses.style.display = "none";
    }, 2200);
});
