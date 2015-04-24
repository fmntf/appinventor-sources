// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.client.editor.simple.components;

import static com.google.appinventor.client.Ode.MESSAGES;
import com.google.appinventor.client.editor.simple.SimpleEditor;
import static com.google.appinventor.client.editor.simple.components.MockForm.PROPERTY_NAME_SCROLLABLE;
import static com.google.appinventor.client.editor.simple.components.MockVisibleComponent.PROPERTY_NAME_WIDTH;
import com.google.appinventor.client.editor.simple.components.utils.PropertiesUtil;
import com.google.appinventor.client.output.OdeLog;
import com.google.appinventor.client.properties.BadPropertyEditorException;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock Form component.
 *
 */
public final class MockUdooBoard extends MockForm {

  public MockUdooBoard(SimpleEditor editor)
  {
      super(editor);
  }
    
  @Override
  protected void initUi()
  {
    // Note(hal): There better not be any calls to MockFormHelper before the
    // next instruction.  Note that the Helper methods are synchronized to avoid possible
    // future problems if we ever have threads creating forms in parallel.
    myLayout = MockFormHelper.getLayout();

    formWidget = new AbsolutePanel();
    formWidget.setStylePrimaryName("ode-UdooBoardForm");

      Image udooQuad = new Image(images.udooquad());
    formWidget.add(udooQuad);


    // Put a ScrollPanel around the rootPanel.
    scrollPanel = new ScrollPanel(rootPanel);
    formWidget.add(scrollPanel);

    OdeLog.log("Dimensione: " + PORTRAIT_WIDTH);
    OdeLog.log(this.getClass().getName());
    screenWidth = PORTRAIT_WIDTH;
    screenHeight = PORTRAIT_HEIGHT;
    usableScreenHeight = screenHeight;

    // This is just the initial size of the form. It will be resized in refresh();
    rootPanel.setPixelSize(screenWidth, usableScreenHeight);
    resizePanels();

    initComponent(formWidget);
    
    // Set up the initial state of the vertical alignment property editor and its
    // dropdowns
    try {
      myVAlignmentPropertyEditor = PropertiesUtil.getVAlignmentEditor(properties);
    } catch (BadPropertyEditorException e) {
      OdeLog.log(MESSAGES.badAlignmentPropertyEditorForArrangement());
      return;
    }
    enableAndDisableDropdowns();
    initialized = true;
    // Now that the default for Scrollable is false, we need to force setting the property when creating the MockForm
    setScrollableProperty(getPropertyValue(PROPERTY_NAME_SCROLLABLE));
  }
  
  @Override
  protected void resizePanels() {
    // Set the scrollPanel's width to account for the width of the vertical scrollbar.
    int vertScrollbarWidth = getVerticalScrollbarWidth();
    
    OdeLog.log("__________ vert: " + vertScrollbarWidth);
    OdeLog.log("__________ a: " + screenWidth + vertScrollbarWidth);
    
    scrollPanel.setPixelSize(screenWidth + vertScrollbarWidth, usableScreenHeight);
    formWidget.setPixelSize(screenWidth + vertScrollbarWidth, screenHeight);
  }
  
  @Override
  protected void setScreenOrientationProperty(String text) {
    if (hasProperty(PROPERTY_NAME_WIDTH) && hasProperty(PROPERTY_NAME_HEIGHT) &&
        hasProperty(PROPERTY_NAME_SCROLLABLE)) {
      if (text.equalsIgnoreCase("landscape")) {
        screenWidth = LANDSCAPE_WIDTH;
        screenHeight = LANDSCAPE_HEIGHT;
      } else {
        screenWidth = PORTRAIT_WIDTH;
        screenHeight = PORTRAIT_HEIGHT;
      }
      usableScreenHeight = screenHeight;
      resizePanels();

      changeProperty(PROPERTY_NAME_WIDTH, "" + screenWidth);
      boolean scrollable = Boolean.parseBoolean(getPropertyValue(PROPERTY_NAME_SCROLLABLE));
      if (!scrollable) {
        changeProperty(PROPERTY_NAME_HEIGHT, "" + usableScreenHeight);
      }
    }
  }
  
  @Override
  public void refresh() {
    Map<MockComponent, LayoutInfo> layoutInfoMap = new HashMap<MockComponent, LayoutInfo>();

    collectLayoutInfos(layoutInfoMap, this);

    LayoutInfo formLayoutInfo = layoutInfoMap.get(this);
    layout.layoutChildren(formLayoutInfo);
    rootPanel.setPixelSize(PORTRAIT_WIDTH,
        Math.max(PORTRAIT_HEIGHT, usableScreenHeight));

    for (LayoutInfo layoutInfo : layoutInfoMap.values()) {
      layoutInfo.cleanUp();
    }
    layoutInfoMap.clear();
  }

  @Override
  public void onPropertyChange(String propertyName, String newValue) {
    super.onPropertyChange(propertyName, newValue);

    // Apply changed properties to the mock component
    if (propertyName.equals(PROPERTY_NAME_BACKGROUNDCOLOR)) {
      setBackgroundColorProperty(newValue);
    } else if (propertyName.equals(PROPERTY_NAME_BACKGROUNDIMAGE)) {
      setBackgroundImageProperty(newValue);
    } else if (propertyName.equals(PROPERTY_NAME_SCREEN_ORIENTATION)) {
      setScreenOrientationProperty(newValue);
    } else if (propertyName.equals(PROPERTY_NAME_SCROLLABLE)) {
      setScrollableProperty(newValue);
      adjustAlignmentDropdowns();
//    } else if (propertyName.equals(PROPERTY_NAME_TITLE)) {
//      titleBar.changeTitle(newValue);
    } else if (propertyName.equals(PROPERTY_NAME_ICON)) {
      setIconProperty(newValue);
    } else if (propertyName.equals(PROPERTY_NAME_VCODE)) {
      setVCodeProperty(newValue);
    } else if (propertyName.equals(PROPERTY_NAME_VNAME)) {
      setVNameProperty(newValue);
    } else if (propertyName.equals(PROPERTY_NAME_ANAME)) {
      setANameProperty(newValue);
    } else if (propertyName.equals(PROPERTY_NAME_HORIZONTAL_ALIGNMENT)) {
      myLayout.setHAlignmentFlags(newValue);
      refreshForm();
    } else if (propertyName.equals(PROPERTY_NAME_VERTICAL_ALIGNMENT)) {
    myLayout.setVAlignmentFlags(newValue);
    refreshForm();
    }
  }
  
  protected int PORTRAIT_WIDTH = 600;
  protected int PORTRAIT_HEIGHT = 490;
  protected int LANDSCAPE_WIDTH = 490;
  protected int LANDSCAPE_HEIGHT = 600;

  /*
   * Widget for the mock form title bar.
   */
  protected class TitleBar extends Composite {
    private static final int HEIGHT = 6;
    


    // UI elements
    private Label title;
    private AbsolutePanel bar;

    /*
     * Creates a new title bar.
     */
    TitleBar() {
      title = new Label();
      title.setStylePrimaryName("ode-SimpleMockFormTitle");
      title.setHorizontalAlignment(Label.ALIGN_LEFT);

      bar = new AbsolutePanel();
      bar.add(title, 12, 4);

      initWidget(bar);

      setStylePrimaryName("ode-SimpleMockFormTitleBar");
      setSize("100%", HEIGHT + "px");
    }

    /*
     * Changes the title in the title bar.
     */
    void changeTitle(String newTitle) {
      title.setText(newTitle);
    }
  }
  
}
