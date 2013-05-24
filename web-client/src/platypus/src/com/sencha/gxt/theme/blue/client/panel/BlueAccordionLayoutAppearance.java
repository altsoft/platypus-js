/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.blue.client.panel;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.theme.base.client.panel.AccordionLayoutBaseAppearance;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance;

public class BlueAccordionLayoutAppearance extends AccordionLayoutBaseAppearance {
  
  public interface BlueAccordionLayoutResources extends ContentPanelResources {

    @Source({"com/sencha/gxt/theme/base/client/panel/ContentPanel.css", "BlueContentPanel.css"})
    @Override
    BlueAccordionLayoutStyle style();

  }

  public interface BlueAccordionLayoutStyle extends ContentPanelStyle {

  }
  
  public BlueAccordionLayoutAppearance() {
    super(GWT.<BlueAccordionLayoutResources> create(BlueAccordionLayoutResources.class));
  }

  public BlueAccordionLayoutAppearance(BlueAccordionLayoutResources resources) {
    super(resources);
  }
  
  @Override
  public HeaderDefaultAppearance getHeaderAppearance() {
    return new BlueAccordionHeaderAppearance();
  }
}
