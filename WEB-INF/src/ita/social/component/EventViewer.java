package ita.social.component;

import ita.social.app.Calendarr;
import ita.social.delegate.BusinessDelegate;
import ita.social.model.Advice;
import ita.social.model.CalendarTestEvent;
import ita.social.utils.AdviceException;

import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class EventViewer extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(GridAdvice.class);
	protected BusinessDelegate bd = new BusinessDelegate();
	private VerticalLayout layout = new VerticalLayout();
	private int numEvent;
	private int numModifiedEvent;
	private int numNewEvent;
	private int numEventPassed;
	private int news;
	private List<CalendarTestEvent> eventi;
	private List<CalendarTestEvent> modificati;
	private Window mainWindow;
	private String username;
	private Panel panel = new Panel();
	private Calendarr application;

	// inizialmente no parametri
	public EventViewer() {
		Label label = new Label("<h1></h1>", Label.CONTENT_XHTML);
		label.addStyleName(Reindeer.PANEL_LIGHT);
		layout.addComponent(label);
		addComponent(layout);
	}

	public EventViewer(Calendarr app, Window main, String userName) {
		application = app;
		mainWindow = main;
		username = userName;

		// recupero eventi per username
		try {
			_log.info("QUERY recupero eventi per username");
			//conto eventi inseriti
			numEvent = bd.countUser(username);
			//conto eventi inseriti da gestire
			numModifiedEvent = bd.countModifiedUser(username);
			//conto eventi per cui lasciare feed
			numEventPassed = bd.countPassed(username);
			//controllo se qualcuno richiede eliminazione eventi
			eventi = bd.getDeleteEvent(username);
			// eventi = bd.getEvent(username);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}
		String advices = new String("(" + numEvent + ") Iscrizioni inserite" 
				                    + " (" + numModifiedEvent + ") da confermare" );

		String advicesFeeds = new String("(" + numEventPassed
				+ ") Feedback da lasciare ");

		Panel p = new Panel();
		String deletedEvent ="";
		String deletedEvent2 ="";
		if (eventi.size() > 0) {
			for (int i = 0; i < eventi.size(); i++) {
				if (userName.equals(eventi.get(i).getUsername())) {
					if (username.equals(eventi.get(i).getFlag())) {
						_log.info("cazzo vuoi aspetta");
					} else {
						_log.info("IL BASTARDO!!");
						deletedEvent = new String( eventi.get(i).getFlag()
								+ " Richiede eliminazione evento ");
						layout.addComponent(new DeleteFeedback(application, mainWindow,
								deletedEvent, username,eventi.get(i)));
						_log.info(eventi.get(i));
					}
				}

				if (userName.equals(eventi.get(i).getTousername())) {
					if (username.equals(eventi.get(i).getFlag())) {
						_log.info("cazzo vuoi aspetta");
					} else {
						_log.info("IL BASTARDO!!");
						deletedEvent2 = new String( eventi.get(i).getFlag()
								+ " Richiede eliminazione evento ");

						layout.addComponent(new DeleteFeedback(application, mainWindow,
								deletedEvent2, username,eventi.get(i)));
						_log.info(eventi.get(i));
					}
				}
			}
		}
		// recupero eventi da username
		try {
			_log.info("QUERY recupero eventi da username");
			//conto eventi da altri utenti
			numEvent = bd.countFromUser(username);
			//conto eventi da confermare 
			numNewEvent = bd.countNewEvent(username);
			// eventi = bd.getEventFromUser(username);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}
		String advicesOther = new String("(" + numEvent
				+ ") Iscrizioni da utenti  " + " (" + numNewEvent
				+ ") da confermare");

		layout.addComponent(new CalendarWindow(application, mainWindow,
				advices, username, "mine"));

		layout.addComponent(new CalendarWindow(application, mainWindow,
				advicesOther, username, "other"));

		layout.addComponent(new CalendarWindowFeedback(application, mainWindow,
				advicesFeeds, username));

		
		panel.setStyleName(Reindeer.PANEL_LIGHT);
		panel.addComponent(layout);
		addComponent(panel);

	}

}