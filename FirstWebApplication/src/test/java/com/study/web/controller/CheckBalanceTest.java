package com.study.web.controller;
import com.study.web.application.Main;
import com.study.web.client.Client;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class CheckBalanceTest {
    @Test
    public void testGetEndPoint() {
        String expected = new CheckBalance().getEndPoint();
        String actual = "/balance";

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testHandle() throws IOException {
        Main.serverInit();
        String expectedExists = Client.showBalance("1237659083");
        String actualExists = "{\"balance\":1000}";

        Assert.assertEquals(expectedExists, actualExists);
    }
}