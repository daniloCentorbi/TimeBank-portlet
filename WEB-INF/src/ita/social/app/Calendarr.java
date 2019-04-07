package ita.social.app;

import ita.social.component.CalendarHistory;
import ita.social.component.CalendarWindow;
import ita.social.component.EventViewer;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

public class Calendarr extends Application implements PortletRequestListener {
	private static final long serialVersionUID = 1L;
	private Window window;
	private EventViewer a;
	private CalendarWindow b;
	private CalendarHistory c;
	private Calendarr app;
	Window mywindow;

	public void init() {
		window = new Window("");
		setMainWindow(window);
		setTheme("calendartest");
		app = this;

		a = new EventViewer(app,window, (String) ((User) getUser()).getScreenName());
		b = new CalendarWindow(app,window, "Calendario Completo",
				(String) ((User) getUser()).getScreenName(), "all");
		c = new CalendarHistory(app,window, "Storico Eventi",
				(String) ((User) getUser()).getScreenName());
		window.addComponent(b);
		window.addComponent(a);
		window.addComponent(c);
	}

	public void update() {
		window.removeComponent(a);
		window.removeComponent(b);
		window.removeComponent(c);
		a = new EventViewer(app,window, (String) ((User) getUser()).getScreenName());
		b = new CalendarWindow(app,window, "Calendario Completo",
				(String) ((User) getUser()).getScreenName(), "all");
		window.addComponent(b);	
		window.addComponent(a);
		window.addComponent(c);	

	}

	@Override
	public void onRequestStart(PortletRequest request, PortletResponse response) {
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

	}

}
