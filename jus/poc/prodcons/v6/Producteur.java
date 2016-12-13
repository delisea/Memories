package jus.poc.prodcons.v6;
import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Message;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons._Producteur;

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
	Mecanisme _obs;
	private static int _TM;
	private static int _TdM;

	public Producteur(ProdCons buffer, Observateur observateur, Mecanisme mec) throws ControlException
	{
		super(Acteur.typeProducteur, observateur, _TM, _TdM);
		_nbM = RANDPRODM.next();
		_buffer = buffer;
		_obs = mec;
		mec.newProducteur(this);
	}

	public static void init(int moyenneTempsDeTraitement, int deviationTempsDeTraitement, int nombreMoyenDeProduction, int deviationNombreDeProduction){
		RANDPRODT = new Aleatoire(moyenneTempsDeTraitement, deviationTempsDeTraitement);
		RANDPRODM = new Aleatoire(nombreMoyenDeProduction, deviationNombreDeProduction);
		_TM = moyenneTempsDeTraitement;
		_TdM = deviationTempsDeTraitement;
	}

	protected void produce()
	{
		int tps = RANDPRODT.next()*1;
		Message msg = new MessageX("Je suis le producteur "+identification()+" et ceci est mon message n�"+nombreDeMessages());
		try {
			_obs.productionMessage(this, msg, tps);
		} catch (ControlException e1) {
			e1.printStackTrace();
		}
		try {
			sleep(tps);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		_buffer.put(this, msg);
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
		if(TestProdCons.getSortie()!=0) System.out.println("P"+identification()+" : Je d�marre et j'ai " + _nbM + " messages � produire.");
		while(_nbM>0)
		{
			produce();
		}

		remove_processing();

		if(get_processing() == 0)
		{
			_buffer.close();
		}
		if(TestProdCons.getSortie()!=0) System.out.println("P"+identification()+" : Je m'en vais.");
		try {
			_obs.producteurPart(this);
		} catch (ControlException e) {
			e.printStackTrace();
		}

	}

}
