/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.datamodel.nodes;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.model.Entity;
import com.eas.client.model.application.ApplicationParametersEntity;
import com.eas.client.model.gui.DatamodelDesignUtils;
import com.eas.client.model.gui.edits.fields.ReorderFieldsEdit;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.designer.datamodel.ModelUndoProvider;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.event.UndoableEditEvent;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.PasteAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.RenameAction;
import org.openide.awt.UndoRedo;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author mg
 */
public class EntityNode<E extends Entity<?, ?, E>> extends AbstractNode implements PropertyChangeListener {

    public static final String PROPS_EVENTS_TAB_NAME = "tabName";
    public static final String NAME_PROP_NAME = "name";
    public static final String TITLE_PROP_NAME = "title";
    protected Map<String, Node.Property<String>> nameToProperty = new HashMap<>();
    protected E entity;
    protected Action[] actions;
    protected ShowEntityAction defaultAction;

    public EntityNode(E aEntity, UndoRedo.Manager aUndoReciever, Children children, Lookup aLookup) throws Exception {
        super(children, aLookup);
        entity = aEntity;
        entity.getChangeSupport().addPropertyChangeListener(this);
        defaultAction = SystemAction.get(ShowEntityAction.class);
    }

    @Override
    public Action getPreferredAction() {
        return defaultAction;
    }

    @Override
    public Action[] getActions(boolean context) {
        if (actions == null) {
            List<Action> lactions = new ArrayList<>(20);
            fillActions(lactions);
            actions = new Action[lactions.size()];
            lactions.toArray(actions);
        }
        return actions;
    }

    protected void fillActions(List<Action> aList) {
        aList.add(defaultAction);
        if (!isParametersEntity()) {
            aList.add(null);
            aList.add(SystemAction.get(RenameAction.class));
            aList.add(null);
            aList.add(SystemAction.get(CutAction.class));
            aList.add(SystemAction.get(CopyAction.class));
            aList.add(SystemAction.get(PasteAction.class));
            aList.add(null);
            aList.add(SystemAction.get(DeleteAction.class));
            aList.add(PropertiesAction.get(PropertiesAction.class));
        }
    }

    @Override
    public String getName() {
        String eName = entity.getName();
        return eName != null ? eName : "";
    }

    @Override
    public String getDisplayName() {
        if (isParametersEntity()) {
            return DatamodelDesignUtils.getLocalizedString("Parameters");
        } else {
            return entity.getTitle();
        }
    }

    @Override
    public void setDisplayName(String s) {
        entity.setTitle(s);
    }

    @Override
    public String getHtmlDisplayName() {
        String displayName = getDisplayName();
        String name = getName();
        if (name != null && !name.isEmpty()) {
            return String.format("<html><b>%s</b> [%s]", name, displayName);
        } else {
            return String.format("<html>%s", displayName);
        }
    }

    public E getEntity() {
        return entity;
    }

    public Node[] fieldsToNodes(Set<? extends Field> fields) {
        Set<Node> convertedNodes = new HashSet<>();
        if (fields != null) {
            Map<Field, Node> entitiesToNodes = new HashMap<>();
            for (Node node : getChildren().getNodes()) {
                if (node instanceof FieldNode) {
                    entitiesToNodes.put(((FieldNode) node).getField(), node);
                }
            }
            for (Field field : fields) {
                convertedNodes.add(entitiesToNodes.get(field));
            }
        }
        return convertedNodes.toArray(new Node[0]);
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.icon2Image(EntityView.initViewIcon(entity));
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        PropertyChangeEvent nodeEvent = convertPropertyChangeEventToNode(evt);
        if (nodeEvent != null) {
            processNodePropertyChange(nodeEvent);
        }
    }

    public PropertyChangeEvent convertPropertyChangeEventToNode(PropertyChangeEvent evt) {
        if (nameToProperty.containsKey(evt.getPropertyName())) {
            return new PropertyChangeEvent(this, evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        }
        return null;
    }

    public void processNodePropertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case TITLE_PROP_NAME:
                firePropertyChange(TITLE_PROP_NAME, evt.getOldValue(), evt.getNewValue());
                fireDisplayNameChange((String) evt.getOldValue(), (String) evt.getNewValue());
                break;
            case NAME_PROP_NAME:
                fireNameChange((String) evt.getOldValue(), (String) evt.getNewValue());
                fireDisplayNameChange(null, getDisplayName());
                break;
        }
        firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }

    @Override
    public boolean canRename() {
        return true;
    }

    @Override
    public boolean canDestroy() {
        return !isParametersEntity();
    }

    protected boolean isParametersEntity() {
        return entity instanceof ApplicationParametersEntity
                || entity instanceof QueryParametersEntity;
    }

    @Override
    protected Sheet createSheet() {
        try {
            Sheet sheet = new Sheet();
            Sheet.Set pSet = Sheet.createPropertiesSet();
            pSet.setValue(PROPS_EVENTS_TAB_NAME, pSet.getDisplayName());
            sheet.put(pSet);
            if (!isParametersEntity()) {
                PropertySupport.Name nameProp = new PropertySupport.Name(this, NAME_PROP_NAME, "");
                pSet.put(nameProp);
                nameToProperty.put(NAME_PROP_NAME, nameProp);
                PropertySupport.Reflection<String> titleProp = new PropertySupport.Reflection<>(entity, String.class, TITLE_PROP_NAME);
                titleProp.setName(TITLE_PROP_NAME);
                pSet.put(titleProp);
                nameToProperty.put(TITLE_PROP_NAME, titleProp);
            }
            return sheet;
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(EntityNode.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return super.createSheet();
        }
    }

    public void reorder(int[] order) {
        ReorderFieldsEdit<E> edit = new ReorderFieldsEdit<>(entity, order);
        edit.redo();
        getUndo().undoableEditHappened(new UndoableEditEvent(this, edit));
    }

    public Property<String> getPropertyByName(String name) {
        return nameToProperty.get(name);
    }

    protected UndoRedo.Manager getUndo() {
        return getLookup().lookup(ModelUndoProvider.class).getModelUndo();
    }
}
