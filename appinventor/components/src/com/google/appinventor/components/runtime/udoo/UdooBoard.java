// -*- mode: java; c-basic-offset: 2; -*-
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime.udoo;

import android.app.Activity;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.Form;
import com.google.appinventor.components.runtime.OnDestroyListener;
import com.google.appinventor.components.runtime.OnPauseListener;
import com.google.appinventor.components.runtime.OnResumeListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A component that allows to call functions on the Arduino side of UDOO boards.
 *
 * @author francesco.monte@gmail.com
 */
public abstract class UdooBoard extends AndroidNonvisibleComponent
implements OnResumeListener, OnDestroyListener, OnPauseListener
{
  protected UdooConnectionInterface connection = null;
  
  public synchronized boolean isConnected()
  {
    boolean isc = getTransport().isConnected();
    if (!isc) {
      if (!getTransport().isConnecting()) {
        getTransport().reconnect();
      }
    }
    return isc;
  }

  public UdooBoard(final Form form)
  {
    super(form);
    
    form.registerForOnResume(this);
    form.registerForOnDestroy(this);
    form.registerForOnPause(this);
    
    new Thread(new Runnable() {
      @Override
      public void run() {
        new Timer().schedule(new TimerTask() {
          @Override
          public void run() {
            getTransport().onCreate(form);
          }
        }, 500);
      }
    }).start();
  }
  
  @Override
  public void onResume()
  {
    this.isConnected(); //connects, if disconnected
  }

  @Override
  public void onDestroy()
  {
    getTransport().disconnect();
    getTransport().onDestroy();
  }
  
  @Override
  public void onPause()
  {
    if (!getTransport().isConnecting()) {
      getTransport().disconnect();
    }
  }
  
  public abstract UdooConnectionInterface getTransport();
  
  public Activity getActivity()
  {
      return this.form.$context();
  }
}
