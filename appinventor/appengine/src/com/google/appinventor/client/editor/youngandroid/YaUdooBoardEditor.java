// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.client.editor.youngandroid;

import com.google.appinventor.client.boxes.AssetListBox;
import com.google.appinventor.client.boxes.PaletteBox;
import com.google.appinventor.client.boxes.PropertiesBox;
import com.google.appinventor.client.boxes.SourceStructureBox;
import com.google.appinventor.client.editor.ProjectEditor;
import com.google.appinventor.client.editor.simple.SimpleNonVisibleComponentsPanel;
import com.google.appinventor.client.editor.simple.SimpleVisibleComponentsPanel;
import com.google.appinventor.client.editor.simple.components.MockComponent;
import com.google.appinventor.client.editor.simple.components.MockContainer;
import com.google.appinventor.client.editor.simple.components.MockForm;
import com.google.appinventor.client.editor.simple.components.MockUdooBoard;
import com.google.appinventor.client.editor.simple.palette.DropTargetProvider;
import com.google.appinventor.client.editor.simple.palette.SimpleComponentDescriptor;
import static com.google.appinventor.client.editor.youngandroid.YaFormEditor.JSON_PARSER;
import com.google.appinventor.client.editor.youngandroid.palette.YoungAndroidPalettePanel;
import com.google.appinventor.client.output.OdeLog;
import com.google.appinventor.client.widgets.dnd.DropTarget;
import com.google.appinventor.client.widgets.properties.PropertiesPanel;
import com.google.appinventor.shared.properties.json.JSONObject;
import com.google.appinventor.shared.properties.json.JSONValue;
import com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidFormNode;
import com.google.appinventor.shared.youngandroid.YoungAndroidSourceAnalyzer;
import com.google.common.base.Preconditions;
import com.google.gwt.user.client.ui.DockPanel;
import java.util.List;
import java.util.Map;

/**
 * Editor for Young Android Form (.scm) files.
 *
 * @author francesco.monte@gmail.com
 */
public final class YaUdooBoardEditor extends YaFormEditor {

  /**
   * Creates a new YaFormEditor.
   *
   * @param projectEditor  the project editor that contains this file editor
   * @param formNode the YoungAndroidFormNode associated with this YaFormEditor
   */
  YaUdooBoardEditor(ProjectEditor projectEditor, YoungAndroidFormNode formNode) {
    super(projectEditor, formNode);
  }
  
  protected void initUi()
  {
    // Get reference to the source structure explorer
    sourceStructureExplorer =
        SourceStructureBox.getSourceStructureBox().getSourceStructureExplorer();

    // Create UI elements for the designer panels.
    nonVisibleComponentsPanel = new SimpleNonVisibleComponentsPanel();
    visibleComponentsPanel = new SimpleVisibleComponentsPanel(this, nonVisibleComponentsPanel);
    DockPanel componentsPanel = new DockPanel();
    componentsPanel.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
    componentsPanel.add(visibleComponentsPanel, DockPanel.NORTH);
//    componentsPanel.add(nonVisibleComponentsPanel, DockPanel.SOUTH);
    componentsPanel.setSize("100%", "100%");

    // Create palettePanel, which will be used as the content of the PaletteBox.
    palettePanel = new YoungAndroidPalettePanel(this);
//    palettePanel.loadComponents(new DropTargetProvider() {
//      @Override
//      public DropTarget[] getDropTargets() {
//        // TODO(markf): Figure out a good way to memorize the targets or refactor things so that
//        // getDropTargets() doesn't get called for each component.
//        // NOTE: These targets must be specified in depth-first order.
//        List<DropTarget> dropTargets = form.getDropTargetsWithin();
//        dropTargets.add(visibleComponentsPanel);
////        dropTargets.add(nonVisibleComponentsPanel);
//        return dropTargets.toArray(new DropTarget[dropTargets.size()]);
//      }
//    });
//    palettePanel.setSize("100%", "100%");

    // Create designProperties, which will be used as the content of the PropertiesBox.
    designProperties = new PropertiesPanel();
    designProperties.setSize("100%", "100%");

    initWidget(componentsPanel);
    setSize("100%", "100%");
  }

  /*
   * Parses the JSON properties and creates the component structure. This method is called
   * recursively for nested components. For the initial invocation parent shall be null.
   */
  protected MockComponent createMockComponent(JSONObject propertiesObject, MockContainer parent) {
    Map<String, JSONValue> properties = propertiesObject.getProperties();

    // Component name and type
    String componentType = properties.get("$Type").asString().getString();

    // Instantiate a mock component for the visual designer
    MockComponent mockComponent;
    if (componentType.equals(MockForm.TYPE)) {
      Preconditions.checkArgument(parent == null);
      // Instantiate new root component
      mockComponent = new MockUdooBoard(this);
    } else {
      mockComponent = SimpleComponentDescriptor.createMockComponent(componentType, this);

      // Add the component to its parent component (and if it is non-visible, add it to the
      // nonVisibleComponent panel).
      parent.addComponent(mockComponent);
//      if (!mockComponent.isVisibleComponent()) {
//        nonVisibleComponentsPanel.addComponent(mockComponent);
//      }
    }

    // Set the name of the component (on instantiation components are assigned a generated name)
    String componentName = properties.get("$Name").asString().getString();
    mockComponent.changeProperty("Name", componentName);

    // Set component properties
    for (String name : properties.keySet()) {
      if (name.charAt(0) != '$') { // Ignore special properties (name, type and nested components)
        mockComponent.changeProperty(name, properties.get(name).asString().getString());
      }
    }

    //This is for old project which doesn't have the AppName property
    if (!properties.keySet().contains("AppName")) {
      String fileId = getFileId();
      String projectName = fileId.split("/")[3];
      mockComponent.changeProperty("AppName", projectName);
    }

    // Add component type to the blocks editor
    YaProjectEditor yaProjectEditor = (YaProjectEditor) projectEditor;
    YaBlocksEditor blockEditor = yaProjectEditor.getBlocksFileEditor(formNode.getFormName());
    blockEditor.addComponent(mockComponent.getType(), mockComponent.getName(),
        mockComponent.getUuid());

    // Add nested components
    if (properties.containsKey("$Components")) {
      for (JSONValue nestedComponent : properties.get("$Components").asArray().getElements()) {
        createMockComponent(nestedComponent.asObject(), (MockContainer) mockComponent);
      }
    }

    return mockComponent;
  }
  
  protected void onFileLoaded(String content) {
    JSONObject propertiesObject = YoungAndroidSourceAnalyzer.parseSourceFile(
        content, JSON_PARSER);
    form = createMockForm(propertiesObject.getProperties().get("Properties").asObject());

//     Initialize the nonVisibleComponentsPanel and visibleComponentsPanel.
//    nonVisibleComponentsPanel.setForm(form);
    visibleComponentsPanel.setForm(form);
    form.select();

    // Set loadCompleted to true.
    // From now on, all change events will be taken seriously.
    loadComplete = true;
  }
  
  /*
   * Updates the the whole designer: form, palette, source structure explorer,
   * assets list, and properties panel.
   */
  protected void loadDesigner() {
    form.refresh();
    MockComponent selectedComponent = form.getSelectedComponent();

    // Set the palette box's content.
//    PaletteBox paletteBox = PaletteBox.getPaletteBox();
//    paletteBox.setContent(palettePanel);

    // Update the source structure explorer with the tree of this form's components.
    sourceStructureExplorer.updateTree(form.buildComponentsTree(),
        selectedComponent.getSourceStructureExplorerItem());
    SourceStructureBox.getSourceStructureBox().setVisible(true);

    // Show the assets box.
//    AssetListBox assetListBox = AssetListBox.getAssetListBox();
//    assetListBox.setVisible(true);

    // Set the properties box's content.
    PropertiesBox propertiesBox = PropertiesBox.getPropertiesBox();
    propertiesBox.setContent(designProperties);
    updatePropertiesPanel(selectedComponent);
    propertiesBox.setVisible(true);

    // Listen to changes on the form.
    form.addFormChangeListener(this);
    // Also have the blocks editor listen to changes. Do this here instead
    // of in the blocks editor so that we don't risk it missing any updates.
    OdeLog.log("Adding blocks editor as a listener for " + form.getName());
    form.addFormChangeListener(((YaProjectEditor) projectEditor)
        .getBlocksFileEditor(form.getName()));
  }
  
  /*
   * Clears the palette, source structure explorer, and properties panel.
   */
  protected void unloadDesigner() {
    // The form can still potentially change if the blocks editor is displayed
    // so don't remove the formChangeListener.

    // Clear the palette box.
//    PaletteBox paletteBox = PaletteBox.getPaletteBox();
//    paletteBox.clear();

    // Clear and hide the source structure explorer.
    sourceStructureExplorer.clearTree();
    SourceStructureBox.getSourceStructureBox().setVisible(false);

    // Hide the assets box.
//    AssetListBox assetListBox = AssetListBox.getAssetListBox();
//    assetListBox.setVisible(false);

    // Clear and hide the properties box.
    PropertiesBox propertiesBox = PropertiesBox.getPropertiesBox();
    propertiesBox.clear();
    propertiesBox.setVisible(false);
  }
}
