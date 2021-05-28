package com.study.web.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * класс описывающий сущность карты в базе данных
 */
public class Card {
    private static final Set<Card> setCards = new HashSet<>();

    private int id;//id карты
    private String number;//номер карты
    private int userId;//id клиента банка из базы данных внешний ключ
    private String expData;//дата окончания действия карты
    private String cvv;//cvv код
    private int balance;//баланс на счёте
    private String billNumber;//номер счёта

    public Card() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getExpData() {
        return expData;
    }

    public void setExpData(String expData) {
        this.expData = expData;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return balance == card.balance && Objects.equals(number, card.number) && Objects.equals(userId, card.userId) && Objects.equals(expData, card.expData) && Objects.equals(cvv, card.cvv) && Objects.equals(billNumber, card.billNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, userId, expData, cvv, balance, billNumber);
    }
}