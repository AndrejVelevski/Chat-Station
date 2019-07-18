import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.Collectors;

import Config.ConfigConstants;
import Exceptions.AccountNotConfirmedException;
import Exceptions.EmailAlreadyExistsException;
import Exceptions.IncorrectConfirmationCodeException;
import Exceptions.IncorrectUsernameOrPasswordException;
import Exceptions.UsernameAlreadyExistsException;
import Packets.LoginUserPacket;
import Packets.ReceiveUserPacket;
import Packets.RegisterUserPacket;

public class Database
{
	private static final String url = "jdbc:mysql://localhost:3306/";
	private static final String user = "root";
	private static final String password = ConfigConstants.db_password;
	private static final String parametars = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	
	private Connection connection;
	private Statement statement;
	
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
	}

	private void createDatabase(String name)
	{
		try
		{
			System.out.println(String.format("Connecting to database '%s'...", url));
			connection = DriverManager.getConnection(String.format("%s%s", url, parametars), user, password);
			System.out.println("Connected");

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
		+ "email VARCHAR(50) UNIQUE NOT NULL,"
		+ "username VARCHAR(50) UNIQUE NOT NULL,"
		+ "password VARCHAR(256) NOT NULL,"
		+ "first_name VARCHAR(50),"
		+ "last_name VARCHAR(50),"
		+ "age INT,"
		+ "confirmed BOOL NOT NULL,"
		+ "confirm_code VARCHAR(8),"
		+ "registered_on DATETIME,"
		+ "last_login DATETIME,"
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
	
	public void registerUser(RegisterUserPacket user) throws EmailAlreadyExistsException, UsernameAlreadyExistsException
	{
		String sql;
		
		//Check if email already exists
		sql = 
		String.format(
		  "SELECT * FROM user "
		+ "WHERE email = '%s';",
		user.email);
		try 
		{
			ResultSet rs = statement.executeQuery(sql);
			rs.last();
			if (rs.getRow() > 0)
				throw new EmailAlreadyExistsException(String.format("Email %s already exists.", user.email));
		} catch (SQLException e) {e.printStackTrace();}
		
		//Check if username already exists
		sql = 
		String.format(
		  "SELECT * FROM user "
		+ "WHERE username = '%s';",
		user.username);
		try 
		{
			ResultSet rs = statement.executeQuery(sql);
			rs.last();
			if (rs.getRow() > 0)
				throw new UsernameAlreadyExistsException(String.format("Username %s already exists.", user.username));
		} catch (SQLException e) {e.printStackTrace();}
		
		//Add user to database
		sql =
		String.format(
		  "INSERT INTO user(username, email, password, first_name, last_name, age, confirmed, registered_on) "
		+ "VALUES('%s', '%s', '%s', '%s', '%s', %d, %b, '%s');",
		user.username, user.email, user.password, user.first_name, user.last_name, user.age, true, LocalDateTime.now());
		try
		{
			statement.execute(sql);
			//sendConfirmationCode(user.email, user.username);
		} catch (SQLException e){e.printStackTrace();}
	}
	
	public void loginUser(LoginUserPacket user) throws IncorrectUsernameOrPasswordException, AccountNotConfirmedException
	{
		String sql;
		
		sql = 
		String.format(
		  "SELECT * FROM user "
		+ "WHERE (email = '%s' AND password = '%s');",
		user.email, user.password);
		try 
		{
			ResultSet rs = statement.executeQuery(sql);
			rs.last();
			if (rs.getRow() > 0)
			{
				if (!rs.getBoolean("confirmed"))
				{
					throw new AccountNotConfirmedException(String.format("Account %s is not confirmed.", user.email));
				}
				
				sql = String.format(
						  "UPDATE user "
						+ "SET last_login = '%s' "
						+ "WHERE email = '%s';",
						LocalDateTime.now(), user.email);
				
				statement.execute(sql);
						
				return;
			}
			
		} catch (SQLException e) {e.printStackTrace();}
		
		throw new IncorrectUsernameOrPasswordException("Email or password are incorrect.");
	}
	
	public void sendConfirmationCode(String email, String username)
	{
		String code = generateConfirmationCode(8);
		String sql = String.format(
			  "UPDATE user "
			+ "SET confirm_code = '%s' "
			+ "WHERE username = '%s';",
			code, username);
		
		try
		{
			statement.execute(sql);
			Email.send(email, username, code);
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	public void confirmAccount(String email, String code) throws IncorrectConfirmationCodeException
	{
		String sql;
		
		sql = 
		String.format(
		  "SELECT * FROM user "
		+ "WHERE email = '%s';",
		email);
		
		try 
		{
			ResultSet rs = statement.executeQuery(sql);
			rs.first();
			
			if (rs.getString("confirm_code").equals(code))
			{
				sql = String.format(
						  "UPDATE user "
						+ "SET confirmed = %b "
						+ "WHERE email = '%s';",
						true, email);
				
				statement.execute(sql);
			}
			else
			{
				throw new IncorrectConfirmationCodeException("The confirmation code you enered is incorrect or expired.");
			}
			
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	public ReceiveUserPacket getUser(String username_email)
	{
		ReceiveUserPacket user = new ReceiveUserPacket();
		
		String sql = 
		String.format(
		  "SELECT * FROM user "
		+ "WHERE (email = '%s' OR username = '%s');",
		username_email, username_email);
		
		ResultSet rs;
		try 
		{
			rs = statement.executeQuery(sql);
			rs.first();
			
			user.email = rs.getString("email");
			user.username = rs.getString("username");
			user.first_name = rs.getString("first_name");
			user.last_name = rs.getString("last_name");
			user.age = rs.getInt("age");
			user.registered_on = rs.getString("registered_on");
			user.last_login = rs.getString("last_login");
		} catch (SQLException e) {e.printStackTrace();}
		
		return user;
	}
	
	private String generateConfirmationCode(int length)
	{
		return new Random().ints((int)'0',(int)'Z'+1).filter(i-> (i<=(int)'9' || i>=(int)'A'))
                .mapToObj(i -> String.valueOf((char)i)).limit(length).collect(Collectors.joining());
	}
}
