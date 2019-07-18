package Models;

import com.esotericsoftware.kryonet.Connection;

public class User
{
	public Connection connection;
	public String username;

	public User(Connection connection, String username)
	{
		this.connection = connection;
		this.username = username;
	}
}
