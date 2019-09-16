import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.esotericsoftware.kryonet.Server;

import Models.ChatRoom;
import Models.User;
import Packets.MessagePacket;

public class CheckConnectionsThread extends Thread
{
	private Server server;
	private Map<Integer, User> connectedUsers;
	private Set<Integer> remove;
	private List<ChatRoom> chatrooms;
	
	public CheckConnectionsThread(Server server, Map<Integer, User> connectedUsers, List<ChatRoom> chatrooms)
	{
		this.server = server;
		this.connectedUsers = connectedUsers;
		remove = new HashSet<Integer>();
		this.chatrooms = chatrooms;
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			remove.clear();
			
			Set<Integer> connections = Arrays.stream(server.getConnections()).map(c-> c.getID()).collect(Collectors.toSet());
    		
	    	connectedUsers.keySet().stream().forEach(id -> {
	    		
	    		if (!connections.contains(id))
	    		{
	    			System.out.println(String.format("User '%s' lost connection.", connectedUsers.get(id).username));
	    			remove.add(id);
	    		}
	    	});
	    	
	    	remove.stream().forEach(id -> {
	    		ChatRoom chatroom = chatrooms.stream().filter(c->c.users.stream().anyMatch(u -> u.connection.getID()==id)).findFirst().orElse(null);
				
				if (chatroom != null)
				{
					chatroom.users.remove(connectedUsers.get(id));
					
					MessagePacket packet = new MessagePacket();
					packet.type = packet.type.LEAVE;
					packet.username = connectedUsers.get(id).username;
					System.out.println(connectedUsers.get(id).username);
					packet.message = String.format("%s has left the chat.", connectedUsers.get(id).username);
					packet.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
					
					chatroom.users.stream().forEach(u -> u.connection.sendTCP(packet));
					
					if (chatroom.users.size() == 0)
					{
						chatrooms.remove(chatroom);
						return;
					}
				}
	    	});

			remove.stream().forEach(id -> connectedUsers.remove(id));
	    	
	    	try
	    	{
				Thread.sleep(1000);
			}
	    	catch (InterruptedException e)
	    	{
				e.printStackTrace();
			}
		}
	}

}
