package com.openpositioning.PositionMe.fragments;

import android.content.Context;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.openpositioning.PositionMe.sensors.SensorFusion;

public class PdrTracker {
    private final SensorFusion sensorFusion;
    private final GoogleMap map;
    private PolylineOptions currentPath;

    public PdrTracker(Context context, GoogleMap map) {
        this.sensorFusion = SensorFusion.getInstance();
        this.map = map;
        this.currentPath = new PolylineOptions();

        // 确保SensorFusion已经使用正确的上下文初始化
        sensorFusion.setContext(context);
        setupSensorFusionCallbacks();
    }

    private void setupSensorFusionCallbacks() {
        sensorFusion.setSensorUpdateCallback(new SensorFusion.SensorUpdateCallback() {
            @Override
            public void onLocationChanged(LatLng newPosition) {
                updatePath(newPosition);
            }

            @Override
            public void onOrientationChanged(float newOrientation) {
                // 如果需要，可以处理方向变化
            }
        });
    }

    private void updatePath(LatLng newPosition) {
        currentPath.add(newPosition);
        map.addPolyline(currentPath);
    }

    public void startTracking() {
        sensorFusion.resumeListening();
        // 初始化路径
        currentPath = new PolylineOptions();
    }

    public void stopTracking() {
        sensorFusion.stopListening();
    }
}
