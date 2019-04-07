package ita.social.delegate;

import ita.social.manager.AdviceManager;
import ita.social.manager.EventManager;
import ita.social.manager.MessageManager;
import ita.social.model.Advice;
import ita.social.model.CalendarTestEvent;
import ita.social.model.Message;
import ita.social.utils.AdviceException;

import java.util.Date;
import java.util.List;

public class BusinessDelegate {

	//ADVICE
	public synchronized List<Advice> getAll(String username) throws AdviceException {
		return AdviceManager.getAll(username);
	}
	public synchronized List<Advice> get(int id) throws AdviceException {
		return AdviceManager.get(id);
	}	
	public synchronized List<Advice> getUser(String username) throws AdviceException {
		return AdviceManager.getUser(username);
	}
	public synchronized List<Advice> getOne(Advice advice) throws AdviceException{
		return AdviceManager.getOne(advice);
	}
	public synchronized List<Advice> getOneFromTitle(String title) throws AdviceException{
		return AdviceManager.getOneFromTitle(title);
	}
	public synchronized List<Advice> getOneOnly(int id) throws AdviceException{
		return AdviceManager.getOneOnly(id);
	}
	public synchronized Advice insert(Advice advice) throws AdviceException {
		return AdviceManager.insert(advice);
	}
	public synchronized int delete(Advice advice) throws AdviceException {
		return AdviceManager.delete(advice);
	}
	public synchronized Advice update(Advice advice) throws AdviceException {
		return AdviceManager.update(advice);
	}
	
	//EVENT
	public synchronized List<CalendarTestEvent> getEvent(String username) throws AdviceException {
		return EventManager.getEvent(username);
	}
	public synchronized List<CalendarTestEvent> getEventFromAdvice(Advice advice) throws AdviceException {
		return EventManager.getEventFromAdvice(advice);
	}
	public synchronized List<CalendarTestEvent> getSelectedEvent(CalendarTestEvent event) throws AdviceException {
		return EventManager.getSelectedEvent(event);
	}
	public synchronized List<CalendarTestEvent> getDeleteEvent(String username) throws AdviceException {
		return EventManager.getDeleteEvent(username);
	}
	public synchronized List<CalendarTestEvent> getEventFromUser(String tousername) throws AdviceException{
		return EventManager.getEventFromUser(tousername);
	}
	public synchronized CalendarTestEvent insert(CalendarTestEvent event) throws AdviceException {
		return EventManager.insert(event);
	}
	public synchronized int delete(CalendarTestEvent event) throws AdviceException {
		return EventManager.delete(event);
	}
	public synchronized CalendarTestEvent update(CalendarTestEvent event) throws AdviceException {
		return EventManager.update(event);
	}
	public synchronized CalendarTestEvent predelete(CalendarTestEvent event) throws AdviceException {
		return EventManager.predelete(event);
	}
	public synchronized CalendarTestEvent confirm(CalendarTestEvent event) throws AdviceException {
		return EventManager.confirm(event);
	}
	public synchronized int countAll() throws AdviceException {
		return EventManager.countAll();
	}
	public synchronized int countUser(String username) throws AdviceException {
		return EventManager.countUser(username);
	}
	public synchronized int countModifiedUser(String username) throws AdviceException {
		return EventManager.countModifiedUser(username);
	}
	public synchronized int countFromUser(String username) throws AdviceException {
		return EventManager.countFromUser(username);
	}
	public synchronized int countNewEvent(String username) throws AdviceException {
		return EventManager.countNewEvent(username);
	}
	public synchronized int countForAdvice(Advice advice) throws AdviceException {
		return EventManager.countForAdvice(advice);
	}	
	//FEEDBACK
	public synchronized List<CalendarTestEvent> getPassedEvent(String username) throws AdviceException {
		return EventManager.getPassedEvent(username);
	}	
	public synchronized int countPassed(String username) throws AdviceException{
		return EventManager.countPassed(username);
	}
	public synchronized List<CalendarTestEvent> getFeed(String username) throws AdviceException {
		return EventManager.getFeed(username);
	}	
	public synchronized List<CalendarTestEvent> getMyFeed(String username) throws AdviceException {
		return EventManager.getMyFeed(username);
	}
	public synchronized List<CalendarTestEvent> getUserFeed(String username) throws AdviceException {
		return EventManager.getUserFeed(username);
	}
	public synchronized int countFeed(Advice advice) throws AdviceException{
		return EventManager.countFeed(advice);
	}
	public synchronized int countAllFeed(Advice advice) throws AdviceException{
		return EventManager.countAllFeed(advice);
	}
	public synchronized CalendarTestEvent insertFeed(CalendarTestEvent event) throws AdviceException {
		return EventManager.insertFeed(event);
	}
	public synchronized int updateCredit(String username,String toUsername) throws AdviceException{
		return EventManager.updateCredit(username,toUsername);
	}
	
	//MESSAGE
	public synchronized List<Message> getIn(String username) throws AdviceException {
		return MessageManager.getIn(username);
	}
	public synchronized List<Message> getOut(String username) throws AdviceException {
		return MessageManager.getOut(username);
	}
	public synchronized List<Message> getAdvice(int idadvice) throws AdviceException {
		return MessageManager.getAdvice(idadvice);
	}	
	public synchronized Message insert(Message message) throws AdviceException {
		return MessageManager.insert(message);
	}	
	public synchronized int delete(Message message) throws AdviceException {
		return MessageManager.delete(message);
	}		
	public synchronized int countMessageForAdvice(int idadvice) throws AdviceException {
		return MessageManager.countMessageForAdvice(idadvice);
	}		
	public synchronized int countInmessage(String receiver) throws AdviceException {
		return MessageManager.countInmessage(receiver);
	}	
	public synchronized int countOutmessage(String send) throws AdviceException {
		return MessageManager.countOutmessage(send);
	}

}
