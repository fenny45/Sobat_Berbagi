package edu.uph.m23si1.sobatberbagi.Model;

public class Chat {

    // Untuk tampilan daftar chat (Chat List)
    private String nama;               // Nama pengguna lain
    private String pesanTerakhir;      // Isi pesan terakhir
    private String avatarPath;         // Path gambar profil user dari SharedPreferences

    // Constructor Chat List
    public Chat(String nama, String pesanTerakhir, String avatarPath) {
        this.nama = nama;
        this.pesanTerakhir = pesanTerakhir;
        this.avatarPath = avatarPath;
    }

    // Getter & Setter
    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPesanTerakhir() {
        return pesanTerakhir;
    }

    public void setPesanTerakhir(String pesanTerakhir) {
        this.pesanTerakhir = pesanTerakhir;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
}
