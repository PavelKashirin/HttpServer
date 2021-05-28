package com.study.web.repositiry.results;

/**
 * класс - шаблон для формирования объекта с номером счёта из тела запроса на получение баланса,
 * списка карт и создания новой карты
 */
public class BillNumber {

    private String billNumber;

    public BillNumber() {
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }
}