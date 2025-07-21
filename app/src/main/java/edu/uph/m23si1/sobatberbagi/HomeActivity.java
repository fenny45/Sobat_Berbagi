package edu.uph.m23si1.sobatberbagi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

import edu.uph.m23si1.sobatberbagi.Adapter.MakananAdapter;
import edu.uph.m23si1.sobatberbagi.Model.Makanan;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvMakanan;
    private TextView tvKosong, tvWelcome;
    private EditText etCariMakanan, etCariLokasi;
    private MakananAdapter adapter;
    private Realm realm;
    private RealmResults<Makanan> makananList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Init Realm
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .allowWritesOnUiThread(true)
                .build();
        realm = Realm.getInstance(config);

        // Init view
        rvMakanan = findViewById(R.id.rvMakanan);
        tvKosong = findViewById(R.id.tvKosong);
        tvWelcome = findViewById(R.id.tvWelcome);
        etCariMakanan = findViewById(R.id.etCariMakanan);
        etCariLokasi = findViewById(R.id.etCariLokasi);

        //Ambil username dari SharedPreferences
        String username = getSharedPreferences("LoginSession", MODE_PRIVATE)
                .getString("currentUser", "User");
        tvWelcome.setText("Selamat Datang, " + username + "!");

        rvMakanan.setLayoutManager(new LinearLayoutManager(this));
        loadData();

        // Filter makanan
        etCariMakanan.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Filter lokasi
        etCariLokasi.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Setup navigasi bawah
        setupBottomNav();
    }

    private void loadData() {
        makananList = realm.where(Makanan.class).findAll();
        adapter = new MakananAdapter(this, makananList, this::hapusMakanan);
        rvMakanan.setAdapter(adapter);
        updateKosongView(makananList.isEmpty());
        adapter.setOnDetailClickListener(this::tampilkanDetailMakanan);

    }

    private void filterData() {
        String nama = etCariMakanan.getText().toString().trim();
        String lokasi = etCariLokasi.getText().toString().trim();

        RealmResults<Makanan> filtered = realm.where(Makanan.class)
                .contains("namaMakanan", nama, Case.INSENSITIVE)
                .contains("lokasi", lokasi, Case.INSENSITIVE)
                .findAll();

        adapter.updateData(filtered);
        updateKosongView(filtered.isEmpty());
    }

    private void updateKosongView(boolean isKosong) {
        tvKosong.setVisibility(isKosong ? View.VISIBLE : View.GONE);
        rvMakanan.setVisibility(isKosong ? View.GONE : View.VISIBLE);
    }

    private void setupBottomNav() {
        CardView navUpload = findViewById(R.id.navUpload);
        ImageView navHome = findViewById(R.id.navHome);
        ImageView navChat = findViewById(R.id.navChat);
        ImageView navInfo = findViewById(R.id.navInfo);
        ImageView navAccount = findViewById(R.id.navAcccount);

        if (navUpload != null) {
            navUpload.setOnClickListener(v -> startActivity(new Intent(this, UploadActivity.class)));
        }

        if (navHome != null) {
            navHome.setOnClickListener(v -> rvMakanan.scrollToPosition(0));
        }

        if (navChat != null) {
            navChat.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        }

        if (navInfo != null) {
            navInfo.setOnClickListener(v -> startActivity(new Intent(this, SaranActivity.class)));
        }

        if (navAccount != null) {
            navAccount.setOnClickListener(v -> startActivity(new Intent(this, AccountActivity.class)));
        } else {
            System.out.println("navAccount tidak ditemukan. Cek ID-nya di XML.");
        }
    }

    private void tampilkanDetailMakanan(Makanan makanan) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 10);

        ImageView img = new ImageView(this);
        img.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 600));
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (makanan.getGambarPath() != null) {
            img.setImageURI(Uri.fromFile(new File(makanan.getGambarPath())));
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


    public void hapusMakanan(String id) {
        realm.executeTransaction(r -> {
            Makanan makanan = r.where(Makanan.class).equalTo("id", id).findFirst();
            if (makanan != null) makanan.deleteFromRealm();
        });
        filterData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) realm.close();
    }
}
