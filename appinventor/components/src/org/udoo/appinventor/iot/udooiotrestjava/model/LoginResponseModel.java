package org.udoo.appinventor.iot.udooiotrestjava.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by harlem88 on 20/06/17.
 */
public class LoginResponseModel {
    public boolean status;
    public String token;

    public static LoginResponseModel Builder(String jsonResp) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResp);
            LoginResponseModel loginResponseModel = new LoginResponseModel();
            loginResponseModel.status = jsonObject.getBoolean("status");
            loginResponseModel.token = jsonObject.getString("token");
            return loginResponseModel;
        } catch (JSONException e) {}
        return null;
    }
}
