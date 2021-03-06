// -*- mode: java; c-basic-offset: 2; -*-
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime.udoo;

import android.os.Build;
import android.util.Log;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class UdooArduinoReader
{
  private Thread thread;
  private boolean running;
  
  public UdooArduinoReader(InputStream inputStream) {
    this.registerReader(inputStream);
    this.running = true;
  }
  
  /**
   * Given an InputStream (created by USB/ADK or network socket) creates a thread,
   * continuously reading from such stream.
   * 
   * ADK: in order to avoid a bug in Android (USB file descriptor not correctly
   * closed), we stop the thread before calling is.read() again. This is achieved
   * by the fake "disconnect" method on the Arduino.
   * @see https://code.google.com/p/android/issues/detail?id=20545
   * @param inputStream 
   */
  private void registerReader(final InputStream inputStream)
  {
    this.thread = new Thread(new Runnable() {
      @Override
      public void run() {
        while (running) {
          String readResponse = this.read();
          if (readResponse != null) {
            parseJson(readResponse);
          }
        }
      }
      
      private void parseJson(String response)
      {
        JSONObject json = null;
        Integer id = null;
        try {
          json = new JSONObject(response);
          if (!json.has("id")) {
            Log.d("UdooArduinoReader", "Ignored response: " + response);
            return;
          }
          id = (Integer) json.get("id");
          if (json.has("disconnected")) {
            stop();
          }

          UdooRequestsRegistry.onRead(json, id);
        } catch (JSONException ex) {
          Logger.getLogger(UdooRequestsRegistry.class.getName()).log(Level.SEVERE, null, ex);
          
          if (response.charAt(0) == '}') {
            parseJson(response.substring(1));
          }
        }
      }
      
      private String read()
      {
        byte[] buffer = new byte[256];
        String message = null;
        int mByteRead = -1;

        try {
          if (Build.MODEL.equals("UDOONEO-MX6SX")) {
            message = "";
            do {
              if (inputStream.available() > 0) {
                mByteRead = inputStream.read(buffer, 0, buffer.length);
                message += new String(Arrays.copyOfRange(buffer, 0, mByteRead));
              }
            } while (!message.contains("\n"));
            message = message.trim();
          } else {
            byte[] response;
            if (inputStream instanceof FileInputStream) {
              mByteRead = inputStream.read(buffer, 0, buffer.length);
              if (mByteRead != -1) {
                response = Arrays.copyOfRange(buffer, 0, mByteRead);
                message = new String(response).trim();
              }
            } else {
              message = "";
              do {
                mByteRead = inputStream.read(buffer, 0, buffer.length);
                message += new String(Arrays.copyOfRange(buffer, 0, mByteRead));
              } while (!message.contains("\n"));
              message = message.trim();
            }
          }
        } catch (IOException e) {
          Log.e("ARDUINO IO Exception", e.getMessage());
          Logger.getLogger(UdooArduinoReader.class.getName()).log(Level.SEVERE, null, e);
          stop();
          message = null;
        }

        if (message!=null) Log.d("UdooArduinoReader", message);
        return message;
      }
    });
    
    thread.start();
  }

  void stop() {
    this.running = false;
  }
}
