package org.udoo.appinventor.iot.udooiotrestjava.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by harlem88 on 20/06/17.
 */
public class SensorModel {
    public String _id;
    public String sensor_id;
    public String sensor_type;
    public String last_value;
    public String displayName;

    public static SensorModel Builder(JSONObject jsonObject) {
        SensorModel sensorModel = new SensorModel();
        try {
            sensorModel._id = jsonObject.has("_id") ? jsonObject.getString("_id") : "";
            sensorModel.sensor_id = jsonObject.has("sensor_id") ? jsonObject.getString("sensor_id") : "";
            sensorModel.sensor_type = jsonObject.has("sensor_type") ? jsonObject.getString("sensor_type") : "";
            sensorModel.displayName = jsonObject.has("displayName") ? jsonObject.getString("displayName") : "";
            sensorModel.last_value = jsonObject.has("last_value") ? jsonObject.getString("last_value") : "";

        } catch (JSONException e) {
            return null;
        }

        return sensorModel;
    }
}
