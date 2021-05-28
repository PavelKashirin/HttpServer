package com.study.web.application;

import com.study.web.controller.interfaces.Connectable;
import com.study.web.database.DataBaseCreator;
import com.study.web.repositiry.Dao;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Objects;

/**
 * <h1>Проектная задача</h1>
 * Программа Main реализует приложение, которое запускает сервер для обработки запросов:
 * <p/>
 * 1 - выполняет поиск баланса в базе данных по номеру счёта, при запросе на end point: "/balance".
 * 2 - выполняет создание новой карты для номера счёта, при запросе на end point: "/newCard".
 * 3 - выполняет пополнение счёта на указанную сумму, при запросе на end point: "/depositingFunds".
 * 4 - выполняет поиск карт в базе данных на определённый номер счёта, при запросе на end point: "/cards.
 * все запросы принимаются и отправляют запросы в формате JSON.
 * @ author Pavel Kashirin
 * @ версия 1.0
 * @ от 2021-05-27
 */
public class Main {
    private static HttpServer server = null;

    /**
     * Этот метод инициализирует базу данных, получает ссылку на connection к базе данных, создаёт объект Dao и
     * создаёт объект httpserver обходит все файлы(не директории) в директории com/study/web/controller и для сервера
     * создаёт контексты для эндпоинтов используемых в программе.
     * controller
     */
    public static void serverInit() {
        String refOfHandlers = "com.study.web.controller.";// ссылка на директорию с обработчиками
        //создание объекта Path
        Path path = Paths.get(
                "/Users/kasirinpavel/Desktop/FirstWebApplication/src/main/java/com/study/web/controller");

        Connection connection = DataBaseCreator.createDataBase(); //инициализация базы данных и получение объекта connection
        Dao dao = new Dao(connection); //создание объекта Dao

        if (server == null) {
            try {
                //создание объекта httpserver на localhost port 8080
                server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
            } catch (IOException e) {
                System.out.println("Can not create server");
                e.printStackTrace();
                throw new RuntimeException();
            }


            //перебирается лист файлов из директории com/study/web/controller
            for (File f : Objects.requireNonNull(path.toFile().listFiles())) {
                String endPoint;
                Object object;
                HttpHandler handler;

                if (f.isFile()) {
                    try {
                        //создание строки ссылки на объект
                        String temp = (refOfHandlers + f.getName()).split(".java")[0];
                        object = Class.forName(temp).newInstance();//создаётся экземпляр объекта
                    } catch (ClassNotFoundException e) {
                        System.out.println("Object of " + f.getName() + " was not found");
                        e.printStackTrace();
                        continue;
                    } catch (IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                        System.out.println("Object of " + f.getName() + " was found, but can not create new instance" +
                                "check default constructor");
                        continue;
                    }

                    try {
                        // приводим полученных объект к Connectable
                        Connectable connectable = (Connectable) object;
                        endPoint = connectable.getEndPoint(); // получаем строку endPoint
                        connectable.setDao(dao);// присваиваем ссылку объекта на созданных нами объект Dao
                    } catch (Exception e) {
                        System.out.println("Can not create endPoint string or set Dao check implementation of Connectable " +
                                "interface in " + object.getClass().getName() + " object");
                        e.printStackTrace();
                        continue;
                    }

                    try {
                        //приводим полученный объект к Connectable для создания контекста
                        handler = ((HttpHandler) object);
                    } catch (Exception e) {
                        System.out.println("Can not create HttpHandler from " + object.getClass().getName() + " " +
                                "check implementation of HttpHandler interface");
                        e.printStackTrace();
                        continue;
                    }

                    System.out.println(endPoint + " " + handler.getClass().getName());
                    server.createContext(endPoint, handler); //создание контекста сервера
                }
            }

            System.out.println();
            server.start();
        }
    }

    /**
     * метод с которого начинается запуск программы
     * @param args не используются
     */
    public static void main(String[] args) {
        serverInit();
    }
}
