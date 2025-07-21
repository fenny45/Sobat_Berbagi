package edu.uph.m23si1.sobatberbagi.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Komentar implements Parcelable {
    private float rating;
    private String komentar;
    private String nama;

    // Konstruktor
    public Komentar(float rating, String komentar, String nama) {
        this.rating = rating;
        this.komentar = komentar;
        this.nama = nama;
    }

    // Getter
    public float getRating() {
        return rating;
    }

    public String getKomentar() {
        return komentar;
    }

    public String getNama() {
        return nama;
    }

    // Parcelable logic
    protected Komentar(Parcel in) {
        rating = in.readFloat();
        komentar = in.readString();
        nama = in.readString();
    }

    public static final Creator<Komentar> CREATOR = new Creator<Komentar>() {
        @Override
        public Komentar createFromParcel(Parcel in) {
            return new Komentar(in);
        }

        @Override
        public Komentar[] newArray(int size) {
            return new Komentar[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeFloat(rating);
        parcel.writeString(komentar);
        parcel.writeString(nama);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
