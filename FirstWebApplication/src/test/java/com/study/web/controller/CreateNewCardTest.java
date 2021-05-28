package com.study.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.web.application.Main;
import com.study.web.client.Client;
import com.study.web.repositiry.results.DataForNewCard;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateNewCardTest {
    @Test
    public void testGetEndPoint() {
        String expected = new CreateNewCard().getEndPoint();
        String actual = "/newCard";

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetCVV() {
        int expected = new CreateNewCard().getCVV();

        Assert.assertTrue(((expected >=100) && (expected <= 999)));
    }

    @Test
    public void testGetCardNumber() {
        String expected = new CreateNewCard().getCardNumber();

        Assert.assertTrue((!expected.startsWith("0")) && (expected.length() == 19));
    }

    @Test
    public void testGetDataString() {
        String expected = new CreateNewCard().getDataString();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 3);
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
        String actual = f.format(calendar.getTime());

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testHandle() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Main.serverInit();
        String result = Client.createNewCard("1237659083");
        DataForNewCard expected = mapper.readValue(result, DataForNewCard.class);

        Assert.assertEquals(expected.getBilNumber(), "1237659083");
    }
}