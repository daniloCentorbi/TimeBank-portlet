package ita.social.utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBConnector {
	private static final DBConnector instance = new DBConnector();

	private DBConnector() {
	}

	public static DBConnector getInstance() {
		return instance;
	}

	/**
	 * Restituisce la connessione alla base dati. Ricordarsi di <b>chiudere la
	 * connessione</b> alla fine del suo utilizzo.
	 * 
	 * @return Restituisce la connessione alla base dati
	 * @throws NamingException
	 *             In caso di errori nel recupero del datasource via JNDI
	 * @throws SQLException
	 *             In caso di errore nel recupero della connessione JDBC
	 */
	public Connection getConnection() throws NamingException, SQLException {
		InitialContext cxt = new InitialContext();
		DataSource ds = (DataSource) cxt
				.lookup("java:/comp/env/jdbc/timebank");

		return ds.getConnection();
	}
}