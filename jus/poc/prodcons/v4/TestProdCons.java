package jus.poc.prodcons.v4;
import java.util.ArrayList;
import java.util.Properties;

import jus.poc.prodcons.*;

/*
 * tester la commutation:
 * 	faire le test avec des temps nuls
 * 	on peut voir qu'il change de temps en temps
 *
 *
*/

public class TestProdCons extends Simulateur {


	public static ArrayList<String> ss = new ArrayList<String>();
	public static synchronized void adds(String s)
	{
		ss.add(s);
	}

	public TestProdCons(Observateur observateur) {
		super(observateur);
		// TODO Auto-generated constructor stub
	}

	private static Thread Thr;
	public static Thread getThr(){
		return Thr;
	}

	protected void run() throws Exception{
		Thr = Thread.currentThread();
		init("options.xml");
		Observateur obs = new Observateur();
		if(getNombreMoyenNbExemplaire()+getDeviationNombreMoyenNbExemplaire() > nbCons)
			nbCons = getNombreMoyenNbExemplaire()+getDeviationNombreMoyenNbExemplaire();
		obs.init(nbProd, nbCons, nbBuffer);
		System.out.println("INIT");
		int fin = 0;
		Buffer_circ b = new Buffer_circ(nbBuffer);
		FProd.init(getTempsMoyenProduction(), getDeviationTempsMoyenProduction(), getNombreMoyenDeProduction(), getDeviationNombreMoyenDeProduction(), getNombreMoyenNbExemplaire(), getDeviationNombreMoyenNbExemplaire());
		FCons.init(getTempsMoyenProduction(), getDeviationTempsMoyenProduction());
		for(fin =0; fin<nbProd; fin++)
			new FProd(b, obs).start();
		for(fin =0; fin<nbCons; fin++)
			new FCons(b, obs).start();

		System.out.println("START");
	}

	protected static Integer nbProd;
	public static int getNbProd(){
		return nbProd;
	}
	protected static Integer nbCons;
	public static int getNbCons(){
		return nbCons;
	}
	protected static Integer nbBuffer;
	public static int getNbBuffer(){
		return nbBuffer;
	}
	protected static Integer tempsMoyenProduction;
	public static int getTempsMoyenProduction(){
		return tempsMoyenProduction;
	}
	protected static Integer deviationTempsMoyenProduction;
	public static int getDeviationTempsMoyenProduction(){
		return deviationTempsMoyenProduction;
	}
	protected static Integer tempsMoyenConsommation;
	public static int getTempsMoyenConsommation(){
		return tempsMoyenConsommation;
	}
	protected static Integer deviationTempsMoyenConsommation;
	public static int getDeviationTempsMoyenConsommation(){
		return deviationTempsMoyenConsommation;
	}
	protected static Integer nombreMoyenDeProduction;
	public static int getNombreMoyenDeProduction(){
		return nombreMoyenDeProduction;
	}
	protected static Integer deviationNombreMoyenDeProduction;
	public static int getDeviationNombreMoyenDeProduction(){
		return deviationNombreMoyenDeProduction;
	}
	protected static Integer nombreMoyenNbExemplaire;
	public static int getNombreMoyenNbExemplaire(){
		return nombreMoyenNbExemplaire;
	}
	protected static Integer deviationNombreMoyenNbExemplaire;
	public static int getDeviationNombreMoyenNbExemplaire(){
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
