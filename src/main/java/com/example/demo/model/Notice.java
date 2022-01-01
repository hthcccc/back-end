package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Table(name = "notice")
@Entity
public class Notice {
    @Id
    @Column(name = "notice_id", nullable = false, length = 24)
    private String id;

    @Column(name = "sender_id", nullable = false, length = 16)
    private String sender_id;

    @Column(name = "receiver_id", nullable = false, length = 16)
    private String receiver_id;

    @Column(name = "topic",length = 30)
    private String topic;

    @Column(name = "text", length = 100)
    private String text;

    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "isread", nullable = false, length = 1)
    private String isread;

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getIsread() {
        return isread;
    }

    public void setIsread(String isread) {
        this.isread = isread;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getReceiverId() {
        return receiver_id;
    }

    public void setReceiverId(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getSenderId() {
        return sender_id;
    }

    public void setSenderId(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}