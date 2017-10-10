package org.udoo.appinventor.iot.udooiotrestjava.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ErrorModel {

    public String err;

    public static ErrorModel Builder(String jsonResp) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResp);
            if (jsonObject.has("err")) {
                ErrorModel errorModel = new ErrorModel();
                errorModel.err = jsonObject.getString("err");
                return errorModel;
            }
        } catch (JSONException e) {}
        return null;
    }
}
