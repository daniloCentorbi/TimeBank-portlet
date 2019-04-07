package ita.social.component;

import ita.social.app.Calendarr;
import ita.social.delegate.BusinessDelegate;
import ita.social.model.Advice;
import ita.social.model.CalendarTestEvent;
import ita.social.utils.AdviceException;
import ita.social.utils.RangeValidator;

import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.vaadin.addon.calendar.event.BasicEventProvider;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClickHandler;
import com.vaadin.addon.calendar.ui.handler.BasicDateClickHandler;
import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.*;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

public class CalendarWindowFeedback extends CustomComponent implements
		Window.CloseListener {
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(CalendarWindow.class);
	protected BusinessDelegate bd = new BusinessDelegate();
	private Window mainwindow;
	private Window mywindow;
	private Button openbutton;

	private enum Mode {
		MONTH, DAY;
	}

	private List<CalendarTestEvent> elenco;
	private GregorianCalendar calendar;
	/** Target calendar component that this test application is made for. */
	private Calendar calendarComponent;
	private Date currentMonthsFirstDate;
	private final Label captionLabel = new Label("");
	private Button monthButton;
	private Button nextButton;
	private Button prevButton;
	private Window feedbackPopup;
	private final Form feedbackForm = new Form();
	private Button applyEventButton;
	private Button deleteEventButton;
	private Mode viewMode = Mode.MONTH;
	private BasicEventProvider dataSource;
	private boolean showWeeklyView;
	private final VerticalLayout layout;
	private CalendarTestEvent evento;
	private Advice advice;
	private Calendarr application;
	private String userName;

	public CalendarWindowFeedback(Calendarr app, Window main, String button,
			String username) {
		// getWindow().setTheme("calendartest");
		mainwindow = main;
		application = app;
		userName = username;
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
		mywindow.setHeight("650px");
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
		if (feedbackPopup == null) {
			createFeedbackPopup();
		}
		feedbackPopup.setCaption("Lascia Feedback");
		applyEventButton.setEnabled(!calendarComponent.isReadOnly());
		updateCalendarFeedbackForm(evento);
		if (!getWindow().getChildWindows().contains(feedbackPopup)) {
			getWindow().addWindow(feedbackPopup);
		}

	}

	/* Initializes a modal window to edit schedule event. */
	@SuppressWarnings("serial")
	private void createFeedbackPopup() {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);

		feedbackPopup = new Window(null, layout);
		feedbackPopup.setWidth("600px");
		feedbackPopup.setModal(true);
		feedbackPopup.center();

		layout.addComponent(feedbackForm);

		applyEventButton = new Button("Lascia Feedback", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				updateFeedbackEvent();
			}
		});
		deleteEventButton = new Button("Elimina", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				deleteFeedbackEvent();
			}
		});
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		buttons.addComponent(applyEventButton);
		buttons.addComponent(deleteEventButton);
		layout.addComponent(buttons);
		layout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);

	}

	private void updateCalendarFeedbackForm(CalendarTestEvent event) {
		List<Advice> avviso = null;
		BeanItem<CalendarTestEvent> item = new BeanItem<CalendarTestEvent>(
				event);
		feedbackForm.getLayout().removeAllComponents();
		feedbackForm.setWriteThrough(false);
		feedbackForm.setItemDataSource(item);

		// Recupero annuncio selezionato
		try {
			avviso = bd.getOneOnly((Integer) item.getItemProperty("idadvice")
					.getValue());
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}

		if (userName.trim().equals(avviso.get(0).getUsername().toString())) {
			feedbackForm.getLayout().addComponent(
					new GridFeedback(avviso.get(0), item.getItemProperty(
							"username").toString(), "mine"));
		} else if (!userName.trim().equals(avviso.get(0).getUsername().toString())) {
			feedbackForm.getLayout().addComponent(
					new GridFeedback(avviso.get(0), item.getItemProperty(
							"username").toString(), "other"));
		}

		feedbackForm.setFormFieldFactory(new FormFieldFactory() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("deprecation")
			public Field createField(Item item, Object propertyId,
					Component uiContext) {

				if (propertyId.equals("vote")) {
					TextField f = createTextField("Voto da 1 a 10");
					f.setWidth("2em");
					//f.addValidator((Validator) new IntegerRangeValidator(
						//	"Descrizione tra 3-500 characters", 0, 10)); 
					Class<?> cls = item.getItemProperty(propertyId).getType();
					if (cls.equals(Integer.class)) {
						f.setDescription("Inserisci un voto");
						f.addValidator(new IntegerValidator("Deve essere un numero tra 1 e 10"));
					}
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
		feedbackForm.setVisibleItemProperties(new Object[] { "vote", "note" });
	}

	private void updateFeedbackEvent() {
		feedbackForm.commit();
		CalendarTestEvent event = getFormFeedbackEvent();
		if (!dataSource.containsEvent(event)) {
			event.setDescription(event.getDescription());
			event.setState("completato");
			event.setViewed(false);
			event.setVote(event.getVote());
			event.setNote(event.getNote());
			event.setFlag(userName);
			Window.Notification n = new Window.Notification(
					"Feedback Inserito",
					Window.Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Window.Notification.POSITION_CENTERED);
			n.setDescription("<br/>Voto: " + event.getVote()
					+ "<br/>Commento: " + event.getNote()
					+ "<br/>Stato evento: " + event.getState());
			getWindow().showNotification(n);

			try {
				bd.insertFeed(event);
			} catch (AdviceException e) {
				_log.error(e);
				e.printStackTrace();
			}

			// aggiorno crediti banca
			if (event.getTipo().equals("richiesta")) {
				try {
					_log.info("Aggiungo a : " + event.getUsername()
							+ "tolgo a: " + event.getTousername());
					int y = bd.updateCredit(event.getUsername(),
							event.getTousername());
				} catch (AdviceException e) {
					_log.error(e);
					e.printStackTrace();
				}
			} else if (event.getTipo().equals("offerta")) {
				try {
					_log.info("Aggiungo a : " + event.getTousername()
							+ "tolgo a: " + event.getUsername());
					int y = bd.updateCredit(event.getTousername(),
							event.getUsername());
				} catch (AdviceException e) {
					_log.error(e);
					e.printStackTrace();
				}
			}

		}
		getWindow().removeWindow(feedbackPopup);
		getWindow().removeWindow(mywindow);
		application.update();
	}

	private void deleteFeedbackEvent() {
		feedbackForm.commit();
		CalendarTestEvent event = getFormFeedbackEvent();
		event.setStartdate(event.getStart());
		event.setEnddate(event.getEnd());
		event.setDescription(event.getDescription());
		event.setState("eliminare");
		event.setViewed(false);
		event.setFlag(userName);
		Window.Notification n = new Window.Notification(
				"Richiesta di eliminazione inviata",
				Window.Notification.TYPE_TRAY_NOTIFICATION);
		n.setPosition(Window.Notification.POSITION_CENTERED);
		getWindow().showNotification(n);
		try {
			bd.predelete(event);
			_log.info("obj evento modificato________" + event);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}
		getWindow().removeWindow(feedbackPopup);
		getWindow().removeWindow(mywindow);
		application.update();
	}

	@SuppressWarnings("unchecked")
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
		Date originalDate = calendar.getTime();
		Date today = getToday();
		Date start;
		Date end;
		CalendarTestEvent event;
		String state;

		// recupero eventi utente
		try {
			elenco = bd.getEvent(userName);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}
		for (int i = 0; i < elenco.size(); i++) {
			if (today.getTime() < elenco.get(i).getStart().getTime()) {
				_log.info("NON IMPORTA");
			} else if (today.getTime() > elenco.get(i).getStart().getTime()) {
				if (elenco.get(i).getState().equals("completato")) {
					_log.info("completato");
				} else if (elenco.get(i).getState().equals("eliminare")) {
					_log.info("in eliminazione");
					start = elenco.get(i).getStart();
					end = elenco.get(i).getEnd();
					event = getNewEvent("DA LISTA", start, end);
					state = elenco.get(i).getState();
					event.setCaption("ATTENDI ELIMINAZIONE: "
							+ elenco.get(i).getUsername());
					event.setStyleName("color3");
					event.setDescription(elenco.get(i).getDescription());
					event.setState(elenco.get(i).getState());
					dataSource.addEvent(event);
				} else {
					start = elenco.get(i).getStart();
					end = elenco.get(i).getEnd();
					event = getNewEvent("DA LISTA", start, end);
					state = elenco.get(i).getState();
					event.setStyleName("color4");
					if (elenco.get(i).getTipo().equals("offerta")) {
						event.setCaption("Lascia FEEDBACK");
						event.setDescription(elenco.get(i).getDescription());
						event.setState(elenco.get(i).getState());
						dataSource.addEvent(event);
					}
				}
			}
		}

		// recupero eventi da utenti
		try {
			elenco = bd.getEventFromUser(userName);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}

		for (int i = 0; i < elenco.size(); i++) {
			if (today.getTime() < elenco.get(i).getStart().getTime()) {
				_log.info("NON IMPORTA");
			} else if (today.getTime() > elenco.get(i).getStart().getTime()) {
				if (elenco.get(i).getState().equals("completato")) {
					_log.info("completato");
				} else if (elenco.get(i).getState().equals("eliminare")) {
					_log.info("in eliminazione");
					start = elenco.get(i).getStart();
					end = elenco.get(i).getEnd();
					event = getNewEvent("DA LISTA", start, end);
					state = elenco.get(i).getState();
					event.setCaption("ATTENDI ELIMINAZIONE: "
							+ elenco.get(i).getUsername());
					event.setStyleName("color3");
					event.setDescription(elenco.get(i).getDescription());
					event.setState(elenco.get(i).getState());
					dataSource.addEvent(event);
				} else {
					start = elenco.get(i).getStart();
					end = elenco.get(i).getEnd();
					event = getNewEvent("DA LISTA", start, end);
					state = elenco.get(i).getState();
					event.setStyleName("color4");
					if (elenco.get(i).getTipo().equals("richiesta")) {
						event.setCaption("Lascia FEEDBACK");
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
