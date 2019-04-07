package ita.social.component;

import java.util.List;

import ita.social.delegate.BusinessDelegate;
import ita.social.model.CalendarTestEvent;
import ita.social.utils.AdviceException;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class FeedbackPanel extends HorizontalLayout implements
		Accordion.SelectedTabChangeListener {

	private static final ThemeResource icon1 = new ThemeResource(
			"../sampler/icons/action_save.gif");
	private static final ThemeResource icon2 = new ThemeResource(
			"../sampler/icons/comment_yellow.gif");
	private static final ThemeResource icon3 = new ThemeResource(
			"../sampler/icons/icon_info.gif");
	private static Log _log = LogFactoryUtil.getLog(CalendarWindow.class);
	protected BusinessDelegate bd = new BusinessDelegate();
	private List<CalendarTestEvent> elenco;
	private String userName;
	private Accordion a;

	public FeedbackPanel(Window window,int id,String username,String usernameVisualized) {
		userName = username;
		setSpacing(true);

		GridFeedbackPanel l1 = new GridFeedbackPanel(window,id,username,usernameVisualized,"mine");
		GridFeedbackPanel l2 = new GridFeedbackPanel(window,id,username,usernameVisualized,"other");
		GridFeedbackPanel l3 = new GridFeedbackPanel(window,id,username,usernameVisualized,"all");

		a = new Accordion();
		a.setHeight("300px");
		a.setWidth("700px");
		a.addTab(l1, "Feedback Ricevuti", icon1);
		a.addTab(l2, "Feedback Lasciati", icon2);
		a.addTab(l3, "Mostra Tutti", icon3);
		a.addListener(this);

		addComponent(a);
	}

	public void selectedTabChange(SelectedTabChangeEvent event) {
		TabSheet tabsheet = event.getTabSheet();
		Tab tab = tabsheet.getTab(tabsheet.getSelectedTab());
		if (tab != null) {
			getWindow().showNotification(tab.getCaption());
		}
	}
}
