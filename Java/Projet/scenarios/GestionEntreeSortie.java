package scenarios;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import database.BdQuery;

public class GestionEntreeSortie {
	
	private BdQuery bd;
	
	public GestionEntreeSortie(BdQuery bd) {
		this.bd = bd;
	}
	
	public synchronized void entrer(String codeBatiment, String codePointAcces, String etatEntree, Timestamp dateEntre, int idPersonne) {
		
		try {
			
			System.out.println("La personne N°" + idPersonne + " entre dans le batiment " + codeBatiment + " par le point d'acces " + codePointAcces);
			this.ajouterAcces(idPersonne);
			this.ajouterEntree(codeBatiment, codePointAcces, etatEntree, dateEntre);
			this.bd.validerTransaction();
			
		} catch (SQLException e) {
			
			this.bd.annulerTransaction();
			e.printStackTrace();
			
		}
		
	}
	
	public synchronized void sortir(String codeBatiment, String codePointAcces, String etatSortie, Timestamp dateSortie, int idPersonne) {
		
		try {
			
			System.out.println("La personne N°" + idPersonne + " quitte dans le batiment " + codeBatiment + " par le point d'acces " + codePointAcces);
			this.ajouterAcces(idPersonne);
			this.ajouterSortir(codeBatiment, codePointAcces, etatSortie, dateSortie);
			this.bd.validerTransaction();
			
		} catch (SQLException e) {
			
			this.bd.annulerTransaction();
			e.printStackTrace();
			
		}
		
	}
	
	private void ajouterAcces(int idPersonne) throws SQLException{
		
		PreparedStatement ps = BdQuery.getCon()
				.prepareStatement("INSERT INTO ACCES VALUES (acces_sequence.nextval, ?)");
		ps.setInt(1, idPersonne);
		ps.executeUpdate();
		
	}
	
	private void ajouterEntree(String codeBatiment, 
			String codePointAcces, String etatEntree, Timestamp dateEntre) throws SQLException {
		
		PreparedStatement ps = BdQuery.getCon()
				.prepareStatement("INSERT INTO ENTREE VALUES (acces_sequence.currval, ?, ?, ?, ?)");
		ps.setString(1, codeBatiment);
		ps.setString(2, codePointAcces);
		ps.setString(3, etatEntree);
		ps.setTimestamp(4, dateEntre);
		ps.executeUpdate();
		
	}
	
	private void ajouterSortir(String codeBatiment, 
			String codePointAcces, String etatSortie, Timestamp dateSortie) throws SQLException {
		
		PreparedStatement ps = BdQuery.getCon()
				.prepareStatement("INSERT INTO SORTIE VALUES (acces_sequence.currval, ?, ?, ?, ?)");
		ps.setString(1, codeBatiment);
		ps.setString(2, codePointAcces);
		ps.setString(3, etatSortie);
		ps.setTimestamp(4, dateSortie);
		ps.executeUpdate();
		
	}

}
