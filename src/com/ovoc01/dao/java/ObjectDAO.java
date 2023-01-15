package com.ovoc01.dao.java;

import com.ovoc01.dao.annotation.Column;
import com.ovoc01.dao.annotation.Number;
import com.ovoc01.dao.annotation.Tables;
import com.ovoc01.dao.utilities.Intermediate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
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
    }
    public void insert(){
       Vector<Field> list = Intermediate.getFieldToInsert(this);
        Field primaryKey = Intermediate.getPrimaryKeyIndex(this);
        for (int i = 0; i < list.size(); i++) {
            Field field = list.get(i);

        }
    }

    public String insertQuery() throws NoSuchMethodException,IllegalAccessException , InvocationTargetException {
        table = Intermediate.setTable(this);
        String query = String.format("insert into %s values(",table);
        Vector<Field> list = Intermediate.getFieldToInsert(this);
        Field primaryKey = Intermediate.getPrimaryKeyIndex(this);
        for (int i = 0; i < list.size() ; i++) {
           if(list.get(i).isAnnotationPresent(Number.class)){
               query+=Intermediate.getterValue(this,list.get(i).getName());
           }else {
               query+="'"+Intermediate.getterValue(this,list.get(i).getName())+"'";
           }
           if(i+1< list.size())query+=',';
        }
        query+=")";
        return query;
    }
    void update(){

    }

    Object find(){
        return null;
    }

    Vector<Object> select(){
        return null;
    }

    public void createPrimaryKeySequence(int index) throws Exception{
        Connection c = null;
        try{
            c = MyConnection.createPostGresConnection(getHost(),getPort(),getUser(),getPwd(),getDbName());
            Statement statement = c.createStatement();
            statement.execute(String.format("create or replace function %s ",getClass().getAnnotations()));
        }catch (SQLException e){
            e.printStackTrace();
            c.rollback();
        }catch (Exception e){
            e.printStackTrace();
            c.rollback();
        }finally {
            c.close();
        }
    }
}
