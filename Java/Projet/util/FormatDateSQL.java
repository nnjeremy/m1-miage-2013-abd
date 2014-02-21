package util;

import java.util.Calendar;
import java.sql.Date;




public class FormatDateSQL {
	
	private Date date;
	
	/**
	 * S au format JJ-MM-AAAA
	 * @param s
	 */
	public FormatDateSQL(){
		System.out.println("passage constructeur");
	}
	
	public void formaterDate(String dateString){
		
		String[] tabDate = dateString.split("-");
		System.out.println(dateString);
		int jour = Integer.parseInt(tabDate[0]);
		int mois = Integer.parseInt(tabDate[1])-1;
		int annee = Integer.parseInt(tabDate[2]);
		Calendar c = Calendar.getInstance();
		c.set(annee, mois, jour);
		this.date = new Date(c.getTimeInMillis()); 
		
		
		
	}
	
	@SuppressWarnings("deprecation")
	public int retournerJourSemAcDate(Date date){

		return date.getDay();
		
	}
	public Date getDate(){
		
		return this.date;
	}

}
