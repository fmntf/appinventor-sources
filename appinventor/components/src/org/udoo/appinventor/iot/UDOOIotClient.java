// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2016 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package org.udoo.appinventor.iot;

import android.os.Handler;
import android.util.Log;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import org.udoo.appinventor.iot.udooiotrestjava.OnResult;
import org.udoo.appinventor.iot.udooiotrestjava.UDOOIoTException;
import org.udoo.appinventor.iot.udooiotrestjava.UDOOIoTRestManager;
import org.udoo.appinventor.iot.udooiotrestjava.node.ArduinoNode;
import org.udoo.appinventor.iot.udooiotrestjava.node.NeoNode;


@DesignerComponent(description = "UDOO IoT Client",
                   version = 1,
                   category = ComponentCategory.EXTENSION,
                   nonVisible = true,
                   iconName = "https://www.udoo.org/appinventor/udoo.png")
@SimpleObject(external = true)
public class UDOOIotClient extends AndroidNonvisibleComponent implements Component
{
  private final String TAG = "UDOOIotClient";
  private String username;
  private String password;
  private String boardName;
  private Boolean connected;
  private final UDOOIoTRestManager udooIoTRestManager;

  public UDOOIotClient(ComponentContainer container) {
    super(container.$form());
    udooIoTRestManager = new UDOOIoTRestManager();
    Log.d(TAG, "UDOOIotClient constructor");
    boardName = "default";
    connected = false;
  }
  
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
  defaultValue = "")
  @SimpleProperty()
  public void Username(String username) {
    this.username = username;
  }
  
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
  defaultValue = "")
  @SimpleProperty()
  public void Password(String password) {
    this.password = password;
  }
  
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
  defaultValue = "default")
  @SimpleProperty()
  public void Board(String boardName) {
    this.boardName = boardName;
  }

  @SimpleFunction
  public void Login() {
    if (this.username == null || this.username.trim().equals("") ||
        this.password == null || this.password.trim().equals("")) {
      LoginFailed("Please configure your IoT account.");
    } else {
      udooIoTRestManager.login(username, password, new OnResult<Boolean>() {
        public void onSuccess(Boolean success) {
           connected = success;
        }

        public void onError(Throwable error) {
          connected = false;
          LoginFailed(error.getMessage());
        }
      });
    }
  }
  
  @SimpleEvent(description = "Login did not succeed.")
  public void LoginFailed(final String reason) {
    Log.d(TAG, "LoginFailed");
    Log.d(TAG, reason);
    final UDOOIotClient dff = this;
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        EventDispatcher.dispatchEvent(dff, "LoginFailed", reason);
      }
    }, 1000);
    
    
  }

  @SimpleFunction
  public void DigitalWrite(int pin, String value) {
    if (!this.connected) {
      signalError("DigitalWrite", 66602, "You are not logged in!");
      return;
    }

    ArduinoNode arduino = udooIoTRestManager.getArduino(boardName);
    if (arduino == null) {
      signalError("DigitalWrite", 66603, "Board not found!");
    } else {
      arduino.digitalWrite(pin, stringToLevel(value));
    }
  }
  
  @SimpleFunction
  public String DigitalRead(int pin) {
    if (!this.connected) {
      signalError("DigitalRead", 66602, "You are not logged in!");
      return "";
    }
    
    ArduinoNode arduino = udooIoTRestManager.getArduino(boardName);
    if (arduino == null) {
      signalError("DigitalRead", 66603, "Board not found!");
      return "";
    } else {
      String result = arduino.digitalRead(pin);
      if (result.equals("1")) {
        return "HIGH";
      } else {
        return "LOW";
      }
    }
  }
  
  @SimpleFunction
  public int AnalogRead(int pin) {
    if (!this.connected) {
      signalError("AnalogRead", 66602, "You are not logged in!");
      return -1;
    }
    
    ArduinoNode arduino = udooIoTRestManager.getArduino(boardName);
    if (arduino == null) {
      signalError("AnalogRead", 66603, "Board not found!");
      return -1;
    } else {
      String result = arduino.analogRead(pin);
      Log.d(TAG, result);
      return Integer.parseInt(result);
    }
  }
  
  @SimpleFunction
  public float ReadTemperatureBrick() throws UDOOIoTException {
    if (!this.connected) {
      signalError("DigitalRead", 66602, "You are not logged in!");
      return -1;
    }
    
    NeoNode neo = udooIoTRestManager.getNeo(boardName);
    if (neo == null) {
      signalError("DigitalRead", 66603, "Board not found!");
      return -1;
    } else {
      return neo.temperatureRead();
    }
  }
  
  @SimpleFunction
  public float ReadBarometerBrick() throws UDOOIoTException {
    if (!this.connected) {
      signalError("DigitalRead", 66602, "You are not logged in!");
      return -1;
    }
    
    NeoNode neo = udooIoTRestManager.getNeo(boardName);
    if (neo == null) {
      signalError("DigitalRead", 66603, "Board not found!");
      return -1;
    } else {
      return neo.barometerRead();
    }
  }
  
  @SimpleFunction
  public float ReadLightBrick() throws UDOOIoTException {
    if (!this.connected) {
      signalError("DigitalRead", 66602, "You are not logged in!");
      return -1;
    }
    
    NeoNode neo = udooIoTRestManager.getNeo(boardName);
    if (neo == null) {
      signalError("DigitalRead", 66603, "Board not found!");
      return -1;
    } else {
      return neo.lightRead();
    }
  }
  
  @SimpleFunction
  public float ReadHumidityBrick() throws UDOOIoTException {
    if (!this.connected) {
      signalError("DigitalRead", 66602, "You are not logged in!");
      return -1;
    }
    
    NeoNode neo = udooIoTRestManager.getNeo(boardName);
    if (neo == null) {
      signalError("DigitalRead", 66603, "Board not found!");
      return -1;
    } else {
      return neo.humidityRead();
    }
  }
  
  private int stringToLevel(String value) {
    value = value.toLowerCase().trim();
    if (value.charAt(0) == 'h') {
      return 1;
    }
    return 0;
  }
  
  /**
   * Signal that an error has occurred. Since we are an extension, we don't have access to the normal
   * error handling used by built-in App Inventor components. BluetoothLE errors are shown in a dialog
   * rather than an alert for added clarity.
   */
  private void signalError(final String functionName, final int errorNumber, final String errorMessage) {
    Log.e(TAG, errorMessage);
  
    form.runOnUiThread(new Runnable() {
      public void run() {
        form.ErrorOccurredDialog(UDOOIotClient.this,
          functionName,
          errorNumber,
          errorMessage,
          "UDOO IoT",
          "Dismiss");
      }
    });
  }
}
