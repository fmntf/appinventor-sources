// -*- mode: java; c-basic-offset: 2; -*-
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.client.editor.youngandroid.properties;

import com.google.appinventor.client.widgets.properties.ChoicePropertyEditor;

/**
 * Property editor for choosing SVM types
 *
 * @author francesco.monte@gmail.com
 */
public class YoungAndroidSvmKernelTypeChoicePropertyEditor extends ChoicePropertyEditor {

  private static final Choice[] types = new Choice[] {
    new Choice("linear", ""+0),
    new Choice("polynomial", ""+1),
    new Choice("RBF", ""+2),
    new Choice("sigmoid", ""+3),
  };

  public YoungAndroidSvmKernelTypeChoicePropertyEditor() {
    super(types);
  }
}
