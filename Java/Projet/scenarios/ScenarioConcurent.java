package scenarios;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.RemoteException;

import database.BdQuery;

public class ScenarioConcurent {
	
	private final static int nbThread = 8;
	
	public static void main(String[] args) {
		
		try {
			int[] tabIdClient = new int[nbThread];
			int[] tabNumScenario = new int[nbThread];
			
				tabIdClient[0] = 4;		tabNumScenario[0] = 1;
				tabIdClient[1] = 8;		tabNumScenario[1] = 2;
				tabIdClient[2] = 3;		tabNumScenario[2] = 3;
				tabIdClient[3] = 7;		tabNumScenario[3] = 4;
				tabIdClient[4] = 5;		tabNumScenario[4] = 5;
				tabIdClient[5] = 1;		tabNumScenario[5] = 6;
				tabIdClient[6] = 10;	tabNumScenario[6] = 7;
				tabIdClient[7] = 2;		tabNumScenario[7] = 8;

			
			BdQuery bd = new BdQuery();
			GestionEntreeSortie ges = new GestionEntreeSortie(bd);
			
			for(int i=0; i<nbThread; i++) {
				Thread thread = new Thread(new ClientThread(ges, tabIdClient[i], tabNumScenario[i]));
			}

		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
	}
	
}
