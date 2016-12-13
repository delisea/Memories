package jus.poc.prodcons.v4;
import java.util.ArrayList;
import java.util.Properties;

import jus.poc.prodcons.*;

public class TestProdCons extends Simulateur {

	public static ArrayList<String> ss = new ArrayList<String>();
	public static synchronized void adds(String s)
	{
		ss.add(s);
	}

	Observateur obs;

	public TestProdCons(Observateur observateur) {
		super(observateur);
		obs = observateur;
	}

	private static Thread Thr;
	public static Thread getThr(){
		return Thr;
	}

	protected void run() throws Exception{
		Thr = Thread.currentThread();
		init("options.xml");
		if(getNombreMoyenNbExemplaire()+getDeviationNombreMoyenNbExemplaire() > nbCons)
			nbCons = getNombreMoyenNbExemplaire()+getDeviationNombreMoyenNbExemplaire();
		obs.init(nbProd, nbCons, nbBuffer);
		if(getSortie()!=0) System.out.println("Initialisation...");
		int fin = 0;
		ProdCons b = new ProdCons(nbBuffer);
		Producteur.init(getTempsMoyenProduction(), getDeviationTempsMoyenProduction(), getNombreMoyenDeProduction(), getDeviationNombreMoyenDeProduction(), getNombreMoyenNbExemplaire(), getDeviationNombreMoyenNbExemplaire());
		Consommateur.init(getTempsMoyenConsommation(), getDeviationTempsMoyenConsommation());
		for(fin =0; fin<nbProd; fin++)
			new Producteur(b, obs).start();
		for(fin =0; fin<nbCons; fin++)
			new Consommateur(b, obs).start();
		if(getSortie()!=0) System.out.println("Start...");
	}

	protected static int nbProd;
	public static int getNbProd(){
		return nbProd;
	}
	protected static int nbCons;
	public static int getNbCons(){
		return nbCons;
	}
	protected static int nbBuffer;
	public static int getNbBuffer(){
		return nbBuffer;
	}
	protected static int tempsMoyenProduction;
	public static int getTempsMoyenProduction(){
		return tempsMoyenProduction;
	}
	protected static int deviationTempsMoyenProduction;
	public static int getDeviationTempsMoyenProduction(){
		return deviationTempsMoyenProduction;
	}
	protected static int tempsMoyenConsommation;
	public static int getTempsMoyenConsommation(){
		return tempsMoyenConsommation;
	}
	protected static int deviationTempsMoyenConsommation;
	public static int getDeviationTempsMoyenConsommation(){
		return deviationTempsMoyenConsommation;
	}
	protected static int nombreMoyenDeProduction;
	public static int getNombreMoyenDeProduction(){
		return nombreMoyenDeProduction;
	}
	protected static int deviationNombreMoyenDeProduction;
	public static int getDeviationNombreMoyenDeProduction(){
		return deviationNombreMoyenDeProduction;
	}
	protected static int nombreMoyenNbExemplaire;
	public static int getNombreMoyenNbExemplaire(){
		return nombreMoyenNbExemplaire;
	}
	protected static int deviationNombreMoyenNbExemplaire;
	public static int getDeviationNombreMoyenNbExemplaire(){
		return deviationNombreMoyenNbExemplaire;
	}
	protected static int sortie;
	public static int getSortie(){
		return sortie;
	}

	protected static void init(String file) {
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
		sortie = opt.get("sortie");
	}

}
