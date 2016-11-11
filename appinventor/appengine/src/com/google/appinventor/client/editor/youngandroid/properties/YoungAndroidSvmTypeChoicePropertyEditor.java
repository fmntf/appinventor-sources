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
public class YoungAndroidSvmTypeChoicePropertyEditor extends ChoicePropertyEditor {

  private static final Choice[] types = new Choice[] {
    new Choice("C-SVC", ""+0),
    new Choice("nu-SVC", ""+1),
    new Choice("one-class", ""+2),
    new Choice("epsilon-SVR", ""+3),
    new Choice("nu-SVR", ""+4),
  };

  public YoungAndroidSvmTypeChoicePropertyEditor() {
    super(types);
  }
}
