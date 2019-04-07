package ita.social.component;

import ita.social.delegate.BusinessDelegate;
import ita.social.model.Message;
import ita.social.utils.AdviceException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.client.ui.AlignmentInfo.Bits;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings({ "serial", "deprecation" })
public class MessageBoard extends VerticalLayout {

	private static Log _log = LogFactoryUtil.getLog(MessageBoard.class);
	/** Business delegate per le chiamate al database */
	protected BusinessDelegate bd = new BusinessDelegate();
	private BeanItemContainer<Message> container;
	public static final Object[] COL_ORDER = new Object[] { "send", "date","receiver" };
	Table table;
	Message messaggio;
	final VerticalSplitPanel vert;
	final Label selected = new Label("Nessun Messaggio selezionato");

	//private ICEPush icepush = new ICEPush();
	
	public MessageBoard(Window main, List<Message> elenco) {

		// First a vertical SplitPanel
		vert = new VerticalSplitPanel();
		vert.setHeight("328px");
		vert.setWidth("298px");
		vert.setSplitPosition(130, Sizeable.UNITS_PIXELS);
		addComponent(vert);
		
		container = new BeanItemContainer<Message>(
				Message.class);
		for (Message o : elenco) {
			container.addBean(o);
		}

		table = new Table("Meggage");
		table.setContainerDataSource(container);
		// only process columns if container has items
		if (table.getContainerDataSource().size() == 0)
			return;
		table.setVisibleColumns(COL_ORDER);
		table.setSelectable(true);
		table.setNullSelectionAllowed(false);
		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		table.addGeneratedColumn("delete", new ColumnGenerator() {	
			@Override
			public Component generateCell(Table source, Object itemId, Object columnId) {
				Button button = new Button("Delete");
				button.setData(itemId);
				button.addListener(ClickEvent.class, MessageBoard.this, "delete");
				return button;
			}
		});
		table.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                messaggio = (Message) table.getValue();
        		_log.info("message.getDATA" + messaggio.getBody());
        		vert.setSecondComponent(new Label(messaggio.getBody()));
            }
        });
    

		vert.setFirstComponent(table);


	}


	
	public void delete(ClickEvent event) {
		Object itemId = event.getButton().getData();
		Message message = (Message) itemId;
		container.removeItem(itemId);
		
		try {
			 bd.delete(message);
			_log.info("messaggio eliminato");
		} catch (AdviceException e) {
			_log.error(e);
			e.printStackTrace();
		}
	}

}


class BodyLayout extends VerticalLayout {

	private static Log _log = LogFactoryUtil.getLog(BodyLayout.class);
	
    public BodyLayout(String description) {
        // Create a grid layout
        final GridLayout grid = new GridLayout(1, 3);
        grid.setSpacing(true);

        grid.addStyleName("gridexample");

        grid.setWidth("298px");
        grid.setHeight("180px");

        Label topleft = new Label(description);
        grid.addComponent(topleft);
        grid.setComponentAlignment(topleft, Alignment.TOP_LEFT);

        Label topcenter = new Label("Top Center");
        grid.addComponent(topcenter);
        grid.setComponentAlignment(topcenter, Alignment.TOP_CENTER);

        Label topright = new Label("Top Right");
        grid.addComponent(topright);
        grid.setComponentAlignment(topright, Alignment.TOP_RIGHT);


        // Add the layout to the containing layout.
        addComponent(grid);

        // Align the grid itself within its container layout.
        setComponentAlignment(grid, Alignment.MIDDLE_CENTER);

    }

}