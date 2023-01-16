package com.ovoc01.dao.java;

import com.ovoc01.dao.annotation.Column;
import com.ovoc01.dao.annotation.Number;
import com.ovoc01.dao.annotation.PrimaryKey;
import com.ovoc01.dao.annotation.Tables;
import com.ovoc01.dao.utilities.Intermediate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class ObjectDAO {

    String host;
    String port;
    String user;
    String pwd;
    String dbName;
    String table;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public ObjectDAO(){
        table=Intermediate.setTable(this);
    }
    public void insert() throws Exception{
       Connection c = MyConnection.createPostGresConnection("localhost","5432","postgres","password","postgres");
       if(c!=null){
           try{
               Intermediate.prepareSql(c);
               Statement statement = c.createStatement();
                statement.execute(insertQuery());
                c.commit();
           }catch (Exception e){
                c.rollback();
                throw e;
           }finally {
               c.close();
           }
       }
    }


    public void insert(Connection c) throws Exception{
        Intermediate.prepareSql(c);
        Statement statement = c.createStatement();
        statement.execute(insertQuery());
    }

    /**
     * function how create a query for insertion of this object in database
     * */
     String insertQuery() throws NoSuchMethodException,IllegalAccessException , InvocationTargetException {
        table = Intermediate.setTable(this);
        String query = String.format("insert into %s values(",table);
        Vector<Field> list = Intermediate.getFieldToInsert(this);
        Field primaryKey = Intermediate.getPrimaryKeyIndex(this);
        for (int i = 0; i < list.size() ; i++) {
            if(list.get(i).isAnnotationPresent(PrimaryKey.class)){
                PrimaryKey primaryKey1 = list.get(i).getAnnotation(PrimaryKey.class);
                query+=String.format("getiddb('%s','%s')",primaryKey1.seqComp(),primaryKey1.prefix());
            }
           else if(list.get(i).isAnnotationPresent(Number.class)){
               query+=Intermediate.getterValue(this,list.get(i).getName());
           }else {
               query+="'"+Intermediate.getterValue(this,list.get(i).getName())+"'";
           }
           if(i+1< list.size())query+=',';
        }
        query+=")";
         System.out.println(query);
        return query;
    }

    void update(){

    }

    /**
     * function how self its self in a database using is primaryKey
     * */
    Object find(){
        return null;
    }

    /**
     * function how create a vector of object based on the table appropriate to this object
     * @return <h1>Vector of object</h1>
     * */
    public Vector select(Connection c) throws Exception{
        Statement statement = c.createStatement();
        ResultSet rs = statement.executeQuery(String.format("Select * from %s",table));
        Vector list = new Vector();
        while(rs.next()){
             list.add(Intermediate.createObject(this,rs));
        }
        return list;
    }
}
