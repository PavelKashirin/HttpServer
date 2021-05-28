package com.study.web.repositiry.results;

/**
 * этот класс - шаблон для формирования объекта, который будет отправлен на запрос баланса клиенту
 */
public class Balance {
    int balance;

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}