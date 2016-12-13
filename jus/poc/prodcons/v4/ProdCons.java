package jus.poc.prodcons.v4;
import jus.poc.prodcons.Message;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class ProdCons implements Tampon {

	static public final Object Global_lock = new Object();

	static public int _nc;
	static public int _np;
	static public final Object _lockP = new Object();
	static public final Object _lockC = new Object();

	MessageX[] _buff;
	int _size;
	int _S;
	int _N;
	int _att;

	boolean _closed;

	public ProdCons(int size)
	{
		_size = size;
		sEmptyRess = new Semaphore(_size);
		_buff = new MessageX[size];
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

			putX(arg0, new MessageX(arg1.toString(), 1));
	}

	@Override
	public int taille() {
		return _size;
	}

	private Semaphore sBuff = new Semaphore(1);
	private Semaphore sProd = new Semaphore(1);
	private Semaphore sCons = new Semaphore(1);
	private Semaphore sStillRess = new Semaphore(0);
	private Semaphore sEmptyRess;
	private Object LectLock = new Object();
	public void putX(_Producteur prod, MessageX message)
	{
		if(TestProdCons.getSortie()!=0) System.out.println("P"+prod.identification()+" : Ready to produce");
		// Semaphore Prod, garanti qu'un seul Producteur  produit à la fois + fifo
		sProd.p();

		// Demande un emplacement libre
		sEmptyRess.p();
		
		// Semaphore _buff, garanti qu'un seul acteur à la fois manipule le buffer
		sBuff.p();

		// Alloue une ressource
		sStillRess.v();

		_buff[_N] = message;
		_N = (_N+1)%_size;
		_att++;

		if(TestProdCons.getSortie()!=0) System.out.println("P"+prod.identification()+" : J'ai produit en "+message.nbExemplairesRestants()+" exemplaires");

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
	{
		if(TestProdCons.getSortie()!=0) System.out.println("C"+cons.identification()+" : Ready to read");
		MessageX ret;
		MessageX temp;
		// Semaphore Prod, garanti qu'un seul Consommateur consome à la fois + fifo
		sCons.p();

		if(_closed && _att == 0)
		{
			sCons.v();
			return null;
		}

		// Demande une ressource
		sStillRess.p();

		if(_att == 0)
		{
			sCons.v();
			return null;
		}

		// Semaphore _buff, garanti qu'un seul acteur à la fois manipule le buffer
		sBuff.p();

		temp = _buff[_S];
		ret = temp.consume();
		if(TestProdCons.getSortie()!=0) System.out.println("C"+cons.identification()+" : Reading -> " + ret);

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

