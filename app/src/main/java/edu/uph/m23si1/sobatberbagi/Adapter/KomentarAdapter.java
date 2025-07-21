package edu.uph.m23si1.sobatberbagi.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uph.m23si1.sobatberbagi.Model.Komentar;
import edu.uph.m23si1.sobatberbagi.R;

public class KomentarAdapter extends RecyclerView.Adapter<KomentarAdapter.ViewHolder> {

    private List<Komentar> komentarList;
    private OnKomentarActionListener listener;
    private String namaPenggunaLogin;

    // Interface listener untuk aksi hapus
    public interface OnKomentarActionListener {
        void onHapusClicked(int position);
    }

    // Konstruktor
    public KomentarAdapter(List<Komentar> komentarList, String namaPenggunaLogin, OnKomentarActionListener listener) {
        this.komentarList = komentarList;
        this.listener = listener;
        this.namaPenggunaLogin = namaPenggunaLogin;
    }

    // ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        RatingBar ratingBar;
        TextView tvKomentar, tvNama;
        ImageView btnMore;

        public ViewHolder(View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvKomentar = itemView.findViewById(R.id.tvKomentar);
            tvNama = itemView.findViewById(R.id.tvNama);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }

    @NonNull
    @Override
    public KomentarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_komentar, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull KomentarAdapter.ViewHolder holder, int position) {
        Komentar data = komentarList.get(position);

        holder.ratingBar.setRating(data.getRating());
        holder.tvKomentar.setText(data.getKomentar());
        holder.tvNama.setText(data.getNama());

        // Tampilkan tombol More hanya jika komentar milik user yang login
        if (data.getNama().equals(namaPenggunaLogin)) {
            holder.btnMore.setVisibility(View.VISIBLE);
            holder.btnMore.setOnClickListener(view -> {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), holder.btnMore);
                popupMenu.getMenu().add("Hapus");
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("Hapus")) {
                        if (listener != null) {
                            listener.onHapusClicked(position);
                        }
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            });
        } else {
            holder.btnMore.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return komentarList.size();
    }
}
