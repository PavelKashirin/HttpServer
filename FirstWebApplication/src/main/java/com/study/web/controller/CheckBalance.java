package com.study.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.web.controller.interfaces.Connectable;
import com.study.web.repositiry.Dao;
import com.study.web.repositiry.results.Balance;
import com.study.web.repositiry.results.BillNumber;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Класс обработчик запросов на endPoint: "/balance"
 */
public class CheckBalance implements HttpHandler, Connectable {
    private Dao dao;

    public CheckBalance() {
    }

    /**
     * указывает ссылку на объект доступа к базе данных созданный при инициализации приложения.
     * @param dao объект с доступом к базе данных
     */
    @Override
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    /**
     * метод который вызывается у объекта обработчика
     * @param exchange объект, который предоставляет потоки ввода и вывода между клиентом и сервером.
     */
    @Override
    public void handle(HttpExchange exchange) {
        String resultOut = "not valid data";
        //создание объекта, который формирует объекты JSON приходящие и исходящие
        ObjectMapper objectMapper = new ObjectMapper();
        //получение входного потока тела запроса - номер счёта (Объект BillNumber в JSON формате)
        try (InputStream requestBody = exchange.getRequestBody();
             Scanner scanner = new Scanner(requestBody);
             OutputStream out = exchange.getResponseBody()) {
            //получение строки из входящего потока
            StringBuilder inputData = new StringBuilder();
            while (scanner.hasNext()) {
                inputData.append(scanner.next());
            }

            BillNumber bilNumber = null;
            try {
                bilNumber = objectMapper.readValue(inputData.toString(), BillNumber.class);//получение объекта
                System.out.println("Принят номер счёта для проверки баланса : "
                        + Integer.parseInt(bilNumber.getBillNumber()) + "\n");
            } catch (IOException e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(400, resultOut.length()); //если объект не формируется
            }

            Balance balance = null;//создание объекта для ответа
            if (bilNumber != null) {
                try {
                    int bal = dao.getBalance(bilNumber.getBillNumber());
                    balance = new Balance();
                    balance.setBalance(bal);
                    System.out.println("Получен баланс из базы данных : " + bal);
                } catch (SQLException e) {
                    e.printStackTrace();
                    resultOut = "server error";
                    exchange.sendResponseHeaders(500, resultOut.length());//если не получилось считать данные
                }
            }

            if (balance != null) {
                //преобразование объекта balance в строкув JSON формате
                resultOut = "no data on this bill number";
                if (balance.getBalance() >=0 ) {
                    resultOut = objectMapper.writeValueAsString(balance);
                }
                System.out.println("Отправляется строка - " + resultOut + "\n");
                exchange.sendResponseHeaders(200, resultOut.length());//формирование хедера
            }

            out.write(resultOut.getBytes(StandardCharsets.UTF_8)); //записть полученной строки в поток
            out.flush();

        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * @return строку для создания обработчика
     */
    @Override
    public String getEndPoint() {
        return "/balance";
    }
}