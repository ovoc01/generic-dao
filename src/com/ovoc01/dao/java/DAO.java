package com.ovoc01.dao.java;

import com.ovoc01.dao.annotation.Column;
import com.ovoc01.dao.annotation.ForeignKey;
import com.ovoc01.dao.annotation.PrimaryKey;
import com.ovoc01.dao.excetpion.NoForeignKey;
import com.ovoc01.dao.excetpion.NullValue;
import com.ovoc01.dao.utilities.Utilities;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

@SuppressWarnings("unused")
 class DAO {
    protected String table;
    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public DAO() {
        table = Utilities.setTable(this);
    }

    /**
     * Inserts the current object into the database in case of a transaction
     *
     * @param c Connection to the database
     * @throws Exception if there is an error executing the SQL statement
     */
    public void insert(Connection c) throws Exception {
        Utilities.prepareSql(c);
        Statement statement = c.createStatement();
        System.out.println(insertQuery());
        statement.execute(insertQuery());
        System.out.println("insert done");
    }

    /**
     * Creates an SQL query for inserting the current object into the database
     *
     * @return a String representing the SQL query
     * @throws NoSuchMethodException     if the getter method for a field does not exist
     * @throws IllegalAccessException    if the getter method for a field cannot be accessed
     * @throws InvocationTargetException if the getter method for a field throws an exception
     * @throws NullValue                 if the value for a field is null
     */
    @SuppressWarnings("ReassignedVariable")
    private String insertQuery() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, NullValue {
        table = Utilities.setTable(this);
        StringBuilder query = new StringBuilder(String.format("insert into %s values(", table));
        Vector<Field> list = Utilities.getFieldToInsert(this);
        for (Field field : list) {
            Column column = field.getAnnotation(Column.class);
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                query.append(String.format("getiddb('%s','%s')", primaryKey.seqComp(), primaryKey.prefix()));
            } else if (column.isNumber()) {
                query.append(Utilities.getterValue(this, field.getName()));
            } else {
                query.append("'").append(Utilities.getterValue(this, field.getName())).append("'");
            }
            if (list.indexOf(field) != list.size() - 1) {
                query.append(',');
            }
        }
        query.append(")");
        return query.toString();
    }

    /**
     * Updates the current object in the database
     * @param c Connection to the database
     * @throws InvocationTargetException if the getter method for a field throws an exception
     * @throws NoSuchMethodException     if the getter method for a field does not exist
     * @throws IllegalAccessException    if the getter method for a field cannot be accessed
     * @throws NullValue                 if the value for a field is null
     * @throws SQLException              if there is an error executing the SQL statement
     */
    public void update(Connection c) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, NullValue, SQLException {
        String query = updateQuery();
        Statement statement = c.createStatement();
        //System.out.println(updateQuery());
        statement.execute(updateQuery());
        System.out.println("update done");
    }

    /**
     * Creates an SQL query for updating the current object in the database
     *
     * @return a String representing the SQL query
     * @throws NoSuchMethodException     if the getter method for a field does not exist
     * @throws IllegalAccessException    if the getter method for a field cannot be accessed
     * @throws InvocationTargetException if the getter method for a field throws an exception
     * @throws NullValue                 if the value for a field is null
     */
    private String updateQuery() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NullValue {
        StringBuilder query = new StringBuilder(String.format("update  %s set ", table));
        Vector<Field> fields = Utilities.getNotNullField(this);
        Field primaryKey = Utilities.getPrimaryKeyIndex(this);
        for (int i = 0; i < fields.size(); i++) {
            String column = Utilities.getColmunName(fields.get(i));
            String method = Utilities.createGetter(fields.get(i).getName());
            Object object = getClass().getDeclaredMethod(method).invoke(this);
            Column column1 = fields.get(i).getAnnotation(Column.class);
            if (column1.isNumber()) {
                query.append(column).append("=").append(object);
            } else {
                query.append(column).append("='").append(object).append("'");
            }
            if (i + 1 < fields.size()) query.append(',');
        }
        query.append(" where ").
                append(Utilities.getColmunName(primaryKey)).append("=").append("'").
                append(Utilities.getterValue(this, Utilities.getColmunName(primaryKey))).append("'");
        System.out.println(query.toString());
        return query.toString();
    }

    /**
     * Selects all objects of the current type from the database
     * @param c Connection to the database
     * @return a Vector of objects of the current type
     * @throws Exception if there is an error executing the SQL statement or creating the objects
     */
    @SuppressWarnings({"rawused", "rawtypes"})
    public <T> Vector<T> select(Connection c) throws Exception {
        Statement statement = c.createStatement();
        // System.out.println(selectQuery());
        ResultSet rs = statement.executeQuery(selectQuery());
        Vector<T> list = new Vector<T>();
        while (rs.next()) {
            list.add((T) Utilities.createObject(this, rs));
        }
        return list;
    }

    /**
     * Selects all objects of the current type from the database
     *
     * @param c     Connection to the database
     * @param query query of the request
     * @return a Vector of objects of the current type
     * @throws Exception if there is an error executing the SQL statement or creating the objects
     */
    public <T> Vector<T> select(Connection c, String query) throws Exception {
        Statement statement = c.createStatement();
        // System.out.println(selectQuery());
        ResultSet rs = statement.executeQuery(query);
        Vector<T> list = new Vector<T>();
        while (rs.next()) {
            list.add((T) Utilities.createObject(this, rs));
        }
        return list;
    }


    private String selectQuery() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        StringBuilder query = new StringBuilder(String.format("select * from %s ", table));
        Vector<Field> listNotNull = Utilities.getNotNullField(this);
        int i = 0;
        if (listNotNull.size() > 0) {
            query.append("where ");
            for (Field field : listNotNull) {
                String column = Utilities.getColmunName(field);
                String method = Utilities.createGetter(field.getName());
                Object object = getClass().getDeclaredMethod(method).invoke(this);
                Column column1 = field.getAnnotation(Column.class);
                if (column1.isNumber()) {
                    query.append(column).append("=").append(object);
                } else {
                    query.append(column).append("='").append(object + "'");
                }

                if (i + 1 < listNotNull.size()) query.append(" and ");
                i++;
            }
        }
        return query.toString();
    }
    /**
     * Deletes the current object from the database
     * @param c Connection to the database
     * @throws SQLException if there is an error executing the SQL statement
     * @throws NullValue   if the primary key for the object is null
     * @throws InvocationTargetException if the getter method for the primary key throws an exception
     * @throws NoSuchMethodException     if the getter method for the primary key does not exist
     * @throws IllegalAccessException    if the getter method for the primary key cannot be accessed
     */
    public void delete(Connection c) throws SQLException, NullValue, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Statement statement = c.createStatement();
        System.out.println(deleteQuery());
        statement.execute(deleteQuery());
        System.out.println("delete from database");
    }

    /**
     * Creates an SQL query for deleting the current object from the database
     * @return a String representing the SQL query
     * @throws NullValue                 if the primary key for the object is null
     * @throws InvocationTargetException if the getter method for the primary key throws an exception
     * @throws NoSuchMethodException     if the getter method for the primary key does not exist
     * @throws IllegalAccessException    if the getter method for the primary key cannot be accessed
     */
    private String deleteQuery() throws NullValue, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        StringBuilder query = new StringBuilder(String.format("delete from  %s  ", table));
        Field primaryKey = Utilities.getPrimaryKeyIndex(this);
        String column = Utilities.getColmunName(primaryKey);
        String method = Utilities.createGetter(primaryKey.getName());
        Object object = getClass().getDeclaredMethod(method).invoke(this);
        query.append("where").append(" ").append(column + "=").append("'" + object + "'");
        return query.toString();
    }

    /**
     * Retrieves the objects that are referenced by foreign keys in the current object
     * @param c Connection to the database
     * @return a Vector of objects that are referenced by foreign keys in the current object
     * @throws InvocationTargetException if the getter method for a foreign key throws an exception
     * @throws NoSuchMethodException     if the getter method for a foreign key does not exist
     * @throws IllegalAccessException    if the getter method for a foreign key cannot be accessed
     * @throws InstantiationException    if the class for
     */
    @SuppressWarnings({"rawused", "rawtypes"})
    @Deprecated
    public Vector fkToObject(Connection c) throws Exception {
        Vector<Field> listFk = Utilities.findAllForeignKey(this);
        Vector<Object> fk = new Vector<>();
        for (Field field : listFk) {
            ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
            String column = Utilities.getColmunName(field);
            String value = (String) Utilities.getterValue(this, column);
            Object object = foreignKey.classReference().newInstance();
            String query = Utilities.queryFind(object, value);
            ResultSet rs = c.createStatement().executeQuery(query);
            while (rs.next()) {
                fk.add(Utilities.createObject(object, rs));
            }
        }
        return fk;
    }

    /**
     * Retrieves an object of the specified foreign key Class that is related to the current object
     * through the foreign key field at the specified index.
     * @param c     Connection to the database
     * @param index index of the foreign key field in the list of foreign key fields of the current object
     * @return an object of the specified foreign key Class
     * @throws SQLException              if a database access error occurs or this method is called on a closed connection
     * @throws InstantiationException    if the class that the object is an instance of has no nullary constructor
     * @throws IllegalAccessException    if the class or its nullary constructor is not accessible
     * @throws InvocationTargetException if the underlying constructor throws an exception
     * @throws NoSuchMethodException     if the property accessor method is not found
     * @throws NoForeignKey              if the specified index does not correspond to a foreign key field in the current object
     */
    @SuppressWarnings("ReassignedVariable")
    public <T> T fkToObject(Connection c, int index) throws SQLException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoForeignKey, NullValue {
        Vector<Field> listFk = Utilities.findAllForeignKey(this);
        if (index >= listFk.size() || index < 0) {
            throw new NoForeignKey("Index does not correspond to a foreign key field in the current object");
        }
        Field field = listFk.get(index);
        ForeignKey foreignKey1 = field.getAnnotation(ForeignKey.class);
        String temp = Utilities.getColmunName(field);
        String val = String.valueOf(Utilities.getterValue(this, temp));
        T t = (T) foreignKey1.classReference().newInstance();
        System.out.println(Utilities.queryFind(t, val));
        ResultSet rs = c.createStatement().executeQuery(Utilities.queryFind(t, val));
        while (rs.next()) {
            t = (T) Utilities.createObject(t, rs);
        }
        return t;
    }
    public void displayObject() {
        Utilities.displayFieldValues(this);
    }
}
