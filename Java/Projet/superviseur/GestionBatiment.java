package superviseur;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import database.BdQuery;

public class GestionBatiment {
	
	private Scanner sc;
	private BdQuery bd;
	private Superviseur s;

	public GestionBatiment(Scanner sc, BdQuery bd, Superviseur s) {
		this.sc = sc;
		this.bd = bd;
		this.s = s;
	}

	public void afficherMenu() {
		System.out.println("*** Gestion des Batiments ***");
		System.out.println("1. Afficher les Batiments");
		System.out.println("2. Ajouter un Batiment");
		System.out.println("3. Modifier un Batiment");
		System.out.println("4. Supprimer un Batiment");
		System.out.println("5. Retour au menu principal SUPERVISEUR");
		switch(this.sc.nextInt()){
		case 1:
			this.afficherBatimentCU();
			break;
		case 2:
			this.ajouterBatimentCU();
			break;
		case 3:
			this.modifierBatimentCU();
			break;
		case 4:
			this.supprimerBatimentCU();
			break;	
		case 5:
			this.s.afficherMenuPrincipal();
			break;
		}
	}

	public void afficherBatiment() throws SQLException {
			
			PreparedStatement ps = BdQuery.getCon()
					.prepareStatement("SELECT * FROM BATIMENT");  	
			ResultSet rs = ps.executeQuery();
			this.bd.afficherResultatRequete(rs);
		
	}
	
	private void afficherBatimentCU() {
		
		try {
			
			this.afficherBatiment();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		} finally {
		
			this.afficherMenu();
			
		}
		
	}

	private void supprimerBatimentCU() {
		
		System.out.println("*** Supprimer un Batiment ***");
		
		try {
			
			//On change le niveau d'isolation en SERIALIZABLE
			BdQuery.changerNiveauIsolation(2);
				
			//On affiche une liste de batiments
			this.afficherBatiment();
			
			//On demande de l'utilisateur le code du batiment a supprimer
			System.out.print("Code du batiment à supprimer :");
			String codeBatiment = this.sc.next();
					
			//On supprime le batiment qui porte le meme CODE
			this.supprimerBatiment(codeBatiment);
			
			//On valide la transaction
			this.bd.validerTransaction();
			
		} catch (SQLException e) {
			
			this.bd.annulerTransaction();
			
			e.printStackTrace();
			
		} finally {
			
			//On change le niveau d'isolation en READ COMMITTED 
			BdQuery.changerNiveauIsolation(1);
			
			//Pour finir on afficher le menu
			this.afficherMenu();
			
		}
				
	}

	public void supprimerBatiment(String codeBatiment) throws SQLException {

		PreparedStatement psUpdate = BdQuery.getCon()
				.prepareStatement("DELETE FROM BATIMENT WHERE CODE_BATIMENT = ?");
		psUpdate.setString(1, codeBatiment);
		psUpdate.executeUpdate();
		
	}

	private void modifierBatimentCU() {
		
		System.out.println("*** Modifier un Batiment ***");
		
		try {
			
			//On affiche une liste de batiments
			this.afficherBatiment();
			
			//On demande de l'utilisateur le code du batiment a modifier
			System.out.print("Code du batiment à modifier :");
			String codeBatiment = this.sc.next();
			
			//On affiche la liste des groupes de batiment
			System.out.println("Liste des groupes de batiment :");

			//On afficher les groupes de batiments
			PreparedStatement ps = BdQuery.getCon()
					.prepareStatement("SELECT * FROM GROUPE_BATIMENTS");  	
			ResultSet rs = ps.executeQuery();
			this.bd.afficherResultatRequete(rs);
			
			//One demande a l'utilisateur l'ID du groupe de batiment
			System.out.print("Nouvel ID de groupe de batiment : ");
			int id_groupeBat = this.sc.nextInt();
			
			//On demande a l'utilisateur l'adresse du batiment
			System.out.print("Nouvelle Adresse du Batiment : ");
			String adresseBatiment = this.sc.next();
			
			//On update le batiment dans la BD
			this.modifierBatiment(id_groupeBat, adresseBatiment, codeBatiment);
			
			//On valide la transaction
			this.bd.validerTransaction();
			
		} catch (SQLException e) {
			
			this.bd.annulerTransaction();
			
			e.printStackTrace();
			
		} finally {
			
			//Pour finir on afficher le menu
			this.afficherMenu();
			
		}
		
	}

	public void modifierBatiment(int id_groupeBat, String adresseBatiment,	String codeBatiment) throws SQLException {

		PreparedStatement psUpdate = BdQuery.getCon()
				.prepareStatement("UPDATE BATIMENT SET ID_GROUPEBAT = ?, ADRESSE = ? WHERE CODE_BATIMENT = ?");
		psUpdate.setInt(1, id_groupeBat);
		psUpdate.setString(2, adresseBatiment);
		psUpdate.setString(3, codeBatiment);
		psUpdate.executeUpdate();
		
	}

	private void ajouterBatimentCU() {
		
		System.out.println("*** Ajouter un Batiment ***");
		
		try {
			
			/*On demande a l'utilisteur le code du batiment*/
			//Verif syntaxe avec un check sur la table ?
			System.out.print("Code du Batiment : ");
			String codeBatiment = this.sc.next();
			
			/*On affiche la liste des groupes de batiment*/
			System.out.println("Liste des groupes de batiment :");

			//On affiche les groupes de batiments
			PreparedStatement ps = BdQuery.getCon()
					.prepareStatement("SELECT * FROM GROUPE_BATIMENTS");  	
			ResultSet rs = ps.executeQuery();
			this.bd.afficherResultatRequete(rs);
		
			/*One demande a l'utilisateur l'ID du groupe de batiment */
			System.out.print("ID du Groupe de Batiment : ");
			int id_groupeBat = this.sc.nextInt();
			
			/*On demande a l'utilisateur l'adresse du batiment*/
			System.out.print("Adresse du Batiment : ");
			String adresseBatiment = this.sc.next();
			
			//On ajoute dans la base de données
			this.ajouterBatiment(codeBatiment, id_groupeBat, adresseBatiment);
			
			//On valide la transaction
			this.bd.validerTransaction();

		} catch (SQLException e) {
			
			this.bd.annulerTransaction();
			
			e.printStackTrace();
			
		} finally {
			
			//Pour finir on afficher le menu
			this.afficherMenu();
			
		}
		
	}

	public void ajouterBatiment(String codeBatiment, int id_groupeBat,	String adresseBatiment) throws SQLException {

		PreparedStatement psUpdate = BdQuery.getCon()
				.prepareStatement("INSERT INTO BATIMENT VALUES (?, ?, ?)");
		psUpdate.setString(1, codeBatiment);
		psUpdate.setInt(2, id_groupeBat);
		psUpdate.setString(3, adresseBatiment);
		psUpdate.executeUpdate();
		
	}

}
