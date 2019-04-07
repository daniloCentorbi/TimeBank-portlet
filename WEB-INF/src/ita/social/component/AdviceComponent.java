package ita.social.component;

import java.util.List;

import ita.social.delegate.BusinessDelegate;
import ita.social.model.Advice;
import ita.social.utils.AdviceException;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

public class AdviceComponent extends CustomComponent implements
		Window.CloseListener {
	List<Advice> elenco;
	private static Log _log = LogFactoryUtil.getLog(AdviceComponent.class);
	protected BusinessDelegate bd = new BusinessDelegate();
	String username;
	int iduser;
	Window mainWindow;
	private VerticalLayout layout = new VerticalLayout();

	//richiamato da feedbackViewer per prendere un annuncio
	public AdviceComponent(Window mainWindow, int id, String userName,
			Advice advice) {
		iduser = id;
		username = userName;

		// Recupero annunci 
		try {
			elenco = bd.getOne(advice);
			_log.info("Ho l' elenco" + elenco);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}

		for (int i = 0; i < elenco.size(); i++) {
			if (userName.equals(elenco.get(i).getUsername())) {
				GridLayout grid = new GridLayout(3, 3);
				grid.addStyleName(Reindeer.LAYOUT_BLUE);
				// grid.setSpacing(true);
				grid.setMargin(true);
				grid.setSizeFull();
				grid.setRowExpandRatio(0, 0);
				grid.setRowExpandRatio(1, 0);
				grid.setRowExpandRatio(2, 1);

				grid.setColumnExpandRatio(1, 1);
				grid.setColumnExpandRatio(2, 1);
				grid.setColumnExpandRatio(3, 1);

				// The style allows us to visualize the cell borders in this
				// example.
				grid.addStyleName("gridexample");
		
				Label titolo = new Label("<h1>" + elenco.get(i).getTitle()
						+ "</h1>" + " " + elenco.get(i).getTipology(),
						Label.CONTENT_XHTML);
				titolo.addStyleName(Reindeer.PANEL_LIGHT);
				grid.addComponent(titolo, 0, 0);
				grid.setComponentAlignment(titolo, Alignment.TOP_LEFT);

				Label data = new Label("<h5>" + elenco.get(i).getDate()
						+ "</h5><br />" + elenco.get(i).getTipo(),
						Label.CONTENT_XHTML);
				data.addStyleName(Reindeer.PANEL_LIGHT);
				grid.addComponent(data, 2, 0);
				grid.setComponentAlignment(data, Alignment.TOP_RIGHT);

				Label descrizione = new Label("<br/><br/>"
						+ elenco.get(i).getDescription() + "<br/><br/>",
						Label.CONTENT_XHTML);
				descrizione.addStyleName(Reindeer.PANEL_LIGHT);
				grid.addComponent(descrizione, 0, 1, 1, 1);
				grid.setComponentAlignment(descrizione, Alignment.MIDDLE_CENTER);

				Link link = new Link(elenco.get(i).getUsername(),
						new ExternalResource("http://localhost:8080/it/web/"
								+ elenco.get(i).getUsername() + "/home"));
				grid.addComponent(link, 2, 2);
				grid.setComponentAlignment(link, Alignment.MIDDLE_CENTER);

				_log.info("componenti AGGIUNTI");
				layout.addComponent(grid);
				layout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
				setCompositionRoot(layout);
			} else {
				GridLayout grid = new GridLayout(3, 3);
				grid.addStyleName(Reindeer.LAYOUT_BLUE);
				// grid.setSpacing(true);
				grid.setMargin(true);
				grid.setSizeFull();
				grid.setRowExpandRatio(0, 0);
				grid.setRowExpandRatio(1, 0);
				grid.setRowExpandRatio(2, 1);

				grid.setColumnExpandRatio(1, 1);
				grid.setColumnExpandRatio(2, 1);
				grid.setColumnExpandRatio(3, 1);

				// The style allows us to visualize the cell borders in this
				// example.
				grid.addStyleName("gridexample");

				Label titolo = new Label("<h1>" + elenco.get(i).getTitle()
						+ "</h1>" + " " + elenco.get(i).getTipology(),
						Label.CONTENT_XHTML);
				titolo.addStyleName(Reindeer.PANEL_LIGHT);
				grid.addComponent(titolo, 0, 0);
				grid.setComponentAlignment(titolo, Alignment.TOP_LEFT);

				Label data = new Label("<h5>" + elenco.get(i).getDate()
						+ "</h5><br />" + elenco.get(i).getTipo(),
						Label.CONTENT_XHTML);
				data.addStyleName(Reindeer.PANEL_LIGHT);
				grid.addComponent(data, 2, 0);
				grid.setComponentAlignment(data, Alignment.TOP_RIGHT);

				Label descrizione = new Label("<br/><br/>"
						+ elenco.get(i).getDescription() + "<br/><br/>",
						Label.CONTENT_XHTML);
				descrizione.addStyleName(Reindeer.PANEL_LIGHT);
				grid.addComponent(descrizione, 0, 1, 1, 1);
				grid.setComponentAlignment(descrizione, Alignment.MIDDLE_CENTER);

				CalendarWindowInsert newEvent = new CalendarWindowInsert(
						mainWindow, iduser, username, elenco.get(i));
				grid.addComponent(newEvent, 0, 2);
				grid.setComponentAlignment(newEvent, Alignment.BOTTOM_LEFT);

				MessageWindow subWindow = new MessageWindow("Invia Messaggio",
						mainWindow, elenco.get(i).getUsername(), elenco.get(i)
								.getIdadvice(), username);
				grid.addComponent(subWindow, 1, 2);
				grid.setComponentAlignment(subWindow, Alignment.BOTTOM_LEFT);

				Link link = new Link(elenco.get(i).getUsername(),
						new ExternalResource("http://localhost:8080/it/web/"
								+ elenco.get(i).getUsername() + "/home"));
				grid.addComponent(link, 2, 2);
				grid.setComponentAlignment(link, Alignment.MIDDLE_CENTER);

				_log.info("componenti AGGIUNTI");
				layout.addComponent(grid);
				layout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
				setCompositionRoot(layout);
			}
		}
	}

	
	//richiamato da getAdvice per prendere tutti tranne i miei
	public AdviceComponent(Window mainWindow, int id, String user) {
		// addComponent(layout);
		iduser = id;
		username = user;
		// Recupero annunci
		try {
			elenco = bd.getAll(user);
			_log.info("Ho l' elenco" + elenco);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}

		for (int i = 0; i < elenco.size(); i++) {
			GridLayout grid = new GridLayout(3, 3);
			grid.addStyleName(Reindeer.LAYOUT_BLUE);
			// grid.setSpacing(true);
			grid.setMargin(true);
			grid.setSizeFull();
			grid.setRowExpandRatio(0, 0);
			grid.setRowExpandRatio(1, 0);
			grid.setRowExpandRatio(2, 1);

			grid.setColumnExpandRatio(1, 1);
			grid.setColumnExpandRatio(2, 1);
			grid.setColumnExpandRatio(3, 1);

			// The style allows us to visualize the cell borders in this
			// example.
			grid.addStyleName("gridexample");

			Label titolo = new Label("<h1>" + elenco.get(i).getTitle()
					+ "</h1>" + " " + elenco.get(i).getTipology(),
					Label.CONTENT_XHTML);
			titolo.addStyleName(Reindeer.PANEL_LIGHT);
			grid.addComponent(titolo, 0, 0);
			grid.setComponentAlignment(titolo, Alignment.TOP_LEFT);

			Label data = new Label("<h5>" + elenco.get(i).getDate()
					+ "</h5><br />" + elenco.get(i).getTipo(),
					Label.CONTENT_XHTML);
			data.addStyleName(Reindeer.PANEL_LIGHT);
			grid.addComponent(data, 2, 0);
			grid.setComponentAlignment(data, Alignment.TOP_RIGHT);

			Label descrizione = new Label("<br/><br/>"
					+ elenco.get(i).getDescription() + "<br/><br/>",
					Label.CONTENT_XHTML);
			descrizione.addStyleName(Reindeer.PANEL_LIGHT);
			grid.addComponent(descrizione, 0, 1, 1, 1);
			grid.setComponentAlignment(descrizione, Alignment.MIDDLE_CENTER);

			CalendarWindowInsert newEvent = new CalendarWindowInsert(
					mainWindow, iduser, username, elenco.get(i));
			grid.addComponent(newEvent, 0, 2);
			grid.setComponentAlignment(newEvent, Alignment.BOTTOM_LEFT);

			MessageWindow subWindow = new MessageWindow("Invia Messaggio",
					mainWindow, elenco.get(i).getUsername(), elenco.get(i)
							.getIdadvice(), username);
			grid.addComponent(subWindow, 1, 2);
			grid.setComponentAlignment(subWindow, Alignment.BOTTOM_LEFT);

			Link link = new Link(elenco.get(i).getUsername(),
					new ExternalResource("http://localhost:8080/it/web/"
							+ elenco.get(i).getUsername() + "/home"));
			grid.addComponent(link, 2, 2);
			grid.setComponentAlignment(link, Alignment.MIDDLE_CENTER);

			layout.addComponent(grid);
			layout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
			setCompositionRoot(layout);

		}
	}

	
	//richiamato da pagina pubblica per prendere annunci sinfgolo utente
	public AdviceComponent(Window mainWindow, int id, String user,Boolean flag) {
		// addComponent(layout);
		iduser = id;
		username = user;
		// Recupero annunci
		try {
			elenco = bd.getUser(user);
			_log.info("Ho l' elenco" + elenco);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}

		for (int i = 0; i < elenco.size(); i++) {
			GridLayout grid = new GridLayout(3, 3);
			grid.addStyleName(Reindeer.LAYOUT_BLUE);
			// grid.setSpacing(true);
			grid.setMargin(true);
			grid.setSizeFull();
			grid.setRowExpandRatio(0, 0);
			grid.setRowExpandRatio(1, 0);
			grid.setRowExpandRatio(2, 1);

			grid.setColumnExpandRatio(1, 1);
			grid.setColumnExpandRatio(2, 1);
			grid.setColumnExpandRatio(3, 1);

			// The style allows us to visualize the cell borders in this
			// example.
			grid.addStyleName("gridexample");

			Label titolo = new Label("<h1>" + elenco.get(i).getTitle()
					+ "</h1>" + " " + elenco.get(i).getTipology(),
					Label.CONTENT_XHTML);
			titolo.addStyleName(Reindeer.PANEL_LIGHT);
			grid.addComponent(titolo, 0, 0);
			grid.setComponentAlignment(titolo, Alignment.TOP_LEFT);

			Label data = new Label("<h5>" + elenco.get(i).getDate()
					+ "</h5><br />" + elenco.get(i).getTipo(),
					Label.CONTENT_XHTML);
			data.addStyleName(Reindeer.PANEL_LIGHT);
			grid.addComponent(data, 2, 0);
			grid.setComponentAlignment(data, Alignment.TOP_RIGHT);

			Label descrizione = new Label("<br/><br/>"
					+ elenco.get(i).getDescription() + "<br/><br/>",
					Label.CONTENT_XHTML);
			descrizione.addStyleName(Reindeer.PANEL_LIGHT);
			grid.addComponent(descrizione, 0, 1, 1, 1);
			grid.setComponentAlignment(descrizione, Alignment.MIDDLE_CENTER);

			CalendarWindowInsert newEvent = new CalendarWindowInsert(
					mainWindow, iduser, username, elenco.get(i));
			grid.addComponent(newEvent, 0, 2);
			grid.setComponentAlignment(newEvent, Alignment.BOTTOM_LEFT);

			MessageWindow subWindow = new MessageWindow("Invia Messaggio",
					mainWindow, elenco.get(i).getUsername(), elenco.get(i)
							.getIdadvice(), username);
			grid.addComponent(subWindow, 1, 2);
			grid.setComponentAlignment(subWindow, Alignment.BOTTOM_LEFT);

			Link link = new Link(elenco.get(i).getUsername(),
					new ExternalResource("http://localhost:8080/it/web/"
							+ elenco.get(i).getUsername() + "/home"));
			grid.addComponent(link, 2, 2);
			grid.setComponentAlignment(link, Alignment.MIDDLE_CENTER);

			layout.addComponent(grid);
			layout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
			setCompositionRoot(layout);

		}
	}
	@Override
	public void windowClose(CloseEvent e) {
		// TODO Auto-generated method stub

	}
}
