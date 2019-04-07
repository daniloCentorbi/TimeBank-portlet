package ita.social.model;

import java.util.Date;

public class Advice {

	int idadvice;
	int idUser;
	String username;
	String title;
	String tipo;
	Date date;
	String tipology;
	String description;
	Advice parent;


	public Advice() {
		this(0 , 0 , "" , null , "", "" , "" , "" , null);
	}
	
	public Advice(String tipo) {
		this.idadvice = 0;
		this.idUser = 0;
		this.username = null;
		this.title = null;
		this.tipo = tipo;
		this.date = null;
		this.tipology = null;
		this.description = null;
		this.parent = null;
	}

	public Advice(int idadvice, int userid, String username, Date date, String tipology,
			String title, String description, String tipo , Advice parent) {
		this.idadvice = idadvice;
		this.idUser = userid;
		this.username = username;
		this.title = title;
		this.tipo = tipo;
		this.date = date;
		this.tipology = tipology;
		this.description = description;
		this.parent = parent;
	}


	public int getIdadvice() {
		return idadvice;
	}

	public void setIdadvice(int idadvice) {
		this.idadvice = idadvice;
	}
	
	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTipo() {
		return tipo;
	}

	public Advice getParent() {
		return parent;
	}

	public void setParent(Advice parent) {
		this.parent = parent;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTipology() {
		return tipology;
	}

	public void setTipology(String tipology) {
		this.tipology = tipology;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
