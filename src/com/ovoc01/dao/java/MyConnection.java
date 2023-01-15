package com.ovoc01.dao.java;

import java.sql.Connection;
import java.sql.DriverManager;

@SuppressWarnings("unused")
public class MyConnection {
    public static Connection createPostGresConnection(String host,String port,String user, String pwd,String dbname) {
        Connection result = null;
        String strConn = "jdbc:postgresql://" + host + ":" + port + "/" + dbname;

        try {
            Class.forName("org.postgresql.Driver");
            result = DriverManager.getConnection(strConn, user, pwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
