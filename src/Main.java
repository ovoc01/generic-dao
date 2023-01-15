import com.ovoc01.dao.Person;
import com.ovoc01.dao.java.MyConnection;
import com.ovoc01.dao.utilities.Intermediate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args)  throws Exception{
        Person person = new Person();
        person.setNom("Mirindra");
        person.setPrenom("Orerk");
        person.setIdPerson("PRS0001");
        person.setAge(18);
        System.out.println(person.insertQuery());
    }
}