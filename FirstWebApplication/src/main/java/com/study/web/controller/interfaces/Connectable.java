package com.study.web.controller.interfaces;

import com.study.web.repositiry.Dao;

/**
 * Этот интерфейс реализует автоматическое добавление обрабатываемого запроса в сервер и
 * устанавливает ссылку на объект доступа к базе данных
 */
public interface Connectable {
    /**
     * @return возвращает строку с названием endPoint
     */
    String getEndPoint();

    /**
     * этот метод устанавливает ссылку объекта на получаемый параметр
     * @param dao объект с доступом к базе данных
     */
    void setDao(Dao dao);
}