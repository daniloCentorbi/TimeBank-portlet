package vaadinExample;

import ita.social.app.AddAdvice;
import ita.social.delegate.BusinessDelegate;
import ita.social.model.CalendarTestEvent;
import ita.social.model.Message;
import ita.social.utils.AdviceException;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.vaadin.Application;
import com.vaadin.addon.calendar.event.BasicEvent;
import com.vaadin.addon.calendar.event.BasicEventProvider;
import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClickHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.RangeSelectEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.RangeSelectHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.WeekClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.WeekClickHandler;
import com.vaadin.addon.calendar.ui.handler.BasicDateClickHandler;
import com.vaadin.addon.calendar.ui.handler.BasicWeekClickHandler;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
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


/** Calendar component test application */
public class CalendarTest extends Application implements PortletRequestListener{
    private static final long serialVersionUID = -5436777475398410597L;
	private static Log _log = LogFactoryUtil.getLog(CalendarTest.class);
	protected BusinessDelegate bd = new BusinessDelegate();
	List<CalendarTestEvent> elenco;
	
    private enum Mode {
        MONTH, WEEK, DAY;
    }

    /**
     * This Gregorian calendar is used to control dates and time inside of this
     * test application.
     */
    private GregorianCalendar calendar;

    /** Target calendar component that this test application is made for. */
    private Calendar calendarComponent;

    private Date currentMonthsFirstDate;

    private final Label captionLabel = new Label("");

    private Button monthButton;

    private Button weekButton;

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


	@SuppressWarnings("serial")
    @Override
    public void init() {
        Window w = new Window();
        setMainWindow(w);
        setTheme("calendartest");

        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("500px");
		layout.setWidth("100%");
        layout.setMargin(true);

        w.setContent(layout);
        w.setHeight("500px");
		w.setWidth("100%");

        // URL parameters must be handled before constructing layout any
        // further. To get access to the parameters, we use ParameterHandler.
        w.addParameterHandler(new ParameterHandler() {
            public void handleParameters(Map<String, String[]> parameters) {
                if (dataSource == null) {
                    //handleURLParams(parameters);
                    // This needs to be called only once per a session after
                    // the first Application init-method call.
                    initContent();
                }
            }
        });
    }

	public void initContent() {
        setLocale(Locale.getDefault());
        initCalendar();
        initLayoutContent();
        addInitialEvents();
    }


    //pulsanti manage calendario
    @SuppressWarnings("serial")
	private void initLayoutContent() {
        monthButton = new Button("Vista Mese", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                switchToMonthView();
            }
        });
        weekButton = new Button("Vista Settimana", new ClickListener() {      
            public void buttonClick(ClickEvent event) {
                // simulate week click
                WeekClickHandler handler = (WeekClickHandler) calendarComponent
                        .getHandler(WeekClick.EVENT_ID);
                handler.weekClick(new WeekClick(calendarComponent, calendar
                        .get(GregorianCalendar.WEEK_OF_YEAR), calendar
                        .get(GregorianCalendar.YEAR)));
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
        hl.addComponent(weekButton);
        hl.addComponent(nextButton);
        hl.setComponentAlignment(prevButton, Alignment.MIDDLE_LEFT);
        hl.setComponentAlignment(captionLabel, Alignment.MIDDLE_CENTER);
        hl.setComponentAlignment(monthButton, Alignment.MIDDLE_CENTER);
        hl.setComponentAlignment(weekButton, Alignment.MIDDLE_CENTER);
        hl.setComponentAlignment(nextButton, Alignment.MIDDLE_RIGHT);

        monthButton.setVisible(viewMode == Mode.WEEK);
        weekButton.setVisible(viewMode == Mode.DAY);

        VerticalLayout layout = (VerticalLayout) getMainWindow().getContent();
        layout.addComponent(hl);
        layout.addComponent(calendarComponent);
        layout.setExpandRatio(calendarComponent, 1);
    }



    private void initCalendar() {
        dataSource = new BasicEventProvider();

        calendarComponent = new Calendar(dataSource);
        calendarComponent.setLocale(getLocale());
        calendarComponent.setImmediate(true);

        Date today = getToday();
        calendar = new GregorianCalendar(getLocale());
        calendar.setTime(today);

        updateCaptionLabel();

        if (!showWeeklyView) {
        	_log.info("_______!showWeeklyView______");
            int rollAmount = calendar.get(GregorianCalendar.DAY_OF_MONTH) - 1;//return giorni del mese(28,30 o 31)
            calendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);
            resetTime(false);
            currentMonthsFirstDate = calendar.getTime();
            calendarComponent.setStartDate(currentMonthsFirstDate);
            calendar.add(GregorianCalendar.MONTH, 1);
            calendar.add(GregorianCalendar.DATE, -1);
            calendarComponent.setEndDate(calendar.getTime());//return ultimo giorno mese
        }

        addCalendarEventListeners();
    }

    private Date getToday() {
        return new Date();
    }

    @SuppressWarnings("serial")
    private void addCalendarEventListeners() {
        // Register week clicks by changing the schedules start and end dates.
        calendarComponent.setHandler(new BasicWeekClickHandler() {
            @Override
            public void weekClick(WeekClick event) {
                super.weekClick(event);
                updateCaptionLabel();
                switchToWeekView();
            }
        });

        calendarComponent.setHandler(new EventClickHandler() {
            public void eventClick(EventClick event) {
                showEventPopup((CalendarTestEvent)event.getCalendarEvent(), false);
            	_log.info("CLICK EVENTO______");
                
            }
        });

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
            	_log.info("RANGE SELECT______");
            }
        });
    }

    private void showEventPopup(CalendarTestEvent event, boolean newEvent) {
        if (event == null) {
            return;
        }
        updateCalendarEventPopup(newEvent);
        updateCalendarEventForm(event);

        if (!getMainWindow().getChildWindows().contains(scheduleEventPopup)) {
            getMainWindow().addWindow(scheduleEventPopup);
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
        Button cancel = new Button("Cancella", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                discardCalendarEvent();
            }
        });
        deleteEventButton = new Button("Elimina", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                deleteCalendarEvent();
            }
        });
        scheduleEventPopup.addListener(new CloseListener() {
            public void windowClose(CloseEvent e) {
                discardCalendarEvent();
            }
        });

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.addComponent(deleteEventButton);
        buttons.addComponent(applyEventButton);
        buttons.addComponent(cancel);
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
            scheduleEventPopup.setCaption("Modifica evento");
        }
        deleteEventButton.setVisible(!newEvent);
        deleteEventButton.setEnabled(!calendarComponent.isReadOnly());
        applyEventButton.setEnabled(!calendarComponent.isReadOnly());
    }

    private void updateCalendarEventForm(CalendarTestEvent event) {
        // Lets create a CalendarEvent BeanItem and pass it to the form's data source
        BeanItem<CalendarTestEvent> item = new BeanItem<CalendarTestEvent>(event);
        scheduleEventForm.setWriteThrough(false);
        scheduleEventForm.setItemDataSource(item);
        scheduleEventForm.setFormFieldFactory(new FormFieldFactory() {
            private static final long serialVersionUID = 1L;

            public Field createField(Item item, Object propertyId,
                    Component uiContext) {
                if (propertyId.equals("caption")) {
                    TextField f = createTextField("Titolo");
                    f.focus();
                    return f;
                } else if (propertyId.equals("description")) {
                    TextField f = createTextField("Descrizione");
                    f.setRows(3);
                    return f;
                } else if (propertyId.equals("state")) {
                    return createStyleNameSelect();

                } else if (propertyId.equals("start")) {
                    return createDateField("Data Inizio");

                } else if (propertyId.equals("end")) {
                    return createDateField("Data Fine");
                } 
                return null;
            }


            private TextField createTextField(String caption) {
                TextField f = new TextField(caption);
                f.setNullRepresentation("");
                return f;
            }

            private DateField createDateField(String caption) {
                DateField f = new DateField(caption);
                if (useSecondResolution) {
                    f.setResolution(DateField.RESOLUTION_SEC);
                } else {
                    f.setResolution(DateField.RESOLUTION_MIN);
                }
                return f;
            }

            private Select createStyleNameSelect() {
                Select s = new Select("Stato");
                s.addContainerProperty("c", String.class, "");
                s.setItemCaptionPropertyId("c");
                Item i = s.addItem("color1");
                i.getItemProperty("c").setValue("Inserimento");
                i = s.addItem("color2");
                i.getItemProperty("c").setValue("Modifica");
                i = s.addItem("color3");
                i.getItemProperty("c").setValue("Accetta");
                return s;
            }
        });

        scheduleEventForm
        .setVisibleItemProperties(new Object[] { "start", "end",
                 "caption", "description", "state" });
    }

    private CalendarTestEvent createNewEvent(Date startDate, Date endDate) {
        CalendarTestEvent event = new CalendarTestEvent();
        event.setCaption("");
        event.setStart(startDate);
        event.setEnd(endDate);
        event.setStyleName("color1");
        return event;
    }

    /* Removes the event from the data source and fires change event. */
    private void deleteCalendarEvent() {
        BasicEvent event = getFormCalendarEvent();
        if (dataSource.containsEvent(event)) {
            dataSource.removeEvent(event);
        }
        getMainWindow().removeWindow(scheduleEventPopup);
    }

    /* Adds/updates the event in the data source and fires change event. */
    private void commitCalendarEvent() {
        scheduleEventForm.commit();
        CalendarTestEvent event = getFormCalendarEvent();
        if (!dataSource.containsEvent(event)) {
        	CalendarTestEvent evento = new CalendarTestEvent();
        	evento.setIdUser(10168);
        	evento.setUsername("test");	
        	evento.setStartdate(  event.getStart());
        	evento.setEnddate( event.getEnd());
        	evento.setCaption(event.getCaption());
        	evento.setDescription(event.getDescription());
        	evento.setState("inserimento");
        	_log.info(evento.getStartdate());
        	
        
			Window.Notification n = new Window.Notification(
					"Valori inviati da scheduleEventForm",
					Window.Notification.TYPE_TRAY_NOTIFICATION);
			n.setPosition(Window.Notification.POSITION_CENTERED);
			n.setDescription("Evento inserito: <br />Caption: "
					+ event.getCaption() + "<br/>Description: "
					+ event.getDescription() + "<br/>stilename: "
					+ event.getStyleName() + "<br/>data start: "
					+ event.getStart() + "<br/>data end: "
					+ event.getEnd() + "<br/>data start Label: "
					+ new Label(new SimpleDateFormat("dd:MM:yyyy HH:mm")
					.format((Date) event.getStart())) + "<br/>data FORMATTATA: "
					+ event.getStart() + "<br/>Stato: "
					+ event.getState());
			
			getMainWindow().showNotification(n);
			
			try {
				 bd.insert(evento);
				_log.info("obj advice inserito________" + evento );
			} catch (AdviceException e) {
				_log.error(e);
				e.printStackTrace();
			}
            dataSource.addEvent(event);
        }

        getMainWindow().removeWindow(scheduleEventPopup);
    }

    private void discardCalendarEvent() {
        scheduleEventForm.discard();
        getMainWindow().removeWindow(scheduleEventPopup);
    }

    @SuppressWarnings("unchecked")
    private CalendarTestEvent getFormCalendarEvent() {
        BeanItem<CalendarTestEvent> item = (BeanItem<CalendarTestEvent>) scheduleEventForm
                .getItemDataSource();
        CalendarTestEvent event = item.getBean();
        return  event;
    }


    private void handleNextButtonClick() {
        switch (viewMode) {
        case MONTH:
            nextMonth();
            break;
        case WEEK:
            nextWeek();
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
        case WEEK:
            previousWeek();
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

    private void nextWeek() {
        rollWeek(1);
    }

    private void previousWeek() {
        rollWeek(-1);
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

    private void rollWeek(int direction) {
        calendar.add(GregorianCalendar.WEEK_OF_YEAR, direction);
        calendar.set(GregorianCalendar.DAY_OF_WEEK,
                calendar.getFirstDayOfWeek());
        resetCalendarTime(false);
        resetTime(true);
        calendar.add(GregorianCalendar.DATE, 6);
        calendarComponent.setEndDate(calendar.getTime());
    }

    private void rollDate(int direction) {
        calendar.add(GregorianCalendar.DATE, direction);
        resetCalendarTime(false);
        resetCalendarTime(true);
    }

    private void updateCaptionLabel() {
        DateFormatSymbols s = new DateFormatSymbols(getLocale());
        String month = s.getShortMonths()[calendar.get(GregorianCalendar.MONTH)];
        captionLabel.setValue(month + " "
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
     * Switch the view to week view.
     */
    public void switchToWeekView() {
        viewMode = Mode.WEEK;
        weekButton.setVisible(false);
        monthButton.setVisible(true);
    }

    /*
     * Switch the Calendar component's start and end date range to the target
     * month only. (sample range: 01.01.2010 00:00.000 - 31.01.2010 23:59.999)
     */
    public void switchToMonthView() {
        viewMode = Mode.MONTH;
        monthButton.setVisible(false);
        weekButton.setVisible(true);

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
        weekButton.setVisible(true);
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
		// recupero eventi utente
		try {
			_log.info("recupero numero messaggi in");
			elenco = bd.getEvent((String) ((User) getUser()).getScreenName());
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}
		
		Date originalDate = calendar.getTime();
		Date today = getToday();
		// Add a event that last a whole week
		Date start = calendarComponent.getFirstDateForWeek(today);
		Date end = calendarComponent.getLastDateForWeek(today);
        CalendarTestEvent event = getNewEvent("Da lista", start, end);
     /*  
		for (int i = 0; i < elenco.size(); i++) {
			calendar.add(GregorianCalendar.DATE, -3);
			calendar.set(GregorianCalendar.HOUR_OF_DAY, 9);
			calendar.set(GregorianCalendar.MINUTE, 30);
			start =  elenco.get(i).getStart();
			end = elenco.get(i).getEnd();
			calendar.add(GregorianCalendar.HOUR_OF_DAY, 5);
			calendar.set(GregorianCalendar.MINUTE, 0);
			event = getNewEvent("DA LISTA", start, end);
			event.setWhere("QUA CAZZO");
			event.setStyleName("color1");
			event.setDescription(elenco.get(i).getDescription());
			event.setCaption(elenco.get(i).getCaption());	
			event.setState(elenco.get(i).getState());	
			dataSource.addEvent(event);
		}
      */
        
		event = getNewEvent("Whole week event", start, end);
		start = calendarComponent.getFirstDateForWeek(today);
		end = calendarComponent.getLastDateForWeek(today);
        event.setAllDay(true);
        event.setStyleName("color4");
        event.setDescription("Description for the whole week event.");
        dataSource.addEvent(event);

        // Add a allday event
        calendar.setTime(start);
        calendar.add(GregorianCalendar.DATE, 3);
        start = calendar.getTime();
        end = start;
        event = getNewEvent("Allday event", start, end);
        event.setAllDay(true);
        event.setDescription("Some description.");
        event.setStyleName("color3");
        dataSource.addEvent(event);

        // Add a second allday event
        calendar.add(GregorianCalendar.DATE, 1);
        start = calendar.getTime();
        end = start;
        event = getNewEvent("Second allday event", start, end);
        event.setAllDay(true);
        event.setDescription("Some description.");
        event.setStyleName("color2");
        dataSource.addEvent(event);

        calendar.add(GregorianCalendar.DATE, -3);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 9);
        calendar.set(GregorianCalendar.MINUTE, 30);
        start = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR_OF_DAY, 5);
        calendar.set(GregorianCalendar.MINUTE, 0);
        end = calendar.getTime();
        event = getNewEvent("Appointment", start, end);
        event.setWhere("Office");
        event.setStyleName("color1");
        event.setDescription("A longer description, which should display correctly.");
        dataSource.addEvent(event);

        calendar.add(GregorianCalendar.DATE, 1);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 11);
        calendar.set(GregorianCalendar.MINUTE, 0);
        start = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR_OF_DAY, 8);
        end = calendar.getTime();
        event = getNewEvent("Training", start, end);
        event.setStyleName("color2");
        dataSource.addEvent(event);

        calendar.add(GregorianCalendar.DATE, 4);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 9);
        calendar.set(GregorianCalendar.MINUTE, 0);
        start = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR_OF_DAY, 9);
        end = calendar.getTime();
        event = getNewEvent("Free time", start, end);
        dataSource.addEvent(event);

        calendar.setTime(originalDate);
    }
  
	@Override
	public void onRequestStart(PortletRequest request, PortletResponse response) {
		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
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

	
}