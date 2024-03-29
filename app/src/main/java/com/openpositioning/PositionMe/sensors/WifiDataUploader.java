package com.openpositioning.PositionMe.sensors;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import android.os.Handler;
//import java.util.logging.Handler;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class WifiDataUploader {
    //public LatLng NewLatLon;
    // 服务器URL
    public interface WifiDataUploadCallback {
        void onUploadComplete(LatLng latLng);
    }


    private static final String SERVER_URL = "https://openpositioning.org/api/position/fine";
    public interface WifiDataCallback {
        void onUploadComplete(LatLng latLng);
        void onUploadFailure(Exception e);
    }

    private static WifiDataUploadCallback uploadCallback;

    public WifiDataUploader(WifiDataUploadCallback callback) {
        this.uploadCallback = callback;
    }


    // 将Wi-Fi列表转换为JSON并上传
    public static void uploadWifiList(List<Wifi> wifiList) {
        if (wifiList != null) {
            try {
                JSONObject jsonWifiData = new JSONObject(); // 创建存储Wi-Fi数据的JSON对象
                JSONObject jsonWifiPrepared = new JSONObject(); // 创建最终要上传的JSON对象

                // 将Wi-Fi列表转换为JSON对象
                for (Wifi wifi : wifiList) {
                    String macAddress = String.valueOf(wifi.getBssid());
                    int signalStrength = wifi.getLevel();
                    jsonWifiData.put(macAddress, signalStrength); // 将MAC地址和信号强度放入jsonWifiData
                }

                jsonWifiPrepared.put("wf", jsonWifiData); // 将jsonWifiData放入jsonWifiPrepared，使用"wf"作为键

                // 异步执行上传操作，避免阻塞UI线程
                new Thread(() -> {
                    try {
                        LatLng latLng = sendData(jsonWifiPrepared.toString()); // 使用修改后的json格式

                    } catch (Exception e) {
                        Log.e("WifiDataUploader", "Failed to upload Wi-Fi data", e);
                    }
                }).start();

            } catch (JSONException e) {
                Log.e("WifiDataUploader", "JSON Exception", e);
            }
        }
    }

    // 使用HTTP POST发送数据
    private static LatLng sendData(String jsonData) throws Exception {
        WifiDataUploader.uploadCallback = uploadCallback;
        StringBuilder response = new StringBuilder();
        URL url = new URL(SERVER_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("accept", "application/json");
        conn.setDoOutput(true);

        // 发送JSON数据
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // 检查响应码
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Log.i("WifiDataUploader", "Wi-Fi data uploaded successfully.");
            try (InputStream is = conn.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            JSONObject responseObject = new JSONObject(response.toString()); // This variable must be declared in the scope
            // 在这里处理服务器返回的数据
            Log.i("WifiDataUploader", "Response from server: " + response.toString());

            // 假设响应JSON中包含latitude和longitude字段
            double latitude = responseObject.getDouble("lat");
            double longitude = responseObject.getDouble("lon");
            LatLng NewLatLng = new LatLng(latitude, longitude); // 使用解析出的纬度和经度创建LatLng对象
            conn.disconnect();
            if (uploadCallback != null) {
                //uploadCallback.onUploadComplete(NewLatLng);
                new Handler(Looper.getMainLooper()).post(() -> {
                    uploadCallback.onUploadComplete(new LatLng(latitude, longitude));
                });
            }
            return NewLatLng;
        } else {
            Log.e("WifiDataUploader", "Failed to upload Wi-Fi data, HTTP response code: " + responseCode);
            conn.disconnect();
            return null;
        }
    }

}
