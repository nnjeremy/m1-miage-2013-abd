package database;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class BdQuery {

	private static Connection con;

	public BdQuery() throws RemoteException {

		this.connexion();
	}

	/**
	 * Changement du niveau d'isolation 1=READ COMMITTED / 2=SERIALIZABLE
	 * 
	 * @param niveauIsolation
	 * @throws SQLException
	 */
	public static void changerNiveauIsolation(int niveauIsolation) {
		try{
			switch (niveauIsolation) {
			case 1:
				con.setTransactionIsolation(2);
				affiche("Niveau isolation change: READ COMMITTED");
				break;
			case 2:
				con.setTransactionIsolation(8);
				affiche("Niveau isolation change: SERIALIZABLE");
				break;
			default:
				affiche("Erreur // READ COMMITTED définit par défaut");
				con.setTransactionIsolation(2);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	private void connexion() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			arret("Impossible de charger le pilote jdbc:oracle");
		}
		affiche("Connexion a la base de donnees");

		try {
			//String DBurl = "jdbc:oracle:thin:@//192.168.0.15:1521/ORCL";
			//con = DriverManager.getConnection(DBurl, "hr", "oracle");
			String DBurl = "jdbc:oracle:thin:@//192.168.137.1:1521/XE";
			con = DriverManager.getConnection(DBurl, "SYSTEM", "123456");
			affiche("Connexion OK");
			con.setAutoCommit(false);
		} catch (SQLException e) {
			arret("Connexion a la base de donnees impossible");
		}
	}

	private static  void affiche(String message) {
		System.out.println(message);
	}

	private void arret(String message) {
		System.err.println(message);
		System.exit(99);
	}

	/**
	 * methode de qui fait executer une requete sql
	 * 
	 * @param requete
	 * @return resultat de la requete
	 */

	public ResultSet executerRequete(String requete) {
		ResultSet resultats = null;
		try {
			Statement stmt = con.createStatement();
			resultats = stmt.executeQuery(requete);
		} catch (SQLException e) {
			e.printStackTrace();
			arret("Anomalie lors de l'execution de la requete");
		}
		return resultats;
	}

	/**
	 * pour le test affiche le resultat d'une requete
	 * 
	 * @param resultats
	 */
	public void afficherResultatRequete(ResultSet resultats) {
		try {
			ResultSetMetaData rsmd = resultats.getMetaData();
			int nbCols = rsmd.getColumnCount();
			boolean encore = resultats.next();
			while (encore) {
				
				for (int i = 1; i <= nbCols; i++)
					System.out.print(resultats.getString(i) + '\t');

				System.out.println();
				encore = resultats.next();
			}
			resultats.close();
		} catch (SQLException e) {
			e.printStackTrace();
			arret(e.getMessage());
		}
	}
	
	public static Connection getCon(){
		return BdQuery.con;
	}
	
	/**
	 * Valider une transaction
	 */
	public void validerTransaction(){
		try {
			System.out.println("*** VALIDATION TRANSACTION ***");
			this.con.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Annuler une transaction
	 */
	public void annulerTransaction(){
		try {
			System.out.println("*** ANNULATION TRANSACTION ***");
			this.con.rollback();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
