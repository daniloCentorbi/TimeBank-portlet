package ita.social.component;

import java.text.SimpleDateFormat;
import java.util.List;

import ita.social.app.Calendarr;
import ita.social.delegate.BusinessDelegate;
import ita.social.model.Advice;
import ita.social.model.CalendarTestEvent;
import ita.social.utils.AdviceException;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

public class DeleteFeedback  extends CustomComponent implements
Window.CloseListener {
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(CalendarWindow.class);
	protected BusinessDelegate bd = new BusinessDelegate();
	private Window mainwindow;
	private Button openbutton;
	private final VerticalLayout layout;
	private Window deletePopup;
	private final Form deleteForm = new Form();
	private Button confirmDeleteButton;
	private Label labelEvent = new Label();
	private String userName;
	private Calendarr application;
	CalendarTestEvent event;
	
	public DeleteFeedback(Calendarr app,Window main,String button, String username,CalendarTestEvent evento) {
		// getWindow().setTheme("calendartest");
		application = app;
		mainwindow = main;
		userName = username;
		event=evento;
		layout = new VerticalLayout();
		openbutton = new Button(button, this, "openButtonClick");
		openbutton.setStyleName(Reindeer.BUTTON_LINK);
		layout.addComponent(openbutton);
		setCompositionRoot(layout);
	}

	/** Handle the clicks for the two buttons. */
	public void openButtonClick(Button.ClickEvent event) {
		createDeletePopup();
		updateDeleteForm();
		mainwindow.addWindow(deletePopup);
	}

	/* Initializes a modal window to edit schedule event. */
	@SuppressWarnings("serial")
	private void createDeletePopup() {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);

		deletePopup = new Window(null, layout);
		deletePopup.setWidth("550px");
		deletePopup.setModal(true);
		deletePopup.center();

		layout.addComponent(deleteForm);

		confirmDeleteButton = new Button("Conferma Eliminazione", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				confirmDeleteEvent();
			}
		});
		deletePopup.addListener(new CloseListener() {
			public void windowClose(CloseEvent e) {
				deleteForm.discard();
				getWindow().removeWindow(deletePopup);
			}
		});

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);

		buttons.addComponent(confirmDeleteButton);
		layout.addComponent(buttons);
		layout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
	}
	
	private void updateDeleteForm() {
		List<Advice> avviso = null;
		// creo CalendarEvent BeanItem e passo al Form
		BeanItem<CalendarTestEvent> item = new BeanItem<CalendarTestEvent>(
				event);
		// rimuovo componenti click precedente
		deleteForm.getLayout().removeAllComponents();
		deleteForm.setWriteThrough(false);
		deleteForm.setItemDataSource(item);
		labelEvent.setContentMode(Label.CONTENT_PREFORMATTED);
		labelEvent.addStyleName(Runo.PANEL_LIGHT);

		// Recupero annuncio selezionato
		try {
			avviso = bd.getOneOnly(event.getIdadvice());
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Label labelData = new Label("<h3> Del: "
				+ sdf.format(event.getStart().getTime()) + "</h3>", Label.CONTENT_XHTML);
		labelData.addStyleName(Reindeer.PANEL_LIGHT);
		
		_log.info("AVVISDO RECUPERATO" + avviso.get(0));
		if (userName.trim().equals(event.getUsername())) {
			deleteForm.getLayout().addComponent(
					new GridEvent(avviso.get(0),event.getFlag(), "delete"));
		} else if (!userName.trim().equals(event.getUsername())) {
			deleteForm.getLayout().addComponent(
					new GridEvent(avviso.get(0),event.getFlag(), "delete"));
		}

		deleteForm.getLayout().addComponent(labelData);
		deleteForm.setFormFieldFactory(new FormFieldFactory() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("deprecation")
			public Field createField(Item item, Object propertyId,
					Component uiContext) {
				return null;
			}
		});
		//visualizzo solo dati da GridEvent
		deleteForm.setVisibleItemProperties(new Object[] {  });
	}
	
	
	private void confirmDeleteEvent() {
		deleteForm.commit();
		CalendarTestEvent event = getFormEvent();
		Window.Notification n = new Window.Notification(
				"Valori inviati PER ELIMINAZIONE",
				Window.Notification.TYPE_TRAY_NOTIFICATION);
		n.setPosition(Window.Notification.POSITION_CENTERED);
		n.setDescription("Appuntamento ELIMINATO");
		getWindow().showNotification(n);
		try {
			bd.delete(event);
			_log.info("obj evento ELIMINATO________" + event);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}
		getWindow().removeWindow(deletePopup);
		application.update();
	}
	
	@SuppressWarnings("unchecked")
	private CalendarTestEvent getFormEvent() {
		BeanItem<CalendarTestEvent> item = (BeanItem<CalendarTestEvent>) deleteForm
				.getItemDataSource();
		CalendarTestEvent event = item.getBean();
		return event;
	}
	
	@Override
	public void windowClose(CloseEvent e) {
		openbutton.setEnabled(true);
	}
	
	
}
