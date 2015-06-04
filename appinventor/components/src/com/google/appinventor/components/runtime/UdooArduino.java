// -*- mode: java; c-basic-offset: 2; -*-
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime;

import android.util.Log;
import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.common.YaVersion;


/**
 * A component that allows to call functions on the Arduino side of UDOO boards.
 *
 * @author francesco.monte@gmail.com
 */
@DesignerComponent(version = YaVersion.UDOO_ARDUINO_COMPONENT_VERSION,
    description = "A component that interfaces with the Arduino CPU in UDOO boards.",
    category = ComponentCategory.UDOO,
    nonVisible = true,
    iconName = "images/udoo.png")
@SimpleObject
public class UdooArduino extends AndroidNonvisibleComponent
implements OnResumeListener, OnDestroyListener, UdooConnectedInterface
{
  private String TAG = "UDOOArduinoCmp";
  
  private String transport = "local";

  /**
   * Sets the transport property
   *
   * @param transport
   */
  @DesignerProperty(
      editorType = PropertyTypeConstants.PROPERTY_TYPE_UDOO_TRANSPORTS,
      defaultValue = "local")
  @SimpleProperty(
      description = "Connect to a local (via ADK) or remote (via TCP) board.",
      userVisible = false)
  public void Transport(String transport) {
    if (transport.equals("local") || transport.equals("remote")) {
      this.transport = transport;
      getTransport().registerComponent(this);
      RemoteAddress(this.remoteAddress);
      RemotePort(this.remotePort);
    }
  }
  
  private String remoteAddress;

  /**
   * Sets the remote IP address
   *
   * @param remoteAddress
   */
  @DesignerProperty()
  @SimpleProperty(
      description = "If transport=remote, the IP address of the remote board.",
      userVisible = false)
  public void RemoteAddress(String remoteAddress) {
    this.remoteAddress = remoteAddress;
    UdooConnectionInterface transport = getTransport();
    if (transport instanceof UdooTcpRedirector) {
      ((UdooTcpRedirector)transport).setAddress(remoteAddress);
    }
  }
  
  private String remotePort;

  /**
   * Sets the remote TCP port number
   *
   * @param remotePort
   */
  @DesignerProperty()
  @SimpleProperty(
      description = "If transport=remote, the TCP port of the remote board.",
      userVisible = false)
  public void RemotePort(String remotePort) {
    this.remotePort = remotePort;
    UdooConnectionInterface transport = getTransport();
    if (transport instanceof UdooTcpRedirector) {
      ((UdooTcpRedirector)transport).setPort(remotePort);
    }
  }
  
  private String remoteSecret;

  /**
   * Sets the remote secret string for authentication
   *
   * @param remoteSecret
   */
  @DesignerProperty()
  @SimpleProperty(
      description = "If transport=remote, the secret key to connect to the remote board.",
      userVisible = false)
  public void RemoteSecret(String remoteSecret) {
    this.remoteSecret = remoteSecret;
  }
  
  public synchronized boolean isConnected()
  {
    boolean isc = getTransport().isConnected();
    if (!isc) {
      Log.d(TAG, "isConnected called, but disconnected!");
      getTransport().disconnect();
      getTransport().connect();
    }
    return isc;
  }

  public UdooArduino(Form form)
  {
    super(form);
    
    Log.d("UDOOLIFECYCLE", "UdooArduino");
    
    form.registerForOnResume(this);
    form.registerForOnDestroy(this);
    
    getTransport().onCreate(form);
  }
  
  @Override
  public void onResume()
  {
    Log.d("UDOOLIFECYCLE", "onResume");
    
    this.isConnected(); //connects, if disconnected
  }

  @Override
  public void onDestroy()
  {
    Log.d("UDOOLIFECYCLE", "onDestroy");
    
    getTransport().disconnect();
    getTransport().onDestroy();
  }

  @SimpleFunction
  public void pinMode(String pin, String mode)
  {
    if (this.isConnected()) {
      getTransport().arduino().pinMode(pin, mode);
    }
  }
  
  @SimpleFunction
  public void digitalWrite(String pin, String value)
  {
    if (this.isConnected()) {
      getTransport().arduino().digitalWrite(pin, value);
    }
  }
  
  @SimpleFunction
  public int digitalRead(String pin) throws Exception
  {
    if (this.isConnected()) {
      return getTransport().arduino().digitalRead(pin);
    }
    
    throw new Exception("Not connected");
  }
  
  @SimpleFunction
  public void analogWrite(String pin, int value)
  {
    if (this.isConnected()) {
      getTransport().arduino().analogWrite(pin, value);
    }
  }
  
  @SimpleFunction
  public int analogRead(String pin) throws Exception
  {
    if (this.isConnected()) {
      return getTransport().arduino().analogRead(pin);
    }
    
    throw new Exception("Not connected");
  }
  
  @SimpleFunction
  public void delay(int ms) throws Exception
  {
    if (this.isConnected()) {
      getTransport().arduino().delay(ms);
    }
  }
  
  @SimpleFunction
  public int map(int value, int fromLow, int fromHigh, int toLow, int toHigh) throws Exception
  {
    if (this.isConnected()) {
      return getTransport().arduino().map(value, fromLow, fromHigh, toLow, toHigh);
    }
    
    throw new Exception("Not connected");
  }
  
  @SimpleEvent(description = "Fires when the Arduino is (re)connected.")
  public void Connected()
  {
    Log.d(TAG, "Connected EVENT");
    EventDispatcher.dispatchEvent(this, "Connected");
  }
  
  private UdooConnectionInterface getTransport()
  {
    if (this.transport.equals("local")) {
      return UdooAdkBroadcastReceiver.getInstance();
    } else {
      return UdooTcpRedirector.getInstance();
    }
  }
}
