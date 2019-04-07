package ita.social.manager;

import ita.social.DAO.AdviceDAO;
import ita.social.DAO.EventDAO;
import ita.social.model.Advice;
import ita.social.model.CalendarTestEvent;
import ita.social.model.Message;
import ita.social.utils.AdviceException;
import ita.social.utils.DBConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class EventManager {
	private static final Log _log = LogFactory.getLog(EventManager.class);
	private static EventDAO dao = new EventDAO();
	
	
	public static List<CalendarTestEvent> getEvent(String username) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<CalendarTestEvent> elenco = dao.getEvent(username, connection);
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
	
	public static List<CalendarTestEvent> getSelectedEvent(CalendarTestEvent event) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<CalendarTestEvent> elenco = dao.getSelectedEvent(event, connection);
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
	
	public static List<CalendarTestEvent> getEventFromAdvice(Advice advice) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<CalendarTestEvent> elenco = dao.getEventFromAdvice(advice, connection);
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
	
	public static List<CalendarTestEvent> getEventFromUser(String tousername) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<CalendarTestEvent> elenco = dao.getEventFromUser(tousername, connection);
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
	
	public static List<CalendarTestEvent> getDeleteEvent(String username) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<CalendarTestEvent> elenco = dao.getDeleteEvent(username, connection);
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
	
	public static CalendarTestEvent insert(CalendarTestEvent event) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			event = dao.insert(event, connection);
			connection.close();
			return event;
		} catch (NamingException e) {
			_log.fatal(e);
			throw new AdviceException(e);
		} catch (SQLException e) {
			_log.error(e);
			throw new AdviceException(e);
		}
	}

	
	public static int countAll() throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			int count = dao.countAll(connection);
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
	
	public static int countForAdvice(Advice advice) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			int count = dao.countForAdvice(advice, connection);
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

	public static int countUser(String username) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			int count = dao.countUser(username, connection);
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

	public static int countModifiedUser(String username) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			int count = dao.countModifiedUser(username, connection);
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
	
	public static int countFromUser(String username) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			int count = dao.countFromUser(username, connection);
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
	
	public static int countNewEvent(String username) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			int count = dao.countNewEvent(username, connection);
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
	
	public static int delete(CalendarTestEvent event) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			int count = dao.delete(event, connection);
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
	
	public static CalendarTestEvent update(CalendarTestEvent event) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			event = dao.update(event, connection);
			connection.close();
			return event;
		} catch (NamingException e) {
			_log.fatal(e);
			throw new AdviceException(e);
		} catch (SQLException e) {
			_log.error(e);
			throw new AdviceException(e);
		}
	}
	
	public static CalendarTestEvent predelete(CalendarTestEvent event) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			event = dao.predelete(event, connection);
			connection.close();
			return event;
		} catch (NamingException e) {
			_log.fatal(e);
			throw new AdviceException(e);
		} catch (SQLException e) {
			_log.error(e);
			throw new AdviceException(e);
		}
	}
	
	public static CalendarTestEvent confirm(CalendarTestEvent event) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			event = dao.confirm(event, connection);
			connection.close();
			return event;
		} catch (NamingException e) {
			_log.fatal(e);
			throw new AdviceException(e);
		} catch (SQLException e) {
			_log.error(e);
			throw new AdviceException(e);
		}
	}
	
	
	
	//PARTE FEEDBACK

	public static List<CalendarTestEvent> getPassedEvent(String username) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<CalendarTestEvent> elenco = dao.getPassedEvent(username, connection);
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

	
	public static int countPassed(String username) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			int i = dao.countPassed(username, connection);
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
	
	
	public static List<CalendarTestEvent> getFeed(String username) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<CalendarTestEvent> elenco = dao.getFeed(username, connection);
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
	
	public static List<CalendarTestEvent> getMyFeed(String username) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<CalendarTestEvent> elenco = dao.getMyFeed(username, connection);
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
	
	public static List<CalendarTestEvent> getUserFeed(String username) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<CalendarTestEvent> elenco = dao.getUserFeed(username, connection);
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
	
	public static int countFeed(Advice advice) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			int i = dao.countFeed(advice, connection);
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
	
	public static int countAllFeed(Advice advice) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			int i = dao.countAllFeed(advice, connection);
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
	
	public static CalendarTestEvent insertFeed(CalendarTestEvent event) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			event = dao.insertFeed(event, connection);
			connection.close();
			return event;
		} catch (NamingException e) {
			_log.fatal(e);
			throw new AdviceException(e);
		} catch (SQLException e) {
			_log.error(e);
			throw new AdviceException(e);
		}
	}
	
	public static int updateCredit(String username,String toUsername) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			int i = dao.updateCredit(username,toUsername, connection);
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
}