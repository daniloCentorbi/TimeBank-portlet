package ita.social.component;

import ita.social.app.AddAdvice;
import ita.social.app.TreeTableAdvice;
import ita.social.delegate.BusinessDelegate;
import ita.social.model.Advice;
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

public class AdviceWindow extends CustomComponent implements
		Window.CloseListener {
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(AddAdvice.class);
	protected BusinessDelegate bd = new BusinessDelegate();
	private Advice advice;
	private TreeTableAdvice application;

	public AdviceWindow(String tipo, TreeTableAdvice app,
			final Window subwindow, Advice objAdvice) {
		// advice obj passato da principale
		advice = objAdvice;
		application = app;
		BeanItem<Advice> adviceItem = new BeanItem<Advice>(advice);
		// the Form
		final Form adviceForm = new AdviceFormLayout(adviceItem);
		setCompositionRoot(adviceForm);

		// apply buttons
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);

		if (tipo.equals("nuovo")) {
			Button apply = new Button("Salva", new Button.ClickListener() {
				private static final long serialVersionUID = 1L;

				public void buttonClick(ClickEvent event) {
					try {
						adviceForm.commit();
						// richiamo insert
						try {
							bd.insert(advice);
							_log.info("obj advice inserito");
						} catch (AdviceException e) {
							_log.error(e);
							e.printStackTrace();
						}
						(subwindow.getParent()).removeWindow(subwindow);
						application.update();
					} catch (Exception e) {
						// Ingnored, we'll let the Form handle the errors
					}
				}
			});
			buttons.addComponent(apply);
		} else if (tipo.equals("modifica")) {
			Button apply = new Button("Salva", new Button.ClickListener() {
				private static final long serialVersionUID = 1L;

				public void buttonClick(ClickEvent event) {
					try {
						adviceForm.commit();
						// richiamo update
						try {
							bd.update(advice);
							_log.info("obj advice modificato");
						} catch (AdviceException e) {
							_log.error(e);
							e.printStackTrace();
						}

						(subwindow.getParent()).removeWindow(subwindow);
						application.update();
					} catch (Exception e) {
						// Ingnored, we'll let the Form handle the errors
					}
				}
			});
			buttons.addComponent(apply);
		}

		adviceForm.getFooter().setMargin(true);
		adviceForm.getFooter().addComponent(buttons);
	}

	@Override
	public void windowClose(CloseEvent e) {
		// TODO Auto-generated method stub

	}

}
