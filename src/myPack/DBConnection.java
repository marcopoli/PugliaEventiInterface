package myPack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection 
{
private static Connection conn = null;

static Connection getConnection()
{
    try
    {
    	Class.forName("org.postgresql.Driver");
		if (conn == null) {
			conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1/PugliaEventi?characterEncoding=utf8", "postgres", "postgres");
		}
			return conn;     
    }
    catch(ClassNotFoundException cnfe)
    {
        System.out.println("Error loading class!");
        cnfe.printStackTrace();
    }
    catch(SQLException sqle)
    {
        System.out.println("Error connecting to the database!");
        sqle.printStackTrace();
    }
    return conn;
}//getConnection()
}//class