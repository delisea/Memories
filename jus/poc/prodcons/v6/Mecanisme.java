package jus.poc.prodcons.v6;

import java.util.ArrayList;

import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Message;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class Mecanisme {

	Boolean inited = false;
	Boolean started = false;
	Boolean ended = false;
	int nbPi;
	int nbCi;
	int nbP;
	int nbC;
	int nbB;

	ArrayList<Producteur> Lp = new ArrayList<Producteur>();
	ArrayList<Consommateur> Lc = new ArrayList<Consommateur>();

	ArrayList<Integer> LMt = new ArrayList<Integer>();
	ArrayList<Message> LM = new ArrayList<Message>();
	ArrayList<_Producteur> LMp = new ArrayList<_Producteur>();

	ArrayList<Message> LD = new ArrayList<Message>();

	ArrayList<Message> LR = new ArrayList<Message>();
	ArrayList<_Consommateur> LRp = new ArrayList<_Consommateur>();

	public synchronized void start() throws ControlException {
		if(nbP == nbPi && nbC == nbCi)
			started = true;
		else
			throw new ControlException(Mecanisme.class, "START failed.");
	}

	public synchronized void newProducteur(Producteur producteur) throws ControlException {
		Lp.add(producteur);
		nbP++;
		if(nbP > nbPi)
			throw new ControlException(Mecanisme.class, "Trop de producteur.");
	}

	public synchronized void newConsommateur(Consommateur consommateur) throws ControlException {
		nbC++;
		if(nbC > nbCi)
			throw new ControlException(Mecanisme.class, "Trop de consommateur.");
	}

	public synchronized void consommationMessage(Consommateur consommateur, Message ret, int tps) throws ControlException {
		if(ret == null || tps <= 0 || consommateur == null)
			throw new ControlException(Mecanisme.class, "Paramêtre invalide");
		int id = LR.indexOf(ret);
		if(id == -1)
			throw new ControlException(Mecanisme.class, "Message "+ret+" non retiré avant consommation.");
		if(LRp.get(id) != consommateur)
			throw new ControlException(Mecanisme.class, "Le consommateur n'a pas retiré lui-même le message consommé");
		LR.remove(id);
		LRp.remove(id);
	}

	public synchronized void productionMessage(Producteur producteur, Message msg, int tps) throws ControlException {
		if(msg == null || tps <= 0 || producteur == null)
			throw new ControlException(Mecanisme.class, "Paramêtre invalide");
		LM.add(msg);
		LMt.add(tps);
		LMp.add(producteur);
	}

	public synchronized void init(int nbProd, int nbCons, int nbBuffer) {
		inited = true;
		nbPi = nbProd;
		nbCi = nbCons;
		nbB = nbBuffer;
		nbP = 0;
		nbC = 0;
	}

	public synchronized void depotMessage(_Producteur prod, Message message) throws ControlException {
		if(ended)
			throw new ControlException(Mecanisme.class, "Pas de depot après la fin");
		if(prod == null || message == null)
			throw new ControlException(Mecanisme.class, "Paramêtre invalide");

		int id = LM.indexOf(message);
		if(id == -1)
			throw new ControlException(Mecanisme.class, "Message "+message+" non produit avant depot.");
		if(LMp.get(id) != prod)
			throw new ControlException(Mecanisme.class, "Le producteur n'a pas déposé lui-même son message.");
		LD.add(LM.get(id));
		LM.remove(id);
		LMt.remove(id);
		LMp.remove(id);

		if(LD.size()>nbB)
			throw new ControlException(Mecanisme.class, "Des messages sont stockés alors que le buffer est plein.");
	}

	public synchronized void retraitMessage(_Consommateur cons, Message ret) throws ControlException {
		if(cons == null || ret == null)
			throw new ControlException(Mecanisme.class, "Paramêtre invalide");
		int id = LD.indexOf(ret);
		if(id == -1)
			throw new ControlException(Mecanisme.class, "Message "+ret+" non deposé avant retrait.");
		LR.add(LD.get(id));
		LRp.add(cons);
		LD.remove(id);
	}

	public void producteurPart(Producteur prod) throws ControlException {
		nbP--;
		if(nbP < 0 )
			throw new ControlException(Mecanisme.class, "Trop de producteur parti.");

		if(nbP == 0 && nbC == 0)
			ended = true;
	}

	public void consommateurPart(Consommateur consommateur) throws ControlException {
		nbC--;
		if(nbC < 0 )
			throw new ControlException(Mecanisme.class, "Trop de consommateur parti.");

		if(nbP == 0 && nbC == 0)
			ended = true;
	}

}
