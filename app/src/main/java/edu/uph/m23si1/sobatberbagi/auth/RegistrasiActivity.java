package edu.uph.m23si1.sobatberbagi.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import edu.uph.m23si1.sobatberbagi.HomeActivity;
import edu.uph.m23si1.sobatberbagi.Model.User;
import edu.uph.m23si1.sobatberbagi.R;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RegistrasiActivity extends AppCompatActivity {

    private EditText etNama, etUsername, etEmail, etPassword, etUlangiPassword, etUmur, etAlamat;
    private RadioGroup rgGender;
    private RadioButton rbLaki, rbPerempuan;
    private Button btnLogin;
    private TextView tvLogin;
    private ImageView backArrow;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .allowWritesOnUiThread(true)
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);

        etNama = findViewById(R.id.etNama);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etUlangiPassword = findViewById(R.id.etUlangiPassword);
        etUmur = findViewById(R.id.etUmur);
        etAlamat = findViewById(R.id.etAlamat);
        rgGender = findViewById(R.id.rgGender);
        rbLaki = findViewById(R.id.rbLaki);
        rbPerempuan = findViewById(R.id.rbPerempuan);
        btnLogin = findViewById(R.id.btnLogin);
        tvLogin = findViewById(R.id.tvLogin);
        backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(v -> finish());
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnLogin.setOnClickListener(v -> prosesRegistrasi());
    }

    private void prosesRegistrasi() {
        String nama = etNama.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String ulangiPassword = etUlangiPassword.getText().toString().trim();
        String umurStr = etUmur.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();

        if (nama.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()
                || ulangiPassword.isEmpty() || umurStr.isEmpty() || alamat.isEmpty()
                || rgGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Isi semua data", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(ulangiPassword)) {
            Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT).show();
            return;
        }

        int umur;
        try {
            umur = Integer.parseInt(umurStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Umur harus angka", Toast.LENGTH_SHORT).show();
            return;
        }

        User existingUser = realm.where(User.class).equalTo("username", username).findFirst();
        if (existingUser != null) {
            Toast.makeText(this, "Username sudah digunakan", Toast.LENGTH_SHORT).show();
            return;
        }

        String gender = rbLaki.isChecked() ? "Laki-Laki" : "Perempuan";

        // Simpan ke Realm
        realm.executeTransaction(r -> {
            User user = r.createObject(User.class, username);
            user.setNama(nama);
            user.setEmail(email);
            user.setPassword(password);
            user.setGender(gender);
            user.setUmur(umur);
            user.setAlamat(alamat);
        });

        // Simpan data profil ke SharedPreferences
        String profileKey = "UserProfile_" + username;
        SharedPreferences.Editor editor = getSharedPreferences(profileKey, MODE_PRIVATE).edit();
        editor.putString("nama", nama);
        editor.putString("username", username);
        editor.putString("email", email);
        editor.putString("gender", gender);
        editor.putString("umur", String.valueOf(umur));
        editor.putString("alamat", alamat);
        editor.putString("deskripsi", "");
        editor.putString("notelp", "");
        editor.apply();

        //Simpan session login
        getSharedPreferences("LoginSession", MODE_PRIVATE)
                .edit()
                .putString("currentUser", username)
                .apply();

        // Simpan nama pengguna (untuk komentar, dll)
        getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .edit()
                .putString("namaPengguna", nama)
                .apply();

        //  untuk menampilkan nama di Home
        getSharedPreferences("sobatberbagi", MODE_PRIVATE)
                .edit()
                .putString("username", username)
                .putString("nama", nama)
                .apply();

        Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}
