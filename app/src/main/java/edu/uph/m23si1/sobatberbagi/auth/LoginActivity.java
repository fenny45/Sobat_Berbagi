package edu.uph.m23si1.sobatberbagi.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.uph.m23si1.sobatberbagi.HomeActivity;
import edu.uph.m23si1.sobatberbagi.Model.User;
import edu.uph.m23si1.sobatberbagi.R;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvRegister;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Init Realm
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .allowWritesOnUiThread(true)
                .build();
        realm = Realm.getInstance(config);

        // Init view
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi semua data!", Toast.LENGTH_SHORT).show();
                return;
            }

            btnLogin.setEnabled(false);

            User user = realm.where(User.class)
                    .equalTo("username", username)
                    .equalTo("password", password)
                    .findFirst();

            if (user != null) {
                String nama = user.getNama();
                String usernameFix = user.getUsername();

                Toast.makeText(this, "Login berhasil. Selamat datang, " + nama, Toast.LENGTH_SHORT).show();

                //Simpan session login
                getSharedPreferences("LoginSession", MODE_PRIVATE)
                        .edit()
                        .putString("currentUser", usernameFix)
                        .apply();

                // Simpan nama untuk komentar/saran
                getSharedPreferences("UserPrefs", MODE_PRIVATE)
                        .edit()
                        .putString("namaPengguna", nama)
                        .apply();

                // (Opsional) Untuk tampilan home atau lainnya
                getSharedPreferences("sobatberbagi", MODE_PRIVATE)
                        .edit()
                        .putString("username", usernameFix)
                        .putString("nama", nama)
                        .apply();

                // Arahkan ke Home
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            }

        });

        // Pindah ke Registrasi
        tvRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegistrasiActivity.class));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) realm.close();
    }
}
