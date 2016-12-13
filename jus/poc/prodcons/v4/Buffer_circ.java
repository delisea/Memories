package jus.poc.prodcons.v4;
import jus.poc.prodcons.Message;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class Buffer_circ implements Tampon {

	static public final Object Global_lock = new Object();

	static public int _nc;
	static public int _np;
	static public final Object _lockP = new Object();
	static public final Object _lockC = new Object();

	GMessage[] _buff;
	int _size;
	int _S;
	int _N;
	int _att;

	boolean _closed;

	public Buffer_circ(int size)
	{
		_size = size;
		_buff = new GMessage[size];
		_S = 0;
		_N = 0;
		_att = 0;
		_np = 0;
		_nc = 0;
		_closed = false;
	}

	public void close()
	{
		_closed = true;
		sStillRess.v();
	}

	@Override
	public int enAttente() {
		return _att;
	}


	@Override
	public void put(_Producteur arg0, Message arg1) {

			putX(arg0, new GMessage(arg1.toString(), 1));
	}

	@Override
	public int taille() {
		return _size;
	}

	private Semaphore sBuff = new Semaphore(1);
	private Semaphore sProd = new Semaphore(1);
	private Semaphore sCons = new Semaphore(1);
	private Semaphore sStillRess = new Semaphore(0);
	private Semaphore sEmptyRess = new Semaphore(/*_size*/5);
	private Object LectLock = new Object();
	public void putX(_Producteur prod, GMessage message)
	{System.out.println(prod.identification()+" want write");
		// Semaphore Prod, garanti qu'un seul Producteur  produit à la fois + fifo
		sProd.p();

		// Demande un emplacement libre
		sEmptyRess.p();System.out.println(prod.identification()+" is writing");

		// Semaphore _buff, garanti qu'un seul acteur à la fois manipule le buffer
		sBuff.p();

		// Alloue une ressource
		sStillRess.v();

		_buff[_N] = message;
		_N = (_N+1)%_size;
		_att++;

		sBuff.v();

		synchronized (LectLock) {
			if(message.nbExemplairesRestants() > 0)
				try {
					LectLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}

		sProd.v();
	}

	public Message get(_Consommateur cons)
	{System.out.println(cons.identification()+" want read");
		GMessage ret;
		GMessage temp;
		// Semaphore Prod, garanti qu'un seul Consommateur consome à la fois + fifo
		sCons.p();

		if(_closed && _att == 0)
		{
			sCons.v();
			return null;
		}

		// Demande une ressource
		sStillRess.p();System.out.println(cons.identification()+" is readding");

		if(_att == 0)
		{
			sCons.v();
			return null;
		}

		// Semaphore _buff, garanti qu'un seul acteur à la fois manipule le buffer
		sBuff.p();

		temp = _buff[_S];
		ret = temp.consume();
		if(temp.nbExemplairesRestants() == 0)
		{
			_att--;
			_S = (_S+1)%_size;

			// Alloue un emplacement vide
			sEmptyRess.v();
		}
		else
			sStillRess.v();

		sBuff.v();

		sCons.v();

		synchronized (LectLock) {
			if(temp.nbExemplairesRestants() > 0)
				try {
					LectLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			else
				LectLock.notifyAll();
		}

		return ret;
	}

	protected class Semaphore {
		int _v;
		public Semaphore(int v)	{
			_v = v;
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

