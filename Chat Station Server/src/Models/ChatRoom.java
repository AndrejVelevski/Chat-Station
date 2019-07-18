package Models;

import java.util.List;
import java.util.Set;

public class ChatRoom
{
	public int maxUsers;
	public Set<String> tags;
	public List<User> users;
	
	public ChatRoom(int maxUsers, Set<String> tags, List<User> users)
	{
		this.maxUsers = maxUsers;
		this.tags = tags;
		this.users = users;
	}
	
	public static String getTags(Set<String> tags)
	{
		if (tags.size() == 0)
			return "";
		
		StringBuilder sb = new StringBuilder();
		tags.stream().forEach(t -> sb.append(String.format("%s, ", t)));
		return sb.toString().substring(0, sb.toString().length()-2);
	}

}
