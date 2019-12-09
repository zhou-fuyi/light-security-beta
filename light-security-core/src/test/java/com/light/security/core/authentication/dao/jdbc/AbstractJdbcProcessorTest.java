package com.light.security.core.authentication.dao.jdbc;

import org.junit.Test;

import java.io.File;

public class AbstractJdbcProcessorTest {

    @Test
    public void createTable() {
        System.out.println(getClass().getResource("").getPath() + "light-security-advance.ddl");
        System.out.println(new File(getClass().getResource("").getPath() + "light-security-advance.ddl").getAbsolutePath());
    }
}
