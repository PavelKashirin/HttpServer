package com.study.web.database;

import com.study.web.repositiry.Dao;

import java.sql.*;
import java.util.Objects;

/**
 * этот класс создаёт базу данных из файла: src/main/resources/create.sql
 */
public class DataBaseCreator {
    static Connection connection = null;
    private static final String URL_CREATE = "jdbc:h2:mem:study;INIT=runscript from " +
            "'classpath:create.sql'";

    /**
     * Этот метод создаёт и возвращает ссылку на connection если объект не был создан ранее
     * @return возвращает connection для созданной базы данных
     */
    public static Connection createDataBase() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL_CREATE);
                new Dao(connection).showUsers();
            } catch (SQLException exc) {
                System.out.println("Connection");
                throw new RuntimeException();
            }
        }
        return connection;
    }
}