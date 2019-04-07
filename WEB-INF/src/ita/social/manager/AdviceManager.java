package ita.social.manager;

import ita.social.DAO.AdviceDAO;
import ita.social.model.Advice;
import ita.social.model.Message;
import ita.social.utils.AdviceException;
import ita.social.utils.DBConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AdviceManager {
	private static final Log _log = LogFactory.getLog(AdviceManager.class);
	private static AdviceDAO dao = new AdviceDAO();
	
	
	public static List<Advice> getAll(String username) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<Advice> elenco = dao.getAll(username,connection);
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


	public static List<Advice> getUser(String username) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<Advice> elenco = dao.getAll(username,connection);
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
	
	
	public static List<Advice> get(int id) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<Advice> elenco = dao.get(id, connection);
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
	
	
	
	public static List<Advice> getOne(Advice advice) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<Advice> elenco = dao.getOne(advice, connection);
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
	
	
	public static List<Advice> getOneFromTitle(String title) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<Advice> elenco = dao.getOneFromTitle(title, connection);
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
	
	public static List<Advice> getOneOnly(int id) throws AdviceException{
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			List<Advice> elenco = dao.getOneOnly(id, connection);
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
	
	public static Advice insert(Advice advice) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			advice = dao.insert(advice, connection);
			connection.close();
			return advice;
		} catch (NamingException e) {
			_log.fatal(e);
			throw new AdviceException(e);
		} catch (SQLException e) {
			_log.error(e);
			throw new AdviceException(e);
		}
	}

	
	public static int delete(Advice advice) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			int i = dao.delete(advice, connection);
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
	
	public static Advice update(Advice advice) throws AdviceException {
		try {
			Connection connection = DBConnector.getInstance().getConnection();
			advice = dao.update(advice, connection);
			connection.close();
			return advice;
		} catch (NamingException e) {
			_log.fatal(e);
			throw new AdviceException(e);
		} catch (SQLException e) {
			_log.error(e);
			throw new AdviceException(e);
		}
	}
	
}
