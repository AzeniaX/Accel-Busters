package com.example.accelbusters.ExternalCommunication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Arrays;
/*
public class BeaconScanner {
    private static final String TAG = "BeaconScanner";
    private static final byte[] UID_NAMESPACE = new byte[]{(byte) 0xAC, (byte) 0xCE, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
    private static final byte[] UID_INSTANCE = new byte[]{(byte) 0xAC, (byte) 0xCE, 0x00, 0x00, 0x00, 0x01};

    private final BluetoothLeScanner bluetoothLeScanner;
    private final Context context;
    private final Handler handler;

    public interface BeaconListener {
        void onBeaconDetected(String signalStrength);
    }

    private BeaconListener beaconListener;

    public BeaconScanner(Context context) {
        this.context = context;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothLeScanner = bluetoothAdapter != null ? bluetoothAdapter.getBluetoothLeScanner() : null;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void setBeaconListener(BeaconListener listener) {
        this.beaconListener = listener;
    }

    public void startScanning() {
        if (bluetoothLeScanner == null) {
            Log.e(TAG, "Bluetooth LE scanner not available.");
            return;
        }

        bluetoothLeScanner.startScan(scanCallback);
        Log.d(TAG, "Started scanning for beacons.");
    }

    public void stopScanning() {
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.stopScan(scanCallback);
            Log.d(TAG, "Stopped scanning for beacons.");
        }
    }
    */
/*
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            byte[] scanRecord = result.getScanRecord() != null ? result.getScanRecord().getBytes() : null;

            if (scanRecord != null && matchesBeacon(scanRecord)) {
                int rssi = result.getRssi();
                String signalStrength = rssi + " dBm";
                Log.d(TAG, "Beacon detected: RSSI = " + signalStrength);

                if (beaconListener != null) {
                    handler.post(() -> beaconListener.onBeaconDetected(signalStrength));
                }
            }
        }
    };
*/
/*
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            byte[] scanRecord = result.getScanRecord() != null ? result.getScanRecord().getBytes() : null;

            // 输出扫描到的设备信息
            Log.d(TAG, "Device Name: " + device.getName());
            Log.d(TAG, "Device Address: " + device.getAddress());
            Log.d(TAG, "RSSI: " + result.getRssi() + " dBm");

            if (scanRecord != null) {
                // 打印扫描记录的十六进制数据
                Log.d(TAG, "Scan Record: " + bytesToHex(scanRecord));
            }

            // 只检测符合条件的信标
            if (scanRecord != null && matchesBeacon(scanRecord)) {
                int rssi = result.getRssi();
                String signalStrength = rssi + " dBm";
                Log.d(TAG, "Beacon detected: RSSI = " + signalStrength);

                if (beaconListener != null) {
                    handler.post(() -> beaconListener.onBeaconDetected(signalStrength));
                }
            }
        }
    };

    // 辅助方法，将字节数组转换为十六进制字符串，方便查看扫描记录
    private String bytesToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02X ", b));
        }
        return stringBuilder.toString().trim();
    }

    private boolean matchesBeacon(byte[] scanRecord) {
        // Check for matching UID Namespace and Instance
        for (int i = 0; i < UID_NAMESPACE.length; i++) {
            if (scanRecord[i + 10] != UID_NAMESPACE[i]) {
                return false;
            }
        }

        for (int i = 0; i < UID_INSTANCE.length; i++) {
            if (scanRecord[i + 20] != UID_INSTANCE[i]) {
                return false;
            }
        }

        return true;
    }
}
*/
/*
    public class BeaconScanner {
        private static final String TAG = "BeaconScanner";
        private static final String TARGET_BEACON_ADDRESS = "00:1D:43:9A:01:0A"; // 目标蓝牙地址

        private final BluetoothLeScanner bluetoothLeScanner;
        private final Context context;
        private final Handler handler;

        public interface BeaconListener {
            void onBeaconDetected(String signalStrength);
        }

        private BeaconListener beaconListener;

        public BeaconScanner(Context context) {
            this.context = context;
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            this.bluetoothLeScanner = bluetoothAdapter != null ? bluetoothAdapter.getBluetoothLeScanner() : null;
            this.handler = new Handler(Looper.getMainLooper());
        }

        public void setBeaconListener(BeaconListener listener) {
            this.beaconListener = listener;
        }

        public void startScanning() {
            if (bluetoothLeScanner == null) {
                Log.e(TAG, "Bluetooth LE scanner not available.");
                return;
            }

            bluetoothLeScanner.startScan(scanCallback);
            Log.d(TAG, "Started scanning for beacons.");
        }

        public void stopScanning() {
            if (bluetoothLeScanner != null) {
                bluetoothLeScanner.stopScan(scanCallback);
                Log.d(TAG, "Stopped scanning for beacons.");
            }
        }

        private final ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();
                String deviceAddress = device.getAddress(); // 获取蓝牙设备的地址

                // 判断设备的地址是否与目标地址匹配
                if (TARGET_BEACON_ADDRESS.equals(deviceAddress)) {
                    int rssi = result.getRssi();
                    String signalStrength = rssi + " dBm";
                    Log.d(TAG, "Beacon detected: " + deviceAddress + " RSSI = " + signalStrength);

                    if (beaconListener != null) {
                        handler.post(() -> beaconListener.onBeaconDetected(signalStrength));
                    }
                }
            }
        };
    }
*/

public class BeaconScanner {
    private static final String TAG = "BeaconScanner";
    private static final String TARGET_BEACON_ADDRESS = "00:1D:43:9A:01:0A"; // 目标蓝牙地址
    private static final long TIMEOUT_MS = 3000; // 3秒超时

    private final BluetoothLeScanner bluetoothLeScanner;
    private final Context context;
    private final Handler handler;
    private long lastDetectedTime = 0; // 上次检测到信标的时间

    public interface BeaconListener {
        void onBeaconDetected(String signalStrength);
        void onBeaconTimeout(); // 当超时没有检测到信标时调用
    }

    private BeaconListener beaconListener;

    public BeaconScanner(Context context) {
        this.context = context;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothLeScanner = bluetoothAdapter != null ? bluetoothAdapter.getBluetoothLeScanner() : null;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void setBeaconListener(BeaconListener listener) {
        this.beaconListener = listener;
    }

    public void startScanning() {
        if (bluetoothLeScanner == null) {
            Log.e(TAG, "Bluetooth LE scanner not available.");
            return;
        }

        bluetoothLeScanner.startScan(scanCallback);
        Log.d(TAG, "Started scanning for beacons.");

        // 每隔一段时间检查是否超时
        handler.postDelayed(timeoutRunnable, TIMEOUT_MS);
    }

    public void stopScanning() {
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.stopScan(scanCallback);
            Log.d(TAG, "Stopped scanning for beacons.");
        }

        // 停止超时检测
        handler.removeCallbacks(timeoutRunnable);
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String deviceAddress = device.getAddress(); // 获取蓝牙设备的地址

            // 判断设备的地址是否与目标地址匹配
            if (TARGET_BEACON_ADDRESS.equals(deviceAddress)) {
                int rssi = result.getRssi();
                String signalStrength = rssi + " dBm";
                Log.d(TAG, "Beacon detected: " + deviceAddress + " RSSI = " + signalStrength);

                // 更新检测时间
                lastDetectedTime = System.currentTimeMillis();

                // 调用 beaconListener 并传递信号强度
                if (beaconListener != null) {
                    handler.post(() -> beaconListener.onBeaconDetected(signalStrength));
                }

                // 重启超时计时器
                handler.removeCallbacks(timeoutRunnable);
                handler.postDelayed(timeoutRunnable, TIMEOUT_MS);
            }
        }
    };

    // 超时检测
    private final Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            // 如果在指定时间内没有检测到信标
            if (System.currentTimeMillis() - lastDetectedTime >= TIMEOUT_MS) {
                if (beaconListener != null) {
                    beaconListener.onBeaconTimeout();
                }
            } else {
                // 如果仍在扫描中，再次设置超时检测
                handler.postDelayed(this, TIMEOUT_MS);
            }
        }
    };
}
