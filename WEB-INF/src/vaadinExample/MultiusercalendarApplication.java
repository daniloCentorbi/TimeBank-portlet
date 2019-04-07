package vaadinExample;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

import java.util.Date;

import org.vaadin.artur.icepush.ICEPush;
import vaadinExample.MUCEvent;
import vaadinExample.MUCEventProvider;
import vaadinExample.MUCEventProvider.EventUpdateListener;

import com.vaadin.Application;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClickHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventMoveHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResize;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResizeHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.MoveEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.RangeSelectEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.RangeSelectHandler;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.Notification;

public class MultiusercalendarApplication extends Application implements
        EventUpdateListener, RangeSelectHandler, EventMoveHandler,
        EventClickHandler, EventResizeHandler {

	private static final long serialVersionUID = 1L;
	private static final int DAY_IN_MS = 1000 * 3600 * 24;
    private Calendar calendar;
    private ICEPush push;
    private static String[] eventStyles = new String[] { "green", "blue",
            "red", "yellow", "white", "cyan", "magenta" };
    private static int styleNr = 0;
    private MUCEventProvider eventProvider;

    private String eventStyle;
    protected String userName;

    @Override
    public void init() {
        eventStyle = eventStyles[styleNr++ % eventStyles.length];

        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("500px");
		layout.setWidth("100%");
        Window mainWindow = new Window("Multi-user Calendar demo", layout);

        setMainWindow(mainWindow);
        setTheme("multiusercalendartheme");

        Calendar cal = constructUI();
        layout.addComponent(cal);
        layout.setExpandRatio(cal, 1);
        push = new ICEPush();
 
        
        layout.addComponent(push);
        layout.addComponent(cal);

        
        final UserNameWindow userNameWindow = new UserNameWindow();
        userNameWindow.addListener(new CloseListener() {

            @Override
            public void windowClose(CloseEvent e) {
                if (userNameWindow.getUserName() == null) {
                    getMainWindow().showNotification("Come on...");
                    getMainWindow().addWindow(userNameWindow);
                } else {
                    userName = userNameWindow.getUserName();
                }
            }
        });

        getMainWindow().addWindow(userNameWindow);
        
        Button monthButton = new Button("View month");
        monthButton.addListener(new Button.ClickListener() {

            private static final long serialVersionUID = 4630911817907732542L;

            public void buttonClick(ClickEvent event) {
                java.util.Calendar internalCalendar = calendar
                        .getInternalCalendar();
                internalCalendar.setTime(calendar.getStartDate());
                internalCalendar.set(java.util.Calendar.DATE, 1);
                Date start = internalCalendar.getTime();
                internalCalendar.set(java.util.Calendar.DATE, internalCalendar
                        .getMaximum(java.util.Calendar.DATE));
                Date end = internalCalendar.getTime();

                calendar.setStartDate(start);
                calendar.setEndDate(end);
            }
        });
        
        Button weekButton = new Button("View week");
        monthButton.addListener(new Button.ClickListener() {

            private static final long serialVersionUID = 4630911817807732542L;

            public void buttonClick(ClickEvent event) {
                java.util.Calendar internalCalendar = calendar
                        .getInternalCalendar();
                internalCalendar.setTime(calendar.getStartDate());
                internalCalendar.set(java.util.Calendar.DATE, 1);
                Date start = internalCalendar.getTime();
                internalCalendar.set(java.util.Calendar.DATE, internalCalendar
                        .getMaximum(java.util.Calendar.DATE));
                Date end = internalCalendar.getTime();

                calendar.setStartDate(start);
                calendar.setEndDate(end);
            }
        });
        layout.addComponent(monthButton);
    }

    private Calendar constructUI() {
        eventProvider = new MUCEventProvider(this);
        calendar = new Calendar(eventProvider);
        calendar.setHeight("500px");
        calendar.setWidth("100%");
        calendar.setImmediate(true);

        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(new Date());
        c.set(java.util.Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        Date start = c.getTime();
        calendar.setStartDate(start);
        Date end = new Date(start.getTime() + DAY_IN_MS * 6);
        calendar.setEndDate(end);
        //calendar.setHideWeekends(false);
        calendar.setHandler((RangeSelectHandler) this);
        calendar.setHandler((EventMoveHandler) this);
        calendar.setHandler((EventClickHandler) this);
        calendar.setHandler((EventResizeHandler) this);

        return calendar;
    }

    @Override
    public void eventAdded(MultiusercalendarApplication app, MUCEvent event) {
        calendar.requestRepaint();
        if (app != this) {
            getMainWindow().showNotification(
                    app.userName + " added event \"" + event.getCaption()
                            + "\" at " + event.getTime(),
                    Notification.TYPE_TRAY_NOTIFICATION);
        }
        push.push();
    }

    @Override
    public void eventUpdated(MultiusercalendarApplication app, MUCEvent event) {
        calendar.requestRepaint();
        if (app != this) {
            getMainWindow().showNotification(
                    app.userName + " updated event \"" + event.getCaption()
                            + "\" at " + event.getTime(),
                    Notification.TYPE_TRAY_NOTIFICATION);
        }
        push.push();
    }

    @Override
    public void eventRemoved(MultiusercalendarApplication app, MUCEvent event) {
        calendar.requestRepaint();
        if (app != this) {
            getMainWindow().showNotification(
                    app.userName + " removed event \"" + event.getCaption()
                            + "\" (" + event.getTime() + ")",
                    Notification.TYPE_TRAY_NOTIFICATION);
        }
        push.push();
    }

    @Override
    public void eventMoved(MultiusercalendarApplication app, MUCEvent event) {
        calendar.requestRepaint();
        if (app != this) {
            getMainWindow().showNotification(
                    app.userName + " moved event \"" + event.getCaption()
                            + "\" to " + event.getTime(),
                    Notification.TYPE_TRAY_NOTIFICATION);
        }
        push.push();
    }

    @Override
    public void eventResized(MultiusercalendarApplication app, MUCEvent event) {
        calendar.requestRepaint();

        if (app != this) {
            getMainWindow().showNotification(
                    app.userName + " changed event \"" + event.getCaption()
                            + "\" to " + event.getTime(),
                    Notification.TYPE_TRAY_NOTIFICATION);
        }
        push.push();
    }

    @Override
    public void rangeSelect(RangeSelectEvent e) {
        final MUCEvent event = new MUCEvent();
        event.setPrivateEventOwner(toString());
        event.setCaption("");
        event.setStart(e.getStart());
        event.setEnd(e.getEnd());
        event.setStyleName(eventStyle);
        eventProvider.addEvent(event);
        calendar.requestRepaint();

        final EditPopup editPopup = new EditPopup(event);
        editPopup.addListener(new CloseListener() {

            @Override
            public void windowClose(CloseEvent e) {
                if (editPopup.isOk()) {
                    event.setCaption(editPopup.getEventCaption());
                    event.setPrivateEventOwner(null);
                    eventProvider.addedEvent(event);
                } else {
                    eventProvider.removeEvent(event);
                    calendar.requestRepaint();
                }
            }
        });
        getMainWindow().addWindow(editPopup);
    }

    @Override
    public void eventMove(MoveEvent e) {
        MUCEvent event = (MUCEvent) e.getCalendarEvent();
        eventProvider.moveEvent(event, e.getNewStart());
    }

    @Override
    public void eventClick(EventClick arg0) {
        final MUCEvent event = (MUCEvent) arg0.getCalendarEvent();
        final EditPopup editPopup = new EditPopup(event);
        editPopup.addListener(new CloseListener() {

            @Override
            public void windowClose(CloseEvent e) {
                if (editPopup.isOk()) {
                    event.setCaption(editPopup.getEventCaption());
                    event.setPrivateEventOwner(null);
                    eventProvider.updatedEvent(event);
                }
            }
        });
        getMainWindow().addWindow(editPopup);
    }

    @Override
    public void eventResize(EventResize e) {
        MUCEvent event = (MUCEvent) e.getCalendarEvent();
        eventProvider
                .resizeEvent(event, e.getNewStartTime(), e.getNewEndTime());

    }

}
