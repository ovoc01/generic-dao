import com.ovoc01.dao.connection.MyConnection;
import java.sql.Connection;

public class Main {
    public static void main(String[] args)  throws Exception{
        Connection c = MyConnection.createPostGresConnection("localhost","5432","postgres","password","postgres");

    }
}