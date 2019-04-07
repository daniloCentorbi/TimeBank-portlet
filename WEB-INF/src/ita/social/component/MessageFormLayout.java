package ita.social.component;

import ita.social.model.Message;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.TextField;

//customizzo form
public class MessageFormLayout extends Form {
	private static final long serialVersionUID = 1L;
	private GridLayout ourLayout;

	public MessageFormLayout(BeanItem<Message> messageItem, Message message) {
		setCaption("Invia Messaggio Privato ");
		// Create our layout (3x3 GridLayout)
		ourLayout = new GridLayout(3, 3);
		// Use top-left margin and spacing
		ourLayout.setMargin(true, false, false, true);
		ourLayout.setSpacing(true);
		setLayout(ourLayout);

		// Set up buffering
		setWriteThrough(false); // we want explicit 'apply'
		setInvalidCommitted(false); // no invalid values in datamodel

		// FieldFactory for customizing the fields and adding validators
		setFormFieldFactory(new MessageFieldFactory(message));
		setItemDataSource(messageItem); // bind to POJO via BeanItem

		// Determines which properties are shown, and in which order:
		setVisibleItemProperties(Arrays.asList(new String[] { "send",
				"receiver", "date", "idadvice", "body" }));
	}

	/*
	 * Override to get control over where fields are placed.
	 */
	@Override
	protected void attachField(Object propertyId, Field field) {
		if (propertyId.equals("send")) {
			ourLayout.addComponent(field, 0, 0);
		} else if (propertyId.equals("receiver")) {
			ourLayout.addComponent(field, 1, 0, 2, 0);
		} else if (propertyId.equals("date")) {
			ourLayout.addComponent(field, 0, 2);
		} else if (propertyId.equals("idadvice")) {
			ourLayout.addComponent(field, 0, 1, 2, 1);
		} else if (propertyId.equals("body")) {
			ourLayout.addComponent(field, 1, 2);
		}
	}

}

class MessageFieldFactory extends DefaultFieldFactory {
	// creo i campi da liste
	private GridLayout ourLayout;
	private Message messagge;
	private static final String COMMON_FIELD_WIDTH = "12em";

	public MessageFieldFactory(Message message) {
		this.messagge = message;
	}

	@Override
	public Field createField(Item item, Object propertyId, Component uiContext) {
		Field f;
		// Use the super class to create a suitable field base on the
		// property type.
		f = super.createField(item, propertyId, uiContext);

		if ("send".equals(propertyId)) {
			TextField tf = (TextField) f;
			tf.setEnabled(false);
			tf.setCaption(messagge.getSend());
		} else if ("receiver".equals(propertyId)) {
			TextField tf = (TextField) f;
			tf.setEnabled(false);
			tf.setCaption(messagge.getReceiver());
		}
		if ("idadvice".equals(propertyId)) {
			TextField tf = (TextField) f;
			tf.setEnabled(false);
		} else if ("body".equals(propertyId)) {
			TextField tf = (TextField) f;
			tf.setRows(3);
			tf.setRequired(true);
			tf.setRequiredError("scrivi qualcosa");
			tf.setWidth(COMMON_FIELD_WIDTH);
			tf.addValidator(new StringLengthValidator(
					"Descrizione tra 3-250 characters", 3, 150, false));
		}else if ("date".equals(propertyId)) {
            DateField tf = (DateField) f;
                tf.setResolution(DateField.RESOLUTION_SEC);
		}
		return f;
	}

}