package jus.poc.prodcons.v3;
import jus.poc.prodcons.*;
import jus.poc.prodcons.v2.TestProdCons;

public class Producteur extends Acteur implements _Producteur {

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

	ProdCons _buffer;
	int _nbM;
	int _dM;
	Observateur _obs;
	private static int _TM;
	private static int _TdM;

	public Producteur(ProdCons buffer, Observateur observateur) throws ControlException
	{
		super(Acteur.typeProducteur, observateur, _TM, _TdM);
		observateur.newProducteur(this);
	    _nbM = RANDPRODM.next();
		_buffer = buffer;
		_obs = observateur;
	}

	  public static void init(int moyenneTempsDeTraitement, int deviationTempsDeTraitement, int nombreMoyenDeProduction, int deviationNombreDeProduction){
	    RANDPRODT = new Aleatoire(moyenneTempsDeTraitement, deviationTempsDeTraitement);
	    RANDPRODM = new Aleatoire(nombreMoyenDeProduction, deviationNombreDeProduction);
		_TM = moyenneTempsDeTraitement;
		_TdM = deviationTempsDeTraitement;
	}

	protected void produce() throws ControlException
	{
		MessageX message = new MessageX("Je suis le producteur "+identification()+" et ceci est mon message n°"+nombreDeMessages());
		int delai = RANDPRODT.next()*1000;
		try {
			sleep(delai);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		observateur.productionMessage(this, message, delai);
		observateur.depotMessage(this,  message);
		_buffer.put(this, message);
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
		if(TestProdCons.getSortie()!=0) System.out.println("P"+identification()+" : Je démarre et j'ai " + _nbM + " messages à produire.");
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
	      synchronized(ProdCons._lockC)
	      {
	        ProdCons._lockC.notifyAll();
	      }
	    }
		if(TestProdCons.getSortie()!=0) System.out.println("P"+identification()+" : Je m'en vais.");
	}

}
