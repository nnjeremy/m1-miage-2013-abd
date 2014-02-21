package scenarios;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import oracle.sql.DATE;

public class ClientThread implements Runnable {
	
	private GestionEntreeSortie ges;
	private int idPersonne;
	private int idScenario;
	
	public ClientThread(GestionEntreeSortie ges, int idPersonne, int idScenario){

		this.ges = ges;
		this.idPersonne = idPersonne;
		this.idScenario = idScenario;
		
		Thread t = new Thread(this);
		t.start();
		
	}

	public void run() {

		System.out.println("Le client N°" + this.idPersonne + " entre en action");

		this.executeScenario(this.idScenario);
		
	}
	
	private void executeScenario(int numScenario) {
		
		try {
		
			switch (numScenario) {
			case 1:
				this.scenario1();
				break;
			case 2:
				this.scenario2();
				break;
			case 3:
				this.scenario3();
				break;
			case 4:
				this.scenario4();
				break;
			case 5:
				this.scenario5();
				break;
			case 6:
				this.scenario6();
				break;
			case 7:
				this.scenario7();
				break;
			default:
				this.scenario8();
				break;
			}
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	private void scenario8() throws InterruptedException {

		System.out.println("Pour la personne N°" + this.idPersonne + " : entre apres la fermeture du batiment");
		
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(2013, 1, 11, 23, 0, 0);
		
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		this.ges.entrer("UFR-B", "001", "ENABLE", new Timestamp(new Date().getTime()), this.idPersonne);
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		this.ges.sortir("UFR-B", "001", "ENABLE", new Timestamp(new Date().getTime()), this.idPersonne);
		
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		this.ges.entrer("UFR-B", "001", "ENABLE", new Timestamp(c.getTimeInMillis()), this.idPersonne);
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		
	}

	private void scenario7() throws InterruptedException {

		System.out.println("Pour la personne N°" + this.idPersonne + " : quitte le batiment sans y etre entre");
		
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(2013, 10, 11, 17, 0, 0);
		
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		this.ges.sortir("DLST-B", "002", "ENABLE", new Timestamp(c.getTimeInMillis()), this.idPersonne);
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		
	}

	private void scenario6() throws InterruptedException {
		
		System.out.println("Pour la personne N°" + this.idPersonne + " : entre le 14 Juillet");
		
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(2013, 6, 14, 17, 0, 0);
		
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		this.ges.entrer("UFR-A", "202", "ENABLE", new Timestamp(c.getTimeInMillis()), this.idPersonne);
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		
	}

	private void scenario5() throws InterruptedException {

		System.out.println("Pour la personne N°" + this.idPersonne + " : n'a pas l'autorisation d'entree dans le batiment");
		
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		this.ges.entrer("UFR-A", "202", "ENABLE", new Timestamp(new Date().getTime()), this.idPersonne);
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		
	}

	private void scenario4() throws InterruptedException {
		
		System.out.println("Pour la personne N°" + this.idPersonne + " : entre 2 fois de suite dans le meme batiment");
		
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		this.ges.entrer("STAPS-A", "007", "ENABLE", new Timestamp(new Date().getTime()), this.idPersonne);
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		this.ges.entrer("STAPS-A", "007", "ENABLE", new Timestamp(new Date().getTime()), this.idPersonne);
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		
	}

	private void scenario3() throws InterruptedException {
		
		System.out.println("Pour la personne N°" + this.idPersonne + " : entre et quitte l'UFR-A sans alarme");
		
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		this.ges.entrer("UFR-A", "202", "ENABLE", new Timestamp(new Date().getTime()), this.idPersonne);
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		this.ges.sortir("UFR-A", "202", "ENABLE", new Timestamp(new Date().getTime()), this.idPersonne);
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		
	}

	private void scenario2() throws InterruptedException {
		
		System.out.println("Pour la personne N°" + this.idPersonne + " : quitte 2 fois de suite dans le meme batiment");
		
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		this.ges.entrer("STAPS-C", "207", "ENABLE", new Timestamp(new Date().getTime()), this.idPersonne);
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		this.ges.sortir("STAPS-C", "207", "ENABLE", new Timestamp(new Date().getTime()), this.idPersonne);
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		//this.ges.sortir("STAPS-C", "207", "ENABLE", new Timestamp(new Date().getTime()), this.idPersonne);

	}

	private void scenario1() throws InterruptedException { 
		
		System.out.println("Pour la personne N°" + this.idPersonne + " : Entre par une sortie");
		
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		this.ges.entrer("DLST-C", "101", "ENABLE", new Timestamp(new Date().getTime()), this.idPersonne);
		Thread.sleep((int)(Math.random() * 5000) + 2000);
		
	}

}
