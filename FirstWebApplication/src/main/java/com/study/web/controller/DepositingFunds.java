package com.study.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.web.controller.interfaces.Connectable;
import com.study.web.repositiry.Dao;
import com.study.web.repositiry.results.Balance;
import com.study.web.repositiry.results.InputDataForDepositing;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Scanner;

public class DepositingFunds implements HttpHandler, Connectable {
    private Dao dao;

    public DepositingFunds() {
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
        //создание объекта, который формирует объект JSON в ответ на запрос баланса
        ObjectMapper objectMapper = new ObjectMapper();
        String resultOut = "not valid data";

        try (//получение входного потока тела запроса - номер счёта (Объект BilNumber в JSON формате)
             InputStream requestBody = exchange.getRequestBody();
             OutputStream out = exchange.getResponseBody();
             Scanner scanner = new Scanner(requestBody)
        ) {
            String inputData = null;
            while (scanner.hasNext()) {
                inputData = scanner.nextLine();
            }

            //получение бокса с номером счёта
            InputDataForDepositing input = null;
            try {
                input = objectMapper.readValue(inputData, InputDataForDepositing.class);
                System.out.println("Принят запрос на пополнение баланса: увеличение баланса на " + input.getSum() +
                        " у счёта номер: " + Integer.parseInt(input.getBillNumber()) + "\n");
                if (input.getSum() < 0) {
                    exchange.sendResponseHeaders(400, resultOut.length());
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            Balance balance = null;
            if (input != null) {
                balance = new Balance();
                try {
                    balance.setBalance(dao.updateBalance(input.getBillNumber(), input.getSum()));
                    System.out.println("Текущий баланс: " + balance.getBalance() + "\n");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (balance != null) {
                resultOut = objectMapper.writeValueAsString(balance);
                if (balance.getBalance() < 0) {
                    resultOut = "bill number not found";
                }
                exchange.sendResponseHeaders(200, resultOut.length());
            }

            try {
                dao.showCards();
                System.out.println();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            out.write(resultOut.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * @return строку для создания обработчика
     */
    @Override
    public String getEndPoint() {
        return "/depositingFunds";
    }
}