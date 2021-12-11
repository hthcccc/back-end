package com.example.demo.model;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Embeddable
public class NoticeId implements Serializable {
    private static final long serialVersionUID = 1416769148555008026L;
    @Column(name = "user_id", nullable = false, length = 16)
    private String userId;
    @Column(name = "date", nullable = false)
    private Instant date;

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        NoticeId entity = (NoticeId) o;
        return Objects.equals(this.date, entity.date) &&
                Objects.equals(this.userId, entity.userId);
    }
}