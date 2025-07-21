package edu.uph.m23si1.sobatberbagi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import edu.uph.m23si1.sobatberbagi.Adapter.ChatMessageAdapter;
import edu.uph.m23si1.sobatberbagi.Model.ChatMessage;

public class ChatDetailActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText etMessage;
    private ImageView btnSend, imgBack;
    private TextView tvNamaPengguna;

    private ArrayList<ChatMessage> messageList;
    private ChatMessageAdapter adapter;

    private String currentUser;
    private String otherUser;
    private String chatKey;

    private String currentUserAvatarPath = "";
    private String otherUserAvatarPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        // Inisialisasi view
        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        imgBack = findViewById(R.id.backButton);
        tvNamaPengguna = findViewById(R.id.tvNamaPengguna);

        // Ambil username dari SharedPreferences
        SharedPreferences loginPrefs = getSharedPreferences("LoginSession", MODE_PRIVATE);
        currentUser = loginPrefs.getString("currentUser", "me");

        // Ambil username lawan bicara dari intent
        otherUser = getIntent().getStringExtra("penerimaUsername");
        if (TextUtils.isEmpty(otherUser)) otherUser = "Pengguna";

        tvNamaPengguna.setText(otherUser);
        chatKey = generateChatKey(currentUser, otherUser);

        // Ambil path avatar masing-masing user
        currentUserAvatarPath = getSharedPreferences("UserProfile_" + currentUser, MODE_PRIVATE)
                .getString("imageUri", "");
        otherUserAvatarPath = getSharedPreferences("UserProfile_" + otherUser, MODE_PRIVATE)
                .getString("imageUri", "");

        // Tampilkan avatar di tombol back (jika ada)
        File otherAvatarFile = new File(otherUserAvatarPath);
        if (otherAvatarFile.exists()) {
            Glide.with(this)
                    .load(otherAvatarFile)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(imgBack);
        } else {
            imgBack.setImageResource(R.drawable.profile);
        }

        // Load chat message
        messageList = loadMessages();

        // Set adapter dan RecyclerView
        adapter = new ChatMessageAdapter(this, messageList, currentUser);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);
        rvChat.scrollToPosition(messageList.size() - 1);

        // Kirim pesan
        btnSend.setOnClickListener(v -> {
            String pesan = etMessage.getText().toString().trim();
            if (!pesan.isEmpty()) {
                ChatMessage newMessage = new ChatMessage();
                newMessage.setId(String.valueOf(System.currentTimeMillis()));
                newMessage.setSender(currentUser);
                newMessage.setReceiver(otherUser);
                newMessage.setMessage(pesan);
                newMessage.setTimestamp(System.currentTimeMillis());
                newMessage.setAvatarPath(currentUserAvatarPath);

                messageList.add(newMessage);
                saveMessages();
                adapter.notifyItemInserted(messageList.size() - 1);
                rvChat.scrollToPosition(messageList.size() - 1);
                etMessage.setText("");
            }
        });

        imgBack.setOnClickListener(v -> finish());
    }

    // Buat key unik berdasarkan dua user
    private String generateChatKey(String user1, String user2) {
        return user1.compareTo(user2) < 0
                ? user1 + "_chat_" + user2
                : user2 + "_chat_" + user1;
    }

    // Simpan pesan ke SharedPreferences
    private void saveMessages() {
        StringBuilder sb = new StringBuilder();
        for (ChatMessage msg : messageList) {
            sb.append(msg.getSender())
                    .append("|").append(msg.getReceiver())
                    .append("|").append(msg.getMessage())
                    .append("|").append(msg.getTimestamp())
                    .append("|").append(msg.getAvatarPath() != null ? msg.getAvatarPath() : "")
                    .append(";;");
        }

        SharedPreferences.Editor editor = getSharedPreferences("ChatData", MODE_PRIVATE).edit();
        editor.putString(chatKey, sb.toString());
        editor.apply();
    }

    // Ambil pesan dari SharedPreferences
    private ArrayList<ChatMessage> loadMessages() {
        ArrayList<ChatMessage> list = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences("ChatData", MODE_PRIVATE);
        String allMessages = prefs.getString(chatKey, "");

        if (!TextUtils.isEmpty(allMessages)) {
            String[] pairs = allMessages.split(";;");
            for (String pair : pairs) {
                String[] parts = pair.split("\\|");
                if (parts.length >= 3) {
                    ChatMessage msg = new ChatMessage();
                    msg.setSender(parts[0]);
                    msg.setReceiver(parts[1]);
                    msg.setMessage(parts[2]);

                    // Timestamp
                    if (parts.length >= 4) {
                        try {
                            msg.setTimestamp(Long.parseLong(parts[3]));
                        } catch (NumberFormatException e) {
                            msg.setTimestamp(System.currentTimeMillis());
                        }
                    }

                    // Avatar
                    if (parts.length >= 5) {
                        msg.setAvatarPath(parts[4]);
                    } else {
                        msg.setAvatarPath(parts[0].equals(currentUser)
                                ? currentUserAvatarPath
                                : otherUserAvatarPath);
                    }

                    list.add(msg);
                }
            }
        }
        return list;
    }
}
