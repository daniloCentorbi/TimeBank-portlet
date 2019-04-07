package ita.social.component;

import ita.social.delegate.BusinessDelegate;
import ita.social.model.Message;
import ita.social.utils.AdviceException;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class MessageWindow extends CustomComponent implements
		Window.CloseListener {

	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(MessageWindow.class);
	protected BusinessDelegate bd = new BusinessDelegate();
	Window mainwindow; // Reference to main window
	Window mywindow; // The window to be opened
	Button openbutton; // Button for opening the window
	//Label explanation; // A descriptive text
	Message message;
	final Form messageForm;

	public MessageWindow(String label, final Window main, String username, int idAdvice,
			String user) {
		mainwindow = main;

		// set values to send to the form
		final VerticalLayout layout = new VerticalLayout();
		message = new Message();
		message.setReceiver(username);
		message.setSend(user);
		message.setIdadvice(idAdvice);
		//message.setDate(date);
		openbutton = new Button("Invia Messaggio", this, "openButtonClick");
		//explanation = new Label(" A: " + username);
		layout.addComponent(openbutton);
		//layout.addComponent(explanation);

		setCompositionRoot(layout);

		// aggiungo il form
		BeanItem<Message> messageItem = new BeanItem<Message>(message);
		// the Form
		messageForm = new MessageFormLayout(messageItem,message);

		// apply buttons
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);

		Button apply = new Button("Invia", new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				try {
					messageForm.commit();
					Window.Notification n = new Window.Notification(
							"Valori inviati da adviceForm",
							Window.Notification.TYPE_TRAY_NOTIFICATION);
					n.setPosition(Window.Notification.POSITION_CENTERED);
					n.setDescription("Messaggio Privato inviato a:  "
							+ message.getReceiver() + "<br/>data: "
							+ message.getDate() + "<br/>per l' annuncio: "
							+ message.getIdadvice() + "<br/>corpo del messaggio: "
							+ message.getBody());
					main.showNotification(n);

					//explanation.setValue("Messaggio inviato");
					mainwindow.removeWindow(mywindow);
					openbutton.setEnabled(true);
					
					_log.debug("Salvo valori obj advice: " + message);


					try {
						_log.debug("inserisco obj advice: " + message);
						bd.insert(message);
						_log.debug("obj message inserito");
					} catch (AdviceException e) {
						_log.error(e);
						e.printStackTrace();
					}

				} catch (Exception e) {
					// Ingnored, we'll let the Form handle the errors
				}
			}
		});
		buttons.addComponent(apply);

		messageForm.getFooter().setMargin(true);
		messageForm.getFooter().addComponent(buttons);
	}

	/** Handle the clicks for the two buttons. */
	public void openButtonClick(Button.ClickEvent event) {
		mywindow = new Window("Invio Messaggio Privato");
		mywindow.center();
		mywindow.setModal(true);
		mywindow.setHeight("350px");
		mywindow.setWidth("500px");	
		mainwindow.addWindow(mywindow);
		/* Listen for close events for the window. */
		mywindow.addListener(this);
		/* Add form in the window. */
		mywindow.addComponent(messageForm);
		/* Allow opening only one window at a time. */
		openbutton.setEnabled(false);
		// text under button
		//explanation.setValue("In Composizione");
	}

	
	
	/** In case the window is closed otherwise. */
	public void windowClose(CloseEvent e) {
		/* Return to initial state. */
		openbutton.setEnabled(true);
		//explanation.setValue(" Messaggio NON inviato");
	}
}
