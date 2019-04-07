package ita.social.manager;

import ita.social.DAO.MessageDAO;
import ita.social.model.Message;
import ita.social.utils.AdviceException;
import ita.social.utils.DBConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageManager {
	private static final Log _log = LogFactory.getLog(AdviceManager.class);
	private static MessageDAO dao = new MessageDAO();
	
	
	public static List<Message> getIn(String username) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<Message> elenco = dao.getIn(username,connection);
			connection.close();

			return elenco;
		} catch (NamingException e) {
			_log.fatal(e);
			throw new AdviceException(e);
		} catch (SQLException e) {
			_log.error(e);
			throw new AdviceException(e);
		}
	}



	public static List<Message> getOut(String username) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<Message> elenco = dao.getOut(username, connection);
			connection.close();

			return elenco;
		} catch (NamingException e) {
			_log.fatal(e);
			throw new AdviceException(e);
		} catch (SQLException e) {
			_log.error(e);
			throw new AdviceException(e);
		}
	}
	
	
	public static List<Message> getAdvice(int idadvice) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<Message> elenco = dao.getAdvice(idadvice,connection);
			connection.close();

			return elenco;
		} catch (NamingException e) {
			_log.fatal(e);
			throw new AdviceException(e);
		} catch (SQLException e) {
			_log.error(e);
			throw new AdviceException(e);
		}
	}
	
	public static Message insert(Message message) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			message = dao.insert(message, connection);
			connection.close();
			return message;
		} catch (NamingException e) {
			_log.fatal(e);
			throw new AdviceException(e);
		} catch (SQLException e) {
			_log.error(e);
			throw new AdviceException(e);
		}
	}
	
	public static int delete(Message message) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			int i = dao.delete(message, connection);
			connection.close();
			return i;
		} catch (NamingException e) {
			_log.fatal(e);
			throw new AdviceException(e);
		} catch (SQLException e) {
			_log.error(e);
			throw new AdviceException(e);
		}
	}
	
	public static int countMessageForAdvice(int idadvice) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			int count = dao.countMessageForAdvice(idadvice , connection);
			connection.close();
			return count;
		} catch (NamingException e) {
			_log.fatal(e);
			throw new AdviceException(e);
		} catch (SQLException e) {
			_log.error(e);
			throw new AdviceException(e);
		}
	}
	
	public static int countInmessage(String receiver) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			int count = dao.countInmessage(receiver , connection);
			connection.close();
			return count;
		} catch (NamingException e) {
			_log.fatal(e);
			throw new AdviceException(e);
		} catch (SQLException e) {
			_log.error(e);
			throw new AdviceException(e);
		}
	}
	
	public static int countOutmessage(String send) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			int count = dao.countOutmessage(send , connection);
			connection.close();
			return count;
		} catch (NamingException e) {
			_log.fatal(e);
			throw new AdviceException(e);
		} catch (SQLException e) {
			_log.error(e);
			throw new AdviceException(e);
		}
	}
}