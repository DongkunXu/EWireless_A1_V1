package com.openpositioning.PositionMe.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import android.util.Log;


import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e("GeofenceReceiver", "Geofence event error: " + geofencingEvent.getErrorCode());
            return;
        }

        // Get the type of geofence event
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.d("GeofenceReceiver", "Geofence event trigger, type: " + geofenceTransition);

        if (geofencingEvent.getTriggeringGeofences() != null && !geofencingEvent.getTriggeringGeofences().isEmpty()) {
            String geofenceId = geofencingEvent.getTriggeringGeofences().get(0).getRequestId();

            Intent localIntent = new Intent("ACTION_GEOFENCE_EVENT");
            localIntent.putExtra("GEOFENCE_ID", geofenceId);
            localIntent.putExtra("GEOFENCE_TRANSITION_TYPE", geofenceTransition);


            // Send local broadcast
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
        } else {
            Log.e("GeofenceReceiver", "List of triggered geofences is empty or null");
        }

        if (geofencingEvent.hasError()) {
            int errorCode = geofencingEvent.getErrorCode();
            Log.e("GeofenceReceiver", "Geofence event error code: " + errorCode);
            return;
        }

    }
}
