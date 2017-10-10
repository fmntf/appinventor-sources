package org.udoo.appinventor.iot.udooiotrestjava;

import android.util.Log;

import org.udoo.appinventor.iot.udooiotrestjava.model.DataModel;
import org.udoo.appinventor.iot.udooiotrestjava.model.IoTApiResponseModel;
import org.udoo.appinventor.iot.udooiotrestjava.model.LoginResponseModel;
import org.udoo.appinventor.iot.udooiotrestjava.model.NetworkModel;
import org.udoo.appinventor.iot.udooiotrestjava.util.ParameterStringBuilder;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by harlem88 on 9/18/17.
 */

public class UDOOIoTRestService {
    private static final String URL = "http://enterprise.media.unisi.it";
    private static final String TOKEN = "/token";
    private static final String API = "/ext";
    private static final String NETWORK = API + "/network";
    private static final String SENSOR_PATH = "/sensors";
    private static final String READ_PATH = "/read";
    private static final String WRITE_PATH = "/write";
    private static ExecutorService executors;


    static Future<DataModel<LoginResponseModel>> Login(final String username, final String password) {
        if (executors == null) executors = Executors.newSingleThreadExecutor();
        return executors.submit(new Callable<DataModel<LoginResponseModel>>() {
            public DataModel<LoginResponseModel> call() throws Exception {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("username", username);
                parameters.put("password", password);
                DataModel<LoginResponseModel> dataModelResponse = new DataModel<LoginResponseModel>();
                try {
                    String jsonResp = ReadUrl(URL + TOKEN, parameters, "");
                    LoginResponseModel loginResponseModel = LoginResponseModel.Builder(jsonResp);
                    if (loginResponseModel != null) {
                        dataModelResponse.data = loginResponseModel;
                    } else {
                        dataModelResponse.error = new Throwable("Error login");
                    }
                } catch (Exception e) {
                    dataModelResponse.error = e;
                }
                return dataModelResponse;
            }
        });
    }


    static NetworkModel GetNetwork(final String token) {
        Future<DataModel<String>> ioTApiResponseModel = ApiCall(token, NETWORK);
        NetworkModel networkModel = null;
        try {
            DataModel<String> stringDataModel = ioTApiResponseModel.get();
            if(stringDataModel != null && stringDataModel.error == null) networkModel = NetworkModel.Builder(stringDataModel.data);
        } catch (Exception e) { }
        return networkModel;
    }


    static IoTApiResponseModel ApiRead(String token, String gatewayId, String nodeId, String sensorId, String pin) {
        return ApiCall(true, token, READ_PATH, gatewayId, nodeId, sensorId, pin, null);
    }

    static IoTApiResponseModel ApiWrite(String token, String gatewayId, String nodeId, String sensorId, String pin, String value) {
        return ApiCall(false, token, WRITE_PATH, gatewayId, nodeId, sensorId, pin, value);
    }


    private static String ReadUrl(String urlString, Map<String, String> params, String token) throws Exception {
        BufferedReader reader = null;
        try {
            HttpURLConnection conn;
            URL url = new URL(urlString);
            Log.i("REQ: ", url.toString());
            if(urlString.startsWith("https")){
                 conn = (HttpsURLConnection) url.openConnection();
            }
            else{
                conn = (HttpURLConnection) url.openConnection();
            }

            conn.setDoOutput(true);
            if (params != null) {
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "utf-8");
                conn.setUseCaches(false);
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(ParameterStringBuilder.getParamsString(params));
                out.flush();
                out.close();
            }
            if (token != null && token.length() > 0) {
                conn.setRequestProperty("Authorization", "JWT " + token);
            }
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            System.out.println(buffer.toString());
            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    private static IoTApiResponseModel ApiCall(boolean sync, final String token, final String operation, final String gatewaId, final String nodeId, final String sensorId, final String pin, final String value) {
        Future<DataModel<String>> result = ApiCall(token, API + SENSOR_PATH + operation + '/' + gatewaId + '/' + nodeId + '/' + sensorId + '/' + pin + (value != null ? '/' + value : ""));
        if (sync){
            IoTApiResponseModel ioTApiResponseModel = new IoTApiResponseModel();
            try {
                DataModel<String> stringDataModel = result.get();
                if (stringDataModel != null && stringDataModel.data != null) {
                    ioTApiResponseModel = IoTApiResponseModel.Builder(stringDataModel.data);
                }
            } catch (Exception e) {}
            return ioTApiResponseModel;
        }
        return null;
    }

    private static Future<DataModel<String>> ApiCall(final String token, final String path) {
        if (executors == null) executors = Executors.newSingleThreadExecutor();
        return executors.submit(new Callable<DataModel<String>>() {
            public DataModel<String> call() throws Exception {

                BufferedReader reader = null;
                DataModel<String> result = new DataModel<String>();
                try {
                    URL url = new URL(URL + path);
                    Log.i("REQ: ", url.toString());
                    HttpURLConnection conn;
                    if (URL.startsWith("https")) {
                        conn = (HttpsURLConnection) url.openConnection();
                    } else {
                        conn = (HttpURLConnection) url.openConnection();
                    }
                    if (conn != null) {
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(10000);
                        conn.setReadTimeout(10000);

                        if (token != null && token.length() > 0) {
                            conn.setRequestProperty("Authorization", "JWT " + token);
                        }
                        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder buffer = new StringBuilder();
                        int read;
                        char[] chars = new char[1024];
                        while ((read = reader.read(chars)) != -1)
                            buffer.append(chars, 0, read);

                        Log.i("RESP: ", buffer.toString());
                        if (buffer.length() > 0) result.data = buffer.toString();
                    }
                } catch (IOException e) {
                    result.error = e;
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Exception e1) {
                        }
                    }

                }
                return result;
            }
        });
    }
}
