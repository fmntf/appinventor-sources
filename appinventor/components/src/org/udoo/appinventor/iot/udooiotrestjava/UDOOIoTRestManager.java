package org.udoo.appinventor.iot.udooiotrestjava;

import org.udoo.appinventor.iot.udooiotrestjava.node.ArduinoNode;
import org.udoo.appinventor.iot.udooiotrestjava.node.NeoNode;
import org.udoo.appinventor.iot.udooiotrestjava.model.NetworkModel;
import org.udoo.appinventor.iot.udooiotrestjava.model.DataModel;
import org.udoo.appinventor.iot.udooiotrestjava.model.LoginResponseModel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.udoo.appinventor.iot.udooiotrestjava.model.AliasModel;
import org.udoo.appinventor.iot.udooiotrestjava.model.GatewayModel;
import org.udoo.appinventor.iot.udooiotrestjava.model.IoTApiResponseModel;
import org.udoo.appinventor.iot.udooiotrestjava.model.NodeModel;
import org.udoo.appinventor.iot.udooiotrestjava.model.SensorModel;

/**
 * Created by harlem88 on 20/06/17.
 */
public class UDOOIoTRestManager {
    private String token, username, password;
    private NetworkModel networkModel;

    public UDOOIoTRestManager() {
    }

    public void login(String user, String pass, final OnResult<Boolean> onResult) {
        this.username = user;
        this.password = pass;

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            public void run() {
                boolean success = false;
                Future<DataModel<LoginResponseModel>> result = UDOOIoTRestService.Login(username, password);
                try {
                    DataModel<LoginResponseModel> dataModelLogin = result.get();
                    if (dataModelLogin != null && dataModelLogin.data != null && dataModelLogin.data.status) {
                        token = dataModelLogin.data.token;
                        success = refreshNetwork();

                    }
                } catch (Exception e) {
                }

                if (onResult != null) onResult.onSuccess(success);
            }
        });
    }

    public boolean refreshNetwork() throws UDOOIoTException {
        NetworkModel model = UDOOIoTRestService.GetNetwork(token);
        boolean success = false;
        if (model != null) {
            networkModel = model;
            success = true;
        }
        return success;
    }

    public IoTApiResponseModel read(String gatewayId, String nodeId, String sensorId, String pin) throws UDOOIoTException {
        return UDOOIoTRestService.ApiRead(token, gatewayId, nodeId, sensorId, pin);
    }

    public void write(String gatewayId, String nodeId, String sensorId, String pin, String value) throws UDOOIoTException {
        UDOOIoTRestService.ApiWrite(token, gatewayId, nodeId, sensorId, pin, value);
    }

    private String[] getNodeByName(String name, String type) {
        String[] res = new String[0];
        if (networkModel != null && name != null && name.equalsIgnoreCase("default")) {
            if (networkModel.gateways != null && networkModel.gateways.keySet().iterator().hasNext()) {
                GatewayModel gatewayModel = networkModel.gateways.get(networkModel.gateways.keySet().iterator().next());
                return getFirstNode(res, gatewayModel, type);
            }
        } else if (networkModel != null && networkModel.nodeAliases != null && networkModel.nodeAliases.containsKey(name)) {
            AliasModel aliasModel = networkModel.nodeAliases.get(name);
            if (networkModel.gateways != null && networkModel.gateways.containsKey(aliasModel.gateway_id)) {
                GatewayModel gatewayModel = networkModel.gateways.get(aliasModel.gateway_id);
                if (gatewayModel != null && gatewayModel.nodes != null && gatewayModel.nodes.containsKey(aliasModel.node_id)) {
                    NodeModel node = gatewayModel.nodes.get(aliasModel.node_id);
                    if (node != null && node.product_type.equalsIgnoreCase(type)) {
                        res = new String[3];
                        res[0] = gatewayModel.gateway_id;
                        res[1] = node.id;
                        res[2] = node.product_type;
                    }
                }
            }
        }
        return res;
    }

    public String[] getFirstNode(String res[], GatewayModel gatewayModel, String type) {
        if (gatewayModel != null && gatewayModel.nodes != null && gatewayModel.nodes.keySet().iterator().hasNext()) {
            Iterator<String> iterator = gatewayModel.nodes.keySet().iterator();
            NodeModel node;
            while (iterator.hasNext()) {
                node = gatewayModel.nodes.get(iterator.next());
                if (node != null && node.product_type.equalsIgnoreCase(type)) {
                    res = new String[3];
                    res[0] = gatewayModel.gateway_id;
                    res[1] = node.id;
                    res[2] = node.product_type;
                    break;
                }
            }
        }
        return res;
    }

    public HashMap<String, SensorModel> getNodeSensors(String gatewayId, String nodeId) {
        HashMap<String, SensorModel> sensors = new HashMap<String, SensorModel>();
        if (networkModel != null && networkModel.gateways != null && networkModel.gateways.containsKey(gatewayId)) {
            GatewayModel gatewayModel = networkModel.gateways.get(gatewayId);
            if (gatewayModel != null && gatewayModel.nodes != null && gatewayModel.nodes.containsKey(nodeId)) {
                NodeModel nodeModel = gatewayModel.nodes.get(nodeId);
                sensors.putAll(nodeModel.sensors);
            }
        }
        return sensors;
    }

    public String[] getNodeFromGateway(String gatewayId, String type) {
        String[] res = new String[0];
        if (networkModel != null && gatewayId != null) {
            if (networkModel.gateways != null && networkModel.gateways.containsKey(gatewayId)) {
                GatewayModel gatewayModel = networkModel.gateways.get(gatewayId);
                return getFirstNode(res, gatewayModel, type);
            }
        }
        return res;
    }

    public NeoNode getNeo(String gatewayAlias) {
        NeoNode neoNode = null;
        String[] values = new String[0];
        if (gatewayAlias != null) {
            if (gatewayAlias.equalsIgnoreCase("default")) {
                values = getNodeByName("default", "neo");
            } else if (networkModel.gwAliases != null && networkModel.gwAliases.containsKey(gatewayAlias)) {
                AliasModel aliasModel = networkModel.gwAliases.get(gatewayAlias);
                values = getNodeFromGateway(aliasModel.gateway_id, "neo");
            }

            if (values.length > 0) {
                neoNode = new NeoNode(values[0], values[1], this);
                neoNode.setSensors(getNodeSensors(neoNode.gatewayId, neoNode.nodeId));
            }
        }
        return neoNode;
    }

    public ArduinoNode getArduino(String gatewayAlias) {
        ArduinoNode arduinoNode = null;
        String[] values = new String[0];
        if (gatewayAlias != null) {
            if (gatewayAlias.equalsIgnoreCase("default")) {
                values = getNodeByName("default", "arduino");
            } else if (networkModel.gwAliases != null && networkModel.gwAliases.containsKey(gatewayAlias)) {
                AliasModel aliasModel = networkModel.gwAliases.get(gatewayAlias);
                values = getNodeFromGateway(aliasModel.gateway_id, "arduino");
            }

            if (values.length > 0) {
                arduinoNode = new ArduinoNode(values[0], values[1], this);
            }
        }

        return arduinoNode;
    }

    public NeoNode getNeoByName(String name) {
        NeoNode arduinoNode = null;
        String[] values = getNodeByName(name, "neo");
        if (values.length > 0) {
            arduinoNode = new NeoNode(values[0], values[1], this);
        }
        return arduinoNode;
    }

    public ArduinoNode getArduinoByName(String name) {
        ArduinoNode arduinoNode = null;
        String[] values = getNodeByName(name, "arduino");
        if (values.length > 0) {
            arduinoNode = new ArduinoNode(values[0], values[1], this);
        }
        return arduinoNode;
    }

    public ArduinoNode getArduinoById(String id) {
        ArduinoNode arduinoNode = null;
        String[] values = getNodeById(id);
        if (values.length > 0 && values[2].equalsIgnoreCase("arduino")) {
            arduinoNode = new ArduinoNode(values[0], values[1], this);
        }
        return arduinoNode;
    }


    private String[] getNodeById(String id) {
        String[] res = new String[0];
        if (networkModel != null && networkModel.gateways != null) {
            GatewayModel gw;
            NodeModel node;
            Set<String> keys = networkModel.gateways.keySet();
            for (String key : keys) {
                gw = networkModel.gateways.get(key);
                if (gw.nodes != null && gw.nodes.containsKey(id)) {
                    node = gw.nodes.get(id);
                    res = new String[3];
                    res[0] = gw.gateway_id;
                    res[1] = node.id;
                    res[2] = node.product_type;

                }
            }
        }
        return res;
    }

    public NeoNode getNeoById(String id) {
        NeoNode neoNode = null;
        String[] values = getNodeById(id);
        if (values.length > 0 && values[2].equalsIgnoreCase("neo")) {
            neoNode = new NeoNode(values[0], values[1], this);
        }
        return neoNode;
    }





    /*
    * fun getArduinoFromName(name : String): Arduino {
    var gatewayId:String = ""
    var nodeId:String = ""

    network?.gateways?.forEach {
        Log.i("ddd", ""+it.displayName)
        val tmpgatewayId = it.gateway_id
        it.nodes.filter{
            name == it.display_name
        }.let { if(it.size > 0){
                    gatewayId = tmpgatewayId
                    nodeId = it[0].id
            }
        }
    }

    return Arduino(gatewayId, nodeId, apiService)
}
*/

}
