package com.example.demo.model;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AddressId implements Serializable {
    private static final long serialVersionUID = -369124973773894812L;
    @Column(name = "user_id", nullable = false, length = 16)
    private String userId;
    @Column(name = "address", nullable = false, length = 32)
    private String address;

    public AddressId(){};

    public AddressId(String userId,String address){
        this.userId=userId;
        this.address=address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AddressId entity = (AddressId) o;
        return Objects.equals(this.address, entity.address) &&
                Objects.equals(this.userId, entity.userId);
    }
}