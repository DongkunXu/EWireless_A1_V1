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
            Log.e("GeofenceReceiver", "地理围栏事件错误: " + geofencingEvent.getErrorCode());
            return;
        }

        // 获取地理围栏事件的类型
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.d("GeofenceReceiver", "地理围栏事件触发, 类型: " + geofenceTransition);

        // 检查触发的地理围栏列表是否为null并且不为空
        if (geofencingEvent.getTriggeringGeofences() != null && !geofencingEvent.getTriggeringGeofences().isEmpty()) {
            String geofenceId = geofencingEvent.getTriggeringGeofences().get(0).getRequestId();

            Intent localIntent = new Intent("ACTION_GEOFENCE_EVENT");
            localIntent.putExtra("GEOFENCE_ID", geofenceId);
            localIntent.putExtra("GEOFENCE_TRANSITION_TYPE", geofenceTransition);

            // 在主线程上显示Toast消息
            //Handler handler = new Handler(Looper.getMainLooper());
            //handler.post(() -> Toast.makeText(context, "Cross the boundary", Toast.LENGTH_SHORT).show());

            // 发送本地广播
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
        } else {
            Log.e("GeofenceReceiver", "触发的地理围栏列表为空或null");
        }

        if (geofencingEvent.hasError()) {
            int errorCode = geofencingEvent.getErrorCode();
            Log.e("GeofenceReceiver", "地理围栏事件错误代码: " + errorCode);
            // 根据错误代码进行适当处理
            return;
        }

    }
}
