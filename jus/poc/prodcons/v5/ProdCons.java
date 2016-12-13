package jus.poc.prodcons.v5;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jus.poc.prodcons.Message;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class ProdCons implements Tampon {


   final Lock lock = new ReentrantLock();
   final Condition notFull  = lock.newCondition();
   final Condition notEmpty = lock.newCondition();

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
		lock.lock();
		notEmpty.signal();
		lock.unlock();
	}

	@Override
	public int enAttente() {
		return _att;
	}

	@Override
	public int taille() {
		return _size;
	}

	public void put(_Producteur prod, Message message)
	{
		if(TestProdCons.getSortie()!=0) System.out.println("P"+prod.identification()+" : Ready to produce");
		lock.lock();

		while (_att == _size)
			try {
				notFull.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		_buff[_N] = message;
		_N = (_N+1)%_size;
		_att++;

		if(TestProdCons.getSortie()!=0) System.out.println("P"+prod.identification()+" : Have produced");

		notEmpty.signal();

		lock.unlock();
	}

	public Message get(_Consommateur cons)
	{
		if(TestProdCons.getSortie()!=0) System.out.println("C"+cons.identification()+" : Ready to read");
		Message ret;

		lock.lock();

		if(_closed && _att == 0)
		{
			notEmpty.signal();
			lock.unlock();
			return null;
		}

		while(_att == 0)
			try {
				if(_closed)
				{
					notEmpty.signal();
					lock.unlock();
					return null;
				}
					notEmpty.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}


		ret = _buff[_S];
		_S = (_S+1)%_size;
		_att--;

		if(TestProdCons.getSortie()!=0) System.out.println("C"+cons.identification()+" : Reading -> " + ret);

		notFull.signal();

		lock.unlock();

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

