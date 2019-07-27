import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import Models.LastMessagePacket;
import Packets.FriendRequestPacket;
import Packets.FriendResponsePacket;
import Packets.LoginUserPacket;
import Packets.PrivateMessagePacket;
import Packets.ReceiveFriendRequestsPacket;
import Packets.ReceiveFriendsPacket;
import Packets.ReceiveLastMessagesPacket;
import Packets.ReceiveMessagesHistoryPacket;
import Packets.ReceiveUserPacket;
import Packets.RegisterUserPacket;
import Packets.RequestLastMessagesPacket;
import Packets.RequestMessagesHistoryPacket;

public class Database
{
	private static final String url = "jdbc:mysql://localhost:3306/";
	private static final String user = "root";
	private static final String password = ConfigConstants.db_password;
	private static final String parametars = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	
	private Connection connection;
	private PreparedStatement st;
	
	public Database(String name)
	{
		connection = null;
		createDatabase(name);
		
		try
		{
			connection = DriverManager.getConnection(String.format("%s%s%s", url, name, parametars), user, password);
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
		      
		    String sql = String.format("CREATE DATABASE %s", name);
		    st = connection.prepareStatement(sql);
		    st.executeUpdate();
		    System.out.println(String.format("Database '%s' created successfully...", name));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		finally
		{
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
		  "CREATE TABLE messages"
		+ "("
		+ "id INT AUTO_INCREMENT,"
		+ "user_from VARCHAR(50) NOT NULL,"
		+ "user_to VARCHAR(50) NOT NULL,"
		+ "message VARCHAR(1000) NOT NULL,"
		+ "date DATETIME NOT NULL,"
		+ "PRIMARY KEY (id),"
		+ "FOREIGN KEY (user_from) REFERENCES user(username),"
		+ "FOREIGN KEY (user_to) REFERENCES user(username)"
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
			st = connection.prepareStatement(sql);
			st.execute();
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
		
		try 
		{
			sql = "SELECT * FROM user " +
	              "WHERE email = ?";
			st = connection.prepareStatement(sql);
			st.setString(1, user.email);
			ResultSet rs = st.executeQuery();
			rs.last();
			if (rs.getRow() > 0)
				throw new EmailAlreadyExistsException(String.format("Email %s already exists.", user.email));
		} catch (SQLException e) {e.printStackTrace();}
		
		try 
		{
			sql = "SELECT * FROM user " +
		          "WHERE username = ?";
			st = connection.prepareStatement(sql);
			st.setString(1, user.username);
			ResultSet rs = st.executeQuery();
			rs.last();
			if (rs.getRow() > 0)
				throw new UsernameAlreadyExistsException(String.format("Username %s already exists.", user.username));
		} catch (SQLException e) {e.printStackTrace();}
		
		sql =
		String.format(
		  "INSERT INTO user(username, email, password, first_name, last_name, age, confirmed, registered_on) "
		+ "VALUES('%s', '%s', '%s', '%s', '%s', %d, %b, '%s');",
		user.username, user.email, user.password, user.first_name, user.last_name, user.age, true, LocalDateTime.now());
		try
		{
			sql = "INSERT INTO user(username, email, password, first_name, last_name, age, confirmed, registered_on) "
				+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?);";
			st = connection.prepareStatement(sql);
			st.setString(1, user.username);
			st.setString(2, user.email);
			st.setString(3, user.password);
			st.setString(4, user.first_name);
			st.setString(5, user.last_name);
			st.setInt(6, user.age);
			st.setBoolean(7, true);
			st.setString(8, LocalDateTime.now().toString());
			st.execute();
			//sendConfirmationCode(user.email, user.username);
		} catch (SQLException e){e.printStackTrace();}
	}
	
	public void loginUser(LoginUserPacket user) throws IncorrectUsernameOrPasswordException, AccountNotConfirmedException
	{
		String sql;
		
		try 
		{
			sql = "SELECT * FROM user "
				+ "WHERE (username = ? OR email = ?) AND password = ?;";
			st = connection.prepareStatement(sql);
			st.setString(1, user.username_email);
			st.setString(2, user.username_email);
			st.setString(3, user.password);
			ResultSet rs = st.executeQuery();
			rs.last();
			if (rs.getRow() > 0)
			{
				if (!rs.getBoolean("confirmed"))
				{
					throw new AccountNotConfirmedException(String.format("Account %s is not confirmed.", user.username_email));
				}
				
				sql = "UPDATE user "
					+ "SET last_login = ? "
					+ "WHERE username = ? OR email = ?;";
				st = connection.prepareStatement(sql);
				st.setString(1, LocalDateTime.now().toString());
				st.setString(2, user.username_email);
				st.setString(3, user.username_email);
				st.execute();
						
				return;
			}
			
		} catch (SQLException e) {e.printStackTrace();}
		
		throw new IncorrectUsernameOrPasswordException("Email or password are incorrect.");
	}
	
	public void sendConfirmationCode(String email, String username)
	{
		String code = generateConfirmationCode(8);
		String sql;
		try
		{
			sql = "UPDATE user "
				+ "SET confirm_code = ? "
				+ "WHERE username = ?;";
			st = connection.prepareStatement(sql);
			st.setString(1, code);
			st.setString(2, username);
			st.execute();
			
			Email.send(email, username, code);
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	public void confirmAccount(String username_email, String code) throws IncorrectConfirmationCodeException
	{
		String sql;
		
		try 
		{
			sql = "SELECT * FROM user "
				+ "WHERE username = ? OR email = ?;";
			st = connection.prepareStatement(sql);
			st.setString(1, username_email);
			st.setString(2, username_email);
			ResultSet rs = st.executeQuery();
			rs.first();
			
			if (rs.getString("confirm_code").equals(code))
			{
				sql = "UPDATE user "
					+ "SET confirmed = ? "
					+ "WHERE username = ? OR email = ?;";
				st = connection.prepareStatement(sql);
				st.setBoolean(1, true);
				st.setString(2, username_email);
				st.setString(3, username_email);
				
				st.execute();
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
		user.toSelf = false;
		String sql;
		
		ResultSet rs;
		try 
		{
			sql = "SELECT * FROM user "
				+ "WHERE (username = ? OR email = ?);";
			st = connection.prepareStatement(sql);
			st.setString(1, username_email);
			st.setString(2, username_email);
			rs = st.executeQuery();
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
			sql = "SELECT * FROM user "
				+ "WHERE username = ?;";
			st = connection.prepareStatement(sql);
			st.setString(1, packet.user_to);
			rs = st.executeQuery();
			rs.last();
			if (rs.getRow() == 0)
				throw new UsernameDoesntExistException(String.format("Username %s doesn't exist.", packet.user_to));
			
			sql = "SELECT * FROM friends "
				+ "WHERE user1 = ? AND user2 = ?;";
			st = connection.prepareStatement(sql);
			st.setString(1, packet.user_from);
			st.setString(2, packet.user_to);
			rs = st.executeQuery();
			rs.last();
			if (rs.getRow() > 0)
				throw new AlreadyFriendsException(String.format("You are already friends with %s.", packet.user_to));
			
			sql = "SELECT * FROM friend_requests "
				+ "WHERE user_from = ? AND user_to = ?;";
			st = connection.prepareStatement(sql);
			st.setString(1, packet.user_from);
			st.setString(2, packet.user_to);
			rs = st.executeQuery();
			rs.last();
			if (rs.getRow() > 0)
				throw new FriendRequestPendingException(String.format("Username %s already has a pending friend request.", packet.user_to));
			
			sql = "SELECT * FROM friend_requests "
				+ "WHERE user_from = ? AND user_to = ?;";
			st = connection.prepareStatement(sql);
			st.setString(1, packet.user_to);
			st.setString(2, packet.user_from);
			rs = st.executeQuery();
			rs.last();
			if (rs.getRow() > 0)
				throw new FriendRequestPendingException(String.format("You already have a pending friend request from %s.", packet.user_to));
			
			sql = "INSERT INTO friend_requests "
				+ "VALUES(?, ?);";
			st = connection.prepareStatement(sql);
			st.setString(1, packet.user_from);
			st.setString(2, packet.user_to);
			st.execute();
			
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	public void respondToFriendRequest(FriendResponsePacket packet)
	{
		String sql;
		
		if (packet.type == FriendResponsePacket.Type.ACCEPT)
		{
			try
			{
				sql = "INSERT INTO friends "
					+ "VALUES(?, ?);";
				st = connection.prepareStatement(sql);
				st.setString(1, packet.user_from);
				st.setString(2, packet.user_to);
				st.execute();
				
				sql = "DELETE FROM friend_requests "
					+ "WHERE user_from = ? AND user_to = ?;";
				st = connection.prepareStatement(sql);
				st.setString(1, packet.user_from);
				st.setString(2, packet.user_to);
				st.execute();
			}
			catch (SQLException e) {e.printStackTrace();}
		}
		else
		{
			try
			{
				sql = "DELETE FROM friend_requests "
					+ "WHERE user_from = ? AND user_to = ?;";
				st = connection.prepareStatement(sql);
				st.setString(1, packet.user_from);
				st.setString(2, packet.user_to);
				st.execute();
			} catch (SQLException e) {e.printStackTrace();}
		}
	}

	public ReceiveFriendRequestsPacket getFriendRequests(String username)
	{
		ReceiveFriendRequestsPacket packet = new ReceiveFriendRequestsPacket();
		
		String sql;
		
		String[] usernames = null;
		
		ResultSet rs;
		try 
		{
			sql = "SELECT * FROM friend_requests "
				+ "WHERE user_to = ?;";
			st = connection.prepareStatement(sql);
			st.setString(1, username);
			rs = st.executeQuery();
			
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
		
		List<String> u = new ArrayList<String>();
		
		try
		{
			sql = "SELECT * FROM friends "
				+ "WHERE user1 = ? OR user2 = ?;";
			st = connection.prepareStatement(sql);
			st.setString(1, username);
			st.setString(2, username);
			ResultSet rs = st.executeQuery();
			
			while(rs.next())
			{
				if (rs.getString("user1").equals(username))
				{
					u.add(rs.getString("user2"));
				}
				else
				{
					u.add(rs.getString("user1"));
				}
			}
			
		} catch (SQLException e) {e.printStackTrace();}
		
		List<ReceiveUserPacket> users = u.stream().map(user -> getUser(user)).collect(Collectors.toList());
		
		packet.users = users;
		
		return packet;
	}
	
	public ReceiveMessagesHistoryPacket requestMessagesHistory(RequestMessagesHistoryPacket packet)
	{
		ReceiveMessagesHistoryPacket p = new ReceiveMessagesHistoryPacket();
	
		String sql;
		
		List<PrivateMessagePacket> messages = new ArrayList<PrivateMessagePacket>();
		
		try
		{
			sql = "SELECT * FROM messages "
				+ "WHERE (user_from = ? AND user_to = ?) OR (user_from = ? AND user_to = ?);";
			st = connection.prepareStatement(sql);
			st.setString(1, packet.user_from);
			st.setString(2, packet.user_to);
			st.setString(3, packet.user_to);
			st.setString(4, packet.user_from);

			ResultSet rs = st.executeQuery();
			
			while(rs.next())
			{
				PrivateMessagePacket msg = new PrivateMessagePacket();
				msg.user_from = rs.getString("user_from");
				msg.user_to = rs.getString("user_to");
				msg.messaage = rs.getString("message");
				msg.date = rs.getString("date");
				
				messages.add(msg);
			}
		}
		catch (SQLException e) {e.printStackTrace();}
		
		p.messages = messages;
		
		return p;
	}
	
	public void sendPrivateMessage(PrivateMessagePacket packet)
	{
		String sql;
		
		try
		{
			sql = "INSERT INTO messages(user_from, user_to, message, date) " +
				  "VALUES(?, ?, ?, ?);";
			st = connection.prepareStatement(sql);
			st.setString(1, packet.user_from);
			st.setString(2, packet.user_to);
			st.setString(3, packet.messaage);
			st.setString(4, packet.date);
			st.execute();
		}
		catch(SQLException e) {e.printStackTrace();}
	}
	
	public ReceiveLastMessagesPacket requestLastMessages(RequestLastMessagesPacket packet)
	{
		String sql;
		
		ReceiveLastMessagesPacket p = new ReceiveLastMessagesPacket();
		
		List<LastMessagePacket> messages = new ArrayList();
		
		try
		{
			sql = "SELECT user_from, first_name, last_name, message, date FROM messages m JOIN user u ON m.user_from = u.username " + 
				  "WHERE date IN (SELECT MAX(date) FROM messages " +
				  "               WHERE user_to = ? " +
				  "               GROUP BY user_from)" +
				  "ORDER BY date DESC;";
			st = connection.prepareStatement(sql);
			st.setString(1, packet.username);
			
			ResultSet rs = st.executeQuery();
			while(rs.next())
			{
				LastMessagePacket msg = new LastMessagePacket();		
				msg.username = rs.getString("user_from");
				msg.first_name = rs.getString("first_name");
				msg.last_name = rs.getString("last_name");
				msg.message = rs.getString("message");
				msg.date = rs.getString("date");
				if(msg.message.length() > 45)
				{
					msg.message = String.format("%s...", msg.message.substring(0,42));
		        }
				messages.add(msg);
			}
		}
		catch(SQLException e) {e.printStackTrace();}
		
		p.messages = messages;
		
		return p;
	}
	
	private String generateConfirmationCode(int length)
	{
		return new Random().ints((int)'0',(int)'Z'+1).filter(i-> (i<=(int)'9' || i>=(int)'A'))
                .mapToObj(i -> String.valueOf((char)i)).limit(length).collect(Collectors.joining());
	}

	

	
}
