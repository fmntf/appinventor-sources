package org.udoo.appinventor.iot.udooiotrestjava.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by harlem88 on 20/06/17.
 */
public class NetworkModel {
    public String username;
    public HashMap<String, GatewayModel> gateways;
    public HashMap<String, AliasModel> gwAliases;
    public HashMap<String, AliasModel> nodeAliases;



    @Override
    public String toString() {
        return username + "gateways: " + gateways.size() + "";
    }

    public static NetworkModel Builder(String jsonResp) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResp);
            NetworkModel networkModel = new NetworkModel();
            networkModel.username = jsonObject.getString("username");
            JSONArray jsonArray = jsonObject.getJSONArray("gateways");

            GatewayModel gw;
            networkModel.gateways = new HashMap<String, GatewayModel>();
            for(int i = 0; i<jsonArray.length(); i++){
                gw = GatewayModel.Builder(jsonArray.getJSONObject(i));
                if(gw != null) networkModel.gateways.put(gw.gateway_id, gw);
            }
            if(jsonObject.has("gateway_aliases")){
                JSONArray jsonArrAliases = jsonObject.getJSONArray("gateway_aliases");
                networkModel.gwAliases = new HashMap<String, AliasModel>();
                AliasModel aliasModel;
                for (int i = 0; i < jsonArrAliases.length(); i++) {
                    aliasModel = AliasModel.Builder(jsonArrAliases.getJSONObject(i));
                    if (aliasModel != null) networkModel.gwAliases.put(aliasModel.name, aliasModel);
                }
            }
            if(jsonObject.has("node_aliases")){
                JSONArray jsonArrAliases = jsonObject.getJSONArray("node_aliases");
                networkModel.nodeAliases = new HashMap<String, AliasModel>();
                AliasModel aliasModel;
                for (int i = 0; i < jsonArrAliases.length(); i++) {
                    aliasModel = AliasModel.Builder(jsonArrAliases.getJSONObject(i));
                    if (aliasModel != null) networkModel.nodeAliases.put(aliasModel.name, aliasModel);
                }
            }
            return networkModel;
        } catch (JSONException e) {}
        return null;
    }
}
