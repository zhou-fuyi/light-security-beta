package com.light.security.core.authentication.dao.jdbc;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Arrays;

public class AbstractJdbcProcessorTest {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private String filename = "support/ddl/light-security-simple.ddl";

    @Test
    public void createTable() throws IOException {
//        System.out.println(getClass().getResource("").getPath() + "light-security-advance.ddl");
//        System.out.println(new File(getClass().getResource("").getPath() + "light-security-advance.ddl").getAbsolutePath());
//        Enum currentAuthType = Enum.valueOf(AuthTypeEnum.class, AuthTypeEnum.SIMPLE.name());
//        System.out.println(filename.indexOf("."));
//        System.out.println(currentAuthType.name().toLowerCase(Locale.getDefault()));
//        System.out.println(currentAuthType.name().toLowerCase());
//        System.out.println(filename.substring(0, filename.indexOf(".")).endsWith(currentAuthType.name().toLowerCase()));
        ClassPathResource classPathResource = new ClassPathResource(filename);
        System.out.println(classPathResource.getURL().getPath());
        System.out.println(classPathResource.getURL().getFile());
        InputStream inputStream = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            inputStream = classPathResource.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;

            while ((len = inputStream.read(buffer)) != -1){
                String temp = new String(buffer, 0, len, "utf-8");
//                logger.info("buffer is {}", temp);
                stringBuffer.append(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(stringBuffer.toString());
    }

    @Test
    public void test() throws IOException {
        InputStream inputStream = null;
        String ddlQuery = null;
        ClassPathResource classPathResource = new ClassPathResource(filename);
        System.out.println(classPathResource.getURL().getPath());
        System.out.println(classPathResource.getURL().getFile());
        try {
            inputStream = classPathResource.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] byteBuffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(byteBuffer)) != -1){
                bos.write(byteBuffer, 0, len);
            }
            bos.close();
            ddlQuery = bos.toString("utf-8");
        }catch (Exception e){
            if (logger.isDebugEnabled()){
                logger.debug("文件读取异常: {}", e.getMessage());
            }
            throw e;
        }
        System.out.println(ddlQuery);
    }

    @Test
    public void testSplit(){
        String sql = " 123; ";
        String sql1 = "123;";
        String sql2 = "123;456;";
        String sql3 = "123;456; ";
        String sql4 = " ";
        String sql5 = "123132";

        System.out.println(Arrays.toString(sql.trim().split(";")));
        System.out.println(Arrays.toString(sql1.split(";")));
        System.out.println(Arrays.toString(sql2.split(";")));
        System.out.println(Arrays.toString(sql3.split(";")));
        String[] temp4 = sql4.trim().split(";");
        System.out.println(temp4.length);
        System.out.println(temp4[0]);
        System.out.println(Arrays.toString(temp4));
        System.out.println(Arrays.toString(sql5.split(";")));

    }
}
