package ita.social.app;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import ita.social.component.FeedbackPanel;

import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import com.vaadin.ui.Window;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;

public class FeedbackViewerVisited extends Application implements PortletRequestListener{
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(FeedbackViewerVisited.class);
	String currentURL;
	User user2;
	String userVisualized;
	String io;
	
	public void init() {
        Window window = new Window();
        setMainWindow(window);
        window.addComponent(new FeedbackPanel(window,(int) ((User) getUser()).getUserId(),(String) ((User) getUser()).getScreenName(),userVisualized));
    }   
	
	@Override
	public void onRequestStart(PortletRequest request, PortletResponse response) {
		if (getUser() == null) {
			try {
				ThemeDisplay themeDisplay = 
					     (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
				long scopeGroupId =   themeDisplay.getScopeGroupId();
				Group group = GroupLocalServiceUtil.getGroup(scopeGroupId);
				UserLocalServiceUtil.getUserById(group.getClassPK());
				User user2 = UserLocalServiceUtil.getUserById(group.getClassPK());
				userVisualized = user2.getScreenName();
			    
				User user = PortalUtil.getUser(request);
				currentURL = PortalUtil.getCurrentURL(request);
				_log.info("USERRRR" + user2.getScreenName());
				
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