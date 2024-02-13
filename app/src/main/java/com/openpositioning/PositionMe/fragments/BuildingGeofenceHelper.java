package com.openpositioning.PositionMe.fragments;

import android.app.PendingIntent;
import android.content.Context;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class BuildingGeofenceHelper {
    private static final float RADIUS_IN_METERS = 100.0f;

    public static void createGeofencesForBuildings(LatLng nuclearBuildingSouthWest, LatLng nuclearBuildingNorthEast,
                                                   LatLng murrayLibrarySouthWest, LatLng murrayLibraryNorthEast,
                                                   PendingIntent geofencePendingIntent, Context context) {
        // 设置核大楼的地理围栏
        Geofence nuclearBuildingGeofence = new Geofence.Builder()
                .setRequestId("NuclearBuilding")
                .setCircularRegion(nuclearBuildingSouthWest.latitude, nuclearBuildingSouthWest.longitude, RADIUS_IN_METERS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        // 设置Murray图书馆的地理围栏
        Geofence murrayLibraryGeofence = new Geofence.Builder()
                .setRequestId("MurrayLibrary")
                .setCircularRegion(murrayLibrarySouthWest.latitude, murrayLibrarySouthWest.longitude, RADIUS_IN_METERS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        // 创建地理围栏请求
        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .addGeofence(nuclearBuildingGeofence)
                .addGeofence(murrayLibraryGeofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();

        // 注册地理围栏
        LocationServices.getGeofencingClient(context).addGeofences(geofencingRequest, geofencePendingIntent);
    }
}
