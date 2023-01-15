package com.ovoc01.dao.utilities;
/**
*@author ovoc01
* */

import com.ovoc01.dao.annotation.Column;
import com.ovoc01.dao.annotation.PrimaryKey;
import com.ovoc01.dao.annotation.Tables;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.Vector;

/**
* Intermediate class for object management
*
* */
@SuppressWarnings("unused")
public class Intermediate {
    /**
     * Create an object which is an instance of reference using the ResultSet
    *@param reference object to referee
     * @param rs a ResultSet which need to be transforms
     * @return an instance of reference object
    * */
    public static Object createObject(Object reference, ResultSet rs){
        return null;
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
    public static Field getPrimaryKeyIndex(Object reference){
        for (int i = 0;i<reference.getClass().getDeclaredFields().length;i++) {
            if(reference.getClass().getDeclaredFields()[i].isAnnotationPresent(PrimaryKey.class)){
                return reference.getClass().getDeclaredFields()[i];
            }
        }
        return null;
    }

    public static String createGetter(String field){
        return "get"+capitalizeFirstLetter(field);
    }

    public static String createSetter(String field){
        return "set"+capitalizeFirstLetter(field);
    }

    public static String getColmunName(){
        return null;
    }

    public static Object getterValue(Object ref,String colName) throws NoSuchMethodException,IllegalAccessException , InvocationTargetException {
        String method = createGetter(colName);
        Object result = null;
        if(ref!=null){
            result = ref.getClass().getDeclaredMethod(method).invoke(ref);
        }
        return result;
    }

    public static String setTable(Object ref){
        Tables tables = ref.getClass().getAnnotation(Tables.class);
        if(tables!=null && !tables.name().equals("")){
            return tables.name();
        }else{
            return ref.getClass().getSimpleName().toLowerCase();
        }
    }
}
