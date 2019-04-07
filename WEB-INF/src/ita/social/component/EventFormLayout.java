package ita.social.component;

import ita.social.model.Advice;

import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;
 
// customizzo form
public class EventFormLayout extends Form {

	private static final long serialVersionUID = 1L;
	private GridLayout ourLayout;


	public EventFormLayout(BeanItem<Event> adviceItem) {
		setCaption("Inserisci evento");
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
		setFormFieldFactory(new EventFieldFactory());
		setItemDataSource(adviceItem); // bind to POJO via BeanItem

		// Determines which properties are shown, and in which order:
		setVisibleItemProperties(Arrays.asList(new String[] { "title",
				"type", "tipology", "date", "description" }));
	}

	/*
	 * Override to get control over where fields are placed.
	 */
	@Override
	protected void attachField(Object propertyId, Field field) {
		if (propertyId.equals("title")) {
			ourLayout.addComponent(field, 0, 0);
		} else if (propertyId.equals("type")) {
			ourLayout.addComponent(field, 1, 0, 2, 0);
		} else if (propertyId.equals("tipology")) {
			ourLayout.addComponent(field, 0, 2);
		} else if (propertyId.equals("date")) {
			ourLayout.addComponent(field, 0, 1, 2, 1);
		} else if (propertyId.equals("description")) {
			ourLayout.addComponent(field, 1, 2);
		}
	}

}

 class EventFieldFactory extends DefaultFieldFactory {
	// creo i campi da liste
		private GridLayout ourLayout;
		private static final String COMMON_FIELD_WIDTH = "12em";
		private static final List<String> typeList = Arrays.asList(new String[] {
				"richiesta", "offerta" });
		private static final String[] tipologyList = new String[] {
				"comunicazione", "arte", "manutenzione", "istruzione",
				"assistenza", "trasporto", "cucina" };
		
	final ComboBox tipologySelect = new ComboBox("Categoria");
	final ListSelect typeSelect = new ListSelect("Tipo di annuncio",
			typeList);

	public EventFieldFactory() {
		for (int i = 0; i < tipologyList.length; i++) {
			tipologySelect.addItem(tipologyList[i]);
		}
		typeSelect.setRows(2); // perfect length in out case
		typeSelect.setNullSelectionAllowed(false); // user can not
													// 'unselect'
		typeSelect.select("offerta"); // select this by default
	}

	@Override
	public Field createField(Item item, Object propertyId,
			Component uiContext) {
		Field f;
		// formatto i campi
		if ("tipology".equals(propertyId)) {
			tipologySelect.setRequired(true);
			tipologySelect.setRequiredError("Seleziona la categoria");
			tipologySelect.setInputPrompt("Seleziona una categoria");
			tipologySelect.setWidth(COMMON_FIELD_WIDTH);
			return tipologySelect;
		} else if ("type".equals(propertyId)) {
			typeSelect.setRequired(true);
			typeSelect.setRequiredError("Inserisci tipologia annuncio");
			typeSelect.setWidth(COMMON_FIELD_WIDTH);
			return typeSelect;
		} else {
			// Use the super class to create a suitable field base on the
			// property type.
			f = super.createField(item, propertyId, uiContext);
		}
		if ("title".equals(propertyId)) {
			TextField tf = (TextField) f;
			tf.setRequired(true);
			tf.setRequiredError("Inserisci un titolo");
			tf.setWidth(COMMON_FIELD_WIDTH);
			tf.addValidator(new StringLengthValidator(
					"Titolo tra 3-25 caratteri", 3, 25, false));
		} else if ("descrizione".equals(propertyId)) {
			TextField tf = (TextField) f;
			tf.setRequired(true);
			tf.setRequiredError("Inserisci la descrizione");
			tf.setWidth(COMMON_FIELD_WIDTH);
			tf.addValidator(new StringLengthValidator(
					"Descrizione tra 3-150 characters", 3, 150, false));
		}
		return f;
	}

}
