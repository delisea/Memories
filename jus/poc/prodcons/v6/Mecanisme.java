package jus.poc.prodcons.v6;

import java.util.ArrayList;

import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Message;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class Mecanisme {

	int nbP;
	int nbC;
	int nbB;

	ArrayList<Producteur> Lp = new ArrayList<Producteur>();
	ArrayList<Consommateur> Lc = new ArrayList<Consommateur>();

	ArrayList<Integer> LMt = new ArrayList<Integer>();
	ArrayList<Message> LM = new ArrayList<Message>();
	ArrayList<Producteur> LMp = new ArrayList<Producteur>();

	ArrayList<Message> LD = new ArrayList<Message>();

	public void newProducteur(Producteur producteur) {
		Lp.add(producteur);
	}

	public void newConsommateur(Consommateur consommateur) {
		Lc.add(consommateur);
	}

	public void consommationMessage(Consommateur consommateur, Message ret, int tps) throws ControlException {
		LM.add(ret);
		LMt.add(tps);
		if(ret == null || tps <= 0 || consommateur == null)
			throw new ControlException(null, "Paramêtre invalide");
	}

	public void productionMessage(Producteur producteur, Message msg, int tps) throws ControlException {
		if(msg == null || tps <= 0 || producteur == null)
			throw new ControlException(null, "Paramêtre invalide");
		LM.add(msg);
		LMt.add(tps);
		LMp.add(producteur);
	}

	public void init(int nbProd, int nbCons, int nbBuffer) {
		nbP = nbProd;
		nbC = nbCons;
		nbB = nbBuffer;
	}

	public void depotMessage(_Producteur prod, Message message) throws ControlException {
		/*if(prod == null || message == null)
			throw new ControlException(null, "Paramêtre invalide");

		int id = LM.indexOf(message);
		if(id == -1)
			throw new ControlException(null, "Message "+message+" non produit avant depot.");
		if(LMp.get(id) != prod)
			throw new ControlException(null, "Le producteur n'a pas déposé lui-même son message.");
		LD.add(LM.get(id));
		LM.remove(id);
		LMt.remove(id);
		LMp.remove(id);*/
	}

	public void retraitMessage(_Consommateur cons, Message ret) throws ControlException {
		// TODO Auto-generated method stub

	}

}
