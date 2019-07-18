import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

import Exceptions.AccountNotConfirmedException;
import Exceptions.EmailAlreadyExistsException;
import Exceptions.IncorrectConfirmationCodeException;
import Exceptions.IncorrectUsernameOrPasswordException;
import Exceptions.UsernameAlreadyExistsException;
import Models.ChatRoom;
import Models.User;
import Packets.*;

public class Main
{
	static Database db;
	static SystemMessagePacket systemMessagePacket;
	static Map<Integer, User> connectedUsers;
	static List<ChatRoom> chatrooms;
	
	public static void main(String[] args) throws IOException
	{
		Server server = new Server();
	    server.bind(54555);
	    
	    Kryo kryo = server.getKryo();
	    kryo.register(SystemMessagePacket.Type.class);
        kryo.register(SystemMessagePacket.class);
        kryo.register(RegisterUserPacket.class);
        kryo.register(LoginUserPacket.class);
        kryo.register(ConfirmUserPacket.class);
        kryo.register(ResendCodePacket.class);
        kryo.register(RequestUserPacket.class);
        kryo.register(ReceiveUserPacket.class);
        kryo.register(RequestRandomChatPacket.class);
        kryo.register(ReceiveRandomChatPacket.class);
        kryo.register(MessagePacket.Type.class);
        kryo.register(MessagePacket.class);
	    
	    db = new Database("chatstation");
	    connectedUsers = new HashMap<Integer, User>();
	    chatrooms = new ArrayList<ChatRoom>();
	    chatrooms.add(new ChatRoom(8, new HashSet<String>(), new ArrayList<User>()));
	    chatrooms.add(new ChatRoom(19, new HashSet<String>(), new ArrayList<User>()));
	    chatrooms.add(new ChatRoom(5, new HashSet<String>(), new ArrayList<User>()));
	    chatrooms.add(new ChatRoom(13, new HashSet<String>(), new ArrayList<User>()));
	    chatrooms.add(new ChatRoom(2, new HashSet<String>(), new ArrayList<User>()));
	    chatrooms.add(new ChatRoom(4, new HashSet<String>(), new ArrayList<User>()));
	    chatrooms.add(new ChatRoom(20, new HashSet<String>(), new ArrayList<User>()));
	    chatrooms.add(new ChatRoom(3, new HashSet<String>(), new ArrayList<User>()));
	    chatrooms.add(new ChatRoom(7, new HashSet<String>(), new ArrayList<User>()));
	    chatrooms.add(new ChatRoom(16, new HashSet<String>(), new ArrayList<User>()));
	    
	    server.start();
	    
	    systemMessagePacket = new SystemMessagePacket();
	    
	    server.addListener(new Listener()
	    {
	        public void received (Connection connection, Object object)
	        {
	        	if (object instanceof SystemMessagePacket)
	        	{
	        		SystemMessagePacket packet = (SystemMessagePacket)object;
	        		
	        		switch (packet.type)
	        		{
		        		case LOGOUT:
		        		{
		        			logoutUser(connection, packet);
		        			break;
		        		}
	        		}
	        	}
	        	else if (object instanceof RegisterUserPacket)
	        	{
	        		RegisterUserPacket packet = (RegisterUserPacket)object;
	        		registerUser(connection, packet);
	        	}
	        	else if (object instanceof LoginUserPacket)
	        	{
	        		LoginUserPacket packet = (LoginUserPacket)object;
	        		loginUser(connection, packet);
	        	}
	        	else if (object instanceof RequestUserPacket)
	        	{
	        		RequestUserPacket packet = (RequestUserPacket)object;
	        		requestUser(connection, packet);
	        	}
	        	else if (object instanceof ResendCodePacket)
	        	{
	        		ResendCodePacket packet = (ResendCodePacket)object;
	        		resendCode(connection, packet);
	        	}
	        	else if (object instanceof ConfirmUserPacket)
	        	{
	        		ConfirmUserPacket packet = (ConfirmUserPacket)object;
	        		confirmUser(connection, packet);
	        	}
	        	else if (object instanceof RequestRandomChatPacket)
	        	{
	        		RequestRandomChatPacket packet = (RequestRandomChatPacket)object;
	        		addUserToChatRoom(connection, packet);
	        	}
	        	else if (object instanceof MessagePacket)
	        	{
	        		MessagePacket packet = (MessagePacket)object;
	        		sendMessage(connection, packet);
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
		    		systemMessagePacket.type = SystemMessagePacket.Type.SERVER_CLOSED;
		    		systemMessagePacket.message = "Server closed.";
		    		Arrays.stream(server.getConnections()).forEach(c -> c.sendTCP(systemMessagePacket));
		    		
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
		    	case "/connected":
		    	{
		    		System.out.println(String.format("Total connected users: %d", connectedUsers.size()));
		    		break;
		    	}
		    	case "/chatrooms":
		    	{
		    		System.out.println(String.format("Totalchatrooms: %d", chatrooms.size()));
		    		break;
		    	}
	    	}
	    }
	    
		scan.close();
	}

	private static void registerUser(Connection connection, RegisterUserPacket packet)
	{
		try
		{
			db.registerUser(packet);
			systemMessagePacket.type = SystemMessagePacket.Type.REGISTER_SUCCESS;
			systemMessagePacket.message = "Registered successfully.";
			connection.sendTCP(systemMessagePacket);
			System.out.println(String.format("Registered user '%s'...", packet.email));
		}
		catch (EmailAlreadyExistsException | UsernameAlreadyExistsException e)
		{
			systemMessagePacket.type = SystemMessagePacket.Type.REGISTER_FAILED;
			systemMessagePacket.message = e.getMessage();
			connection.sendTCP(systemMessagePacket);
		}
	}
	
	private static void loginUser(Connection connection, LoginUserPacket packet)
	{
		try
		{
			db.loginUser(packet);
			systemMessagePacket.type = SystemMessagePacket.Type.LOGIN_SUCCESS;
			systemMessagePacket.message = "Logged in successfully.";
			connection.sendTCP(systemMessagePacket);
			System.out.println(String.format("Logged in user '%s'...", packet.email));
			ReceiveUserPacket user = db.getUser(packet.email);
			connectedUsers.put(connection.getID(), new User(connection, user.username));
		}
		catch (IncorrectUsernameOrPasswordException e)
		{
			systemMessagePacket.type = SystemMessagePacket.Type.LOGIN_FAILED;
			systemMessagePacket.message = e.getMessage();
			connection.sendTCP(systemMessagePacket);
		}
		catch (AccountNotConfirmedException e)
		{
			systemMessagePacket.type = SystemMessagePacket.Type.ACCOUNT_NOT_CONFIRMED;
			systemMessagePacket.message = e.getMessage();
			connection.sendTCP(systemMessagePacket);
		}
	}
	
	private static void logoutUser(Connection connection, SystemMessagePacket packet)
	{
		connectedUsers.remove(connection.getID());
		System.out.println(packet.message);
	}
	
	private static void requestUser(Connection connection, RequestUserPacket packet)
	{
		ReceiveUserPacket user = db.getUser(packet.username_email);
		connection.sendTCP(user);
	}

	private static void resendCode(Connection connection, ResendCodePacket packet)
	{
		ReceiveUserPacket user = db.getUser(packet.email);
		db.sendConfirmationCode(user.email, user.username);
	}

	private static void confirmUser(Connection connection, ConfirmUserPacket packet)
	{
		try
		{
			db.confirmAccount(packet.email, packet.confirm_code.toUpperCase());
			systemMessagePacket.type = SystemMessagePacket.Type.CONFIRMATION_CODE_SUCCESS;
			systemMessagePacket.message = "Account confirmed successfully.";
			connection.sendTCP(systemMessagePacket);
		}
		catch (IncorrectConfirmationCodeException e)
		{
			systemMessagePacket.type = SystemMessagePacket.Type.CONFIRMATION_CODE_FAILED;
			systemMessagePacket.message = e.getMessage();
			connection.sendTCP(systemMessagePacket);
		}
	}

	private static void addUserToChatRoom(Connection connection, RequestRandomChatPacket packet)
	{
		ReceiveRandomChatPacket receiveRandomChatPacket = new ReceiveRandomChatPacket();
		receiveRandomChatPacket.roomTags = "";
		receiveRandomChatPacket.matchingTags = "";
		
		ChatRoom chatroom = null;
		
		Set<String> tags = Arrays.stream(packet.tags.split(","))
								 .filter(t -> t.length() > 0)
								 .collect(Collectors.toCollection(HashSet::new));
		
		System.out.println(String.format("%d: %s", tags.size(), ChatRoom.getTags(tags)));
		
		boolean anyRoomSize = packet.maxUsers<2 ? true:false;
		
		System.out.println(anyRoomSize + " " + packet.maxUsers.toString());
		
		if (tags.size() == 0)
		{
			chatroom = chatrooms.stream()
								.filter(c -> c.users.size() < c.maxUsers)
     							.filter(c -> anyRoomSize || c.maxUsers == packet.maxUsers)
     							.filter(c -> c.tags.size() == 0)
     							.findFirst()
     							.orElse(null);
		}
		else
		{
			/*List<ChatRoom> rooms = chatrooms.stream()
					.filter(c -> c.maxUsers == packet.maxUsers)
					.filter(c -> c.users.size() < c.maxUsers)
					.filter(c -> c.tags.size() > 0)
					.collect(Collectors.toList());
					
			long max = 0;
			for (ChatRoom c : rooms)
			{
				long numMatchingTags = c.tags.stream().filter(tags::contains).count();
			    if (numMatchingTags > max)
			    {
			    	max  = numMatchingTags;
			    	chatroom = c;
			    }
			}
					*/
	        
	        chatroom = chatrooms.stream()
	        					.filter(c -> c.users.size() < c.maxUsers)
	        					.filter(c -> anyRoomSize || c.maxUsers == packet.maxUsers)
	        					.filter(c -> c.tags.size() > 0)
	        					.max((c1, c2) -> {return (int) (c1.tags.stream().filter(tags::contains).count() - c2.tags.stream().filter(tags::contains).count());})
	        					.orElse(null);
			 
			
		}
		
		if (chatroom != null)
		{
			receiveRandomChatPacket.found = true;
			receiveRandomChatPacket.maxUsers = chatroom.maxUsers;
			if (tags.size() > 0)
			{
				Set<String> matching = chatroom.tags.stream().filter(tags::contains).collect(Collectors.toSet());
				receiveRandomChatPacket.roomTags = ChatRoom.getTags(chatroom.tags);
				receiveRandomChatPacket.matchingTags = ChatRoom.getTags(matching);
			}
			
			chatroom.users.add(connectedUsers.get(connection.getID()));
		}
		else
		{
			receiveRandomChatPacket.found = false;
			receiveRandomChatPacket.maxUsers = packet.maxUsers<2 ? 2:packet.maxUsers;
			if (tags.size() > 0)
			{
				receiveRandomChatPacket.roomTags = ChatRoom.getTags(tags);
				receiveRandomChatPacket.matchingTags = ChatRoom.getTags(tags);
			}
			
			chatroom = new ChatRoom(packet.maxUsers<2 ? 2:packet.maxUsers, tags, new ArrayList<User>());
			chatroom.users.add(connectedUsers.get(connection.getID()));
			chatrooms.add(chatroom);
		}
		
		connection.sendTCP(receiveRandomChatPacket);
	}

	private static void sendMessage(Connection connection, MessagePacket packet)
	{
		packet.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
		
		if (packet.type == MessagePacket.Type.TOSELF)
		{
			connection.sendTCP(packet);
			return;
		}
		
		ChatRoom chatroom = chatrooms.stream().filter(c->c.users.stream().anyMatch(u -> u.connection.getID()==connection.getID())).findFirst().orElse(null);
		
		if (packet.type == MessagePacket.Type.LEAVE)
		{
			chatroom.users.remove(connectedUsers.get(connection.getID()));
			
			if (chatroom.users.size() == 0)
			{
				chatrooms.remove(chatroom);
				return;
			}
		}
		
		chatroom.users.stream().forEach(u -> u.connection.sendTCP(packet));
	}
}
