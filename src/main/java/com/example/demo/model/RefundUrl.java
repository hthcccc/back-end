package com.example.demo.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "refund_url")
@Entity
public class RefundUrl {
    @EmbeddedId
    private RefundUrlId id;

    public RefundUrlId getId() {
        return id;
    }

    public void setId(RefundUrlId id) {
        this.id = id;
    }
}