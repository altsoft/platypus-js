/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.models;

import com.eas.dbcontrols.grid.rt.columns.ModelColumn;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 * Table model, getting and setting data to an arbitrary rowset. Gets data from
 * an arbitrary rowset or from a script.
 *
 * @author mg
 */
public class RowsetsTableModel extends RowsetsModel implements TableModel {

    protected Set<TableModelListener> listeners = new HashSet<>();

    /**
     * Constructor, accepting a elements rowset.
     *
     * @param aColumns
     * @param aRows
     * @param aOnRender
     */
    public RowsetsTableModel(TableColumnModel aColumns, JSObject aRows, JSObject aOnRender) {
        super(aColumns, aRows, aOnRender);
        // TODO: move to ModelGrid.setData() when refactored events listening
        //rowsRowsetListener = new TabularRowsRowsetListener(this, rowsRowset);
        //rowsRowset.addRowsetListener(rowsRowsetListener);
    }

    @Override
    public int getRowCount() {
        return JSType.toInteger(elements.getMember("length"));
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex >= 0 && columnIndex < columns.getColumnCount()) {
            Object oElement = elements.getSlot(rowIndex);
            if (oElement instanceof JSObject) {
                return getValue((JSObject) oElement, columnIndex);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex >= 0 && columnIndex < columns.getColumnCount()) {
            Object oElement = elements.getSlot(rowIndex);
            if (oElement instanceof JSObject) {
                setValue((JSObject) oElement, columnIndex, aValue);
            }
        }
    }

    private void postEvent(TableModelEvent e) {
        listeners.stream().forEach((l) -> {
            l.tableChanged(e);
        });
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    public void fireElementDeleted(int aRowIndex) {
        TableModelEvent e = new TableModelEvent(this, aRowIndex, aRowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
        postEvent(e);
    }

    public void fireElementInserted(int aRowIndex) {
        TableModelEvent e = new TableModelEvent(this, aRowIndex, aRowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
        postEvent(e);
    }

    /**
     * Fires an event, that tells all listeners that elements structure has been
     * changed.
     */
    public void fireElementsChanged() {
        TableModelEvent e = new TableModelEvent(this, TableModelEvent.HEADER_ROW);
        postEvent(e);
    }

    /**
     * Fires an event, that tells all listeners that data in all elements have
     * been changed, but elements structure havn't been changed.
     */
    @Override
    public void fireElementsDataChanged() {
        TableModelEvent e = new TableModelEvent(this, 0, getRowCount() - 1, TableModelEvent.ALL_COLUMNS);
        postEvent(e);
    }

    public void fireElementsFieldChanged(String aElementsField) {
        for (int i = 0; i < columns.getColumnCount(); i++) {
            ModelColumn col = (ModelColumn) columns.getColumn(i);
            if (col.getField() == null ? aElementsField == null : col.getField().equals(aElementsField)) {
                int modelColumnIndex = col.getModelIndex();
                if (modelColumnIndex != -1) {
                    TableModelEvent e = new TableModelEvent(this, 0, getRowCount() - 1, modelColumnIndex);
                    postEvent(e);
                }
            }
        }
    }

    /**
     * Fires an event, occuring when data or rowset structure of column's rowset
     * is changed. It takes place when column's rowset and elements rowset are
     * not same.
     *
     * @param aColumn ModelColumn instance, the change is related to.
     * @see ModelColumn
     */
    @Override
    public void fireColumnFieldChanged(ModelColumn aColumn) {
        int colIndex = aColumn.getModelIndex();
        if (colIndex != -1) {
            TableModelEvent e = new TableModelEvent(this, 0, getRowCount() - 1, colIndex);
            postEvent(e);
        }
    }
}
