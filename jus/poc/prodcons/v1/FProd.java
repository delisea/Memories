package jus.poc.prodcons.v1;
import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons._Producteur;

public class FProd extends Acteur implements _Producteur {

	private static Aleatoire RANDPROD = new Aleatoire(10, 5);

	Buffer_circ _buff;
	int _nbM;

	public FProd(Buffer_circ buffer, Observateur observateur, int moyenneTempsDeTraitement, int deviationTempsDeTraitement) throws ControlException
	{
		super(Acteur.typeProducteur, observateur, moyenneTempsDeTraitement, deviationTempsDeTraitement);
		_nbM = Aleatoire.valeur(moyenneTempsDeTraitement, deviationTempsDeTraitement);
		_buff = buffer;
	}

	protected void produce()
	{
		System.out.println(identification() + ": I want produce.");
		_buff.put(this, new GMessage(nombreDeMessages() + ";Hi! I'm " + identification()));
		_nbM--;
		System.out.println(identification() + ": I have produced.");
		try {
			sleep(RANDPROD.next());
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
