package jus.poc.prodcons.v4;

public class GMessage implements jus.poc.prodcons.Message {
	String _msg;
	int _exRestants;

	public GMessage(String msg, int NbEx)
	{
		_msg = msg;
		_exRestants = NbEx;
	}

	public String toString()
	{
		return _msg;
	}
	
	public GMessage consume(){
		_exRestants--;
		if(_exRestants>0)
			return new GMessage(_msg, 1);
		else {
			GMessage ret =  new GMessage(_msg, 1);
			_msg = "";
			return ret;
		}
	}
	
	public int nbExemplairesRestants(){
		return _exRestants;
	}
}
