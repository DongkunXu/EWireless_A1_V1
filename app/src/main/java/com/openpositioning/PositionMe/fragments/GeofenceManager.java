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
        // Register a geofence for Nuclear building
        registerGeofence(GEOFENCE_ID_NUCLEAR, new LatLng(55.923055, -3.174247), 150);
        // Register for geofencing for the NK Library
        registerGeofence(GEOFENCE_ID_NKLIB, new LatLng(55.922904, -3.174967), 50);
    }

    private void registerGeofence(String geofenceId, LatLng center, float radius) {
        Log.d("GeofenceManager", "Registering geofence: " + geofenceId + ", center: " + center + ", radius: " + radius + "m");

        Geofence geofence = new Geofence.Builder()
                .setRequestId(geofenceId)
                .setCircularRegion(center.latitude, center.longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient.addGeofences(getGeofencingRequest(geofence), getGeofencePendingIntent())
                    .addOnSuccessListener(aVoid -> Log.d("GeofenceManager", "Geofencing registration successful: " + geofenceId))
                    .addOnFailureListener(e -> Log.e("GeofenceManager", "Geofence registration failed: " + geofenceId, e));
        } else {
            // No permission, request permission
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

    }

    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }

    //Check if there are any pending geofences and check the Android version to comply with Android regulations.
    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        // Check the Android version before setting flags
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, flags);
        return geofencePendingIntent;
    }
}

