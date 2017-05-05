package com.aimu.datacollector.wrapper;

import android.graphics.PointF;
import android.net.wifi.WifiInfo;

import java.security.Timestamp;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 收集到的数据的封装器
 * Created by Itsuka Kotori on 2017/4/21.
 */
public class DataWrapper
{
    /**
     * 时间戳
     */
    public long timeStamp;
    /**
     * 地图图片坐标
     */
    public PointF mapPoint;
    /**
     * 楼层
     */
    public int floor;
    /**
     * WiFi信息扫描次数
     */
    public int scanNum;
    /**
     * WiFi信息存储数组
     */
    public ArrayList<WiFiDataList> wifiArray;
    /**
     * 加速度信息存储数组
     */
    public ArrayList<TimeVector3> accArray;
    /**
     * 陀螺仪信息存储数组
     */
    public ArrayList<TimeVector3> gyroArray;
    /**
     * 磁场信息存储数组
     */
    public ArrayList<TimeVector3> magArray;

    /**
     * 构造函数
     */
    public DataWrapper()
    {
        this.wifiArray = new ArrayList<>();
        this.accArray = new ArrayList<>();
        this.gyroArray = new ArrayList<>();
        this.magArray = new ArrayList<>();
    }

    /**
     * 将当前存储的数据格式化成JSON
     * @return JSON数据
     */
    public JSONObject ToJSON()
    {
        JSONObject jo = new JSONObject();

        try
        {
            jo.put("time_stamp", timeStamp);
            jo.put("map_x", mapPoint.x);
            jo.put("map_y", mapPoint.y);
            jo.put("floor_index", floor);

            scanNum = wifiArray.size();
            jo.put("scan_num", scanNum);

            JSONArray jaw = new JSONArray();
            for (WiFiDataList wdl : wifiArray)
            {
                jaw.put(wdl.ToJSON());
            }
            jo.put("wifi_array", jaw);

            jo.put("acc_array", getJSONArray(accArray));
            jo.put("gyro_array", getJSONArray(gyroArray));
            jo.put("mag_array", getJSONArray(magArray));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jo;
    }

    /**
     * 将数组数据格式化为JSON数组
     * @param al 数组
     * @return JSON
     */
    JSONArray getJSONArray(ArrayList<TimeVector3> al)
    {
        JSONArray ja = new JSONArray();
        for (TimeVector3 tv : al)
        {
            ja.put(tv.ToJSON());
        }
        return ja;
    }
}
