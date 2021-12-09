package com.example.demo.model;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class HistoryId implements Serializable {
    private static final long serialVersionUID = -4435367624455179312L;
    @Column(name = "user_id", nullable = false, length = 16)
    private String userId;
    @Column(name = "good_id", nullable = false, length = 16)
    private String goodId;

    public HistoryId(){};

    public HistoryId(String userId,String goodId){
        this.userId=userId;
        this.goodId=goodId;
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

    @Override
    public int hashCode() {
        return Objects.hash(goodId, userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        HistoryId entity = (HistoryId) o;
        return Objects.equals(this.goodId, entity.goodId) &&
                Objects.equals(this.userId, entity.userId);
    }
}