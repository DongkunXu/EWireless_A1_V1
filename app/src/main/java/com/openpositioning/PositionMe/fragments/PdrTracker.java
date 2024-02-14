package com.openpositioning.PositionMe.fragments;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.openpositioning.PositionMe.sensors.SensorFusion;

import java.util.ArrayList;
import java.util.List;

public class PdrTracker {
    private SensorFusion sensorFusion;
    private GoogleMap map;
    private List<LatLng> pathPoints; // 轨迹点列表
    private Polyline currentPolyline; // 当前绘制的Polyline
    int Yellow = Color.argb(255, 245, 245, 0); // 参数分别代表透明度，红色，绿色，蓝色分量

    public PdrTracker(Context context, GoogleMap map) {
        this.sensorFusion = SensorFusion.getInstance();
        this.map = map;
        this.pathPoints = new ArrayList<>();

        sensorFusion.setContext(context);
        setupSensorFusionCallbacks();
    }

    private void setupSensorFusionCallbacks() {
        sensorFusion.setSensorUpdateCallback(new SensorFusion.SensorUpdateCallback() {
            @Override
            public void onLocationChanged(LatLng newPosition) {
                if (newPosition != null) {
                    pathPoints.add(newPosition); // 向轨迹点列表添加新位置
                    drawPath(); // 绘制轨迹
                }
            }

            @Override
            public void onOrientationChanged(float newOrientation) {
                // 方向变化的处理可以在这里实现
            }
        });
    }

    // 绘制轨迹
    private void drawPath() {
        if (currentPolyline != null) {
            currentPolyline.remove(); // 移除之前的轨迹以避免重叠
        }
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(pathPoints)
                .width(5)
                .color(Yellow);
        currentPolyline = map.addPolyline(polylineOptions); // 使用最新的轨迹点列表绘制轨迹
    }

    public void startTracking() {
        pathPoints.clear(); // 开始新的追踪前清空轨迹点列表
        sensorFusion.resumeListening();
    }

    public void stopTracking() {
        sensorFusion.stopListening();
        // 停止追踪后的逻辑可以在这里实现
    }
}


