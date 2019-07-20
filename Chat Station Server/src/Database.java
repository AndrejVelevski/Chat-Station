import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import Config.ConfigConstants;
import Exceptions.AccountNotConfirmedException;
import Exceptions.AlreadyFriendsException;
import Exceptions.EmailAlreadyExistsException;
import Exceptions.FriendRequestPendingException;
import Exceptions.IncorrectConfirmationCodeException;
import Exceptions.IncorrectUsernameOrPasswordException;
import Exceptions.UsernameAlreadyExistsException;
import Exceptions.UsernameDoesntExistException;
import Packets.FriendRequestPacket;
import Packets.FriendResponsePacket;
import Packets.LoginUserPacket;
import Packets.ReceiveFriendRequestsPacket;
import Packets.ReceiveFriendsPacket;
import Packets.ReceiveUserPacket;
import Packets.RegisterUserPacket;
import Packets.RequestFriendsPacket;

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
		+ "user1 VARCHAR(50) NOT NULL,"
		+ "user2 VARCHAR(50) NOT NULL,"
		+ "PRIMARY KEY (user1, user2),"
		+ "FOREIGN KEY (user1) REFERENCES user(username),"
		+ "FOREIGN KEY (user2) REFERENCES user(username)"
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
		+ "user VARCHAR(50) NOT NULL,"
		+ "PRIMARY KEY (id),"
		+ "FOREIGN KEY (room) REFERENCES room(id),"
		+ "FOREIGN KEY (user) REFERENCES user(username)"
		+ ")";
		createTable("room_members", sql);
		
		sql =
		  "CREATE TABLE messages"
		+ "("
		+ "id INT AUTO_INCREMENT,"
		+ "user VARCHAR(50) NOT NULL,"
		+ "room INT NOT NULL,"
		+ "message VARCHAR(1000) NOT NULL,"
		+ "date DATETIME NOT NULL,"
		+ "PRIMARY KEY (id),"
		+ "FOREIGN KEY (user) REFERENCES user(username),"
		+ "FOREIGN KEY (room) REFERENCES room(id)"
		+ ")";
		createTable("messages", sql);
		
		sql =
	      "CREATE TABLE friend_requests"
	    + "("
	    + "user_from VARCHAR(50) NOT NULL,"
	    + "user_to VARCHAR(50) NOT NULL,"
	    + "PRIMARY KEY (user_from, user_to),"
	    + "FOREIGN KEY (user_from) REFERENCES user(username),"
	    + "FOREIGN KEY (user_to) REFERENCES user(username)"
	    + ")";
	    createTable("friend_requests", sql);
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
		
		sql =
		String.format(
		  "INSERT INTO user(username, email, password, first_name, last_name, age, confirmed, registered_on) "
		+ "VALUES('%s', '%s', '%s', '%s', '%s', %d, %b, '%s');",
		user.username, user.email, user.password, user.first_name, user.last_name, user.age, false, LocalDateTime.now());
		try
		{
			statement.execute(sql);
			sendConfirmationCode(user.email, user.username);
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
	
	public void sendFriendRequest(FriendRequestPacket packet) throws UsernameDoesntExistException, FriendRequestPendingException, AlreadyFriendsException
	{
		String sql;
		ResultSet rs;
		
		try 
		{
			sql = 
				String.format(
				  "SELECT * FROM user "
				+ "WHERE username = '%s';",
				packet.user_to);
			rs = statement.executeQuery(sql);
			rs.last();
			if (rs.getRow() == 0)
				throw new UsernameDoesntExistException(String.format("Username %s doesn't exist.", packet.user_to));
			
			sql = 
			String.format(
			  "SELECT * FROM friends "
			+ "WHERE user1 = '%s' AND user2 = '%s';",
			packet.user_from, packet.user_to);
			rs = statement.executeQuery(sql);
			rs.last();
			if (rs.getRow() > 0)
				throw new AlreadyFriendsException(String.format("You are already friends with %s.", packet.user_to));
			
			sql = 
				String.format(
				  "SELECT * FROM friend_requests "
				+ "WHERE user_from = '%s' AND user_to = '%s';",
				packet.user_from, packet.user_to);
			rs = statement.executeQuery(sql);
			rs.last();
			if (rs.getRow() > 0)
				throw new FriendRequestPendingException(String.format("Username %s already has a pending friend request.", packet.user_to));
			
			sql = 
				String.format(
				  "SELECT * FROM friend_requests "
				+ "WHERE user_from = '%s' AND user_to = '%s';",
				packet.user_to, packet.user_from);
			rs = statement.executeQuery(sql);
			rs.last();
			if (rs.getRow() > 0)
				throw new FriendRequestPendingException(String.format("You already have a pending friend request from %s.", packet.user_to));
			
			sql = 
				String.format(
				  "INSERT INTO friend_requests "
				+ "VALUES('%s', '%s');",
				packet.user_from, packet.user_to);
			statement.execute(sql);
			
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	public void respondToFriendRequest(FriendResponsePacket packet)
	{
		String sql;
		
		if (packet.type == FriendResponsePacket.Type.ACCEPT)
		{
			try
			{
				sql = 
				String.format(
				  "INSERT INTO friends "
				+ "VALUES('%s', '%s');",
				packet.user_from, packet.user_to);
				statement.execute(sql);
				
				sql = 
				String.format(
				  "DELETE FROM friend_requests "
				+ "WHERE user_from = '%s' AND user_to = '%s';",
				packet.user_from, packet.user_to);
				statement.execute(sql);
			}
			catch (SQLException e) {e.printStackTrace();}
		}
		else
		{
			try
			{
				sql = 
				String.format(
				  "DELETE FROM friend_requests "
				+ "WHERE user_from = '%s' AND user_to = '%s';",
				packet.user_from, packet.user_to);
				statement.execute(sql);
			} catch (SQLException e) {e.printStackTrace();}
		}
	}

	public ReceiveFriendRequestsPacket getFriendRequests(String username)
	{
		ReceiveFriendRequestsPacket packet = new ReceiveFriendRequestsPacket();
		
		String sql = 
		String.format(
		  "SELECT * FROM friend_requests "
		+ "WHERE user_to = '%s';",
		username);
		
		String[] usernames = null;
		
		ResultSet rs;
		try 
		{
			rs = statement.executeQuery(sql);
			
			rs.last();
			usernames = new String[rs.getRow()];
			rs.beforeFirst();
			
			int i=0;
			while (rs.next())
			{
				usernames[i++] = (String.format("%s", rs.getString("user_from")));
			}
		} catch (SQLException e) {e.printStackTrace();}
		
		packet.usernames = usernames;
		
		return packet;
	}

	public ReceiveFriendsPacket getFriends(String username)
	{
		String sql;
		
		ReceiveFriendsPacket packet = new ReceiveFriendsPacket();
		
		String[] usernames = null;
		
		sql = 
		String.format(
		  "SELECT * FROM friends "
		+ "WHERE user1 = '%s' OR user2 = '%s';",
		username, username);
		
		try
		{
			ResultSet rs = statement.executeQuery(sql);
			rs.last();
			usernames = new String[rs.getRow()];
			rs.beforeFirst();
			
			int i=0;
			while(rs.next())
			{
				if (rs.getString("user1").equals(username))
				{
					usernames[i++] = rs.getString("user2");
				}
				else
				{
					usernames[i++] = rs.getString("user1");
				}
				
			}
			
		} catch (SQLException e) {e.printStackTrace();}
		
		packet.usernames = usernames;
		
		return packet;
	}
	
	private String generateConfirmationCode(int length)
	{
		return new Random().ints((int)'0',(int)'Z'+1).filter(i-> (i<=(int)'9' || i>=(int)'A'))
                .mapToObj(i -> String.valueOf((char)i)).limit(length).collect(Collectors.joining());
	}

	
}
