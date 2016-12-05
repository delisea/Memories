package Qone;
import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons._Producteur;

public class FProd extends Acteur implements _Producteur {

	Buffer_circ _buff;
	int _nbM;

	public FProd(Aleatoire alea, Buffer_circ buffer, Observateur observateur, int moyenneTempsDeTraitement, int deviationTempsDeTraitement) throws ControlException
	{
		super(Acteur.typeProducteur, observateur, moyenneTempsDeTraitement, deviationTempsDeTraitement);
		_nbM = alea.next();
		_buff = buffer;
	}

	protected void produce()
	{
		System.out.println(identification() + ": i want produce.");
		_buff.put(this, new GMessage(nombreDeMessages() + ";Hi! I'm " + identification()));
		_nbM--;
		System.out.println(identification() + ": i have produced.");
		try {
			sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int deviationTempsDeTraitement() {
		return super.deviationTempsDeTraitement;
	}

	@Override
	public int identification() {
		return super.identification();
	}

	@Override
	public int moyenneTempsDeTraitement() {
		return super.moyenneTempsDeTraitement;
	}

	@Override
	public int nombreDeMessages() {
		return _nbM;
	}

	@Override
	public void run() {
		synchronized(Buffer_circ.Global_lock)
		{
			try {
				Buffer_circ.Global_lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(identification() + "P: je démarre");
		while(_nbM>0)
		{
			produce();
		}
	}

}
