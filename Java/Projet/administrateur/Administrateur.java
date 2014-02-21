package administrateur;

import java.sql.SQLException;
import java.util.Scanner;


import database.BdQuery;

public class Administrateur {
	
	private BdQuery bd;
	private Scanner sc;
	private GestionReservation gesResa;
	private GestionPersonne gesPersonne;
	private GestionPlageAccesBat gesPlageAccesBat;
	private GestionPlageAccesGr gestPlageAccesGr;
	
	public Administrateur(BdQuery bd){
		this.bd = bd;
		this.sc = new Scanner(System.in);
		this.gesResa = new GestionReservation(sc, bd,this);
		this.gesPersonne = new GestionPersonne(sc, bd, this);
		this.gesPlageAccesBat = new GestionPlageAccesBat(sc, bd, this);
		this.gestPlageAccesGr = new GestionPlageAccesGr(sc, bd, this);
	}
	
	public void afficherMenuPrincipal(){
		System.out.println("*** MENU Administrateur ***");
		System.out.println("1. Gestion des personnes et des badges");
		System.out.println("2. Gestion des plages d'acces et des batiments");
		System.out.println("3. Gestion des plages d'acces pour les groupes");
		System.out.println("4. Reservation de salle");
		System.out.println("Que voulez-vous faire? (1, 2, 3, 4)");
		switch(sc.nextInt()){
		case 1:
			this.gesPersonne.afficherMenu();
			break;
		case 2:
			this.gesPlageAccesBat.afficherMenu();
			break;
		case 3:
			this.gestPlageAccesGr.afficherMenu();
			break;
		case 4:
			this.gesResa.afficherMenu();
			break;
		}
	}

	public GestionReservation getGesResa() {
		return gesResa;
	}

	public GestionPersonne getGesPersonne() {
		return gesPersonne;
	}

	public GestionPlageAccesBat getGesPlageAccesBat() {
		return gesPlageAccesBat;
	}

	public GestionPlageAccesGr getGestPlageAccesGr() {
		return gestPlageAccesGr;
	}
	
	

}
