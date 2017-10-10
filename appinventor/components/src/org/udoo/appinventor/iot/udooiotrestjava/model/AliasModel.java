package org.udoo.appinventor.iot.udooiotrestjava.model;

import org.json.JSONException;
import org.json.JSONObject;

public class AliasModel {
    public String gateway_id;
    public String node_id;
    public String name;

    public static AliasModel Builder(JSONObject jsonObject) {
        AliasModel aliasModel = new AliasModel();
        try {
            aliasModel.name = jsonObject.has("alias") ? jsonObject.getString("alias") : "";
            aliasModel.gateway_id = jsonObject.has("gateway_id") ? jsonObject.getString("gateway_id") : "";
            aliasModel.node_id = jsonObject.has("node_id") ? jsonObject.getString("node_id") : "";
            return aliasModel;

        } catch (JSONException e) {}
        return null;
    }
}
