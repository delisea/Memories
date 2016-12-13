package jus.poc.prodcons.v2;
import jus.poc.prodcons.Message;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class ProdCons implements Tampon {


	private Semaphore sBuff = new Semaphore(1);
	private Semaphore sProd = new Semaphore(1);
	private Semaphore sCons = new Semaphore(1);
	private Semaphore sStillRess = new Semaphore(0);
	private Semaphore sEmptyRess = new Semaphore(/*_size*/5);

	static public final Object Global_lock = new Object();

	static public int _nc;
	static public int _np;
	static public final Object _lockP = new Object();
	static public final Object _lockC = new Object();

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
		_np = 0;
		_nc = 0;
		_att = 0;
		_closed = false;
	}

	public void close()
	{
		_closed = true;
		sStillRess.v();
	}

	@Override
	public int enAttente() {
		return sStillRess.n();
	}

	@Override
	public int taille() {
		return _size;
	}

	public void put(_Producteur cons, Message message)
	{System.out.println(cons.identification()+" want write");
		// Semaphore Prod, garanti qu'un seul Producteur  produit à la fois + fifo
		sProd.p();

		// Demande un emplacement libre
		sEmptyRess.p();System.out.println(cons.identification()+" is writing");

		// Semaphore _buff, garanti qu'un seul acteur à la fois manipule le buffer
		sBuff.p();

		_buff[_N] = message;
		_N = (_N+1)%_size;
		_att++;

		// Alloue une ressource
		sStillRess.v();

		sBuff.v();

		sProd.v();
	}

	public Message get(_Consommateur cons)
	{System.out.println(cons.identification()+" want read");
		Message ret;

		// Semaphore Prod, garanti qu'un seul Consommateur consome à la fois + fifo
		sCons.p();

		if(_closed && _att == 0)
		{
			sCons.v();
			return null;
		}

		// Demande une ressource
		sStillRess.p();System.out.println(cons.identification()+" is writing");

		if(_att == 0)
			return null;

		// Semaphore _buff, garanti qu'un seul acteur à la fois manipule le buffer
		sBuff.p();

		ret = _buff[_S];
		_S = (_S+1)%_size;
		_att--;

		// Alloue un emplacement vide
		sEmptyRess.v();

		sBuff.v();

		sCons.v();

		return ret;
	}

	protected class Semaphore {
		int _v;
		public Semaphore(int v)	{
			_v = v;
		}

		public synchronized int n(){
			return _v;
		}

		public synchronized  void p() {
			_v--;
			if(_v<0)
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}

		public synchronized  void v() {
			_v++;
			if(_v<=0)
				this.notify();
		}
	}

}

