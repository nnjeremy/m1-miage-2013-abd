package administrateur;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Scanner;

import oracle.sql.DATE;

import database.BdQuery;

public abstract class GestionPlageAcces {

	protected Scanner sc;
	protected BdQuery bd;
	protected Connection connection;
	protected Administrateur admin;
	
	
	public GestionPlageAcces(Scanner sc, BdQuery bd, Administrateur admin) {
		this.sc = sc;
		this.bd = bd;
		this.connection = BdQuery.getCon();
		this.admin = admin;
	}
	
	public abstract void afficherMenu();
	
	protected void ajouterPlageSemaine() {
		System.out.println("Libelle plage semaine: ");
		String libellePS = sc.next();
		System.out.println("Semaine debut (1..52): ");
		int semainedeb = sc.nextInt();
		System.out.println("Semaine fin (1..52): ");
		int semainefin = sc.nextInt();
		
		try {
			PreparedStatement psInsert = connection.prepareStatement("INSERT INTO PLAGE_SEMAINE VALUES (?,?,?)");
			psInsert.setString(1, libellePS);
			psInsert.setInt(2, semainedeb);
			psInsert.setInt(3, semainefin);
			psInsert.executeUpdate();
			
			this.bd.validerTransaction();
			
		} catch (SQLException e) {
			//e.printStackTrace();
			this.bd.annulerTransaction();
		}
		this.afficherMenu();
	}
	
	protected void ajouterPlageHoraire() {
		System.out.println("Libelle plage horaire: ");
		String libellePH = sc.next();
		System.out.println("horaire debut (HH24:MM:SS): ");
		String horairedeb = sc.next();
		System.out.println("horaire fin (HH24:MM:SS): ");
		String horairefin = sc.next();
		
		String[] split = horairedeb.split(":");
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(2000, 0, 1, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
		Timestamp datedeb = new Timestamp(c.getTimeInMillis());
		System.out.println(datedeb.toString());
		
		split = horairefin.split(":");
		c.clear();
		c.set(2000, 0, 1, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
		Timestamp datefin = new Timestamp(c.getTimeInMillis());
		System.out.println(datefin.toString());
		try {
			
			PreparedStatement psInsert = connection.prepareStatement("INSERT INTO PLAGE_HORAIRE VALUES (?,?,?)");
			psInsert.setString(1, libellePH);
			psInsert.setTimestamp(2, datedeb);
			psInsert.setTimestamp(3, datefin);
			psInsert.executeUpdate();
			
			this.bd.validerTransaction();
			
		} catch (SQLException e) {
			//e.printStackTrace();
			this.bd.annulerTransaction();
		}
		this.afficherMenu();
	}
	
	
	protected void ajouterPeriodeAcces() {
		System.out.println("Libelle periode acces: ");
		String libellePA = sc.next();
		System.out.println("ferie [vrai, faux]: ");
		String ferie = sc.next();
		System.out.println("ouvre [vrai, faux]: ");
		String ouvre = sc.next();
		
		System.out.println("*** Affichage des plages horaires disponnibles (n¡ Libelle, n¡ Horaire_debut, horaire_fin) ***");
		try {
			
			PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM PLAGE_HORAIRE");
			this.bd.afficherResultatRequete(ps.executeQuery());
			System.out.println("Libelle plage horaire: ");
			String libellePH = sc.next();
			
			PreparedStatement psInsert = connection.prepareStatement("INSERT INTO PERIODE_ACCES VALUES (?,?,?,?)");
			psInsert.setString(1, libellePA);
			psInsert.setString(2, libellePH);
			psInsert.setString(3, ferie);
			psInsert.setString(4, ouvre);
			psInsert.executeUpdate();
			
			this.bd.validerTransaction();
			
		} catch (SQLException e) {
			e.printStackTrace();
			this.bd.annulerTransaction();
		}
		this.afficherMenu();
	}

	
}
