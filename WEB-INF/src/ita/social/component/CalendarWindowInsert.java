package ita.social.component;

import ita.social.delegate.BusinessDelegate;
import ita.social.model.Advice;
import ita.social.model.CalendarTestEvent;
import ita.social.utils.AdviceException;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.vaadin.addon.calendar.event.BasicEvent;
import com.vaadin.addon.calendar.event.BasicEventProvider;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClickHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.RangeSelectEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.RangeSelectHandler;
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
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class CalendarWindowInsert extends CustomComponent implements
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
	private final Form scheduleEventForm = new Form();
	private Button deleteEventButton;
	private Button applyEventButton;
	private Mode viewMode = Mode.MONTH;
	private BasicEventProvider dataSource;
	private boolean showWeeklyView;
	private boolean useSecondResolution;
	final VerticalLayout layout;
	int idUser;
	private String userName;
	private String toUsername;
	private Advice advice;

	// Reference to main window
	public CalendarWindowInsert(Window main, int iduser, String username,
			Advice adviceinsert) {
		mainwindow = main;
		idUser = iduser;
		userName = username;
		toUsername = username;
		advice = adviceinsert;
		layout = new VerticalLayout();
		openbutton = new Button("Iscriviti", this, "openButtonClick");
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
		// mywindow.getWindow().setTheme("calendartest");
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

	private void initCalendar() {
		dataSource = new BasicEventProvider();
		calendarComponent = new Calendar(dataSource);
		calendarComponent.setVisibleHoursOfDay(8, 23);
		calendarComponent.setLocale(getLocale());
		calendarComponent.setImmediate(true);

		Date today = getToday();
		calendar = new GregorianCalendar(getLocale());
		calendar.setTime(today);
		updateCaptionLabel();

		if (!showWeeklyView) {
			int rollAmount = calendar.get(GregorianCalendar.DAY_OF_MONTH) - 1;// month's
																				// day
			calendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);
			resetTime(false);
			currentMonthsFirstDate = calendar.getTime();
			calendarComponent.setStartDate(currentMonthsFirstDate);
			calendar.add(GregorianCalendar.MONTH, 1);
			calendar.add(GregorianCalendar.DATE, -1);
			calendarComponent.setEndDate(calendar.getTime());// return last
																// month day
		}

		addCalendarEventListeners();
	}

	private Date getToday() {
		return new Date();
	}

	@SuppressWarnings("serial")
	private void addCalendarEventListeners() {

		calendarComponent.setHandler(new BasicDateClickHandler() {
			@Override
			public void dateClick(DateClickEvent event) {
				super.dateClick(event);
				switchToDayView();
			}
		});
		calendarComponent.setHandler(new RangeSelectHandler() {
			public void rangeSelect(RangeSelectEvent event) {
				Date start = event.getStart();
				Date end = event.getEnd();
				showEventPopup(createNewEvent(start, end), true);
			}
		});
	}

	private void showEventPopup(CalendarTestEvent event, boolean newEvent) {
		if (event == null) {
			return;
		}
		updateCalendarEventPopup(newEvent);
		updateCalendarEventForm(event);
		if (!getWindow().getChildWindows().contains(scheduleEventPopup)) {
			getWindow().addWindow(scheduleEventPopup);
		}
	}

	/* Initializes a modal window to edit schedule event. */
	@SuppressWarnings("serial")
	private void createCalendarEventPopup() {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);

		scheduleEventPopup = new Window(null, layout);
		scheduleEventPopup.setWidth("400px");
		scheduleEventPopup.setModal(true);
		scheduleEventPopup.center();

		layout.addComponent(scheduleEventForm);

		applyEventButton = new Button("Salva", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				commitCalendarEvent();
			}
		});
		deleteEventButton = new Button("Cancella", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				discardCalendarEvent();
			}
		});
		
		scheduleEventPopup.addListener(new CloseListener() {
			public void windowClose(CloseEvent e) {
				discardCalendarEvent();
			}
		});

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		buttons.addComponent(applyEventButton);
		buttons.addComponent(deleteEventButton);
		layout.addComponent(buttons);
		layout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
	}
	
	private void updateCalendarEventPopup(boolean newEvent) {
		if (scheduleEventPopup == null) {
			createCalendarEventPopup();
		}
		if (newEvent) {
			scheduleEventPopup.setCaption("Nuovo evento");
		} else {
		}
		scheduleEventPopup.setCaption("Modifica evento");
		applyEventButton.setEnabled(!calendarComponent.isReadOnly());
	}

	private void updateCalendarEventForm(CalendarTestEvent event) {
		// Lets create a CalendarEvent BeanItem and pass it to the form's data
		// source
		BeanItem<CalendarTestEvent> item = new BeanItem<CalendarTestEvent>(
				event);
		scheduleEventForm.setWriteThrough(false);
		scheduleEventForm.setItemDataSource(item);
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
		scheduleEventForm.setVisibleItemProperties(new Object[] { "start",
				"end" });
	}

	private CalendarTestEvent createNewEvent(Date startDate, Date endDate) {
		CalendarTestEvent event = new CalendarTestEvent();
		event.setCaption("");
		event.setStart(startDate);
		event.setEnd(endDate);
		event.setStyleName("inserito");
		return event;
	}

	/* Adds/updates the event in the data source and fires change event. */
	private void commitCalendarEvent() {
		scheduleEventForm.commit();
		CalendarTestEvent event = getFormCalendarEvent();
		if (!dataSource.containsEvent(event)) {
			CalendarTestEvent evento = new CalendarTestEvent();
			evento.setIdUser(idUser);
			evento.setUsername(userName);
			evento.setStartdate(event.getStart());
			evento.setEnddate(event.getEnd());
			evento.setCaption(advice.getTipology());
			evento.setDescription(advice.getTitle());
			evento.setState("inserito");
			evento.setTousername(advice.getUsername());
			evento.setIdadvice(advice.getIdadvice());
			evento.setViewed(false);
			evento.setVote(0);
			evento.setNote("blank");
			evento.setTipo(advice.getTipo());
			evento.setFlag(userName);
			
			Window.Notification n = new Window.Notification(
					"Appuntamento Inserito",
					Window.Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Window.Notification.POSITION_CENTERED);
			n.setDescription("Appuntamento Inserito: <br />Per Annuncio di : "
					+ evento.getTousername() +  "<br/>data inizio: "
					+ evento.getStartdate() + "<br/>data fine: "
					+ evento.getEnddate());
			getWindow().showNotification(n);

			try {
				bd.insert(evento);
				_log.info("obj advice inserito________" + evento);
			} catch (AdviceException e) {
				_log.error(e);
				e.printStackTrace();
			}
		}
		getWindow().removeWindow(scheduleEventPopup);
		getWindow().removeWindow(mywindow);		
	}

	private void discardCalendarEvent() {
		scheduleEventForm.discard();
		getWindow().removeWindow(scheduleEventPopup);
	}

	@SuppressWarnings("unchecked")
	private CalendarTestEvent getFormCalendarEvent() {
		BeanItem<CalendarTestEvent> item = (BeanItem<CalendarTestEvent>) scheduleEventForm
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
		// recupero eventi
		try {
			elenco = bd.getEvent(userName);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}
		for (int i = 0; i < elenco.size(); i++) {
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
				if (elenco.get(i).getState().equals("modificato")) {
					event.setStyleName("color3");
				} else {
					event.setStyleName("color1");
				}
			} else if (today.getTime() > elenco.get(i).getStart().getTime()) {
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

		// recupero eventi da utenti
		try {
			elenco = bd.getEventFromUser(userName);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}

		for (int i = 0; i < elenco.size(); i++) {
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
				if (elenco.get(i).getState().equals("modificato")) {
					event.setStyleName("color3");
				} else {
					event.setStyleName("color2");
				}
			} else if (today.getTime() > elenco.get(i).getStart().getTime()) {
				if (elenco.get(i).getState().equals("completato")) {
					_log.info("completato");
					start = elenco.get(i).getStart();
					end = elenco.get(i).getEnd();
					event = getNewEvent("DA LISTA", start, end);
					state = elenco.get(i).getState();
					event.setCaption("MACCHE PORCO: "
							+ elenco.get(i).getUsername());
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