package objetDatabase;

import java.sql.Date;

public class Reservation {
private int idGroupPers;
private String idCodeBat ;
private int numSalle;
private String libelleSem;
private String libelleHor;
private Date DateResa;
private int JourSemaine;

public int getIdGroupPers() {
	return idGroupPers;
}
public void setIdGroupPers(int idGroupPers) {
	this.idGroupPers = idGroupPers;
}
public String getIdCodeBat() {
	return idCodeBat;
}
public void setIdCodeBat(String idCodeBat) {
	this.idCodeBat = idCodeBat;
}
public int getNumSalle() {
	return numSalle;
}
public void setNumSalle(int numSalle) {
	this.numSalle = numSalle;
}
public String getLibelleSem() {
	return libelleSem;
}
public void setLibelleSem(String libelleSem) {
	this.libelleSem = libelleSem;
}
public String getLibelleHor() {
	return libelleHor;
}
public void setLibelleHor(String libelleHor) {
	this.libelleHor = libelleHor;
}
public Date getDateResa() {
	return DateResa;
}
public void setDateResa(Date dateResa) {
	DateResa = dateResa;
}
public int getJourSemaine() {
	return JourSemaine;
}
public void setJourSemaine(int jourSemaine) {
	JourSemaine = jourSemaine;
}



}
