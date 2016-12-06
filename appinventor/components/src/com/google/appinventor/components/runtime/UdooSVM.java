// -*- mode: java; c-basic-offset: 2; -*-
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime;

import android.os.Environment;
import android.util.Log;
import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.annotations.UsesLibraries;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.common.YaVersion;
import com.google.appinventor.components.runtime.util.ErrorMessages;
import com.google.appinventor.components.runtime.util.YailList;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import libsvm.*;

/**
 * A component that provides SVM capabilities
 *
 * @author francesco.monte@gmail.com
 */
@DesignerComponent(version = YaVersion.MACHINELEARNING_SVM_COMPONENT_VERSION,
    description = "A component that allows SVM, a machine learning technique.",
    category = ComponentCategory.MACHINELEARNING,
    nonVisible = true,
    iconName = "images/udooSvm.png")
@SimpleObject
@UsesLibraries(libraries = "libsvm.jar")
public class UdooSVM extends AndroidNonvisibleComponent
{
  private final String TAG = "UdooSVM";

  private ArrayList<double[]> trainData;
  private svm_model model = null;
  private double[][] extremes;
  private int type;
  private int kernel;
  
  public UdooSVM(Form form) {
    super(form);
    
    this.trainData = new ArrayList<double[]>();
  }
  
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_SVM_TYPES,
    defaultValue = svm_parameter.C_SVC+"")
  @SimpleProperty(userVisible = false)
  public void Type(int type) {
    this.type = type;
  }
  
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_SVM_KERNEL_TYPES,
    defaultValue = svm_parameter.LINEAR+"")
  @SimpleProperty(userVisible = false)
  public void Kernel(int kernel) {
    this.kernel = kernel;
  }

  @SimpleFunction
  public void Teach(int category, YailList features)
  {
    this.trainData.add(listToVector(features, category));
  }
  
  @SimpleFunction
  public void CreateModel()
  {
    if (this.trainData.isEmpty()) {
      form.dispatchErrorOccurredEvent(this, "CreateModel", ErrorMessages.ERROR_SVM_NO_VECTORS);
      return;
    }

    svm_problem problem = getProblem();
    svm_parameter parameter = getSVM();

    this.model = svm.svm_train(problem, parameter);
  }
  
  @SimpleFunction
  public void Clear()
  {
    this.trainData = new ArrayList<double[]>();
    this.model = null;
  }
  
  @SimpleFunction
  public void SaveModel(String name)
  {
    if (this.model == null) {
      form.dispatchErrorOccurredEvent(this, "SaveModel", ErrorMessages.ERROR_SVM_NO_MODEL);
      return;
    }

    File imagesFolder = new File(Environment.getExternalStorageDirectory(), "/UDOO");
    imagesFolder.mkdirs();
    try {
      svm.svm_save_model(imagesFolder.getAbsolutePath() + "/" + name, this.model);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @SimpleFunction
  public void LoadModel(String name)
  {
    File imagesFolder = new File(Environment.getExternalStorageDirectory(), "/UDOO");
    try {
      this.model = svm.svm_load_model(imagesFolder.getAbsolutePath() + "/" + name);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @SimpleFunction
  public int Predict(YailList features)
  {
    if (this.model == null) {
      form.dispatchErrorOccurredEvent(this, "SaveModel", ErrorMessages.ERROR_SVM_NO_MODEL);
      return -1;
    }
    
    double[] vector = listToVector(features);
    
    scaleData(vector);

    svm_node[] nodes = new svm_node[vector.length];
    for (int i = 0; i < vector.length; i++) {
      svm_node node = new svm_node();
      node.index = i + 1;
      node.value = vector[i];
      nodes[i] = node;
    }
    
    int prediction = (int) svm.svm_predict(this.model, nodes);
    Log.d(TAG, "Prediction: " + prediction);
    
    return (int) svm.svm_predict(this.model, nodes);
  }
  
  private svm_problem getProblem()
  {
    findExtremes();
    Log.d("SVM", "Features before scaling");
    dumpFeatures();
    scaleTrainingData();
    Log.d("SVM", "Features AFTER scaling");
    dumpFeatures();

    svm_problem prob = new svm_problem();
    int dataCount = this.trainData.size();
    prob.y = new double[dataCount];
    prob.l = dataCount;
    prob.x = new svm_node[dataCount][];

    for (int i = 0; i < dataCount; i++) {
      double[] features = this.trainData.get(i);
      prob.x[i] = new svm_node[features.length - 1];
      for (int j = 1; j < features.length; j++) {
        svm_node node = new svm_node();
        node.index = j;
        node.value = features[j];
        prob.x[i][j - 1] = node;
      }
      prob.y[i] = features[0];
    }

    return prob;
  }

  private svm_parameter getSVM()
  {
    svm_parameter param = new svm_parameter();
    param.probability = 1;
    param.gamma = 1/this.trainData.get(0).length;
    param.nu = 0.5;
    param.C = 1;
    param.svm_type = this.type;
    param.kernel_type = this.kernel;
    param.cache_size = 20000;
    param.eps = 0.001;

    return param;
  }
    
  private void findExtremes()
  {
    double[] firstVector = this.trainData.get(0);
      
    this.extremes = new double[firstVector.length][2];

    for (int i = 1; i < firstVector.length; i++) {
      double min = firstVector[i];
      double max = firstVector[i];
      for (int j = 1; j < this.trainData.size(); j++) {
        if (this.trainData.get(j)[i] < min) min = this.trainData.get(j)[i];
        if (this.trainData.get(j)[i] > max) max = this.trainData.get(j)[i];
      }
      this.extremes[i][0] = min;
      this.extremes[i][1] = max;
    }
  }

  private void scaleTrainingData()
  {
    for (int j = 0; j < this.trainData.size(); j++) {
      double[] row = this.trainData.get(j);
      for (int i = 1; i < row.length; i++) {
        row[i] = map(row[i], this.extremes[i][0], this.extremes[i][1]);
      }
    }
  }

  private void scaleData(double[] vector)
  {
    for (int i = 0; i < vector.length; i++) {
      vector[i] = map(vector[i], this.extremes[i + 1][0], this.extremes[i + 1][1]);
    }
  }
  
  private void dumpFeatures()
  {
    double[] firstVector = this.trainData.get(0);
    for (int i = 1; i < firstVector.length; i++) {
      StringBuilder vect = new StringBuilder();
      for (int j = 0; j < this.trainData.size(); j++) {
        vect.append(this.trainData.get(j)[i] + "  ");
      }
      Log.d("SVM", "i=" + i + ": " + vect.toString());
    }
  }
  
  private double[] listToVector(YailList features)
  {
    Object[] array = features.toArray();
    double[] vector = new double[array.length];
    
    for (int i = 0; i < array.length; i++) {
      vector[i] = Double.parseDouble(array[i].toString());
    }
    
    return vector;
  }
  
  private double[] listToVector(YailList features, int category)
  {
    Object[] array = features.toArray();
    double[] vector = new double[array.length+1];
    vector[0] = category;
    
    for (int i = 0; i < array.length; i++) {
      vector[i+1] = Double.parseDouble(array[i].toString());
    }
    
    return vector;
  }
  
  private double map(double x, double in_min, double in_max)
  {
    return (x - in_min) * 2 / (in_max - in_min) - 1;
  }
}
