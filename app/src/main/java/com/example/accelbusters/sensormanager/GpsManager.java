package com.example.accelbusters.sensormanager;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.LinkedList;

public class GpsManager {

    private static final String TAG = "GpsManager";
    private static final int SPEED_HISTORY_SIZE = 5; // 存储最近5次的速度
    private static final double MOVEMENT_THRESHOLD = 1.0; // 设置阈值为1米

    private double speed = 0.0;
    private LinkedList<Double> speedHistory = new LinkedList<>();
    private Handler handler;
    private Location previousLocation;
    private TextView tvLocation, tvSpeed;

    private LocationCallback locationCallback;

    public GpsManager(Context context, TextView locationTextView, TextView speedTextView) {
        this.tvLocation = locationTextView;
        this.tvSpeed = speedTextView;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void startLocationUpdates(Context context) {
        // 设置GPS请求参数
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // 每秒更新一次
        locationRequest.setFastestInterval(500); // 最快每半秒更新
        locationRequest.setSmallestDisplacement(1); // 设置最小位移，只有当用户移动超过5米时才会触发更新

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        // 请求位置更新
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) return;

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    // 更新位置
                    updateLocation(location);
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void updateLocation(Location location) {
        // 计算速度，更新UI
        if (previousLocation != null) {
            double distance = previousLocation.distanceTo(location);  // 距离
            long timeDelta = location.getTime() - previousLocation.getTime();  // 时间差

            // 计算速度
            if (timeDelta > 0) {
                double currentSpeed = distance / (timeDelta / 1000.0);  // 单位：米/秒

                // 如果移动距离超过阈值，则更新速度
                if (distance > MOVEMENT_THRESHOLD) {
                    addSpeedToHistory(currentSpeed); // 记录当前速度
                    speed = getAverageSpeed();  // 获取加权平均速度
                }
            }
        }
        previousLocation = location;

        // 更新UI
        tvLocation.setText("Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
        tvSpeed.setText("Speed: " + String.format("%.2f", speed) + " m/s");
    }

    private void addSpeedToHistory(double speed) {
        // 存储并更新速度历史
        if (speedHistory.size() >= SPEED_HISTORY_SIZE) {
            speedHistory.poll(); // 删除最旧的速度数据
        }
        speedHistory.add(speed);
    }

    private double getAverageSpeed() {
        double sum = 0.0;
        for (Double s : speedHistory) {
            sum += s;
        }
        return sum / speedHistory.size();
    }

    public void stopLocationUpdates(Context context) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public String getLocationData() {
        if (previousLocation != null) {
            return "Latitude: " + previousLocation.getLatitude() + ", Longitude: " + previousLocation.getLongitude();
        }
        return "Location not available";
    }

    public String getSpeed() {
        return String.format("%.2f", speed) + " m/s";
    }
}
