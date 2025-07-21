package edu.uph.m23si1.sobatberbagi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

import edu.uph.m23si1.sobatberbagi.Adapter.ChatAdapter;
import edu.uph.m23si1.sobatberbagi.Model.Chat;
import edu.uph.m23si1.sobatberbagi.auth.LoginActivity;

public class ChatActivity extends AppCompatActivity {

    private EditText etSearchChat;
    private RecyclerView rvChatList;
    private ChatAdapter chatAdapter;

    private final ArrayList<Chat> chatList = new ArrayList<>();
    private final ArrayList<Chat> filteredList = new ArrayList<>();

    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SharedPreferences loginPrefs = getSharedPreferences("LoginSession", MODE_PRIVATE);
        currentUser = loginPrefs.getString("currentUser", null);

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        loadChatData();
        setupRecyclerView();
        setupSearchBar();
        setupBottomNav();
    }

    private void initViews() {
        etSearchChat = findViewById(R.id.etSearchChat);
        rvChatList = findViewById(R.id.rvChatList);
        rvChatList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadChatData() {
        chatList.clear();
        SharedPreferences prefs = getSharedPreferences("ChatData", MODE_PRIVATE);
        Map<String, ?> allChats = prefs.getAll();

        for (Map.Entry<String, ?> entry : allChats.entrySet()) {
            String key = entry.getKey();
            String value = (String) entry.getValue();

            if (!key.contains("_chat_")) continue;

            String[] users = key.replace("_chat_", "|").split("\\|");
            if (users.length != 2) continue;

            String user1 = users[0];
            String user2 = users[1];

            String otherUser = currentUser.equals(user1) ? user2 : currentUser.equals(user2) ? user1 : null;
            if (otherUser == null) continue;

            String[] messages = value.split(";;");
            if (messages.length == 0) continue;

            String[] lastParts = messages[messages.length - 1].split("\\|", 2);
            String lastMsg = (lastParts.length == 2) ? lastParts[1] : "";

            // Ambil foto profil
            SharedPreferences globalPrefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
            String profilePath = globalPrefs.getString(otherUser + "_photo", null);

            chatList.add(new Chat(otherUser, lastMsg, profilePath));
        }

        filteredList.clear();
        filteredList.addAll(chatList);
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(this, filteredList, chat -> {
            Intent intent = new Intent(ChatActivity.this, ChatDetailActivity.class);
            intent.putExtra("penerimaUsername", chat.getNama());
            startActivity(intent);
        });
        rvChatList.setAdapter(chatAdapter);
    }

    private void setupSearchBar() {
        etSearchChat.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterChat(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterChat(String keyword) {
        filteredList.clear();
        for (Chat chat : chatList) {
            if (chat.getNama().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(chat);
            }
        }
        chatAdapter.notifyDataSetChanged();
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        findViewById(R.id.navUpload).setOnClickListener(v -> {
            startActivity(new Intent(this, UploadActivity.class));
            finish();
        });

        findViewById(R.id.navChat).setOnClickListener(v -> {
            // Sudah di halaman Chat
        });

        findViewById(R.id.navAccount).setOnClickListener(v -> {
            startActivity(new Intent(this, AccountActivity.class));
            finish();
        });

        findViewById(R.id.navInfo).setOnClickListener(v -> {
            startActivity(new Intent(this, SaranActivity.class));
            finish();
        });
    }
}
