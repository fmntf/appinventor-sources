// -*- mode: java; c-basic-offset: 2; -*-
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime;

import android.util.Log;
import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.annotations.UsesPermissions;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.common.YaVersion;

/**
 * A component that interfaces with GPIOs in UDOO boards.
 *
 * @author francesco.monte@gmail.com
 */
@DesignerComponent(version = YaVersion.UDOO_GPIO_COMPONENT_VERSION,
    description = "A component that interfaces with GPIOs in UDOO boards.",
    category = ComponentCategory.UDOO,
    nonVisible = true,
    iconName = "images/udoo.png")
@SimpleObject
@UsesPermissions(permissionNames = "android.permission.WRITE_EXTERNAL_STORAGE, android.permission.ACCESS_SUPERUSER")
public class UdooPinConfig extends AndroidNonvisibleComponent
implements OnResumeListener, OnDestroyListener, UdooConnectedInterface
{
    private String TAG = "UDOOUsbActivity";
    private UdooBroadcastReceiver usbReceiver = new UdooBroadcastReceiver();

    public synchronized boolean isConnected()
    {
        boolean isc = usbReceiver.isConnected();
        if (!isc) {
            Log.d(TAG, "isConnected called, but disconnected!");
            usbReceiver.disconnect();
            usbReceiver.connect();
        }
        return isc;
    }

    public UdooPinConfig(Form form)
    {
        super(form);
        
        Log.d("UDOOLIFECYCLE", "UdooArduino Pin Config");
        
        form.registerForOnResume(this);
        form.registerForOnDestroy(this);
        
        usbReceiver.setComponent(this);
        usbReceiver.onCreate(form);
    }
    
    @Override
    public void onResume()
    {
        Log.d("UDOOLIFECYCLE", "onResume Pin Config");
        
        this.isConnected(); //connects, if disconnected
    }

    @Override
    public void onDestroy()
    {
        Log.d("UDOOLIFECYCLE", "onDestroy Pin Config");
        
        usbReceiver.disconnect();
        usbReceiver.onDestroy();
    }
    
    private int pinNumber;

    /**
     * Returns the GPIO pin number
     *
     * @return integer
     */
    @SimpleProperty(description = "GPIO pin number")
    public int PinNumber() {
        return this.pinNumber;
    }

    /**
     * Sets the GPIO pin number
     *
     * @param pinNumber
     */
    @DesignerProperty(
        editorType = PropertyTypeConstants.PROPERTY_TYPE_INTEGER,
        defaultValue = "1")
    @SimpleProperty
    public void PinNumber(int pinNumber) {
        this.pinNumber = pinNumber;
    }

    
    private String direction;

    /**
     * Returns the active GPIO direction
     *
     * @return String
     */
    @SimpleProperty(description = "GPIO direction")
    public String Direction() {
        return this.direction;
    }

    /**
     * Sets the GPIO direction
     *
     * @param direction
     */
    @DesignerProperty(
        editorType = PropertyTypeConstants.PROPERTY_TYPE_GPIO_DIRECTIONS,
        defaultValue = "in")
    @SimpleProperty
    public void Direction(String direction) {
        if (direction.equals("in") || direction.equals("out")) {
            this.direction = direction;
            if (this.isConnected()) {
                usbReceiver.arduino.pinMode(this.pinNumber, direction);
            }
        } else {
            throw new RuntimeException("Invalid direction `" + direction + "` for GPIO #" + this.pinNumber);
        }
    }
    
    @SimpleEvent(description = "Fires when the Arduino is (re)connected.")
    public void Connected()
    {
        Log.d(TAG, "Connected EVENT");
        EventDispatcher.dispatchEvent(this, "Connected");
    }
}
