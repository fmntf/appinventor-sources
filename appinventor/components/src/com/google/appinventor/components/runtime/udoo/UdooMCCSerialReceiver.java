// -*- mode: java; c-basic-offset: 2; -*-
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime.udoo;

import java.io.FileDescriptor;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.Form;
import com.google.appinventor.components.runtime.util.ErrorMessages;
import java.util.ArrayList;
import java.util.List;
import org.udoo.minicom.SerialPort;

/**
 * USB for local App Iventor <-> UDOO Neo connections.
 * 
 * @author Francesco Montefoschi francesco.monte@gmail.com
 */
public class UdooMCCSerialReceiver implements UdooConnectionInterface
{
  private static final String TAG = "MCCSerialReceiver";
  List<UdooConnectedInterface> connectedComponents = new ArrayList<UdooConnectedInterface>();
  Form form;
  private UdooArduinoManager arduino;
  private InputStream inputStream;
  private OutputStream outputStream;
  private SerialPort mSerialPort;
  private boolean connected = false;
  private boolean isConnecting = false;
  
  @Override
  public boolean isConnected()
  {
    return this.connected;
  }
  
  @Override
  public boolean isConnecting() {
    return this.isConnecting;
  }
  
  @Override
  public void reconnect()
  {
    this.disconnect();
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    this.connect();
  }
  
  public synchronized void connect()
  {
    if (this.connected) {
      return;
    }
    
    try {
      mSerialPort = new SerialPort("/dev/ttyMCC", 115200, 0);
    } catch (IOException e) {
      e.printStackTrace();
    }
    outputStream = mSerialPort.getOutputStream();
    inputStream = mSerialPort.getInputStream();
    
    this.arduino = new UdooArduinoManager(outputStream, inputStream, this);
    if (!this.arduino.hi()) {
      this.connected = false;
      form.dispatchErrorOccurredEvent((Component)connectedComponents.get(0), "connect", ErrorMessages.ERROR_UDOO_ADK_NO_CONNECTION);
      return;
    }
    
    this.connected = true;
    for (UdooConnectedInterface c : connectedComponents) {
      c.Connected();
    }
  }
  
  @Override
  public synchronized void disconnect()
  {
    if (this.arduino != null) {
      this.arduino.disconnect();
      this.arduino.stop();
      this.arduino = null;
    }
      
    this.connected = false;
    this.isConnecting = false;
    notifyAll();
    
    mSerialPort.close();
    mSerialPort = null;
  }
  
  @Override
  public void registerComponent(UdooConnectedInterface component, Form form) {
    this.connectedComponents.add(component);
    this.form = form;
  }

  @Override
  public void onCreate(ContextWrapper ctx) {
    if (this.connected) {
      reconnect();
    } else {
      connect();
    }
  }

  @Override
  public void onDestroy() {
  }
  
  @Override
  public UdooArduinoManager arduino() {
    return this.arduino;
  }
}
