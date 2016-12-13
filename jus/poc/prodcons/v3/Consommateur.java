package jus.poc.prodcons.v3;
import jus.poc.prodcons.*;
import jus.poc.prodcons.v2.TestProdCons;

public class Consommateur extends Acteur implements _Consommateur {

	private static Aleatoire RANDCONS = new Aleatoire(5, 2);

	int _nbM;
	ProdCons _buffer;
	Observateur _obs;
	private static int _TM;
	private static int _TdM;

	public Consommateur(ProdCons buffer, Observateur observateur) throws ControlException
	{
		super(Acteur.typeConsommateur, observateur, _TM, _TdM);
		observateur.newConsommateur(this);
		_nbM = 0;
		_buffer = buffer;
		_obs = observateur;
	}

	public static void init(int moyenneTempsDeTraitement, int deviationTempsDeTraitement){
		RANDCONS = new Aleatoire(moyenneTempsDeTraitement, deviationTempsDeTraitement);
		_TM = moyenneTempsDeTraitement;
		_TdM = deviationTempsDeTraitement;
	}

	protected Message consume() throws ControlException
	{
		int delai = RANDCONS.next()*1000;
		Message ret = _buffer.get(this);
		if(ret == null) return null;
		observateur.consommationMessage(this, ret, delai);
		observateur.retraitMessage(this, ret);
		_nbM++;
		try {
			sleep(delai);
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
		if(TestProdCons.getSortie()!=0) System.out.println("C"+identification()+" : Démarre");

		Message ret = null;
		do
		{
			try {
				ret = consume();
			} catch (ControlException e) {
				e.printStackTrace();
			}
		} while(ret != null);
		if(TestProdCons.getSortie()!=0) System.out.println("C"+identification()+" : Leaving");

	}

}
