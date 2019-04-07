package ita.social.component;

import ita.social.delegate.BusinessDelegate;
import ita.social.model.Advice;
import ita.social.utils.AdviceException;

import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

public class GridFeedback extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(GridAdvice.class);
	protected BusinessDelegate bd = new BusinessDelegate();
	private List<Advice> elenco;
	private VerticalLayout layout = new VerticalLayout();
	private String username;
	private String tipo = "";

	public GridFeedback() {

	}

	public GridFeedback(Advice advice, String userName, String who) {
		username = userName;
		tipo = who;
		// Recupero annunci
		try {
			elenco = bd.getOne(advice);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}

		for (int i = 0; i < elenco.size(); i++) {
			
			if(tipo.equals("mine")) {
				
				Label utente = new Label("Lascia Feedback per il <h5> tuo annuncio </h5>", Label.CONTENT_XHTML);
				utente.addStyleName(Reindeer.PANEL_LIGHT);
				layout.addComponent(utente);

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

				grid.addStyleName("EventGrid");

				Label titolo = new Label("<h3> " + elenco.get(i).getTitle()
						+ "</h3>", Label.CONTENT_XHTML);
				titolo.addStyleName(Reindeer.PANEL_LIGHT);
				grid.addComponent(titolo, 0, 0);
				grid.setComponentAlignment(titolo, Alignment.TOP_LEFT);

				Label data = new Label("<h5>" + elenco.get(i).getTipo() + "</h5>"
						+ elenco.get(i).getTipology(), Label.CONTENT_XHTML);
				data.addStyleName(Reindeer.PANEL_LIGHT);
				grid.addComponent(data, 2, 0);
				grid.setComponentAlignment(data, Alignment.TOP_RIGHT);

				Label descrizione = new Label(elenco.get(i).getDescription());
				descrizione.addStyleName(Reindeer.PANEL_LIGHT);
				grid.addComponent(descrizione, 0, 2 , 1 , 2);
				grid.setComponentAlignment(descrizione, Alignment.TOP_RIGHT);

				layout.addComponent(grid);
				layout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
				addComponent(layout);
			
			}else if(tipo.equals("other")){
				
				Label utente = new Label("Lascia Feedback per l' annuncio di <h5>" + elenco.get(i).getUsername() + "</h5>", Label.CONTENT_XHTML);
				utente.addStyleName(Reindeer.PANEL_LIGHT);
				layout.addComponent(utente);

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

				grid.addStyleName("EventGrid");

				Label titolo = new Label("<h3> " + elenco.get(i).getTitle()
						+ "</h3>", Label.CONTENT_XHTML);
				titolo.addStyleName(Reindeer.PANEL_LIGHT);
				grid.addComponent(titolo, 0, 0);
				grid.setComponentAlignment(titolo, Alignment.TOP_LEFT);

				Label data = new Label("<h5>" + elenco.get(i).getTipo() + "</h5>"
						+ elenco.get(i).getTipology(), Label.CONTENT_XHTML);
				data.addStyleName(Reindeer.PANEL_LIGHT);
				grid.addComponent(data, 2, 0);
				grid.setComponentAlignment(data, Alignment.TOP_RIGHT);

				Label descrizione = new Label(elenco.get(i).getDescription());
				descrizione.addStyleName(Reindeer.PANEL_LIGHT);
				grid.addComponent(descrizione, 0, 2 , 1 , 2);
				grid.setComponentAlignment(descrizione, Alignment.TOP_RIGHT);

				layout.addComponent(grid);
				layout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
				addComponent(layout);
				
			}else if(tipo.equals("feedback")){
				
				Label utente = new Label("<h5>Feedback "
						+ "</h5> per annuncio ", Label.CONTENT_XHTML);
				utente.addStyleName(Reindeer.PANEL_LIGHT);
				layout.addComponent(utente);

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

				grid.addStyleName("EventGrid");

				Label titolo = new Label("<h3> " + elenco.get(i).getTitle()
						+ "</h3>", Label.CONTENT_XHTML);
				titolo.addStyleName(Reindeer.PANEL_LIGHT);
				grid.addComponent(titolo, 0, 0);
				grid.setComponentAlignment(titolo, Alignment.TOP_LEFT);

				Label data = new Label("<h5>" + elenco.get(i).getTipo() + "</h5>"
						+ elenco.get(i).getTipology(), Label.CONTENT_XHTML);
				data.addStyleName(Reindeer.PANEL_LIGHT);
				grid.addComponent(data, 2, 0);
				grid.setComponentAlignment(data, Alignment.TOP_RIGHT);

				Label descrizione = new Label(elenco.get(i).getDescription());
				descrizione.addStyleName(Reindeer.PANEL_LIGHT);
				grid.addComponent(descrizione, 0, 2 , 1 , 2);
				grid.setComponentAlignment(descrizione, Alignment.TOP_RIGHT);

				Link link = new Link(elenco.get(i).getUsername(),
						new ExternalResource("http://localhost:8080/it/web/"
								+ elenco.get(i).getUsername() + "/home"));
				grid.addComponent(link, 2, 2);
				grid.setComponentAlignment(link, Alignment.MIDDLE_CENTER);

				layout.addComponent(grid);
				layout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
				addComponent(layout);
			}
		}
	}

}