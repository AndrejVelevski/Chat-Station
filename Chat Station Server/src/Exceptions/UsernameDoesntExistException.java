package Exceptions;

public class UsernameDoesntExistException extends Exception
{
	public UsernameDoesntExistException(String message)
	{
		super(message);
	}
}
