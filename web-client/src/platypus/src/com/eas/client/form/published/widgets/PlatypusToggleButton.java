package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.ImageToggleButton;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.events.ActionEvent;
import com.eas.client.form.events.ActionHandler;
import com.eas.client.form.events.HasActionHandlers;
import com.eas.client.form.events.HasHideHandlers;
import com.eas.client.form.events.HasShowHandlers;
import com.eas.client.form.events.HideEvent;
import com.eas.client.form.events.HideHandler;
import com.eas.client.form.events.ShowEvent;
import com.eas.client.form.events.ShowHandler;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasEventsExecutor;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPlatypusButtonGroup;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.containers.ButtonGroup;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;

public class PlatypusToggleButton extends ImageToggleButton implements HasActionHandlers, HasJsFacade, HasPlatypusButtonGroup, HasComponentPopupMenu, HasEventsExecutor, HasShowHandlers, HasHideHandlers, HasResizeHandlers {

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected String name;	
	protected JavaScriptObject published;
	
	protected ButtonGroup group;
	
	public PlatypusToggleButton() {
		super("", false);
	}

	public PlatypusToggleButton(String aTitle, boolean asHtml) {
		super(aTitle, asHtml);
	}

	public PlatypusToggleButton(String aTitle, boolean asHtml, ImageResource aImage) {
		super(aTitle, asHtml, aImage);
	}

	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}

	@Override
	public void onResize() {
		super.onResize();
		if(isAttached()){
			ResizeEvent.fire(this, getElement().getOffsetWidth(), getElement().getOffsetHeight());
		}
	}

	@Override
	public HandlerRegistration addHideHandler(HideHandler handler) {
		return addHandler(handler, HideEvent.getType());
	}

	@Override
	public HandlerRegistration addShowHandler(ShowHandler handler) {
		return addHandler(handler, ShowEvent.getType());
	}

	@Override
	public void setVisible(boolean visible) {
		boolean oldValue = isVisible();
		super.setVisible(visible);
		if (oldValue != visible) {
			if (visible) {
				ShowEvent.fire(this, this);
			} else {
				HideEvent.fire(this, this);
			}
		}
	}

	protected int actionHandlers;
	protected HandlerRegistration clickReg;

	@Override
	public HandlerRegistration addActionHandler(ActionHandler handler) {
		final HandlerRegistration superReg = super.addHandler(handler, ActionEvent.getType());
		if (actionHandlers == 0) {
			clickReg = addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					ActionEvent.fire(PlatypusToggleButton.this, PlatypusToggleButton.this);
				}

			});
		}
		actionHandlers++;
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				superReg.removeHandler();
				actionHandlers--;
				if (actionHandlers == 0) {
					assert clickReg != null : "Erroneous use of addActionHandler/removeHandler detected in PlatypusToggleButton";
					clickReg.removeHandler();
					clickReg = null;
				}
			}
		};
	}
	
	
	@Override
	public EventsExecutor getEventsExecutor() {
		return eventsExecutor;
	}

	@Override
	public void setEventsExecutor(EventsExecutor aExecutor) {
		eventsExecutor = aExecutor;
	}

	@Override
    public PlatypusPopupMenu getPlatypusPopupMenu() {
		return menu; 
    }

	protected HandlerRegistration menuTriggerReg;

	@Override
	public void setPlatypusPopupMenu(PlatypusPopupMenu aMenu) {
		if (menu != aMenu) {
			if (menuTriggerReg != null)
				menuTriggerReg.removeHandler();
			menu = aMenu;
			if (menu != null) {
				menuTriggerReg = super.addDomHandler(new ContextMenuHandler() {
					
					@Override
					public void onContextMenu(ContextMenuEvent event) {
						event.preventDefault();
						event.stopPropagation();
						menu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
						menu.show();
					}
				}, ContextMenuEvent.getType());
			}
		}
	}

	@Override
	public String getJsName() {
		return name;
	}

	@Override
	public void setJsName(String aValue) {
		name = aValue;
	}

	@Override
	public ButtonGroup getButtonGroup() {
		return group;
	}

	@Override
	public void setButtonGroup(ButtonGroup aGroup) {
		group = aGroup;
	}

	@Override
	public void mutateButtonGroup(ButtonGroup aGroup) {
		if (group != aGroup) {
			if (group != null)
				group.remove((HasPublished)this);
			group = aGroup;
			if (group != null)
				group.add((HasPublished)this);
		}
	}

	public boolean getPlainValue() {
		Boolean v = getValue();
		return v != null ? v : false;
	}

	public void setPlainValue(boolean aValue) {
		setValue(aValue);
	}
	
	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
			if (published != null) {
				publish(this, aValue);
			}
		}
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		published.opaque = true;

		Object.defineProperty(published, "text", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
			}
		});
		Object.defineProperty(published, "icon", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::getImageResource()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::setImageResource(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
			}
		});
		Object.defineProperty(published, "iconTextGap", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::getIconTextGap()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::setIconTextGap(I)(aValue);
			}
		});
		Object.defineProperty(published, "horizontalTextPosition", {
			get : function() {
				var position = aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::getHorizontalTextPosition()();
				switch(position) { 
					case @com.bearsoft.gwt.ui.HasImageParagraph::LEFT :	return $wnd.P.HorizontalPosition.LEFT; 
					case @com.bearsoft.gwt.ui.HasImageParagraph::RIGHT :	return $wnd.P.HorizontalPosition.RIGHT; 
					case @com.bearsoft.gwt.ui.HasImageParagraph::CENTER :	return $wnd.P.HorizontalPosition.CENTER;
					default : return null; 
				}	
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.P.HorizontalPosition.LEFT:
						aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::setHorizontalTextPosition(I)(@com.bearsoft.gwt.ui.HasImageParagraph::LEFT);
						break;
					case $wnd.P.HorizontalPosition.RIGHT:
						aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::setHorizontalTextPosition(I)(@com.bearsoft.gwt.ui.HasImageParagraph::RIGHT);
						break;
					case $wnd.P.HorizontalPosition.CENTER:
						aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::setHorizontalTextPosition(I)(@com.bearsoft.gwt.ui.HasImageParagraph::CENTER);
						break;
				}
			}
		});
		Object.defineProperty(published, "verticalTextPosition", {
			get : function() {
				var position = aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::getVerticalTextPosition()();
				switch(position) { 
					case @com.bearsoft.gwt.ui.HasImageParagraph::TOP :	return $wnd.P.VerticalPosition.TOP; 
					case @com.bearsoft.gwt.ui.HasImageParagraph::BOTTOM :	return $wnd.P.VerticalPosition.BOTTOM; 
					case @com.bearsoft.gwt.ui.HasImageParagraph::CENTER :	return $wnd.P.VerticalPosition.CENTER;
					default : return null;
				} 
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.P.VerticalPosition.TOP:
						aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::setVerticalTextPosition(I)(@com.bearsoft.gwt.ui.HasImageParagraph::TOP);
						break;
					case $wnd.P.VerticalPosition.BOTTOM:
						aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::setVerticalTextPosition(I)(@com.bearsoft.gwt.ui.HasImageParagraph::BOTTOM);
						break;
					case $wnd.P.VerticalPosition.CENTER:
						aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::setVerticalTextPosition(I)(@com.bearsoft.gwt.ui.HasImageParagraph::CENTER);
						break;
				}
			}
		});

		Object.defineProperty(published, "horizontalAlignment", {
			get : function() {
				var position = aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::getHorizontalAlignment()();
				switch(position) { 
					case @com.bearsoft.gwt.ui.HasImageParagraph::LEFT :	return $wnd.P.HorizontalPosition.LEFT; 
					case @com.bearsoft.gwt.ui.HasImageParagraph::RIGHT :	return $wnd.P.HorizontalPosition.RIGHT; 
					case @com.bearsoft.gwt.ui.HasImageParagraph::CENTER :	return $wnd.P.HorizontalPosition.CENTER;
					default : return null; 
				}	
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.P.HorizontalPosition.LEFT:
						aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::setHorizontalAlignment(I)(@com.bearsoft.gwt.ui.HasImageParagraph::LEFT);
						break;
					case $wnd.P.HorizontalPosition.RIGHT:
						aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::setHorizontalAlignment(I)(@com.bearsoft.gwt.ui.HasImageParagraph::RIGHT);
						break;
					case $wnd.P.HorizontalPosition.CENTER:
						aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::setHorizontalAlignment(I)(@com.bearsoft.gwt.ui.HasImageParagraph::CENTER);
						break;
				}
			}
		});
		Object.defineProperty(published, "verticalAlignment", {
			get : function() {
				var position = aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::getVerticalAlignment()();
				switch(position) { 
					case @com.bearsoft.gwt.ui.HasImageParagraph::TOP :	return $wnd.P.VerticalPosition.TOP; 
					case @com.bearsoft.gwt.ui.HasImageParagraph::BOTTOM :	return $wnd.P.VerticalPosition.BOTTOM; 
					case @com.bearsoft.gwt.ui.HasImageParagraph::CENTER :	return $wnd.P.VerticalPosition.CENTER;
					default : return null;
				} 
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.P.VerticalPosition.TOP:
						aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::setVerticalAlignment(I)(@com.bearsoft.gwt.ui.HasImageParagraph::TOP);
						break;
					case $wnd.P.VerticalPosition.BOTTOM:
						aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::setVerticalAlignment(I)(@com.bearsoft.gwt.ui.HasImageParagraph::BOTTOM);
						break;
					case $wnd.P.VerticalPosition.CENTER:
						aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::setVerticalAlignment(I)(@com.bearsoft.gwt.ui.HasImageParagraph::CENTER);
						break;
				}
			}
		});
		Object.defineProperty(published, "selected", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::getPlainValue()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusToggleButton::setPlainValue(Z)(aValue!=null?aValue:false);
			}
		});
		Object.defineProperty(published, "buttonGroup", {
			get : function() {
				var buttonGroup = aWidget.@com.eas.client.form.published.HasPlatypusButtonGroup::getButtonGroup()();
				return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(buttonGroup);					
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.HasPlatypusButtonGroup::mutateButtonGroup(Lcom/eas/client/form/published/containers/ButtonGroup;)(aValue != null ? aValue.unwrap() : null);
			}
		});
	}-*/;
}
