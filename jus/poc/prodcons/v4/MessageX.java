package jus.poc.prodcons.v4;

public class MessageX implements jus.poc.prodcons.Message {
	String _msg;
	int _exRestants;

	public MessageX(String msg, int NbEx)
	{
		_msg = msg;
		_exRestants = NbEx;
	}

	public String toString()
	{
		return _msg;
	}
	
	public MessageX consume(){
		_exRestants--;
		if(_exRestants>0)
			return new MessageX(_msg, 1);
		else {
			MessageX ret =  new MessageX(_msg, 1);
			_msg = "";
			return ret;
		}
	}
	
	public int nbExemplairesRestants(){
		return _exRestants;
	}
}
