package jus.poc.prodcons.v3;

public class MessageX implements jus.poc.prodcons.Message {
	String _msg;

	public MessageX(String msg)
	{
		_msg = msg;
	}

	public String toString()
	{
		return _msg;
	}
}
