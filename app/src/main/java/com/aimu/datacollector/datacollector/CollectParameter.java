package com.aimu.datacollector.datacollector;

import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * 数据采集的参数
 * Created by Itsuka Kotori on 2017/4/21.
 */
public class CollectParameter
{
    /**
     * 数据采集的时间长度（秒）
     */
    public int duration = 1;

    /**
     * 楼层编号
     */
    public int floor;

    /**
     * 地图坐标
     */
    public PointF mapLocation;

    /**
     * 是否收集加速度数据
     */
    public boolean hasAccSensor = true;
    /**
     * 加速度传感器的原始类型
     */
    public int accSensorType = Sensor.TYPE_ACCELEROMETER;
    /**
     * 加速度传感器的收集速率
     */
    public int accSensorDelay = SensorManager.SENSOR_DELAY_FASTEST;

    /**
     * 是否收集陀螺仪数据
     */
    public boolean hasGyroSensor = true;
    /**
     * 陀螺仪的原始类型，可选Sensor.TYPE_GYROSCOPE_UNCALIBRATED和Sensor.TYPE_GYROSCOPE
     * 其中由于系统会自动对陀螺仪进行一定的校准，故UNCALIBRATED类型才是原始数据，但要求的最低APILevel为18
     */
    public int gyroSensorType = Sensor.TYPE_GYROSCOPE_UNCALIBRATED;
    /**
     * 陀螺仪的收集速率
     */
    public int gyroSensorDelay = SensorManager.SENSOR_DELAY_FASTEST;

    /**
     * 是否收集磁场传感器数据
     */
    public boolean hasMagSensor = true;
    /**
     * 磁场传感器的原始类型，可选Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED和Sensor.TYPE_MAGNETIC_FIELD
     * 其中由于系统会自动对磁场传感器的原始数据进行一定的校准，故UNCALIBRATED类型才是原始数据，但要求的最低APILevel为18
     */
    public int magSensorType = Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED;
    /**
     * 磁场传感器的收集速率
     */
    public int magSensorDelay = SensorManager.SENSOR_DELAY_FASTEST;

    /**
     * WiFi数据采集的时间间隔（毫秒）
     */
    public int wifiDelay = 200;
}
