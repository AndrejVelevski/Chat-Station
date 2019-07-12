import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import org.mindrot.jbcrypt.BCrypt;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import Config.MessageType;
import Packets.SystemMessagePacket;
import Packets.LoginPacket;
import Packets.ReceiveUserPacket;
import Packets.RegisterPacket;
import Packets.RequestUserPacket;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		Server server = new Server();
	    server.bind(54555);
	    
	    Kryo kryo = server.getKryo();
	    kryo.register(MessageType.class);
	    kryo.register(SystemMessagePacket.class);
	    kryo.register(RegisterPacket.class);
	    kryo.register(LoginPacket.class);
	    kryo.register(RequestUserPacket.class);
	    kryo.register(ReceiveUserPacket.class);
	    
	    Database db = new Database("chatstation");
	    
	    server.start();
	    
	    SystemMessagePacket systemMessage = new SystemMessagePacket();
	    
	    server.addListener(new Listener()
	    {
	        public void received (Connection connection, Object object)
	        {
	        	if (object instanceof RegisterPacket)
	        	{
	        		RegisterPacket user = (RegisterPacket)object;
	        		try
	        		{
						db.registerUser(user);
						systemMessage.type = MessageType.REGISTER_SUCCESS;
	        			systemMessage.message = "Registered successfully.";
	        			connection.sendTCP(systemMessage);
	        			System.out.println(String.format("Registered user '%s %s'...", user.email, user.username));
					}
	        		catch (ErrorException e)
	        		{
	        			systemMessage.type = MessageType.REGISTER_FAILED;
	        			systemMessage.message = e.getMessage();
	        			connection.sendTCP(systemMessage);
					}
	        	}
	        	else if (object instanceof LoginPacket)
	        	{
	        		LoginPacket user = (LoginPacket)object;
	        		try
	        		{
						db.loginUser(user);
						systemMessage.type = MessageType.LOGIN_SUCCESS;
	        			systemMessage.message = "Logged in successfully.";
	        			connection.sendTCP(systemMessage);
	        			System.out.println(String.format("Logged in user '%s'...", user.usernameEmail));
					}
	        		catch (ErrorException e)
	        		{
	        			systemMessage.type = MessageType.LOGIN_FAILED;
	        			systemMessage.message = e.getMessage();
	        			connection.sendTCP(systemMessage);
					}
	        	}
	        	else if (object instanceof RequestUserPacket)
	        	{
	        		RequestUserPacket requestUser = (RequestUserPacket)object;
	        		ReceiveUserPacket receiveUser = db.getUser(requestUser.usernameEmail);
	        		connection.sendTCP(receiveUser);
	        	}
	        }
	     });
	    
	    boolean systemOnline = true;
	    
	    Scanner scan = new Scanner(System.in);
	    
	    while(systemOnline)
	    {
	    	String command = scan.nextLine();
	    	
	    	switch (command)
	    	{
		    	case "/stop":
		    	{
		    		systemMessage.type = MessageType.SERVER_CLOSED;
		    		systemMessage.message = "Server closed.";
		    		Arrays.stream(server.getConnections()).forEach(c -> c.sendTCP(systemMessage));
		    		
		    		server.close();
		    		server.stop();
		    		systemOnline = false;
		    		System.out.println("Server closed.");
		    		
		    		break;
		    	}
		    	case "/connections":
		    	{
		    		Connection[] connections = server.getConnections();
		    		System.out.println("Total connections: " + connections.length);
		    		Arrays.stream(connections).forEach(c -> System.out.println(c.getRemoteAddressTCP()));
		    		break;
		    	}
	    	}
	    }
	    
		scan.close();
	}

}
