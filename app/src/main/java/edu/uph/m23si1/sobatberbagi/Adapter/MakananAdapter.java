package edu.uph.m23si1.sobatberbagi.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;

import edu.uph.m23si1.sobatberbagi.ChatDetailActivity;
import edu.uph.m23si1.sobatberbagi.Model.Makanan;
import edu.uph.m23si1.sobatberbagi.R;
import io.realm.RealmResults;

public class MakananAdapter extends RecyclerView.Adapter<MakananAdapter.MakananViewHolder> {

    private final Context context;
    private RealmResults<Makanan> makananList;
    private final OnDeleteListener deleteListener;

    public interface OnDetailClickListener {
        void onDetailClick(Makanan makanan);
    }
    private OnDetailClickListener detailClickListener;
    public void setOnDetailClickListener(OnDetailClickListener listener) {
        this.detailClickListener = listener;
    }


    public interface OnDeleteListener {
        void onDelete(String id);
    }

    public MakananAdapter(Context context, RealmResults<Makanan> makananList, OnDeleteListener deleteListener) {
        this.context = context;
        this.makananList = makananList;
        this.deleteListener = deleteListener;
    }

    public void updateData(RealmResults<Makanan> newData) {
        this.makananList = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MakananViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_makanan, parent, false);
        return new MakananViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MakananViewHolder holder, int position) {
        Makanan makanan = makananList.get(position);
        if (makanan == null) return;

        holder.tvNamaMakanan.setText(makanan.getNamaMakanan());
        holder.tvLokasi.setText("Lokasi: " + makanan.getLokasi());
        holder.tvBatasWaktu.setText("Batas: " + makanan.getBatasWaktu());

        // Load gambar dari path lokal
        String path = makanan.getGambarPath();
        if (path != null) {
            File imgFile = new File(path);
            if (imgFile.exists()) {
                try {
                    Uri imageUri = Uri.fromFile(imgFile);
                    ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), imageUri);
                    Drawable drawable = ImageDecoder.decodeDrawable(source);
                    holder.imgMakanan.setImageDrawable(drawable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        SharedPreferences prefs = context.getSharedPreferences("LoginSession", Context.MODE_PRIVATE);
        String currentUser = prefs.getString("currentUser", null);
        String pengunggah = makanan.getUsernamePengunggah(); // Pastikan field ini ada di model Makanan

        // Sembunyikan tombol hapus jika bukan makanan milik user
        if (currentUser != null && !currentUser.equals(pengunggah)) {
            holder.btnHapus.setVisibility(View.GONE);
        } else {
            holder.btnHapus.setVisibility(View.VISIBLE);
        }

        holder.btnHapus.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(makanan.getId());
            }
        });

        holder.btnLihatDetail.setOnClickListener(v -> {
            if (detailClickListener != null) {
                detailClickListener.onDetailClick(makanan);
            }
        });


        holder.btnHubungi.setOnClickListener(v -> {
            if (currentUser != null && pengunggah != null && !currentUser.equals(pengunggah)) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("penerimaUsername", pengunggah);
                context.startActivity(intent);
            } else if (currentUser.equals(pengunggah)) {
                Toast.makeText(context, "Tidak bisa chat dengan diri sendiri", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return makananList.size();
    }

    static class MakananViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaMakanan, tvLokasi, tvBatasWaktu;
        ImageView imgMakanan, btnHapus;
        Button btnLihatDetail, btnHubungi;

        public MakananViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaMakanan = itemView.findViewById(R.id.tvNamaMakanan);
            tvLokasi = itemView.findViewById(R.id.tvLokasi);
            tvBatasWaktu = itemView.findViewById(R.id.tvBatasWaktu);
            imgMakanan = itemView.findViewById(R.id.imgMakanan);
            btnHapus = itemView.findViewById(R.id.btnHapus);
            btnLihatDetail = itemView.findViewById(R.id.btnLihatDetail);
            btnHubungi = itemView.findViewById(R.id.btnHubungi);
        }
    }
}
