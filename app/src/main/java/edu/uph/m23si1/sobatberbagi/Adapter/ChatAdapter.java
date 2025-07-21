package edu.uph.m23si1.sobatberbagi.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import edu.uph.m23si1.sobatberbagi.Model.Chat;
import edu.uph.m23si1.sobatberbagi.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public interface OnChatClickListener {
        void onClick(Chat chat);
    }

    private final Context context;
    private final List<Chat> chatList;
    private final OnChatClickListener listener;

    public ChatAdapter(Context context, List<Chat> chatList, OnChatClickListener listener) {
        this.context = context;
        this.chatList = chatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.tvNama.setText(chat.getNama());
        holder.tvPesanTerakhir.setText(chat.getPesanTerakhir());

        // Load foto profil lokal jika ada path
        if (!TextUtils.isEmpty(chat.getAvatarPath())) {
            File file = new File(chat.getAvatarPath());
            if (file.exists()) {
                Glide.with(context)
                        .load(file)
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.profile)
                        .into(holder.imgAvatar);
            } else {
                holder.imgAvatar.setImageResource(R.drawable.profile);
            }
        } else {
            holder.imgAvatar.setImageResource(R.drawable.profile);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(chat);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvPesanTerakhir;
        ImageView imgAvatar;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvPesanTerakhir = itemView.findViewById(R.id.tvPesanTerakhir);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}
