// -*- mode: java; c-basic-offset: 2; -*-
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.common.YaVersion;
import com.google.appinventor.components.runtime.udoo.UdooBackgroundEventFirer;
import com.google.appinventor.components.runtime.udoo.UdooBoard;
import com.google.appinventor.components.runtime.udoo.UdooConnectedInterface;
import com.google.appinventor.components.runtime.udoo.UdooConnectionDetailsInterface;
import com.google.appinventor.components.runtime.udoo.UdooConnectionFactory;
import com.google.appinventor.components.runtime.udoo.UdooConnectionInterface;
import com.google.appinventor.components.runtime.udoo.UdooInterruptibleInterface;

/**
 * A component that allows to call functions on the Arduino side of UDOO Neo.
 *
 * @author francesco.monte@gmail.com
 */
@DesignerComponent(version = YaVersion.UDOO_ARDUINO_NEO_COMPONENT_VERSION,
    description = "A component that interfaces with the Arduino MPU in UDOO Neo.",
    category = ComponentCategory.UDOO,
    nonVisible = true,
    iconName = "images/udoo.png")
@SimpleObject
public class UdooNeo extends UdooBoard
  implements UdooConnectedInterface, UdooConnectionDetailsInterface, UdooInterruptibleInterface
{
  public UdooNeo(Form form) {
    super(form);
  }
  
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
    this.transport = transport;
  }
  
  @Override
  public boolean isLocal() {
    return this.transport.equals("local");
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
  }
 
  @Override
  public String getRemoteAddress() {
    return this.remoteAddress;
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
  }
  
  @Override
  public String getRemotePort() {
    return this.remotePort;
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

  @Override
  public String getRemoteSecret() {
    return this.remoteSecret;
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
    } else {
      throw new Exception("Not connected");
    }
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
    } else {
      throw new Exception("Not connected");
    }
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
  
  @SimpleFunction
  public void attachInterrupt(String pin, String mode) throws Exception
  {
    if (this.isConnected()) {
      getTransport().arduino().setInterruptible(this);
      getTransport().arduino().attachInterrupt(pin, mode);
    }
  }
  
  @SimpleEvent(description = "Fires when the Arduino triggers an interrupt routine.")
  @Override
  public void InterruptFired(int pinNumber, int timestamp)
  {
    UdooBackgroundEventFirer ef = new UdooBackgroundEventFirer();
    ef.setEventName("InterruptFired").setArguments(pinNumber, timestamp).setComponent(this);
    new Thread(ef).start();
  }
  
  @SimpleEvent(description = "Fires when the Arduino is (re)connected.")
  @Override
  public void Connected()
  {
    UdooBackgroundEventFirer ef = new UdooBackgroundEventFirer();
    ef.setEventName("Connected").setComponent(this);
    new Thread(ef).start();
  }
  
  @Override
  public UdooConnectionInterface getTransport()
  {
    if (this.connection == null) {
      this.connection = UdooConnectionFactory.getConnection(this, form);
      this.connection.registerComponent(this, form);
    }
    return this.connection;
  }
}
