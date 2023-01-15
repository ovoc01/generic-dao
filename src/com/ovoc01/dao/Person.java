package com.ovoc01.dao;

import com.ovoc01.dao.annotation.Column;
import com.ovoc01.dao.annotation.Number;
import com.ovoc01.dao.annotation.PrimaryKey;
import com.ovoc01.dao.annotation.Tables;
import com.ovoc01.dao.java.ObjectDAO;

@Tables()
@SuppressWarnings("unused")
public class Person extends ObjectDAO {
    @Column
    @PrimaryKey(prefix = "PRS",seqComp = "PersSeq")
    String idPerson;
    @Column(name = "name")
    String nom;

    String prenom;
    @Column @Number
    Integer age;

    public String getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(String idPerson) {
        this.idPerson = idPerson;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
