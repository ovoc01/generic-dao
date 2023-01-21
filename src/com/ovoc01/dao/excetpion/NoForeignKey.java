package com.ovoc01.dao.excetpion;

public class NoForeignKey extends  Exception{
    public NoForeignKey(String message){
        super(message);
    }
}
