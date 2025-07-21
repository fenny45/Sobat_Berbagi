package edu.uph.m23si1.sobatberbagi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.uph.m23si1.sobatberbagi.Adapter.KomentarAdapter;
import edu.uph.m23si1.sobatberbagi.Model.Komentar;

public class Saran2Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    KomentarAdapter komentarAdapter;
    ArrayList<Komentar> listKomentar;
    SharedPreferences prefs;
    String namaPengguna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saran2);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Ambil nama pengguna dari SharedPreferences yang digunakan saat login
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        namaPengguna = userPrefs.getString("namaPengguna", "-Anonim");

        listKomentar = new ArrayList<>();
        prefs = getSharedPreferences("KomentarPrefs", MODE_PRIVATE);
        Set<String> komentarSet = prefs.getStringSet("komentarSet", null);

        if (komentarSet != null) {
            for (String data : komentarSet) {
                String[] parts = data.split("\\|");
                if (parts.length == 3) {
                    float rating = Float.parseFloat(parts[0]);
                    String komentar = parts[1];
                    String nama = parts[2];
                    listKomentar.add(new Komentar(rating, komentar, nama));
                }
            }
        }

        // Kirim nama pengguna ke adapter untuk validasi hak hapus
        komentarAdapter = new KomentarAdapter(listKomentar, namaPengguna, position -> {
            Komentar komentar = listKomentar.get(position);
            if (komentar.getNama().equals(namaPengguna)) {
                listKomentar.remove(position);
                komentarAdapter.notifyItemRemoved(position);
                Toast.makeText(Saran2Activity.this, "Komentar dihapus", Toast.LENGTH_SHORT).show();

                // Simpan ulang ke SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                ArrayList<String> simpanList = new ArrayList<>();
                for (Komentar k : listKomentar) {
                    simpanList.add(k.getRating() + "|" + k.getKomentar() + "|" + k.getNama());
                }
                editor.putStringSet("komentarSet", new HashSet<>(simpanList));
                editor.apply();
            } else {
                Toast.makeText(Saran2Activity.this, "Hanya komentar Anda yang bisa dihapus", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(komentarAdapter);
    }
}
