package ita.social.component;

import ita.social.delegate.BusinessDelegate;
import ita.social.model.Advice;
import ita.social.model.CalendarTestEvent;
import ita.social.utils.AdviceException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

public class GridFeedbackPanel extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(GridAdvice.class);
	protected BusinessDelegate bd = new BusinessDelegate();
	private List<CalendarTestEvent> elenco;
	private List<Advice> lista;
	private VerticalLayout layout = new VerticalLayout();
	private int id;
	private String userName;
	private String usernameVisualized;
	private String tipo = "";
	private Window mywindow;
	private Window mainWindow;

	public GridFeedbackPanel() {

	}

	public GridFeedbackPanel(Window window, int iduser, String username,
			String userNameVisualized, String who) {
		mainWindow = window;
		id = iduser;
		userName = username;
		usernameVisualized = userNameVisualized;
		tipo = who;
		Date today = getToday();

		if (tipo.equals("mine")) {

			// recupero feedback da utenti per i miei annunci
			try {
				elenco = bd.getUserFeed(usernameVisualized);
			} catch (AdviceException e) {
				_log.error(e);
				e.printStackTrace();
			}

			for (int i = 0; i < elenco.size(); i++) {
				// recupero annuncio da idannuncio dell' obj CalendarTestEvent
				try {
					lista = bd.getOneOnly(elenco.get(i).getIdadvice());
				} catch (AdviceException e) {
					_log.error(e);
					e.printStackTrace();
				}
				final Advice ad = lista.get(0);
				if (today.getTime() > elenco.get(i).getStart().getTime()) {
					GridLayout grid = new GridLayout(3, 2);
					grid.addStyleName(Reindeer.LAYOUT_BLUE);
					// grid.setSpacing(true);
					grid.setMargin(true);

					grid.setSizeFull();
					grid.addStyleName("FeedbackGrid");
					grid.addListener(new LayoutClickListener() {
						public void layoutClick(LayoutClickEvent event) {
							Window mywindow = new Window();
							mywindow.center();
							mywindow.setModal(true);
							mywindow.setHeight("350px");
							mywindow.setWidth("600px");
							mainWindow.addWindow(mywindow);
							_log.info("APRO " + lista.get(0));
							mywindow.addComponent(new AdviceComponent(
									mainWindow, id, userName, ad));
						}
					});
					Label titolo = new Label("<h5> "
							+ elenco.get(i).getDescription() + "</h5>",
							Label.CONTENT_XHTML);
					titolo.addStyleName(Reindeer.PANEL_LIGHT);
					grid.addComponent(titolo, 0, 0);
					grid.setComponentAlignment(titolo, Alignment.TOP_LEFT);

					Button open = new Button("Vai", new Button.ClickListener() {
						// inline click-listener
						public void buttonClick(ClickEvent event) {
							Window mywindow = new Window();
							mywindow.center();
							mywindow.setModal(true);
							mywindow.setHeight("350px");
							mywindow.setWidth("600px");
							mainWindow.addWindow(mywindow);
							_log.info("APRO " + lista.get(0));
							mywindow.addComponent(new AdviceComponent(
									mainWindow, id, userName, ad));
							/* Listen for close events for the window. */
							// mywindow.addListener(this);
						}
					});
					grid.addComponent(open, 0, 1);
					grid.setComponentAlignment(open, Alignment.TOP_LEFT);

					Label voto = new Label("<h5>Voto:  "
							+ elenco.get(i).getVote() + "</h5>",
							Label.CONTENT_XHTML);
					voto.addStyleName(Reindeer.PANEL_LIGHT);
					grid.addComponent(voto, 1, 0);
					grid.setComponentAlignment(voto, Alignment.TOP_RIGHT);

					Label commento = new Label(elenco.get(i).getNote());
					commento.addStyleName(Reindeer.PANEL_LIGHT);
					grid.addComponent(commento, 1, 1);
					grid.setComponentAlignment(commento, Alignment.TOP_RIGHT);

					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
					Label data = new Label("<h5>"
							+ sdf.format(elenco.get(i).getStart().getTime())
							+ "</h5>", Label.CONTENT_XHTML);
					data.addStyleName(Reindeer.PANEL_LIGHT);
					grid.addComponent(data, 2, 0);
					grid.setComponentAlignment(data, Alignment.TOP_RIGHT);

					Label utente = new Label("<h5>Da "
							+ elenco.get(i).getFlag() + "</h5>",
							Label.CONTENT_XHTML);
					utente.addStyleName(Reindeer.PANEL_LIGHT);
					grid.addComponent(utente, 2, 1);
					grid.setComponentAlignment(utente, Alignment.TOP_RIGHT);

					layout.addComponent(grid);
					layout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
					addComponent(layout);

				}
			}

		} else if (tipo.equals("other")) {
			// recupero feedback lasciati da me
			try {
				elenco = bd.getMyFeed(usernameVisualized);
			} catch (AdviceException e) {
				_log.error(e);
				e.printStackTrace();
			}

			for (int i = 0; i < elenco.size(); i++) {
				// recupero annuncio da idannuncio dell' obj CalendarTestEvent
				// in questione
				try {
					lista = bd.getOneOnly(elenco.get(i).getIdadvice());
				} catch (AdviceException e) {
					_log.error(e);
					e.printStackTrace();
				}
				final Advice ad = lista.get(0);
				if (today.getTime() > elenco.get(i).getStart().getTime()) {
					GridLayout grid = new GridLayout(3, 2);
					grid.addStyleName(Reindeer.LAYOUT_BLUE);
					// grid.setSpacing(true);
					grid.setMargin(true);
					grid.setSizeFull();
					grid.addStyleName("FeedbackGrid");
					grid.addListener(new LayoutClickListener() {
						public void layoutClick(LayoutClickEvent event) {
							Window mywindow = new Window();
							mywindow.center();
							mywindow.setModal(true);
							mywindow.setHeight("350px");
							mywindow.setWidth("600px");
							mainWindow.addWindow(mywindow);
							_log.info("APRO " + lista.get(0));
							mywindow.addComponent(new AdviceComponent(
									mainWindow, id, userName, ad));
						}
					});
					Label titolo = new Label("<h5> "
							+ elenco.get(i).getDescription() + "</h5>",
							Label.CONTENT_XHTML);
					titolo.addStyleName(Reindeer.PANEL_LIGHT);
					grid.addComponent(titolo, 0, 0);
					grid.setComponentAlignment(titolo, Alignment.TOP_LEFT);

					Button open = new Button("Vai", new Button.ClickListener() {
						// inline click-listener
						public void buttonClick(ClickEvent event) {
							Window mywindow = new Window();
							mywindow.center();
							mywindow.setModal(true);
							mywindow.setHeight("350px");
							mywindow.setWidth("600px");
							mainWindow.addWindow(mywindow);
							_log.info("APRO " + lista.get(0));
							mywindow.addComponent(new AdviceComponent(
									mainWindow, id, userName, ad));
							/* Listen for close events for the window. */
							// mywindow.addListener(this);
						}
					});
					grid.addComponent(open, 0, 1);
					grid.setComponentAlignment(open, Alignment.TOP_LEFT);

					Label voto = new Label("<h5>Voto:  "
							+ elenco.get(i).getVote() + "</h5>",
							Label.CONTENT_XHTML);
					voto.addStyleName(Reindeer.PANEL_LIGHT);
					grid.addComponent(voto, 1, 0);
					grid.setComponentAlignment(voto, Alignment.TOP_RIGHT);

					Label commento = new Label(elenco.get(i).getNote());
					commento.addStyleName(Reindeer.PANEL_LIGHT);
					grid.addComponent(commento, 1, 1);
					grid.setComponentAlignment(commento, Alignment.TOP_RIGHT);

					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
					Label data = new Label("<h5>"
							+ sdf.format(elenco.get(i).getStart().getTime())
							+ "</h5>", Label.CONTENT_XHTML);
					data.addStyleName(Reindeer.PANEL_LIGHT);
					grid.addComponent(data, 2, 0);
					grid.setComponentAlignment(data, Alignment.TOP_RIGHT);

					Label utente = new Label("<h5>Da "
							+ elenco.get(i).getFlag() + "</h5>",
							Label.CONTENT_XHTML);
					utente.addStyleName(Reindeer.PANEL_LIGHT);
					grid.addComponent(utente, 2, 1);
					grid.setComponentAlignment(utente, Alignment.TOP_RIGHT);

					layout.addComponent(grid);
					layout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
					addComponent(layout);
				}
			}

		} else if (tipo.equals("all")) {

			// recupero tutti feedback che mi riguardano
			try {
				elenco = bd.getFeed(usernameVisualized);
			} catch (AdviceException e) {
				_log.error(e);
				e.printStackTrace();
			}
			for (int i = 0; i < elenco.size(); i++) {
				// recupero annuncio da idannuncio dell' obj CalendarTestEvent
				// in questione
				try {
					lista = bd.getOneOnly(elenco.get(i).getIdadvice());
				} catch (AdviceException e) {
					_log.error(e);
					e.printStackTrace();
				}
				final Advice ad = lista.get(0);
				if (today.getTime() > elenco.get(i).getStart().getTime()) {
					GridLayout grid = new GridLayout(3, 2);
					grid.addStyleName(Reindeer.LAYOUT_BLUE);
					// grid.setSpacing(true);
					grid.setMargin(true);
					grid.setSizeFull();
					grid.addListener(new LayoutClickListener() {
						public void layoutClick(LayoutClickEvent event) {
							Window mywindow = new Window();
							mywindow.center();
							mywindow.setModal(true);
							mywindow.setHeight("350px");
							mywindow.setWidth("600px");
							mainWindow.addWindow(mywindow);
							_log.info("APRO " + lista.get(0));
							mywindow.addComponent(new AdviceComponent(
									mainWindow, id, userName, ad));
						}
					});
					grid.addStyleName("FeedbackGrid");

					Label titolo = new Label("<h5> "
							+ elenco.get(i).getDescription() + "</h5>",
							Label.CONTENT_XHTML);
					titolo.addStyleName(Reindeer.PANEL_LIGHT);
					grid.addComponent(titolo, 0, 0);
					grid.setComponentAlignment(titolo, Alignment.TOP_LEFT);

					Button open = new Button("Vai", new Button.ClickListener() {
						// inline click-listener
						public void buttonClick(ClickEvent event) {
							Window mywindow = new Window();
							mywindow.center();
							mywindow.setModal(true);
							mywindow.setHeight("350px");
							mywindow.setWidth("600px");
							mainWindow.addWindow(mywindow);
							_log.info("APRO " + lista.get(0));
							mywindow.addComponent(new AdviceComponent(
									mainWindow, id, userName, ad));
							/* Listen for close events for the window. */
							// mywindow.addListener(this);
						}
					});
					grid.addComponent(open, 0, 1);
					grid.setComponentAlignment(open, Alignment.TOP_LEFT);

					Label voto = new Label("<h5>Voto:  "
							+ elenco.get(i).getVote() + "</h5>",
							Label.CONTENT_XHTML);
					voto.addStyleName(Reindeer.PANEL_LIGHT);
					grid.addComponent(voto, 1, 0);
					grid.setComponentAlignment(voto, Alignment.TOP_RIGHT);

					Label commento = new Label(elenco.get(i).getNote());
					commento.addStyleName(Reindeer.PANEL_LIGHT);
					grid.addComponent(commento, 1, 1);
					grid.setComponentAlignment(commento, Alignment.TOP_RIGHT);

					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
					Label data = new Label("<h5>"
							+ sdf.format(elenco.get(i).getStart().getTime())
							+ "</h5>", Label.CONTENT_XHTML);
					data.addStyleName(Reindeer.PANEL_LIGHT);
					grid.addComponent(data, 2, 0);
					grid.setComponentAlignment(data, Alignment.TOP_RIGHT);

					Label utente = new Label("<h5>Da "
							+ elenco.get(i).getFlag() + "</h5>",
							Label.CONTENT_XHTML);
					utente.addStyleName(Reindeer.PANEL_LIGHT);
					grid.addComponent(utente, 2, 1);
					grid.setComponentAlignment(utente, Alignment.TOP_RIGHT);

					layout.addComponent(grid);
					layout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
					addComponent(layout);
				}
			}
		}
	}

	private Date getToday() {
		return new Date();
	}

}