package com.example.demo.model;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {

  @Id
  @Column(name = "user_id")
  private String userId;
  @Column(name = "name")
  private String name;
  @Column(name = "age")
  private Integer age;
  @Column(name = "sex")
  private String sex;
  @Column(name = "mail")
  private String mail;
  @Column(name = "phone")
  private String phone;
  @Column(name = "password")
  private String password;
  @Column(name = "salt")
  private String salt;
  @Column(name = "credit")
  private double credit;
  @Column(name = "balance")
  private double balance;


  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }


  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }


  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getSalt() {
    return salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  public double getCredit() {
    return credit;
  }

  public void setCredit(double credit) {
    this.credit = credit;
  }


  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

}
