package com.study.web.repositiry.results;

/**
 * класс для формирования объекта из запроса на пополнение баланса
 */
public class InputDataForDepositing {
    public InputDataForDepositing() {
    }

    int sum;
    String billNumber;

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }
}