package edu.uph.m23si1.sobatberbagi;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.*;

import edu.uph.m23si1.sobatberbagi.auth.LoginActivity;

public class EditProfilActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private String currentUsername;
    private String currentUserKey;

    private ImageView imgProfile, btnChangePhoto;
    private TextView tvNama, tvDeskripsi, tvUsername, tvEmail, tvGender, tvNoTelp, tvUmur, tvAlamat;
    private Button btnSave;
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        // Ambil user login
        SharedPreferences sessionPrefs = getSharedPreferences("LoginSession", MODE_PRIVATE);
        currentUsername = sessionPrefs.getString("currentUser", null);
        if (currentUsername == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        currentUserKey = "UserProfile_" + currentUsername;

        // Inisialisasi View
        imgProfile = findViewById(R.id.imgProfile);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        btnSave = findViewById(R.id.btnSave);

        tvNama = findViewById(R.id.tvNama);
        tvDeskripsi = findViewById(R.id.tvDeskripsi);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvGender = findViewById(R.id.tvGender);
        tvNoTelp = findViewById(R.id.tvNoTelp);
        tvUmur = findViewById(R.id.tvUmur);
        tvAlamat = findViewById(R.id.tvAlamat);

        loadSavedProfile();

        // Set semua tombol edit
        findViewById(R.id.btnEditNama).setOnClickListener(v -> showEditDialog("Nama", tvNama));
        findViewById(R.id.btnEditDeskripsi).setOnClickListener(v -> showEditDialog("Deskripsi", tvDeskripsi));
        findViewById(R.id.btnEditUsername).setOnClickListener(v -> showEditDialog("Username", tvUsername));
        findViewById(R.id.btnEditEmail).setOnClickListener(v -> showEditDialog("Email", tvEmail));
        findViewById(R.id.btnEditGender).setOnClickListener(v -> showEditDialog("Jenis Kelamin", tvGender));
        findViewById(R.id.btnEditNoTelp).setOnClickListener(v -> showEditDialog("No Telp", tvNoTelp));
        findViewById(R.id.btnEditUmur).setOnClickListener(v -> showEditDialog("Umur", tvUmur));
        findViewById(R.id.btnEditAlamat).setOnClickListener(v -> showEditDialog("Alamat", tvAlamat));

        btnChangePhoto.setOnClickListener(v -> openImagePicker());

        btnSave.setOnClickListener(v -> {
            if (selectedImageUri == null) {
                String path = getSharedPreferences(currentUserKey, MODE_PRIVATE).getString("imageUri", null);
                if (path != null) {
                    File file = new File(path);
                    if (file.exists()) {
                        selectedImageUri = Uri.fromFile(file);
                    }
                }
            }
            saveToPreferences();
            Toast.makeText(this, "Data profil berhasil disimpan", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AccountActivity.class));
            finish();
        });
    }

    private void showEditDialog(String title, TextView textView) {
        EditText input = new EditText(this);
        input.setText(textView.getText().toString());
        new AlertDialog.Builder(this)
                .setTitle("Edit " + title)
                .setView(input)
                .setPositiveButton("Simpan", (dialog, which) -> textView.setText(input.getText().toString()))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Pilih Foto Profil"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri originalUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(originalUri);
                File copiedFile = new File(getFilesDir(), "profile_" + currentUsername + ".jpg");
                FileOutputStream outputStream = new FileOutputStream(copiedFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                selectedImageUri = Uri.fromFile(copiedFile);
                imgProfile.setImageURI(selectedImageUri);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveToPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences(currentUserKey, MODE_PRIVATE).edit();
        editor.putString("nama", tvNama.getText().toString());
        editor.putString("deskripsi", tvDeskripsi.getText().toString());
        editor.putString("username", tvUsername.getText().toString());
        editor.putString("email", tvEmail.getText().toString());
        editor.putString("gender", tvGender.getText().toString());
        editor.putString("notelp", tvNoTelp.getText().toString());
        editor.putString("umur", tvUmur.getText().toString());
        editor.putString("alamat", tvAlamat.getText().toString());

        if (selectedImageUri != null) {
            String path = selectedImageUri.getPath();
            editor.putString("imageUri", path);
            Log.d("EditProfil", "Path gambar disimpan: " + path);

            // Simpan path secara global (untuk akses di Chat dan Account)
            SharedPreferences globalPrefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
            globalPrefs.edit().putString(currentUsername + "_photo", path).apply();
        }

        editor.apply();
    }

    private void loadSavedProfile() {
        SharedPreferences prefs = getSharedPreferences(currentUserKey, MODE_PRIVATE);
        tvNama.setText(prefs.getString("nama", ""));
        tvDeskripsi.setText(prefs.getString("deskripsi", ""));
        tvUsername.setText(prefs.getString("username", ""));
        tvEmail.setText(prefs.getString("email", ""));
        tvGender.setText(prefs.getString("gender", ""));
        tvNoTelp.setText(prefs.getString("notelp", ""));
        tvUmur.setText(prefs.getString("umur", ""));
        tvAlamat.setText(prefs.getString("alamat", ""));

        String path = prefs.getString("imageUri", null);
        Log.d("EditProfil", "Path gambar di loadSavedProfile: " + path);
        if (path != null) {
            File file = new File(path);
            Log.d("EditProfil", "File exists? " + file.exists());
            if (file.exists()) {
                selectedImageUri = Uri.fromFile(file);
                imgProfile.setImageURI(selectedImageUri);
            } else {
                imgProfile.setImageResource(R.drawable.profile);
            }
        } else {
            imgProfile.setImageResource(R.drawable.profile);
        }
    }
}
