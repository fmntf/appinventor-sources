package org.udoo.appinventor.iot.udooiotrestjava.node;

import org.udoo.appinventor.iot.udooiotrestjava.UDOOIoTException;
import org.udoo.appinventor.iot.udooiotrestjava.UDOOIoTRestManager;
import org.udoo.appinventor.iot.udooiotrestjava.UDOOIoTRestNode;
import org.udoo.appinventor.iot.udooiotrestjava.model.SensorModel;
import org.udoo.appinventor.iot.udooiotrestjava.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by harlem88 on 9/20/17.
 */

public class NeoNode extends UDOOIoTRestNode {
    private final int i2cIdx = 1;
    private Map<String, SensorModel> sensorModelList;

    public NeoNode(String gatewayId, String nodeId, UDOOIoTRestManager UDOOIoTRestManager) {
        super(gatewayId, nodeId, UDOOIoTRestManager);
        sensorModelList = new HashMap<String, SensorModel>();
    }

    public void setSensors(HashMap<String, SensorModel> sensorModelList){
        this.sensorModelList = sensorModelList;
    }

    private String i2cRead(String addr) throws UDOOIoTException{
        return read("i2c", addr);
    }

    public int lightRead() throws UDOOIoTException{
        String sValue = i2cRead(i2cIdx + "-0029");
        if (sValue != null && sValue.length() > 0) {
            return Integer.parseInt(sValue);
        } else {
            throw new UDOOIoTException("Cannot read light brick");
        }
    }

    public float humidityRead() throws UDOOIoTException{
        float value;
        String sValue = i2cRead(i2cIdx + "-0040");
        String[] items = Util.StringToValueArr(sValue);
        if (items.length > 0) {
            value = Float.parseFloat(items[0]);
        }else {
            throw new UDOOIoTException("Cannot read humidity brick");
        }
        return value;
    }

    public float temperatureRead() throws UDOOIoTException {
        float value = -1;
        if (sensorModelList.containsKey(i2cIdx + "-0048")) {
            value = Float.parseFloat(i2cRead(i2cIdx + "-0048"));
        } else if (sensorModelList.containsKey(i2cIdx + "-0040")) {
            String sValue = i2cRead(i2cIdx + "-0040");
            String[] items = Util.StringToValueArr(sValue);
            if (items.length > 1) {
                value = Float.parseFloat(items[1]);
            }
        }else {
            throw new UDOOIoTException("Cannot read temperature brick");
        }
        return value;
    }

    public float barometerRead() throws UDOOIoTException {
        String sValue = i2cRead(i2cIdx+"-0060");
        String[] items = Util.StringToValueArr(sValue);
        if (items.length > 0) {
            return Float.parseFloat(items[0]);
        }else {
            throw new UDOOIoTException("Cannot read humidity brick");
        }
    }
}
