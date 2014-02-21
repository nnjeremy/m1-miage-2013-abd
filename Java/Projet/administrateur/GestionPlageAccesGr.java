package administrateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import database.BdQuery;

public class GestionPlageAccesGr extends GestionPlageAcces {

	
	
	public GestionPlageAccesGr(Scanner sc, BdQuery bd, Administrateur admin) {
		super(sc, bd, admin);
	}

	public void afficherMenu() {
		System.out.println("*** Gestion des Plages pour les groupes ***");
		System.out.println("1. Ajout d'une plage semaine");
		System.out.println("2. Ajout d'une plage horaire");
		System.out.println("3. Ajout d'une periode d'acces");
		System.out.println("4. Ajout d'une autorisation");
		System.out.println("");
		
		switch (this.sc.nextInt()) {
		case 1:
			this.ajouterPlageSemaine();
			break;
		case 2:
			this.ajouterPlageHoraire();
			break;
		case 3:
			this.ajouterPeriodeAcces();
			break;
		case 4:
			this.ajouterAutorisation();
			break;

		}
		
	}

	

	

	private void ajouterAutorisation() {
		try {
			System.out.println("*** Affichage des groupes de b�timent (n� ID_GROUPEBAT, n� NOMGROUPEBAT) ***");
			PreparedStatement psSelectGB = this.connection.prepareStatement("SELECT * FROM GROUPE_BATIMENTS");
			this.bd.afficherResultatRequete(psSelectGB.executeQuery());
			System.out.println("ID_GROUPEBAT: ");
			int idgrbat = sc.nextInt();
			
			System.out.println("*** Affichage des periodes d'acces (n� LIBELLE_PLAGE_ACCES, n� LIBELLE_PLAGE_HORAIRE, n� ferie, n� ouvre) ***");
			PreparedStatement psSelectPA = this.connection.prepareStatement("SELECT * FROM PERIODE_ACCES");
			this.bd.afficherResultatRequete(psSelectPA.executeQuery());
			System.out.println("libelle periode d'acces: ");
			String libellePA = sc.next();
			
			System.out.println("*** Affichage des groupes de personnes (n� ID_GROUPEPERS, n� NOMGROUPEPERS) ***");
			PreparedStatement psSelectGpers = this.connection.prepareStatement("SELECT * FROM GROUPE");
			this.bd.afficherResultatRequete(psSelectGpers.executeQuery());
			System.out.println("ID_GROUPEPERS: ");
			int idgpers = sc.nextInt();
			
			
			PreparedStatement psInsert = connection.prepareStatement("INSERT INTO AUTORISATION VALUES (?,?,?)");
			psInsert.setInt(1, idgrbat);
			psInsert.setInt(2, idgpers);
			psInsert.setString(3, libellePA);
			psInsert.executeUpdate();
			
			this.bd.validerTransaction();
			
		} catch (SQLException e) {
			//e.printStackTrace();
			System.out.println(e.getMessage());
			this.bd.annulerTransaction();
		}
		
		this.afficherMenu();
	}



}
