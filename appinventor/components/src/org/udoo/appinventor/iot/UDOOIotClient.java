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


@DesignerComponent(description = "This component allows you to connect to the <a href=\"http://ai2.udoo.org/\">UDOO IoT service</a>.<br>" +
    "Allowed methods are:<br>" + 
    " - DigitalWrite(13, \"HIGH\") <br>" + 
    " - DigitalRead(13) (returns \"LOW\"/\"HIGH\") <br>" + 
    " - AnalogRead(0) (returns 0-1023) <br>" + 
    "<br>It is not necessary to configure pin directions (pinMode).",
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
      ConnectionError("Please configure your IoT account.");
    } else {
      udooIoTRestManager.login(username, password, new OnResult<Boolean>() {
        public void onSuccess(Boolean success) {
           connected = success;
        }

        public void onError(Throwable error) {
          connected = false;
          ConnectionError(error.getMessage());
        }
      });
    }
  }
  
  @SimpleEvent(description = "Login did not succeed.")
  public void ConnectionError(final String reason) {
    Log.d(TAG, "ConnectionError");
    final UDOOIotClient client = this;
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        EventDispatcher.dispatchEvent(client, "ConnectionError", reason);
      }
    }, 100);
  }
  
  @SimpleFunction
  public void DigitalWrite(int pin, String value) {
    if (!this.connected) {
      ConnectionError("You are not logged in!");
      return;
    }

    ArduinoNode arduino = udooIoTRestManager.getArduino(boardName);
    if (arduino == null) {
      ConnectionError("Board not found!");
    } else {
      try {
        arduino.digitalWrite(pin, stringToLevel(value));
      } catch (UDOOIoTException e) {
        ConnectionError(e.getMessage());
      }
    }
  }
  
  @SimpleFunction
  public String DigitalRead(int pin) {
    if (!this.connected) {
      ConnectionError("You are not logged in!");
      return "";    
    }
    
    ArduinoNode arduino = udooIoTRestManager.getArduino(boardName);
    if (arduino == null) {
      ConnectionError("Board not found!");
      return "";
    } else {
      try {
        String result = arduino.digitalRead(pin);
        if (result.equals("1")) {
          return "HIGH";
        } else {
          return "LOW";
        }
      } catch (UDOOIoTException e) {
        ConnectionError(e.getMessage());
        return "";
      }
    }
  }
  
  @SimpleFunction
  public int AnalogRead(int pin) {
    if (!this.connected) {
      ConnectionError("You are not logged in!");
      return -1;
    }
    
    ArduinoNode arduino = udooIoTRestManager.getArduino(boardName);
    if (arduino == null) {
      ConnectionError("Board not found!");
      return -1;
    } else {
      try {
        return arduino.analogRead(pin);
      } catch (UDOOIoTException e) {
        ConnectionError(e.getMessage());
        return -1;
      }
    }
  }
  
  @SimpleFunction
  public float ReadTemperatureBrick() throws UDOOIoTException {
    if (!this.connected) {
      ConnectionError("You are not logged in!");
      return -1;
    }
    
    NeoNode neo = udooIoTRestManager.getNeo(boardName);
    if (neo == null) {
      ConnectionError("Board not found!");
      return -1;
    } else {
      try {
        return neo.temperatureRead();
      } catch (UDOOIoTException e) {
        ConnectionError(e.getMessage());
        return -1;
      }
    }
  }
  
  @SimpleFunction
  public float ReadBarometerBrick() {
    if (!this.connected) {
      ConnectionError("You are not logged in!");
      return -1;
    }
    
    NeoNode neo = udooIoTRestManager.getNeo(boardName);
    if (neo == null) {
      ConnectionError("Board not found!");
      return -1;
    } else {
      try {
        return neo.barometerRead();
      } catch (UDOOIoTException e) {
        ConnectionError(e.getMessage());
        return -1;
      }
    }
  }
  
  @SimpleFunction
  public float ReadLightBrick() {
    if (!this.connected) {
      ConnectionError("You are not logged in!");
      return -1;
    }
    
    NeoNode neo = udooIoTRestManager.getNeo(boardName);
    if (neo == null) {
      ConnectionError("Board not found!");
      return -1;
    } else {
      try {
        return neo.lightRead();
      } catch (UDOOIoTException e) {
        ConnectionError(e.getMessage());
        return -1;
      }
    }
  }
  
  @SimpleFunction
  public float ReadHumidityBrick() {
    if (!this.connected) {
      ConnectionError("You are not logged in!");
      return -1;
    }
    
    NeoNode neo = udooIoTRestManager.getNeo(boardName);
    if (neo == null) {
      ConnectionError("Board not found!");
      return -1;
    } else {
      try {
        return neo.humidityRead();
      } catch (UDOOIoTException e) {
        ConnectionError(e.getMessage());
        return -1;
      }
    }
  }
  
  private int stringToLevel(String value) {
    value = value.toLowerCase().trim();
    if (value.charAt(0) == 'h') {
      return 1;
    }
    return 0;
  }
}
