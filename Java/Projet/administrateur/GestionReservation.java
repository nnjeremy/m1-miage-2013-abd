package administrateur;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import objetDatabase.Reservation;

import util.FormatDateSQL;

import database.BdQuery;

public class GestionReservation {

	private Scanner sc;
	private BdQuery bd;
	private FormatDateSQL dateSQL;
	private Connection connection;
	private Administrateur admin;
	private ArrayList<Reservation> listeResa;
	Reservation objResa;
	private String repMessageCodeBat = null;

	public GestionReservation(Scanner sc, BdQuery bd, Administrateur admin) {

		this.connection = BdQuery.getCon();
		this.sc = sc;
		this.bd = bd;
		this.admin = admin;
	}

	public void afficherMenu() {
		System.out.println("*** Gestion des Reservations **");
		System.out.println("1. Ajouter une reservation");
		System.out.println("2. Modifier une reservation");
		System.out.println("3. Supprimer une reservation");
		System.out.println("4. Afficher les reservations");
		System.out.println("5. Retour au menu Reservation");
		switch (this.sc.nextInt()) {
		case 1:
			this.ajouterReservationCU();
			break;
		case 2:
			this.menuModifierReservationCU();
			break;
		case 3:
			this.supprimerReservationCU();
			break;
		case 4:
			try {
				this.afficheReservation();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 5:
			this.admin.afficherMenuPrincipal();
			break;
		}
	}

	private void afficheReservation() throws SQLException {
		this.afficherChoixAffichage(repMessageCodeBat);

		this.afficherMenu();
	}

	/**
	 * Ajoute une Reservation en fonction des parametres demandes
	 * 
	 * @throws SQLException
	 */
	private void ajouterReservationCU() {
		try {
			dateSQL = new FormatDateSQL();
			BdQuery.changerNiveauIsolation(2);
			// On demande toutes les informations necessaires ˆ l'ajout d'une
			// personne
			System.out.println("idGroupPers: ");
			int idGroupPers = this.sc.nextInt();
			System.out.println("idCodeBat: ");
			String idCodeBat = this.sc.next();
			System.out.println("numSalle: ");
			int numSalle = this.sc.nextInt();
			System.out.println("libelleSem: ");
			String libelleSem = this.sc.next();
			System.out.println("libelleHor: ");
			String libelleHor = this.sc.next();
			System.out.println("DateResa de type JJ-MM-AAAA : ");
			String DateResa = this.sc.next();
			dateSQL.formaterDate(DateResa);
			Date dateFormate = dateSQL.getDate();
			System.out.println("JourSem: ");
			int JourSem = this.sc.nextInt();

			this.ajouterReservation(idGroupPers, idCodeBat, numSalle,
					libelleSem, libelleHor, dateFormate, JourSem);
			this.bd.validerTransaction();
		} catch (SQLException e) {
			this.bd.annulerTransaction();
			e.printStackTrace();
		} finally {
			this.afficherMenu();
		}

	}

	public void ajouterReservation(int idGroupPers, String idCodeBat,
			int numSalle, String libelleSem, String libelleHor,
			Date dateFormate, int JourSem) throws SQLException {
		PreparedStatement ps;
		System.out.println(idGroupPers + " : " + idCodeBat + " : " + numSalle
				+ " : " + libelleSem + " : " + libelleHor + " : " + dateFormate
				+ " : " + JourSem);
		ps = connection
				.prepareStatement("INSERT INTO RESERVATION VALUES (?, ?, ?, ?, ?,?,?)");
		ps.setInt(1, idGroupPers);
		ps.setString(2, idCodeBat);
		ps.setInt(3, numSalle);
		ps.setString(4, libelleSem);
		ps.setString(5, libelleHor);
		ps.setDate(6, dateFormate);
		ps.setInt(7, JourSem);
		ps.executeUpdate();
	}

	private void menuModifierReservationCU() {

		// Modification de la date resa et/ou JourSemaine
		System.out.println("*** Changement de reservation ***");
		System.out
				.println("1. Modifier la date d'une reservation ( Jour de Semaine Automatiquement changée)");
		System.out.println("2. Retour au menu Reservation");

		switch (this.sc.nextInt()) {
		case 1:
			this.modifierReservationDateCU();
			break;
		case 2:
			this.afficherMenu();
			break;

		}
	}

	private Reservation afficherChoixAffichage(String repMessageCodeBat)
			throws SQLException {
		ResultSet rs;
		Reservation resa;
		ArrayList<Reservation> listeResa;
		System.out
				.println("y/n, Souhaitez vous trier les reservations par batiments ? /n,"
						+ "le cas echeant, l'affichage de l'ensemble des reservations sera effectuée");
		if (repMessageCodeBat == null) {
			repMessageCodeBat = this.sc.next();
		}
		if (repMessageCodeBat.equals("y")) {

			System.out.println("Veuillez entrer le numero de code batiment : ");
			rs = this.prepareRequeteResa(this.sc.next());
			listeResa = getListeResa(rs);
			resa = afficherListeResa(listeResa);
			return resa;
		} else if (repMessageCodeBat.equals("n")) {
			rs = this.prepareRequeteResa("");
			listeResa = getListeResa(rs);
			resa = (afficherListeResa(listeResa));
			return resa;
		} else if (!repMessageCodeBat.equals("n")
				| !repMessageCodeBat.equals("y")) {
			this.afficherChoixAffichage(repMessageCodeBat);
		}

		return null;
	}

	private ResultSet prepareRequeteResa(String repMessageCodeBat)
			throws SQLException {
		// TODO Auto-generated method stub
		ResultSet rs = null;
		System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAA" + repMessageCodeBat);
		if (repMessageCodeBat.equals("")) {
			System.out.println("*** Entree dans codeBat=null***");
			PreparedStatement psAfficheResa;

			psAfficheResa = connection
					.prepareStatement("SELECT R.ID_GROUPEPERS, R.CODE_BATIMENT, R.NUMERO_SALLE, R.LIBELLE_PLAGE_SEMAINE, R.LIBELLE_PLAGE_HORAIRE, R.DATE_RESA, R.JOUR_SEMAINE FROM RESERVATION R");
			rs = psAfficheResa.executeQuery();

		} else {

			PreparedStatement psAfficheResa;

			psAfficheResa = connection
					.prepareStatement("SELECT R.ID_GROUPEPERS, R.CODE_BATIMENT, R.NUMERO_SALLE, R.LIBELLE_PLAGE_SEMAINE, R.LIBELLE_PLAGE_HORAIRE, R.DATE_RESA, R.JOUR_SEMAINE FROM RESERVATION R WHERE R.CODE_BATIMENT=?");
			psAfficheResa.setString(1, repMessageCodeBat);

			rs = psAfficheResa.executeQuery();

		}
		return rs;
	}

	public ArrayList<Reservation> getListeResa(ResultSet rs) {
		try {

			listeResa = new ArrayList<Reservation>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int nbCols = rsmd.getColumnCount();
			boolean encore = rs.next();
			while (encore) {
				objResa = new Reservation();
				for (int i = 1; i <= 7; i++) {
					switch (i) {
					case 1:
						System.out.println("case 1 : " + rs.getInt(i));
						objResa.setIdGroupPers(rs.getInt(i));
						break;
					case 2:
						System.out.println("case 2 : " + rs.getString(i));
						objResa.setIdCodeBat(rs.getString(i));
						break;
					case 3:
						System.out.println("case 3 : " + rs.getInt(i));
						objResa.setNumSalle(rs.getInt(i));
						break;
					case 4:
						System.out.println("case 4 : " + rs.getString(i));
						objResa.setLibelleSem(rs.getString(i));
						break;
					case 5:
						System.out.println("case 5 : " + rs.getString(i));
						objResa.setLibelleHor(rs.getString(i));
						break;
					case 6:
						System.out.println("case 6 : " + rs.getDate(i));
						objResa.setDateResa(rs.getDate(i));
						break;
					case 7:
						System.out.println("case 7 : " + rs.getInt(i));
						objResa.setJourSemaine(rs.getInt(i));
						break;
					}
				}
				this.listeResa.add(objResa);

				encore = rs.next();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.bd.annulerTransaction();
			e.printStackTrace();
			this.afficherMenu();
		} finally {
			System.out.println("Before Return ListeResa");
			return listeResa;

		}

	}

	public Reservation afficherListeResa(ArrayList<Reservation> listeResa) {

		int nbobj = listeResa.size();
		System.out
				.println("------------ Affichage des reservations --------------");

		for (int i = 0; i < nbobj; i++) {
			System.out.println("\n------------" + i
					+ ". Selection de la reservation --------------\n");
			System.out.println("idGroupePers : "
					+ listeResa.get(i).getIdGroupPers());
			System.out
					.println("IdCodeBat : " + listeResa.get(i).getIdCodeBat());
			System.out.println("NumSalle : " + listeResa.get(i).getNumSalle());
			System.out.println("LibelleSem : "
					+ listeResa.get(i).getLibelleSem());
			System.out.println("LibelleHor : "
					+ listeResa.get(i).getLibelleHor());
			System.out.println("DateResa : " + listeResa.get(i).getDateResa());
			System.out.println("JourSemaine : "
					+ listeResa.get(i).getJourSemaine());

		}
		try {
			System.out
					.println("\n------------Veuillez entrer le numero correspondant à votre selection --------------");
			int selectionInt = this.sc.nextInt();
			return listeResa.get(selectionInt);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Ce numero de reservation n'est pas compris dans la liste affichee, veuillez resaisir un numero valide");
			afficherListeResa(listeResa);
		}
	
		return null;

	}

	private void modifierReservationDateCU() {
		try {
			dateSQL = new FormatDateSQL();

			BdQuery.changerNiveauIsolation(2);
			Reservation resa = this.afficherChoixAffichage(repMessageCodeBat);
			System.out
					.println("Veuillez entrer une date du type JJ-MM-AAAA : ");
			String dateStringUpdate = this.sc.next();
			dateSQL.formaterDate(dateStringUpdate);
			Date dateFormate = dateSQL.getDate();
			int JourSem = dateSQL.retournerJourSemAcDate(dateFormate);

			int idGroupePers = resa.getIdGroupPers();
			String idCodeBat = resa.getIdCodeBat();
			int numSalle = resa.getNumSalle();
			String libelleSem = resa.getLibelleSem();
			String libelleHor = resa.getLibelleHor();

			this.modifierReservationDate(idGroupePers, idCodeBat, numSalle,
					libelleSem, libelleHor, dateFormate, JourSem);
			this.bd.validerTransaction();

		} catch (SQLException e) {
			// TODO Auto-generated catch block

			this.bd.annulerTransaction();
			e.printStackTrace();
		} finally {
			this.afficherMenu();
		}

	}

	private void modifierReservationDate(int idGroupePers, String idCodeBat,
			int numSalle, String libelleSem, String libelleHor,
			Date dateFormate, int JourSem) throws SQLException {
		ResultSet rs;
		PreparedStatement psUpdateResaDate = connection
				.prepareStatement("UPDATE RESERVATION R SET DATE_RESA=?,JOUR_SEMAINE=? WHERE R.ID_GROUPEPERS=? AND R.CODE_BATIMENT=? AND R.NUMERO_SALLE=? AND R.LIBELLE_PLAGE_SEMAINE=? AND R.LIBELLE_PLAGE_HORAIRE=?");
		psUpdateResaDate.setDate(1, dateFormate);
		psUpdateResaDate.setInt(2, JourSem);
		psUpdateResaDate.setInt(3, idGroupePers);
		psUpdateResaDate.setString(4, idCodeBat);
		psUpdateResaDate.setInt(5, numSalle);
		psUpdateResaDate.setString(6, libelleSem);
		psUpdateResaDate.setString(7, libelleHor);

		psUpdateResaDate.executeUpdate();

	}

	private void supprimerReservationCU() {

		// On la liste de reservations
		try {

			Reservation resa = this.afficherChoixAffichage(repMessageCodeBat);

			int idGroupePers = resa.getIdGroupPers();
			String idCodeBat = resa.getIdCodeBat();
			int numSalle = resa.getNumSalle();
			String libelleSem = resa.getLibelleSem();
			String libelleHor = resa.getLibelleHor();
			Date dateFormate = resa.getDateResa();
			int JourSem = resa.getJourSemaine();
			this.supprimerReservation(idGroupePers, idCodeBat, numSalle,
					libelleSem, libelleHor, dateFormate, JourSem);

			this.bd.validerTransaction();

		} catch (SQLException e) {
			this.bd.annulerTransaction();
			e.printStackTrace();
		} finally {
			this.afficherMenu();
		}
	}

	private void supprimerReservation(int idGroupePers, String idCodeBat,
			int numSalle, String libelleSem, String libelleHor,
			Date dateFormate, int JourSem) throws SQLException {
		ResultSet rs;
		PreparedStatement psUpdateResaDate = connection
				.prepareStatement("DELETE FROM RESERVATION R WHERE R.ID_GROUPEPERS=? AND R.CODE_BATIMENT=? AND R.NUMERO_SALLE=? AND R.LIBELLE_PLAGE_SEMAINE=? AND R.LIBELLE_PLAGE_HORAIRE=? AND DATE_RESA=? AND JOUR_SEMAINE=? ");

		psUpdateResaDate.setInt(1, idGroupePers);
		psUpdateResaDate.setString(2, idCodeBat);
		psUpdateResaDate.setInt(3, numSalle);
		psUpdateResaDate.setString(4, libelleSem);
		psUpdateResaDate.setString(5, libelleHor);
		psUpdateResaDate.setDate(6, dateFormate);
		psUpdateResaDate.setInt(7, JourSem);
		psUpdateResaDate.executeUpdate();
	}
}
