package com.example.accelbusters.sensormanager;

/*
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class AccelerometerManager {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView tvAcceleration;
    private SensorEventListener sensorEventListener;

    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];

    // 第一次低通滤波器的平滑系数（用于分离重力加速度）
    private static final float ALPHA1 = 0.8f;
    // 第二次低通滤波器的平滑系数（用于去除高频噪声）
    private static final float ALPHA2 = 0.2f;


    public AccelerometerManager(SensorManager sensorManager, TextView tvAcceleration) {
        this.sensorManager = sensorManager;
        this.tvAcceleration = tvAcceleration;
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    // 启动加速度传感器监听
    public void startAccelerometerUpdates() {
        if (accelerometer != null) {
            sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                        // 将加速度数据从传感器坐标系转换为地球坐标系
                        gravity[0] = ALPHA1 * gravity[0] + (1 - ALPHA1) * event.values[0];
                        gravity[1] = ALPHA1 * gravity[1] + (1 - ALPHA1) * event.values[1];
                        gravity[2] = ALPHA1 * gravity[2] + (1 - ALPHA1) * event.values[2];

                        linear_acceleration[0] = event.values[0] - gravity[0];
                        linear_acceleration[1] = event.values[1] - gravity[1];
                        linear_acceleration[2] = event.values[2] - gravity[2];

                        // 第二次低通滤波，用于平滑处理后的加速度数据（去除高频噪声）
                        linear_acceleration[0] = ALPHA2 * linear_acceleration[0] + (1 - ALPHA2) * linear_acceleration[0];
                        linear_acceleration[1] = ALPHA2 * linear_acceleration[1] + (1 - ALPHA2) * linear_acceleration[1];
                        linear_acceleration[2] = ALPHA2 * linear_acceleration[2] + (1 - ALPHA2) * linear_acceleration[2];

                        float acceleration = (float) Math.sqrt(linear_acceleration[0] * linear_acceleration[0]
                                + linear_acceleration[1] * linear_acceleration[1]
                                + linear_acceleration[2] * linear_acceleration[2]);
                        tvAcceleration.setText("Acceleration: " + acceleration + " m/s²");
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {}
            };
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    // 停止加速度传感器更新
    public void stopAccelerometerUpdates() {
        if (sensorEventListener != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    public String getAccelerationData() {
        // 返回加速度的字符串表示
        return String.format("%.2f m/s²", Math.sqrt(linear_acceleration[0] * linear_acceleration[0]
                + linear_acceleration[1] * linear_acceleration[1]
                + linear_acceleration[2] * linear_acceleration[2]));
    }

}
*/
/*
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class AccelerometerManager {

    private SensorManager sensorManager;
    private Sensor linearAccelerationSensor;
    private Sensor accelerometer;
    private TextView tvAcceleration;
    private SensorEventListener sensorEventListener;

    private boolean isUsingLinearAccelerationSensor = false;

    private float[] gravity = new float[3];
    private float[] linearAcceleration = new float[3];
    private float[] smoothedAcceleration = new float[3];

    // 低通滤波器的平滑系数
    private static final float ALPHA1 = 0.8f; // 分离重力加速度
    private static final float ALPHA2 = 0.2f; // 平滑加速度数据

    public AccelerometerManager(SensorManager sensorManager, TextView tvAcceleration) {
        this.sensorManager = sensorManager;
        this.tvAcceleration = tvAcceleration;
        this.linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // 检查是否有线性加速度传感器
        isUsingLinearAccelerationSensor = (linearAccelerationSensor != null);
    }

    // 启动传感器监听
    public void startAccelerometerUpdates() {
        if (isUsingLinearAccelerationSensor) {
            // 使用线性加速度传感器
            sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                        // 使用低通滤波器平滑线性加速度数据
                        smoothedAcceleration[0] = ALPHA2 * smoothedAcceleration[0] + (1 - ALPHA2) * event.values[0];
                        smoothedAcceleration[1] = ALPHA2 * smoothedAcceleration[1] + (1 - ALPHA2) * event.values[1];
                        smoothedAcceleration[2] = ALPHA2 * smoothedAcceleration[2] + (1 - ALPHA2) * event.values[2];

                        float acceleration = (float) Math.sqrt(
                                smoothedAcceleration[0] * smoothedAcceleration[0]
                                        + smoothedAcceleration[1] * smoothedAcceleration[1]
                                        + smoothedAcceleration[2] * smoothedAcceleration[2]
                        );

                        // 更新 TextView 显示加速度值
                        tvAcceleration.setText("Acceleration: " + acceleration + " m/s²");
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
            sensorManager.registerListener(sensorEventListener, linearAccelerationSensor, SensorManager.SENSOR_DELAY_UI);
        } else if (accelerometer != null) {
            // 使用普通加速度计并手动分离重力加速度
            sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                        // 分离重力加速度
                        gravity[0] = ALPHA1 * gravity[0] + (1 - ALPHA1) * event.values[0];
                        gravity[1] = ALPHA1 * gravity[1] + (1 - ALPHA1) * event.values[1];
                        gravity[2] = ALPHA1 * gravity[2] + (1 - ALPHA1) * event.values[2];

                        linearAcceleration[0] = event.values[0] - gravity[0];
                        linearAcceleration[1] = event.values[1] - gravity[1];
                        linearAcceleration[2] = event.values[2] - gravity[2];

                        // 平滑线性加速度数据
                        smoothedAcceleration[0] = ALPHA2 * smoothedAcceleration[0] + (1 - ALPHA2) * linearAcceleration[0];
                        smoothedAcceleration[1] = ALPHA2 * smoothedAcceleration[1] + (1 - ALPHA2) * linearAcceleration[1];
                        smoothedAcceleration[2] = ALPHA2 * smoothedAcceleration[2] + (1 - ALPHA2) * linearAcceleration[2];

                        float acceleration = (float) Math.sqrt(
                                smoothedAcceleration[0] * smoothedAcceleration[0]
                                        + smoothedAcceleration[1] * smoothedAcceleration[1]
                                        + smoothedAcceleration[2] * smoothedAcceleration[2]
                        );

                        // 更新 TextView 显示加速度值
                        tvAcceleration.setText("Acceleration: " + acceleration + " m/s²");
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    // 停止传感器更新
    public void stopAccelerometerUpdates() {
        if (sensorEventListener != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    public String getAccelerationData() {
        // 返回加速度的字符串表示
        return String.format("%.2f m/s²", Math.sqrt(smoothedAcceleration[0] * smoothedAcceleration[0]
                + smoothedAcceleration[1] * smoothedAcceleration[1]
                + smoothedAcceleration[2] * smoothedAcceleration[2]));
    }
}
*/


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class AccelerometerManager {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor linearAccelerometer;
    private TextView tvAcceleration;
    private SensorEventListener sensorEventListener;

    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];

    // 第一次低通滤波器的平滑系数（用于分离重力加速度）
    private static final float ALPHA1 = 0.85f;
    // 第二次低通滤波器的平滑系数（用于去除高频噪声）
    private static final float ALPHA2 = 0.25f;

    private boolean useLinearAccelerometer = false;

    public AccelerometerManager(SensorManager sensorManager, TextView tvAcceleration) {
        this.sensorManager = sensorManager;
        this.tvAcceleration = tvAcceleration;
        this.linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.useLinearAccelerometer = (linearAccelerometer != null);
    }

    // 启动加速度传感器监听
    public void startAccelerometerUpdates() {
        if (useLinearAccelerometer && linearAccelerometer != null) {
            sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                        System.arraycopy(event.values, 0, linear_acceleration, 0, event.values.length);
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {}
            };
            sensorManager.registerListener(sensorEventListener, linearAccelerometer, SensorManager.SENSOR_DELAY_UI);
        } else if (accelerometer != null) {
            sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                        gravity[0] = ALPHA1 * gravity[0] + (1 - ALPHA1) * event.values[0];
                        gravity[1] = ALPHA1 * gravity[1] + (1 - ALPHA1) * event.values[1];
                        gravity[2] = ALPHA1 * gravity[2] + (1 - ALPHA1) * event.values[2];

                        linear_acceleration[0] = event.values[0] - gravity[0];
                        linear_acceleration[1] = event.values[1] - gravity[1];
                        linear_acceleration[2] = event.values[2] - gravity[2];

                        linear_acceleration[0] = ALPHA2 * linear_acceleration[0] + (1 - ALPHA2) * linear_acceleration[0];
                        linear_acceleration[1] = ALPHA2 * linear_acceleration[1] + (1 - ALPHA2) * linear_acceleration[1];
                        linear_acceleration[2] = ALPHA2 * linear_acceleration[2] + (1 - ALPHA2) * linear_acceleration[2];
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {}
            };
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    // 停止加速度传感器更新
    public void stopAccelerometerUpdates() {
        if (sensorEventListener != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    public String getAccelerationData() {
        // 总加速度
        float totalAcceleration = (float) Math.sqrt(
                linear_acceleration[0] * linear_acceleration[0] +
                        linear_acceleration[1] * linear_acceleration[1] +
                        linear_acceleration[2] * linear_acceleration[2]
        );

        // 竖直分量（假设 Z 轴为竖直方向）
        float verticalComponent = linear_acceleration[2];

        // 水平面内分量
        float horizontalComponent = (float) Math.sqrt(
                linear_acceleration[0] * linear_acceleration[0] +
                        linear_acceleration[1] * linear_acceleration[1]
        );

        // 水平面内方向（角度，0°-360°）
        float horizontalDirection = (float) Math.toDegrees(Math.atan2(linear_acceleration[1], linear_acceleration[0]));
        if (horizontalDirection < 0) {
            horizontalDirection += 360;
        }

        // 返回格式化的字符串
        return String.format(
                "%.2f\n%.2f\n%.2f\n%.2f",
                totalAcceleration, verticalComponent, horizontalComponent, horizontalDirection
        );
    }
}
