package jus.poc.prodcons.v4;
import jus.poc.prodcons.*;

public class FProd extends Acteur implements _Producteur {

	private static int _processing = 0;

	private synchronized static void add_processing()
	{
		_processing++;
	}

	private synchronized static void remove_processing()
	{
		_processing--;
	}

	public static int get_processing()
	{
		return _processing;
	}

	private static Aleatoire RANDPRODT = new Aleatoire(2, 1);
	private static Aleatoire RANDPRODM = new Aleatoire(2, 1);
	private static Aleatoire RANDPRODE = new Aleatoire(2, 1);

	Buffer_circ _buffer;
	int _nbM;

	Observateur _obs;
	private static int _TM;
	private static int _TdM;
	static int _NbEx;
	static int _dNbEx;

	public FProd(Buffer_circ buffer, Observateur observateur) throws ControlException
	{
		super(Acteur.typeProducteur, observateur, _TM, _TdM);
		observateur.newProducteur(this);
	    _nbM = RANDPRODM.next();
		_buffer = buffer;
		_obs = observateur;
	}

	  public static void init(int moyenneTempsDeTraitement, int deviationTempsDeTraitement, int nombreMoyenDeProduction, int deviationNombreDeProduction, int nombreMoyenExemplaires, int deviationNombreExemplaires){
	    RANDPRODT = new Aleatoire(moyenneTempsDeTraitement, deviationTempsDeTraitement);
	    RANDPRODM = new Aleatoire(nombreMoyenDeProduction, deviationNombreDeProduction);
	    RANDPRODE = new Aleatoire(nombreMoyenExemplaires, deviationNombreExemplaires);
		_TM = moyenneTempsDeTraitement;
		_TdM = deviationTempsDeTraitement;
		_NbEx = nombreMoyenExemplaires;
		_dNbEx = deviationNombreExemplaires;
	}

	protected void produce() throws ControlException
	{
		int nbE = RANDPRODE.next();
		GMessage message = new GMessage(nombreDeMessages() + ";Hi! I'm " + identification(), nbE);
		int delai = RANDPRODT.next()*1;
		try {
			sleep(delai*1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		observateur.productionMessage(this, message, delai);
		observateur.depotMessage(this,  message);
		_buffer.putX(this, message);
		_nbM--;
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
			try {
				produce();
			} catch (ControlException e) {
				e.printStackTrace();
			}
		}

		remove_processing();

	    if(get_processing() == 0)
	    {
	      _buffer.close();
	    }
	    System.out.println(identification() + "P: je part.");
	}

}
