package edu.uph.m23si1.sobatberbagi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.uph.m23si1.sobatberbagi.Model.Komentar;

public class SaranActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText etSaran;
    private Button btnKirim;
    private LinearLayout layoutKomentar;
    private View cardSaranPenggunaLain;

    private ArrayList<Komentar> semuaKomentar = new ArrayList<>();
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saran);

        ratingBar = findViewById(R.id.ratingBar);
        etSaran = findViewById(R.id.etSaran);
        btnKirim = findViewById(R.id.btnKirim);
        layoutKomentar = findViewById(R.id.layoutKomentarContainer);
        cardSaranPenggunaLain = findViewById(R.id.cardSaranPenggunaLain);

        ImageView navHome = findViewById(R.id.navHome);
        ImageView navInfo = findViewById(R.id.navInfo);
        ImageView navChat = findViewById(R.id.navChat);
        ImageView navAcccount = findViewById(R.id.navAcccount);
        CardView navUpload = findViewById(R.id.navUpload);

        // Setup tombol navigasi
        navHome.setOnClickListener(v -> startActivity(new Intent(SaranActivity.this, HomeActivity.class)));
        navInfo.setOnClickListener(v -> startActivity(new Intent(SaranActivity.this, SaranActivity.class)));
        navChat.setOnClickListener(v -> startActivity(new Intent(SaranActivity.this, ChatActivity.class)));
        navAcccount.setOnClickListener(v -> startActivity(new Intent(SaranActivity.this, AccountActivity.class)));
        navUpload.setOnClickListener(v -> startActivity(new Intent(SaranActivity.this, UploadActivity.class)));


        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String namaPengguna = userPrefs.getString("namaPengguna", "-Anonim");

        // Ambil komentar yang sudah disimpan
        prefs = getSharedPreferences("KomentarPrefs", MODE_PRIVATE);
        Set<String> komentarSet = prefs.getStringSet("komentarSet", null);
        if (komentarSet != null) {
            for (String data : komentarSet) {
                String[] parts = data.split("\\|");
                if (parts.length == 3) {
                    float rating = Float.parseFloat(parts[0]);
                    String komentar = parts[1];
                    String nama = parts[2];
                    Komentar k = new Komentar(rating, komentar, nama);
                    semuaKomentar.add(k);
                    layoutKomentar.addView(buatKomentarView(k));
                }
            }
        }

        // Kirim komentar
        btnKirim.setOnClickListener(view -> {
            String saranText = etSaran.getText().toString().trim();
            float ratingValue = ratingBar.getRating();

            if (!saranText.isEmpty() && ratingValue > 0) {
                Komentar komentar = new Komentar(ratingValue, saranText, namaPengguna);
                semuaKomentar.add(komentar);
                layoutKomentar.addView(buatKomentarView(komentar));

                // Simpan komentar ke SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                ArrayList<String> simpanList = new ArrayList<>();
                for (Komentar k : semuaKomentar) {
                    simpanList.add(k.getRating() + "|" + k.getKomentar() + "|" + k.getNama());
                }
                editor.putStringSet("komentarSet", new HashSet<>(simpanList));
                editor.apply();

                etSaran.setText("");
                ratingBar.setRating(0);
            }
        });

        // Buka daftar komentar pengguna lain
        if (cardSaranPenggunaLain != null) {
            cardSaranPenggunaLain.setOnClickListener(view -> {
                Intent intent = new Intent(SaranActivity.this, Saran2Activity.class);
                intent.putParcelableArrayListExtra("dataKomentar", semuaKomentar);
                startActivity(intent);
            });
        }
    }

    private View buatKomentarView(Komentar model) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_komentar, layoutKomentar, false);

        RatingBar rb = view.findViewById(R.id.ratingBar);
        TextView tvKomentar = view.findViewById(R.id.tvKomentar);
        TextView tvNama = view.findViewById(R.id.tvNama);

        rb.setRating(model.getRating());
        tvKomentar.setText(model.getKomentar());
        tvNama.setText(model.getNama());

        return view;
    }
}
