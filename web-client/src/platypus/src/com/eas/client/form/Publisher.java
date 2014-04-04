package com.eas.client.form;

import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedComponent;
import com.eas.client.form.published.containers.AbsolutePane;
import com.eas.client.form.published.containers.AnchorsPane;
import com.eas.client.form.published.containers.BorderPane;
import com.eas.client.form.published.containers.ButtonGroup;
import com.eas.client.form.published.containers.CardPane;
import com.eas.client.form.published.containers.FlowPane;
import com.eas.client.form.published.containers.GridPane;
import com.eas.client.form.published.containers.HBoxPane;
import com.eas.client.form.published.containers.ScrollPane;
import com.eas.client.form.published.containers.SplitPane;
import com.eas.client.form.published.containers.TabbedPane;
import com.eas.client.form.published.containers.ToolBar;
import com.eas.client.form.published.containers.VBoxPane;
import com.eas.client.form.published.menu.PlatypusMenu;
import com.eas.client.form.published.menu.PlatypusMenuBar;
import com.eas.client.form.published.menu.PlatypusMenuItemCheckBox;
import com.eas.client.form.published.menu.PlatypusMenuItemImageText;
import com.eas.client.form.published.menu.PlatypusMenuItemRadioButton;
import com.eas.client.form.published.menu.PlatypusMenuItemSeparator;
import com.eas.client.form.published.widgets.DesktopPane;
import com.eas.client.form.published.widgets.PlatypusButton;
import com.eas.client.form.published.widgets.PlatypusCheckBox;
import com.eas.client.form.published.widgets.PlatypusFormattedTextField;
import com.eas.client.form.published.widgets.PlatypusHtmlEditor;
import com.eas.client.form.published.widgets.PlatypusLabel;
import com.eas.client.form.published.widgets.PlatypusPasswordField;
import com.eas.client.form.published.widgets.PlatypusProgressBar;
import com.eas.client.form.published.widgets.PlatypusRadioButton;
import com.eas.client.form.published.widgets.PlatypusSlider;
import com.eas.client.form.published.widgets.PlatypusSplitButton;
import com.eas.client.form.published.widgets.PlatypusTextArea;
import com.eas.client.form.published.widgets.PlatypusTextField;
import com.eas.client.form.published.widgets.PlatypusToggleButton;
import com.eas.client.form.published.widgets.model.ModelCheck;
import com.eas.client.form.published.widgets.model.ModelCombo;
import com.eas.client.form.published.widgets.model.ModelDate;
import com.eas.client.form.published.widgets.model.ModelFormattedField;
import com.eas.client.form.published.widgets.model.ModelGrid;
import com.eas.client.form.published.widgets.model.ModelSpin;
import com.eas.client.form.published.widgets.model.ModelTextArea;
import com.google.gwt.core.client.JavaScriptObject;

public class Publisher {

	public native static PublishedComponent publish(PlatypusRadioButton aComponent)/*-{
		return new $wnd.RadioButton(null, null, null, aComponent);
	}-*/;
	
	public native static PublishedComponent publish(PlatypusCheckBox aComponent)/*-{
		return new $wnd.CheckBox(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusSlider aComponent)/*-{
		return new $wnd.Slider(null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusToggleButton aComponent)/*-{
		return new $wnd.ToggleButton(null, null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusTextField aComponent)/*-{
		return new $wnd.TextField(null, aComponent);
	}-*/;
	
	public native static PublishedComponent publish(PlatypusFormattedTextField aComponent)/*-{
		return new $wnd.FormattedField(null, aComponent);
	}-*/;	

	public native static PublishedComponent publish(PlatypusTextArea aComponent)/*-{
		return new $wnd.TextArea(null, aComponent);
	}-*/;
	
	public native static PublishedComponent publish(PlatypusHtmlEditor aComponent)/*-{
		return new $wnd.HtmlArea(null, aComponent);
	}-*/;	

	public native static PublishedComponent publish(PlatypusProgressBar aComponent)/*-{
		return new $wnd.ProgressBar(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusPasswordField aComponent)/*-{
		return new $wnd.PasswordField(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusButton aComponent)/*-{
		return new $wnd.Button(null, null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusSplitButton aComponent)/*-{
		return new $wnd.DropDownButton(null, null, null, null, aComponent);
	}-*/;
	
	public native static PublishedComponent publish(PlatypusLabel aComponent)/*-{
		return new $wnd.Label(null, null, null, aComponent);

	}-*/;

	public native static PublishedComponent publish(PlatypusMenuItemSeparator aComponent)/*-{
		return new $wnd.MenuSeparator(aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMenuBar aComponent)/*-{
		return new $wnd.MenuBar(aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMenu aComponent)/*-{
		return new $wnd.Menu(null, aComponent);
	}-*/;

	public native static PublishedComponent publishPopup(PlatypusMenu aComponent)/*-{
		return new $wnd.PopupMenu(aComponent);
	}-*/;
	
	public native static PublishedComponent publish(PlatypusMenuItemImageText aComponent)/*-{
		return new $wnd.MenuItem(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(PlatypusMenuItemCheckBox aComponent)/*-{
		return new $wnd.CheckMenuItem(null, null, null, aComponent);
	}-*/;

	public native static JavaScriptObject publish(PlatypusMenuItemRadioButton aComponent)/*-{
		return new $wnd.RadioMenuItem(null, null, null, aComponent);
	}-*/;
	
	public native static PublishedCell publishCell(Object aData, String aDisplay)/*-{
		var published = {
					data : $wnd.boxAsJs(aData)
		};
		var _display = aDisplay;
		var _style = new $wnd.Style();
		Object.defineProperty(published, "display", {
			get: function(){
				return _display;
			},
			set: function(aValue){
				_display = aValue;
				if(published.displayCallback != null)
					published.displayCallback.@java.lang.Runnable::run()();
			}
		});
		Object.defineProperty(published, "style", {
			get: function(){
				return _style;
			},
			set: function(aValue){
				_style = aValue;
				if(published.displayCallback != null)
					published.displayCallback.@java.lang.Runnable::run()();
			}
		});
		return published;
	}-*/;
	
	public native static PublishedComponent publish(ModelGrid aComponent)/*-{
		return new $wnd.ModelGrid(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelCheck aComponent)/*-{
		return new $wnd.ModelCheckBox(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelFormattedField aComponent)/*-{
		return new $wnd.ModelFormattedField(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelTextArea aComponent)/*-{
		return new $wnd.ModelTextArea(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelDate aComponent)/*-{
		return new $wnd.ModelDate(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelSpin aComponent)/*-{
		return new $wnd.ModelSpin(aComponent);
	}-*/;

	public native static PublishedComponent publish(ModelCombo aComponent)/*-{
		return new $wnd.ModelCombo(aComponent);
	}-*/;

	public native static PublishedComponent publish(BorderPane aComponent)/*-{
		return new $wnd.BorderPane(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(ScrollPane aComponent)/*-{
		return new $wnd.ScrollPane(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(SplitPane aComponent)/*-{
		return new $wnd.SplitPane(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(VBoxPane aComponent)/*-{
		return new $wnd.BoxPane($wnd.Orientation.VERTICAL, aComponent);
	}-*/;

	public native static PublishedComponent publish(HBoxPane aComponent)/*-{
		return new $wnd.BoxPane($wnd.Orientation.HORIZONTAL, aComponent);
	}-*/;

	public native static PublishedComponent publish(CardPane aComponent)/*-{
		return new $wnd.CardPane(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(AbsolutePane aComponent)/*-{
		return new $wnd.AbsolutePane(aComponent);
	}-*/;

	public native static PublishedComponent publish(AnchorsPane aComponent)/*-{
		return new $wnd.AnchorsPane(aComponent);
	}-*/;
	
	public native static PublishedComponent publish(DesktopPane aComponent)/*-{
		return new $wnd.DesktopPane(aComponent);
	}-*/;

	public native static PublishedComponent publish(TabbedPane aComponent)/*-{
		return new $wnd.TabbedPane(aComponent);
	}-*/;

	public native static PublishedComponent publish(ToolBar aComponent)/*-{
		return new $wnd.ToolBar(null, aComponent);
	}-*/;

	public native static PublishedComponent publish(FlowPane aComponent)/*-{
		return new $wnd.FlowPane(null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(GridPane aComponent)/*-{
		return new $wnd.GridPane(null, null, null, null, aComponent);
	}-*/;

	protected static JavaScriptObject checkPublishedComponent(Object aCandidate) {
		if (aCandidate instanceof HasPublished) {
			return ((HasPublished)aCandidate).getPublished();
		}else
			return null;
	}

	public native static JavaScriptObject publish(ButtonGroup aButtonGroup)/*-{
		return new $wnd.ButtonGroup(aButtonGroup);
	}-*/;

}
