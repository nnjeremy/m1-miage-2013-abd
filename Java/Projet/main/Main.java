package main;

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.Scanner;

import administrateur.Administrateur;

import database.BdQuery;

import superviseur.Superviseur;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		try {
			BdQuery bd = new BdQuery();
			System.out
					.println("Type d'utilisateur: (1=Superviseur, 2=Administrateur, 3=Utilisateur)");
			int typeUser = sc.nextInt();
			switch (typeUser) {
			case 1:
				new Superviseur(bd).afficherMenuPrincipal();
				break;
			case 2:
				new Administrateur(bd).afficherMenuPrincipal();
				break;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
