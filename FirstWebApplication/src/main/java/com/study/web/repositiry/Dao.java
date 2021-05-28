package com.study.web.repositiry;

import com.study.web.model.Card;
import com.study.web.repositiry.results.DataForNewCard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * класс определяющий методы взаимодействия с базой данных
 */
public class Dao {
    private final Connection connection;

    public Dao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Метод возвращает баланс по номеру счёта
     * @param bilNumber номер счёта
     * @return возвращает значение баланса
     * @throws SQLException
     */
    public int getBalance(String bilNumber) throws SQLException {
        int result = -1;
        PreparedStatement stmt = connection.prepareStatement("SELECT BALANCE FROM CARD WHERE (BILLNUMBER = ?)");
        stmt.setString(1, bilNumber);
        ResultSet resultSet = stmt.executeQuery();
        if (resultSet.next()) {
            result = resultSet.getInt(1);
        }

        resultSet.close();
        stmt.close();
        return result;
    }

    /**
     * метод возвращает объект DataForNewCard с заполненными полями id пользователя и балансом
     * @param bilNumber номер счёта
     * @return объект DataForNewCard с заполненными полями id пользователя и балансом
     * @throws SQLException
     */
    public DataForNewCard getUserIdAndBalance(String bilNumber) throws SQLException {
        DataForNewCard result = null;
        PreparedStatement stmt = connection.prepareStatement("SELECT USERID, BALANCE FROM CARD WHERE (BILLNUMBER = ?)");
        stmt.setString(1, bilNumber);
        ResultSet resultSet = stmt.executeQuery();
        if (resultSet.next()) {
            result = new DataForNewCard();
            result.setId(resultSet.getInt(1));
            result.setBalance(resultSet.getInt(2));
        }
        resultSet.close();
        stmt.close();
        return result;
    }

    /**
     * метод заполняет поля firstName и lastName объекта DataForNewCard из базы данных по id пользователя
     * @param toBuild объект DataForNewCard с заполненными полями id пользователя и балансом
     * @throws SQLException
     */
    public void getNamesForNewCard(DataForNewCard toBuild) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("SELECT FIRSTNAME, LASTNAME FROM USER WHERE (ID = ?)");
        stmt.setInt(toBuild.getId(), 1);
        ResultSet resultSet = stmt.executeQuery();
        resultSet.next();
        toBuild.setFirstName(resultSet.getString(1));
        toBuild.setLastName(resultSet.getString(2));
        resultSet.close();
        stmt.close();
    }

    /**
     * @return множество номеров карт из базы данных
     * @throws SQLException
     */
    public Set<String> getCardNumbers() throws SQLException {
        Set<String> numbers = new HashSet<>();
        PreparedStatement stmt = connection.prepareStatement("SELECT NUMBER FROM CARD");
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet.next()) {
            numbers.add(resultSet.getString(1));
        }
        resultSet.close();
        stmt.close();

        return numbers;
    }

    /**
     * @param newCard объект DataForNewCard со всеми параметрами необходимыми для создания карты
     * @return true если карта была добавлена и false если нет
     * @throws SQLException
     */
    public boolean createNewCard(DataForNewCard newCard) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("insert into CARD (NUMBER, USERID, EXP_DATE, CVV, BALANCE, BILLNUMBER)" +
                " values (?, ?, ?, ?, ?, ?)");
        stmt.setString(1, newCard.getCardNumber());
        stmt.setInt(2, newCard.getId());
        stmt.setString(3, newCard.getExpData());
        stmt.setInt(4, newCard.getCvv());
        stmt.setInt(5, newCard.getBalance());
        stmt.setString(6, newCard.getBilNumber());
        if (stmt.executeUpdate() > 0) {
            stmt.close();
            return true;
        }
        return false;
    }

    /**
     * @param billNumber номер счёта
     * @param sum сумма для пополения
     * @return полученный баланс
     * @throws SQLException
     */
    public int updateBalance(String billNumber, int sum) throws SQLException {
        int resultBalance = getBalance(billNumber) + sum;
        PreparedStatement stmt = connection.prepareStatement("UPDATE CARD SET BALANCE = ? WHERE BILLNUMBER = ?");
        stmt.setInt(1, resultBalance);
        stmt.setString(2, billNumber);
        stmt.executeUpdate();
        return getBalance(billNumber);
    }

    /**
     * метод определяет карты по номеру счёта
     * @param billNumber номер счёта
     * @return список карт по номеру счёта
     * @throws SQLException
     */
    public List<Card> getListOfCards(String billNumber) throws SQLException {
        List<Card> result = new ArrayList<>();
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT NUMBER, EXP_DATE, CVV, BALANCE, BILLNUMBER FROM CARD WHERE BILLNUMBER = ?");
        stmt.setString(1, billNumber);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Card temp = new Card();
            temp.setNumber(rs.getString(1));
            temp.setExpData(rs.getString(2));
            temp.setCvv(rs.getString(3));
            temp.setBalance(rs.getInt(4));
            temp.setBillNumber(rs.getString(5));
            result.add(temp);
        }

        return result;
    }

    /**
     * метод показывает в консоль всех пользователей в базе данных
     * @throws SQLException
     */
    public void showUsers() throws SQLException {
        String SELECT_USERS = "SELECT * FROM USER";
        ResultSet rs = connection.createStatement().executeQuery(SELECT_USERS);
        while (rs.next()) {
            System.out.println(rs.getInt(1) +" " + rs.getString(2) + " " +
                    "" + rs.getString(3) + " " + rs.getString(4));
        }
        rs.close();
    }

    /**
     * метод показывает в консоль все карты в базе данных
     * @throws SQLException
     */
    public void showCards() throws SQLException {
        String SELECT_CARD = "SELECT * FROM CARD";
        ResultSet rs = connection.createStatement().executeQuery(SELECT_CARD);
        while (rs.next()) {
            System.out.println(rs.getInt(1) +" " + rs.getString(2) + " " +
                    "" + rs.getString(3) + " " + rs.getString(4) + " "
                    + rs.getInt(5) + " " + rs.getInt(6) + " " + rs.getString(7));
        }
        rs.close();
    }

    /**
     * метод возвращает connection к базе данных
     * @return
     */
    public Connection getConnection() {
        return connection;
    }
}