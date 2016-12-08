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
	int _dM;
	private static int _TM;
	private static int _TdM;

	public FProd(Buffer_circ buffer, Observateur observateur, int nombreMoyenDeProduction, int deviationNombreDeProduction) throws ControlException
	{
		super(Acteur.typeProducteur, observateur, _TM, _TdM);
		_nbM = Aleatoire.valeur(moyenneTempsDeTraitement, deviationTempsDeTraitement);
		_buffer = buffer;
	}

	public static void init(int moyenneTempsDeTraitement, int deviationTempsDeTraitement){
		RANDPROD = new Aleatoire(moyenneTempsDeTraitement, deviationTempsDeTraitement);
		_TM = moyenneTempsDeTraitement;
		_TdM = deviationTempsDeTraitement;
	}

	protected void produce()
	{
		_buffer.put(this, new GMessage(nombreDeMessages() + ";Hi! I'm " + identification()));
		_nbM--;
		try {
			sleep(RANDPROD.next()*1000);
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

		try {
			TestProdCons.getThr().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(identification() + "P: je démarre et j'ai " + _nbM + " paquets.");
		while(_nbM>0)
		{
			produce();
		}

		remove_processing();
		if(get_processing() == 0)
			_buffer.close();
		System.out.println(identification() + "P: je part.");

		synchronized(_buffer)
		{
			_buffer.notifyAll();
		}
	}

}
