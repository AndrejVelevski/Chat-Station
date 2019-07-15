import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import Config.SystemMessagePacketType;
import Config.UserPacketType;
import Exceptions.AccountNotConfirmedException;
import Exceptions.EmailAlreadyExistsException;
import Exceptions.IncorrectConfirmationCodeException;
import Exceptions.IncorrectUsernameOrPasswordException;
import Exceptions.UsernameAlreadyExistsException;
import Packets.SystemMessagePacket;
import Packets.UserPacket;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		Server server = new Server();
	    server.bind(54555);
	    
	    Kryo kryo = server.getKryo();
	    kryo.register(SystemMessagePacketType.class);
        kryo.register(SystemMessagePacket.class);
        kryo.register(UserPacketType.class);
        kryo.register(UserPacket.class);
	    
	    Database db = new Database("chatstation");
	    
	    server.start();
	    
	    SystemMessagePacket systemMessage = new SystemMessagePacket();
	    
	    server.addListener(new Listener()
	    {
	        public void received (Connection connection, Object object)
	        {
	        	if (object instanceof UserPacket)
	        	{
	        		UserPacket user = (UserPacket)object;
	        		
	        		switch (user.type)
	        		{
	        			case REGISTER_USER:
	        			{
	        				try
	    	        		{
	    	        			db.registerUser(user);
	    						systemMessage.type = SystemMessagePacketType.REGISTER_SUCCESS;
	    	        			systemMessage.message = "Registered successfully.";
	    	        			connection.sendTCP(systemMessage);
	    	        			System.out.println(String.format("Registered user '%s'...", user.email));
	    					}
	    	        		catch (EmailAlreadyExistsException | UsernameAlreadyExistsException e)
	    	        		{
	    	        			systemMessage.type = SystemMessagePacketType.REGISTER_FAILED;
	    	        			systemMessage.message = e.getMessage();
	    	        			connection.sendTCP(systemMessage);
	    					}
	        				break;
	        			}
	        				
	        			case LOGIN_USER:
	        			{
	        				try
	    	        		{
	    						db.loginUser(user);
	    						systemMessage.type = SystemMessagePacketType.LOGIN_SUCCESS;
	    	        			systemMessage.message = "Logged in successfully.";
	    	        			connection.sendTCP(systemMessage);
	    	        			System.out.println(String.format("Logged in user '%s'...", user.email));
	    					}
	    	        		catch (IncorrectUsernameOrPasswordException e)
	    	        		{
	    	        			systemMessage.type = SystemMessagePacketType.LOGIN_FAILED;
	    	        			systemMessage.message = e.getMessage();
	    	        			connection.sendTCP(systemMessage);
	    					}
	        				catch (AccountNotConfirmedException e)
	        				{
	        					systemMessage.type = SystemMessagePacketType.ACCOUNT_NOT_CONFIRMED;
	    	        			systemMessage.message = e.getMessage();
	    	        			connection.sendTCP(systemMessage);
							}
	        				break;
	        			}
	        				
	        			case REQUEST_USER:
	        			{
	        				UserPacket receiveUser = db.getUser(user.email);
	        				receiveUser.type = UserPacketType.RECEIVE_USER;
    						connection.sendTCP(receiveUser);
	    					break;
	        			}
	    					
	        			case RESEND_CODE:
		        		{
		        			UserPacket receiveUser = db.getUser(user.email);
		        			db.sendConfirmationCode(receiveUser.email, receiveUser.username);
		        			break;
		        		}
	        				
						case CONFIRM_CODE:
						{
							try
							{
								db.confirmAccount(user.email, user.confirm_code.toUpperCase());
								systemMessage.type = SystemMessagePacketType.CONFIRMATION_CODE_SUCCESS;
	    	        			systemMessage.message = "Account confirmed successfully.";
	    	        			connection.sendTCP(systemMessage);
							}
							catch (IncorrectConfirmationCodeException e)
							{
								systemMessage.type = SystemMessagePacketType.CONFIRMATION_CODE_FAILED;
	    	        			systemMessage.message = e.getMessage();
	    	        			connection.sendTCP(systemMessage);
							}
							break;
						}
						default:
							break;
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
		    		systemMessage.type = SystemMessagePacketType.SERVER_CLOSED;
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
