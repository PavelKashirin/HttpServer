package com.study.web.database;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;

public class DataBaseCreatorTest {

    @Test
    public void testCreateDataBase_NO_NULL() {
        Connection expected = DataBaseCreator.createDataBase();

        Assert.assertNotNull(expected);
    }
}