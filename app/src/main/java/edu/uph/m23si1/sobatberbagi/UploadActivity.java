package edu.uph.m23si1.sobatberbagi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.*;
import java.util.UUID;

import edu.uph.m23si1.sobatberbagi.Model.Makanan;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class UploadActivity extends AppCompatActivity {

    private EditText etNamaMakanan, etDeskripsi, etLokasi, etBatasWaktu;
    private ImageView previewImage;
    private CheckBox checkLayak;
    private Button btnUpload;
    private Uri imageUri;
    private String imagePath;
    private Realm realm;

    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // Init Realm
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .allowWritesOnUiThread(true)
                .build();
        realm = Realm.getInstance(config);

        //Ambil username login dari session
        SharedPreferences prefs = getSharedPreferences("LoginSession", MODE_PRIVATE);
        currentUsername = prefs.getString("currentUser", "me");

        // Init view
        etNamaMakanan = findViewById(R.id.etNamaMakanan);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        etLokasi = findViewById(R.id.etLokasi);
        etBatasWaktu = findViewById(R.id.etBatasWaktu);
        previewImage = findViewById(R.id.previewImage);
        checkLayak = findViewById(R.id.checkLayak);
        btnUpload = findViewById(R.id.btnUpload);

        findViewById(R.id.btnUploadMakanan).setOnClickListener(v -> pickImage.launch("image/*"));

        btnUpload.setOnClickListener(v -> {
            String nama = etNamaMakanan.getText().toString().trim();
            String deskripsi = etDeskripsi.getText().toString().trim();
            String lokasi = etLokasi.getText().toString().trim();
            String batas = etBatasWaktu.getText().toString().trim();

            if (nama.isEmpty() || lokasi.isEmpty() || batas.isEmpty() || imagePath == null) {
                Toast.makeText(this, "Semua kolom dan gambar harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!checkLayak.isChecked()) {
                Toast.makeText(this, "Harap centang konfirmasi kelayakan", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simpan makanan dan tambah nama pengunggah
            realm.executeTransaction(r -> {
                Makanan m = r.createObject(Makanan.class, UUID.randomUUID().toString());
                m.setNamaMakanan(nama);
                m.setDeskripsi(deskripsi);
                m.setLokasi(lokasi);
                m.setBatasWaktu(batas);
                m.setGambarPath(imagePath);
                m.setUsernamePengunggah(currentUsername);
            });

            Toast.makeText(this, "Makanan berhasil diunggah", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UploadActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        setupBottomNav();
    }

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUri = uri;
                    imagePath = salinGambarKeInternal(uri);
                    if (imagePath != null) {
                        previewImage.setVisibility(View.VISIBLE);
                        previewImage.setImageURI(Uri.fromFile(new File(imagePath)));
                    } else {
                        Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private String salinGambarKeInternal(Uri sourceUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(sourceUri);
            File file = new File(getFilesDir(), "gambar_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            inputStream.close();
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setupBottomNav() {
        View navHome = findViewById(R.id.navHome);
        View navUpload = findViewById(R.id.navUpload);
        View navChat = findViewById(R.id.navChat);
        View navInfo = findViewById(R.id.navInfo);
        View navAccount = findViewById(R.id.navAccount);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        }

        if (navUpload != null) {
            navUpload.setOnClickListener(v ->
                    Toast.makeText(this, "Kamu sudah di halaman Upload", Toast.LENGTH_SHORT).show());
        }

        if (navChat != null) {
            navChat.setOnClickListener(v -> {
                Intent intent = new Intent(this, ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        }

        if (navInfo != null) {
            navInfo.setOnClickListener(v -> {
                Intent intent = new Intent(this, SaranActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        }

        if (navAccount != null) {
            navAccount.setOnClickListener(v -> {
                Intent intent = new Intent(this, AccountActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}
