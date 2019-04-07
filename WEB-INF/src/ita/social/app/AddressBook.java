package ita.social.app;

import java.util.List;

import ita.social.component.MessageBoard;
import ita.social.delegate.BusinessDelegate;
import ita.social.model.Advice;
import ita.social.model.Message;
import ita.social.utils.AdviceException;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class AddressBook extends Application {
	
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(AddressBook.class);
	private static String[] fields = { "First Name", "Last Name", "Company",
            "Mobile Phone", "Work Phone", "Home Phone", "Work Email",
            "Home Email", "Street", "Zip", "City", "State", "Country" };
    private static String[] visibleCols = new String[] { "Last Name",
            "First Name", "Company" };

    private Table contactList = new Table();
    private Form contactEditor = new Form();
    private HorizontalLayout bottomLeftCorner = new HorizontalLayout();
    private Button contactRemovalButton;
    private IndexedContainer addressBookData = createDummyData();

	BeanItemContainer<Advice> container = new BeanItemContainer<Advice>(
			Advice.class);
	protected BusinessDelegate bd = new BusinessDelegate();
	List<Advice> elenco;
	
    @Override
    public void init() {
        initLayout();
        initContactAddRemoveButtons();
        initAddressList();
        initFilteringControls();
    }

    private void initLayout() {
        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.setSizeFull();
        Window main = new Window("Address Book");
        main.setSizeFull();
        main.addComponent(splitPanel);
        setMainWindow(main);
        
        VerticalLayout left = new VerticalLayout();
        left.addComponent(contactList);
        contactList.setHeight("500px");
		left.setWidth("300px");
        left.setExpandRatio(contactList, 1);
        //add table
        splitPanel.addComponent(left);
        //add form
        splitPanel.addComponent(contactEditor);
        contactEditor.setCaption("Contact details editor");
        contactEditor.getLayout().setMargin(true);
        contactEditor.setImmediate(true);
        bottomLeftCorner.setWidth("100%");
        left.addComponent(bottomLeftCorner);
    }

    private void initContactAddRemoveButtons() {
        // New item button
        bottomLeftCorner.addComponent(new Button("+",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        // Add new contact "John Doe" as the first item
                        Object id = ((IndexedContainer) contactList
                              .getContainerDataSource()).addItemAt(0);
                        contactList.getItem(id).getItemProperty("First Name")
                              .setValue("dioporco");
                        contactList.getItem(id).getItemProperty("Last Name")
                              .setValue("");


                        _log.info("_____" + contactList.getContainerPropertyIds());
        				_log.info("aggiungo____" + contactList.getValue());
                		_log.info("-------" + contactList.getItem(id).getItemProperty("First Name").getValue());
                        // Select the newly added item and scroll to the item
                        contactList.setValue(id);
                        contactList.setCurrentPageFirstItemId(id);
                    }
                }));

        // Remove item button
        contactRemovalButton = new Button("-", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
				_log.info("elimino____" + contactList.getValue());
                contactList.removeItem(contactList.getValue());
                contactList.select(null);
            }
        });
        contactRemovalButton.setVisible(false);
        bottomLeftCorner.addComponent(contactRemovalButton);
    }

    private void initAddressList() {
        contactList.setContainerDataSource(addressBookData);
        contactList.setVisibleColumns(visibleCols);
        contactList.setSelectable(true);
        contactList.setImmediate(true);
        contactList.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                Object id = contactList.getValue();
                contactEditor.setItemDataSource(id == null ? null : contactList
                        .getItem(id));
                contactRemovalButton.setVisible(id != null);
            }
        });
    }

    private void initFilteringControls() {
        for (final String pn : visibleCols) {
            final TextField sf = new TextField();
            bottomLeftCorner.addComponent(sf);
            sf.setWidth("100%");
            sf.setInputPrompt(pn);
            sf.setImmediate(true);
            bottomLeftCorner.setExpandRatio(sf, 1);
            sf.addListener(new Property.ValueChangeListener() {
                public void valueChange(ValueChangeEvent event) {
                    addressBookData.removeContainerFilters(pn);
                    if (sf.toString().length() > 0 && !pn.equals(sf.toString())) {
                        addressBookData.addContainerFilter(pn, sf.toString(),
                                true, false);
                    }
                    getMainWindow().showNotification(
                            "" + addressBookData.size() + " matches found");
                }
            });
        }
    }

    private static IndexedContainer createDummyData() {

        String[] fnames = { "Peter", "Alice", "Joshua", "Mike", "Olivia",
                "Nina", "Alex", "Rita", "Dan", "Umberto", "Henrik", "Rene",
                "Lisa", "Marge" };
        String[] lnames = { "Smith", "Gordon", "Simpson", "Brown", "Clavel",
                "Simons", "Verne", "Scott", "Allison", "Gates", "Rowling",
                "Barks", "Ross", "Schneider", "Tate" };

        IndexedContainer ic = new IndexedContainer();

        for (String p : fields) {
            ic.addContainerProperty(p, String.class, "");
        }

        // Create dummy data by randomly combining first and last names
        for (int i = 0; i < 1000; i++) {
            Object id = ic.addItem();
            ic.getContainerProperty(id, "First Name").setValue(
                    fnames[(int) (fnames.length * Math.random())]);
            ic.getContainerProperty(id, "Last Name").setValue(
                    lnames[(int) (lnames.length * Math.random())]);
        }

        return ic;
    }

    class NavigationTree extends Tree {
    	public final Object SHOW_ALL = "Show all";
    	public final Object SEARCH = "Search";

		
    	public NavigationTree() {
    		// Recupero elenco annunci
    		try {
    			_log.debug("richiamo busines-------------()");
    			elenco = bd.get((int) ((User) getUser()).getUserId());
    			_log.debug("Ho l' elenco" + elenco);
    		} catch (AdviceException e) {
    			_log.error(e);
    			e.printStackTrace();
    		}
    		
    		for (Advice o : elenco) {
    			container.addBean(o);
    		}
    		setContainerDataSource(container);

    	}
    }
    
}