import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Simulateur;
import jus.poc.prodcons.v5.TestProdCons;

public class Main extends Simulateur {

	public Main(Observateur observateur) {
		super(observateur);
	}

	public static void main(String[] args) {
		new TestProdCons(new Observateur()).start();
	}

	@Override
	protected void run() throws Exception {

	}

}
