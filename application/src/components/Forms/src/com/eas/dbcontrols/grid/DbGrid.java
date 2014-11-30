package com.eas.dbcontrols.grid;

import com.eas.client.forms.IconCache;
import com.bearsoft.gui.grid.columns.ConstrainedColumnModel;
import com.bearsoft.gui.grid.constraints.LinearConstraint;
import com.bearsoft.gui.grid.data.CachingTableModel;
import com.bearsoft.gui.grid.data.CellData;
import com.bearsoft.gui.grid.data.TableFront2TreedModel;
import com.bearsoft.gui.grid.editing.InsettedTreeEditor;
import com.bearsoft.gui.grid.header.GridColumnsGroup;
import com.bearsoft.gui.grid.header.MultiLevelHeader;
import com.bearsoft.gui.grid.rendering.InsettedTreeRenderer;
import com.bearsoft.gui.grid.rendering.TreeColumnLeadingComponent;
import com.bearsoft.gui.grid.rows.ConstrainedRowSorter;
import com.bearsoft.gui.grid.rows.TabularRowsSorter;
import com.bearsoft.gui.grid.rows.TreedRowsSorter;
import com.bearsoft.gui.grid.selection.ConstrainedListSelectionModel;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetSaveEvent;
import com.bearsoft.rowset.events.RowsetScrollEvent;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.locators.RowWrap;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.forms.Form;
import com.eas.client.forms.api.components.model.ArrayModelWidget;
import com.eas.client.forms.api.components.model.ModelComponentDecorator;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.ModelEntityParameterRef;
import com.eas.client.model.ModelEntityRef;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.dbcontrols.*;
import com.eas.dbcontrols.grid.rt.*;
import com.eas.dbcontrols.grid.rt.columns.ModelColumn;
import com.eas.dbcontrols.grid.rt.columns.RowHeaderTableColumn;
import com.eas.dbcontrols.grid.rt.models.RowsetsModel;
import com.eas.dbcontrols.grid.rt.models.RowsetsTableModel;
import com.eas.dbcontrols.grid.rt.models.RowsetsTreedModel;
import com.eas.dbcontrols.grid.rt.rowheader.RowHeaderCellEditor;
import com.eas.dbcontrols.grid.rt.rowheader.RowHeaderCellRenderer;
import com.eas.design.Designable;
import com.eas.design.Undesignable;
import com.eas.gui.CascadedStyle;
import com.eas.script.ScriptUtils;
import com.eas.util.StringUtils;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class DbGrid extends JPanel implements ArrayModelWidget, TablesGridContainer {

    public static final int ROWS_HEADER_TYPE_NONE = 0;
    public static final int ROWS_HEADER_TYPE_USUAL = 1;
    public static final int ROWS_HEADER_TYPE_CHECKBOX = 2;
    public static final int ROWS_HEADER_TYPE_RADIOBUTTON = 3;

    protected Object published;

    public void injectPublished(Object aValue) {
        published = aValue;
    }

    protected void applyComponentPopupMenu() {
        if (tlTable != null) {
            tlTable.setComponentPopupMenu(getComponentPopupMenu());
        }
        if (trTable != null) {
            trTable.setComponentPopupMenu(getComponentPopupMenu());
        }
        if (blTable != null) {
            blTable.setComponentPopupMenu(getComponentPopupMenu());
        }
        if (brTable != null) {
            brTable.setComponentPopupMenu(getComponentPopupMenu());
        }
    }

    protected void applyGridColor() {
        if (getGridColor() != null) {
            if (tlTable != null) {
                tlTable.setGridColor(getGridColor());
            }
            if (trTable != null) {
                trTable.setGridColor(getGridColor());
            }
            if (blTable != null) {
                blTable.setGridColor(getGridColor());
            }
            if (brTable != null) {
                brTable.setGridColor(getGridColor());
            }
        }
    }

    protected void applyRowHeight() {
        if (tlTable != null) {
            tlTable.setRowHeight(getRowsHeight());
        }
        if (trTable != null) {
            trTable.setRowHeight(getRowsHeight());
        }
        if (blTable != null) {
            blTable.setRowHeight(getRowsHeight());
        }
        if (brTable != null) {
            brTable.setRowHeight(getRowsHeight());
        }
    }

    protected void applyShowHorizontalLines() {
        if (tlTable != null) {
            tlTable.setShowHorizontalLines(isShowHorizontalLines());
        }
        if (trTable != null) {
            trTable.setShowHorizontalLines(isShowHorizontalLines());
        }
        if (blTable != null) {
            blTable.setShowHorizontalLines(isShowHorizontalLines());
        }
        if (brTable != null) {
            brTable.setShowHorizontalLines(isShowHorizontalLines());
        }
    }

    protected void applyShowVerticalLines() {
        if (tlTable != null) {
            tlTable.setShowVerticalLines(isShowVerticalLines());
        }
        if (trTable != null) {
            trTable.setShowVerticalLines(isShowVerticalLines());
        }
        if (blTable != null) {
            blTable.setShowVerticalLines(isShowVerticalLines());
        }
        if (brTable != null) {
            brTable.setShowVerticalLines(isShowVerticalLines());
        }
    }

    protected void applyToolTipText() {
        if (tlTable != null) {
            tlTable.setToolTipText(getToolTipText());
        }
        if (trTable != null) {
            trTable.setToolTipText(getToolTipText());
        }
        if (blTable != null) {
            blTable.setToolTipText(getToolTipText());
        }
        if (brTable != null) {
            brTable.setToolTipText(getToolTipText());
        }
    }

    protected Locator createPksLocator(Rowset colsRs) throws IllegalStateException {
        Locator colsLoc = colsRs.createLocator();
        colsLoc.beginConstrainting();
        try {
            List<Integer> pkIndicies = colsRs.getFields().getPrimaryKeysIndicies();
            for (Integer pkIdx : pkIndicies) {
                colsLoc.addConstraint(pkIdx);
            }
        } finally {
            colsLoc.endConstrainting();
        }
        return colsLoc;
    }

    /**
     * Returns index of a row in the model. Index is in model coordinates. Index
     * is 0-based.
     *
     * @param anElement Element to calculate index for.
     * @return Index if row.
     * @throws RowsetException
     */
    public int row2Index(JSObject anElement) throws RowsetException {
        int idx = -1;
        if (deepModel instanceof TableFront2TreedModel<?>) {
            TableFront2TreedModel<JSObject> front = (TableFront2TreedModel<JSObject>) deepModel;
            idx = front.getIndexOf(anElement);
        } else if (deepModel instanceof RowsetsTableModel) {
            RowsetsTableModel lmodel = (RowsetsTableModel) deepModel;
            Object[] keys = anElement.getPKValues();
            if (lmodel.getPkLocator().find(keys != null && keys.length > 1 ? new Object[]{keys[0]} : keys)) {
                RowWrap rw = lmodel.getPkLocator().getSubSet().get(0);
                idx = rw.getIndex() - 1;
            }
        }
        return idx;
    }

    /**
     * Returns row for particular Index. Index is in model's coordinates. Index
     * is 0-based.
     *
     * @param aIdx Index the row is to be calculated for.
     * @return Row's index;
     * @throws RowsetException
     */
    public JSObject index2Row(int aIdx) throws RowsetException {
        JSObject row = null;
        if (deepModel instanceof TableFront2TreedModel<?>) {
            TableFront2TreedModel<JSObject> front = (TableFront2TreedModel<JSObject>) deepModel;
            row = front.getElementAt(aIdx);
        } else if (deepModel instanceof RowsetsTableModel) {
            RowsetsTableModel lmodel = (RowsetsTableModel) deepModel;
            row = lmodel.getRowsRowset().getRow(aIdx + 1);
        }
        return row;
    }

    protected void putAction(Action aAction) {
        if (aAction != null) {
            tlTable.getActionMap().put(aAction.getClass().getName(), aAction);
            trTable.getActionMap().put(aAction.getClass().getName(), aAction);
            blTable.getActionMap().put(aAction.getClass().getName(), aAction);
            brTable.getActionMap().put(aAction.getClass().getName(), aAction);
        }
    }

    /**
     * @param aParent Columns group, with will be parentfor new groups.
     * @param aContents A list of used as a source for columns groups.
     * @param linkSource Wether to link new column groups with source
     * DbGridColumn-s. This also means, that LinkedGridColumnsGroup will be
     * created.
     * @return
     * @throws Exception
     */
    private Map<TableColumn, GridColumnsGroup> fillColumnsGroup(GridColumnsGroup aParent, List<GridColumnsGroup> aContents) throws Exception {
        Map<TableColumn, GridColumnsGroup> groups = new HashMap<>();
        for (GridColumnsGroup dCol : aContents) {
            // create grid columns group
            GridColumnsGroup group = new GridColumnsGroup();
            if (aParent != null) {
                group.setParent(aParent);
                aParent.addChild(group);
            }
            // Let's take care of structure
            if (dCol.hasChildren()) {
                Map<TableColumn, GridColumnsGroup> childGroups = fillColumnsGroup(group, dCol.getChildren(), linkSource);
                groups.putAll(childGroups);
            } else // Leaf group
            {
                Rowset rs = DbControlsUtils.resolveRowset(model, dCol.getDatamodelElement());
                int fidx = DbControlsUtils.resolveFieldIndex(model, dCol.getDatamodelElement());
                if (fidx < 1) {
                    if (dCol.getDatamodelElement() != null) {
                        Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, "Bad column configuration: {0}''s model binding can''t be resolved", dCol.getName());
                    }
                }
                // Column setup
                ModelColumn tCol = new ModelColumn();
                tCol.setModelIndex(columnModel.getColumnCount());
                tCol.setMinWidth(group.getMinWidth());
                tCol.setMaxWidth(group.getMaxWidth());
                tCol.setPreferredWidth(dCol.getWidth());
                tCol.setWidth(dCol.getWidth());
                tCol.setResizable(dCol.isResizable());
                tCol.setMoveable(dCol.isMoveable());
                String title = group.getTitle();
                if (title == null || title.isEmpty()) {
                    title = group.getName();
                }
                tCol.setTitle(title);
                if (dCol.getControlInfo() != null) {
                    TableCellRenderer cellRenderer = dCol.createCellRenderer();
                    tCol.setCellRenderer(cellRenderer);
                    if (cellRenderer instanceof ScalarDbControl) {
                        ((ScalarDbControl) cellRenderer).setModel(model);
                        mCol.setView((ScalarDbControl) cellRenderer);
                    }
                    TableCellEditor cellEditor = dCol.createCellEditor();
                    tCol.setCellEditor(cellEditor);
                    if (cellEditor instanceof ScalarDbControl) {
                        Field field = DbControlsUtils.resolveField(model, dCol.getDatamodelElement());
                        ((ScalarDbControl) cellEditor).setModel(model);
                        ((ScalarDbControl) cellEditor).extraCellControls(null, field != null ? field.isNullable() : false);
                        mCol.setEditor((ScalarDbControl) cellEditor);
                    }
                }
                columnModel.addColumn(tCol);
                // groups-view link
                group.setTableColumn(tCol);
                group.setMoveable(tCol.getMoveable());
                group.setResizeable(tCol.isResizable());
                groups.put(tCol, group);
            }
        }
        return groups;
    }

    private void configureTreedView() {
        if (rowsModel instanceof RowsetsTreedModel) {
            if (columnModel.getColumnCount() > 0) {
                TableColumn tCol = null;
                // Let's find first stable column
                for (int i = 0; i < columnModel.getColumnCount(); i++) {
                    TableColumn col = columnModel.getColumn(i);
                    if (col.getIdentifier() instanceof FieldModelColumn) {
                        tCol = col;
                        break;
                    }
                }
                assert tCol != null;
                tCol.setCellRenderer(new InsettedTreeRenderer<>(tCol.getCellRenderer(), new TreeColumnLeadingComponent<>(deepModel, style, false)));
                tCol.setCellEditor(new InsettedTreeEditor<>(tCol.getCellEditor(), new TreeColumnLeadingComponent<>(deepModel, style, true)));
            }
        }
    }

    private void applyOddRowsColor() {
        if (tlTable != null) {
            tlTable.setOddRowsColor(getOddRowsColor());
        }
        if (trTable != null) {
            trTable.setOddRowsColor(getOddRowsColor());
        }
        if (blTable != null) {
            blTable.setOddRowsColor(getOddRowsColor());
        }
        if (brTable != null) {
            brTable.setOddRowsColor(getOddRowsColor());
        }
    }

    private void applyShowOddRowsInOtherColor() {
        if (tlTable != null) {
            tlTable.setShowOddRowsInOtherColor(isShowOddRowsInOtherColor());
        }
        if (trTable != null) {
            trTable.setShowOddRowsInOtherColor(isShowOddRowsInOtherColor());
        }
        if (blTable != null) {
            blTable.setShowOddRowsInOtherColor(isShowOddRowsInOtherColor());
        }
        if (brTable != null) {
            brTable.setShowOddRowsInOtherColor(isShowOddRowsInOtherColor());
        }
    }

    private void applyEditable() {
        if (tlTable != null) {
            tlTable.setEditable(editable);
        }
        if (trTable != null) {
            trTable.setEditable(editable);
        }
        if (blTable != null) {
            blTable.setEditable(editable);
        }
        if (brTable != null) {
            brTable.setEditable(editable);
        }
    }

    private void applyEnabled() {
        if (tlTable != null) {
            tlTable.setEnabled(isEnabled());
        }
        if (trTable != null) {
            trTable.setEnabled(isEnabled());
        }
        if (blTable != null) {
            blTable.setEnabled(isEnabled());
        }
        if (brTable != null) {
            brTable.setEnabled(isEnabled());
        }
    }

    public JTable getTableByViewCell(int row, int column) {
        if (row >= 0 && row < tlTable.getRowCount()
                && column >= 0 && column < tlTable.getColumnCount()) {
            return tlTable;
        } else if (row >= 0 && row < trTable.getRowCount()
                && (column - tlTable.getColumnCount()) >= 0 && (column - tlTable.getColumnCount()) < trTable.getColumnCount()) {
            return trTable;
        } else if ((row - tlTable.getRowCount()) >= 0 && (row - tlTable.getRowCount()) < blTable.getRowCount()
                && column >= 0 && column < blTable.getColumnCount()) {
            return blTable;
        } else if ((row - tlTable.getRowCount()) >= 0 && (row - tlTable.getRowCount()) < brTable.getRowCount()
                && (column - tlTable.getColumnCount()) >= 0 && (column - tlTable.getColumnCount()) < brTable.getColumnCount()) {
            return brTable;
        } else {
            return null;
        }
    }

    private int convertFixedColumns2Leaves(List<DbGridColumn> roots, int fixedCols) {
        int leavesCount = 0;
        if (fixedCols > 0) {
            for (int i = 0; i < fixedCols; i++) {
                DbGridColumn col = roots.get(i);
                if (!col.hasChildren()) {
                    leavesCount++;
                } else {
                    leavesCount += convertFixedColumns2Leaves(col.getChildren(), col.getChildren().size());
                }
            }
        }
        return leavesCount;
    }

    public boolean isTreeConfigured() throws Exception {
        return unaryLinkField != null && !unaryLinkField.isEmpty();
    }
    //
    public static final int CELLS_CACHE_SIZE = 65536;// 2^16
    public static final Color FIXED_COLOR = new Color(154, 204, 255);
    public static Icon processIcon = IconCache.getIcon("16x16/process-indicator.gif");
    protected TablesFocusManager tablesFocusManager = new TablesFocusManager();
    protected TablesMousePropagator tablesMousePropagator = new TablesMousePropagator();
    // design
    protected List<DbGridColumn> header = new ArrayList<>();
    protected int rowsHeight = 20;
    protected boolean showVerticalLines = true;
    protected boolean showHorizontalLines = true;
    protected boolean showOddRowsInOtherColor = true;
    protected boolean editable = true;
    protected boolean insertable = true;
    protected boolean deletable = true;
    protected Color oddRowsColor;
    protected Color gridColor;
    protected CascadedStyle style = new CascadedStyle();
    // data
    protected RowsetsModel rowsModel;
    protected TableModel deepModel;
    protected TabularRowsSorter<? extends TableModel> rowSorter;
    protected Set<JSObject> processedRows = new HashSet<>();
    // tree info
    protected String unaryLinkField;
    // rows column info
    protected int rowsHeaderType = ROWS_HEADER_TYPE_USUAL;
    protected int fixedRows;
    protected int fixedColumns;
    // view
    protected TableColumnModel columnModel;
    protected JSObject generalOnRender;
    // selection
    protected ListSelectionModel rowsSelectionModel;
    protected ListSelectionModel columnsSelectionModel;
    protected GeneralSelectionChangesReflector generalSelectionChangesReflector;
    // visual components
    protected JLabel progressIndicator;
    protected JScrollPane gridScroll;
    protected MultiLevelHeader lheader;
    protected MultiLevelHeader rheader;
    protected GridTable tlTable;
    protected GridTable trTable;
    protected GridTable blTable;
    protected GridTable brTable;
    protected RowsetListener scrollReflector = new RowsetAdapter() {
        protected void repaintRowHeader() {
            if (gridScroll.getRowHeader() != null) {
                gridScroll.getRowHeader().repaint();
            }
        }

        @Override
        public void rowsetScrolled(RowsetScrollEvent event) {
            repaintRowHeader();
        }

        @Override
        public void rowsetRolledback(RowsetRollbackEvent event) {
            repaintRowHeader();
        }

        @Override
        public void rowsetSaved(RowsetSaveEvent event) {
            repaintRowHeader();
        }
    };
    // actions
    protected Action findSomethingAction;

    public DbGrid() {
        super();
        setupStyle();
        initializeDesign();
    }

    protected void setupStyle() {
        JTable tbl = new JTable();
        gridColor = tbl.getGridColor();
        style = new CascadedStyle(null);
        style.setAlign(SwingConstants.LEFT);
        style.setFont(tbl.getFont());
        Color uiColor = tbl.getBackground();
        style.setBackground(new Color(uiColor.getRed(), uiColor.getGreen(), uiColor.getBlue(), uiColor.getAlpha()));
        uiColor = tbl.getForeground();
        style.setForeground(new Color(uiColor.getRed(), uiColor.getGreen(), uiColor.getBlue(), uiColor.getAlpha()));
        style.setIconName(null);

        Object openedIcon = UIManager.get("Tree.openIcon");
        Object closedIcon = UIManager.get("Tree.closedIcon");
        Object leafIcon = UIManager.get("Tree.leafIcon");
        if (openedIcon instanceof Icon) {
            style.setOpenFolderIcon((Icon) openedIcon);
        } else {
            style.setOpenFolderIconName("folder-horizontal-open.png");
        }
        if (closedIcon instanceof Icon) {
            style.setFolderIcon((Icon) closedIcon);
        } else {
            style.setFolderIconName("folder-horizontal.png");
        }
        if (leafIcon instanceof Icon) {
            style.setLeafIcon((Icon) leafIcon);
        } else {
            style.setLeafIconName("status-offline.png");
        }
    }

    public void setShowHorizontalLines(boolean aValue) {
        showHorizontalLines = aValue;
        applyShowHorizontalLines();
    }

    public void setShowVerticalLines(boolean aValue) {
        showVerticalLines = aValue;
        applyShowVerticalLines();
    }

    @Designable(category = "appearance")
    public Color getOddRowsColor() {
        return oddRowsColor;
    }

    public void setOddRowsColor(Color aValue) {
        oddRowsColor = aValue;
        applyOddRowsColor();
    }

    @Designable(category = "appearance")
    public boolean isShowHorizontalLines() {
        return showHorizontalLines;
    }

    @Designable(category = "appearance")
    public boolean isShowVerticalLines() {
        return showVerticalLines;
    }

    @Designable(category = "appearance")
    public boolean isShowOddRowsInOtherColor() {
        return showOddRowsInOtherColor;
    }

    public void setShowOddRowsInOtherColor(boolean aValue) {
        showOddRowsInOtherColor = aValue;
        applyShowOddRowsInOtherColor();
    }

    @Designable(category = "appearance")
    public Color getGridColor() {
        return gridColor;
    }

    public void setGridColor(Color aValue) {
        gridColor = aValue;
        applyGridColor();
    }

    @Designable(category = "appearance")
    public int getRowsHeight() {
        return rowsHeight;
    }

    public void setRowsHeight(int aValue) {
        if (aValue < 10) {
            aValue = 10;
        }
        if (aValue > 350) {
            aValue = 350;
        }
        rowsHeight = aValue;
        applyRowHeight();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        applyEnabled();
    }

    public void setEditable(boolean aValue) {
        editable = aValue;
        applyEditable();
    }

    @Designable(category = "editing")
    public boolean isEditable() {
        return editable;
    }

    public void setInsertable(boolean aValue) {
        insertable = aValue;
    }

    @Designable(category = "editing")
    public boolean isInsertable() {
        return insertable;
    }

    public void setDeletable(boolean aValue) {
        deletable = aValue;
    }

    @Designable(category = "editing")
    public boolean isDeletable() {
        return deletable;
    }

    public GridTable getTopLeftTable() {
        return tlTable;
    }

    public GridTable getTopRightTable() {
        return trTable;
    }

    public GridTable getBottomLeftTable() {
        return blTable;
    }

    public GridTable getBottomRightTable() {
        return brTable;
    }

    public ListSelectionModel getRowsSelectionModel() {
        return rowsSelectionModel;
    }

    public ListSelectionModel getColumnsSelectionModel() {
        return columnsSelectionModel;
    }

    public List<Row> getSelected() throws Exception {
        List<Row> selectedRows = new ArrayList<>();
        if (deepModel != null) {// design time is only the case
            for (int i = 0; i < deepModel.getRowCount(); i++) {
                if (rowsSelectionModel.isSelectedIndex(i)) {
                    Row row = index2Row(rowSorter.convertRowIndexToModel(i));
                    selectedRows.add(row);
                }
            }
        }
        return selectedRows;
    }

    @Override
    public void setComponentPopupMenu(JPopupMenu aPopup) {
        super.setComponentPopupMenu(aPopup);
        applyComponentPopupMenu();
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (style != null) {
            style.setBackground(bg);
        }
        applyBackground();
    }

    public void applyBackground() {
        if (tlTable != null) {
            tlTable.setBackground(getBackground());
        }
        if (trTable != null) {
            trTable.setBackground(getBackground());
        }
        if (blTable != null) {
            blTable.setBackground(getBackground());
        }
        if (brTable != null) {
            brTable.setBackground(getBackground());
        }
    }

    @Override
    public Color getBackground() {
        if (style != null) {
            return style.getBackground();
        } else {
            return super.getBackground();
        }
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (style != null) {
            style.setForeground(fg);
        }
        applyForeground();
    }

    public void applyForeground() {
        if (tlTable != null) {
            tlTable.setForeground(getForeground());
        }
        if (trTable != null) {
            trTable.setForeground(getForeground());
        }
        if (blTable != null) {
            blTable.setForeground(getForeground());
        }
        if (brTable != null) {
            brTable.setForeground(getForeground());
        }
    }

    @Override
    public Color getForeground() {
        if (style != null) {
            return style.getForeground();
        } else {
            return super.getForeground();
        }
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (style != null) {
            style.setFont(font);
        }
        applyFont();
    }

    public void applyFont() {
        if (tlTable != null) {
            tlTable.setFont(getFont());
        }
        if (trTable != null) {
            trTable.setFont(getFont());
        }
        if (blTable != null) {
            blTable.setFont(getFont());
        }
        if (brTable != null) {
            brTable.setFont(getFont());
        }
    }

    @Override
    public Font getFont() {
        if (style != null) {
            return style.getFont();
        } else {
            return super.getFont();
        }
    }

    @Override
    public void setToolTipText(String aValue) {
        if (aValue != null && aValue.isEmpty()) {
            aValue = null;
        }
        super.setToolTipText(aValue);
        applyToolTipText();
    }

    @Designable(category = "appearance")
    public int getFixedColumns() {
        return fixedColumns;
    }

    public void setFixedColumns(int aValue) {
        fixedColumns = aValue;
    }

    @Designable(category = "appearance")
    public int getFixedRows() {
        return fixedRows;
    }

    public void setFixedRows(int aValue) {
        fixedRows = aValue;
    }

    @Undesignable
    public List<DbGridColumn> getHeader() {
        return header;
    }

    public void setHeader(List<DbGridColumn> aValue) {
        header = aValue;
    }

    @Designable(displayName = "entity", category = "model")
    public ModelEntityRef getRowsDatasource() {
        return rowsDatasource;
    }

    public void setRowsDatasource(ModelEntityRef aValue) {
        rowsDatasource = aValue;
    }

    @Designable(category = "tree")
    public ModelElementRef getUnaryLinkField() {
        return unaryLinkField;
    }

    public void setUnaryLinkField(ModelElementRef aValue) {
        unaryLinkField = aValue;
    }

    @Designable(category = "tree")
    public ModelEntityParameterRef getParam2GetChildren() {
        return param2GetChildren;
    }

    public void setParam2GetChildren(ModelEntityParameterRef aValue) {
        param2GetChildren = aValue;
    }

    @Designable(category = "tree")
    public ModelElementRef getParamSourceField() {
        return paramSourceField;
    }

    public void setParamSourceField(ModelElementRef aValue) {
        paramSourceField = aValue;
    }

    @Designable(category = "appearance")
    public int getRowsHeaderType() {
        return rowsHeaderType;
    }

    public void setRowsHeaderType(int aValue) {
        if (rowsHeaderType != aValue) {
            rowsHeaderType = aValue;
            initializeDesign();
        }
    }

    @Undesignable
    public JSObject getGeneralOnRender() {
        return generalOnRender;
    }

    public void setGeneralOnRender(JSObject aValue) {
        generalOnRender = aValue;
        if (rowsModel != null) {
            rowsModel.setGeneralOnRender(aValue);
        }
    }

    public void insertRow() {
        try {
            if (insertable && !(rowsModel.getRowsRowset() instanceof ParametersRowset)) {
                rowsSelectionModel.removeListSelectionListener(generalSelectionChangesReflector);
                try {
                    if (rowsModel instanceof RowsetsTreedModel) {
                        int parentColIndex = ((RowsetsTreedModel) rowsModel).getParentFieldIndex();
                        Object parentColValue = null;
                        if (!rowsModel.getRowsRowset().isEmpty()
                                && !rowsModel.getRowsRowset().isBeforeFirst()
                                && !rowsModel.getRowsRowset().isAfterLast()) {
                            parentColValue = rowsModel.getRowsRowset().getObject(parentColIndex);
                        }
                        rowsModel.getRowsRowset().insert(parentColIndex, parentColValue);
                    } else {
                        rowsModel.getRowsRowset().insert();
                    }
                } finally {
                    rowsSelectionModel.addListSelectionListener(generalSelectionChangesReflector);
                }
                Row insertedRow = rowsModel.getRowsRowset().getCurrentRow();
                if (insertedRow.isInserted()) {
                    makeVisible(insertedRow);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteRow() {
        try {
            if (deletable && !(rowsModel.getRowsRowset() instanceof ParametersRowset)) {
                ListSelectionModel savedSelection = new DefaultListSelectionModel();
                Set<Row> rows = new HashSet<>();
                for (int viewRowIndex = rowsSelectionModel.getMinSelectionIndex(); viewRowIndex <= rowsSelectionModel.getMaxSelectionIndex(); viewRowIndex++) {
                    if (rowsSelectionModel.isSelectedIndex(viewRowIndex)) {
                        // We have to act upon model coordinates here!
                        Row rsRow = index2Row(rowSorter.convertRowIndexToModel(viewRowIndex));
                        if (rsRow != null) {
                            rows.add(rsRow);
                        }
                        // We have to act upon view coordinates here!
                        savedSelection.addSelectionInterval(viewRowIndex, viewRowIndex);
                    }
                }
                try {
                    rowsModel.getRowsRowset().delete(rows);
                } finally {
                    // We have to act upon view coordinates here!
                    rowsSelectionModel.clearSelection();
                    for (int viewRowIndex = savedSelection.getMinSelectionIndex(); viewRowIndex <= savedSelection.getMaxSelectionIndex(); viewRowIndex++) {
                        if (viewRowIndex >= 0 && viewRowIndex < rowSorter.getViewRowCount()) {
                            rowsSelectionModel.addSelectionInterval(viewRowIndex, viewRowIndex);
                        } else if (viewRowIndex == rowSorter.getViewRowCount()) {
                            rowsSelectionModel.addSelectionInterval(viewRowIndex - 1, viewRowIndex - 1);
                        }
                    }
                }
            }
        } catch (RowsetException ex) {
            Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected class GeneralSelectionChangesReflector implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            // If remove !e.getValueIsAdjusting() check,
            // then there is a bug with rowset.insert() -> rowset.field1 = ... chain 
            // because selection restoring repositions rowset before assignment can take place.
            if (!e.getValueIsAdjusting() && !rowsModel.getRowsRowset().isInserting() && rowsSelectionModel.getLeadSelectionIndex() != -1) {
                try {
                    if (!try2StopAnyEditing()) {
                        try2CancelAnyEditing();
                    }
                    Row row = index2Row(rowSorter.convertRowIndexToModel(rowsSelectionModel.getLeadSelectionIndex()));
                    if (row != null) {
                        rowsModel.positionRowsetWithRow(row);
                    }
                    repaint();
                } catch (Exception ex) {
                    Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    protected class ColumnHeaderScroller implements ChangeListener {

        protected JViewport columnHeader;
        protected JViewport content;
        protected boolean working;

        public ColumnHeaderScroller(JViewport aColumnHeader, JViewport aContent) {
            columnHeader = aColumnHeader;
            content = aContent;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!working) {
                working = true;
                try {
                    Point contentPos = content.getViewPosition();
                    contentPos.x = columnHeader.getViewPosition().x;
                    content.setViewPosition(contentPos);
                } finally {
                    working = false;
                }
            }
        }
    }

    protected class RowHeaderScroller implements ChangeListener {

        protected JViewport rowHeader;
        protected JViewport content;
        protected boolean working;

        public RowHeaderScroller(JViewport aRowHeader, JViewport aContent) {
            rowHeader = aRowHeader;
            content = aContent;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!working && fixedColumns > 0) {
                working = true;
                try {
                    Point contentPos = content.getViewPosition();
                    contentPos.y = rowHeader.getViewPosition().y;
                    content.setViewPosition(contentPos);
                } finally {
                    working = false;
                }
            }
        }
    }

    // Some cleanup
    protected void cleanup() throws Exception {
        removeAll();
        if (rowsEntity != null && rowsEntity.getRowset() != null) {
            rowsEntity.getRowset().removeRowsetListener(scrollReflector);
        }
        rowsEntity = null;
        if (columnModel != null) {
            for (int i = columnModel.getColumnCount() - 1; i >= 0; i--) {
                columnModel.removeColumn(columnModel.getColumn(i));
            }
            columnModel = null;
        }
        rowsModel = null;
        deepModel = null;
        rowSorter = null;
        tlTable = null;
        trTable = null;
        blTable = null;
        brTable = null;
        gridScroll = null;
    }

    public void configure() throws Exception {
        if (model != null) {
            cleanup();
            if (rowsHeaderType != ROWS_HEADER_TYPE_NONE) {
                int fixedWidth = 18;
                if (rowsHeaderType == ROWS_HEADER_TYPE_CHECKBOX
                        || rowsHeaderType == ROWS_HEADER_TYPE_RADIOBUTTON) {
                    fixedWidth += 20;
                }
                // This place is very special, because editing while configuring takes place.
                if (header.isEmpty() || header.get(0) == null || !(header.get(0) instanceof FixedDbGridColumn)) {
                    header.add(0, new FixedDbGridColumn(fixedWidth, rowsHeaderType));// Space enough for two icons
                }
            }
            fixedColumns = convertFixedColumns2Leaves(header, fixedColumns);

            // Rows configuration
            rowsSelectionModel = new DefaultListSelectionModel();
            rowsEntity = DbControlsUtils.resolveEntity(model, rowsDatasource);
            Rowset rowsRowset = DbControlsUtils.resolveRowset(model, rowsDatasource);
            if (rowsRowset != null) {
                if (isTreeConfigured()) {
                    final Parameter param = DbControlsUtils.resolveParameter(model, param2GetChildren);
                    final int paramSourceFieldIndex = DbControlsUtils.resolveFieldIndex(model, paramSourceField);

                    int parentColIndex = rowsRowset.getFields().find(unaryLinkField.getFieldName());
                    rowsModel = new RowsetsTreedModel(rowsEntity, rowsRowset, parentColIndex, generalOnRender) {
                        @Override
                        public boolean isLeaf(Row anElement) {
                            if (param != null && paramSourceFieldIndex != 0)// lazy tree
                            {
                                return false;
                            } else {
                                return super.isLeaf(anElement);
                            }
                        }
                    };
                    if (param != null && paramSourceFieldIndex != 0) {// lazy tree
                        GridChildrenFetcher fetcher = new GridChildrenFetcher(this, rowsEntity, param, paramSourceFieldIndex);
                        deepModel = new TableFront2TreedModel<>((RowsetsTreedModel) rowsModel, fetcher);
                    } else {
                        deepModel = new TableFront2TreedModel<>((RowsetsTreedModel) rowsModel);
                    }
                    rowSorter = new TreedRowsSorter<>((TableFront2TreedModel<Row>) deepModel, rowsSelectionModel);
                } else {
                    rowsModel = new RowsetsTableModel(rowsEntity, rowsRowset, generalOnRender);
                    deepModel = (TableModel) rowsModel;
                    rowSorter = new TabularRowsSorter<>((RowsetsTableModel) deepModel, rowsSelectionModel);
                }
                TableModel generalModel = new CachingTableModel(deepModel, CELLS_CACHE_SIZE);

                generalSelectionChangesReflector = new GeneralSelectionChangesReflector();
                rowsSelectionModel.addListSelectionListener(generalSelectionChangesReflector);
                // Columns configuration
                columnsSelectionModel = new DefaultListSelectionModel();
                columnModel = new DefaultTableColumnModel();
                Map<TableColumn, GridColumnsGroup> cols2groups = fillColumnsGroup(null, header, false);
                columnModel.setSelectionModel(columnsSelectionModel);
                columnModel.setColumnSelectionAllowed(true);
                rowsSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                configureTreedView();

                // constraints setup
                LinearConstraint leftColsConstraint = new LinearConstraint(0, fixedColumns - 1);
                LinearConstraint rightColsConstraint = new LinearConstraint(fixedColumns, Integer.MAX_VALUE);
                LinearConstraint topRowsConstraint = new LinearConstraint(0, fixedRows - 1);
                LinearConstraint bottomRowsConstraint = new LinearConstraint(fixedRows, Integer.MAX_VALUE);

                // constrained layer models setup
                tlTable = new GridTable(null, rowsRowset, this);
                trTable = new GridTable(null, rowsRowset, this);
                blTable = new GridTable(tlTable, rowsRowset, this);
                brTable = new GridTable(trTable, rowsRowset, this);
                tlTable.setModel(generalModel);
                trTable.setModel(generalModel);
                blTable.setModel(generalModel);
                brTable.setModel(generalModel);

                columnModel.setColumnSelectionAllowed(rowsHeaderType != DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_CHECKBOX
                        && rowsHeaderType != DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_RADIOBUTTON);

                tlTable.setRowSorter(new ConstrainedRowSorter<>(rowSorter, topRowsConstraint));
                tlTable.setSelectionModel(new ConstrainedListSelectionModel(rowsSelectionModel, topRowsConstraint));
                tlTable.setColumnModel(new ConstrainedColumnModel(columnModel, leftColsConstraint));
                tlTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(columnsSelectionModel, leftColsConstraint));

                trTable.setRowSorter(new ConstrainedRowSorter<>(rowSorter, topRowsConstraint));
                trTable.setSelectionModel(new ConstrainedListSelectionModel(rowsSelectionModel, topRowsConstraint));
                trTable.setColumnModel(new ConstrainedColumnModel(columnModel, rightColsConstraint));
                trTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(columnsSelectionModel, rightColsConstraint));

                blTable.setRowSorter(new ConstrainedRowSorter<>(rowSorter, bottomRowsConstraint));
                blTable.setSelectionModel(new ConstrainedListSelectionModel(rowsSelectionModel, bottomRowsConstraint));
                blTable.setColumnModel(new ConstrainedColumnModel(columnModel, leftColsConstraint));
                blTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(columnsSelectionModel, leftColsConstraint));

                brTable.setRowSorter(new ConstrainedRowSorter<>(rowSorter, bottomRowsConstraint));
                brTable.setSelectionModel(new ConstrainedListSelectionModel(rowsSelectionModel, bottomRowsConstraint));
                brTable.setColumnModel(new ConstrainedColumnModel(columnModel, rightColsConstraint));
                brTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(columnsSelectionModel, rightColsConstraint));

                tlTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                trTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                blTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                brTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                tlTable.setAutoCreateColumnsFromModel(false);
                trTable.setAutoCreateColumnsFromModel(false);
                blTable.setAutoCreateColumnsFromModel(false);
                brTable.setAutoCreateColumnsFromModel(false);

                // keyboard focus flow setup
                tlTable.addKeyListener(tablesFocusManager);
                trTable.addKeyListener(tablesFocusManager);
                blTable.addKeyListener(tablesFocusManager);
                brTable.addKeyListener(tablesFocusManager);
                // mouse propagating setup
                tlTable.addMouseListener(tablesMousePropagator);
                trTable.addMouseListener(tablesMousePropagator);
                blTable.addMouseListener(tablesMousePropagator);
                brTable.addMouseListener(tablesMousePropagator);
                tlTable.addMouseWheelListener(tablesMousePropagator);
                trTable.addMouseWheelListener(tablesMousePropagator);
                blTable.addMouseWheelListener(tablesMousePropagator);
                brTable.addMouseWheelListener(tablesMousePropagator);
                tlTable.addMouseMotionListener(tablesMousePropagator);
                trTable.addMouseMotionListener(tablesMousePropagator);
                blTable.addMouseMotionListener(tablesMousePropagator);
                brTable.addMouseMotionListener(tablesMousePropagator);
                // grid components setup.
                // left header setup
                lheader = new MultiLevelHeader();
                lheader.setTable(tlTable);
                tlTable.getTableHeader().setResizingAllowed(true);
                lheader.setSlaveHeaders(tlTable.getTableHeader(), blTable.getTableHeader());
                lheader.setColumnModel(tlTable.getColumnModel());
                lheader.getColumnsParents().putAll(filterLeaves(cols2groups, columnModel, 0, fixedColumns - 1));
                lheader.setRowSorter(rowSorter);
                // right header setup
                rheader = new MultiLevelHeader();
                rheader.setTable(trTable);
                trTable.getTableHeader().setResizingAllowed(true);
                rheader.setSlaveHeaders(trTable.getTableHeader(), brTable.getTableHeader());
                rheader.setColumnModel(trTable.getColumnModel());
                rheader.getColumnsParents().putAll(filterLeaves(cols2groups, columnModel, fixedColumns, columnModel.getColumnCount() - 1));
                rheader.setRowSorter(rowSorter);
                scriptableColumns.stream().forEach((sCol) -> {
                    if (rheader.getColumnsParents().containsKey(sCol.getViewColumn())) {
                        sCol.setHeader(rheader);
                    } else if (lheader.getColumnsParents().containsKey(sCol.getViewColumn())) {
                        sCol.setHeader(lheader);
                    }
                });
                // Tables are enclosed in panels to avoid table's stupid efforts
                // to configure it's parent scroll pane.
                JPanel tlPanel = new JPanel(new BorderLayout());
                tlPanel.add(lheader, BorderLayout.NORTH);
                tlPanel.add(tlTable, BorderLayout.CENTER);
                JPanel trPanel = new JPanel(new BorderLayout());
                trPanel.add(rheader, BorderLayout.NORTH);
                trPanel.add(trTable, BorderLayout.CENTER);
                JPanel blPanel = new JPanel(new BorderLayout());
                blPanel.add(blTable, BorderLayout.CENTER);
                JPanel brPanel = new GridTableScrollablePanel(brTable);
                //brPanel.add(brTable, BorderLayout.CENTER);

                boolean needOutlineCols = fixedColumns > 0;
                tlPanel.setBorder(new MatteBorder(0, 0, fixedRows > 0 ? 1 : 0, needOutlineCols ? 1 : 0, FIXED_COLOR));
                trPanel.setBorder(new MatteBorder(0, 0, fixedRows > 0 ? 1 : 0, 0, FIXED_COLOR));
                blPanel.setBorder(new MatteBorder(0, 0, 0, needOutlineCols ? 1 : 0, FIXED_COLOR));

                progressIndicator = new JLabel(processIcon);
                progressIndicator.setVisible(false);
                gridScroll = new JScrollPane();
                gridScroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, tlPanel);
                gridScroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, progressIndicator);
                gridScroll.setColumnHeaderView(trPanel);
                gridScroll.getColumnHeader().addChangeListener(new ColumnHeaderScroller(gridScroll.getColumnHeader(), gridScroll.getViewport()));
                gridScroll.setRowHeaderView(blPanel);
                if (rowsHeaderType != DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_NONE) {
                    gridScroll.getRowHeader().addChangeListener(new RowHeaderScroller(gridScroll.getRowHeader(), gridScroll.getViewport()));
                }
                gridScroll.setViewportView(brPanel);

                setLayout(new BorderLayout());
                add(gridScroll, BorderLayout.CENTER);
                //
                lheader.setNeightbour(rheader);
                rheader.setNeightbour(lheader);
                lheader.setRegenerateable(true);
                rheader.setRegenerateable(true);
                lheader.regenerate();
                rheader.regenerate();
                rowsRowset.addRowsetListener(scrollReflector);
                configureActions();
            }
            applyEnabled();
            applyFont();
            applyEditable();
            applyBackground();
            applyForeground();
            applyComponentPopupMenu();
            applyGridColor();
            applyRowHeight();
            applyShowHorizontalLines();
            applyShowVerticalLines();
            applyToolTipText();
            applyShowOddRowsInOtherColor();
            applyOddRowsColor();
            applyComponentOrientation(getComponentOrientation());// Swing allows only argumented call. 
            repaint();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        int width = d.width;
        int height = d.height;
        if (width < ModelComponentDecorator.WIDGETS_DEFAULT_WIDTH) {
            width = ModelComponentDecorator.WIDGETS_DEFAULT_WIDTH;
        }
        if (height < ModelComponentDecorator.WIDGETS_DEFAULT_HEIGHT) {
            height = ModelComponentDecorator.WIDGETS_DEFAULT_HEIGHT;
        }
        return new Dimension(width, height);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (lheader != null) {
            lheader.invalidate();
        }
        if (rheader != null) {
            rheader.invalidate();
        }
        if (tlTable != null) {
            tlTable.invalidate();
        }
        if (trTable != null) {
            trTable.invalidate();
        }
        if (blTable != null) {
            blTable.invalidate();
        }
        if (brTable != null) {
            brTable.invalidate();
        }
    }

    @Override
    public boolean try2StopAnyEditing() {
        boolean res = true;
        if (tlTable.getCellEditor() != null && !tlTable.getCellEditor().stopCellEditing()) {
            res = false;
        }
        if (trTable.getCellEditor() != null && !trTable.getCellEditor().stopCellEditing()) {
            res = false;
        }
        if (blTable.getCellEditor() != null && !blTable.getCellEditor().stopCellEditing()) {
            res = false;
        }
        if (brTable.getCellEditor() != null && !brTable.getCellEditor().stopCellEditing()) {
            res = false;
        }
        return res;
    }

    @Override
    public boolean try2CancelAnyEditing() {
        if (tlTable.getCellEditor() != null) {
            tlTable.getCellEditor().cancelCellEditing();
        }
        if (trTable.getCellEditor() != null) {
            trTable.getCellEditor().cancelCellEditing();
        }
        if (blTable.getCellEditor() != null) {
            blTable.getCellEditor().cancelCellEditing();
        }
        if (brTable.getCellEditor() != null) {
            brTable.getCellEditor().cancelCellEditing();
        }
        return tlTable.getCellEditor() == null && trTable.getCellEditor() == null
                && blTable.getCellEditor() == null && brTable.getCellEditor() == null;
    }

    protected Map<TableColumn, GridColumnsGroup> filterLeaves(Map<TableColumn, GridColumnsGroup> cols2groups, TableColumnModel aTableColumnModel, int begIdx, int endIdx) {
        Set<TableColumn> allowedColumns = new HashSet<>();
        for (int i = begIdx; i <= endIdx; i++) {
            allowedColumns.add(aTableColumnModel.getColumn(i));
        }
        Map<TableColumn, GridColumnsGroup> table2Group = new HashMap<>();
        for (Entry<TableColumn, GridColumnsGroup> entry : cols2groups.entrySet()) {
            if (allowedColumns.contains(entry.getKey())) {
                table2Group.put(entry.getKey(), entry.getValue());
            }
        }
        return table2Group;
    }

    public TableColumnModel getColumnModel() {
        return columnModel;
    }

    public TableModel getDeepModel() {
        return deepModel;
    }

    public RowsetsModel getRowsetsModel() {
        return rowsModel;
    }

    public TabularRowsSorter<? extends TableModel> getRowSorter() {
        return rowSorter;
    }

    public static DbGrid getFirstDbGrid(Component aComp) {
        Component lParent = aComp;
        while (lParent != null && !(lParent instanceof DbGrid)) {
            lParent = lParent.getParent();
        }
        if (lParent != null && lParent instanceof DbGrid) {
            return (DbGrid) lParent;
        }
        return null;
    }

    @Override
    public void addProcessedElement(JSObject aRow) {
        processedRows.add(aRow);
        progressIndicator.setVisible(true);
        gridScroll.repaint();
    }

    @Override
    public void removeProcessedElement(JSObject aRow) {
        processedRows.remove(aRow);
        if (processedRows.isEmpty()) {
            progressIndicator.setVisible(false);
        }
        gridScroll.repaint();
    }

    @Override
    public JSObject[] getProcessedElements() {
        return processedRows.toArray(new JSObject[]{});
    }

    @Override
    public boolean isElementProcessed(JSObject anElement) {
        return processedRows.contains(anElement);
    }

    public void select(JSObject anElement) throws Exception {
        selectRow(anElement);
    }

    public void selectRow(JSObject anElement) throws Exception {
        if (anElement != null) {
            int idx = row2Index(anElement);
            if (idx != -1) {
                rowsSelectionModel.addSelectionInterval(idx, idx);
            }
        }
    }

    public JSObject selectRow(Object aId) throws Exception {
        if (rowsModel.getPkLocator().find(aId)) {
            Row lRow = rowsModel.getPkLocator().getRow(0);
            if (lRow != null) {
                selectRow(lRow);
            }
            return lRow;
        } else {
            return null;
        }
    }

    public void unselect(JSObject aRow) throws Exception {
        unselectRow(aRow);
    }

    public void unselectRow(JSObject aRow) throws Exception {
        if (aRow != null) {
            int idx = row2Index(aRow);
            if (idx != -1) {
                rowsSelectionModel.removeSelectionInterval(idx, idx);
            }
        }
    }

    public JSObject unselectRow(Object aId) throws Exception {
        if (rowsModel.getPkLocator().find(aId)) {
            Row lRow = rowsModel.getPkLocator().getRow(0);
            unselectRow(lRow);
            return lRow;
        }
        return null;
    }

    public void clearSelection() {
        columnsSelectionModel.clearSelection();
        rowsSelectionModel.clearSelection();
    }

    public void findSomething() {
        findSomethingAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "startFinding", 0));
    }

    public boolean ensureFetched(ApplicationEntity<?, ?, ?> aEntity, Field aParentField, Object aTargetId) throws Exception {
        if (deepModel instanceof TableFront2TreedModel) {
            TableFront2TreedModel<Row> front = (TableFront2TreedModel<Row>) deepModel;
            if (front.unwrap() instanceof RowsetsTreedModel) {
                RowsetsTreedModel rModel = (RowsetsTreedModel) front.unwrap();
                Rowset r = aEntity.getRowset();
                if (!rowsModel.getPkLocator().getFields().isEmpty()) {
                    int pkColIndex = rowsModel.getPkLocator().getFields().get(0);
                    int parentColIndex = r.getFields().find(aParentField.getName());
                    List<Row> toFetch = new ArrayList<>();
                    toFetch.addAll(r.getCurrent());
                    int fetched = toFetch.size();
                    while (!toFetch.isEmpty() && fetched != 0) {
                        fetched = 0;
                        for (int i = toFetch.size() - 1; i >= 0; i--) {
                            Row rowToFetch = toFetch.get(i);
                            if (!rModel.getPkLocator().find(new Object[]{rowToFetch.getColumnObject(pkColIndex)})) {
                                if (rModel.getPkLocator().find(new Object[]{rowToFetch.getColumnObject(parentColIndex)})) {
                                    Row innerParentRow = rModel.getPkLocator().getRow(0);
                                    front.expand(innerParentRow, false);
                                    toFetch.remove(rowToFetch);
                                    ++fetched;
                                }
                            } else {
                                toFetch.remove(rowToFetch);
                                ++fetched;
                            }
                        }
                    }
                    return rModel.getPkLocator().find(new Object[]{aTargetId});
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean makeVisible(JSObject anElement) throws Exception {
        return makeVisible(anElement, true);
    }

    @Override
    public boolean makeVisible(JSObject anElement, boolean need2Select) throws Exception {
        if (anElement != null) {
            // Let's process possible tree paths
            if (rowsModel instanceof RowsetsTreedModel) {
                assert deepModel instanceof TableFront2TreedModel<?>;
                TableFront2TreedModel<JSObject> front = (TableFront2TreedModel<JSObject>) deepModel;
                List<JSObject> path = front.buildPathTo(anElement);
                for (int i = 0; i < path.size() - 1; i++) {
                    front.expand(path.get(i), true);
                }
            }
            int modelIndex = row2Index(anElement);
            if (modelIndex != -1) {
                int generalViewIndex = rowSorter.convertRowIndexToView(modelIndex);
                if (brTable.getColumnCount() > 0) {
                    int brViewIndex = generalViewIndex - trTable.getRowCount();
                    if (brViewIndex >= 0) {
                        Rectangle cellRect = brTable.getCellRect(brViewIndex, 0, false);
                        cellRect.height *= 10;
                        brTable.scrollRectToVisible(cellRect);
                    }
                } else if (blTable.getColumnCount() > 0) {
                    int blViewIndex = generalViewIndex - tlTable.getRowCount();
                    if (blViewIndex >= 0) {
                        Rectangle cellRect = blTable.getCellRect(blViewIndex, 0, false);
                        cellRect.height *= 10;
                        blTable.scrollRectToVisible(cellRect);
                    }
                }
                if (need2Select) {
                    columnsSelectionModel.setSelectionInterval(0, columnModel.getColumnCount() - 1);
                    rowsSelectionModel.setSelectionInterval(generalViewIndex, generalViewIndex);
                }
                return true;
            }
        }
        return false;
    }

    public boolean makeVisible(Object aId) throws Exception {
        return makeVisible(aId, true);
    }

    public boolean makeVisible(Object aId, boolean need2Select) throws Exception {
        aId = ScriptUtils.toJava(aId);
        aId = rowsModel.getRowsRowset().getConverter().convert2RowsetCompatible(aId, rowsModel.getRowsRowset().getFields().get(rowsModel.getPkLocator().getFields().get(0)).getTypeInfo());
        if (rowsModel.getPkLocator().find(aId)) {
            return makeVisible(rowsModel.getPkLocator().getRow(0), need2Select);
        }
        return false;
    }

    public boolean isCurrentRow(JSObject anElement) {
        return getCurrentRow() == anElement;
    }

    public JSObject getCurrentRow() {
        try {
            if (rowsEntity != null && rowsEntity.getRowset() != null) {
                Rowset rowset = rowsEntity.getRowset();
                return rowset.getCurrentRow();
            }
        } catch (Exception ex) {
            Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public JTable getFocusedTable() {
        JTable focusedTable = null;
        if (tlTable != null && tlTable.hasFocus()) {
            focusedTable = tlTable;
        }
        if (trTable != null && trTable.hasFocus()) {
            focusedTable = trTable;
        }
        if (blTable != null && blTable.hasFocus()) {
            focusedTable = blTable;
        }
        if (brTable != null && brTable.hasFocus()) {
            focusedTable = brTable;
        }
        return focusedTable;
    }

    private String transformCellValue(Object aValue, int aCol, boolean isData) {
        if (aValue != null) {
            Object value = null;
            if (isData) {
                if (aValue instanceof CellData) {
                    CellData cd = (CellData) aValue;
                    value = cd.getData();
                } else {
                    value = aValue;
                }
            } else {
                TableColumn tc = getColumnModel().getColumn(aCol);
                TableCellRenderer renderer = tc.getCellRenderer();
                if (renderer instanceof DbCombo) {
                    try {
                        value = ((DbCombo) renderer).achiveDisplayValue(aValue instanceof CellData ? ((CellData) aValue).getData() : aValue);
                    } catch (Exception ex) {
                        Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, "Could not get cell value", ex);
                    }
                } else {
                    if (aValue instanceof CellData) {
                        CellData cd = (CellData) aValue;
                        value = cd.getDisplay() != null ? cd.getDisplay() : cd.getData();
                    } else {
                        value = aValue;
                    }
                }
            }
            if (value != null) {
                return value.toString();
            }
        }
        return "";
    }

    private String[] transformRow(int aRow, boolean selectedOnly, boolean isData) {
        TableModel view = getDeepModel();
        int minCol = 0;
        int maxCol = view.getColumnCount();
        String[] res = new String[maxCol];
        int curentColumn = 0;
        for (int col = minCol; col < getColumnModel().getColumnCount(); col++) {
            TableColumn tc = getColumnModel().getColumn(col);
            if (tc.getWidth() > 0 && !(tc instanceof RowHeaderTableColumn)) {
                if (selectedOnly) {
                    if (getColumnsSelectionModel().isSelectedIndex(col)) {
                        res[curentColumn] = transformCellValue(view.getValueAt(aRow, tc.getModelIndex()), col, isData);
                        curentColumn++;
                    }
                } else {
                    res[curentColumn] = transformCellValue(view.getValueAt(aRow, tc.getModelIndex()), col, isData);
                    curentColumn++;
                }
            }
        }
        return res;
    }

    private Object[] convertView(String[][] aCells) {
        Object[] cells = new Object[aCells.length];
        for (int i = 0; i < aCells.length; i++) {
            String[] row = aCells[i];
            Object[] o = new Object[row.length];
            System.arraycopy(row, 0, o, 0, row.length);
            cells[i] = o;
        }
        return cells;
    }

    public Object[] getCells() {
        Object[] cells = convertView(getGridView(false, false));
        return cells;
    }

    public Object[] getSelectedCells() {
        Object[] selectedCells = convertView(getGridView(true, false));
        return selectedCells;
    }

    private String[][] getGridView(boolean selectedOnly, boolean isData) {
        TableModel cellsModel = getDeepModel();
        if (cellsModel != null) {
            int minRow = 0;
            int maxRow = cellsModel.getRowCount();
            int columnCount = cellsModel.getColumnCount();
            String[][] res = new String[maxRow][columnCount];
            ListSelectionModel rowSelecter = getRowsSelectionModel();
            for (int viewRow = minRow; viewRow < maxRow; viewRow++) {
                if (selectedOnly) {
                    if (rowSelecter.isSelectedIndex(viewRow)) {
                        res[viewRow] = transformRow(rowSorter.convertRowIndexToModel(viewRow), selectedOnly, isData);
                    }
                } else {
                    res[viewRow] = transformRow(rowSorter.convertRowIndexToModel(viewRow), selectedOnly, isData);
                }
            }
            return res;
        } else {
            return new String[0][0];
        }
    }

    protected class TablesMousePropagator implements MouseListener, MouseMotionListener, MouseWheelListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, DbGrid.this);
                for (MouseListener l : DbGrid.this.getMouseListeners()) {
                    l.mouseClicked(e);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, DbGrid.this);
                for (MouseListener l : DbGrid.this.getMouseListeners()) {
                    l.mousePressed(e);
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, DbGrid.this);
                for (MouseListener l : DbGrid.this.getMouseListeners()) {
                    l.mouseReleased(e);
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, DbGrid.this);
                for (MouseListener l : DbGrid.this.getMouseListeners()) {
                    l.mouseEntered(e);
                }
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, DbGrid.this);
                for (MouseListener l : DbGrid.this.getMouseListeners()) {
                    l.mouseExited(e);
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, DbGrid.this);
                for (MouseMotionListener l : DbGrid.this.getMouseMotionListeners()) {
                    l.mouseDragged(e);
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, DbGrid.this);
                for (MouseMotionListener l : DbGrid.this.getMouseMotionListeners()) {
                    l.mouseMoved(e);
                }
            }
        }

        protected MouseWheelEvent sendWheelTo(MouseWheelEvent e, Component aComp) {
            MouseEvent me = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, aComp);
            e = new MouseWheelEvent(aComp, e.getID(), e.getWhen(), e.getModifiers(), me.getX(), me.getY(), me.getXOnScreen(), me.getYOnScreen(), e.getClickCount(), e.isPopupTrigger(), e.getScrollType(), e.getScrollAmount(), e.getWheelRotation());
            for (MouseWheelListener l : aComp.getMouseWheelListeners()) {
                l.mouseWheelMoved(e);
            }
            return e;
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.getSource() instanceof Component) {
                MouseWheelEvent sended = sendWheelTo(e, DbGrid.this);
                if (!sended.isConsumed()) {
                    sendWheelTo(e, DbGrid.this.gridScroll);
                }
            }
        }
    }

    protected class TablesFocusManager implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getModifiers() == 0) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (e.getSource() == tlTable) {
                        if (blTable != null && blTable.getRowCount() > 0 && tlTable.getSelectionModel().getLeadSelectionIndex() == tlTable.getRowCount() - 1) {
                            int colIndex = tlTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                            EventQueue.invokeLater(() -> {
                                tlTable.clearSelection();
                                blTable.getSelectionModel().setSelectionInterval(0, 0);
                                blTable.getColumnModel().getSelectionModel().setSelectionInterval(colIndex, colIndex);
                                blTable.requestFocus();
                            });
                        }
                    } else if (e.getSource() == trTable) {
                        if (brTable != null && brTable.getRowCount() > 0 && trTable.getSelectionModel().getLeadSelectionIndex() == trTable.getRowCount() - 1) {
                            EventQueue.invokeLater(() -> {
                                int colIndex = trTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                                trTable.clearSelection();
                                brTable.getSelectionModel().setSelectionInterval(0, 0);
                                brTable.getColumnModel().getSelectionModel().setSelectionInterval(colIndex, colIndex);
                                brTable.requestFocus();
                            });
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (e.getSource() == blTable) {
                        if (tlTable != null && tlTable.getRowCount() > 0 && blTable.getSelectionModel().getLeadSelectionIndex() == 0) {
                            int colIndex = blTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                            EventQueue.invokeLater(() -> {
                                blTable.clearSelection();
                                tlTable.getSelectionModel().setSelectionInterval(tlTable.getRowCount() - 1, tlTable.getRowCount() - 1);
                                tlTable.getColumnModel().getSelectionModel().setSelectionInterval(colIndex, colIndex);
                                tlTable.requestFocus();
                            });
                        }
                    } else if (e.getSource() == brTable) {
                        if (trTable != null && trTable.getRowCount() > 0 && brTable.getSelectionModel().getLeadSelectionIndex() == 0) {
                            int colIndex = brTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                            EventQueue.invokeLater(() -> {
                                brTable.clearSelection();
                                trTable.getSelectionModel().setSelectionInterval(trTable.getRowCount() - 1, trTable.getRowCount() - 1);
                                trTable.getColumnModel().getSelectionModel().setSelectionInterval(colIndex, colIndex);
                                trTable.requestFocus();
                            });
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (e.getSource() == blTable) {
                        int blLeadIndex = blTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                        int blColCount = blTable.getColumnModel().getColumnCount();
                        while (blLeadIndex < blColCount - 1 && GridTable.skipableColumn(blTable.getColumnModel().getColumn(blLeadIndex + 1))) {
                            blLeadIndex++;
                        }
                        if (brTable != null && brTable.getColumnCount() > 0
                                && blLeadIndex == blColCount - 1) {
                            int lColIndex = 0;
                            if (!(brTable.getColumnModel().getColumn(lColIndex) instanceof RowHeaderTableColumn)) {
                                while (GridTable.skipableColumn(brTable.getColumnModel().getColumn(lColIndex))) {
                                    lColIndex++;
                                }
                                if (lColIndex >= 0 && lColIndex < brTable.getColumnModel().getColumnCount()) {
                                    final int colToSelect = lColIndex;
                                    EventQueue.invokeLater(() -> {
                                        brTable.getColumnModel().getSelectionModel().setSelectionInterval(colToSelect, colToSelect);
                                        brTable.requestFocus();
                                    });
                                }
                            }
                        }
                    } else if (e.getSource() == tlTable) {
                        int tlLeadIndex = tlTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                        int tlColCount = tlTable.getColumnModel().getColumnCount();
                        while (tlLeadIndex < tlColCount - 1 && GridTable.skipableColumn(tlTable.getColumnModel().getColumn(tlLeadIndex + 1))) {
                            tlLeadIndex++;
                        }
                        if (trTable != null && trTable.getColumnCount() > 0 && (tlLeadIndex == tlColCount - 1)) {
                            int lColIndex = 0;
                            if (!(trTable.getColumnModel().getColumn(lColIndex) instanceof RowHeaderTableColumn)) {
                                while (GridTable.skipableColumn(trTable.getColumnModel().getColumn(lColIndex))) {
                                    lColIndex++;
                                }
                                if (lColIndex >= 0 && lColIndex < trTable.getColumnModel().getColumnCount()) {
                                    final int col2Select = lColIndex;
                                    EventQueue.invokeLater(() -> {
                                        trTable.getColumnModel().getSelectionModel().setSelectionInterval(col2Select, col2Select);
                                        trTable.requestFocus();
                                    });
                                }
                            }
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (e.getSource() == trTable) {
                        int trLeadIndex = trTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                        while (trLeadIndex > 0 && GridTable.skipableColumn(trTable.getColumnModel().getColumn(trLeadIndex - 1))) {
                            trLeadIndex--;
                        }
                        if (tlTable != null && tlTable.getColumnCount() > 0
                                && trLeadIndex == 0) {
                            if (tlTable.getColumnModel().getColumnCount() > 0) {
                                int lColIndex = tlTable.getColumnModel().getColumnCount() - 1;
                                if (!(tlTable.getColumnModel().getColumn(lColIndex) instanceof RowHeaderTableColumn)) {
                                    while (GridTable.skipableColumn(tlTable.getColumnModel().getColumn(lColIndex))) {
                                        lColIndex--;
                                    }
                                    if (lColIndex >= 0 && lColIndex < tlTable.getColumnModel().getColumnCount()) {
                                        final int col2Select = lColIndex;
                                        EventQueue.invokeLater(() -> {
                                            tlTable.getColumnModel().getSelectionModel().setSelectionInterval(col2Select, col2Select);
                                            tlTable.requestFocus();
                                        });
                                    }
                                }
                            }
                        }
                    } else if (e.getSource() == brTable) {
                        int brLeadIndex = brTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                        while (brLeadIndex > 0 && GridTable.skipableColumn(brTable.getColumnModel().getColumn(brLeadIndex - 1))) {
                            brLeadIndex--;
                        }
                        if (blTable != null && blTable.getColumnCount() > 0
                                && brLeadIndex == 0) {
                            if (blTable.getColumnModel().getColumnCount() > 0) {
                                int lColIndex = blTable.getColumnModel().getColumnCount() - 1;
                                if (!(blTable.getColumnModel().getColumn(lColIndex) instanceof RowHeaderTableColumn)) {
                                    while (GridTable.skipableColumn(blTable.getColumnModel().getColumn(lColIndex))) {
                                        lColIndex--;
                                    }
                                    if (lColIndex >= 0 && lColIndex < blTable.getColumnModel().getColumnCount()) {
                                        final int col2Select = lColIndex;
                                        EventQueue.invokeLater(() -> {
                                            blTable.getColumnModel().getSelectionModel().setSelectionInterval(col2Select, col2Select);
                                            blTable.requestFocus();
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    protected class DbGridDeleteSelectedAction extends AbstractAction {

        DbGridDeleteSelectedAction() {
            super();
            putValue(Action.NAME, Form.getLocalizedString(DbGridDeleteSelectedAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, Form.getLocalizedString(DbGridDeleteSelectedAction.class.getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_DOWN_MASK));
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return !rowsSelectionModel.isSelectionEmpty();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteRow();
        }
    }

    protected class DbGridInsertAction extends AbstractAction {

        DbGridInsertAction() {
            super();
            putValue(Action.NAME, Form.getLocalizedString(DbGridInsertAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, Form.getLocalizedString(DbGridInsertAction.class.getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
            setEnabled(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            insertRow();
        }
    }

    protected class DbGridInsertChildAction extends AbstractAction {

        DbGridInsertChildAction() {
            super();
            putValue(Action.NAME, Form.getLocalizedString(DbGridInsertChildAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, Form.getLocalizedString(DbGridInsertChildAction.class.getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.ALT_DOWN_MASK));
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (insertable && !(rowsModel.getRowsRowset() instanceof ParametersRowset)) {
                    rowsSelectionModel.removeListSelectionListener(generalSelectionChangesReflector);
                    try {
                        if (rowsModel instanceof RowsetsTreedModel) {
                            int parentColIndex = ((RowsetsTreedModel) rowsModel).getParentFieldIndex();
                            Object parentColValue = null;
                            if (!rowsModel.getRowsRowset().isEmpty()
                                    && !rowsModel.getRowsRowset().isBeforeFirst()
                                    && !rowsModel.getRowsRowset().isAfterLast()) {
                                Object[] pkValues = rowsModel.getRowsRowset().getCurrentRow().getPKValues();
                                if (pkValues != null && pkValues.length == 1) {
                                    parentColValue = pkValues[0];
                                }
                            }
                            rowsModel.getRowsRowset().insert(parentColIndex, parentColValue);
                        } else {
                            rowsModel.getRowsRowset().insert();
                        }
                    } finally {
                        rowsSelectionModel.addListSelectionListener(generalSelectionChangesReflector);
                    }
                    Row insertedRow = rowsModel.getRowsRowset().getCurrentRow();
                    assert insertedRow.isInserted();
                    makeVisible(insertedRow);
                }
            } catch (Exception ex) {
                Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected class DbGridRowInfoAction extends AbstractAction {

        DbGridRowInfoAction() {
            super();
            putValue(Action.NAME, Form.getLocalizedString(DbGridRowInfoAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, Form.getLocalizedString(DbGridRowInfoAction.class.getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK));
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return !rowsSelectionModel.isSelectionEmpty();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!rowsModel.getRowsRowset().isEmpty()
                    && !rowsModel.getRowsRowset().isBeforeFirst()
                    && !rowsModel.getRowsRowset().isAfterLast()) {
                Row row = rowsModel.getRowsRowset().getCurrentRow();
                if (row != null) {
                    JOptionPane.showInputDialog(DbGrid.this, Form.getLocalizedString("rowPkValues"), (String) getValue(Action.SHORT_DESCRIPTION), JOptionPane.INFORMATION_MESSAGE, null, null, StringUtils.join(", ", StringUtils.toStringArray(row.getPKValues())));
                }
            }
        }
    }

    protected class DbGridGotoRowAction extends AbstractAction {

        DbGridGotoRowAction() {
            super();
            putValue(Action.NAME, Form.getLocalizedString(DbGridGotoRowAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, Form.getLocalizedString(DbGridGotoRowAction.class.getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK));
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!rowsModel.getRowsRowset().isEmpty()
                    && !rowsModel.getRowsRowset().isBeforeFirst()
                    && !rowsModel.getRowsRowset().isAfterLast()) {
                Row row = rowsModel.getRowsRowset().getCurrentRow();
                if (row != null) {
                    Object oInput = JOptionPane.showInputDialog(DbGrid.this, Form.getLocalizedString("rowPkValues"), (String) getValue(Action.SHORT_DESCRIPTION), JOptionPane.INFORMATION_MESSAGE, null, null, null);
                    if (oInput != null) {
                        try {
                            makeVisible(oInput);
                        } catch (Exception ex) {
                            Logger.getLogger(DbGrid.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }

    protected class DbGridCopyCellAction extends AbstractAction {

        DbGridCopyCellAction() {
            super();
            putValue(Action.NAME, Form.getLocalizedString(DbGridCopyCellAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, Form.getLocalizedString(DbGridCopyCellAction.class.getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            StringBuilder sbCells = new StringBuilder();
            String[][] cells = getGridView(true, false);
            for (int i = 0; i < cells.length; i++) {
                StringBuilder sbRow = new StringBuilder();
                String[] row = cells[i];
                for (int j = 0; j < row.length; j++) {
                    String value = row[j];
                    if (value != null) {
                        if (sbRow.length() > 0) {
                            sbRow.append("\t");
                        }
                        sbRow.append(value);
                    }
                }
                if (sbRow.length() > 0) {
                    if (sbCells.length() > 0) {
                        sbCells.append("\n");
                    }
                    sbCells.append(sbRow);
                }
            }
            StringSelection ss = new StringSelection(sbCells.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
        }
    }

    protected class DbGridFindSomethingAction extends AbstractAction {

        protected JFrame findFrame;

        DbGridFindSomethingAction() {
            super();
            putValue(Action.NAME, Form.getLocalizedString(DbGridFindSomethingAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, Form.getLocalizedString(DbGridFindSomethingAction.class.getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
            setEnabled(true);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                if (findFrame == null) {
                    findFrame = new JFrame(Form.getLocalizedString("Find"));
                    findFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    findFrame.getContentPane().setLayout(new BorderLayout());
                    findFrame.getContentPane().add(new GridSearchView(DbGrid.this), BorderLayout.CENTER);
                    findFrame.setIconImage(IconCache.getIcon("16x16/binocular.png").getImage());
                    findFrame.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
                    findFrame.setAlwaysOnTop(true);
                    findFrame.setLocationByPlatform(true);
                    findFrame.pack();
                }
                findFrame.setVisible(true);
            }
        }
    }

    protected void configureActions() {
        Action deleteAction = null;
        Action insertAction = null;
        Action insertChildAction = null;
        findSomethingAction = new DbGridFindSomethingAction();
        deleteAction = new DbGridDeleteSelectedAction();
        putAction(deleteAction);
        insertAction = new DbGridInsertAction();
        putAction(insertAction);
        insertChildAction = new DbGridInsertChildAction();
        putAction(insertChildAction);
        putAction(findSomethingAction);
        Action rowInfoAction = new DbGridRowInfoAction();
        putAction(rowInfoAction);
        Action goToRowAction = new DbGridGotoRowAction();
        putAction(goToRowAction);
        Action copyCellAction = new DbGridCopyCellAction();
        putAction(copyCellAction);
        fillInputMap(tlTable.getInputMap(), deleteAction, insertAction, insertChildAction, findSomethingAction, rowInfoAction, goToRowAction, copyCellAction);
        fillInputMap(trTable.getInputMap(), deleteAction, insertAction, insertChildAction, findSomethingAction, rowInfoAction, goToRowAction, copyCellAction);
        fillInputMap(blTable.getInputMap(), deleteAction, insertAction, insertChildAction, findSomethingAction, rowInfoAction, goToRowAction, copyCellAction);
        fillInputMap(brTable.getInputMap(), deleteAction, insertAction, insertChildAction, findSomethingAction, rowInfoAction, goToRowAction, copyCellAction);
    }

    protected void fillInputMap(InputMap aInputMap, Action... actions) {
        for (Action action : actions) {
            if (action != null) {
                if (action instanceof DbGridCopyCellAction) {
                    aInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.CTRL_DOWN_MASK), action.getClass().getName());
                }
                KeyStroke keyStroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
                if (keyStroke != null) {
                    aInputMap.put(keyStroke, action.getClass().getName());
                }
            }
        }
    }

    protected void checkDbGridActions() {
        ActionMap actionMap = getActionMap();
        if (actionMap != null) {
            for (Object lkey : actionMap.keys()) {
                if (lkey != null) {
                    Action action = actionMap.get(lkey);
                    action.setEnabled(action.isEnabled());
                }
            }
        }
    }

    public CascadedStyle getStyle() {
        return style;
    }
}
