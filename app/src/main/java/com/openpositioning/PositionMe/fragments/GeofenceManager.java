package com.openpositioning.PositionMe.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceManager {

    public static final String GEOFENCE_ID_NUCLEAR = "NuclearBuilding";
    public static final String GEOFENCE_ID_NKLIB = "NoreenKennethLibrary";
    private Context context;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private static final int REQUEST_CODE = 1; // 或者任何其他未使用的整数


    public GeofenceManager(Context context) {
        this.context = context;
        this.geofencingClient = LocationServices.getGeofencingClient(context);
    }

    public void registerAllGeofences() {
        // 为核能大楼注册地理围栏
        registerGeofence(GEOFENCE_ID_NUCLEAR, new LatLng(55.923055, -3.174247), 80);
        // 为诺琳和肯尼斯·默里图书馆注册地理围栏
        registerGeofence(GEOFENCE_ID_NKLIB, new LatLng(55.922904, -3.174967), 40);
    }

    private void registerGeofence(String geofenceId, LatLng center, float radius) {
        Log.d("GeofenceManager", "正在注册地理围栏: " + geofenceId + ", 中心: " + center + ", 半径: " + radius + "米");

        Geofence geofence = new Geofence.Builder()
                .setRequestId(geofenceId)
                .setCircularRegion(center.latitude, center.longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 已有权限，可以添加地理围栏
            geofencingClient.addGeofences(getGeofencingRequest(geofence), getGeofencePendingIntent())
                    .addOnSuccessListener(aVoid -> Log.d("GeofenceManager", "地理围栏注册成功: " + geofenceId))
                    .addOnFailureListener(e -> Log.e("GeofenceManager", "地理围栏注册失败: " + geofenceId, e));
        } else {
            // 没有权限，需要请求权限
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

    }

    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }
}

