package com.study.web.controller;

import com.study.web.application.Main;
import com.study.web.client.Client;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class DepositingFundsTest {
    @Test
    public void testGetEndPoint() {
        String expected = new DepositingFunds().getEndPoint();
        String actual = "/depositingFunds";

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testHandle() throws IOException {
        Main.serverInit();
        String expected = Client.depositingFunds();
        String actual = "{\"balance\":1200}";

        Assert.assertEquals(expected, actual);
    }
}