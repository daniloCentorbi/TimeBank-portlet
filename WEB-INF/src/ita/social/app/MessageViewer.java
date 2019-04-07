package ita.social.app;

import ita.social.component.CalendarWindow;
import ita.social.component.MessageBoard;
import ita.social.delegate.BusinessDelegate;
import ita.social.model.Message;
import ita.social.utils.AdviceException;

import java.io.File;
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
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class MessageViewer extends Application implements
		PortletRequestListener, TabSheet.SelectedTabChangeListener {

	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(MessageViewer.class);
	/** Business delegate per le chiamate al database */
	protected BusinessDelegate bd = new BusinessDelegate();

	File f = new File(System.getProperty("user.dir") + "/images");
	private static final ThemeResource icon1 = new ThemeResource(
			"../messageIn.jpg");
	private static final ThemeResource icon2 = new ThemeResource(
			"../messageAll.jpg");
	private static final ThemeResource icon3 = new ThemeResource(
			"../messageInfo.jpeg");

	Window mainWindow = new Window();
	Panel panel = new Panel("Tasks");
	private TabSheet a;
	int numInmessage;
	int numOutmessage;
	List<Message> elenco;

	@Override
	public void init() {
		setTheme("chamaleon");
		mainWindow.setName("MessageWindow");
		VerticalLayout layout = (VerticalLayout) panel.getContent();
		layout.setMargin(false);
		layout.setSpacing(false);
		panel.setSizeFull();
		mainWindow.setSizeFull();
		setMainWindow(mainWindow);

		// main.getApplication().addWindow(adderWindow);----------------------------x
		// multiple??????'
		// _log.info("MESSAGEVIEWER>>>>>>>>>>>>>>getURL:  " +
		// mainWindow.getURL());
		_log.info("MESSAGEVIEWER>>>>>>>>>>>>>>getWindow():  "
				+ mainWindow.getWindow());
		_log.info("MESSAGEVIEWER>>>>>>>>>>>>>>getWindow():  "
				+ mainWindow.getName());

		// recupero INBOX utente
		try {
			_log.info("recupero numero messaggi in");
			numInmessage = bd.countInmessage((String) ((User) getUser())
					.getScreenName());
			elenco = bd.getIn((String) ((User) getUser()).getScreenName());
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}

		final VerticalLayout inbox = new VerticalLayout();
		for (int i = 0; i < elenco.size(); i++) {
			final String body = elenco.get(i).getBody();
			inbox.addComponent(new Label("messaggio: "
					+ elenco.get(i).getBody()));
			// Add message to the VerticalLayout
			Button open = new Button(elenco.get(i).getSend(),
					new Button.ClickListener() {
						private static final long serialVersionUID = 1L;

						public void buttonClick(ClickEvent event) {
							try {
								_log.info("bottone clicckato");
								inbox.removeAllComponents();
								inbox.addComponent(new Label(body));

							} catch (Exception e) {
								Panel panel = new Panel("Tasks");
								// Ingnored, we'll let the Form handle the
								// errors
							}
						}
					});
			inbox.addComponent(open);
		}

		MessageBoard outbox = new MessageBoard(mainWindow, elenco);
		Label l3 = new Label("NOTE");

		a = new TabSheet();
		a.setHeight("360px");
		a.setWidth("400px");
		a.addTab(l3, "Non Letti", icon3);
		a.addTab(inbox, "Ricevuti (" + numInmessage + ")", icon1);
		a.addTab(outbox, "Inviati", icon2);
		a.addListener((SelectedTabChangeListener) this);

		panel.addComponent(a);
		mainWindow.addComponent(a);
	}

	public void selectedTabChange(SelectedTabChangeEvent event) {
		TabSheet tabsheet = event.getTabSheet();
		Tab tab = tabsheet.getTab(tabsheet.getSelectedTab());
		if (tab != null) {
			mainWindow.showNotification(tab.getCaption());
		}
	}

	// per ottenere dati utente loggato
	@Override
	public void onRequestStart(PortletRequest request, PortletResponse response) {
		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
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