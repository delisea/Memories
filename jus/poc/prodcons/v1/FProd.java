package jus.poc.prodcons.v1;
import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons._Producteur;

public class FProd extends Acteur implements _Producteur {

	private static int _processing = 0;

	private static void add_processing()
	{
		_processing++;
	}

	private static void remove_processing()
	{
		_processing--;
	}

	public static int get_processing()
	{
		return _processing;
	}

	private static Aleatoire RANDPROD = new Aleatoire(2, 1);

	Buffer_circ _buffer;
	int _nbM;

	public FProd(Buffer_circ buffer, Observateur observateur, int moyenneTempsDeTraitement, int deviationTempsDeTraitement) throws ControlException
	{
		super(Acteur.typeProducteur, observateur, moyenneTempsDeTraitement, deviationTempsDeTraitement);
		_nbM = Aleatoire.valeur(moyenneTempsDeTraitement, deviationTempsDeTraitement);
		_buffer = buffer;
	}

	protected void produce()
	{
		_buffer.put(this, new GMessage(nombreDeMessages() + ";Hi! I'm " + identification()));
		_nbM--;
		try {
			sleep(RANDPROD.next());
		} catch (InterruptedException e) {
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
		add_processing();

		synchronized(Buffer_circ.Global_lock)
		{
			try {
				Buffer_circ.Global_lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(identification() + "P: je démarre et j'ai " + _nbM + " paquets.");
		while(_nbM>0)
		{
			produce();
		}

		remove_processing();

		synchronized(_buffer)
		{
			_buffer.notifyAll();
		}
	}

}
