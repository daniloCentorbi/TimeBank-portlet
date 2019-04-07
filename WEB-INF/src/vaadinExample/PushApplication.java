package vaadinExample;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.vaadin.artur.icepush.ICEPush;

import com.vaadin.Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class PushApplication extends Application {

	private Thread thread = new Thread(new TimeRunnable());

	private Label label = new Label();

	private ICEPush icepush = new ICEPush();

	private CheckBox checkbox;

	@Override
	public void init() {

		Window window = new Window("Push Application");

		window.addComponent(icepush);

		checkbox = new CheckBox("Check to start");

		checkbox.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {

				boolean checked = (Boolean) event.getProperty().getValue();

				if (checked && !thread.isAlive()) {

					thread.start();
				}
			}
		});

		checkbox.setImmediate(true);

		window.addComponent(checkbox);

		window.addComponent(label);

		setMainWindow(window);

		changeLabel();
	}

	private class TimeRunnable implements Runnable {

		@Override
		public void run() {

			try {

				while (true) {

					if ((Boolean) checkbox.getValue()) {
					
						changeLabel();
	
						icepush.push();

					}

					Thread.sleep (1000);
				}

			} catch (InterruptedException e) {

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	private void changeLabel() {

		DateFormat format = new SimpleDateFormat("hh:mm:ss");

		String date = format.format(new Date());

		label.setValue(date);
	}
}