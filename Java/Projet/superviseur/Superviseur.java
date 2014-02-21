package superviseur;

import java.util.Scanner;

import administrateur.Administrateur;


import database.BdQuery;

public class Superviseur {
	
	private Administrateur admin;
	private BdQuery bd;
	private Scanner sc;
	private GestionBatiment gesBatiment;
	private GestionGroupeBatiment gesGroupeBat;
	private GestionGroupePersonne gesGroupePers;
	private GestionSalles gesSalles;
	
	public Superviseur(BdQuery bd){
		this.sc = new Scanner(System.in);
		this.admin = new Administrateur(bd);
		this.bd = bd;
		this.gesBatiment = new GestionBatiment(sc, bd, this);
		this.gesGroupeBat = new GestionGroupeBatiment(sc, bd, this);
		this.gesGroupePers = new GestionGroupePersonne(sc, bd, this);
		this.gesSalles = new GestionSalles(sc, bd, this);
	}
	
	
	public void afficherMenuPrincipal(){		
		System.out.println("*** MENU Superviseur ***");
		System.out.println("1. Gestion des batiments");
		System.out.println("2. Gestion des groupes de batiments");
		System.out.println("3. Gestion des groupes de personnes");
		System.out.println("4. Gestion des salles");
		System.out.println("5. Gestion des personnes et des badges");
		System.out.println("6. Gestion des plages d'acces et des batiments");
		System.out.println("7. Gestion des plages d'acces pour les groupes");
		System.out.println("8. RŽservation de salle");
		System.out.println("Que voulez-vous faire? (1, 2, 3, 4, 5, 6, 7, 8)");
		switch(sc.nextInt()){
		case 1:
			this.gesBatiment.afficherMenu();
			break;
		case 2:
			this.gesGroupeBat.afficherMenu();
			break;
		case 3:
			this.gesGroupePers.afficherMenu();
			break;
		case 4:
			this.gesSalles.afficherMenu();
			break;
		case 5:
			admin.getGesPersonne().afficherMenu();
			break;
		case 6:
			admin.getGesPlageAccesBat().afficherMenu();
			break;
		case 7:
			admin.getGestPlageAccesGr().afficherMenu();
			break;
		case 8:
			admin.getGesResa().afficherMenu();
			break;
		}
	}

}
