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

		// gérer mieux le cas ou ça start avant l'init de run
		Thread.sleep(1000);
		System.out.println("START");
		synchronized(Buffer_circ.Global_lock)
		{
			Buffer_circ.Global_lock.notifyAll();
		}
	}

	protected static Integer nbProd;
	protected static int getNbProd(){
		return nbProd;
	}
	protected static Integer nbCons;
	protected static int getNbCons(){
		return nbCons;
	}
	protected static Integer nbBuffer;
	protected static int getNbBuffer(){
		return nbBuffer;
	}
	protected static Integer tempsMoyenProduction;
	protected static int getTempsMoyenProduction(){
		return tempsMoyenProduction;
	}
	protected static Integer deviationTempsMoyenProduction;
	protected static int getDeviationTempsMoyenProduction(){
		return deviationTempsMoyenProduction;
	}
	protected static Integer tempsMoyenConsommation;
	protected static int getTempsMoyenConsommation(){
		return tempsMoyenConsommation;
	}
	protected static Integer deviationTempsMoyenConsommation;
	protected static int getDeviationTempsMoyenConsommation(){
		return deviationTempsMoyenConsommation;
	}
	protected static Integer nombreMoyenDeProduction;
	protected static int getNombreMoyenDeProduction(){
		return nombreMoyenDeProduction;
	}
	protected static Integer deviationNombreMoyenDeProduction;
	protected static int getDeviationNombreMoyenDeProduction(){
		return deviationNombreMoyenDeProduction;
	}
	protected static Integer nombreMoyenNbExemplaire;
	protected static int getNombreMoyenNbExemplaire(){
		return nombreMoyenNbExemplaire;
	}
	protected static Integer deviationNombreMoyenNbExemplaire;
	protected static int getDeviationNombreMoyenNbExemplaire(){
		return deviationNombreMoyenNbExemplaire;
	}

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
		nbProd = opt.get("nbProd");
		nbCons = opt.get("nbCons");
		nbBuffer = opt.get("nbBuffer");
		tempsMoyenProduction = opt.get("tempsMoyenProduction");
		deviationTempsMoyenProduction = opt.get("deviationTempsMoyenProduction");
		tempsMoyenConsommation = opt.get("tempsMoyenConsommation");
		deviationTempsMoyenConsommation = opt.get("deviationTempsMoyenConsommation");
		nombreMoyenDeProduction = opt.get("nombreMoyenDeProduction");
		deviationNombreMoyenDeProduction = opt.get("deviationNombreMoyenDeProduction");
		nombreMoyenNbExemplaire = opt.get("nombreMoyenNbExemplaire");
		deviationNombreMoyenNbExemplaire = opt.get("deviationNombreMoyenNbExemplaire");
	}

}
