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

import edu.uph.m23si1.sobatberbagi.Model.ChatMessage;
import edu.uph.m23si1.sobatberbagi.R;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<ChatMessage> messageList;
    private final String currentUser;

    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    public ChatMessageAdapter(Context context, List<ChatMessage> messageList, String currentUser) {
        this.context = context;
        this.messageList = messageList;
        this.currentUser = currentUser;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messageList.get(position);
        return message.getSender().equals(currentUser) ? VIEW_TYPE_ME : VIEW_TYPE_OTHER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_ME) {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_me, parent, false);
            return new MeViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_other, parent, false);
            return new OtherViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        String avatarPath = message.getAvatarPath();

        if (holder instanceof MeViewHolder) {
            MeViewHolder vh = (MeViewHolder) holder;
            vh.tvMessage.setText(message.getMessage());
            loadAvatar(avatarPath, vh.imgAvatar);
        } else if (holder instanceof OtherViewHolder) {
            OtherViewHolder vh = (OtherViewHolder) holder;
            vh.tvMessage.setText(message.getMessage());
            loadAvatar(avatarPath, vh.imgAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // Fungsi untuk load avatar dengan Glide dan pengecekan file
    private void loadAvatar(String path, ImageView imgView) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                Glide.with(context)
                        .load(file)
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.profile)
                        .into(imgView);
                return;
            }
        }
        imgView.setImageResource(R.drawable.profile);
    }

    // ViewHolder untuk pesan saya
    static class MeViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        ImageView imgAvatar;

        public MeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }

    // ViewHolder untuk pesan orang lain
    static class OtherViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        ImageView imgAvatar;

        public OtherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}
