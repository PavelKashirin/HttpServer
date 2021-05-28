package com.study.web.repositiry;

import com.study.web.database.DataBaseCreator;
import com.study.web.model.Card;
import com.study.web.repositiry.results.DataForNewCard;
import org.junit.Assert;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DaoTest {
    Dao dao = new Dao(DataBaseCreator.createDataBase());

    @Test
    public void testGetBalance() throws SQLException {
        int expected = dao.getBalance("1267822083");

        Assert.assertEquals(expected, 10200);
    }

    @Test
    public void testUpdateBalance() throws SQLException {
        int expected = dao.updateBalance("1237659083", 1000);

        Assert.assertEquals(expected, 2000);
    }

    @Test
    public void testGetUserIdAndBalance() throws SQLException {
        DataForNewCard inputData = dao.getUserIdAndBalance("1267989083");

        Assert.assertEquals(inputData.getId(), 3);
        Assert.assertEquals(inputData.getBalance(), 23200);
    }

    @Test
    public void testGetCardNumbers() throws SQLException {
        Set<String> expected = dao.getCardNumbers();
        PreparedStatement stmt = dao.getConnection().prepareStatement("SELECT NUMBER FROM CARD");
        ResultSet rs = stmt.executeQuery();
        Set<String> actual = new HashSet<>();
        while (rs.next()) {
            actual.add(rs.getString(1));
        }

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testCreateNewCard() throws SQLException {
        DataForNewCard data = new DataForNewCard();
        data.setExpData("12.10.2025");
        data.setBilNumber("1237659777");
        data.setCvv(777);
        data.setBalance(1000);
        data.setId(1);
        data.setCardNumber("1234 9811 8736 7777");

        Assert.assertTrue(dao.createNewCard(data));
    }

    @Test
    public void testGetListOfCards() throws SQLException {
        List<Card> expected = dao.getListOfCards("1267859093");
        List<Card> actual = new ArrayList<>();
        PreparedStatement stmt = dao.getConnection().prepareStatement(
                "SELECT NUMBER, EXP_DATE, CVV, BALANCE, BILLNUMBER FROM CARD WHERE BILLNUMBER = ?");
        stmt.setString(1, "1267859093");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Card card = new Card();
            card.setNumber(rs.getString(1));
            card.setExpData(rs.getString(2));
            card.setCvv(rs.getString(3));
            card.setBalance(rs.getInt(4));
            card.setBillNumber(rs.getString(5));
            actual.add(card);

            Assert.assertEquals(expected, actual);
        }
    }
}