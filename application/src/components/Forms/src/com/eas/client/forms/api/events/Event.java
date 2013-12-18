/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.events;

import com.eas.client.forms.api.Component;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptUtils;
import java.util.EventObject;
import javax.swing.JComponent;

/**
 *
 * @author mg
 */
public abstract class Event<E extends EventObject> {

    protected E delegate;

    protected Event(E aDelegate) {
        super();
        delegate = aDelegate;
    }

    private static final String SOURCE_JS_DOC = "/**\n"
            + "* The source component object of event propagation.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = SOURCE_JS_DOC)
    public Component<?> getSource() {
        Object oSource = delegate.getSource();
        if (oSource instanceof JComponent) {
            return lookupApiComponent((JComponent) oSource);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%s on %s", getClass().getSimpleName(), getSource() != null ? getSource().toString() : "");
    }

    protected static Component<?> lookupApiComponent(JComponent aComp) {
        JComponent comp = aComp;
        while (comp.getParent() != null && comp.getClientProperty(ScriptUtils.WRAPPER_PROP_NAME) == null) {
            comp = (JComponent) comp.getParent();
        }
        return (Component<?>)comp.getClientProperty(ScriptUtils.WRAPPER_PROP_NAME);
    }
}
