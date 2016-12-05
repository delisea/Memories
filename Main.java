import Qone.TestProdCons;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Simulateur;

public class Main extends Simulateur {

	public Main(Observateur observateur) {
		super(observateur);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		System.out.println("Hello world!");
		new TestProdCons(new Observateur()).start();
		System.out.println("Done.");
	}

	@Override
	protected void run() throws Exception {
		// TODO Auto-generated method stub

	}

}
