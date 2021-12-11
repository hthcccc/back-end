package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "notice")
@Entity
public class Notice {
    @EmbeddedId
    private NoticeId id;

    @Column(name = "text", length = 100)
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public NoticeId getId() {
        return id;
    }

    public void setId(NoticeId id) {
        this.id = id;
    }
}