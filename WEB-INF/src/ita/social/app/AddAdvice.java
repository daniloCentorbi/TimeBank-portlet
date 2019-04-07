package ita.social.app;

import ita.social.component.AdviceFormLayout;
import ita.social.delegate.BusinessDelegate;
import ita.social.model.Advice;
import ita.social.utils.AdviceException;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.vaadin.Application;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

public class AddAdvice extends CustomComponent {
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(AddAdvice.class);
	/** Business delegate per le chiamate al database */
	Advice advice;
	Window window = new Window("Aggiungi Annuncio");
	protected BusinessDelegate bd = new BusinessDelegate();

	public AddAdvice(Advice objAdvice) {
		// advice obj
		advice = objAdvice;
		BeanItem<Advice> adviceItem = new BeanItem<Advice>(advice);
		// the Form
		final Form adviceForm = new AdviceFormLayout(adviceItem);
		window.addComponent(adviceForm);

		// apply buttons
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);

		Button apply = new Button("Salva", new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				try {
					adviceForm.commit();					
					Window.Notification n = new Window.Notification(
							"Valori inviati da adviceForm",
							Window.Notification.TYPE_TRAY_NOTIFICATION);
					n.setPosition(Window.Notification.POSITION_CENTERED);
					n.setDescription("Annuncio inserito: <br />Titolo: "
							+ advice.getTitle() + "<br/>categoria: "
							+ advice.getTipology() + "<br/>tipo avviso: "
							+ advice.getTipo() + "<br/>data: "
							+ advice.getDate() + "<br/>descrizione: "
							+ advice.getDescription());
					window.showNotification(n);

					// richiamo insert
					try {
						 bd.insert(advice);
						_log.info("obj advice inserito" );
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

		adviceForm.getFooter().setMargin(true);
		adviceForm.getFooter().addComponent(buttons);
	}

}
