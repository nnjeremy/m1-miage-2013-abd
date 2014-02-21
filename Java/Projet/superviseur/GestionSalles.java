package superviseur;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import database.BdQuery;

public class GestionSalles {
	
	private Scanner sc;
	private BdQuery bd;
	private Superviseur s;

	public GestionSalles(Scanner sc, BdQuery bd, Superviseur s) {
		this.sc = sc;
		this.bd = bd;
		this.s = s;
	}

	public void afficherMenu() {
		
		System.out.println("*** Gestion des Salles ***");
		System.out.println("1. Afficher les Salles");
		System.out.println("2. Ajouter une Salle");
		System.out.println("3. Modifier une Salle");
		System.out.println("4. Supprimer une Salle");
		System.out.println("5. Retour au menu principal SUPERVISEUR");
		switch(this.sc.nextInt()){
		case 1:
			this.afficherSalleCU();
			break;
		case 2:
			this.ajouterSalleCU();
			break;
		case 3:
			this.modifierSalleCU();
			break;
		case 4:
			this.supprimerSalleCU();
			break;	
		case 5:
			this.s.afficherMenuPrincipal();
			break;
		}
		
	}

	private void supprimerSalleCU() {
		String codeBatiment;
		int numSalle;
		
		try {
			
			//On change le niveau d'isolation en SERIALIZABLE
			BdQuery.changerNiveauIsolation(2);
			
			//On affiche la liste des Salles
			this.afficherSalle();
			
			/*One demande a l'utilisateur le code du batiment */
			System.out.print("Code du batiment a modifier : ");
			codeBatiment = this.sc.next();
			
			/*One demande a l'utilisateur le numero de la salle */
			System.out.print("Numero de la salle a modifier : ");
			numSalle = this.sc.nextInt();
			
			//On lance le delete
			this.SupprimerSalle(codeBatiment, numSalle);
			
			//On valide la transaction
			this.bd.validerTransaction();
			
		} catch (SQLException e) {
			
			this.bd.annulerTransaction();
			e.printStackTrace();
			
		} finally {
			
			//On change le niveau d'isolation en READ COMMITTED 
			BdQuery.changerNiveauIsolation(1);
			
			this.afficherMenu();
			
		}
		
	}

	public void SupprimerSalle(String codeBatiment, int numSalle) throws SQLException {

		PreparedStatement ps = BdQuery.getCon()
				.prepareStatement("DELETE FROM SALLE WHERE CODE_BATIMENT = ? AND NUMERO_SALLE = ?");
		ps.setString(1, codeBatiment);
		ps.setInt(2, numSalle);
		ps.executeUpdate();
		
	}

	private void modifierSalleCU() {
		String codeBatiment, typeSalle;
		int numSalle, capacite;
		
		try {
			
			//On affiche la liste des Salles
			this.afficherSalle();
			
			/*One demande a l'utilisateur le code du batiment */
			System.out.print("Code du batiment a modifier : ");
			codeBatiment = this.sc.next();
			
			/*One demande a l'utilisateur le numero de la salle */
			System.out.print("Numero de la salle a modifier : ");
			numSalle = this.sc.nextInt();
			
			/*One demande a l'utilisateur le code du batiment */
			System.out.print("Nouveau type de la salle ('CM', 'TD', 'TP', 'REUNION', 'BUREAU', 'AUTRE') : ");
			typeSalle = this.sc.next();
			
			/*One demande a l'utilisateur le code du batiment */
			System.out.print("Nouvelle capacite de la salle : ");
			capacite = this.sc.nextInt();
			
			//On lance l'update
			this.modifierSalle(codeBatiment, numSalle, typeSalle, capacite);
			
			//On valide la transaction
			this.bd.validerTransaction();
			
		} catch (SQLException e) {
			
			this.bd.annulerTransaction();
			e.printStackTrace();
			
		} finally {
			
			this.afficherMenu();
			
		}
		
	}

	public void modifierSalle(String codeBatiment, int numSalle, String typeSalle, int capacite) throws SQLException {
		
		PreparedStatement ps = BdQuery.getCon()
				.prepareStatement("UPDATE SALLE SET TYPE_SALLE = ?, CAPACITE = ? WHERE CODE_BATIMENT = ? AND NUMERO_SALLE = ?");
		ps.setString(1, typeSalle);
		ps.setInt(2, capacite);
		ps.setString(3, codeBatiment);
		ps.setInt(4, numSalle);
		ps.executeUpdate();
		
	}

	private void ajouterSalleCU() {
		String codeBatiment, typeSalle;
		int numSalle, capacite;
		
		try {
			
			//On affiche la liste des batiments
			System.out.println("Liste des codes de batiments :");
			PreparedStatement ps = BdQuery.getCon()
					.prepareStatement("SELECT CODE_BATIMENT FROM Batiment");  	
			ResultSet rs = ps.executeQuery();
			this.bd.afficherResultatRequete(rs);
			
			/*One demande a l'utilisateur le code du batiment */
			System.out.print("Code du batiment : ");
			codeBatiment = this.sc.next();
			
			/*One demande a l'utilisateur le numero de la salle */
			System.out.print("Numero de la salle : ");
			numSalle = this.sc.nextInt();
			
			/*One demande a l'utilisateur le code du batiment */
			System.out.print("Type de la salle ('CM', 'TD', 'TP', 'REUNION', 'BUREAU', 'AUTRE') : ");
			typeSalle = this.sc.next();
			
			/*One demande a l'utilisateur le code du batiment */
			System.out.print("Capacite de la salle : ");
			capacite = this.sc.nextInt();
			
			//On lance l'insert
			this.ajouterSalle(codeBatiment, numSalle, typeSalle, capacite);
			
			//On Valide la transaction
			this.bd.validerTransaction();
			
		} catch (SQLException e) {
			
			this.bd.annulerTransaction();
			e.printStackTrace();
			
		} finally {
			
			this.afficherMenu();
		}
		
	}

	public void ajouterSalle(String codeBatiment, int numSalle, String typeSalle, int capacite) throws SQLException {
		
		PreparedStatement ps = BdQuery.getCon()
				.prepareStatement("INSERT INTO SALLE VALUES (?, ?, ?, ?)");
		ps.setString(1, codeBatiment);
		ps.setInt(2, numSalle);
		ps.setString(3, typeSalle);
		ps.setInt(4, capacite);
		ps.executeUpdate();
		
	}

	private void afficherSalleCU() {
		
		try {
			
			this.afficherSalle();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		} finally {
		
			this.afficherMenu();
			
		}
		
	}

	public void afficherSalle() throws SQLException {
		
		System.out.println("/ codeBatiment // numSalle // typeSalle // capacite /");
		
		PreparedStatement ps = BdQuery.getCon()
				.prepareStatement("SELECT * FROM Salle");  	
		ResultSet rs = ps.executeQuery();
		this.bd.afficherResultatRequete(rs);
		
	}

}
