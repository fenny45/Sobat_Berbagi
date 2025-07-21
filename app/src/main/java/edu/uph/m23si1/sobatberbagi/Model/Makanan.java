package edu.uph.m23si1.sobatberbagi.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Makanan extends RealmObject {
    @PrimaryKey
    private String id;

    private String namaMakanan;
    private String deskripsi;
    private String lokasi;
    private String batasWaktu;
    private String gambarPath;
    private String usernamePengunggah;

    // Getter & Setter
    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    public String getNamaMakanan() {
        return namaMakanan;
    }

    public void setNamaMakanan(String namaMakanan) {
        this.namaMakanan = namaMakanan;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getBatasWaktu() {
        return batasWaktu;
    }

    public void setBatasWaktu(String batasWaktu) {
        this.batasWaktu = batasWaktu;
    }

    public String getGambarPath() {
        return gambarPath;
    }

    public void setGambarPath(String gambarPath) {
        this.gambarPath = gambarPath;
    }

    public String getUsernamePengunggah() { return usernamePengunggah; }

    public void setUsernamePengunggah(String usernamePengunggah) {
        this.usernamePengunggah = usernamePengunggah;
    }


}
