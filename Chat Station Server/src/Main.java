import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import Packets.MessageType;
import Packets.SystemMessage;
import Packets.User;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		Server server = new Server();
	    server.bind(54555);
	    
	    Kryo kryo = server.getKryo();
	    kryo.register(MessageType.class);
	    kryo.register(SystemMessage.class);
	    kryo.register(User.class);
	    
	    Database db = new Database("chatstation");
	    
	    server.start();
	    
	    SystemMessage systemMessage = new SystemMessage();
	    
	    server.addListener(new Listener()
	    {
	        public void received (Connection connection, Object object)
	        {
	        	if (object instanceof SystemMessage)
	            {
	        		//MessagePacket packet = (MessagePacket)object;
	        		//Arrays.stream(server.getConnections()).forEach(c -> c.sendTCP(packet));
	            }
	        	else if (object instanceof User)
	        	{
	        		User user = (User)object;
	        		try
	        		{
						db.addUser(user);
						systemMessage.type = MessageType.REGISTER_SUCCESS;
	        			systemMessage.message = "Registered successfully.";
	        			connection.sendTCP(systemMessage);
					}
	        		catch (AlreadyExistsException e)
	        		{
	        			systemMessage.type = MessageType.ERROR;
	        			systemMessage.message = e.getMessage();
	        			connection.sendTCP(systemMessage);
					}
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
