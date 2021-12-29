package com.example.demo.model;

import javax.persistence.*;

@Table(name = "admin", indexes = {
        @Index(name = "mail", columnList = "mail", unique = true)
})
@Entity
public class Admin {
    @Id
    @Column(name = "admin_id", nullable = false, length = 16)
    private String id;

    @Column(name = "name", length = 8)
    private String name;

    @Column(name = "age")
    private Integer age;

    @Lob
    @Column(name = "sex")
    private String sex;

    @Column(name = "phone")
    private String phone;

    @Column(name = "mail", nullable = false, length = 32)
    private String mail;

    @Column(name = "password", length = 32)
    private String password;

    @Column(name = "salt", length = 24)
    private String salt;

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}