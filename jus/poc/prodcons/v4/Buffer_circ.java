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
	}

	@Override
	public int enAttente() {
		return _att;
	}

	@Override
	public Message get(_Consommateur arg0) {
		GMessage ret;
		GMessage xmas;
		Boolean b = false;

		synchronized(_lockC){
			System.out.println(arg0.identification() + "C: I want read.");
			if(_att == 0 || _nc > 0)
			{
				_nc++;
				if(_closed && _att == 0)
					return null;

				try {
					Buffer_circ._lockC.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(_att > 1 || _att == 1 && _buff[_S].nbExemplairesRestants()>1) Buffer_circ._lockC.notify();
				else if(_closed) Buffer_circ._lockC.notifyAll();

		        if(_closed && _att == 0)
		          return null;
				_nc--;
			}
			ret = _buff[_S].consume();
			xmas = _buff[_S];
			if(xmas.nbExemplairesRestants()==0){
				_S = (_S+1)%_size;
				_att--;
				b = true;
			}
			TestProdCons.adds(arg0.identification() + "C: I wait ->"+ret);
		}
		synchronized(xmas){
			TestProdCons.adds(arg0.identification() + "C: I pre ->" + ret);
			if(xmas.nbExemplairesRestants()==0) xmas.notifyAll();
			else
				try {
					xmas.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			TestProdCons.adds(arg0.identification() + "C: I read ->" + ret);
		}
		synchronized(_lockP){ if(b && enAttente()==_size-1)Buffer_circ._lockP.notify(); }
		return ret;
	}

	@Override
	public void put(_Producteur arg0, Message arg1) {

			putX(arg0, new GMessage(arg1.toString(), 1));
	}

	public void putX(_Producteur arg0, GMessage arg1) {
		GMessage xmas;

		synchronized(Buffer_circ._lockP){
			TestProdCons.adds(arg0.identification() + "P: I want produce." + arg1._exRestants);

			if(_size - _att == 0 || _np > 0)
			{
				_np++;
				System.out.println("taken");
				try {
					Buffer_circ._lockP.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(_size - _att > 1) Buffer_circ._lockP.notify();
				_np--;
			}

			_att++;
			_buff[_N] = arg1;
			xmas = _buff[_S];
			_N = (_N+1)%_size;
			TestProdCons.adds(arg0.identification() + "P: I have produced.");
		}
		synchronized(_lockC){ if(enAttente() == 1) Buffer_circ._lockC.notify(); }
		synchronized(xmas){
			if(xmas.nbExemplairesRestants()!=0)
				try {TestProdCons.adds("ici"+arg0.identification());
					xmas.wait();
					if(xmas.nbExemplairesRestants()!=0)
						System.out.println("pasvide"+arg0.identification());
					else
						TestProdCons.adds("vide"+arg0.identification());
					TestProdCons.adds("sortie"+arg0.identification());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}

	@Override
	public int taille() {
		return _size;
	}

}

