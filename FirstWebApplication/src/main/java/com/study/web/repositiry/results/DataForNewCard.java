package com.study.web.repositiry.results;

/**
 * класс для формирования объекта необходимого для сбора данных для создания новой карты
 * и отправки данных клиенту при получении запроса на выпуск новой карты
 */
public class DataForNewCard {
    private String CardNumber;
    private int id;
    private int balance;
    private String firstName;
    private String lastName;
    private int cvv;
    private String ExpData;
    private String bilNumber;

    public String getBilNumber() {
        return bilNumber;
    }

    public void setBilNumber(String bilNumber) {
        this.bilNumber = bilNumber;
    }

    public String getExpData() {
        return ExpData;
    }

    public void setExpData(String expData) {
        ExpData = expData;
    }

    public DataForNewCard() {
    }

    public String getCardNumber() {
        return CardNumber;
    }

    public void setCardNumber(String cardNumber) {
        CardNumber = cardNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}