package vaadinExample;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vaadinExample.MultiusercalendarApplication;

import com.vaadin.addon.calendar.event.BasicEvent;
import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.event.CalendarEventProvider;

public class MUCEventProvider implements CalendarEventProvider {

	private static final long serialVersionUID = 1L;
	private static List<BasicEvent> events = new ArrayList<BasicEvent>();

    public interface EventUpdateListener {
        public void eventAdded(MultiusercalendarApplication app, MUCEvent event);

        public void eventRemoved(MultiusercalendarApplication app,
                MUCEvent event);

        public void eventMoved(MultiusercalendarApplication app, MUCEvent event);

        public void eventUpdated(MultiusercalendarApplication app,
                MUCEvent event);

        public void eventResized(MultiusercalendarApplication app,
                MUCEvent event);
    }

    private static List<EventUpdateListener> listeners = new ArrayList<EventUpdateListener>();
    private static List<EventSetChangeListener> eventSetChangeListeners = new ArrayList<EventSetChangeListener>();

    private MultiusercalendarApplication app;

    public MUCEventProvider(MultiusercalendarApplication app) {
        this.app = app;
        addListener(app);
    }

    private void addListener(EventUpdateListener listener) {
        synchronized (MUCEventProvider.class) {
            listeners.add(listener);
        }
    }

    public void addEvent(MUCEvent event) {
        synchronized (events) {
            events.add(event);
        }

        if (event.getPrivateEventOwner() == null)
            fireEventAdded(app, event);

    }

    public void removeEvent(MUCEvent event) {
        synchronized (events) {
            events.remove(event);
        }

        if (event.getPrivateEventOwner() == null)
            fireEventRemoved(app, event);
    }

    @Override
    public List<CalendarEvent> getEvents(Date from, Date to) {

        List<CalendarEvent> matchingEvents = new ArrayList<CalendarEvent>();
        Object[] available = events.toArray();
        for (Object o : available) {
            MUCEvent e = (MUCEvent) o;
            if (e.getStart().after(to) || e.getEnd().before(from))
                continue;

            // Show private events only to the owner
            if (e.getPrivateEventOwner() != null
                    && !e.getPrivateEventOwner().equals(app.toString())) {
                continue;
            }

            matchingEvents.add(e);
        }

        return matchingEvents;
    }

    public void fireEventUpdated(MultiusercalendarApplication app,
            BasicEvent event) {
        synchronized (MUCEventProvider.class) {
            for (EventUpdateListener l : listeners) {
                l.eventUpdated(app, (MUCEvent) event);
            }
        }
    }

    public void fireEventAdded(MultiusercalendarApplication app,
            BasicEvent event) {
        synchronized (MUCEventProvider.class) {
            for (EventUpdateListener l : listeners) {
                l.eventAdded(app, (MUCEvent) event);
            }
        }
    }

    public void fireEventRemoved(MultiusercalendarApplication app,
            BasicEvent event) {
        synchronized (MUCEventProvider.class) {
            for (EventUpdateListener l : listeners) {
                l.eventRemoved(app, (MUCEvent) event);
            }
        }
    }

    public void fireEventMoved(MultiusercalendarApplication app,
            BasicEvent event) {
        synchronized (MUCEventProvider.class) {
            for (EventUpdateListener l : listeners) {
                l.eventMoved(app, (MUCEvent) event);
            }
        }
    }

    public void fireEventResized(MultiusercalendarApplication app,
            BasicEvent event) {
        synchronized (MUCEventProvider.class) {
            for (EventUpdateListener l : listeners) {
                l.eventResized(app, (MUCEvent) event);
            }
        }
    }

    public void addedEvent(MUCEvent event) {
        if (event.getPrivateEventOwner() == null) {
            fireEventAdded(app, event);
        }
    }

    public void updatedEvent(MUCEvent event) {
        if (event.getPrivateEventOwner() == null) {
            fireEventUpdated(app, event);
        }
    }

    public void moveEvent(BasicEvent event, Date newStart) {
        long diff = newStart.getTime() - event.getStart().getTime();
        event.setStart(newStart);
        event.setEnd(new Date(event.getEnd().getTime() + diff));

        fireEventMoved(app, event);
    }

    public void resizeEvent(BasicEvent event, Date newStartTime, Date newEndTime) {
        event.setStart(newStartTime);
        event.setEnd(newEndTime);

        fireEventResized(app, event);

    }

}
