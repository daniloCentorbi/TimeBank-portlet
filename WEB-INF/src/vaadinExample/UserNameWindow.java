package vaadinExample;

import com.vaadin.event.ShortcutListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class UserNameWindow extends Window {

    private TextField name;

    public UserNameWindow() {
        super("Hello, who are you?");
        setSizeUndefined();
        getContent().setSizeUndefined();
        setModal(true);
        name = new TextField((String) null);
        name.addShortcutListener(new ShortcutListener(null, KeyCode.ENTER,
                        null) {

                    @Override
                    public void handleAction(Object sender, Object target) {
                        close();
                    }
                });
        addComponent(name);
    }

    public String getUserName() {
        if (name.getValue() == null
                || ((String) name.getValue()).trim().equals(""))
            return null;

        return ((String) name.getValue()).trim();
    }

    @Override
    public void attach() {
        super.attach();
        name.focus();
    }
}