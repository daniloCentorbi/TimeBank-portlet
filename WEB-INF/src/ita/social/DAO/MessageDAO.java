package ita.social.DAO;

import ita.social.model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageDAO {
	private static final Log _log = LogFactory.getLog(MessageDAO.class);
	private static final String GETINMESSAGE = "SELECT * FROM message WHERE receiver = ?";
	private static final String GETOUTMESSAGE = "SELECT * FROM message WHERE send = ?";
	private static final String GETADVICEMESSAGE = "SELECT * FROM message WHERE idadvice = ?";
	private static final String INSERT = "INSERT INTO message (send,receiver,date,object,idadvice,body,read) VALUES (?,?,?,?,?,?,?)";
	private static final String DELETE = "DELETE FROM message WHERE idmessage=?";
	private static final String COUNT_MESSAGE_4_ADVICE = "SELECT COUNT(*) FROM message WHERE idadvice = ?";
	private static final String COUNT_INMESSAGE = "SELECT COUNT(*) FROM message WHERE receiver = ?";
	private static final String COUNT_OUTMESSAGE = "SELECT COUNT(*) FROM message WHERE send = ?";

	public List<Message> getResults(ResultSet rs) throws SQLException {
		List<Message> elenco = new ArrayList<Message>();
		while (rs.next()) {
			Message model = new Message();
			model.setIdmessage(rs.getInt("idmessage"));
			model.setObject(rs.getString("object"));
			model.setSend(rs.getString("send"));
			model.setReceiver(rs.getString("receiver"));
			model.setDate(rs.getDate("date"));
			model.setBody(rs.getString("body"));

			elenco.add(model);
		}
		_log.info("ritorno elenco------------: " + elenco);
		return elenco;
	}

	public List<Message> getIn(String username, Connection connection)
			throws SQLException {
		_log.info("Invoco la query: " + GETINMESSAGE);

		PreparedStatement stmt = connection.prepareStatement(GETINMESSAGE);
		int i = 1;
		stmt.setString(i++, username);
		ResultSet res = stmt.executeQuery();
		List<Message> elenco = getResults(res);

		_log.debug("Recuperati " + elenco.size() + " elementi");

		res.close();
		stmt.close();

		return elenco;
	}

	public List<Message> getOut(String username, Connection connection)
			throws SQLException {
		_log.info("Invoco la query: " + GETOUTMESSAGE);

		PreparedStatement stmt = connection.prepareStatement(GETOUTMESSAGE);
		int i = 1;
		stmt.setString(i++, username);
		ResultSet res = stmt.executeQuery();
		List<Message> elenco = getResults(res);

		_log.debug("Recuperati " + elenco.size() + " elementi");

		res.close();
		stmt.close();

		return elenco;
	}


	public List<Message> getAdvice(int idadvice, Connection connection)
			throws SQLException {
		_log.info("Invoco la query: " + GETADVICEMESSAGE);

		PreparedStatement stmt = connection.prepareStatement(GETADVICEMESSAGE);
		int i = 1;
		stmt.setInt(i++, idadvice);
		ResultSet res = stmt.executeQuery();
		List<Message> elenco = getResults(res);

		_log.debug("Recuperati " + elenco.size() + " elementi");

		res.close();
		stmt.close();

		return elenco;
	}
	

	public Message insert(Message message, Connection connection)
			throws SQLException {
		_log.info("Invoco la query [" + INSERT + "] con l'oggetto [" + message
				+ "]");

		PreparedStatement stmt = connection.prepareStatement(INSERT,
				Statement.RETURN_GENERATED_KEYS);
		int i = 1;
		stmt.setString(i++, message.getSend());
		stmt.setString(i++, message.getReceiver());
		stmt.setTimestamp(i++, new java.sql.Timestamp(message.getDate().getTime()));
		stmt.setString(i++, message.getObject());
		stmt.setInt(i++, message.getIdadvice());		
		stmt.setString(i++, message.getBody());
		stmt.setBoolean(i++, false);
		
		stmt.executeUpdate();

		_log.debug("Oggetto valorizzato: " + message);

		stmt.close();

		return message;
	}
	
	
	public int delete(Message message, Connection connection)
			throws SQLException {
		_log.info("Invoco la query [" + DELETE + "] con l'oggetto [" + message
				+ "]");

		PreparedStatement stmt = connection.prepareStatement(DELETE);
		int i = 1;
		stmt.setInt(i++, message.getIdmessage());
		int deleted = stmt.executeUpdate();

		_log.debug("Record effettivamente cancellati: " + deleted);

		stmt.close();

		return deleted;
	}
	

	public int countMessageForAdvice(int idadvice, Connection connection)
			throws SQLException {
		_log.info("Invoco la query: " + COUNT_MESSAGE_4_ADVICE);

		PreparedStatement stmt = connection
				.prepareStatement(COUNT_MESSAGE_4_ADVICE);
		int i = 1;
		stmt.setInt(i++, idadvice);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int count = rs.getInt(1);

		_log.debug("Valore recuperato: " + count);

		rs.close();
		stmt.close();

		return count;
	}

	public int countInmessage(String receiver, Connection connection)
			throws SQLException {
		_log.info("Invoco la query: " + COUNT_INMESSAGE);

		PreparedStatement stmt = connection.prepareStatement(COUNT_INMESSAGE);
		int i = 1;
		stmt.setString(i++, receiver);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int count = rs.getInt(1);

		_log.debug("Valore recuperato: " + count);

		rs.close();
		stmt.close();

		return count;
	}

	public int countOutmessage(String send, Connection connection)
			throws SQLException {
		_log.info("Invoco la query: " + COUNT_OUTMESSAGE);

		PreparedStatement stmt = connection.prepareStatement(COUNT_OUTMESSAGE);
		int i = 1;
		stmt.setString(i++, send);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int count = rs.getInt(1);

		_log.debug("Valore recuperato: " + count);

		rs.close();
		stmt.close();

		return count;
	}
}
