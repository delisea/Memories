package jus.poc.prodcons.v1;
import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons._Consommateur;

public class FCons extends Acteur implements _Consommateur {

	private static Aleatoire RANDCONS = new Aleatoire(10, 5);

	int _nbM;
	Buffer_circ _buff;

	public FCons(Buffer_circ buffer, Observateur observateur, int moyenneTempsDeTraitement, int deviationTempsDeTraitement) throws ControlException
	{
		super(Acteur.typeConsommateur, observateur, moyenneTempsDeTraitement, deviationTempsDeTraitement);
		_nbM = 0;
		_buff = buffer;
	}

	protected void consume()
	{
		System.out.println(identification() + "C: I want read.");
		System.out.println(identification() + "C: I red ->" + _buff.get(this));
		_nbM++;
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
		System.out.println(identification() + "C: je démarre");
	}

}
