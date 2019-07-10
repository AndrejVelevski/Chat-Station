import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database
{
	static final String url = "jdbc:mysql://localhost:3306/";
	static final String user = "root";
	static final String password = "qwerty";
	static final String parametars = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	
	Connection connection;
	Statement statement = null;
	
	public Database(String name)
	{
		connection = null;
		statement = null;
		createDatabase(name);
		
		try
		{
			connection = DriverManager.getConnection(String.format("%s%s%s", url, name, parametars), user, password);
			statement = connection.createStatement();
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
		
		generateTables();
		
		
		//ovde eksplicitno gi staviv vo koi koloni zaradi id (poso e auto generate)
		/*String sql = "INSERT INTO user(username, email, password, fullname)"
				+ "VALUES('User2', 'user@user.com', 'user123', 'User Userovski')";
		try {
			statement.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

	private void createDatabase(String name)
	{
		try
		{
			System.out.println(String.format("Connecting to database '%s'...", url));
			connection = DriverManager.getConnection(String.format("%s%s", url, parametars), user, password);

		    System.out.println(String.format("Creating database '%s'...", name));
		    statement = connection.createStatement();
		      
		    String sql = String.format("CREATE DATABASE %s", name);
		    statement.executeUpdate(sql);
		    System.out.println(String.format("Database '%s' created successfully...", name));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		finally
		{
			if (statement != null)
				try {statement.close();} catch (SQLException e) {System.out.println(e.getMessage());}
			if (connection != null)
				try {connection.close();} catch (SQLException e) {System.out.println(e.getMessage());}
		}
	}
	
	private void generateTables()
	{
String sql;
		
		sql =
		  "CREATE TABLE user"
		+ "("
		+ "id INT AUTO_INCREMENT,"
		+ "username VARCHAR(50) UNIQUE NOT NULL,"
		+ "email VARCHAR(50) UNIQUE NOT NULL,"
		+ "password VARCHAR(50) NOT NULL,"
		+ "fullname VARCHAR(80),"
		+ "age INT,"
		+ "PRIMARY KEY (id)"
		+ ")"; 
		createTable("user", sql);
		
		sql =
		  "CREATE TABLE friends"
		+ "("
		+ "user1 INT NOT NULL,"
		+ "user2 INT NOT NULL,"
		+ "PRIMARY KEY (user1, user2),"
		+ "FOREIGN KEY (user1) REFERENCES user(id),"
		+ "FOREIGN KEY (user2) REFERENCES user(id)"
		+ ")";
		createTable("friends", sql);
		
		sql =
		  "CREATE TABLE room"
		+ "("
		+ "id INT AUTO_INCREMENT,"
		+ "name VARCHAR(50),"
		+ "PRIMARY KEY (id)"
		+ ")";
		createTable("room", sql);
		
		sql =
		  "CREATE TABLE room_members"
		+ "("
		+ "id INT AUTO_INCREMENT,"
		+ "room INT NOT NULL,"
		+ "user INT NOT NULL,"
		+ "PRIMARY KEY (id),"
		+ "FOREIGN KEY (room) REFERENCES room(id),"
		+ "FOREIGN KEY (user) REFERENCES user(id)"
		+ ")";
		createTable("room_members", sql);
		
		sql =
		  "CREATE TABLE messages"
		+ "("
		+ "id INT AUTO_INCREMENT,"
		+ "user INT NOT NULL,"
		+ "room INT NOT NULL,"
		+ "message VARCHAR(1000) NOT NULL,"
		+ "date DATETIME NOT NULL,"
		+ "PRIMARY KEY (id),"
		+ "FOREIGN KEY (user) REFERENCES user(id),"
		+ "FOREIGN KEY (room) REFERENCES room(id)"
		+ ")";
		createTable("messages", sql);
	}
	
	private void createTable(String name, String sql)
	{
		try
		{
			System.out.println(String.format("Creating table '%s'...", name));
			statement.execute(sql);
			System.out.println(String.format("Table '%s' create successfully...", name));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}
