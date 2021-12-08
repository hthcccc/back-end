package com.example.demo.model;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SubscribeId implements Serializable {
    private static final long serialVersionUID = 8037441412676303647L;
    @Column(name = "user_id", nullable = false, length = 16)
    private String userId;
    @Column(name = "subscribed_id", nullable = false, length = 16)
    private String subscribedId;

    public String getSubscribedId() {
        return subscribedId;
    }

    public void setSubscribedId(String subscribedId) {
        this.subscribedId = subscribedId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, subscribedId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SubscribeId entity = (SubscribeId) o;
        return Objects.equals(this.userId, entity.userId) &&
                Objects.equals(this.subscribedId, entity.subscribedId);
    }
}