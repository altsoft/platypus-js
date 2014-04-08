/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets.grid;

import com.bearsoft.gwt.ui.dnd.XDataTransfer;
import com.bearsoft.gwt.ui.menu.MenuItemCheckBox;
import com.bearsoft.gwt.ui.widgets.grid.builders.NullHeaderOrFooterBuilder;
import com.bearsoft.gwt.ui.widgets.grid.builders.ThemedCellTableBuilder;
import com.bearsoft.gwt.ui.widgets.grid.builders.ThemedHeaderOrFooterBuilder;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.StyleElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionModel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author mg
 * @param <T>
 */
public class Grid<T> extends SimplePanel implements ProvidesResize, RequiresResize {

	public static final String RULER_STYLE = "grid-ruler";
	public static final String COLUMN_PHANTOM_STYLE = "grid-column-phantom";
	public static final String COLUMNS_CHEVRON_STYLE = "grid-columns-chevron";
	private static final int MINIMUM_COLUMN_WIDTH = 20;
	//
	protected FlexTable hive;
	protected SimplePanel headerLeftContainer;
	protected GridSection<T> headerLeft;
	protected SimplePanel headerRightContainer;
	protected GridSection<T> headerRight;
	protected SimplePanel frozenLeftContainer;
	protected GridSection<T> frozenLeft;
	protected SimplePanel frozenRightContainer;
	protected GridSection<T> frozenRight;
	protected SimplePanel scrollableLeftContainer;
	protected GridSection<T> scrollableLeft;
	protected ScrollPanel scrollableRightContainer;
	protected GridSection<T> scrollableRight;
	protected SimplePanel footerLeftContainer;
	protected GridSection<T> footerLeft;
	protected ScrollPanel footerRightContainer;
	protected GridSection<T> footerRight;
	//
	protected HTML columnsChevron = new HTML();
	//
	protected int rowsHeight;
	protected String dynamicCellClassName = "grid-cell-" + Document.get().createUniqueId();
	protected StyleElement styleElement = Document.get().createStyleElement();

	protected ListDataProvider<T> dataProvider;

	protected int frozenColumns;
	protected int frozenRows;

	public Grid(ProvidesKey<T> aKeyProvider) {
		super();
		getElement().getStyle().setPosition(Style.Position.RELATIVE);
		getElement().appendChild(styleElement);
		setRowsHeight(25);
		hive = new FlexTable();
		setWidget(hive);
		hive.setCellPadding(0);
		hive.setCellSpacing(0);
		hive.setBorderWidth(0);
		headerLeft = new GridSection<>(aKeyProvider);
		headerLeftContainer = new ScrollPanel(headerLeft);
		headerRight = new GridSection<>(aKeyProvider);
		headerRightContainer = new ScrollPanel(headerRight);
		frozenLeft = new GridSection<T>(aKeyProvider) {

			@Override
			protected void replaceAllChildren(List<T> values, SafeHtml html) {
				super.replaceAllChildren(values, html);
				footerLeft.redrawFooters();
			}

			@Override
			protected void replaceChildren(List<T> values, int start, SafeHtml html) {
				super.replaceChildren(values, start, html);
				footerLeft.redrawFooters();
			}

		};

		frozenLeftContainer = new ScrollPanel(frozenLeft);
		frozenRight = new GridSection<T>(aKeyProvider) {

			@Override
			protected void replaceAllChildren(List<T> values, SafeHtml html) {
				super.replaceAllChildren(values, html);
				footerRight.redrawFooters();
			}

			@Override
			protected void replaceChildren(List<T> values, int start, SafeHtml html) {
				super.replaceChildren(values, start, html);
				footerRight.redrawFooters();
			}

		};
		frozenRightContainer = new ScrollPanel(frozenRight);
		scrollableLeft = new GridSection<T>(aKeyProvider) {

			@Override
			protected void replaceAllChildren(List<T> values, SafeHtml html) {
				super.replaceAllChildren(values, html);
				footerLeft.redrawFooters();
			}

			@Override
			protected void replaceChildren(List<T> values, int start, SafeHtml html) {
				super.replaceChildren(values, start, html);
				footerLeft.redrawFooters();
			}

		};
		scrollableLeftContainer = new ScrollPanel(scrollableLeft);
		scrollableRight = new GridSection<T>(aKeyProvider) {

			@Override
			protected void replaceAllChildren(List<T> values, SafeHtml html) {
				super.replaceAllChildren(values, html);
				footerRight.redrawFooters();
			}

			@Override
			protected void replaceChildren(List<T> values, int start, SafeHtml html) {
				super.replaceChildren(values, start, html);
				footerRight.redrawFooters();
			}
		};
		scrollableRightContainer = new ScrollPanel(scrollableRight);
		footerLeft = new GridSection<>(aKeyProvider);
		footerLeftContainer = new ScrollPanel(footerLeft);
		footerRight = new GridSection<>(aKeyProvider);
		footerRightContainer = new ScrollPanel(footerRight);
		// positioning context / overflow setup
		// overflow
		for (Widget w : new Widget[] { headerLeftContainer, headerRightContainer, frozenLeftContainer, frozenRightContainer, scrollableLeftContainer, footerLeftContainer, footerRightContainer }) {
			w.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		}
		// scrollableRightContainer.getElement().getStyle().setOverflow(Style.Overflow.AUTO);
		// default value
		// context
		for (Widget w : new Widget[] { headerLeftContainer, headerRightContainer, frozenLeftContainer, frozenRightContainer, scrollableLeftContainer, scrollableRightContainer, footerLeftContainer,
		        footerRightContainer }) {
			w.getElement().getFirstChildElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		}
		// propagation of some widths
		headerLeft.setWidthPropagator(new GridWidthPropagator<T>(headerLeft) {

			@Override
			public void changed() {
				super.changed();
				propagateHeaderLeftWidth();
			}

		});
		for (GridSection<T> section : (GridSection<T>[]) new GridSection<?>[] { headerRight, frozenLeft, frozenRight, scrollableLeft, scrollableRight, footerLeft, footerRight }) {
			section.setWidthPropagator(new GridWidthPropagator<>(section));
		}
		headerLeft.setColumnsPartners(new AbstractCellTable[] { frozenLeft, scrollableLeft, footerLeft });
		headerRight.setColumnsPartners(new AbstractCellTable[] { frozenRight, scrollableRight, footerRight });
		ColumnsRemover leftColumnsRemover = new ColumnsRemoverAdapter<T>(headerLeft, frozenLeft, scrollableLeft, footerLeft);
		ColumnsRemover rightColumnsRemover = new ColumnsRemoverAdapter<T>(headerRight, frozenRight, scrollableRight, footerRight);
		for (GridSection<T> section : (GridSection<T>[]) new GridSection<?>[] { headerLeft, frozenLeft, scrollableLeft, footerLeft }) {
			section.setColumnsRemover(leftColumnsRemover);
		}
		for (GridSection<T> section : (GridSection<T>[]) new GridSection<?>[] { headerRight, frozenRight, scrollableRight, footerRight }) {
			section.setColumnsRemover(rightColumnsRemover);
		}
		for (GridSection<T> section : (GridSection<T>[]) new GridSection<?>[] { frozenLeft, scrollableLeft, footerLeft }) {
			section.setHeaderSource(headerLeft);
		}
		for (GridSection<T> section : (GridSection<T>[]) new GridSection<?>[] { frozenRight, scrollableRight, footerRight }) {
			section.setHeaderSource(headerRight);
		}
		for (GridSection<T> section : (GridSection<T>[]) new GridSection<?>[] { headerLeft, frozenLeft, scrollableLeft }) {
			section.setFooterSource(footerLeft);
		}
		for (GridSection<T> section : (GridSection<T>[]) new GridSection<?>[] { headerRight, frozenRight, scrollableRight }) {
			section.setFooterSource(footerRight);
		}

		// hive organization
		hive.setWidget(0, 0, headerLeftContainer);
		hive.setWidget(0, 1, headerRightContainer);
		hive.setWidget(1, 0, frozenLeftContainer);
		hive.setWidget(1, 1, frozenRightContainer);
		hive.setWidget(2, 0, scrollableLeftContainer);
		hive.setWidget(2, 1, scrollableRightContainer);
		hive.setWidget(3, 0, footerLeftContainer);
		hive.setWidget(3, 1, footerRightContainer);

		for (Widget w : new Widget[] { headerLeftContainer, headerRightContainer, frozenLeftContainer, frozenRightContainer, scrollableLeftContainer, scrollableRightContainer, footerLeftContainer,
		        footerRightContainer }) {
			w.setWidth("100%");
			w.setHeight("100%");
		}
		// misc
		for (Widget w : new Widget[] { headerRightContainer, frozenRightContainer, footerRightContainer, scrollableLeftContainer }) {
			w.getElement().getParentElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		}
		hive.getElement().getStyle().setTableLayout(Style.TableLayout.FIXED);
		hive.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		for (CellTable<?> tbl : new CellTable<?>[] { headerLeft, headerRight, frozenLeft, frozenRight, scrollableLeft, scrollableRight, footerLeft, footerRight }) {
			tbl.setTableLayoutFixed(true);
		}
		// header
		headerLeft.setHeaderBuilder(new ThemedHeaderOrFooterBuilder<T>(headerLeft, false));
		headerLeft.setFooterBuilder(new NullHeaderOrFooterBuilder<T>(headerLeft, true));
		headerRight.setHeaderBuilder(new ThemedHeaderOrFooterBuilder<T>(headerRight, false));
		headerRight.setFooterBuilder(new NullHeaderOrFooterBuilder<T>(headerRight, true));
		// footer
		footerLeft.setHeaderBuilder(new NullHeaderOrFooterBuilder<T>(footerLeft, false));
		footerLeft.setFooterBuilder(new ThemedHeaderOrFooterBuilder<T>(footerLeft, true));
		footerRight.setHeaderBuilder(new NullHeaderOrFooterBuilder<T>(footerRight, false));
		footerRight.setFooterBuilder(new ThemedHeaderOrFooterBuilder<T>(footerRight, true));
		// data bodies
		for (GridSection<?> section : new GridSection<?>[] { frozenLeft, frozenRight, scrollableLeft, scrollableRight }) {
			GridSection<T> gSection = (GridSection<T>) section;
			gSection.setHeaderBuilder(new NullHeaderOrFooterBuilder<T>(gSection, false));
			gSection.setFooterBuilder(new NullHeaderOrFooterBuilder<T>(gSection, true));
		}
		for (GridSection<?> section : new GridSection<?>[] { headerLeft, headerRight, frozenLeft, frozenRight, scrollableLeft, scrollableRight, footerLeft, footerRight }) {
			section.setAutoHeaderRefreshDisabled(true);
		}
		for (GridSection<?> section : new GridSection<?>[] { headerLeft, headerRight, footerLeft, footerRight }) {
			section.setAutoFooterRefreshDisabled(true);
		}
		// cells
		for (GridSection<?> section : new GridSection<?>[] { frozenLeft, frozenRight, scrollableLeft, scrollableRight }) {
			GridSection<T> gSection = (GridSection<T>) section;
			gSection.setTableBuilder(new ThemedCellTableBuilder<>(gSection, dynamicCellClassName));
			//
		}

		scrollableRightContainer.addScrollHandler(new ScrollHandler() {

			@Override
			public void onScroll(ScrollEvent event) {
				int aimTop = scrollableRightContainer.getElement().getScrollTop();
				int aimLeft = scrollableRightContainer.getElement().getScrollLeft();

				scrollableLeftContainer.getElement().setScrollTop(aimTop);
				int factTopDelta = aimTop - scrollableLeftContainer.getElement().getScrollTop();
				if (factTopDelta > 0) {
					scrollableLeftContainer.getElement().getStyle().setBottom(factTopDelta, Style.Unit.PX);
				} else {
					scrollableLeftContainer.getElement().getStyle().clearBottom();
				}
				headerRightContainer.getElement().setScrollLeft(aimLeft);
				int factLeftDelta0 = aimLeft - headerRightContainer.getElement().getScrollLeft();
				if (factLeftDelta0 > 0) {
					headerRightContainer.getElement().getStyle().setRight(factLeftDelta0, Style.Unit.PX);
				} else {
					headerRightContainer.getElement().getStyle().clearRight();
				}
				frozenRightContainer.getElement().setScrollLeft(aimLeft);
				int factLeftDelta1 = aimLeft - frozenRightContainer.getElement().getScrollLeft();
				if (factLeftDelta1 > 0) {
					frozenRightContainer.getElement().getStyle().setRight(factLeftDelta1, Style.Unit.PX);
				} else {
					frozenRightContainer.getElement().getStyle().clearRight();
				}
				footerRightContainer.getElement().setScrollLeft(scrollableRightContainer.getElement().getScrollLeft());
				int factLeftDelta2 = aimLeft - footerRightContainer.getElement().getScrollLeft();
				if (factLeftDelta2 > 0) {
					footerRightContainer.getElement().getStyle().setRight(factLeftDelta2, Style.Unit.PX);
				} else {
					footerRightContainer.getElement().getStyle().clearRight();
				}
			}

		});
		ghostLine = Document.get().createDivElement();
		ghostLine.addClassName(RULER_STYLE);
		ghostLine.getStyle().setPosition(Style.Position.ABSOLUTE);
		ghostLine.getStyle().setTop(0, Style.Unit.PX);
		ghostColumn = Document.get().createDivElement();
		ghostColumn.addClassName(COLUMN_PHANTOM_STYLE);
		ghostColumn.getStyle().setPosition(Style.Position.ABSOLUTE);
		ghostColumn.getStyle().setTop(0, Style.Unit.PX);
		addDomHandler(new DragEnterHandler() {

			@Override
			public void onDragEnter(DragEnterEvent event) {
				if (DraggedColumn.instance != null && DraggedColumn.instance.getColumnIndex() != -1) {
					if (DraggedColumn.instance.isMove()) {
						event.preventDefault();
						event.stopPropagation();
						DraggedColumn<T> target = findTargetDraggedColumn(event.getNativeEvent().getEventTarget());
						if (target != null) {
							showColumnMoveDecorations(target);
							event.getDataTransfer().<XDataTransfer> cast().setDropEffect("move");
						} else {
							event.getDataTransfer().<XDataTransfer> cast().setDropEffect("none");
						}
					} else {
					}
				} else {
					event.getDataTransfer().<XDataTransfer> cast().setDropEffect("none");
				}
			}
		}, DragEnterEvent.getType());
		addDomHandler(new DragHandler() {

			@Override
			public void onDrag(DragEvent event) {
				if (DraggedColumn.instance != null && DraggedColumn.instance.getColumnIndex() != -1 && DraggedColumn.instance.isResize()) {
					event.stopPropagation();
					Element hostElement = Grid.this.getElement();
					int clientX = event.getNativeEvent().getClientX();
					int hostAbsX = hostElement.getAbsoluteLeft();
					int hostScrollX = hostElement.getScrollLeft();
					int docScrollX = hostElement.getOwnerDocument().getScrollLeft();
					int relativeX = clientX - hostAbsX + hostScrollX + docScrollX;
					ghostLine.getStyle().setLeft(relativeX, Style.Unit.PX);
					ghostLine.getStyle().setHeight(hostElement.getClientHeight(), Style.Unit.PX);
					if (ghostLine.getParentElement() != hostElement) {
						hostElement.appendChild(ghostLine);
					}
					int newWidth = event.getNativeEvent().getClientX() - DraggedColumn.instance.getCellElement().getAbsoluteLeft();
					if (newWidth > MINIMUM_COLUMN_WIDTH) {
						event.getDataTransfer().<XDataTransfer> cast().setDropEffect("move");
					} else {
						event.getDataTransfer().<XDataTransfer> cast().setDropEffect("none");
					}
				}
			}
		}, DragEvent.getType());
		addDomHandler(new DragOverHandler() {

			@Override
			public void onDragOver(DragOverEvent event) {
				if (DraggedColumn.instance != null && DraggedColumn.instance.getColumnIndex() != -1) {
					event.preventDefault();
					event.stopPropagation();
					if (DraggedColumn.instance.isMove()) {
						DraggedColumn<T> target = findTargetDraggedColumn(event.getNativeEvent().getEventTarget());
						if (target != null) {
							event.getDataTransfer().<XDataTransfer> cast().setDropEffect("move");
						} else {
							hideColumnDecorations();
							event.getDataTransfer().<XDataTransfer> cast().setDropEffect("none");
						}
					} else {
					}
				}
			}
		}, DragOverEvent.getType());
		addDomHandler(new DragLeaveHandler() {

			@Override
			public void onDragLeave(DragLeaveEvent event) {
				if (DraggedColumn.instance != null && DraggedColumn.instance.getColumnIndex() != -1) {
					event.stopPropagation();
					if (DraggedColumn.instance.isMove()) {
						if (event.getNativeEvent().getEventTarget() == (JavaScriptObject) Grid.this.getElement()) {
							hideColumnDecorations();
						}
					}
				}
			}
		}, DragLeaveEvent.getType());
		addDomHandler(new DragEndHandler() {

			@Override
			public void onDragEnd(DragEndEvent event) {
				event.stopPropagation();
				hideColumnDecorations();
				DraggedColumn.instance = null;
			}
		}, DragEndEvent.getType());
		addDomHandler(new DropHandler() {

			@Override
			public void onDrop(DropEvent event) {
				// some care...
				DraggedColumn<?> source = DraggedColumn.instance;
				DraggedColumn<T> target = targetDraggedColumn;
				hideColumnDecorations();
				DraggedColumn.instance = null;
				if (source != null && source.getColumnIndex() != -1) {
					event.preventDefault();
					event.stopPropagation();
					if (source.isMove()) {
						AbstractCellTable<T> sourceTable = (AbstractCellTable<T>) source.getTable();
						// target table may be any section in our grid
						if (target != null) {
							int sourceIndex = source.getColumnIndex();
							int targetIndex = target.getColumnIndex();
							GridSection<T> targetSection = (GridSection<T>) target.getTable();
							if (targetIndex > targetSection.getColumnCount()) {
								targetIndex = targetSection.getColumnCount();
							}
							boolean isTargetLeft = targetSection == headerLeft || targetSection == frozenLeft || targetSection == scrollableLeft || targetSection == footerLeft;
							targetSection = isTargetLeft ? headerLeft : headerRight;
							boolean isForeignColumn;
							if (isTargetLeft) {
								isForeignColumn = sourceTable != headerLeft && sourceTable != frozenLeft && sourceTable != scrollableLeft && sourceTable != footerLeft;
							} else {
								isForeignColumn = sourceTable != headerRight && sourceTable != frozenRight && sourceTable != scrollableRight && sourceTable != footerRight;
							}
							if (isForeignColumn || sourceIndex != targetIndex) {
								Column<T, ?> column = (Column<T, ?>) source.getColumn();
								Header<?> header = sourceTable.getHeader(sourceIndex);
								if (header instanceof DraggableHeader) {
									((DraggableHeader) header).setTable(isTargetLeft ? headerLeft : headerRight);
								}
								Header<?> footer = sourceTable.getFooter(sourceIndex);
								String width = sourceTable.getColumnWidth(column);
								sourceTable.clearColumnWidth(column);
								sourceTable.removeColumn(sourceIndex);// ColumnsRemover
								                                      // will
								                                      // take
								                                      // care
								                                      // about
								                                      // any
								                                      // column
								                                      // sharing.
								targetSection.insertColumn(targetIndex, column, targetSection == headerLeft || targetSection == headerRight ? header : null, targetSection == footerLeft
								        || targetSection == footerRight ? footer : null);
								for (AbstractCellTable<T> partner : targetSection.getColumnsPartners()) {
									partner.insertColumn(targetIndex, column, partner == headerLeft || partner == headerRight ? header : null, partner == footerLeft || partner == footerRight ? footer
									        : null);
								}
								frozenColumns = headerLeft.getColumnCount();
								targetSection.setColumnWidth(column, width);// ColumnWidthPropagator
								                                            // will
								                                            // take
								                                            // care
								                                            // about
								                                            // any
								                                            // column
								                                            // sharing.
								headerLeft.getWidthPropagator().changed();
							}
						}
					} else {
						int newWidth = Math.max(event.getNativeEvent().getClientX() - source.getCellElement().getAbsoluteLeft(), MINIMUM_COLUMN_WIDTH);
						// Source and target tables atre the same, so we can
						// cast to DraggedColumn<T> with no care
						((DraggedColumn<T>) source).getTable().setColumnWidth(((DraggedColumn<T>) source).getColumn(), newWidth, Style.Unit.PX);
					}
				}
			}
		}, DropEvent.getType());

		columnsChevron.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		columnsChevron.getElement().addClassName(COLUMNS_CHEVRON_STYLE);
		getElement().appendChild(columnsChevron.getElement());
		columnsChevron.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				PopupPanel pp = new PopupPanel();
				pp.setAutoHideEnabled(true);
				pp.setAutoHideOnHistoryEventsEnabled(true);
				pp.setAnimationEnabled(true);
				MenuBar columnsMenu = new MenuBar(true);
				fillColumns(columnsMenu, headerLeft);
				fillColumns(columnsMenu, headerRight);
				pp.setWidget(columnsMenu);
				pp.showRelativeTo(columnsChevron);
			}

			private void fillColumns(MenuBar aTarget, final GridSection<T> aSection) {
				for (int i = 0; i < aSection.getColumnCount(); i++) {
					Header<?> h = aSection.getHeader(i);
					final Column<T, ?> column = aSection.getColumn(i);
					SafeHtml rendered;
					if (h.getValue() instanceof String) {
						String hVal = (String) h.getValue();
						rendered = hVal.startsWith("<html>") ? SafeHtmlUtils.fromTrustedString(hVal.substring(6)) : SafeHtmlUtils.fromString(hVal);
					} else {
						Cell.Context context = new Cell.Context(0, i, h.getKey());
						SafeHtmlBuilder sb = new SafeHtmlBuilder();
						h.render(context, sb);
						rendered = sb.toSafeHtml();
					}
					MenuItemCheckBox miCheck = new MenuItemCheckBox(!aSection.isColumnHidden(column), rendered.asString(), true);
					miCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							if (Boolean.TRUE.equals(event.getValue())) {
								aSection.showColumn(column);
							} else {
								aSection.hideColumn(column);
							}
						}

					});
					aTarget.addItem(miCheck);
				}
			}

		}, ClickEvent.getType());
	}

	public ListDataProvider<T> getDataProvider() {
		return dataProvider;
	}

	protected DraggedColumn<T> findTargetDraggedColumn(JavaScriptObject aEventTarget) {
		if (Element.is(aEventTarget)) {
			GridSection<T> targetSection = null;
			Element targetCell = null;
			Element currentTarget = Element.as(aEventTarget);
			if (COLUMN_PHANTOM_STYLE.equals(currentTarget.getClassName()) || RULER_STYLE.equals(currentTarget.getClassName())) {
				return targetDraggedColumn;
			}
			while ((targetCell == null || targetSection == null) && currentTarget != null && currentTarget != Grid.this.getElement()) {
				if (targetCell == null) {
					if ("td".equalsIgnoreCase(currentTarget.getTagName()) || "th".equalsIgnoreCase(currentTarget.getTagName())) {
						targetCell = currentTarget;
					}
				}
				if (targetSection == null) {
					if (currentTarget == headerLeft.getElement()) {
						targetSection = headerLeft;
					} else if (currentTarget == frozenLeft.getElement()) {
						targetSection = frozenLeft;
					} else if (currentTarget == scrollableLeft.getElement()) {
						targetSection = scrollableLeft;
					} else if (currentTarget == footerLeft.getElement()) {
						targetSection = footerLeft;
					} else if (currentTarget == headerRight.getElement()) {
						targetSection = headerRight;
					} else if (currentTarget == frozenRight.getElement()) {
						targetSection = frozenRight;
					} else if (currentTarget == scrollableRight.getElement()) {
						targetSection = scrollableRight;
					} else if (currentTarget == footerRight.getElement()) {
						targetSection = footerRight;
					}
				}
				currentTarget = currentTarget.getParentElement();
			}
			if (targetSection != null && targetCell != null) {
				Element targetRow = targetCell.getParentElement();
				for (int i = 0; i < targetRow.getChildCount(); i++) {
					if (targetRow.getChild(i) == targetCell) {
						return new DraggedColumn<T>(targetSection.getColumn(i), i, targetSection, targetCell, Element.as(aEventTarget));
					}
				}
			}
			return null;
		} else {
			return null;
		}
	}

	protected Element ghostLine;
	protected Element ghostColumn;
	protected DraggedColumn<T> targetDraggedColumn;

	protected void hideColumnDecorations() {
		ghostLine.removeFromParent();
		ghostColumn.removeFromParent();
		targetDraggedColumn = null;
	}

	protected void showColumnMoveDecorations(DraggedColumn<T> target) {
		targetDraggedColumn = target;
		Element hostElement = getElement();
		Element thtdElement = target.getCellElement();
		int thLeft = thtdElement.getAbsoluteLeft();
		thLeft = thLeft - getAbsoluteLeft() + hostElement.getScrollLeft();
		ghostLine.getStyle().setLeft(thLeft, Style.Unit.PX);
		ghostLine.getStyle().setHeight(hostElement.getClientHeight(), Style.Unit.PX);
		ghostColumn.getStyle().setLeft(thLeft, Style.Unit.PX);
		ghostColumn.getStyle().setWidth(thtdElement.getOffsetWidth(), Style.Unit.PX);
		ghostColumn.getStyle().setHeight(hostElement.getClientHeight(), Style.Unit.PX);
		if (ghostLine.getParentElement() != hostElement) {
			ghostLine.removeFromParent();
			hostElement.appendChild(ghostLine);
		}
		if (ghostColumn.getParentElement() != hostElement) {
			ghostColumn.removeFromParent();
			hostElement.appendChild(ghostColumn);
		}
	}

	public String getDynamicCellClassName() {
		return dynamicCellClassName;
	}

	public int getRowsHeight() {
		return rowsHeight;
	}

	public void setRowsHeight(int aValue) {
		if (rowsHeight != aValue && aValue >= 10) {
			rowsHeight = aValue;
			styleElement.setInnerHTML("." + dynamicCellClassName + "{position: relative; height: " + rowsHeight + "px;" + "text-overflow: ellipsis; overflow: hidden; white-space: nowrap;}");
			onResize();
		}
	}

	private void propagateHeaderLeftWidth() {
		double dw = headerLeft.getElement().getOffsetWidth();
		headerLeftContainer.getElement().getParentElement().getStyle().setWidth(dw, Style.Unit.PX);
		frozenLeftContainer.getElement().getParentElement().getStyle().setWidth(dw, Style.Unit.PX);
		scrollableLeftContainer.getElement().getParentElement().getStyle().setWidth(dw, Style.Unit.PX);
		footerLeftContainer.getElement().getParentElement().getStyle().setWidth(dw, Style.Unit.PX);
	}

	private void propagateHeightButScrollable() {
		int r0Height = Math.max(headerLeft.getOffsetHeight(), headerRight.getOffsetHeight());
		headerLeftContainer.getElement().getParentElement().getStyle().setHeight(r0Height, Style.Unit.PX);
		headerRightContainer.getElement().getParentElement().getStyle().setHeight(r0Height, Style.Unit.PX);
		int r1Height = Math.max(frozenLeft.getOffsetHeight(), frozenRight.getOffsetHeight());
		frozenLeftContainer.getElement().getParentElement().getStyle().setHeight(r1Height, Style.Unit.PX);
		frozenRightContainer.getElement().getParentElement().getStyle().setHeight(r1Height, Style.Unit.PX);
		int r3Height = Math.max(footerLeft.getOffsetHeight(), footerRight.getOffsetHeight());
		footerLeftContainer.getElement().getParentElement().getStyle().setHeight(r3Height, Style.Unit.PX);
		footerRightContainer.getElement().getParentElement().getStyle().setHeight(r3Height, Style.Unit.PX);
		// special care about scrollable
		// row height is free, but cells' height is setted by hand in order to
		// support gecko browsers
		// scrollableLeftContainer.getElement().getParentElement().getStyle().setHeight(100,
		// Style.Unit.PCT);
		// scrollableRightContainer.getElement().getParentElement().getStyle().setHeight(100,
		// Style.Unit.PCT);
		// some code for opera...
		scrollableLeftContainer.getElement().getStyle().clearHeight();
		scrollableRightContainer.getElement().getStyle().clearHeight();
		// it seems that after clearing the height, hive offsetHeight is changed
		// ...
		scrollableLeftContainer.getElement().getStyle().setHeight(hive.getElement().getOffsetHeight() - r0Height - r1Height - r3Height, Style.Unit.PX);
		scrollableRightContainer.getElement().getStyle().setHeight(hive.getElement().getOffsetHeight() - r0Height - r1Height - r3Height, Style.Unit.PX);
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		adopt(columnsChevron);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		orphan(columnsChevron);
	}

	@Override
	public void onResize() {
		if (isAttached()) {
			hive.setSize(getElement().getClientWidth()+"px", getElement().getClientHeight()+"px");
			propagateHeaderLeftWidth();
			propagateHeightButScrollable();
			columnsChevron.setHeight(Math.max(headerLeftContainer.getOffsetHeight(), headerRightContainer.getOffsetHeight()) + "px");
			for (Widget child : new Widget[] { headerLeftContainer, headerRightContainer, frozenLeftContainer, frozenRightContainer, scrollableLeftContainer, scrollableRightContainer }) {
				if (child instanceof RequiresResize) {
					((RequiresResize) child).onResize();
				}
			}
		}
	}

	public int getFrozenColumns() {
		return frozenColumns;
	}

	public int getFrozenRows() {
		return frozenRows;
	}

	public void setFrozenColumns(int aValue) {
		if (aValue >= 0 && frozenColumns != aValue) {
			if (aValue >= 0) {
				frozenColumns = aValue;
				if (getDataColumnCount() > 0 && aValue <= getDataColumnCount()) {
					refreshColumns();
				}
			}
		}
	}

	public void setFrozenRows(int aValue) {
		if (aValue >= 0 && frozenRows != aValue) {
			if (aValue >= 0) {
				frozenRows = aValue;
				if (dataProvider != null && aValue <= dataProvider.getList().size()) {
					setupVisibleRanges();
				}
			}
		}
	}

	/**
	 * TODO: Check if working with sectioned grid.
	 * 
	 * @param sModel
	 */
	public void setSelectionModel(SelectionModel<T> sModel) {
		headerLeft.setSelectionModel(sModel);
		headerRight.setSelectionModel(sModel);
		frozenLeft.setSelectionModel(sModel);
		frozenRight.setSelectionModel(sModel);
		scrollableLeft.setSelectionModel(sModel);
		scrollableRight.setSelectionModel(sModel);
	}

	public SelectionModel<? super T> getSelectionModel() {
		return scrollableRight.getSelectionModel();
	}

	/**
	 * @param aDataProvider
	 */
	public void setDataProvider(ListDataProvider<T> aDataProvider) {
		if (dataProvider != null) {
			dataProvider.removeDataDisplay(headerLeft);
			dataProvider.removeDataDisplay(headerRight);
			dataProvider.removeDataDisplay(frozenLeft);
			dataProvider.removeDataDisplay(frozenRight);
			dataProvider.removeDataDisplay(scrollableLeft);
			dataProvider.removeDataDisplay(scrollableRight);
			dataProvider.removeDataDisplay(footerLeft);
			dataProvider.removeDataDisplay(footerRight);
		}
		dataProvider = aDataProvider;
		if (dataProvider != null) {
			dataProvider.addDataDisplay(headerLeft);
			dataProvider.addDataDisplay(headerRight);
			dataProvider.addDataDisplay(frozenLeft);
			dataProvider.addDataDisplay(frozenRight);
			dataProvider.addDataDisplay(scrollableLeft);
			dataProvider.addDataDisplay(scrollableRight);
			dataProvider.addDataDisplay(footerLeft);
			dataProvider.addDataDisplay(footerRight);
		}
		setupVisibleRanges();
	}

	public void setupVisibleRanges() {
		int generalLength = dataProvider.getList().size();
		int lfrozenRows = generalLength >= frozenRows ? frozenRows : generalLength;
		int scrollableRowCount = generalLength - lfrozenRows;
		//
		headerLeft.setVisibleRange(new Range(0, 0));
		headerRight.setVisibleRange(new Range(0, 0));
		frozenLeft.setVisibleRange(new Range(0, lfrozenRows));
		frozenRight.setVisibleRange(new Range(0, lfrozenRows));
		scrollableLeft.setVisibleRange(new Range(lfrozenRows, scrollableRowCount >= 0 ? scrollableRowCount : 0));
		scrollableRight.setVisibleRange(new Range(lfrozenRows, scrollableRowCount >= 0 ? scrollableRowCount : 0));
		footerLeft.setVisibleRange(new Range(0, 0));
		footerRight.setVisibleRange(new Range(0, 0));
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				onResize();
			}

		});
	}

	public void addColumn(Column<T, ?> aColumn, String aHeaderValue) {
		addColumn(aColumn, aHeaderValue, null);
	}

	public void addColumn(Column<T, ?> aColumn, String aHeaderValue, Header<?> aFooter) {
		if (headerLeft.getColumnCount() < frozenColumns) {
			headerLeft.addColumn(aColumn, new DraggableHeader<T>(aHeaderValue != null ? aHeaderValue : "", headerLeft, aColumn, getElement()));
			frozenLeft.addColumn(aColumn);
			scrollableLeft.addColumn(aColumn);
			footerLeft.addColumn(aColumn, null, aFooter);
		} else {
			headerRight.addColumn(aColumn, new DraggableHeader<T>(aHeaderValue != null ? aHeaderValue : "", headerRight, aColumn, getElement()));
			frozenRight.addColumn(aColumn);
			scrollableRight.addColumn(aColumn);
			footerRight.addColumn(aColumn, null, aFooter);
		}
	}

	public void insertColumn(int aIndex, Column<T, ?> aColumn, String aHeaderValue, Header<?> aFooter) {
		if (aIndex < frozenColumns) {
			headerLeft.insertColumn(aIndex, aColumn, new DraggableHeader<T>(aHeaderValue, headerLeft, aColumn, getElement()));
			frozenLeft.insertColumn(aIndex, aColumn);
			scrollableLeft.insertColumn(aIndex, aColumn);
			footerLeft.insertColumn(aIndex, aColumn, null, aFooter);
			refreshColumns();
		} else {
			headerRight.insertColumn(aIndex, aColumn, new DraggableHeader<T>(aHeaderValue, headerRight, aColumn, getElement()));
			frozenRight.insertColumn(aIndex, aColumn);
			scrollableRight.insertColumn(aIndex, aColumn);
			footerRight.insertColumn(aIndex, aColumn, null, aFooter);
		}
	}

	public void removeColumn(int aIndex) {
		if (aIndex < frozenColumns) {
			headerRight.removeColumn(aIndex);// ColumnsRemover will care
			                                 // about columns sharing
			refreshColumns();
		} else {
			headerRight.removeColumn(aIndex);// ColumnsRemover will care
			                                 // about columns sharing
		}
	}

	public void addColumn(Column<T, ?> aColumn, String aWidth, Header<?> aHeader, Header<?> aFooter, boolean hidden) {
		if (aHeader instanceof DraggableHeader<?>) {
			DraggableHeader<T> h = (DraggableHeader<T>) aHeader;
			h.setColumn(aColumn);
		}
		if (headerLeft.getColumnCount() < frozenColumns) {
			if (aHeader instanceof DraggableHeader<?>) {
				DraggableHeader<T> h = (DraggableHeader<T>) aHeader;
				h.setTable(headerLeft);
			}
			headerLeft.addColumn(aColumn, aHeader);
			frozenLeft.addColumn(aColumn);
			scrollableLeft.addColumn(aColumn);
			footerLeft.addColumn(aColumn, null, aFooter);
			//
			for (GridSection<?> section : new GridSection<?>[] { headerLeft, frozenLeft, scrollableLeft, footerLeft }) {
				GridSection<T> gSection = (GridSection<T>) section;
				gSection.setColumnWidth(aColumn, aWidth);
				if (hidden) {
					gSection.hideColumn(aColumn);
				}
			}
		} else {
			if (aHeader instanceof DraggableHeader<?>) {
				DraggableHeader<T> h = (DraggableHeader<T>) aHeader;
				h.setTable(headerRight);
			}
			headerRight.addColumn(aColumn, aHeader);
			frozenRight.addColumn(aColumn);
			scrollableRight.addColumn(aColumn);
			footerRight.addColumn(aColumn, null, aFooter);
			//
			for (GridSection<?> section : new GridSection<?>[] { headerRight, frozenRight, scrollableRight, footerRight }) {
				GridSection<T> gSection = (GridSection<T>) section;
				gSection.setColumnWidth(aColumn, aWidth);
				if (hidden) {
					gSection.hideColumn(aColumn);
				}
			}
		}
	}

	protected void refreshColumns() {
		List<Column<T, ?>> cols = new ArrayList<>();
		List<Header<?>> headers = new ArrayList<>();
		List<Header<?>> footers = new ArrayList<>();
		List<String> widths = new ArrayList<>();
		List<Boolean> hidden = new ArrayList<>();
		for (int i = headerRight.getColumnCount() - 1; i >= 0; i--) {
			Column<T, ?> col = headerRight.getColumn(i);
			cols.add(0, col);
			widths.add(0, headerRight.getColumnWidth(col, true));
			headers.add(0, headerRight.getHeader(i));
			footers.add(0, footerRight.getFooter(i));
			hidden.add(0, headerRight.isColumnHidden(col));
			headerRight.removeColumn(i);// ColumnsRemover will care about
			                            // columns sharing
		}
		for (int i = headerLeft.getColumnCount() - 1; i >= 0; i--) {
			Column<T, ?> col = headerLeft.getColumn(i);
			cols.add(0, col);
			widths.add(0, headerLeft.getColumnWidth(col, true));
			headers.add(0, headerLeft.getHeader(i));
			footers.add(0, footerLeft.getFooter(i));
			hidden.add(0, headerLeft.isColumnHidden(col));
			headerLeft.removeColumn(i);// ColumnsRemover will care about
			                           // columns sharing
		}
		headerLeft.setWidth("0px", true);
		frozenLeft.setWidth("0px", true);
		scrollableLeft.setWidth("0px", true);
		footerLeft.setWidth("0px", true);
		headerRight.setWidth("0px", true);
		frozenRight.setWidth("0px", true);
		scrollableRight.setWidth("0px", true);
		footerRight.setWidth("0px", true);
		for (int i = 0; i < cols.size(); i++) {
			Column<T, ?> col = cols.get(i);
			Header<?> h = headers.get(i);
			Header<?> f = footers.get(i);
			String w = widths.get(i);
			Boolean b = hidden.get(i);
			addColumn(col, w, h, f, b);
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				onResize();
			}

		});
	}

	public void setColumnWidth(Column<T, ?> aColumn, double aWidth, Style.Unit aUnit) {
		if (headerLeft.getColumnIndex(aColumn) != -1) {
			headerLeft.setColumnWidth(aColumn, aWidth, aUnit);
			frozenLeft.setColumnWidth(aColumn, aWidth, aUnit);
			scrollableLeft.setColumnWidth(aColumn, aWidth, aUnit);
			footerLeft.setColumnWidth(aColumn, aWidth, aUnit);
		} else if (headerRight.getColumnIndex(aColumn) != -1) {
			headerRight.setColumnWidth(aColumn, aWidth, aUnit);
			frozenRight.setColumnWidth(aColumn, aWidth, aUnit);
			scrollableRight.setColumnWidth(aColumn, aWidth, aUnit);
			footerRight.setColumnWidth(aColumn, aWidth, aUnit);
		} else {
			Logger.getLogger(Grid.class.getName()).log(Level.WARNING, "Unknown column is met while setting column width");
		}
	}

	public void addColumnSortHandler(ColumnSortEvent.ListHandler<T> aSortHandler) {
		headerLeft.addColumnSortHandler(aSortHandler);
		frozenLeft.addColumnSortHandler(aSortHandler);
		scrollableLeft.addColumnSortHandler(aSortHandler);
		footerLeft.addColumnSortHandler(aSortHandler);
		headerRight.addColumnSortHandler(aSortHandler);
		frozenRight.addColumnSortHandler(aSortHandler);
		scrollableRight.addColumnSortHandler(aSortHandler);
		footerRight.addColumnSortHandler(aSortHandler);
	}

	public void redrawRow(int index) {
		frozenLeft.redrawRow(index);
		frozenRight.redrawRow(index);
		scrollableLeft.redrawRow(index);
		scrollableRight.redrawRow(index);
	}

	public void redraw() {
		headerLeft.redraw();
		headerRight.redraw();
		frozenLeft.redraw();
		frozenRight.redraw();
		scrollableLeft.redraw();
		scrollableRight.redraw();
		footerLeft.redraw();
		footerRight.redraw();
	}

	public void redrawHeaders() {
		headerLeft.redrawHeaders();
		headerRight.redrawHeaders();
	}

	public void redrawFooters() {
		footerLeft.redrawFooters();
		footerRight.redrawFooters();
	}

	public int getDataColumnCount() {
		return headerLeft.getColumnCount() + headerRight.getColumnCount();
	}

	public Column<T, ?> getDataColumn(int aIndex){
		return aIndex >= 0 && aIndex < headerLeft.getColumnCount() ? headerLeft.getColumn(aIndex) : headerRight.getColumn(aIndex - headerLeft.getColumnCount()); 
	}
	
	public Header<?> getColumnHeader(int aIndex){
		return aIndex >= 0 && aIndex < headerLeft.getColumnCount() ? headerLeft.getHeader(aIndex) : headerRight.getHeader(aIndex - headerLeft.getColumnCount()); 
	}
	
	public Element getViewCell(int row, int col) {
		assert false : "getViewCell is not implemented yet.";
		return null;
	}

	public T getObject(int row) {
		assert false : "getObject is not implemented yet.";
		return null;
	}
}
