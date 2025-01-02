package com.example.accelbusters;
/*
import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.hardware.SensorManager;
import android.content.pm.PackageManager;

import com.example.accelbusters.sensormanager.GpsManager;
import com.example.accelbusters.sensormanager.AccelerometerManager;

public class MainActivity extends AppCompatActivity {

    private TextView tvLocation, tvSpeed, tvAcceleration;
    private GpsManager gpsManager;
    private AccelerometerManager accelerometerManager;
    private Handler handler;
    private Runnable updateUIRunnable;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 UI 组件
        tvLocation = findViewById(R.id.tvLocation);
        tvSpeed = findViewById(R.id.tvSpeed);
        tvAcceleration = findViewById(R.id.tvAcceleration);


        // 初始化 GpsManager
        gpsManager = new GpsManager(this, tvLocation, tvSpeed);

        // 初始化 AccelerometerManager
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerManager = new AccelerometerManager(sensorManager, tvAcceleration);

        // 请求位置权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // 启动 GPS 更新
            gpsManager.startLocationUpdates(this);
        }

        // 启动加速度传感器更新
        accelerometerManager.startAccelerometerUpdates();

        // 定时更新 UI
        handler = new Handler(Looper.getMainLooper());
        updateUIRunnable = new Runnable() {
            @Override
            public void run() {
                updateUI();
                handler.postDelayed(this, 1000);  // 每秒更新一次
            }
        };
        handler.post(updateUIRunnable);
    }

    private void updateUI() {
        // 更新 UI
        tvLocation.setText("Location: " + gpsManager.getLocationData());
        tvSpeed.setText("Speed: " + gpsManager.getSpeed());
        tvAcceleration.setText("Acceleration: " + accelerometerManager.getAccelerationData());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限请求被批准，启动 GPS 更新
                gpsManager.startLocationUpdates(this);
            } else {
                Toast.makeText(this, "位置权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止 GPS 更新
        gpsManager.stopLocationUpdates(this);
        // 停止加速度传感器更新
        accelerometerManager.stopAccelerometerUpdates();
    }
}
*/


import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.hardware.SensorManager;
import android.content.pm.PackageManager;

import com.example.accelbusters.sensormanager.GpsManager;
import com.example.accelbusters.sensormanager.AccelerometerManager;
import com.example.accelbusters.ExternalCommunication.BeaconScanner;

public class MainActivity extends AppCompatActivity {

    private TextView tvLocation, tvSpeed, tvAcceleration, tvBeaconInfo;
    private GpsManager gpsManager;
    private AccelerometerManager accelerometerManager;
    private BeaconScanner beaconScanner;
    private Handler handler;
    private Runnable updateUIRunnable;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 2;
    private static final int BLUETOOTH_CONNECT_PERMISSION_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 UI 组件
        tvLocation = findViewById(R.id.tvLocation);
        tvSpeed = findViewById(R.id.tvSpeed);
        tvAcceleration = findViewById(R.id.tvAcceleration);
        tvBeaconInfo = findViewById(R.id.tvBeaconInfo);

        // 初始化 GpsManager
        gpsManager = new GpsManager(this, tvLocation, tvSpeed);

        // 初始化 AccelerometerManager
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerManager = new AccelerometerManager(sensorManager, tvAcceleration);

        // 请求位置权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // 启动 GPS 更新
            gpsManager.startLocationUpdates(this);
        }

        // 请求蓝牙权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, BLUETOOTH_PERMISSION_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT_PERMISSION_REQUEST_CODE);
        }
        // 初始化BeaconScanner
        beaconScanner = new BeaconScanner(this);
        beaconScanner.setBeaconListener(new BeaconScanner.BeaconListener() {
            @Override
            public void onBeaconDetected(String signalStrength) {
                // 更新 UI 显示信标信息
                tvBeaconInfo.setText("Beacon detected! Signal strength: " + signalStrength);
            }

            @Override
            public void onBeaconTimeout() {
                // 超时后更新 UI 显示
                tvBeaconInfo.setText("No beacon detected in the last 3 seconds");
            }
        });

        // BeaconScanner开始扫描
        beaconScanner.startScanning();

        // 启动加速度传感器更新
        accelerometerManager.startAccelerometerUpdates();

        // 定时更新 UI
        handler = new Handler(Looper.getMainLooper());
        updateUIRunnable = new Runnable() {
            @Override
            public void run() {
                updateUI();
                handler.postDelayed(this, 1000);  // 每秒更新一次
            }
        };
        handler.post(updateUIRunnable);
    }

    private void updateUI() {
        // 更新 UI
        tvLocation.setText("Location: " + gpsManager.getLocationData());
        tvSpeed.setText("Speed: " + gpsManager.getSpeed());
        // 修改加速度的显示格式为四行
        String[] accelerationData = accelerometerManager.getAccelerationData().split("\\n");
        tvAcceleration.setText(
                "Total Acceleration: " + accelerationData[0] + " m/s²\n" +
                        "Vertical Component: " + accelerationData[1] + " m/s²\n" +
                        "Horizontal Component: " + accelerationData[2] + " m/s²\n" +
                        "Horizontal Direction: " + accelerationData[3] + "°"
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限请求被批准，启动 GPS 更新
                gpsManager.startLocationUpdates(this);
            } else {
                Toast.makeText(this, "位置权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE || requestCode == BLUETOOTH_CONNECT_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Bluetooth Permissions granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止 GPS 更新
        gpsManager.stopLocationUpdates(this);
        // 停止加速度传感器更新
        accelerometerManager.stopAccelerometerUpdates();
        // 停止蓝牙信标扫描
        beaconScanner.stopScanning();
    }
}