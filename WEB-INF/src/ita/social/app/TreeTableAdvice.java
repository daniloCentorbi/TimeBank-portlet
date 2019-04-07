package ita.social.app;

import ita.social.component.AdviceFormLayout;
import ita.social.component.AdviceWindow;
import ita.social.component.GridAdvice;
import ita.social.component.HistoryAdvice;
import ita.social.delegate.BusinessDelegate;
import ita.social.model.Advice;
import ita.social.utils.AdviceException;
import ita.social.utils.HierarchicalBeanItemContainer;

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
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

public class TreeTableAdvice extends Application implements
		PortletRequestListener, ClickListener, ItemClickListener , Window.CloseListener{
	private static final long serialVersionUID = 97529549237L;
	private static Log _log = LogFactoryUtil.getLog(TreeTableAdvice.class);
	private BusinessDelegate bd = new BusinessDelegate();
	private Window main;
	private VerticalLayout leftLayout;
	private VerticalSplitPanel rigthLayout;
	private HorizontalSplitPanel horizontalSplit;
    private HorizontalLayout bottomLeftCorner = new HorizontalLayout();
    //private HorizontalLayout bottomRigthCorner = new HorizontalLayout();
    private Button contactAddButton;
	private Button contactRemovalButton;
	private Button contactEditButton;
	private Tree tree;
	private TreeTableAdvice app;
	Window mywindow;
	
	public void init() {
		setTheme("reindeer");
		app = this;
		main = new Window();
		main.setSizeFull();
		initTree();
		buildMainLayout();
		initContactAddRemoveButtons();
	}

	void buildMainLayout() {
		horizontalSplit = new HorizontalSplitPanel();
		horizontalSplit.setSizeFull();
		main = new Window();
		main.setSizeFull();
		main.addComponent(horizontalSplit);
		setMainWindow(main);

		leftLayout = new VerticalLayout();
		leftLayout.setWidth("100%");
		leftLayout.setHeight("310px");
		leftLayout.addComponent(tree);	
		leftLayout.setExpandRatio(tree, 1);
		
		rigthLayout = new VerticalSplitPanel();
		rigthLayout.setWidth("100%");
		rigthLayout.setHeight("310px");
		rigthLayout.setSplitPosition(220, Sizeable.UNITS_PIXELS);
		rigthLayout.addStyleName(Reindeer.SPLITPANEL_SMALL);
		rigthLayout.setFirstComponent(new GridAdvice());
		rigthLayout.setSecondComponent(new HistoryAdvice());		
		
		horizontalSplit.setSplitPosition(200, Sizeable.UNITS_PIXELS);
		horizontalSplit.setFirstComponent(leftLayout);
		horizontalSplit.setSecondComponent(rigthLayout);

		bottomLeftCorner.setWidth("100%");
		//bottomLeftCorner.setSpacing(true);
		leftLayout.addComponent(bottomLeftCorner);
	}

	private void initTree() {
		List<Advice> adviceBeans = null;
		// recupero annunci inseriti da utente loggato
		try {
			adviceBeans = bd.get((int) ((User) getUser()).getUserId());
			_log.debug("Ho l' elenco" + adviceBeans);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}
		final HierarchicalBeanItemContainer<Advice> bodyContainer = new HierarchicalBeanItemContainer<Advice>(
				Advice.class, "parent");
		//creo obj parent per tree
		Advice offerta = new Advice("offerta");
		Advice richiesta = new Advice("richiesta");
		offerta.setTitle("Offerte");
		richiesta.setTitle("Richieste");
		bodyContainer.addBean(offerta);
		bodyContainer.addBean(richiesta);
		// Add the beans to the container
		for (Advice body : adviceBeans) {
			if (body.getTipo().equals("offerta")) {
				body.setParent(offerta);
				bodyContainer.addBean(body);
			} else {
				body.setParent(richiesta);
				bodyContainer.addBean(body);
			}
		}
		// Put them in a tree
		tree = new Tree("Advice List", bodyContainer);
		tree.setItemCaptionPropertyId("title");
		tree.setCaption("Annunci Inseriti");
		// Expand the tree
		for (Object rootItemId : bodyContainer.rootItemIds()) {
			tree.setItemCaption(rootItemId, "porco dio");
			tree.expandItemsRecursively(rootItemId);
		}
		tree.setSelectable(true);
		tree.setImmediate(true);
		tree.addListener((ItemClickListener) app);
	}
	
	
	public void itemClick(ItemClickEvent event) {
		if (event.getSource() == tree) {
			Object itemId = event.getItemId();
			Advice advice = (Advice) event.getItemId();
			_log.info("Selezionato: "+advice);
			if (itemId != null) {
				rigthLayout.setFirstComponent(new GridAdvice(advice));
				rigthLayout.setSecondComponent(new HistoryAdvice(main,advice,(String) ((User) getUser()).getScreenName()));	
			}
			contactRemovalButton.setVisible(true);
			contactEditButton.setVisible(true);			
		}
	}
	
	public void update() {
		leftLayout.removeComponent(tree);	
		leftLayout.removeComponent(bottomLeftCorner);
		initTree();
		leftLayout.addComponent(tree);
		leftLayout.setExpandRatio(tree, 1);
		leftLayout.addComponent(bottomLeftCorner);
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		final Button source = event.getButton();
		//if (source == help) {
		//	showHelpWindow();
		//}
	}
	
	
	
	private void initContactAddRemoveButtons() {
		
		// New item button
		contactAddButton = new Button(" + ", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				_log.info("APRO FORM____" + tree.getValue());
				// aggiungo il form passando dati utente
				Advice advice = new Advice();
				advice.setUsername((String) ((User) getUser()).getScreenName());
				advice.setIdUser((int) ((User) getUser()).getUserId());	
				mywindow = new Window("Inserimento Annuncio");
				mywindow.center();
				mywindow.setModal(true);
				mywindow.setHeight("350px");
				mywindow.setWidth("500px");	
				main.addWindow(mywindow);
				mywindow.addComponent(new AdviceWindow("nuovo",app,mywindow,advice));
				
			}
		});	 
		bottomLeftCorner.addComponent(contactAddButton);
		
		// Remove item button
		contactRemovalButton = new Button(" - ", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				_log.info("elimino____" + tree.getValue());
				Advice advice = (Advice) tree.getValue();
				tree.removeItem(tree.getValue());
				tree.select(null);
				_log.info(":::::::::::" + advice.getIdadvice());
				// richiamo delete
				try {
					bd.delete(advice);
					_log.info("obj advice eliminato");
				} catch (AdviceException e) {
					_log.error(e);
					e.printStackTrace();
				}
		        contactRemovalButton.setVisible(false);
		        contactEditButton.setVisible(false);
			}
		});
		contactRemovalButton.setStyleName(Reindeer.BUTTON_DEFAULT);
        contactRemovalButton.setVisible(false);
		bottomLeftCorner.addComponent(contactRemovalButton);
		
		// Edit item button
		contactEditButton = new Button("Modifica", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				_log.info("modifico____" + tree.getValue());
				// aggiungo il form passando obj selezionato
				Advice advice = (Advice) tree.getValue();
				mywindow = new Window("Modifica Annuncio");
				mywindow.center();
				mywindow.setModal(true);
				mywindow.setHeight("350px");
				mywindow.setWidth("500px");	
				main.addWindow(mywindow);
				mywindow.addComponent(new AdviceWindow("modifica",app,mywindow,advice));
			}
		});
		contactEditButton.setStyleName(Reindeer.BUTTON_DEFAULT);
		contactEditButton.setVisible(false);
		bottomLeftCorner.addComponent(contactEditButton);
		
	}
	
	
	
	// per ottenere dati utente loggato
	@Override
	public void onRequestStart(PortletRequest request, PortletResponse response) {
		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		themeDisplay.getCompanyId();
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

	@Override
	public void windowClose(CloseEvent e) {
		// TODO Auto-generated method stub
		
	}

}