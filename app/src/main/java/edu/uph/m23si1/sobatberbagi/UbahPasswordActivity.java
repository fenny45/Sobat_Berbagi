package edu.uph.m23si1.sobatberbagi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;

import edu.uph.m23si1.sobatberbagi.Model.User;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class UbahPasswordActivity extends AppCompatActivity {

    private EditText etPasswordLama, etPasswordBaru, etKonfirmasiPassword;
    private MaterialButton btnSimpanPassword;
    private ImageView btnBack;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_password);

        // Inisialisasi Realm
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .allowWritesOnUiThread(true)
                .build();
        realm = Realm.getInstance(config);

        // Inisialisasi View
        etPasswordLama = findViewById(R.id.etPasswordLama);
        etPasswordBaru = findViewById(R.id.etPasswordBaru);
        etKonfirmasiPassword = findViewById(R.id.etKonfirmasiPassword);
        btnSimpanPassword = findViewById(R.id.btnSimpanPassword);
        btnBack = findViewById(R.id.btnBack);

        // Tombol kembali
        btnBack.setOnClickListener(v -> finish());

        // Tombol Simpan
        btnSimpanPassword.setOnClickListener(v -> {
            String lama = etPasswordLama.getText().toString().trim();
            String baru = etPasswordBaru.getText().toString().trim();
            String konfirmasi = etKonfirmasiPassword.getText().toString().trim();

            if (TextUtils.isEmpty(lama) || TextUtils.isEmpty(baru) || TextUtils.isEmpty(konfirmasi)) {
                Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!baru.equals(konfirmasi)) {
                Toast.makeText(this, "Password baru dan konfirmasi tidak cocok!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ambil username yang login dari SharedPreferences
            SharedPreferences prefs = getSharedPreferences("sobatberbagi", MODE_PRIVATE);
            String currentUsername = prefs.getString("username", null);

            if (currentUsername != null) {
                realm.executeTransaction(r -> {
                    User user = r.where(User.class)
                            .equalTo("username", currentUsername)
                            .findFirst();

                    if (user != null) {
                        if (!user.getPassword().equals(lama)) {
                            Toast.makeText(this, "Password lama salah!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        user.setPassword(baru);
                        Toast.makeText(this, "Password berhasil diubah!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "User tidak ditemukan!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Gagal mengambil data pengguna!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) realm.close();
    }
}
