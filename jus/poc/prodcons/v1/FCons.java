package jus.poc.prodcons.v1;
import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Message;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons._Consommateur;

public class FCons extends Acteur implements _Consommateur {

	private static Aleatoire RANDCONS = new Aleatoire(5, 2);

	int _nbM;
	Buffer_circ _buffer;

	public FCons(Buffer_circ buffer, Observateur observateur, int moyenneTempsDeTraitement, int deviationTempsDeTraitement) throws ControlException
	{
		super(Acteur.typeConsommateur, observateur, moyenneTempsDeTraitement, deviationTempsDeTraitement);
		_nbM = 0;
		_buffer = buffer;
	}

	protected Message consume()
	{
		Message ret = _buffer.get(this);
		_nbM++;
		try {
			sleep(RANDCONS.next()*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return ret;
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

		Message ret;
		do
		{
			ret = consume();
		} while(ret != null);
		System.out.println(identification() + "C: I leave");

	}

}
