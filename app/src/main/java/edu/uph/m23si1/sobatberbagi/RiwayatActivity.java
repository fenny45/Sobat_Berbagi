package edu.uph.m23si1.sobatberbagi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.uph.m23si1.sobatberbagi.Model.Makanan;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class RiwayatActivity extends AppCompatActivity {

    private LinearLayout containerRiwayat;
    private ImageView btnBack;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        containerRiwayat = findViewById(R.id.containerRiwayat);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Init Realm
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .allowWritesOnUiThread(true)
                .build();
        realm = Realm.getInstance(config);

        // Tampilkan tanggal hari ini
        addTanggalSection(getTodayInIndonesian());

        // Ambil semua data dari Realm
        RealmResults<Makanan> data = realm.where(Makanan.class).findAll();

        for (Makanan m : data) {
            addRiwayatItem(m);
        }
    }

    private void addTanggalSection(String tanggal) {
        TextView tvTanggal = new TextView(this);
        tvTanggal.setText(tanggal);
        tvTanggal.setTextSize(16);
        tvTanggal.setPadding(10, 20, 10, 10);
        tvTanggal.setTextColor(getResources().getColor(android.R.color.black));
        containerRiwayat.addView(tvTanggal);
    }

    private void addRiwayatItem(Makanan makanan) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.item_riwayat, null);

        ImageView imgMakanan = cardView.findViewById(R.id.imgMakanan);
        TextView tvNama = cardView.findViewById(R.id.tvNamaMakanan);
        TextView tvLokasi = cardView.findViewById(R.id.tvLokasi);
        TextView tvBatasWaktu = cardView.findViewById(R.id.tvBatasWaktu);
        Button btnDetail = cardView.findViewById(R.id.btnLihat);
        Button btnEdit = cardView.findViewById(R.id.btnEdit);
        Button btnHapus = cardView.findViewById(R.id.btnHapus);

        // Isi data
        tvNama.setText(makanan.getNamaMakanan());
        tvLokasi.setText("Lokasi: " + makanan.getLokasi());
        tvBatasWaktu.setText("Batas Waktu: " + makanan.getBatasWaktu());

        // Tampilkan gambar jika ada
        if (makanan.getGambarPath() != null && new File(makanan.getGambarPath()).exists()) {
            imgMakanan.setImageURI(Uri.fromFile(new File(makanan.getGambarPath())));
        } else {
            imgMakanan.setImageResource(R.drawable.gallery); // fallback jika gambar tidak ada
        }

        // Tombol aksi
        btnDetail.setOnClickListener(v -> {
            tampilkanDetailMakanan(makanan);
        });


        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(RiwayatActivity.this, EditMakananActivity.class);
            intent.putExtra("id", makanan.getId()); // kirim ID makanan ke halaman edit
            startActivity(intent);
        });


        btnHapus.setOnClickListener(v -> {
            // Hapus dari Realm
            realm.executeTransaction(r -> {
                Makanan toDelete = r.where(Makanan.class).equalTo("id", makanan.getId()).findFirst();
                if (toDelete != null) toDelete.deleteFromRealm();
            });

            // Hapus dari tampilan
            containerRiwayat.removeView(cardView);
            Toast.makeText(this, "Item dihapus", Toast.LENGTH_SHORT).show();
        });

        containerRiwayat.addView(cardView);
    }

    private void tampilkanDetailMakanan(Makanan makanan) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 10);

        ImageView img = new ImageView(this);
        img.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 600));
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (makanan.getGambarPath() != null && new File(makanan.getGambarPath()).exists()) {
            img.setImageURI(Uri.fromFile(new File(makanan.getGambarPath())));
        } else {
            img.setImageResource(R.drawable.gallery);
        }

        TextView tvNama = new TextView(this);
        tvNama.setText("Nama Makanan: " + makanan.getNamaMakanan());
        tvNama.setTextSize(18);
        tvNama.setPadding(0, 20, 0, 10);

        TextView tvDeskripsi = new TextView(this);
        tvDeskripsi.setText("Deskripsi: " + makanan.getDeskripsi());
        tvDeskripsi.setPadding(0, 10, 0, 10);

        TextView tvLokasi = new TextView(this);
        tvLokasi.setText("Lokasi: " + makanan.getLokasi());
        tvLokasi.setPadding(0, 10, 0, 10);

        TextView tvBatas = new TextView(this);
        tvBatas.setText("Batas Waktu: " + makanan.getBatasWaktu());
        tvBatas.setPadding(0, 10, 0, 20);

        layout.addView(img);
        layout.addView(tvNama);
        layout.addView(tvDeskripsi);
        layout.addView(tvLokasi);
        layout.addView(tvBatas);

        new AlertDialog.Builder(this)
                .setTitle("Detail Makanan")
                .setView(layout)
                .setPositiveButton("Tutup", null)
                .show();
    }


    private String getTodayInIndonesian() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
        return sdf.format(new Date());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) realm.close();
    }
}
