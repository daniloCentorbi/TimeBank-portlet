package ita.social.component;

import ita.social.app.Calendarr;
import ita.social.delegate.BusinessDelegate;
import ita.social.model.Advice;
import ita.social.model.CalendarTestEvent;
import ita.social.utils.AdviceException;

import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class HistoryAdvice extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(GridAdvice.class);
	protected BusinessDelegate bd = new BusinessDelegate();
	private VerticalLayout layout = new VerticalLayout();
	private List<Advice> elenco;
	private int numFeed;
	private int numAdvice;
	private List<CalendarTestEvent> eventi;
	private List<CalendarTestEvent> feeds;
	private Window mainWindow;
	private String username;
	private Panel panel = new Panel();

	// inizialmente no parametri
	public HistoryAdvice() {
		Label label = new Label("<h1></h1>", Label.CONTENT_XHTML);
		label.addStyleName(Reindeer.PANEL_LIGHT);
		layout.addComponent(label);
		addComponent(layout);
	}

	public HistoryAdvice(Window main, Advice advice, String userName) {
		mainWindow = main;
		username = userName;
		// Recupero annuncio per annuncio selezionato
		try {
			_log.info("QUERY recupero advice");
			elenco = bd.getOne(advice);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}

		for (int i = 0; i < elenco.size(); i++) {
			// recupero eventi per ogni annuncio
			try {
				_log.info("QUERY recupero iscrizioni per advice");
				numAdvice = bd.countForAdvice(elenco.get(i));
				eventi = bd.getEventFromAdvice(elenco.get(i));
			} catch (AdviceException e) {
				_log.error(e);
				e.printStackTrace();
			}

			String advices = new String("(" + numAdvice
					+ ") Iscrizioni da gestire per questo annuncio");
			// layout.addComponent(link);
			layout.addComponent(new CalendarWindowAdvice(mainWindow, advices,
					username, elenco.get(i)));

			// recupero feedback per ogni annuncio
			try {
				_log.info("QUERY 3");
				numFeed = bd.countFeed(elenco.get(i));
			} catch (AdviceException e) {
				_log.error(e);
				e.printStackTrace();
			}
			String feeds = new String("(" + numFeed
					+ ") Feedback per questo annuncio");

			// layout.addComponent(feeds);
			layout.addComponent(new CalendarWindowFeedback(new Calendarr(),mainWindow, feeds,
					username));
			panel.setStyleName(Reindeer.PANEL_LIGHT);
			panel.addComponent(layout);
			addComponent(panel);
		}

	}

}
