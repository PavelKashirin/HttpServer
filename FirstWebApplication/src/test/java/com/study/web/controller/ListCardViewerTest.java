package com.study.web.controller;

import com.study.web.application.Main;
import com.study.web.client.Client;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ListCardViewerTest {

    @Test
    public void testGetEndPoint() {
        String expected = new ListCardViewer().getEndPoint();
        String actual = "/cards";

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testHandle() throws IOException {
        Main.serverInit();
        String expected = Client.showListOfCards("1267877783");
        String actual = "[{\"number\":\"1884 9123 8724 3463\",\"expData\":\"12." +
                "10.2021\",\"cvv\":\"999\",\"balance\":320200,\"billNumber\":\"1267877783\"}]";

        Assert.assertEquals(expected, actual);
    }
}