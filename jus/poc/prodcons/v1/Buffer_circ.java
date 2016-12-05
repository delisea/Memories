package jus.poc.prodcons.v1;
import jus.poc.prodcons.Message;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class Buffer_circ implements Tampon {

	static public final Object Global_lock = new Object();

	Message[] _buff;
	int _size;
	int _S;
	int _N;
	int _att;

	public Buffer_circ(int size)
	{
		_size = size;
		_buff = new Message[size];
		_S = 0;
		_N = 0;
		_att = 0;
	}

	@Override
	public int enAttente() {
		return _att;
	}

	@Override
	public synchronized Message get(_Consommateur arg0) {
		Message ret;
		while(_att == 0)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		_att--;
		ret = _buff[_S];
		_S = (_S+1)%_size;
		notify();
		return ret;
	}

	@Override
	public synchronized void put(_Producteur arg0, Message arg1) {
		System.out.println(arg0.identification() + ": I want produce.");
		while(_size - _att == 0)
		{
			System.out.println("taken");
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		_att++;
		_buff[_N] = arg1;
		_N = (_N+1)%_size;
		notifyAll();
		System.out.println(arg0.identification() + ": I have produced.");
	}

	@Override
	public int taille() {
		return _size;
	}

}
