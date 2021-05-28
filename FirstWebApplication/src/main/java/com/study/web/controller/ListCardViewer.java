package com.study.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.web.controller.interfaces.Connectable;
import com.study.web.model.Card;
import com.study.web.repositiry.Dao;
import com.study.web.repositiry.results.BillNumber;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ListCardViewer implements HttpHandler, Connectable {
    private Dao dao;

    public ListCardViewer() {
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
        //создание объекта, который формирует объект JSON в ответ на запрос баланса
        ObjectMapper objectMapper = new ObjectMapper();
        try (//получение входного потока тела запроса - номер счёта (Объект BilNumber в JSON формате)
             InputStream requestBody = exchange.getRequestBody();
             OutputStream out = exchange.getResponseBody();
             Scanner scanner = new Scanner(requestBody)
        ) {
            String input = null;
            while (scanner.hasNext()) {
                input = scanner.nextLine();
            }

            //получение бокса с номером счёта
            BillNumber billNumber = null;
            if (input != null) {
                try {
                    billNumber = objectMapper.readValue(input, BillNumber.class);
                    System.out.println("Принят номер счёта для получение списка карт : "
                            + Integer.parseInt(billNumber.getBillNumber()) + "\n");
                } catch (IOException ioException) {
                    exchange.sendResponseHeaders(400, resultOut.length());
                    ioException.printStackTrace();
                }
            }

            if (billNumber != null) {
                List<Card> cards = null;
                try {
                    cards = dao.getListOfCards(billNumber.getBillNumber());
                } catch (SQLException e) {
                    e.printStackTrace();
                    resultOut = "Server error";
                    exchange.sendResponseHeaders(500, resultOut.length());
                }

                if (cards != null) {
                    resultOut = objectMapper.writeValueAsString(cards);
                    if (cards.size() == 0) resultOut = "bill number not found";
                    System.out.println("Отправляемая строка: " + resultOut);
                }

                exchange.sendResponseHeaders(200, resultOut.length());
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
        return "/cards";
    }
}