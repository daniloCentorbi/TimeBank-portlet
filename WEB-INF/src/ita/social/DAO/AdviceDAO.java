package ita.social.DAO;

import ita.social.model.Advice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AdviceDAO {
	private static final Log _log = LogFactory.getLog(AdviceDAO.class);
	private static final String GETADVICE = "SELECT * FROM advice WHERE userid = ?";
	private static final String GETONEADVICE = "SELECT * FROM advice WHERE idadvice = ?";
	private static final String GETONEONLYADVICE = "SELECT * FROM advice WHERE idadvice = ?";
	private static final String GETONEFROMTITLE = "SELECT * FROM advice WHERE title = ?";
	private static final String GETALLADVICE = "SELECT * FROM advice WHERE username <> ?";
	private static final String USERADVICE = "SELECT * FROM advice WHERE username = ?";
	private static final String COUNT_ALL = "SELECT COUNT(*) FROM advice";
	private static final String INSERT = "INSERT INTO advice (userid,username,createdate,tipology,title,description,type) VALUES (?,?,?,?,?,?,?)";
	private static final String UPDATE = "UPDATE advice SET createdate=?, tipology=?, title=?, description=?, type=? WHERE idadvice=?";
	private static final String DELETE = "DELETE FROM advice WHERE idadvice=?";

	public List<Advice> getResults(ResultSet rs) throws SQLException {
		List<Advice> elenco = new ArrayList<Advice>();
		while (rs.next()) {
			Advice model = new Advice();
			model.setIdadvice(rs.getInt("idadvice"));
			model.setIdUser(rs.getInt("userid"));
			model.setUsername(rs.getString("username"));
			model.setDate(rs.getDate("createdate"));
			model.setTipology(rs.getString("tipology"));
			model.setTitle(rs.getString("title"));
			model.setDescription(rs.getString("description"));
			model.setTipo(rs.getString("type"));

			elenco.add(model);
		}
		_log.debug("ritorno elenco------------: " + elenco);
		return elenco;
	}

	public List<Advice> getAll(String username, Connection connection) throws SQLException {
		_log.info("Invoco la query: " + GETALLADVICE);

		PreparedStatement stmt = connection.prepareStatement(GETALLADVICE);
		int i = 1;
		stmt.setString(i++, username);
		ResultSet rs = stmt.executeQuery();
		List<Advice> elenco = getResults(rs);
		_log.debug("Recuperati " + elenco.size() + " elementi");

		rs.close();
		stmt.close();

		return elenco;
	}

	public List<Advice> getUser(String username, Connection connection) throws SQLException {
		_log.info("Invoco la query: " + GETALLADVICE);

		PreparedStatement stmt = connection.prepareStatement(GETALLADVICE);
		int i = 1;
		stmt.setString(i++, username);
		ResultSet rs = stmt.executeQuery();
		List<Advice> elenco = getResults(rs);
		_log.debug("Recuperati " + elenco.size() + " elementi");

		rs.close();
		stmt.close();

		return elenco;
	}
	
	public List<Advice> get(int id, Connection connection) throws SQLException {
		_log.info("Invoco la query: " + GETADVICE);

		PreparedStatement stmt = connection.prepareStatement(GETADVICE);
		int i = 1;
		stmt.setInt(i++, id);
		ResultSet res = stmt.executeQuery();
		List<Advice> elenco = getResults(res);

		_log.debug("Recuperati " + elenco.size() + " elementi");

		res.close();
		stmt.close();

		return elenco;
	}

	public List<Advice> getOne(Advice advice, Connection connection)
			throws SQLException {
		_log.info("Invoco la query: " + GETONEADVICE);

		PreparedStatement stmt = connection.prepareStatement(GETONEADVICE);
		int i = 1;
		_log.debug("Recupero idAdvice: " + advice.getIdadvice());
		stmt.setInt(i++, advice.getIdadvice());
		ResultSet res = stmt.executeQuery();
		List<Advice> elenco = getResults(res);

		_log.debug("Recuperato 1 elemento");

		res.close();
		stmt.close();

		return elenco;
	}

	
	public List<Advice> getOneFromTitle(String title, Connection connection)
			throws SQLException {
		_log.info("Invoco la query: " + GETONEFROMTITLE);
		PreparedStatement stmt = connection.prepareStatement(GETONEFROMTITLE);

		int i = 1;
		_log.debug("Recupero idAdvice da titolo: " + title);
		stmt.setString(i++, title);
		ResultSet res = stmt.executeQuery();
		List<Advice> elenco = getResults(res);

		_log.debug("Recuperato 1 elemento");

		res.close();
		stmt.close();

		return elenco;
	}
	
	public List<Advice> getOneOnly(int id, Connection connection)
			throws SQLException {
		_log.info("Invoco la query: " + GETONEONLYADVICE);

		PreparedStatement stmt = connection.prepareStatement(GETONEONLYADVICE);
		int i = 1;
		_log.debug("Recupero idAdvice: " + id);
		stmt.setInt(i++, id);
		ResultSet res = stmt.executeQuery();
		List<Advice> elenco = getResults(res);

		_log.debug("Recuperato 1 elemento");

		res.close();
		stmt.close();

		return elenco;
	}
	public int countAll(Connection connection) throws SQLException {
		_log.info("Invoco la query: " + COUNT_ALL);

		PreparedStatement stmt = connection.prepareStatement(COUNT_ALL);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int count = rs.getInt(1);

		_log.debug("Valore recuperato: " + count);

		rs.close();
		stmt.close();

		return count;
	}

	public Advice insert(Advice advice, Connection connection)
			throws SQLException {
		_log.info("Invoco la query [" + INSERT + "] con l'oggetto [" + advice
				+ "]");

		PreparedStatement stmt = connection.prepareStatement(INSERT,
				Statement.RETURN_GENERATED_KEYS);
		int i = 1;
		stmt.setInt(i++, advice.getIdUser());
		stmt.setString(i++, advice.getUsername());
		stmt.setDate(i++, new java.sql.Date(advice.getDate().getTime()));
		stmt.setString(i++, advice.getTipology());
		stmt.setString(i++, advice.getTitle());
		stmt.setString(i++, advice.getDescription());
		stmt.setString(i++, advice.getTipo());
		stmt.executeUpdate();

		_log.debug("Oggetto valorizzato: " + advice);

		stmt.close();

		return advice;
	}

	
	public int delete(Advice advice, Connection connection)
			throws SQLException {
		_log.info("Invoco la query [" + DELETE + "] con l'oggetto [" + advice
				+ "]");

		PreparedStatement stmt = connection.prepareStatement(DELETE);
		int i = 1;
		stmt.setInt(i++, advice.getIdadvice());
		int deleted = stmt.executeUpdate();

		_log.debug("Record effettivamente cancellati: " + deleted);

		stmt.close();

		return deleted;
	}


	public Advice update(Advice advice, Connection connection)
			throws SQLException {
		_log.info("Invoco la query [" + UPDATE + "] sull'oggetto [" + advice
				+ "]");

		PreparedStatement stmt = connection.prepareStatement(UPDATE);
		int i = 1;
		stmt.setDate(i++, new java.sql.Date(advice.getDate().getTime()));
		stmt.setString(i++, advice.getTipology());
		stmt.setString(i++, advice.getTitle());
		stmt.setString(i++, advice.getDescription());
		stmt.setString(i++, advice.getTipo());
		stmt.setInt(i++, advice.getIdadvice());		
		stmt.executeUpdate();
		
		_log.debug("Oggetto modificato: " + advice);
		
		stmt.close();

		return advice;
	}

}
