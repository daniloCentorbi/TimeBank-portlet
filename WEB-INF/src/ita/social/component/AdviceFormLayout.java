package ita.social.component;

import ita.social.model.Advice;

import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
 
// customizzo form
public class AdviceFormLayout extends Form {
	private GridLayout ourLayout;


	public AdviceFormLayout(BeanItem<Advice> adviceItem) {
		setCaption("Inserisci articolo");
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
		setFormFieldFactory(new AdviceFieldFactory());
		setItemDataSource(adviceItem); // bind to POJO via BeanItem

		// Determines which properties are shown, and in which order:
		setVisibleItemProperties(Arrays.asList(new String[] { "title",
				"tipo", "tipology", "date", "description" }));
	}

	/*
	 * Override to get control over where fields are placed.
	 */
	@Override
	protected void attachField(Object propertyId, Field field) {
		if (propertyId.equals("title")) {
			ourLayout.addComponent(field, 0, 0);
		} else if (propertyId.equals("tipo")) {
			ourLayout.addComponent(field,  2, 0);
		} else if (propertyId.equals("tipology")) {
			ourLayout.addComponent(field, 0, 2);
		} else if (propertyId.equals("date")) {
			ourLayout.addComponent(field, 0, 1);
		} else if (propertyId.equals("description")) {
			ourLayout.addComponent(field, 2, 1 ,2, 2);
		}
	}

}

 class AdviceFieldFactory extends DefaultFieldFactory {
	// creo i campi da liste
		private GridLayout ourLayout;
		private static final String COMMON_FIELD_WIDTH = "12em";
		private static final List<String> typeList = Arrays.asList(new String[] {
				"richiesta", "offerta" });
		private static final String[] tipologyList = new String[] {
				"comunicazione", "arte", "manutenzione", "istruzione",
				"assistenza", "trasporto", "cucina" , "altro"};
		
	final NativeSelect tipologySelect = new NativeSelect("Categoria");
	final OptionGroup typeSelect = new OptionGroup("",
			typeList);


	public AdviceFieldFactory() {
		//creo nativeBox categoria
		for (int i = 0; i < tipologyList.length; i++) {
			tipologySelect.addItem(tipologyList[i]);
		}
		//creo checkBox tipologia
		typeSelect.setNullSelectionAllowed(false); // user can not 'unselect'
		typeSelect.setValue("richiesta"); // select this by default
		typeSelect.setImmediate(true); // send the change to the server at once
	}

	@SuppressWarnings("deprecation")
	@Override
	public Field createField(Item item, Object propertyId,
			Component uiContext) {
		Field f;
		// formatto i campi
		if ("tipology".equals(propertyId)) {
			tipologySelect.setRequired(true);
			tipologySelect.setValue(tipologyList[7]);
			tipologySelect.setRequiredError("Seleziona categoria annuncio");
			tipologySelect.setWidth(COMMON_FIELD_WIDTH);
			return tipologySelect;
		} else if ("tipo".equals(propertyId)) {
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
			tf.setCaption("Titolo");
			tf.setRequired(true);
			tf.setRequiredError("Inserisci un titolo");
			tf.setWidth(COMMON_FIELD_WIDTH);
			tf.addValidator(new StringLengthValidator(
					"Titolo tra 3-50 caratteri", 3, 50, false));
		} else if ("description".equals(propertyId)) {
			TextField tf = (TextField) f;
			tf.setCaption("Descrizione");
			tf.setRows(3);
			tf.setRequired(true);
			tf.setRequiredError("Inserisci breve descrizione");
			tf.setWidth(COMMON_FIELD_WIDTH);
			tf.addValidator(new StringLengthValidator(
					"Descrizione tra 3-500 characters", 3, 500, false));
		}else if ("date".equals(propertyId)) {
			DateField tf = (DateField) f;
			tf.setCaption("Data");
			tf.setRequired(true);
			tf.setRequiredError("Inserisci la data");
			tf.setWidth(COMMON_FIELD_WIDTH);
			tf.setDateFormat("yyyy-MM-dd");
		}
		return f;
	}

}



