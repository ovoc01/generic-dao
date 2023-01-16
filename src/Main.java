import com.ovoc01.dao.Person;
import com.ovoc01.dao.java.MyConnection;
import com.ovoc01.dao.utilities.Intermediate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class Main {
    public static void main(String[] args)  throws Exception{
        Connection c = MyConnection.createPostGresConnection("localhost","5432","postgres","password","postgres");
        new Person("Raselison","Toky",17).insert();
        Person person = new Person();
        Vector<Person> list = person.select(c);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getAge());
        }
    }
}