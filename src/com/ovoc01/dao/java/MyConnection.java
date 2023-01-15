package com.ovoc01.dao.java;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *Generic class to get instance of SQL connection
 * @author ovoc01
 * */
@SuppressWarnings("unused")
public class MyConnection {
    /**
     * Function who create postgres sql connection
     * @param host the host of the connection
     * @param port the port to be used
     * @param user the username
     * @param pwd the password
     * @param dbname the database name
     * @return Connection
     */

    public static Connection createPostGresConnection(String host,String port,String user, String pwd,String dbname) {
        Connection result = null;
        String strConn = "jdbc:postgresql://" + host + ":" + port + "/" + dbname;

        try {
            Class.forName("org.postgresql.Driver");
            result = DriverManager.getConnection(strConn, user, pwd);
            result.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
