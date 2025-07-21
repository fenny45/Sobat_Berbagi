package edu.uph.m23si1.sobatberbagi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import edu.uph.m23si1.sobatberbagi.Model.Makanan;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class EditMakananActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imgPreview, btnBack;
    private EditText etNamaMakanan, etDeskripsi, etLokasi, etBatasWaktu;
    private Button btnGantiGambar, btnSimpan, btnBatal;

    private Realm realm;
    private Makanan makanan;
    private Uri gambarUriTerpilih;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_makanan);

        // Realm setup
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .allowWritesOnUiThread(true)
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);

        // Ambil ID dari intent
        String idMakanan = getIntent().getStringExtra("id");
        makanan = realm.where(Makanan.class).equalTo("id", idMakanan).findFirst();

        // Bind View
        imgPreview = findViewById(R.id.imgPreview);
        btnBack = findViewById(R.id.btnBack);
        etNamaMakanan = findViewById(R.id.etNamaMakanan);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        etLokasi = findViewById(R.id.etLokasi);
        etBatasWaktu = findViewById(R.id.etBatasWaktu);
        btnGantiGambar = findViewById(R.id.btnGantiGambar);
        btnSimpan = findViewById(R.id.btnSimpan);
        btnBatal = findViewById(R.id.btnBatal);

        // Tampilkan data lama
        if (makanan != null) {
            etNamaMakanan.setText(makanan.getNamaMakanan());
            etDeskripsi.setText(makanan.getDeskripsi());
            etLokasi.setText(makanan.getLokasi());
            etBatasWaktu.setText(makanan.getBatasWaktu());

            if (makanan.getGambarPath() != null && new File(makanan.getGambarPath()).exists()) {
                imgPreview.setImageURI(Uri.fromFile(new File(makanan.getGambarPath())));
            }
        }

        // Tombol back & batal
        btnBack.setOnClickListener(v -> finish());
        btnBatal.setOnClickListener(v -> finish());

        // Ganti gambar
        btnGantiGambar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Simpan perubahan
        btnSimpan.setOnClickListener(v -> {
            String nama = etNamaMakanan.getText().toString().trim();
            String deskripsi = etDeskripsi.getText().toString().trim();
            String lokasi = etLokasi.getText().toString().trim();
            String batas = etBatasWaktu.getText().toString().trim();

            if (nama.isEmpty() || deskripsi.isEmpty() || lokasi.isEmpty() || batas.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            realm.executeTransaction(r -> {
                makanan.setNamaMakanan(nama);
                makanan.setDeskripsi(deskripsi);
                makanan.setLokasi(lokasi);
                makanan.setBatasWaktu(batas);

                // Kalau ada gambar baru
                if (gambarUriTerpilih != null) {
                    makanan.setGambarPath(gambarUriTerpilih.toString()); // Simpan URI-nya
                }
            });

            Toast.makeText(this, "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            gambarUriTerpilih = data.getData();
            if (gambarUriTerpilih != null) {
                imgPreview.setImageURI(gambarUriTerpilih);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) realm.close();
    }
}
