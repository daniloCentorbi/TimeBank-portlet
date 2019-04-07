package ita.social.component;

import ita.social.app.Calendarr;
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
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

public class CalendarHistory extends CustomComponent implements
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
	private Window feedbackPopup;
	private final Form feedbackForm = new Form();
	private Mode viewMode = Mode.MONTH;
	private BasicEventProvider dataSource;
	private boolean showWeeklyView;
	private final VerticalLayout layout;
	private String userName;
	private CalendarTestEvent evento;
	private Calendarr application;

	// Reference to main window
	public CalendarHistory(Calendarr app, Window main, String button,
			String username) {
		// getWindow().setTheme("calendartest");
		application = app;
		mainwindow = main;
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
	public void initLayoutContent() {
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
	public void initCalendar() {
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
			feedbackPopup.setCaption("Visualizza Feedback");
			updateCalendarFeedbackForm(evento);
			if (!getWindow().getChildWindows().contains(feedbackPopup)) {
				getWindow().addWindow(feedbackPopup);
			}
			// se è un evento posteriore creo form modifica
		} else if (getToday().getTime() < evento.getStart().getTime()) {
	
		}

	}

	/* Initializes a modal window to edit schedule event. */
	private void createFeedbackPopup() {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);

		feedbackPopup = new Window(null, layout);
		feedbackPopup.setWidth("500px");
		feedbackPopup.setModal(true);
		feedbackPopup.center();

		layout.addComponent(feedbackForm);
	}

	private void updateCalendarFeedbackForm(CalendarTestEvent event) {
		List<Advice> avviso = null;
		BeanItem<CalendarTestEvent> item = new BeanItem<CalendarTestEvent>(
				event);
		feedbackForm.getLayout().removeAllComponents();
		feedbackForm.setWriteThrough(false);
		feedbackForm.setItemDataSource(item);
		Label labelFeed = null;

		// Recupero annuncio selezionato
		try {
			avviso = bd.getOneOnly((Integer) item.getItemProperty("idadvice")
					.getValue());
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}

		feedbackForm.getLayout().addComponent(
				new GridFeedback(avviso.get(0), item
						.getItemProperty("username").toString(), "feedback"));

		labelFeed = new Label("<h3> Voto: " + item.getItemProperty("vote")
				+ "</h3>" + "Commento: " + item.getItemProperty("note")
				+ "<h5> Da: " + item.getItemProperty("flag") + "</h5>",
				Label.CONTENT_XHTML);
		labelFeed.addStyleName(Reindeer.PANEL_LIGHT);

		feedbackForm.getLayout().addComponent(labelFeed);
		feedbackForm.setFormFieldFactory(new FormFieldFactory() {
			private static final long serialVersionUID = 1L;

			
			public Field createField(Item item, Object propertyId,
					Component uiContext) {
				return null;
			}
		});
		feedbackForm.setVisibleItemProperties(new Object[] {});
	}

	@SuppressWarnings("unused")
	private CalendarTestEvent getFormFeedbackEvent() {
		@SuppressWarnings("unchecked")
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

	public void addInitialEvents() {
		Date originalDate = calendar.getTime();
		Date today = getToday();
		Date start;
		Date end;
		CalendarTestEvent event;
		String state;

		// recupero feedback lasciati da me
		try {
			elenco = bd.getEvent(userName);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}
		
		for (int i = 0; i < elenco.size(); i++) {
			if (today.getTime() < elenco.get(i).getStart().getTime()) {

			} else if (today.getTime() > elenco.get(i).getStart().getTime()) {
				if (elenco.get(i).getState().equals("completato")) {
					start = elenco.get(i).getStart();
					end = elenco.get(i).getEnd();
					event = getNewEvent("DA LISTA", start, end);
					state = elenco.get(i).getState();
					event.setCaption("VOTO: "
							+ elenco.get(i).getVote());
					event.setDescription(elenco.get(i).getDescription());
					event.setState(elenco.get(i).getState());
					event.setStyleName("color2");
					dataSource.addEvent(event);
				}
			}
		}

		// recupero feedback da utenti per i miei annunci
		try {
			elenco = bd.getEventFromUser(userName);
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}

		for (int i = 0; i < elenco.size(); i++) {
			if (today.getTime() < elenco.get(i).getStart().getTime()) {

			} else if (today.getTime() > elenco.get(i).getStart().getTime()) {
				if (elenco.get(i).getState().equals("completato")) {
					start = elenco.get(i).getStart();
					end = elenco.get(i).getEnd();
					event = getNewEvent("DA LISTA", start, end);
					state = elenco.get(i).getState();
					event.setCaption("VOTO: "
							+ elenco.get(i).getVote());
					event.setDescription(elenco.get(i).getDescription());
					event.setState(elenco.get(i).getState());
					event.setStyleName("color2");
					dataSource.addEvent(event);
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
