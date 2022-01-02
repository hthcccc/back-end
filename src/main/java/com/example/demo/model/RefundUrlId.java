package com.example.demo.model;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RefundUrlId implements Serializable {
    private static final long serialVersionUID = 4190027455074785937L;
    @Column(name = "order_id", nullable = false, length = 16)
    private String orderId;
    @Column(name = "url", nullable = false, length = 100)
    private String url;

    public RefundUrlId() {
    }

    public RefundUrlId(String orderId, String url) {
        this.orderId = orderId;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, url);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RefundUrlId entity = (RefundUrlId) o;
        return Objects.equals(this.orderId, entity.orderId) &&
                Objects.equals(this.url, entity.url);
    }
}