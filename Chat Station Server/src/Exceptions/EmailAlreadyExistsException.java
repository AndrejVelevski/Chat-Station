package Exceptions;

public class EmailAlreadyExistsException extends Exception
{
	public EmailAlreadyExistsException(String message)
	{
		super(message);
	}
}
