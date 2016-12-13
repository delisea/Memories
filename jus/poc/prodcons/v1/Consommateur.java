package jus.poc.prodcons.v1;
import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Message;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons._Consommateur;

public class Consommateur extends Acteur implements _Consommateur {

	private static Aleatoire RANDCONS = new Aleatoire(5, 2);

	int _nbM;
	ProdCons _buffer;
	private static int _TM;
	private static int _TdM;

	public Consommateur(ProdCons buffer, Observateur observateur) throws ControlException
	{
		super(Acteur.typeConsommateur, observateur, _TM, _TdM);
		_nbM = 0;
		_buffer = buffer;
	}

	public static void init(int moyenneTempsDeTraitement, int deviationTempsDeTraitement){
		RANDCONS = new Aleatoire(moyenneTempsDeTraitement, deviationTempsDeTraitement);
		_TM = moyenneTempsDeTraitement;
		_TdM = deviationTempsDeTraitement;
	}

	protected Message consume()
	{
		Message ret = _buffer.get(this);
		_nbM++;
		try {
			sleep(RANDCONS.next()*1);
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

		try {
			TestProdCons.getThr().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if(TestProdCons.getSortie()!=0) System.out.println("C"+identification()+" : D�marre");

		Message ret;
		do
		{
			ret = consume();
		} while(ret != null);
		if(TestProdCons.getSortie()!=0) System.out.println("C"+identification()+" : Leaving");

	}

}
