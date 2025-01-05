package com.example.accelbusters.utiles;

import static com.example.accelbusters.utiles.PositionUtil.doubleScalar;
import static com.example.accelbusters.utiles.PositionUtil.getDistance;

import android.location.Location;
import android.util.Log;

public class LevelAssessment {
    private static final String TAG = "LevelAssessment";
    private static final int w1 = 1;
    private static final int w2 = 1;
    private static final double g = 9.8;
    private double scalar_velocity;
    private double limited_velocity;
    private double tangential_acceleration;
    private double delta_time;

    public void setDeltaTime(Location a, Location b, double v_a, double v_b, double a_a, double a_b,
                             double a_bear_a, double a_bear_b) {
        // 1. 提取物体的初始位置
        double lat_a = a.getLatitude();
        double lon_a = a.getLongitude();
        double lat_b = b.getLatitude();
        double lon_b = b.getLongitude();

        long time_a = a.getTime(); // Location a 的时间戳 (ms)
        long time_b = b.getTime(); // Location b 的时间戳 (ms)
        double timeDifference = (time_b - time_a) / 1000.0; // 转换为秒

// 转换速度方向到弧度制
        double bear_a_rad = Math.toRadians(a.getBearing());
        double bear_b_rad = Math.toRadians(b.getBearing());

        // 解析加速度方向到弧度制
        double a_bear_a_rad = Math.toRadians(a_bear_a);
        double a_bear_b_rad = Math.toRadians(a_bear_b);

        // 2. 计算初始速度的分量 (v_x, v_y)
        double v_ax = doubleScalar(v_a * Math.sin(bear_a_rad));
        double v_ay = doubleScalar(v_a * Math.cos(bear_a_rad));
        double v_bx = doubleScalar(v_b * Math.sin(bear_b_rad));
        double v_by = doubleScalar(v_b * Math.cos(bear_b_rad));

        // 3. 计算加速度的分量 (a_x, a_y)
        double a_ax = doubleScalar(a_a * Math.sin(a_bear_a_rad));
        double a_ay = doubleScalar(a_a * Math.cos(a_bear_a_rad));
        double a_bx = doubleScalar(a_b * Math.sin(a_bear_b_rad));
        double a_by = doubleScalar(a_b * Math.cos(a_bear_b_rad));

        // 4. 将地理坐标转换为米制坐标
        double x_a = 0;
        double y_a = getDistance(lon_a,lat_a, lon_a, lat_b);
        double x_b = getDistance(lon_b,lat_b, lon_a, lat_b);
        double y_b = 0;

        // 5. 时间对齐处理
        if (timeDifference > 0) {
            // B 比 A 晚，计算 B 在 A 时间点的位置
            x_b -= v_bx * timeDifference + 0.5 * a_bx * timeDifference * timeDifference;
            y_b -= v_by * timeDifference + 0.5 * a_by * timeDifference * timeDifference;
        } else if (timeDifference < 0) {
            // A 比 B 晚，计算 A 在 B 时间点的位置
            timeDifference = -timeDifference;
            x_a -= v_ax * timeDifference + 0.5 * a_ax * timeDifference * timeDifference;
            y_a -= v_ay * timeDifference + 0.5 * a_ay * timeDifference * timeDifference;
        }


        // 5. 使用数值方法查找两物体轨迹是否有交点
        double deltaTime = -1; // 用于存储交点时间差
        double minTime = 0;
        double maxTime = 10; // 假设最大预测时间为10秒
        double epsilon = 1e-2; // 时间步长

        double closestDistance = 100;
        double collisionTimeA = -1;
        double collisionTimeB = -1;

        for (double t_a = minTime; t_a <= maxTime; t_a += epsilon) {
            // 计算物体A在时间t_a的坐标
            double pos_ax = x_a + v_ax * t_a + 0.5 * a_ax * t_a * t_a;
            double pos_ay = y_a + v_ay * t_a + 0.5 * a_ay * t_a * t_a;

            for (double t_b = minTime; t_b <= maxTime; t_b += epsilon) {
                // 计算物体B在时间t_b的坐标
                double pos_bx = x_b + v_bx * t_b + 0.5 * a_bx * t_b * t_b;
                double pos_by = y_b + v_by * t_b + 0.5 * a_by * t_b * t_b;

                // 判断两物体是否接近 (交点条件: 距离小于某个阈值)
                double distance = Math.sqrt(Math.pow(pos_ax - pos_bx, 2) + Math.pow(pos_ay - pos_by, 2));
                if (distance < closestDistance) {
                    closestDistance = distance;
                    collisionTimeA = t_a;
                    collisionTimeB = t_b;
                }

                if (distance < 1.0) { // 距离小于1米视为交点
                    break;
                }
            }
            if (closestDistance < 1.0) { // 距离小于1米视为交点
                break;
            }
        }

        if (closestDistance < 1.0) {
            Log.d(TAG, "交点时间差: " + Math.abs(collisionTimeA - collisionTimeB) + " 秒");
            deltaTime = Math.abs(collisionTimeA - collisionTimeB);
        } else {
            Log.d(TAG, "两物体的轨迹没有交点");
        }
    }
    public int getLevel() {
        int level=1;
        double pr_v,pr_a,pr;
        pr_v=(scalar_velocity/limited_velocity)*(0.219+0.356*(scalar_velocity/limited_velocity))-0.015;
        pr_a=(tangential_acceleration/g)*(0.334+0.996*(tangential_acceleration/g))-0.005;
        pr= (w1*pr_v+w2*pr_a)/(w1+w2);
        if(pr<0.4)
            level=1;
        if (pr>=0.4&&pr<0.6||scalar_velocity>limited_velocity||delta_time<=7&&delta_time>4)
            level=2;
        if (pr>=0.6&&pr<0.8||delta_time<=4&&delta_time>2)
            level=3;
        if(pr>=0.8||delta_time<=2)
            level=4;
        return level;
    }
}
