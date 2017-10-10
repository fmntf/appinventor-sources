package org.udoo.appinventor.iot.udooiotrestjava.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by harlem88 on 20/06/17.
 */
public class NodeModel {
    public long interval_time;
    public ArrayList<AddressModel> address;
    public String product_type;
    public String id;
    public String displayName;
    public String _id;
    public ArrayList<GatewayModel> actuators;
    public HashMap<String, SensorModel> sensors;

    public static NodeModel Builder(JSONObject jsonObject) {
        NodeModel nodeModel = new NodeModel();
        try {
            nodeModel._id = jsonObject.has("_id") ? jsonObject.getString("_id") : "";
            nodeModel.product_type = jsonObject.getString("product_type");
            nodeModel.id = jsonObject.getString("id");
            nodeModel.displayName = jsonObject.has("displayName") ? jsonObject.getString("displayName") : "";

            SensorModel sm;
            JSONArray jsonArray = jsonObject.getJSONArray("sensors");
            nodeModel.sensors = new HashMap<String, SensorModel>();
            for(int i = 0; i<jsonArray.length(); i++){
                sm = SensorModel.Builder(jsonArray.getJSONObject(i));
                if(sm != null) nodeModel.sensors.put(sm.sensor_id, sm);
            }

            return nodeModel;

        } catch (JSONException e) {}
        return null;
    }
}
