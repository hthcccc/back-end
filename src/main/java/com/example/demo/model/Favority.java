package com.example.demo.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "favority")
@Entity
public class Favority {
    @EmbeddedId
    private FavorityId id;

    public FavorityId getId() {
        return id;
    }

    public Favority(){};

    public Favority(String user_id,String good_id){
        this.id=new FavorityId(user_id,good_id);
    }

    public Favority(FavorityId id) {
        this.id = id;
    }

    public void setId(FavorityId id) {
        this.id = id;
    }
}