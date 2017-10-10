package org.udoo.appinventor.iot.udooiotrestjava.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by harlem88 on 20/06/17.
 */
public class GatewayModel {
    public String _id;
    public String gateway_id;
    public String __v;
    public String board_id;
    public String company;
    public String displayName;
    public boolean connected;
    public HashMap<String, NodeModel> nodes;

    public static GatewayModel Builder(JSONObject jsonObject) {
        GatewayModel gatewayModel = new GatewayModel();
        try {
            gatewayModel._id = jsonObject.has("_id") ? jsonObject.getString("_id") : "";
            gatewayModel.gateway_id = jsonObject.has("gateway_id") ? jsonObject.getString("gateway_id") : "";
            gatewayModel.__v = jsonObject.has("__v") ? jsonObject.getString("__v") : "";
            gatewayModel.board_id = jsonObject.has("board_id") ? jsonObject.getString("board_id") : "";
            gatewayModel.company = jsonObject.has("company") ? jsonObject.getString("company") : "";
            gatewayModel.displayName = jsonObject.has("displayName") ? jsonObject.getString("displayName") : "";
            gatewayModel.connected = jsonObject.has("connected") ? jsonObject.getBoolean("connected") : false;
            JSONArray jsonArray = jsonObject.getJSONArray("nodes") ;

            NodeModel node;
            gatewayModel.nodes = new HashMap<String, NodeModel>();
            for(int i = 0; i<jsonArray.length(); i++){
                node = NodeModel.Builder(jsonArray.getJSONObject(i));
                if(node != null) gatewayModel.nodes.put(node.id, node);
            }
            return gatewayModel;

        } catch (JSONException e) {}
        return null;
    }
}
