package ita.social.app;

import ita.social.delegate.BusinessDelegate;
import ita.social.model.Advice;
import ita.social.model.Message;
import ita.social.utils.AdviceException;

import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class GetMyadvice extends Application implements PortletRequestListener {
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(GetAdvice.class);
	/** Business delegate per le chiamate al database */
	protected BusinessDelegate bd = new BusinessDelegate();
	List<Advice> elenco;
	List<Message> messaggi;
	int numMessage;

	public void init() {
        setTheme("runo");
		Window mainWindow = new Window("Myvaadinproject Application");
		mainWindow.setSizeFull();
		mainWindow.addComponent(new GridAdvice(mainWindow));
		setMainWindow(mainWindow);
		
	}

	public class GridAdvice extends VerticalLayout {
		private static final long serialVersionUID = 1L;

		public GridAdvice(Window mainWindow) {

			// Recupero elenco annunci
			try {
				_log.debug("richiamo busines-------------()");
				elenco = bd.get((int) ((User) getUser()).getUserId());
				_log.debug("Ho l' elenco" + elenco);
			} catch (AdviceException e) {
				_log.error(e);
				e.printStackTrace();
			}

			for (int i = 0; i < elenco.size(); i++) {

				// recupero messaggi per ogni annuncio
				try {
					_log.debug("recupero numero messaggi e messaggi");
					numMessage = bd.countMessageForAdvice(elenco.get(i)
							.getIdadvice());
					messaggi =  bd.getIn((String) ((User) getUser())
							.getScreenName());
				} catch (AdviceException e) {
					_log.error(e);
					e.printStackTrace();
				}

				GridLayout grid = new GridLayout(3, 3);
				grid.setSpacing(true);
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
				grid.addComponent(titolo, 0, 0);
				grid.setComponentAlignment(titolo, Alignment.TOP_LEFT);

				Label data = new Label("<h5>" + elenco.get(i).getDate()
						+ "</h5><br />" + elenco.get(i).getTipo(),
						Label.CONTENT_XHTML);
				grid.addComponent(data, 2, 0);
				grid.setComponentAlignment(data, Alignment.TOP_RIGHT);

				Label descrizione = new Label(elenco.get(i).getDescription());
				grid.addComponent(descrizione, 0, 1);
				grid.setComponentAlignment(descrizione, Alignment.MIDDLE_CENTER);

				Link link = new Link("(" + numMessage + ") Messaggi per questo annuncio",
						new ExternalResource("http://localhost:8080/it/web/"
								+ elenco.get(i).getUsername() + "/home"));
				grid.addComponent(link, 0, 2);
				grid.setComponentAlignment(link, Alignment.MIDDLE_CENTER);

				// Add the layout to the containing layout.
				addComponent(grid);

				// Align the grid itself within its container layout.
				setComponentAlignment(grid, Alignment.MIDDLE_CENTER);

				addComponent(new Label(
						"________________________________________________________________________________"));

			}
		}
	}

	// per ottenere dati utente loggato
	@Override
	public void onRequestStart(PortletRequest request, PortletResponse response) {
		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		themeDisplay.getCompanyId();
		_log.debug("getCompany id: " + themeDisplay.getCompanyId());
		_log.debug("getFullname id: " + themeDisplay.getUser().getScreenName());
		if (getUser() == null) {
			try {
				User user = PortalUtil.getUser(request);
				setUser(user);
			} catch (com.liferay.portal.kernel.exception.PortalException e) {
				e.printStackTrace();
			} catch (com.liferay.portal.kernel.exception.SystemException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onRequestEnd(PortletRequest request, PortletResponse response) {
		// TODO Auto-generated method stub

	}

}
