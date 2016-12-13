import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Simulateur;
import jus.poc.prodcons.v6.TestProdCons;
import jus.poc.prodcons.v6.Mecanisme;

public class Main extends Simulateur {

	public Main(Observateur observateur) {
		super(observateur);
	}

	public static void main(String[] args) {
		TestProdCons tt = new TestProdCons(new Observateur());
		tt.start();
	}

	@Override
	protected void run() throws Exception {

	}

}
