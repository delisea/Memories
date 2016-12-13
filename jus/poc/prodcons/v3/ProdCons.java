package jus.poc.prodcons.v3;
import jus.poc.prodcons.Message;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;
import jus.poc.prodcons.v2.TestProdCons;

public class ProdCons implements Tampon {

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
		_att = 0;
		_np = 0;
		_nc = 0;
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
	public Message get(_Consommateur arg0) {
		Message ret;

		synchronized(_lockC){
			if(TestProdCons.getSortie()!=0) System.out.println("C"+arg0.identification()+" : Ready to read");
			if(_att == 0 || _nc > 0)
			{
				_nc++;
				if(_closed)
					return null;

				try {
					ProdCons._lockC.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(_att > 1) ProdCons._lockC.notify();

		        if(_closed)
		          return null;
				_nc--;
			}
			_att--;
			ret = _buff[_S];
			_S = (_S+1)%_size;
			if(TestProdCons.getSortie()!=0) System.out.println("C"+arg0.identification()+" : Reading -> " + ret);
		}
		synchronized(_lockP){ ProdCons._lockP.notify(); }
		return ret;
	}

	@Override
	public void put(_Producteur arg0, Message arg1) {

		synchronized(ProdCons._lockP){
			if(TestProdCons.getSortie()!=0) System.out.println("P"+arg0.identification()+" : Ready to produce");

			if(_size - _att == 0 || _np > 0)
			{
				_np++;
				System.out.println("taken");
				try {
					ProdCons._lockP.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(_size - _att > 1) ProdCons._lockP.notify();
				_np--;
			}

			_att++;
			_buff[_N] = arg1;
			_N = (_N+1)%_size;
			if(TestProdCons.getSortie()!=0) System.out.println("P"+arg0.identification()+" : Have produced");
		}
		synchronized(_lockC){ ProdCons._lockC.notify(); }
	}

	@Override
	public int taille() {
		return _size;
	}

}

