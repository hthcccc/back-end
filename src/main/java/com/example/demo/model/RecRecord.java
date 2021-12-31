package com.example.demo.model;

import javax.persistence.*;

@Table(name = "recrecord")
@Entity
public class RecRecord {
    @Id
    @Column(name="id")
    private String id;

    @Column(name="good_id")
    private String good_id;

    @Column(name="rec_id")
    private String rec_id;

    @Column(name="name")
    private String name;

    @Column(name="start_time")
    private Integer start_time;

    @Column(name="state")
    private String state;


}
