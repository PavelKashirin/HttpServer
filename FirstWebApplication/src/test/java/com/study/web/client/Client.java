package com.study.web.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.web.repositiry.results.BillNumber;
import com.study.web.repositiry.results.InputDataForDepositing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * этот класса используется в интеграционных тестах для проверки работы обработчиков
 */
public class Client {

    /**
     * этот метод ипользуется как шаблонный код для методов класса
     * @param Url строка запроса
     * @param billNumber номер счёта
     * @return строку, которая приходит на клиент после ответа сервер
     * @throws IOException
     */
    public static String templateRequest(String Url, String billNumber) throws IOException {
        BillNumber b = new BillNumber();
        b.setBillNumber(billNumber);
        ObjectMapper mapper = new ObjectMapper();

        URL url = new URL(Url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        OutputStream out = con.getOutputStream();
        mapper.writeValue(out, b);
        out.flush();
        out.close();

        InputStream inputStream = con.getInputStream();

        String result = null;
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            result = scanner.nextLine();
        }

        inputStream.close();
        return result;
    }

    /**
     * этот метод отправляет объект со значениями суммы для пополнения и номером счёта на сервер в формате JSON
     * @return строку ответа сервера
     * @throws IOException
     */
    public static String depositingFunds() throws IOException {
        InputDataForDepositing outputData = new InputDataForDepositing();
        outputData.setBillNumber("1267859093");
        outputData.setSum(1000);
        ObjectMapper mapper = new ObjectMapper();
        URL url = new URL("http://localhost:8080/depositingFunds");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        OutputStream out = con.getOutputStream();
        mapper.writeValue(out, outputData);
        out.flush();
        out.close();

        InputStream inputStream = con.getInputStream();

        String result = null;
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            result = scanner.nextLine();
        }

        inputStream.close();
        return result;
    }

    /**
     * этот метод отправляет объект со значением номера счёта на сервер в формате JSON
     * @param billNumber номер счёта
     * @return строку с полученным результатом
     * @throws IOException
     */
    public static String showListOfCards(String billNumber) throws IOException {
        return templateRequest("http://localhost:8080/cards", billNumber);
    }

    /**
     * этот метод отправляет объект со значение номера счёта, для которого необходимо создать новую карту
     * и возвращает параметры новой карты
     * @param billNumber номер счёта для новой карты
     * @return строку со значениями параметров новой карты
     * @throws IOException
     */
    public static String createNewCard(String billNumber) throws IOException {
        return templateRequest("http://localhost:8080/newCard", billNumber);
    }

    /**
     * этот метод отправляет объект со значением номера счёта, баланс которого нужно узнать и
     * возвращает значение баланса этого счёта
     * @param billNumber номер счёта
     * @return строку со значением баланса
     * @throws IOException
     */
    public static String showBalance(String billNumber) throws IOException {
        return templateRequest("http://localhost:8080/balance", billNumber);
    }
}
