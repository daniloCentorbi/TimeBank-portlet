package ita.social.component;

import ita.social.delegate.BusinessDelegate;
import ita.social.model.Advice;
import ita.social.model.CalendarTestEvent;
import ita.social.utils.AdviceException;

import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.vaadin.addon.calendar.event.BasicEventProvider;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClickHandler;
import com.vaadin.addon.calendar.ui.handler.BasicDateClickHandler;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

public class CalendarWindowAdvice extends CustomComponent implements
		Window.CloseListener {
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(CalendarWindow.class);
	protected BusinessDelegate bd = new BusinessDelegate();
	private List<CalendarTestEvent> elenco;
	private Window mainwindow;
	private Window mywindow;
	private Button openbutton;

	private enum Mode {
		MONTH, DAY;
	}

	private GregorianCalendar calendar;
	/** Target calendar component that this test application is made for. */
	private Calendar calendarComponent;
	private Date currentMonthsFirstDate;
	private final Label captionLabel = new Label("");
	private Button monthButton;
	private Button nextButton;
	private Button prevButton;
	private Window scheduleEventPopup;
	private Window feedbackPopup;
	private final Form scheduleEventForm = new Form();
	private final Form feedbackForm = new Form();
	private Button deleteEventButton;
	private Button applyEventButton;
	private Mode viewMode = Mode.MONTH;
	private BasicEventProvider dataSource;
	private boolean showWeeklyView;
	private boolean useSecondResolution;
	private final VerticalLayout layout;
	private String userName;
	private CalendarTestEvent evento;
	private Advice advice;

	// Reference to main window
	public CalendarWindowAdvice(Window main,String button, String username, Advice adVice) {
		// getWindow().setTheme("calendartest");
		mainwindow = main;
		userName = username;
		advice = adVice;
		// advice=adviceinsert;
		layout = new VerticalLayout();
		openbutton = new Button(button, this, "openButtonClick");
		openbutton.setStyleName(Reindeer.BUTTON_LINK);
		layout.addComponent(openbutton);
		setCompositionRoot(layout);
	}

	/** Handle the clicks for the two buttons. */
	public void openButtonClick(Button.ClickEvent event) {
		mywindow = new Window();
		mywindow.center();
		mywindow.setModal(true);
		mywindow.setHeight("760px");
		mywindow.setWidth("800px");
		mainwindow.addWindow(mywindow);
		/* Listen for close events for the window. */
		mywindow.addListener(this);

		initCalendar();
		initLayoutContent();
		addInitialEvents();
	}

	// pulsanti manage calendario
	@SuppressWarnings("serial")
	private void initLayoutContent() {
		monthButton = new Button("Vista Mese", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				switchToMonthView();
			}
		});
		nextButton = new Button("Next", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				handleNextButtonClick();
			}
		});
		prevButton = new Button("Prev", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				handlePreviousButtonClick();
			}
		});
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setSpacing(true);
		hl.setMargin(false, false, true, false);
		hl.addComponent(prevButton);
		hl.addComponent(captionLabel);
		hl.addComponent(monthButton);
		hl.addComponent(nextButton);
		hl.setComponentAlignment(prevButton, Alignment.MIDDLE_LEFT);
		hl.setComponentAlignment(captionLabel, Alignment.MIDDLE_CENTER);
		hl.setComponentAlignment(monthButton, Alignment.MIDDLE_CENTER);
		hl.setComponentAlignment(nextButton, Alignment.MIDDLE_RIGHT);

		monthButton.setVisible(false);

		mywindow.addComponent(hl);
		mywindow.addComponent(calendarComponent);
	}

	@SuppressWarnings("deprecation")
	private void initCalendar() {
		dataSource = new BasicEventProvider();
		calendarComponent = new Calendar(dataSource);
		calendarComponent.setVisibleHoursOfDay(8, 20);
		calendarComponent.setLocale(getLocale());
		calendarComponent.setImmediate(true);

		Date today = getToday();
		calendar = new GregorianCalendar(getLocale());
		calendar.setTime(today);
		updateCaptionLabel();

		if (!showWeeklyView) {
			int rollAmount = calendar.get(GregorianCalendar.DAY_OF_MONTH) - 1;
			calendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);
			resetTime(false);
			currentMonthsFirstDate = calendar.getTime();
			calendarComponent.setStartDate(currentMonthsFirstDate);
			calendar.add(GregorianCalendar.MONTH, 1);
			calendar.add(GregorianCalendar.DATE, -1);
			calendarComponent.setEndDate(calendar.getTime());
		}

		addCalendarEventListeners();
	}

	private Date getToday() {
		return new Date();
	}

	@SuppressWarnings("serial")
	private void addCalendarEventListeners() {
		calendarComponent.setHandler(new EventClickHandler() {
			public void eventClick(EventClick event) {
				showEventPopup((CalendarTestEvent) event.getCalendarEvent(),
						false);
			}
		});
		calendarComponent.setHandler(new BasicDateClickHandler() {
			@Override
			public void dateClick(DateClickEvent event) {
				super.dateClick(event);
				switchToDayView();
			}
		});

	}

	private void showEventPopup(CalendarTestEvent event, boolean newEvent) {
		if (event == null) {
			return;
		}
		// recupero evento cliccato per avere tutti i campi
		try {
			elenco = bd.getSelectedEvent(event);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}
		for (int i = 0; i < elenco.size(); i++) {
			evento = elenco.get(i);
		}

		// se è un evento passato creo popup FEEDBACK
		if (getToday().getTime() > evento.getStart().getTime()) {
			if (feedbackPopup == null) {
				createFeedbackPopup();
			}
			feedbackPopup.setCaption("Lascia Feedback");
			deleteEventButton.setEnabled(!calendarComponent.isReadOnly());
			applyEventButton.setEnabled(!calendarComponent.isReadOnly());
			updateCalendarFeedbackForm(evento);
			if (!getWindow().getChildWindows().contains(feedbackPopup)) {
				getWindow().addWindow(feedbackPopup);
			}
			// se è un evento posteriore creo form modifica
		} else if (getToday().getTime() < evento.getStart().getTime()) {
			if (scheduleEventPopup == null) {
				scheduleEventForm.getLayout().removeAllComponents();
				createCalendarEventPopup();
			}
			scheduleEventPopup.setCaption("Modifica evento");
			deleteEventButton.setEnabled(!calendarComponent.isReadOnly());
			applyEventButton.setEnabled(!calendarComponent.isReadOnly());
			updateCalendarEventForm(evento);
			if (!getWindow().getChildWindows().contains(scheduleEventPopup)) {
				getWindow().addWindow(scheduleEventPopup);
			}
		}

	}

	/* Initializes a modal window to edit schedule event. */
	@SuppressWarnings("serial")
	private void createCalendarEventPopup() {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);

		scheduleEventPopup = new Window(null, layout);
		scheduleEventPopup.setWidth("600px");
		scheduleEventPopup.setModal(true);
		scheduleEventPopup.center();

		layout.addComponent(scheduleEventForm);

		applyEventButton = new Button("Modifica", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				commitCalendarEvent();
			}
		});
		applyEventButton.setStyleName(Reindeer.BUTTON_DEFAULT);
		Button cancel = new Button("Conferma", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				confirmCalendarEvent();
			}
		});
		cancel.setStyleName(Reindeer.BUTTON_DEFAULT);
		deleteEventButton = new Button("Elimina", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				_log.info("devo eliminare" + event.toString());
				deleteCalendarEvent();
			}
		});
		deleteEventButton.setStyleName(Reindeer.BUTTON_SMALL);
		scheduleEventPopup.addListener(new CloseListener() {
			public void windowClose(CloseEvent e) {
				discardCalendarEvent();
			}
		});
		deleteEventButton.setStyleName(Reindeer.BUTTON_DEFAULT);

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		buttons.addComponent(deleteEventButton);
		buttons.addComponent(applyEventButton);
		buttons.addComponent(cancel);
		layout.addComponent(buttons);
		layout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
	}

	/* Initializes a modal window to edit schedule event. */
	@SuppressWarnings("serial")
	private void createFeedbackPopup() {

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);

		feedbackPopup = new Window(null, layout);
		feedbackPopup.setWidth("400px");
		feedbackPopup.setModal(true);
		feedbackPopup.center();

		layout.addComponent(feedbackForm);

		applyEventButton = new Button("Lascia Feedback", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				commitFeedbackEvent();
			}
		});
		deleteEventButton = new Button("Elimina", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				deleteCalendarEvent();
			}
		});

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		buttons.addComponent(deleteEventButton);
		buttons.addComponent(applyEventButton);
		layout.addComponent(buttons);
		layout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);

	}

	private void updateCalendarEventForm(CalendarTestEvent event) {
		List<Advice> avviso = null;
		// creo CalendarEvent BeanItem e passo al Form
		BeanItem<CalendarTestEvent> item = new BeanItem<CalendarTestEvent>(
				event);
		//rimuovo componenti click precedente
		scheduleEventForm.getLayout().removeAllComponents();
		scheduleEventForm.setWriteThrough(false);
		scheduleEventForm.setItemDataSource(item);

		// Recupero annuncio selezionato
		try {
			avviso = bd.getOneOnly((Integer) item.getItemProperty("idadvice").getValue());
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}

		Label labelState = new Label("<h3> Stato: "
				+ item.getItemProperty("state") + "</h3>" , Label.CONTENT_XHTML);	
		labelState.addStyleName(Reindeer.PANEL_LIGHT);
		scheduleEventForm.getLayout().addComponent(new GridEvent(avviso.get(0),item.getItemProperty("username").toString(),"other"));
		scheduleEventForm.getLayout().addComponent(labelState);		
		scheduleEventForm.setFormFieldFactory(new FormFieldFactory() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("deprecation")
			public Field createField(Item item, Object propertyId,
					Component uiContext) {
				if (propertyId.equals("start")) {
					return createDateField("Data Inizio");
				} else if (propertyId.equals("end")) {
					return createDateField("Data Fine");
				} 
				return null;
			}
			private DateField createDateField(String caption) {
				DateField f = new DateField(caption);
				if (useSecondResolution) {
					f.setResolution(DateField.RESOLUTION_SEC);
				} else {
					f.setResolution(DateField.RESOLUTION_SEC);
				}
				return f;
			}

		});
		scheduleEventForm
				.setVisibleItemProperties(new Object[] { "start", "end" });
	}

	private void updateCalendarFeedbackForm(CalendarTestEvent event) {
		// Lets create a CalendarEvent BeanItem and pass it to the form's data
		// source
		BeanItem<CalendarTestEvent> item = new BeanItem<CalendarTestEvent>(
				event);
		feedbackForm.setWriteThrough(false);
		feedbackForm.setItemDataSource(item);
		feedbackForm.setFormFieldFactory(new FormFieldFactory() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("deprecation")
			public Field createField(Item item, Object propertyId,
					Component uiContext) {
				if (propertyId.equals("vote")) {
					TextField f = createTextField("Voto da 1 a 10");
					return f;
				} else if (propertyId.equals("note")) {
					TextField f = createTextField("Commento");
					f.setRows(3);
					return f;
				}
				return null;
			}

			private TextField createTextField(String caption) {
				TextField f = new TextField(caption);
				f.setNullRepresentation("");
				return f;
			}

		});
		feedbackForm.setVisibleItemProperties(new Object[] { "description",
				"vote", "note" });
	}

	/* Removes the event from the data source and fires change event. */
	private void deleteCalendarEvent() {	
		scheduleEventForm.commit();
		CalendarTestEvent event = getFormCalendarEvent();
		event.setStartdate(event.getStart());
		event.setEnddate(event.getEnd());
		event.setState("eliminazione");
		event.setViewed(false);
		event.setFlag(userName);
		Window.Notification n = new Window.Notification(
				"Valori inviati da adviceForm",
				Window.Notification.TYPE_TRAY_NOTIFICATION);
		n.setPosition(Window.Notification.POSITION_CENTERED);
		n.setDescription("Evento Eliminato ");
		getWindow().showNotification(n);
		_log.info("eliminato>>>>>>>>>>>>>>>" + event.getState());

			// recupero eventi da utenti
			try {
				_log.info("ELIMINO EVENTO CON ID: " + event.getIdevent());
				bd.predelete(event);
				//bd.delete(event);
			} catch (AdviceException e) {
				_log.error(e);
				e.printStackTrace();
			}
		
		getWindow().removeWindow(scheduleEventPopup);
	}

	/* UPDATES the event in the data source and fires change event. */
	private void commitCalendarEvent() {
		scheduleEventForm.commit();
		CalendarTestEvent event = getFormCalendarEvent();
		if (!dataSource.containsEvent(event)) {
			event.setStartdate(event.getStart());
			event.setEnddate(event.getEnd());
			evento.setDescription(event.getDescription());
			event.setState("modificato");
			event.setViewed(false);
			event.setFlag(userName);
			evento.setVote(0);
			evento.setNote("blank");
			_log.info("MODIFICATO" + event.getState());

			Window.Notification n = new Window.Notification(
					"Valori inviati da adviceForm",
					Window.Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Window.Notification.POSITION_CENTERED);
			n.setDescription("Appuntamento Modificato");
			getWindow().showNotification(n);

			try {
				bd.update(event);
				_log.info("obj evento modificato________" + event);
			} catch (AdviceException e) {
				_log.error(e);
				e.printStackTrace();
			}
			// dataSource.addEvent(event);
		}
		getWindow().removeWindow(scheduleEventPopup);
	}

	private void commitFeedbackEvent() {
		feedbackForm.commit();
		CalendarTestEvent event = getFormFeedbackEvent();
		if (!dataSource.containsEvent(event)) {
			// CalendarTestEvent evento = new CalendarTestEvent();
			event.setDescription(event.getDescription());
			event.setState("completato");
			event.setViewed(false);
			evento.setVote(event.getVote());
			evento.setNote(event.getNote());
			_log.info("INSERITO FEEDBACK" + event.getIdevent());

			Window.Notification n = new Window.Notification(
					"Feedback inserito",
					Window.Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Window.Notification.POSITION_CENTERED);
			n.setDescription("Lasciato Feedback: <br />Per : "
					+ event.getTousername() + "<br/>Voto: " + event.getVote()
					+ "<br/>Commento: " + event.getNote()
					+ "<br/>Stato evento: " + event.getState());
			getWindow().showNotification(n);

			try {
				bd.insertFeed(event);
				_log.info("obj Feedback inserito________" + event);
			} catch (AdviceException e) {
				_log.error(e);
				e.printStackTrace();
			}
		}
		getWindow().removeWindow(feedbackPopup);
	}

	private void discardCalendarEvent() {
		scheduleEventForm.discard();
		getWindow().removeWindow(scheduleEventPopup);
	}

	private void confirmCalendarEvent() {
		scheduleEventForm.commit();
		CalendarTestEvent event = getFormCalendarEvent();
		event.setState("confermato");
		event.setViewed(false);
		event.setFlag(userName);
		Window.Notification n = new Window.Notification(
				"Appuntamento Confermato",
				Window.Notification.TYPE_TRAY_NOTIFICATION);
		n.setPosition(Window.Notification.POSITION_CENTERED);
		n.setDescription("Appuntamento Confermato");
		getWindow().showNotification(n);
		_log.info("CONFERMATO>>>>>>>>>>>>>>>" + event.getState());

		try {
			bd.confirm(event);
			_log.info("obj evento modificato________" + event);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}
		getWindow().removeWindow(scheduleEventPopup);
		requestRepaint();
	}

	@SuppressWarnings("unchecked")
	private CalendarTestEvent getFormCalendarEvent() {
		BeanItem<CalendarTestEvent> item = (BeanItem<CalendarTestEvent>) scheduleEventForm
				.getItemDataSource();
		CalendarTestEvent event = item.getBean();
		return event;
	}

	private CalendarTestEvent getFormFeedbackEvent() {
		BeanItem<CalendarTestEvent> item = (BeanItem<CalendarTestEvent>) feedbackForm
				.getItemDataSource();
		CalendarTestEvent event = item.getBean();
		return event;
	}

	private void handleNextButtonClick() {
		switch (viewMode) {
		case MONTH:
			nextMonth();
			break;
		case DAY:
			nextDay();
			break;
		}
	}

	private void handlePreviousButtonClick() {
		switch (viewMode) {
		case MONTH:
			previousMonth();
			break;
		case DAY:
			previousDay();
			break;
		}
	}

	private void nextMonth() {
		rollMonth(1);
	}

	private void previousMonth() {
		rollMonth(-1);
	}

	private void nextDay() {
		rollDate(1);
	}

	private void previousDay() {
		rollDate(-1);
	}

	private void rollMonth(int direction) {
		calendar.setTime(currentMonthsFirstDate);
		calendar.add(GregorianCalendar.MONTH, direction);
		resetTime(false);
		currentMonthsFirstDate = calendar.getTime();
		calendarComponent.setStartDate(currentMonthsFirstDate);
		updateCaptionLabel();
		calendar.add(GregorianCalendar.MONTH, 1);
		calendar.add(GregorianCalendar.DATE, -1);
		resetCalendarTime(true);
	}

	private void rollDate(int direction) {
		calendar.add(GregorianCalendar.DATE, direction);
		resetCalendarTime(false);
		resetCalendarTime(true);
	}

	private void updateCaptionLabel() {
		DateFormatSymbols s = new DateFormatSymbols(getLocale());
		captionLabel.setValue(s.getShortMonths()[calendar
				.get(GregorianCalendar.MONTH)]
				+ " "
				+ calendar.get(GregorianCalendar.YEAR));
	}

	private CalendarTestEvent getNewEvent(String caption, Date start, Date end) {
		CalendarTestEvent event = new CalendarTestEvent();
		event.setCaption(caption);
		event.setStart(start);
		event.setEnd(end);
		return event;
	}

	/*
	 * Switch the Calendar component's start and end date range to the target
	 * month only. (sample range: 01.01.2010 00:00.000 - 31.01.2010 23:59.999)
	 */
	public void switchToMonthView() {
		viewMode = Mode.MONTH;
		monthButton.setVisible(false);

		calendar.setTime(currentMonthsFirstDate);
		calendarComponent.setStartDate(currentMonthsFirstDate);

		updateCaptionLabel();

		calendar.add(GregorianCalendar.MONTH, 1);
		calendar.add(GregorianCalendar.DATE, -1);
		resetCalendarTime(true);
	}

	/*
	 * Switch to day view (week view with a single day visible).
	 */
	public void switchToDayView() {
		viewMode = Mode.DAY;
		monthButton.setVisible(true);
	}

	private void resetCalendarTime(boolean resetEndTime) {
		resetTime(resetEndTime);
		if (resetEndTime) {
			calendarComponent.setEndDate(calendar.getTime());
		} else {
			calendarComponent.setStartDate(calendar.getTime());
			updateCaptionLabel();
		}
	}

	/*
	 * Resets the calendar time (hour, minute second and millisecond) either to
	 * zero or maximum value.
	 */
	private void resetTime(boolean max) {
		if (max) {
			calendar.set(GregorianCalendar.HOUR_OF_DAY,
					calendar.getMaximum(GregorianCalendar.HOUR_OF_DAY));
			calendar.set(GregorianCalendar.MINUTE,
					calendar.getMaximum(GregorianCalendar.MINUTE));
			calendar.set(GregorianCalendar.SECOND,
					calendar.getMaximum(GregorianCalendar.SECOND));
			calendar.set(GregorianCalendar.MILLISECOND,
					calendar.getMaximum(GregorianCalendar.MILLISECOND));
		} else {
			calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
			calendar.set(GregorianCalendar.MINUTE, 0);
			calendar.set(GregorianCalendar.SECOND, 0);
			calendar.set(GregorianCalendar.MILLISECOND, 0);
		}
	}

	
	
	private void addInitialEvents() {
		// recupero eventi per advice selezionato
		try {
			elenco = bd.getEventFromAdvice(advice);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}

		Date originalDate = calendar.getTime();
		Date today = getToday();
		Date start;
		Date end;
		CalendarTestEvent event;
		String state;

		for (int i = 0; i < elenco.size(); i++) {

			// if evento posteriore a giorno corrente
			if (today.getTime() < elenco.get(i).getStart().getTime()) {
				start = elenco.get(i).getStart();
				end = elenco.get(i).getEnd();
				event = getNewEvent("DA LISTA", start, end);
				state = elenco.get(i).getState();
				event.setCaption(elenco.get(i).getCaption() + "<br/>"
						+ elenco.get(i).getState());
				event.setDescription(elenco.get(i).getDescription());
				event.setState(elenco.get(i).getState());
				dataSource.addEvent(event);
				// se è stata modificata data
				if (elenco.get(i).getState().equals("modificato")) {
					event.setStyleName("color3");
				} else {
					// evento da fare
					event.setStyleName("color2");
				}
				// se evento passato
			} else if (today.getTime() > elenco.get(i).getStart().getTime()) {
				// se ho già lasciato feed
				if (elenco.get(i).getState().equals("completato")) {
					_log.info("completato");
					// se devo lasciare feed
				} else {
					start = elenco.get(i).getStart();
					end = elenco.get(i).getEnd();
					event = getNewEvent("DA LISTA", start, end);
					state = elenco.get(i).getState();
					event.setStyleName("color4");
					if (elenco.get(i).getTipo().equals("richiesta")) {
						event.setCaption("Lascia FEEDBACK A: "
								+ elenco.get(i).getUsername());
						event.setDescription(elenco.get(i).getDescription());
						event.setState(elenco.get(i).getState());
						dataSource.addEvent(event);
					} else if (elenco.get(i).getTipo().equals("offerta")) {
						event.setCaption("Sollecita FEEDBACK a: "
								+ elenco.get(i).getUsername());
						event.setDescription(elenco.get(i).getDescription());
						event.setState(elenco.get(i).getState());
						dataSource.addEvent(event);
					}
				}
			}
		}

		calendar.setTime(originalDate);
	}

	@Override
	public void windowClose(CloseEvent e) {
		openbutton.setEnabled(true);
	}
}