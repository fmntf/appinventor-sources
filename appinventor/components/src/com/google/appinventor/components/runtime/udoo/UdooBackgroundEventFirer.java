// -*- mode: java; c-basic-offset: 2; -*-
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime.udoo;

import android.util.Log;
import com.google.appinventor.components.runtime.EventDispatcher;

public class UdooBackgroundEventFirer implements Runnable
{
  private final String TAG = "UdooBackgroundEventFirer";
  
  int pinNumber;
  int timeStamp;
  
  public UdooBackgroundEventFirer setArguments(int pin, int ts) {
    pinNumber = pin;
    timeStamp = ts;
    return this;
  }
  
  String eventName;
  
  public UdooBackgroundEventFirer setEventName(String event) {
    eventName = event;
    return this;
  }
  
  UdooBoard component;
  
  public UdooBackgroundEventFirer setComponent(UdooBoard c) {
    component = c;
    return this;
  }
  
  @Override
  public void run() {
    component.getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "Firing event " + eventName);
        if (eventName.equals("InterruptFired")) {
          EventDispatcher.dispatchEvent(component, eventName, pinNumber, timeStamp);
        } else {
          EventDispatcher.dispatchEvent(component, eventName);
        }
      }
    });
  }
}

