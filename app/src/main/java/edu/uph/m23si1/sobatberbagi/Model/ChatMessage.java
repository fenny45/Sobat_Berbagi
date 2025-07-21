package edu.uph.m23si1.sobatberbagi.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ChatMessage extends RealmObject {

    @PrimaryKey
    private String id;

    private String sender;       // username pengirim
    private String receiver;     // username penerima
    private String message;
    private long timestamp;

    private String avatarPath;

    // Konstruktor kosong untuk Realm
    public ChatMessage() {
    }

    // Konstruktor tambahan (jika mau pakai manual)
    public ChatMessage(String message, String sender, String receiver) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = System.currentTimeMillis();
    }

    // Getter & Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
}
