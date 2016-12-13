package jus.poc.prodcons.v1;
import jus.poc.prodcons.Message;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class ProdCons implements Tampon {

	static public final Object Global_lock = new Object();

	Message[] _buff;
	int _size;
	int _S;
	int _N;
	int _att;

	boolean _closed;

	public ProdCons(int size)
	{
		_size = size;
		_buff = new Message[size];
		_S = 0;
		_N = 0;
		_att = 0;
		_closed = false;
	}

	public void close()
	{
		_closed = true;
	}

	@Override
	public int enAttente() {
		return _att;
	}

	@Override
	public synchronized Message get(_Consommateur arg0) {
		Message ret;
		if(TestProdCons.getSortie()!=0) System.out.println("C"+arg0.identification()+" : Ready to read");
		while(_att == 0)
		{
			if(_closed)
				return null;

			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		_att--;
		ret = _buff[_S];
		_S = (_S+1)%_size;
		notifyAll();
		if(TestProdCons.getSortie()!=0) System.out.println("C"+arg0.identification()+" : Reading -> " + ret);
		return ret;
	}

	@Override
	public synchronized void put(_Producteur arg0, Message arg1) {
		if(TestProdCons.getSortie()!=0) System.out.println("P"+arg0.identification()+" : Ready to produce");
		while(_size - _att == 0)
		{
			if(TestProdCons.getSortie()!=0) System.out.println("P"+arg0.identification()+"En attente");
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
		if(TestProdCons.getSortie()!=0) System.out.println("P"+arg0.identification()+" : Have produced");
	}

	@Override
	public int taille() {
		return _size;
	}

}
