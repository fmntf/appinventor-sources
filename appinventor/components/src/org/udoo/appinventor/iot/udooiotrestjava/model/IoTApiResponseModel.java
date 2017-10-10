package org.udoo.appinventor.iot.udooiotrestjava.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by harlem88 on 20/06/17.
 */
public class IoTApiResponseModel {
    public String status;
    public String sensor_id;
    public String id;
    public String sensor_type;
    public String value;


    public static IoTApiResponseModel Builder(String jsonResp) {
        IoTApiResponseModel resp = new IoTApiResponseModel();
        try {
            JSONObject jsonObject = new JSONObject(jsonResp);
            resp.id = jsonObject.has("id") ? jsonObject.getString("id") : "";
            resp.status = jsonObject.has("status") ? jsonObject.getString("status") : "";
            resp.sensor_id = jsonObject.has("sensor_id") ? jsonObject.getString("sensor_id") : "";
            resp.sensor_type = jsonObject.has("sensor_type") ? jsonObject.getString("sensor_type") : "";
            resp.value = jsonObject.has("value") ? jsonObject.getString("value") : "";
            return resp;
        } catch (JSONException e) {
        }
        return null;
    }
}
/*
*  {"value":0,"sensor_type":"digital",
*  "gateway_id":"809d67539ed4ccc09c81a28e978da31922f68528dab02eed1d8d316565948a02",
*  "sensor_id":"12"}
*
*  {"status":"ok","gateway_id":"809d67539ed4ccc09c81a28e978da31922f68528dab02eed1d8d316565948a02"
*  ,"sensor_type":"digital","sensor_id":"13","id":"ttyMCC-42049d4e3167fd0"}
* */