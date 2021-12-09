package com.example.demo.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "address")
@Entity
public class Address {
    @EmbeddedId
    private AddressId id;

    public Address(){};

    public Address(String user_id,String address){
        this.id=new AddressId(user_id,address);
    }

    public AddressId getId() {
        return id;
    }

    public void setId(AddressId id) {
        this.id = id;
    }
}