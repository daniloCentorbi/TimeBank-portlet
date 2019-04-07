package ita.social.model;

import java.util.Date;

import com.vaadin.addon.calendar.event.BasicEvent;

/**
 * Test CalendarEvent implementation.
 * 
 * @see com.vaadin.addon.calendar.Calendarr.ui.Calendar.Event
 */
public class CalendarTestEvent extends BasicEvent {

	private static final long serialVersionUID = 2820133201983036866L;
	private String where;
	private Object data;
	private String state;
	private int idUser;
	private String username;
	private Date startdate;
	private Date enddate;
	private String tousername;
	private int idadvice;
	private int idevent;
	private Boolean viewed;
	private String note;
	private int vote;
	private String tipo;
	private String flag;

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getVote() {
		return vote;
	}

	public void setVote(int vote) {
		this.vote = vote;
	}

	public Boolean getViewed() {
		return viewed;
	}

	public void setViewed(Boolean viewed) {
		this.viewed = viewed;
	}

	public int getIdevent() {
		return idevent;
	}

	public void setIdevent(int idevent) {
		this.idevent = idevent;
	}

	public int getIdadvice() {
		return idadvice;
	}

	public void setIdadvice(int idadvice) {
		this.idadvice = idadvice;
	}

	public String getTousername() {
		return tousername;
	}

	public void setTousername(String tousername) {
		this.tousername = tousername;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int iduser) {
		this.idUser = iduser;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
		fireEventChange();
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
		fireEventChange();
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date date) {
		this.startdate = date;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

}
