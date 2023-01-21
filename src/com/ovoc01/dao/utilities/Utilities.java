package com.ovoc01.dao.utilities;

import com.ovoc01.dao.annotation.Column;
import com.ovoc01.dao.annotation.ForeignKey;
import com.ovoc01.dao.annotation.PrimaryKey;
import com.ovoc01.dao.annotation.Tables;
import com.ovoc01.dao.excetpion.NoForeignKey;
import com.ovoc01.dao.excetpion.NullValue;
import com.ovoc01.dao.java.MyConnection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.Vector;

/**
* Intermediate class for object management
*@author ovoc01
* */
@SuppressWarnings("unused")
public class Utilities {

    static String host="localhost";
    static String port="5432";
    static String usr="rakharrs";
    static String dbname="star";
    static String pwd="pixel";

    public static Connection createConnection(){
        return  MyConnection.createPostGresConnection(host,port,usr,pwd,dbname);
    }

    /**
     * Create an object which is an instance of reference using the ResultSet
    *@param reference object to referee
     * @param rs a ResultSet which need to be transforms
     * @return an instance of reference object
    * */

    @SuppressWarnings("deprecated")
    public static Object createObject(Object reference, ResultSet rs) throws IllegalAccessException,
            InstantiationException, SQLException,NoSuchMethodException ,InvocationTargetException{
        Object result = reference.getClass().newInstance();
        Vector<Field> list = getFieldToInsert(reference);
        for (int i = 0; i < list.size(); i++) {
           Object object = rs.getObject(getColmunName(list.get(i)));
           //System.out.println(object);
           Class aClass = rs.getObject(getColmunName(list.get(i))).getClass();
           if (aClass== Date.class){
               reference.getClass().getDeclaredMethod(createSetter((list.get(i)).getName()), String.class).invoke(result,String.valueOf(object));
           }else if (aClass==BigDecimal.class){
               reference.getClass().getDeclaredMethod(createSetter((list.get(i)).getName()), Double.class).invoke(result,Double.parseDouble(String.valueOf(object)));
           }
           else{
               reference.getClass().getDeclaredMethod(createSetter((list.get(i)).getName()), aClass).invoke(result,object);
           }

        }
        return result;
    }

    /**
     * get the field index to insert
     * @param reference the object to referee
     * @return Vector<Integer>
     * */
    public static Vector<Field> getFieldToInsert(Object reference){
        Vector<Field> listIndex = new Vector<>();
        for (int i = 0;i<reference.getClass().getDeclaredFields().length;i++) {
            if(reference.getClass().getDeclaredFields()[i].isAnnotationPresent(Column.class)){
                listIndex.add(reference.getClass().getDeclaredFields()[i]);
            }
        }
        return listIndex;
    }


    /**
     * a function who capitalize the first letter of a String
     * @author chatGpt
     * @param input string to modify
     * @return String
     * */
    public static String capitalizeFirstLetter(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    /**
     * Get the primary key field of  the object
     * @param reference object to get reference
     * @return Field
     * */
    public static Field getPrimaryKeyIndex(Object reference) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, NullValue {
        for (int i = 0;i<reference.getClass().getDeclaredFields().length;i++) {
            if(reference.getClass().getDeclaredFields()[i].isAnnotationPresent(PrimaryKey.class)){
                Object temp = getterValue(reference,reference.getClass().getDeclaredFields()[i].getName());
               // if (temp==null) throw new NullValue("Primary key with a null value");
                return reference.getClass().getDeclaredFields()[i];
            }
        }
        return null;
    }


    /**
     * Function who create a getter using the field name
     * @param field
     * @return String
     * */
    public static String createGetter(String field){
        return "get"+capitalizeFirstLetter(field);
    }


    /**
     * Function who create a setter using the field name
     * @param field field to set setter
     * @return String
     * */
    public static String createSetter(String field){
        return "set"+capitalizeFirstLetter(field);
    }

    public static String getColmunName(Field field){
        Column column = field.getAnnotation(Column.class);
        if(!column.name().equals("")) return column.name();
        else return field.getName();
    }

    /**
     * Function who get the value of a specific field using the column name
     * @param ref object reference
     * @param  colName column name
     * @return Object
     * */
    public static Object getterValue(Object ref,String colName) throws NoSuchMethodException,IllegalAccessException , InvocationTargetException {
        String method = createGetter(colName);
        Object result = null;
        if(ref!=null){
            result = ref.getClass().getDeclaredMethod(method).invoke(ref);
        }
        return result;
    }

    /**
     * Function who create a table name
     * @param ref object reference
     * @return String
     * */
    public static String setTable(Object ref){
        Tables tables = ref.getClass().getAnnotation(Tables.class);
        if(tables!=null && !tables.name().equals("")){
            return tables.name();
        }else{
            return ref.getClass().getSimpleName().toLowerCase();
        }
    }




    public static String sqlCompleteZero(){
        String query ="create or replace function public.completezero(seq integer, prefix character varying) returns character varying " +
                "language plpgsql as" +
                " $$ declare limite integer;" +
                "alava integer;" +
                "ng varchar;" +
                "final varchar;" +
                "temp integer;" +
                "pref integer;" +
                "BEGIN " +
                "limite:=9;" +
                "select cast(seq as varchar) into ng;" +
                "select length(ng) into alava;" +
                "select length(prefix) into pref;" +
                "temp:=limite-(alava+pref);" +
                "Loop final:=concat(final,0);" +
                "temp:=temp-1;" +
                "exit when  temp = 0;" +
                "end loop;" +
                "return concat(final,ng);" +
                "end $$;";
        return query;
    }

    public static String createPkSeq(){
        String query="create or replace function public.getiddb(nameseq character varying, prefix character varying) returns character varying\n" +
                "    language plpgsql\n" +
                "as\n" +
                "$$\n" +
                " declare\n" +
                "    seq integer;\n" +
                "     id varchar;\n" +
                "    Begin\n" +
                "    \n" +
                "        select nextval(nameSeq) into seq;\n" +
                "        id:=concat(prefix,completeZero(seq,prefix));\n" +
                "        return id;\n" +
                "    End\n" +
                "$$;\n";
        return query;
    }

    /**
     *
     * */
    public static void prepareSql(Connection c) throws  Exception{
        Statement statement = c.createStatement();
        statement.execute(Utilities.sqlCompleteZero());
        statement.execute(Utilities.createPkSeq());
        c.commit();
    }

    public static Vector<Field> getNotNullField(Object reference) throws NoSuchMethodException,InvocationTargetException,IllegalAccessException{
        Vector<Field> list = getFieldToInsert(reference);
        Vector<Field> list2 = new Vector<>();
        for (Field field: list) {
            String method = createGetter(field.getName());
            if(reference.getClass().getDeclaredMethod(method).invoke(reference)!=null){
                list2.add(field);
            }
        }
        return list2;
    }

    /**
     * function how self its self in a database using is primaryKey
     * */
    @SuppressWarnings({"rawused","rawtypes"})
    public static Vector find(Connection c,Object ref) throws SQLException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException, NullValue {
        Statement statement = c.createStatement();
        Field primaryKey = Utilities.getPrimaryKeyIndex(ref);
        String colName = Utilities.getColmunName(primaryKey);
        Object primaryKeyValue = getterValue(ref,colName);
        System.out.println(String.format("Select * from %s where %s='%s'",setTable(ref),colName,primaryKeyValue));
        ResultSet rs = statement.executeQuery(String.format("Select * from %s where %s='%s'",setTable(ref),colName,primaryKeyValue));
        Vector<Object> list = new Vector();
        while(rs.next()){
            list.add(Utilities.createObject(ref,rs));
        }
        return list;
    }

    public static Vector<Field> findAllForeignKey(Object ref) throws  NoForeignKey{
        Vector<Field> listIndex = new Vector<>();
        for (int i = 0;i<ref.getClass().getDeclaredFields().length;i++) {
            if(ref.getClass().getDeclaredFields()[i].isAnnotationPresent(ForeignKey.class)){
                listIndex.add(ref.getClass().getDeclaredFields()[i]);
            }
        }
        if(listIndex.isEmpty()) throw new NoForeignKey("No foreign key present");
        return listIndex;
    }

    public static <T> T dynamicCast(Object obj, Class<T> cls) {
        return cls.cast(obj);
    }



    public static String  queryFind(Object ref,String id) throws NullValue, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String colName = Utilities.getColmunName(getPrimaryKeyIndex(ref));
        return String.format("select * from %s where %s = '%s'",setTable(ref),colName,id);
    }

    public static void displayFieldValues(Object obj) {
        Class<?> cls = obj.getClass();
        Field[] fields = cls.getDeclaredFields();
        String tableFormat = "| %-23s | %-23s |%n";
        System.out.format("+-------------------------+-------------------------+%n");
        System.out.format("| Field Name              | Field Value             |%n");
        System.out.format("+-------------------------+-------------------------+%n");

        for (Field f : fields) {
            try {
                f.setAccessible(true);
                System.out.format(tableFormat, f.getName(), f.get(obj));
            } catch (IllegalAccessException e) {
                System.out.format(tableFormat, f.getName(), "Error accessing field");
            }
        }
        System.out.format("+-------------------------+-------------------------+%n");
    }

}
