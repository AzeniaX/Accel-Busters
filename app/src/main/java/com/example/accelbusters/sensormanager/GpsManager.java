package com.example.accelbusters.sensormanager;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.LinkedList;

public class GpsManager {

    private static final String TAG = "GPS";
    private static final int SPEED_HISTORY_SIZE = 5;
    private static final double MOVEMENT_THRESHOLD = 1.0; // 阈值为1米
    private static final double GPS_ERROR_MARGIN = 0.2; // GPS误差 0.2m

    private double speed = 0.0;
    private LinkedList<Double> speedHistory = new LinkedList<>();
    private Handler handler;
    private Location previousLocation;
    private LinkedList<Location> initialLocations = new LinkedList<>(); // 存储最初的3个GPS点
    private Location centroidLocation; // 重心位置
    private int skippedUpdates = 0; // 未发送的更新次数

    private TextView tvLocation, tvSpeed;
    private LocationCallback locationCallback;

    public GpsManager(Context context, TextView locationTextView, TextView speedTextView) {
        this.tvLocation = locationTextView;
        this.tvSpeed = speedTextView;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void startLocationUpdates(Context context) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // 每秒更新一次
        locationRequest.setFastestInterval(500); // 最快每半秒更新
        locationRequest.setSmallestDisplacement(0); // 最小位移0米
        locationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) return;

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    processLocation(location);
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void processLocation(Location location) {
        if (initialLocations.size() < 3) {
            // 收集最初的3个GPS点
            initialLocations.add(location);
            if (initialLocations.size() == 3) {
                calculateCentroid(); // 计算重心
            }
        } else {
            // 计算当前点与前一点的距离
            if (previousLocation != null) {
                double distance = previousLocation.distanceTo(location);
                long timeDelta = location.getTime() - previousLocation.getTime();

                if (timeDelta > 0) {
                    double currentSpeed = distance / (timeDelta / 1000.0);

                    double X1 = calculateThreshold(currentSpeed);
                    if (distance <= X1) {
                        // 正常更新
                        addSpeedToHistory(currentSpeed);
                        speed = getAverageSpeed();
                        skippedUpdates = 0; // 重置跳过计数
                    } else {
                        // 忽略异常数据
                        skippedUpdates++;
                        return;
                    }
                }
            }
            previousLocation = location;
//            Log.d(TAG, "location"+location);
            // 更新UI
            tvLocation.setText("Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
            tvSpeed.setText("Speed: " + String.format("%.2f", speed) + " m/s");
        }
    }

    private void calculateCentroid() {
        // 使用3个点计算重心
        double x = 0, y = 0;
        for (Location loc : initialLocations) {
            x += loc.getLatitude();
            y += loc.getLongitude();
        }
        x /= 3;
        y /= 3;

        centroidLocation = new Location("");
        centroidLocation.setLatitude(x);
        centroidLocation.setLongitude(y);
    }

    private double calculateThreshold(double currentSpeed) {
        // 计算阈值X1
        return currentSpeed * 1.2 * (skippedUpdates + 1) + GPS_ERROR_MARGIN;
    }

    private void addSpeedToHistory(double speed) {
        if (speedHistory.size() >= SPEED_HISTORY_SIZE) {
            speedHistory.poll();
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
