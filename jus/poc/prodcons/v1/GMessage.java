package jus.poc.prodcons.v1;

public class GMessage implements jus.poc.prodcons.Message {
	String _msg;

	public GMessage(String msg)
	{
		_msg = msg;
	}

	public String toString()
	{
		return _msg;
	}
}
