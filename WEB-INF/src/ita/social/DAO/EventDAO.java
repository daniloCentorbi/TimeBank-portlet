package ita.social.DAO;

import ita.social.model.Advice;
import ita.social.model.CalendarTestEvent;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class EventDAO {
	private static final Log _log = LogFactory.getLog(EventDAO.class);
	private static final String COUNT_ALL = "SELECT COUNT(*) FROM event";
	//recupero eventi inseriti
	private static final String GETEVENT = "SELECT * FROM event WHERE username = ?";
	private static final String COUNT_USER = "SELECT COUNT(*) FROM event WHERE username = ? AND startdate>CURRENT_TIMESTAMP";	
	private static final String COUNT_MY_USER = "SELECT COUNT(*) FROM event WHERE username = ? AND startdate>CURRENT_TIMESTAMP AND state = 'modificato' AND flag <> ?";	
	//conto eventi per miei annunci
	private static final String COUNT_FROM_USER = "SELECT COUNT(*) FROM event WHERE tousername = ? AND startdate>CURRENT_TIMESTAMP";
	private static final String COUNT_NEW_EVENT = "SELECT COUNT(*) FROM event WHERE tousername = ? AND state = 'inserito' AND flag <> ? AND startdate>CURRENT_TIMESTAMP OR tousername = ? AND state = 'modificato' AND startdate>CURRENT_TIMESTAMP AND flag <> ?";
	//recupero evento da event click
	private static final String GETSELECTEDEVENT = "SELECT * FROM event WHERE startdate = ? AND enddate=? AND description=?";	
	//recupero eventi degli utenti per miei annunci	
	private static final String GETEVENTFROMUSER = "SELECT * FROM event WHERE tousername = ?";
	//recupero evento da clickato treeTableAdvice 
	private static final String GETEVENTFROMADCVICE = "SELECT * FROM event WHERE idadvice = ?";	
	private static final String COUNT_ADVICE = "SELECT COUNT(*) FROM event WHERE idadvice = ?";
	//utility
	private static final String INSERT = "INSERT INTO event (userid,idadvice,username,startdate,enddate,caption,description,state,tousername,viewed,vote,note,tipo,flag) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String DELETE = "DELETE FROM event WHERE idevent=?";
	private static final String UPDATE = "UPDATE event SET startdate=?, enddate=?,state=?,viewed=?,flag=? WHERE idevent=?";
	private static final String CONFIRM = "UPDATE event SET state=?,viewed=?,flag=? WHERE idevent=?";
	private static final String VIEWED = "UPDATE event SET viewed=? WHERE idevent=?";	
	//query FEEDBACK	
	private static final String GETPASSEDEVENT = "SELECT * FROM event WHERE username=? AND tipo='offerta' AND startdate<CURRENT_TIMESTAMP AND note='blank' AND vote=0 OR tousername=? AND tipo='richiesta' AND startdate<CURRENT_TIMESTAMP note='blank' AND vote=0";
	private static final String COUNT_PASSED = "SELECT COUNT(*) FROM event WHERE username=? AND tipo='offerta' AND startdate<CURRENT_TIMESTAMP AND note='blank' AND vote=0 OR tousername=? AND tipo='richiesta' AND startdate<CURRENT_TIMESTAMP AND note='blank' AND vote=0";	
	private static final String GETDELETEEVENT = "SELECT * FROM event WHERE username = ? AND state = 'eliminare' OR tousername = ? AND state = 'eliminare'";
	//query FEEDBACK
	private static final String GET = "SELECT * FROM event WHERE username = ? AND note <> 'blank'  OR tousername = ? AND note <> 'blank'  ";	
	private static final String GETMYFEED = "SELECT * FROM event WHERE username = ? AND tipo = 'offerta' AND note <> 'blank' OR tousername = ? AND tipo = 'richiesta' AND note <> 'blank'";
	private static final String GETUSERFEED = "SELECT * FROM event WHERE tousername = ? AND tipo='offerta' AND note <> 'blank' OR  username = ? AND tipo='richiesta' AND note <> 'blank'";	
	private static final String COUNTFEED = "SELECT COUNT(*) FROM event WHERE idadvice = ? AND note <> ?";
	private static final String COUNT_ALL_FEED = "SELECT COUNT(*) FROM feedback WHERE username = ? AND note <> ?";
	private static final String INSERT_FEED = "UPDATE event SET vote= ? , note= ? , state = ? , flag = ? WHERE idevent = ?";
	private static final String INSERT_CREDIT = "UPDATE user_ set credit= credit+1 where screenname= ? ";
	private static final String REMOVE_CREDIT = "UPDATE user_ set credit= credit-1 where screenname = ? ";
	

	
	public List<CalendarTestEvent> getResults(ResultSet rs) throws SQLException {
		List<CalendarTestEvent> elenco = new ArrayList<CalendarTestEvent>();
		while (rs.next()) {
			CalendarTestEvent model = new CalendarTestEvent();
			model.setIdUser(rs.getInt("userid"));
			model.setIdadvice(rs.getInt("idadvice"));
			model.setUsername(rs.getString("username"));
			model.setStart(rs.getTimestamp("startdate"));
			model.setEnd(rs.getTimestamp("enddate"));
			model.setCaption(rs.getString("caption"));
			model.setDescription(rs.getString("description"));
			model.setState(rs.getString("state"));
			model.setTousername(rs.getString("tousername"));
			model.setIdevent(rs.getInt("idevent"));
			model.setViewed(rs.getBoolean("viewed"));
			model.setVote(rs.getInt("vote"));
			model.setNote(rs.getString("note"));
			model.setTipo(rs.getString("tipo"));
			model.setFlag(rs.getString("flag"));
			
			elenco.add(model);
		}
		_log.debug("ritorno elenco------------: " + elenco);
		return elenco;
	}


	public List<CalendarTestEvent> getEvent(String username, Connection connection) throws SQLException {
		_log.info("Invoco la query: " + GETEVENT);

		PreparedStatement stmt = connection.prepareStatement(GETEVENT);
		int i = 1;
		stmt.setString(i++, username);
		ResultSet res = stmt.executeQuery();
		List<CalendarTestEvent> elenco = getResults(res);

		_log.debug("Recuperata lista: " + elenco);

		res.close();
		stmt.close();

		return elenco;
	}
	
	public List<CalendarTestEvent> getSelectedEvent(CalendarTestEvent event, Connection connection) throws SQLException {
		_log.info("Invoco la query: " + GETSELECTEDEVENT);

		PreparedStatement stmt = connection.prepareStatement(GETSELECTEDEVENT);
		int i = 1;

		stmt.setTimestamp(i++,  new java.sql.Timestamp(event.getStart().getTime()));
		stmt.setTimestamp(i++,  new java.sql.Timestamp(event.getEnd().getTime()));
		stmt.setString(i++, event.getDescription());
		ResultSet res = stmt.executeQuery();
		List<CalendarTestEvent> elenco = getResults(res);

		_log.debug("Recuperata lista: " + elenco);

		res.close();
		stmt.close();

		return elenco;
	}
	
	public List<CalendarTestEvent> getEventFromUser(String tousername, Connection connection) throws SQLException {
		_log.info("Invoco la query: " + GETEVENTFROMUSER);

		PreparedStatement stmt = connection.prepareStatement(GETEVENTFROMUSER);
		int i = 1;
		stmt.setString(i++, tousername);
		ResultSet res = stmt.executeQuery();
		List<CalendarTestEvent> elenco = getResults(res);

		_log.debug("Recuperata lista: " + elenco);

		res.close();
		stmt.close();

		return elenco;
	}
	
	public List<CalendarTestEvent> getEventFromAdvice(Advice advice, Connection connection) throws SQLException {
		_log.info("Invoco la query: " + GETEVENTFROMADCVICE);

		PreparedStatement stmt = connection.prepareStatement(GETEVENTFROMADCVICE);
		int i = 1;
		stmt.setInt(i++, advice.getIdadvice());
		ResultSet res = stmt.executeQuery();
		List<CalendarTestEvent> elenco = getResults(res);

		_log.debug("Recuperata lista: " + elenco);

		res.close();
		stmt.close();

		return elenco;
	}
	
	public List<CalendarTestEvent> getDeleteEvent(String username, Connection connection) throws SQLException {
		_log.info("Invoco la query: " + GETDELETEEVENT);

		PreparedStatement stmt = connection.prepareStatement(GETDELETEEVENT);
		int i = 1;
		stmt.setString(i++, username);
		stmt.setString(i++, username);
		ResultSet res = stmt.executeQuery();
		List<CalendarTestEvent> elenco = getResults(res);

		_log.debug("Recuperata lista: " + elenco);

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

	public int countForAdvice(Advice advice, Connection connection) throws SQLException {
		_log.debug("Invoco la query: " + COUNT_ADVICE);

		PreparedStatement stmt = connection.prepareStatement(COUNT_ADVICE);
		int i = 1;
		stmt.setInt(i++, advice.getIdadvice());
		ResultSet res = stmt.executeQuery();
		res.next();
		int count = res.getInt(1);

		_log.debug("Valore recuperato: " + count);

		res.close();
		stmt.close();

		return count;
	}
	
	public int countUser(String username, Connection connection) throws SQLException {
		_log.debug("Invoco la query: " + COUNT_USER);

		PreparedStatement stmt = connection.prepareStatement(COUNT_USER);
		int i = 1;
		stmt.setString(i++, username);
		ResultSet res = stmt.executeQuery();
		res.next();
		int count = res.getInt(1);

		_log.debug("Valore recuperato: " + count);

		res.close();
		stmt.close();

		return count;
	}
	
	
	public int countModifiedUser(String username, Connection connection) throws SQLException {
		_log.debug("Invoco la query: " + COUNT_MY_USER);

		PreparedStatement stmt = connection.prepareStatement(COUNT_MY_USER);
		int i = 1;
		stmt.setString(i++, username);
		stmt.setString(i++, username);
		ResultSet res = stmt.executeQuery();
		res.next();
		int count = res.getInt(1);

		_log.debug("Valore recuperato: " + count);

		res.close();
		stmt.close();

		return count;
	}
	
	public int countFromUser(String username, Connection connection) throws SQLException {
		_log.debug("Invoco la query: " + COUNT_FROM_USER);

		PreparedStatement stmt = connection.prepareStatement(COUNT_FROM_USER);
		int i = 1;
		stmt.setString(i++, username);
		ResultSet res = stmt.executeQuery();
		res.next();
		int count = res.getInt(1);

		_log.debug("Valore recuperato: " + count);

		res.close();
		stmt.close();

		return count;
	}
	
	public int countNewEvent(String username, Connection connection) throws SQLException {
		_log.debug("Invoco la query: " + COUNT_NEW_EVENT);

		PreparedStatement stmt = connection.prepareStatement(COUNT_NEW_EVENT);
		int i = 1;
		stmt.setString(i++, username);	
		stmt.setString(i++, username);
		stmt.setString(i++, username);		
		stmt.setString(i++, username);	
		ResultSet res = stmt.executeQuery();
		res.next();
		int count = res.getInt(1);

		_log.debug("Valore recuperato: " + count);

		res.close();
		stmt.close();

		return count;
	}
	
	public CalendarTestEvent insert(CalendarTestEvent event, Connection connection)
			throws SQLException {
		_log.info("Invoco la query [" + INSERT + "] con l'oggetto [" + event
				+ "]");

		PreparedStatement stmt = connection.prepareStatement(INSERT,
				Statement.RETURN_GENERATED_KEYS);
		int i = 1;
		
		stmt.setInt(i++, event.getIdUser());
		stmt.setInt(i++, event.getIdadvice());
		stmt.setString(i++, event.getUsername());
		stmt.setTimestamp(i++,  new java.sql.Timestamp(event.getStartdate().getTime()));
		stmt.setTimestamp(i++,  new java.sql.Timestamp(event.getEnddate().getTime()));
		stmt.setString(i++, event.getCaption());
		stmt.setString(i++, event.getDescription());
		stmt.setString(i++, event.getState());
		stmt.setString(i++, event.getTousername());
		stmt.setBoolean(i++, event.getViewed());
		stmt.setInt(i++, event.getVote());
		stmt.setString(i++, event.getNote());
		stmt.setString(i++, event.getTipo());
		stmt.setString(i++, event.getFlag());
		stmt.executeUpdate();

		_log.debug("Oggetto valorizzato: " + event);

		stmt.close();

		return event;
	}

	public int delete(CalendarTestEvent event, Connection connection)
			throws SQLException {
		_log.info("Invoco la query [" + DELETE + "] con l'oggetto [" + event
				+ "]");

		PreparedStatement stmt = connection.prepareStatement(DELETE);
		int i = 1;
		stmt.setInt(i++, event.getIdevent());
		int deleted = stmt.executeUpdate();

		_log.debug("Record effettivamente cancellati: " + deleted);

		stmt.close();

		return deleted;
	}


	public CalendarTestEvent update(CalendarTestEvent event, Connection connection)
			throws SQLException {
		_log.info("Invoco la query [" + UPDATE + "] sull'oggetto [" + event
				+ "]");

		PreparedStatement stmt = connection.prepareStatement(UPDATE);
		int i = 1;
		stmt.setTimestamp(i++,  new java.sql.Timestamp(event.getStartdate().getTime()));
		stmt.setTimestamp(i++,  new java.sql.Timestamp(event.getEnddate().getTime()));
		stmt.setString(i++, event.getState());
		stmt.setBoolean(i++, event.getViewed());		
		stmt.setString(i++, event.getFlag());
		stmt.setInt(i++, event.getIdevent());
		stmt.executeUpdate();
		stmt.close();

		return event;
	}
	
	public CalendarTestEvent predelete(CalendarTestEvent event, Connection connection)
			throws SQLException {
		_log.info("Invoco la query [" + UPDATE + "] sull'oggetto [" + event
				+ "]");

		PreparedStatement stmt = connection.prepareStatement(UPDATE);
		int i = 1;
		stmt.setTimestamp(i++,  new java.sql.Timestamp(event.getStartdate().getTime()));
		stmt.setTimestamp(i++,  new java.sql.Timestamp(event.getEnddate().getTime()));
		stmt.setString(i++, event.getState());
		stmt.setBoolean(i++, event.getViewed());
		stmt.setString(i++, event.getFlag());
		stmt.setInt(i++, event.getIdevent());
		stmt.executeUpdate();
		stmt.close();

		return event;
	}
	public CalendarTestEvent confirm(CalendarTestEvent event, Connection connection)
			throws SQLException {
		_log.info("Invoco la query [" + CONFIRM + "] sull'oggetto [" + event
				+ "]");

		PreparedStatement stmt = connection.prepareStatement(CONFIRM);
		int i = 1;
		stmt.setString(i++, event.getState());
		stmt.setBoolean(i++, event.getViewed());	
		stmt.setString(i++, event.getFlag());
		stmt.setInt(i++, event.getIdevent());
		stmt.executeUpdate();
		stmt.close();

		return event;
	}
	
	
	//PARTE FEEDBACK
	
	public List<CalendarTestEvent> getPassedEvent(String username, Connection connection) throws SQLException {
		_log.info("Invoco la query: " + GETPASSEDEVENT);

		PreparedStatement stmt = connection.prepareStatement(GETPASSEDEVENT);
		int i = 1;
		stmt.setString(i++, username);
		stmt.setString(i++, username);
		ResultSet rs = stmt.executeQuery();
		List<CalendarTestEvent> elenco = getResults(rs);
		_log.debug("Recuperati " + elenco.size() + " elementi");

		rs.close();
		stmt.close();

		return elenco;
	}
	
	public int countPassed(String username, Connection connection) throws SQLException {
		_log.info("Invoco la query: " + COUNT_PASSED);

		PreparedStatement stmt = connection.prepareStatement(COUNT_PASSED);
		int i = 1;
		stmt.setString(i++, username);
		stmt.setString(i++, username);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int count = rs.getInt(1);

		_log.debug("Valore recuperato: " + count);

		rs.close();
		stmt.close();

		return count;
	}
	
	public List<CalendarTestEvent> getFeed(String username, Connection connection) throws SQLException {
		_log.info("Invoco la query: " + GET);

		PreparedStatement stmt = connection.prepareStatement(GET);
		int i = 1;
		stmt.setString(i++, username);
		stmt.setString(i++, username);
		ResultSet res = stmt.executeQuery();
		List<CalendarTestEvent> elenco = getResults(res);

		_log.debug("Recuperata lista: " + elenco);

		res.close();
		stmt.close();

		return elenco;
	}
	
	public List<CalendarTestEvent> getMyFeed(String username, Connection connection) throws SQLException {
		_log.info("Invoco la query: " + GETMYFEED);

		PreparedStatement stmt = connection.prepareStatement(GETMYFEED);
		int i = 1;
		stmt.setString(i++, username);
		stmt.setString(i++, username);
		ResultSet rs = stmt.executeQuery();
		List<CalendarTestEvent> elenco = getResults(rs);
		_log.debug("Recuperati " + elenco.size() + " elementi");

		rs.close();
		stmt.close();

		return elenco;
	}
	
	public List<CalendarTestEvent> getUserFeed(String username, Connection connection) throws SQLException {
		_log.info("Invoco la query: " + GETUSERFEED);

		PreparedStatement stmt = connection.prepareStatement(GETUSERFEED);
		int i = 1;
		stmt.setString(i++, username);
		stmt.setString(i++, username);
		ResultSet rs = stmt.executeQuery();
		List<CalendarTestEvent> elenco = getResults(rs);
		_log.debug("Recuperati " + elenco.size() + " elementi");

		rs.close();
		stmt.close();

		return elenco;
	}

	public int countFeed(Advice advice, Connection connection) throws SQLException {
		_log.info("Invoco la query: " + COUNTFEED);

		PreparedStatement stmt = connection.prepareStatement(COUNTFEED);
		int i = 1;
		stmt.setInt(i++, advice.getIdadvice());
		stmt.setString(i++, "blank");
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int count = rs.getInt(1);

		_log.debug("Valore recuperato: " + count);

		rs.close();
		stmt.close();

		return count;
	}
	
	public int countAllFeed(Advice advice, Connection connection) throws SQLException {
		_log.info("Invoco la query: " + COUNT_ALL_FEED);

		PreparedStatement stmt = connection.prepareStatement(COUNT_ALL_FEED);
		int i = 1;
		stmt.setString(i++, advice.getUsername());
		stmt.setString(i++, "blank");
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int count = rs.getInt(1);

		_log.debug("Valore recuperato: " + count);

		rs.close();
		stmt.close();

		return count;
	}

	public CalendarTestEvent insertFeed(CalendarTestEvent event, Connection connection)
			throws SQLException {
		_log.info("Invoco la query [" + INSERT_FEED + "] con l'oggetto [" + event
				+ "]");

		PreparedStatement stmt = connection.prepareStatement(INSERT_FEED,
				Statement.RETURN_GENERATED_KEYS);
		int i = 1;
		stmt.setInt(i++, event.getVote());
		stmt.setString(i++, event.getNote());
		stmt.setString(i++, event.getState());
		stmt.setString(i++, event.getFlag());
		stmt.setInt(i++, event.getIdevent());		
		stmt.executeUpdate();

		_log.debug("Oggetto valorizzato: " + event);

		stmt.close();

		return event;
	}
	
	public int updateCredit(String username,String toUsername, Connection connection)
			throws SQLException {
		_log.info("Invoco la query [" + INSERT_CREDIT + "] con l'oggetto [" + username
				+ "]");

		PreparedStatement stmt = connection.prepareStatement(INSERT_CREDIT,
				Statement.RETURN_GENERATED_KEYS);
		int i = 1;
		stmt.setString(i++, username);	
		stmt.executeUpdate();

		
		_log.info("Invoco la query [" + REMOVE_CREDIT + "] con l'oggetto [" + username
				+ "]");
		stmt = connection.prepareStatement(REMOVE_CREDIT,
				Statement.RETURN_GENERATED_KEYS);
		i = 1;
		stmt.setString(i++, toUsername);	
		stmt.executeUpdate();
		
		stmt.close();

		
		return 0;
	}
	
}
