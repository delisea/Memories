package jus.poc.prodcons.v1;
import java.util.Properties;

import jus.poc.prodcons.*;

public class TestProdCons extends Simulateur {

	public TestProdCons(Observateur observateur) {
		super(observateur);
		// TODO Auto-generated constructor stub
	}

	protected void run() throws Exception{
		Observateur obs = new Observateur();
		Aleatoire alea = new Aleatoire(5, 3);
		System.out.println("INIT");
		int fin = 0;
		Buffer_circ b = new Buffer_circ(100);
		for(fin =0; fin<3; fin++)
			new FProd(b, obs, 10, 5).start();
		for(fin =0; fin<3; fin++)
			new FCons(b, obs, 10, 5).start();

		System.out.println("START");
		synchronized(Buffer_circ.Global_lock)
		{
			Buffer_circ.Global_lock.notifyAll();
		}
	}

	protected static Integer option;

	/**
	* Retreave the parameters of the application.
	* @param file the final name of the file containing the options.
	*/
	protected static void init(String file) {
		// retreave the parameters of the application
		final class Properties extends java.util.Properties{
			private static final long serialVersionUID = 1L;
			public int get(String key){
				return Integer.parseInt(getProperty(key));
			}
			public Properties(String file){
				try{
					loadFromXML(ClassLoader.getSystemResourceAsStream(file));
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		Properties opt = new Properties("jus/poc/prodcons/options/"+file);
		option = opt.get("option");

	}


}
