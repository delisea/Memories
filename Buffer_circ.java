import jus.poc.prodcons.Message;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class Buffer_circ implements Tampon {

	Message[] _buff;
	int _size;
	int _S;
	int _N;

	public Buffer_circ(int size)
	{
		_size = size;
		_buff = new Message[size];
		_S = 0;
		_N = 0;
	}

	@Override
	public int enAttente() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Message get(_Consommateur arg0) throws Exception, InterruptedException {
		Message ret;
		if(_S!=_N)
		{
			ret = _buff[_S];
			_S = (_S+1)%_size;
			notify();
			return ret;
		}
		else
		{
			wait();
			return get(arg0);
		}
	}

	@Override
	public void put(_Producteur arg0, Message arg1) throws Exception, InterruptedException {
		if(_S!=(_N+1)%_size)
		{
			_buff[_N] = arg1;
			_N = (_N+1)%_size;
			notify();
		}
		else
		{
			wait();
			put(arg0, arg1);
		}

	}

	@Override
	public int taille() {
		return _size;
	}

}
