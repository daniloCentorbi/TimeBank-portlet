package ita.social.component;

import ita.social.delegate.BusinessDelegate;
import ita.social.model.Advice;
import ita.social.model.CalendarTestEvent;
import ita.social.model.Message;
import ita.social.utils.AdviceException;

import java.util.Date;
import java.util.List;


import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class GridAdvice extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(GridAdvice.class);
	protected BusinessDelegate bd = new BusinessDelegate();
	private List<Advice> elenco;
	private int numMessage;
	private int numAdvice;
	private List<Message> messaggi;
	private List<CalendarTestEvent> eventi;
	private VerticalLayout layout = new VerticalLayout();
	private Window mainWindow;
	private String username;

	//inizialmente no parametri
	public GridAdvice() {
		Label label = new Label("<h1>Seleziona un annuncio a Sinistra per visualizzarne i Dettagli</h1>",Label.CONTENT_XHTML);
		label.addStyleName(Reindeer.PANEL_LIGHT);	
		layout.addComponent(label);
        addComponent(layout);
	}
	
	public GridAdvice(Advice advice) {
		// Recupero annunci
		try {
			_log.info("QUERY 1");
			elenco = bd.getOne(advice);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}

		for (int i = 0; i < elenco.size(); i++) {
		
			// recupero annunci per ogni annuncio
			try {
				_log.info("QUERY 2");
				numAdvice = bd.countForAdvice(elenco.get(i));
				eventi = bd.getEventFromAdvice(elenco.get(i));
			} catch (AdviceException e) {
				_log.error(e);
				e.printStackTrace();
			}
			
			GridLayout grid = new GridLayout(3, 3);
			grid.setSpacing(true);
			grid.setMargin(true);
			grid.setSizeFull();
			grid.setRowExpandRatio(0, 0);
			grid.setRowExpandRatio(1, 0);
			grid.setRowExpandRatio(2, 1);
			grid.setColumnExpandRatio(1, 1);
			grid.setColumnExpandRatio(2, 1);
			grid.setColumnExpandRatio(3, 1);


			grid.addStyleName("gridexample");

			Label titolo = new Label(elenco.get(i).getTipology()
					+ "<h1>" + elenco.get(i).getTitle(),
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

			Label descrizione = new Label(elenco.get(i).getDescription());
			descrizione.addStyleName(Reindeer.PANEL_LIGHT);
			grid.addComponent(descrizione, 0, 1 , 1 , 1);
			grid.setComponentAlignment(descrizione, Alignment.MIDDLE_CENTER);

			layout.addComponent(grid);
			layout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
			addComponent(layout);
			
		}
	}


}
