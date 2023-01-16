package com.ovoc01.dao.utilities;

import com.ovoc01.dao.annotation.Column;
import com.ovoc01.dao.annotation.PrimaryKey;
import com.ovoc01.dao.annotation.Tables;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

/**
* Intermediate class for object management
*@author ovoc01
* */
@SuppressWarnings("unused")
public class Intermediate {
    /**
     * Create an object which is an instance of reference using the ResultSet
    *@param reference object to referee
     * @param rs a ResultSet which need to be transforms
     * @return an instance of reference object
    * */

    public static Object createObject(Object reference, ResultSet rs) throws IllegalAccessException,
            InstantiationException, SQLException,NoSuchMethodException ,InvocationTargetException{
        Object result = reference.getClass().newInstance();
        Vector<Field> list = getFieldToInsert(reference);
        for (int i = 0; i < list.size(); i++) {
           Object object = rs.getObject(getColmunName(list.get(i)));
           // System.out.println(object);
           Class aClass = rs.getObject(getColmunName(list.get(i))).getClass();
            reference.getClass().getDeclaredMethod(createSetter(getColmunName(list.get(i))), aClass).invoke(result,object);

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
    public static Field getPrimaryKeyIndex(Object reference){
        for (int i = 0;i<reference.getClass().getDeclaredFields().length;i++) {
            if(reference.getClass().getDeclaredFields()[i].isAnnotationPresent(PrimaryKey.class)){
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
     * @param field
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
        statement.execute(Intermediate.sqlCompleteZero());
        statement.execute(Intermediate.createPkSeq());
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
}
