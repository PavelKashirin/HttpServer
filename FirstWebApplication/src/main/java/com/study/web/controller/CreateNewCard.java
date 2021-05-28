package com.study.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.web.controller.interfaces.Connectable;
import com.study.web.repositiry.Dao;
import com.study.web.repositiry.results.BillNumber;
import com.study.web.repositiry.results.DataForNewCard;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Класс обработчик запросов на endPoint: "/newCard"
 */
public class CreateNewCard implements HttpHandler, Connectable {
    private Dao dao;

    public CreateNewCard() {
    }

    private final Random random = new Random();

    /**
     * метод генерирующий cvv код новой карты
     * @return целочисленное значение кода cvv
     */
    public int getCVV() {
        return 100 + random.nextInt(900);
    }

    /**
     * этот метод формирует номер новой карты
     * @return строку номера новой карты
     */
    public String getCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            cardNumber.append(1000 + random.nextInt(9000)).append(" ");
        }
        cardNumber.deleteCharAt(cardNumber.length() - 1);

        return cardNumber.toString();
    }

    /**
     * этот метод формирует дату окончания действия карты
     * @return строку даты, до которой действует новая карта.
     */
    public String getDataString() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 3);
        return format.format(calendar.getTime());
    }

    /**
     * @return строку для создания обработчика
     */
    @Override
    public String getEndPoint() {
        return "/newCard";
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
        //строка ответа в случае если нет данных или при возникновении проблем в приложении
        String resultOut = "Not valid data";
        //создание объекта, который преобразует объекты из и в JSON
        ObjectMapper objectMapper = new ObjectMapper();

        try(InputStream requestBody = exchange.getRequestBody(); //получение входного потока тела запроса
            Scanner scanner = new Scanner(requestBody);
            OutputStream out = exchange.getResponseBody()) {

            StringBuilder inputData = new StringBuilder();
            while (scanner.hasNext()) {
                inputData.append(scanner.nextLine());
            }

            BillNumber inputNumber = null;
            try {
                //получение объекта BillNumber для определения номера счёта для новой карты
                 inputNumber = objectMapper.readValue(inputData.toString(), BillNumber.class);
                System.out.println("Принят номер счёта на изготовление новой карты : "
                        + Integer.parseInt(inputNumber.getBillNumber()) + "\n");
            } catch (IOException e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(400, resultOut.length()); //если объект не формируется
            }

            DataForNewCard newCard = null;
            if (inputNumber != null) {
                try {
                    //создание объекта новой карты
                    newCard = dao.getUserIdAndBalance(inputNumber.getBillNumber());
                    if (newCard == null) {
                        resultOut = "bill number not found";
                        exchange.sendResponseHeaders(200, resultOut.length());
                    }
                } catch (SQLException e) {
                    System.out.println("Can not read balance for new card");
                    e.printStackTrace();
                    resultOut = "server error";
                    exchange.sendResponseHeaders(500, resultOut.length());//если не получилось считать данные
                }
            }

            if (newCard != null) {

                Set<String> cardNumbers = new HashSet<>();//создание нового объекта для множества номеров карт
                try {
                    cardNumbers = dao.getCardNumbers();// получение номеров карт из базы данных
                } catch (SQLException e) {
                    System.out.println("Can not read numbers of cards - problem with data base");
                    e.printStackTrace();
                    resultOut = "server error";
                    exchange.sendResponseHeaders(500, resultOut.length());//если не получилось считать данные
                }

                String newCardNumber = getCardNumber();//создание номера карты
                while (!cardNumbers.add(newCardNumber)) {//проверка на уникальность
                    newCardNumber = getCardNumber();//если такой уже есть, то создание нового номера
                }

                try {
                    dao.getNamesForNewCard(newCard);// получение имени и фамилии для новой карты
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Can not read Names for new card - problem with data base");
                    resultOut = "server error";
                    exchange.sendResponseHeaders(500, resultOut.length());//если не получилось считать данные
                }

                newCard.setCvv(getCVV());
                newCard.setCardNumber(newCardNumber);
                newCard.setExpData(getDataString());
                newCard.setBilNumber(inputNumber.getBillNumber());

                try {
                    if (dao.createNewCard(newCard)) {//если новая карта записана в базу данных, то формируется
                        resultOut = objectMapper.writeValueAsString(newCard);//строка ответа клиенту с данными новой карты
                        exchange.sendResponseHeaders(200, resultOut.length());//если не получилось считать данные
                    }
                } catch (SQLException e) {
                    System.out.println("Card was not write in data base - Problem with database");
                    e.printStackTrace();
                    resultOut = "server error";
                    exchange.sendResponseHeaders(500, resultOut.length());//если не получилось считать данные
                }
            }

            out.write(resultOut.getBytes(StandardCharsets.UTF_8)); //записть полученной строки в поток
            out.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // карта будет создана если объект для новой карты был создан и частично заполнен данными



        try {
            dao.showCards();
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}