package org.udoo.appinventor.iot.udooiotrestjava;

import org.udoo.appinventor.iot.udooiotrestjava.model.IoTApiResponseModel;

/**
 * Created by harlem88 on 9/15/17.
 */

public abstract class UDOOIoTRestNode {
    private UDOOIoTRestManager mUDOOIoTRestManager;
    protected String nodeId, gatewayId;

    protected UDOOIoTRestNode(String gatewayId, String nodeId, UDOOIoTRestManager UDOOIoTRestManager){
        mUDOOIoTRestManager = UDOOIoTRestManager;
        this.gatewayId = gatewayId;
        this.nodeId = nodeId;
    }

    protected String read(String sensorId, String pin) throws UDOOIoTException{
        if(mUDOOIoTRestManager != null) {
            IoTApiResponseModel ioTApiResponseModel = mUDOOIoTRestManager.read(gatewayId, nodeId, sensorId, pin);
            if(ioTApiResponseModel!= null) return ioTApiResponseModel.value;
        }
        return "";
    }
    protected void write(String sensorId, String pin, String value) throws UDOOIoTException{
        if(mUDOOIoTRestManager != null) mUDOOIoTRestManager.write(gatewayId, nodeId, sensorId, pin, value);
    }
}
