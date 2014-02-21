package superviseur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import database.BdQuery;

public class GestionGroupePersonne {

	private Scanner sc;
	private BdQuery bd;
	private Superviseur s;
	private Connection connection;

	public GestionGroupePersonne(Scanner sc, BdQuery bd, Superviseur s) {
		this.connection = this.bd.getCon();
		this.sc = sc;
		this.bd = bd;
		this.s = s;
	}

	/**
	 * Affichage du menu de gestion des groupes de personnes
	 */
	public void afficherMenu() {
		System.out.println("*** Gestion des groupes de personnes ***");
		System.out.println("1. Suppression d'un groupe personne");
		System.out.println("2. Ajout d'un groupe personne");
		System.out.println("3. Ajout d'un membre");
		System.out.println("4. Suppression de membres ");
		System.out.println("5. Affichage des groupes ");
		System.out.println("6. Retour au menu principal SUPERVISEUR");
		System.out.println("");
		switch (this.sc.nextInt()) {
		case 1:
			this.supprimerGroupeCU();
			break;
		case 2:
			this.ajouterGroupeCU();
			break;
		case 3:
			this.ajouterMembreCU();
			break;
		case 4:
			this.supprimerMembreCU();
			break;
		case 5:
			this.afficherGroupeCU();
			break;
		case 6:
			this.s.afficherMenuPrincipal();
		}

	}
	
	/**
	 * Suppression de idPers dans idGroupe
	 * @param idPers
	 * @param idGroupe
	 * @throws SQLException
	 */
	private void supprimerMembreGroupe(int idPers, int idGroupe) throws SQLException{
		PreparedStatement psSupprMembre = connection.prepareStatement("DELETE FROM MEMBRE WHERE ID_GROUPEPERS=? AND ID_PERSONNE=?");
		psSupprMembre.setInt(1, idGroupe);
		psSupprMembre.setInt(2, idPers);
		psSupprMembre.executeUpdate();
	}

	/**
	 * Cas d'utilisation : Suppression des membres d'un groupe
	 */
	private void supprimerMembreCU() {
		try{
			//On affiche d'abord le groupe et ses membres
			this.afficherGroupe();
			System.out.print("Num groupe: "); int numGroupe = sc.nextInt();
			System.out.print("Num personne: "); int numPers = sc.nextInt();
			this.supprimerMembreGroupe(numPers, numGroupe);
			this.bd.validerTransaction();
			System.out.println("-- Personne n¡" + numPers + " supprime du groupe n¡"+numGroupe+ " --");
			System.out.print("Continuer la suppression? (O/N)"); String confirm = this.sc.next();
			if(confirm.equalsIgnoreCase("O"))
				this.supprimerMembreCU();
		}catch(SQLException e){
			//Si une exception a ete levee: On annule la transaction
			this.bd.annulerTransaction();
		}finally{
			this.afficherMenu();
		}
	}
	
	/**
	 * Affichage des personnes et de leur groupe
	 */
	private void afficherPersonneGroupe() throws SQLException{
		System.out.println("*** Affichage des personnes et des groupes (id_personne, nom, prenom, id_groupe) ***");
		PreparedStatement psAffichePersonne = this.connection.prepareStatement("SELECT P.ID_PERSONNE, P.NOM, P.PRENOM, M.ID_GROUPEPERS FROM PERSONNE P, MEMBRE M WHERE P.ID_PERSONNE=M.ID_PERSONNE");
		ResultSet rsAffichePersonne = psAffichePersonne.executeQuery();
		this.bd.afficherResultatRequete(rsAffichePersonne);
	}

	/**
	 * Cas d'utilisation : Ajouter un membre ˆ un groupe
	 */
	private void ajouterMembreCU() {
		try {
			//On affiche les personnes et leur groupe respectif
			this.afficherPersonneGroupe();
			//Affichage des groupes 
			this.afficherGroupe();
			System.out.print("id personne : "); int idPersonne = sc.nextInt();
			System.out.print("id groupe : "); int idGroupe = sc.nextInt();
			this.affecterPersonneGroupe(idPersonne, idGroupe);
			//On valide dans le cas ou tout s'est bien passe
			this.bd.validerTransaction();
			System.out.println("Personne n¡"+idPersonne+" ajoutee au groupe n¡"+idGroupe);
		} catch (SQLException e) {
			if(e.getErrorCode()==1)
				System.out.println("Ce membre existe deja dans ce groupe");
			else
				e.printStackTrace();
			this.bd.annulerTransaction();
		}finally{
			this.afficherMenu();
		}

	}

	/**
	 * Affecter idPersonne a idGroupe
	 * @param idPersonne
	 * @param idGroupe
	 */
	private void affecterPersonneGroupe(int idPersonne, int idGroupe) throws SQLException {
		PreparedStatement psInsert = connection.prepareStatement("INSERT INTO Membre VALUES(?, ?)");
		psInsert.setInt(1, idGroupe);
		psInsert.setInt(2, idPersonne);
		psInsert.executeUpdate();
	}
	
	/**
	 * Ajout du groupe nomGroupe dans la BD
	 * @param nomGroupe
	 */
	private void ajouterGroupeBD(String nomGroupe) throws SQLException{
		PreparedStatement ps = connection.prepareStatement("INSERT INTO GROUPE VALUES(groupe_sequence.nextval,?) ");
		ps.setString(1, nomGroupe);
		ps.executeUpdate();
	}

	/**
	 * Cas d'utilisation: Ajouter un groupe
	 */
	private void ajouterGroupeCU() {
		try{
			//On commence par afficher les diffŽrents groupes
			this.afficherGroupe();
			System.out.print("Nom du groupe : "); String nomGroupe = sc.next();
			//On ajoute le groupe dans la base
			this.ajouterGroupeBD(nomGroupe);
			//On valide la transaction
			bd.validerTransaction();
		}catch(SQLException e){
			bd.annulerTransaction();
			e.printStackTrace();
		}finally{
			this.afficherMenu();
		}
	}

	/**
	 * Supprime un groupe numGroupe de la BD
	 * @param numGroupe
	 * @return vrai si groupe supprime / faux si presence de membres
	 * @throws SQLException
	 */
	private boolean supprimerGroupe(int numGroupe) throws SQLException {
		//On commence par vŽrifier que le groupe est bien vide
		int nbPersGroupe = 0;
		PreparedStatement psNbMembre = connection.prepareStatement("SELECT count(*) FROM GROUPE G, MEMBRE M WHERE G.ID_GROUPEPERS=? AND M.ID_GROUPEPERS=G.ID_GROUPEPERS");
		psNbMembre.setInt(1, numGroupe);
		ResultSet rs = psNbMembre.executeQuery();
		if(rs.next())
			nbPersGroupe = rs.getInt(1);
		
		PreparedStatement psSelect = BdQuery.getCon()
				.prepareStatement("SELECT NOMGROUPEPERS FROM groupe WHERE ID_GROUPEPERS = ? ");
		psSelect.setInt(1, numGroupe);
		rs = psSelect.executeQuery();
		String nomgrbat="";
		if (rs.next())
			nomgrbat = rs.getString("NOMGROUPEPERS");
		
		PreparedStatement psMetagroupe = connection.prepareStatement("SELECT COUNT(*) FROM GROUPE_BATIMENTS WHERE NOMGROUPEBAT = ?");
		psMetagroupe.setString(1, nomgrbat);
		rs = psMetagroupe.executeQuery();
		int ismeta=0;
		if(rs.next())
			ismeta = rs.getInt(1);
		System.out.println("nb membre ------> "+ nbPersGroupe);
		if(nbPersGroupe == 0 && ismeta == 0){
			//On s'occupe des suppression en cascade
			//D'abord dans la table administrateur
			PreparedStatement psAdministrateur = connection.prepareStatement("DELETE FROM ADMINISTRATEUR WHERE ID_GROUPEPERS=?");
			psAdministrateur.setInt(1, numGroupe);
			psAdministrateur.executeUpdate();
			//Ensuite on supprime toutes les autorisations
			PreparedStatement psAuthorisations = connection.prepareStatement("DELETE FROM AUTORISATION WHERE ID_GROUPEPERS=?");
			psAuthorisations.setInt(1, numGroupe);
			psAuthorisations.executeUpdate();
			//Ensuite dans la table membre
			PreparedStatement psMembre = connection.prepareStatement("DELETE FROM MEMBRE WHERE ID_GROUPEPERS=?");
			psMembre.setInt(1, numGroupe);
			psMembre.executeUpdate();
			//Ensuite dans la table reservation
			PreparedStatement psResa = connection.prepareStatement("DELETE FROM RESERVATION WHERE ID_GROUPEPERS=?");
			psResa.setInt(1, numGroupe);
			psResa.executeUpdate();
			//Enfin, on peut supprimer le groupe
			PreparedStatement ps = connection
					.prepareStatement("DELETE FROM GROUPE WHERE ID_GROUPEPERS=?");
			ps.setInt(1, numGroupe);
			ps.executeUpdate();
			return true;
		}else //Il reste encore des personnes membres dans le groupe
			return false;
	}

	/**
	 * Cas d'utilisation: Supprimer un groupe
	 */
	private void supprimerGroupeCU() {
		try {
			// Transaction devant s'executer en mode serializable. Pas de
			// lecture fantome possible
			BdQuery.changerNiveauIsolation(2);
			// On commence par afficher les groupes
			this.afficherGroupe();
			System.out.print("Numero de groupe a supprimer : ");
			int numGroupe = this.sc.nextInt();
			boolean ok = this.supprimerGroupe(numGroupe);
			if(ok){
				// On valide dans le cas ou tout s'est bien passe
				this.bd.validerTransaction();
				System.out.println("-- Groupe N¡"+ numGroupe + " supprime --");
			}
			else{
				//Il restait des membres: On annule
				System.out
				.println("-- Veuillez supprimer les personnes membres de ce groupe ou bien verifier que le groupe ne soit pas un meta-groupe --");
				this.bd.annulerTransaction();
			}
		} catch (SQLException e) {
				e.printStackTrace();
			// On annule la transaction dans le cas ou une erreur a ete levee
			this.bd.annulerTransaction();			
		} finally{
			//On remet le niveau d'isolation par defaut
			BdQuery.changerNiveauIsolation(1);
			// on renvoie au menu dans tous les cas
			this.afficherMenu();
		}

	}

	/**
	 * Afficher les groupes et les membres.
	 */
	private void afficherGroupe() throws SQLException {
		System.out
				.println("*** Affichage des groupes de personnes (n¡ Groupe, n¡ personne, nomgroupepers) ***");
		PreparedStatement ps = this.connection
				.prepareStatement("SELECT GROUPE.ID_GROUPEPERS, GROUPE.NOMGROUPEPERS, MEMBRE.ID_PERSONNE FROM GROUPE LEFT OUTER JOIN MEMBRE ON GROUPE.ID_GROUPEPERS=MEMBRE.ID_GROUPEPERS");
		this.bd.afficherResultatRequete(ps.executeQuery());
	}

	/**
	 * Cas d'utilisation : Afficher les groupes de personnes
	 */
	private void afficherGroupeCU() {
		try {
			this.afficherGroupe();
			this.afficherMenu();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
