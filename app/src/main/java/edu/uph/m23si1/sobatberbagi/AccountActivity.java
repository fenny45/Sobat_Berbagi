package edu.uph.m23si1.sobatberbagi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;

import edu.uph.m23si1.sobatberbagi.auth.LoginActivity;

public class AccountActivity extends AppCompatActivity {

    private ImageView navHome, navInfo, navChat, navAccount, imgEdit, imgProfile;
    private TextView riwayat, ubahPassword, tvNama, tvDeskripsi;
    private MaterialButton btnLogout;

    private String currentUsername = "";
    private String currentUserKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Cek apakah user sudah login
        SharedPreferences loginPrefs = getSharedPreferences("LoginSession", MODE_PRIVATE);
        String currentUser = loginPrefs.getString("currentUser", null);

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        currentUsername = currentUser;
        currentUserKey = "UserProfile_" + currentUsername;

        // Inisialisasi view
        initViews();
        setupBottomNav();
        setupMenu();
        setupLogout();
    }

    private void initViews() {
        navHome = findViewById(R.id.navHome);
        navInfo = findViewById(R.id.navInfo);
        navChat = findViewById(R.id.navChat);
        navAccount = findViewById(R.id.navAccount);
        imgEdit = findViewById(R.id.imgEdit);
        imgProfile = findViewById(R.id.imgProfile);
        tvNama = findViewById(R.id.tvNama);
        tvDeskripsi = findViewById(R.id.tvDeskripsi);
        riwayat = findViewById(R.id.tvRiwayat);
        ubahPassword = findViewById(R.id.tvPassword);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupBottomNav() {
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        navInfo.setOnClickListener(v -> {
            startActivity(new Intent(this, SaranActivity.class));
            finish();
        });

        navChat.setOnClickListener(v -> {
            startActivity(new Intent(this, ChatActivity.class));
            finish();
        });

        navAccount.setOnClickListener(v -> {
            Toast.makeText(this, "Sudah di halaman Akun", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.navUpload).setOnClickListener(v -> {
            startActivity(new Intent(this, UploadActivity.class));
            finish();
        });
    }

    private void setupMenu() {
        imgEdit.setOnClickListener(v -> startActivity(new Intent(this, EditProfilActivity.class)));
        riwayat.setOnClickListener(v -> startActivity(new Intent(this, RiwayatActivity.class)));
        ubahPassword.setOnClickListener(v -> startActivity(new Intent(this, UbahPasswordActivity.class)));
    }

    private void setupLogout() {
        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("LoginSession", MODE_PRIVATE).edit().clear().apply();
            Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileFromPreferences();
    }

    private void loadProfileFromPreferences() {
        SharedPreferences prefs = getSharedPreferences(currentUserKey, MODE_PRIVATE);

        String nama = prefs.getString("nama", "Nama User");
        String deskripsi = prefs.getString("deskripsi", "Deskripsi belum diisi");

        tvNama.setText(nama);
        tvDeskripsi.setText(deskripsi);

        String path = prefs.getString("imageUri", null);
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                Glide.with(this)
                        .load(Uri.fromFile(file))
                        .placeholder(R.drawable.profile)
                        .into(imgProfile);
            } else {
                imgProfile.setImageResource(R.drawable.profile);
            }
        } else {
            imgProfile.setImageResource(R.drawable.profile);
        }
    }
}
