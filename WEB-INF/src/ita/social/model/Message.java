package ita.social.model;

import java.util.Date;

public class Message {

	int idmessage;
	int idadvice;
	String object;
	String send;
	String receiver;
	Date date;
	String body;
	Boolean read;

	public Message() {
		this(0, 0, "", "", null, "", "", null);
	}

	public Message(int idmessage,int idadvice, String send, String receiver, Date date,
			String object, String body, Boolean read) {
		this.idmessage = idmessage;
		this.idadvice = idadvice;
		this.send = send;
		this.receiver = receiver;
		this.date = date;
		this.object = object;
		this.date = date;
		this.body = body;
	}

	public int getIdadvice() {
		return idadvice;
	}

	public void setIdadvice(int idadvice) {
		this.idadvice = idadvice;
	}
	
	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	public int getIdmessage() {
		return idmessage;
	}

	public void setIdmessage(int idmessage) {
		this.idmessage = idmessage;
	}

	public String getSend() {
		return send;
	}

	public void setSend(String send) {
		this.send = send;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}