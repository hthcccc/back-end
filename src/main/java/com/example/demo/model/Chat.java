package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Table(name = "chat")
@Entity
public class Chat {
    @Id
    @Column(name = "chat_id", nullable = false, length = 24)
    private String id;

    @Column(name = "sender_id", length = 16)
    private String senderId;

    @Column(name = "receiver_id", length = 16)
    private String receiverId;

    @Column(name = "text", length = 500)
    private String text;

    @Column(name = "time")
    private Instant time;

    @Column(name = "is_read", length = 2)
    private String isRead;

    @Column(name = "group_id", length = 32)
    private String group_id;

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}