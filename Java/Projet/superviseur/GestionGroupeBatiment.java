package superviseur;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import database.BdQuery;

public class GestionGroupeBatiment {
	
	private Scanner sc;
	private BdQuery bd;
	private Superviseur s;

	public GestionGroupeBatiment(Scanner sc, BdQuery bd, Superviseur s) {
		this.sc = sc;
		this.bd = bd;
		this.s = s;
	}

	public void afficherMenu() {
		System.out.println("*** Gestion des groupes de batiments ***");
		System.out.println("1. Afficher les Groupes de batiments");
		System.out.println("2. Ajouter un Groupe de batiments");
		System.out.println("3. Modifier un Groupe de batiments");
		System.out.println("4. Supprimer un Groupe de batiments");
		System.out.println("5. Retour au menu principal SUPERVISEUR");
		switch(this.sc.nextInt()){
		case 1:
			this.afficherGroupeBatimentCU();
			break;
		case 2:
			this.ajouterGroupeBatimentCU();
			break;
		case 3:
			this.modifierGroupeBatimentCU();
			break;
		case 4:
			this.supprimerGroupeBatimentCU();
			break;		
		case 5:
			this.s.afficherMenuPrincipal();
			break;
		}
	}

	private void supprimerGroupeBatimentCU() {

	System.out.println("*** Supprimer un Batiment ***");
				
		try {
			
			//On change le niveau d'isolation en SERIALIZABLE
			BdQuery.changerNiveauIsolation(2);
			
			//On affiche une liste de batiments
			this.afficherGroupeBatiment();
			
			//On demande de l'utilisateur le code du batiment a supprimer
			System.out.print("ID du groupe de batiments à supprimer :");
			int idGroupeBatiment = this.sc.nextInt();
			
			//Execution de la requete de suppression
			this.supprimerGroupeBatiment(idGroupeBatiment);
			
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

	public void supprimerGroupeBatiment(int idGroupeBatiment) throws SQLException {

		PreparedStatement psUpdate = BdQuery.getCon()
				.prepareStatement("DELETE FROM groupe_batiments WHERE ID_GROUPEBAT = ?");
		psUpdate.setInt(1, idGroupeBatiment);
		psUpdate.executeUpdate();
		
	}

	private void modifierGroupeBatimentCU() {

		System.out.println("*** Modifier un Groupe de Batiments ***");
		
		try {
			
			//On affiche une liste de batiments
			this.afficherGroupeBatiment();
			
			//On demande de l'utilisateur l'ID du groupe de batiments a modifier
			System.out.print("ID du groupe de batiments à modifier :");
			int idGroupeBatiment = this.sc.nextInt();
			
			//One demande a l'utilisateur le nouveau NOMGROUPEBAT du groupe de batiment
			System.out.print("Nouveau nom pour le groupe de batiments : ");
			String nomGroupeBat = this.sc.next();
			
			//Execution de la requete de modification
			this.modifierGroupeBatiment(nomGroupeBat, idGroupeBatiment);
			
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

	public void modifierGroupeBatiment(String nomGroupeBat, int idGroupeBatiment) throws SQLException {

		PreparedStatement psUpdate = BdQuery.getCon()
				.prepareStatement("UPDATE groupe_batiments SET NOMGROUPEBAT = ? WHERE ID_GROUPEBAT = ?");
		psUpdate.setString(1, nomGroupeBat);
		psUpdate.setInt(2, idGroupeBatiment);
		psUpdate.executeUpdate();
		
	}

	private void ajouterGroupeBatimentCU() {

		System.out.println("*** Ajouter un Groupe de Batiments ***");
				
		try {
			
			/*One demande a l'utilisateur le nom du groupe de batiment */
			System.out.print("Nom du Groupe de Batiment : ");
			String nomGroupeBat = this.sc.next();
			
			//On execute la requete d'ajout
			this.ajouterGroupeBatiment(nomGroupeBat);
			
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

	public void ajouterGroupeBatiment(String nomGroupeBat) throws SQLException {

		PreparedStatement psUpdate = BdQuery.getCon()
				.prepareStatement("INSERT INTO groupe_batiments VALUES(groupe_batiments_sequence.nextval, ?)");
		psUpdate.setString(1, nomGroupeBat);
		psUpdate.executeUpdate();
		
		//On execute la requete d'ajout dans groupe
		PreparedStatement psUpdate2 = BdQuery.getCon()
				.prepareStatement("INSERT INTO groupe VALUES(groupe_sequence.nextval, ?)");
		psUpdate2.setString(1, nomGroupeBat);
		psUpdate2.executeUpdate();
		
	}

	private void afficherGroupeBatimentCU() {
		
		try {
			
			this.afficherGroupeBatiment();
			
		} catch (SQLException e) {

			e.printStackTrace();
			
		} finally {
		
			this.afficherMenu();
		}
		
	}
	
	public void afficherGroupeBatiment() throws SQLException {
			
			PreparedStatement ps = BdQuery.getCon()
					.prepareStatement("SELECT * FROM groupe_batiments");  	
			ResultSet rs = ps.executeQuery();
			bd.afficherResultatRequete(rs);
		
	}

}
