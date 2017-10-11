package org.udoo.appinventor.iot.udooiotrestjava.node;

import org.udoo.appinventor.iot.udooiotrestjava.UDOOIoTException;
import org.udoo.appinventor.iot.udooiotrestjava.UDOOIoTRestManager;
import org.udoo.appinventor.iot.udooiotrestjava.UDOOIoTRestNode;

/**
 * Created by harlem88 on 9/18/17.
 */

public class ArduinoNode extends UDOOIoTRestNode {

    public ArduinoNode(String gatewayId, String nodeId, UDOOIoTRestManager UDOOIoTRestManager) {
        super(gatewayId, nodeId, UDOOIoTRestManager);
    }

    public void digitalWrite(Integer pin, Integer value) throws UDOOIoTException {
        write("digital", pin.toString(), value.toString());
    }

    public String digitalRead(Integer pin) throws UDOOIoTException {
        String result = read("digital", pin.toString());
        if (result != null && result.length() > 0) {
            return result;
        }
        return "LOW";
    }

    public void servoWrite(Integer pin, Integer degrees) throws UDOOIoTException{
        write("servo", pin.toString(), degrees.toString());
    }

    public int analogRead(Integer pin) throws UDOOIoTException {
        String result = read("analog", pin.toString());
        if (result != null && result.length() > 0) {
            return Integer.parseInt(result);
        }
        return -1;
    }
}
