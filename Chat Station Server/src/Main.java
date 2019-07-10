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

public class Main
{
	public static void main(String[] args) throws IOException
	{
		Server server = new Server();
	    server.bind(54555);
	    
	    Kryo kryo = server.getKryo();
	    kryo.register(MessagePacket.class);
	    
	    server.start();
	    
	    MessagePacket mp = new MessagePacket();
	    
	    server.addListener(new Listener()
	    {
	        public void received (Connection connection, Object object)
	        {
	        	if (object instanceof MessagePacket)
	            {
	        		MessagePacket packet = (MessagePacket)object;
	        		
	        		Arrays.stream(server.getConnections()).forEach(c -> c.sendTCP(packet));
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
		    	case "/test":
		    	{
		    		mp.text = "Testerinoo";
		    		Arrays.stream(server.getConnections()).forEach(c -> c.sendTCP(mp));
		    		break;
		    	}
		    	case "/sql":
		    	{
		    		Database db = new Database("chatstation");
		    	}
	    	}
	    }
	    
		scan.close();
	}

}
