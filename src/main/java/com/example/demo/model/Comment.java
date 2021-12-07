package com.example.demo.model;

import javax.persistence.*;
import java.time.Instant;

@Table(name = "comment")
@Entity
public class Comment {
    @EmbeddedId
    private CommentId id;

    @Column(name = "user_id", nullable = false, length = 16)
    private String userId;

    @Column(name = "good_id", nullable = false, length = 16)
    private String goodId;

    @Column(name = "text", length = 100)
    private String text;

    @Column(name = "date")
    private Instant date;

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

    public String getGoodId() {
        return goodId;
    }

    public void setGoodId(String goodId) {
        this.goodId = goodId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CommentId getId() {
        return id;
    }

    public void setId(CommentId id) {
        this.id = id;
    }
}