package administrateur;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import util.FormatDateSQL;

import database.BdQuery;

public class GestionPersonne {

	private Scanner sc;
	private BdQuery bd;
	private Connection connection;
	private Administrateur admin;

	public GestionPersonne(Scanner sc, BdQuery bd, Administrateur admin) {
		this.connection = BdQuery.getCon();
		this.sc = sc;
		this.bd = bd;
		this.admin = admin;
	}

	/**
	 * Affiche le menu permettant de g�rer les personnes
	 */
	public void afficherMenu() {
		System.out.println("*** Gestion des personnes et des badges ***");
		System.out.println("1. Ajouter une personne");
		System.out.println("2. Supprimer une personne");
		System.out.println("3. Ajouter un badge");
		System.out.println("4. Afficher les personnes et les badges");
		System.out.println("5. Retour au menu principal ADMINISTRATEUR");
		System.out.println("");
		switch (this.sc.nextInt()) {
		case 1:
			this.ajouterPersonneCU();
			break;
		case 2:
			this.supprimerPersonneCU();
			break;
		case 3:
			this.ajouterBadgeCU();
			break;
		case 4:
			this.afficherPersonnesEtBadgesCU();
			break;
		case 5:
			this.admin.afficherMenuPrincipal();
		}
	}

	/**
	 * R�cup�rer le num�ro de badge actif associ� � une personne
	 * 
	 * @param idPersonne
	 * @return numBadge associ� � idPersonne. 0 si aucun badge trouv�
	 */
	private int getNumBadgeActifPersonne(int idPersonne) {
		int numBadge = 0;
		try {
			PreparedStatement ps = connection
					.prepareStatement("SELECT B.NUMBADGE FROM AFFECTATION A, BADGE B WHERE A.NUMBADGE=B.NUMBADGE AND B.ETATBADGE='ENABLE' AND A.ID_PERSONNE=?");
			ps.setInt(1, idPersonne);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				numBadge = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numBadge;
	}

	/**
	 * Changement de l'�tat de badge d'une personne
	 * 
	 * @param numBadge
	 * @param nouvelEtat
	 *            du badge
	 */
	private void setEtatBadge(int numBadge, String nouvelEtat)
			throws SQLException {
		PreparedStatement psUpdateBadge = connection
				.prepareStatement("UPDATE BADGE SET ETATBADGE=? WHERE NUMBADGE=?");
		psUpdateBadge.setString(1, nouvelEtat);
		psUpdateBadge.setInt(2, numBadge);
		psUpdateBadge.executeUpdate();
		System.out.println("Badge n� " + numBadge + " dans l'�tat "
				+ nouvelEtat);
	}

	/**
	 * Cr�ation d'une nouvelle affectation de badge idBadge � idPersonne
	 * 
	 * @param idPersonne
	 * @param idBadge
	 */
	private void ajouterBadgePersonne(int idPersonne, int idBadge)
			throws SQLException {
		// On commence par r�cup�rer la date de fin d'affectation (Cas d'un
		// visiteur)
		int numBadge = this.getNumBadgeActifPersonne(idPersonne);
		Date dateFinAffectation = null;
		PreparedStatement psSelect = connection
				.prepareStatement("SELECT DATE_FIN_AFFECTATION FROM AFFECTATION A WHERE A.ID_PERSONNE=? AND NUMBADGE=? ");
		psSelect.setInt(1, idPersonne);
		psSelect.setInt(2, numBadge);
		ResultSet rsSelect = psSelect.executeQuery();
		while (rsSelect.next())
			dateFinAffectation = rsSelect.getDate(1);
		// ON calcule la date de d�but d'affectation = Date du jour
		FormatDateSQL dateFinAffectationFormat = new FormatDateSQL();
		dateFinAffectationFormat.formaterDate("27-12-2013");
		PreparedStatement psUpdate = connection
				.prepareStatement("INSERT INTO AFFECTATION VALUES (?, ?, ?, ?)");
		psUpdate.setInt(1, idPersonne);
		psUpdate.setInt(2, idBadge);
		psUpdate.setDate(3, dateFinAffectationFormat.getDate());
		psUpdate.setDate(4, dateFinAffectation);
		psUpdate.executeUpdate();
	}

	/**
	 * Supprimer l'affectation du badge de la personne
	 * 
	 * @param idPersonne
	 */
	private void supprimerBadgeAffecte(int idPersonne) throws SQLException {
		PreparedStatement ps = connection
				.prepareStatement("DELETE FROM AFFECTATION WHERE ID_PERSONNE=?");
		ps.setInt(1, idPersonne);
		ps.executeUpdate();
	}

	/**
	 * Permet d'ajouter un badge dans un �tat DISABLE
	 * 
	 * @return l'id du nouveau badge cr��. 0 si erreur
	 */

	private int ajouterBadge() throws SQLException {
		int numBadgeAjoute = 0;
		PreparedStatement rs = connection
				.prepareStatement("INSERT INTO BADGE VALUES (badge_sequence.nextval,?)");
		rs.setString(1, "DISABLE");
		rs.executeUpdate();
		connection.commit();
		ResultSet rsSelect = connection.createStatement().executeQuery(
				"SELECT MAX(NUMBADGE) FROM BADGE");
		while (rsSelect.next())
			numBadgeAjoute = rsSelect.getInt(1);
		return numBadgeAjoute;

	}

	/**
	 * Affiche les Personnes de la base ainsi que leur badge respectif
	 */
	private void afficherPersonnesEtBadges() {
		System.out
				.println("*** Affichage des personnes et du num�ro de badge ACTIF 2***");
		ResultSet rs = this.bd
				.executerRequete("SELECT P.ID_PERSONNE, NOM, PRENOM, B.NUMBADGE FROM PERSONNE P, AFFECTATION A, BADGE B WHERE B.ETATBADGE='ENABLE' AND B.NUMBADGE = A.NUMBADGE AND P.ID_PERSONNE=A.ID_PERSONNE");
		bd.afficherResultatRequete(rs);
	}

	/**
	 * Cas d'utilisation: Affiche les Personnes de la base ainsi que leur badge
	 * respectif
	 */
	private void afficherPersonnesEtBadgesCU() {
		System.out
				.println("*** Affichage des personnes et du num�ro de badge ACTIF 2***");
		ResultSet rs = this.bd
				.executerRequete("SELECT P.ID_PERSONNE, NOM, PRENOM, B.NUMBADGE FROM PERSONNE P, AFFECTATION A, BADGE B WHERE B.ETATBADGE='ENABLE' AND B.NUMBADGE = A.NUMBADGE AND P.ID_PERSONNE=A.ID_PERSONNE");
		bd.afficherResultatRequete(rs);
		System.out.println("\n\n");
		this.afficherMenu();
	}

	/**
	 * Cas d'utilisation: On ajoute une nouveau badge
	 */
	private void ajouterBadgeCU() {
		try {
			System.out.println("*** Changement d'affectation de badge ***");
			this.afficherPersonnesEtBadges();
			System.out.println("ID Personne: ");
			int idPersonne = this.sc.nextInt();
			//On active le mode READ COMMITTED
			BdQuery.changerNiveauIsolation(1);
			// On desactive le badge actuel de la personne
			int numBadgeActuel = this.getNumBadgeActifPersonne(idPersonne);
			this.setEtatBadge(numBadgeActuel, "DISABLE");
			// On ajoute un nouveau badge
			int numNouveauBadge = this.ajouterBadge();
			// On affecte ce nouveau badge � la personne
			this.ajouterBadgePersonne(idPersonne, numNouveauBadge);
			// On active ce nouveau badge
			this.setEtatBadge(numNouveauBadge, "ENABLE");
			// On valide la transaction
			connection.commit();
			System.out.println("-- Personne n�"+idPersonne+" li� au badge n�"+numNouveauBadge+ " -- \n\n");
		} catch (SQLException e) {
			System.out.println("*** ANNULATION DE LA TRANSACTION ***");
			this.bd.annulerTransaction();
			e.printStackTrace();
		}
		this.afficherMenu();

	}

	/**
	 * Supprimer une personne de la BD
	 * 
	 * @param idPersonne
	 *            � supprimer
	 */
	private void supprimerPersonneBD(int idPersonne) throws SQLException {
		// On supprime d'abord les r�f�rences de la personne
		// D'abord dand la table acces
		PreparedStatement psAcces = connection
				.prepareStatement("DELETE FROM ACCES WHERE ID_PERSONNE=?");
		psAcces.setInt(1, idPersonne);
		psAcces.executeUpdate();
		// Ensuite dans la table ADMINISTRATEUR
		PreparedStatement psAdministrateur = connection
				.prepareStatement("DELETE FROM ADMINISTRATEUR WHERE ID_PERSONNE=?");
		psAdministrateur.setInt(1, idPersonne);
		psAdministrateur.executeUpdate();
		// Enfin dans la table MEMBRE
		PreparedStatement psMembre = connection
				.prepareStatement("DELETE FROM MEMBRE WHERE ID_PERSONNE=?");
		psMembre.setInt(1, idPersonne);
		psMembre.executeUpdate();
		// On supprime la personne
		PreparedStatement ps = connection
				.prepareStatement("DELETE FROM PERSONNE WHERE ID_PERSONNE=?");
		ps.setInt(1, idPersonne);
		ps.executeUpdate();
	}

	/**
	 * Cas d'utilisation : Supprimer une personne
	 */
	private void supprimerPersonneCU() {
		try {
			//On passe en mode serializable
			BdQuery.changerNiveauIsolation(2);
			// On commence par afficher les personnes
			this.afficherPersonnesEtBadges();
			System.out.print("\nid personne � supprimer: ");
			int idPersonne = this.sc.nextInt();
			// On desactive le badge de la personne
			this.setEtatBadge(idPersonne, "DISABLE");
			// On supprime toutes les affectations de badge de la personne
			this.supprimerBadgeAffecte(idPersonne);
			// On supprime la personne de la base
			this.supprimerPersonneBD(idPersonne);
			// On valide la transaction
			this.bd.validerTransaction();
			System.out.println("--Suppression OK de la personne " + idPersonne
					+ "--");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("*** ANNULATION DE LA TRANSACTION ***");
			this.bd.annulerTransaction();
		}
		this.afficherMenu();
	}

	/**
	 * Ajouter une nouvelle personne � la base
	 * 
	 * @return idPersonne ajout�. 0 si erreur
	 */
	private int ajouterPersonneBD() throws SQLException {
		int idPersonne = 0;
		// On demande toutes les informations necessaires � l'ajout d'une
		// personne
		System.out.println("Nom:");
		String nom = sc.next();
		System.out.println("Prenom: ");
		String prenom = sc.next();
		System.out.println("Date naissance: (JJ-MM-AAAA)");
		String dateNais = sc.next();
		FormatDateSQL dateNaissance = new FormatDateSQL();
		dateNaissance.formaterDate(dateNais);
		System.out.println("Lieu naissance: ");
		String lieuNais = sc.next();
		System.out.println("Bureau: ");
		String bureau = sc.next();
		System.out.println("Telephone: (xx.yy.zz.tt.rr)");
		String telephone = sc.next();
		System.out.println("EMail: ");
		String mail = sc.next();
		System.out.println("NumCarte Etu:");
		int numCarteEtu = sc.nextInt();
		System.out.println("Fili�re: ");
		String filiere = sc.next();
		System.out.println("Annee Promo: ");
		int anneePromo = sc.nextInt();
		PreparedStatement ps = connection
		/* id_personne, 1=nom, 2=prenom, 3=date_naissance, 4=lieu_naissance, 5=bureau, 6=telephone, 7=mail, 8=num_carte_etu, 9=filiere, 10=annee_promo, 11=num_carte_id */
				.prepareStatement("INSERT INTO PERSONNE VALUES(personne_sequence.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		ps.setString(1, nom);
		ps.setString(2, prenom);
		ps.setDate(3, dateNaissance.getDate());
		ps.setString(4, lieuNais);
		ps.setString(5, bureau);
		ps.setString(6, telephone);
		ps.setString(7, mail);
		ps.setInt(8, numCarteEtu);
		ps.setString(9, filiere);
		ps.setInt(10, anneePromo);
		ps.executeUpdate();
		ps.executeUpdate();
		//On r�cup�re le num�ro de la personne que l'on vient d'ajouter
		ResultSet rs = connection.createStatement().executeQuery("SELECT MAX(id_personne) FROM PERSONNE");
		if (rs.next())
			idPersonne = rs.getInt(1);
		return idPersonne;
	}

	/**
	 * Cas d'utilisation : Ajouter une personne � la base
	 */
	private void ajouterPersonneCU() {
		try {
			//Transaction effectuee en mode READ COMMITTED
			BdQuery.changerNiveauIsolation(1);
			// On commence par ajouter une personne � la BD
			int idPersonne = this.ajouterPersonneBD();
			// Puis un nouveau badge
			int numBadge = this.ajouterBadge();
			// Enfin, on lie la personne au badge
			this.ajouterBadgePersonne(idPersonne, numBadge);
			// On active le badge
			this.setEtatBadge(numBadge, "ENABLE");
			// On valide la transaction
			this.bd.validerTransaction();
			System.out.println("-- Personne n�"+idPersonne+" ajoutee avec success -- \n\n");
		} catch (SQLException e) {
			System.out.println("*** ANNULATION DE LA TRANSACTION ***");
			this.bd.annulerTransaction();
			e.printStackTrace();
		}
		this.afficherMenu();
	}

}
