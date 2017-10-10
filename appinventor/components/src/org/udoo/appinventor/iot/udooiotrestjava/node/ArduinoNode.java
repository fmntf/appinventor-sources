package org.udoo.appinventor.iot.udooiotrestjava.node;

import org.udoo.appinventor.iot.udooiotrestjava.UDOOIoTRestManager;
import org.udoo.appinventor.iot.udooiotrestjava.UDOOIoTRestNode;

/**
 * Created by harlem88 on 9/18/17.
 */

public class ArduinoNode extends UDOOIoTRestNode {

    public ArduinoNode(String gatewayId, String nodeId, UDOOIoTRestManager UDOOIoTRestManager) {
        super(gatewayId, nodeId, UDOOIoTRestManager);
    }

    public void digitalWrite(Integer pin, Integer value){
        write("digital", pin.toString(), value.toString());
    }

    public String digitalRead(Integer pin){
        return read("digital", pin.toString());
    }

    public void servoWrite(Integer pin, Integer degrees){
        write("servo", pin.toString(), degrees.toString());
    }

    public String analogRead(Integer pin){
        return read("analog", pin.toString());
    }
}
