package ita.social.app;

import ita.social.component.AdviceComponent;
import ita.social.delegate.BusinessDelegate;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import com.vaadin.ui.Window;

public class GetAdvice extends Application implements PortletRequestListener {
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(GetAdvice.class);
	/** Business delegate per le chiamate al database */
	protected BusinessDelegate bd = new BusinessDelegate();

	public void init() {
		setTheme("calendartest");
		//this.getMainWindow().setStyleName(""); 
		Window mainWindow = new Window("");
		mainWindow.setSizeFull();
		mainWindow.addComponent(new AdviceComponent(mainWindow,(int) ((User) getUser()).getUserId(),(String) ((User) getUser()).getScreenName()));
		setMainWindow(mainWindow);
	}

	// ottenere dati utente loggato
	@Override
	public void onRequestStart(PortletRequest request, PortletResponse response) {
		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		themeDisplay.getCompanyId();
		_log.debug("getCompany id: " + themeDisplay.getCompanyId());
		_log.debug("getFullname id: " + themeDisplay.getUser().getFullName());
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
