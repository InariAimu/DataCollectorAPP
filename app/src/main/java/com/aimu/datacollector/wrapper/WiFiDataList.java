package com.aimu.datacollector.wrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * WiFi数据的封装器
 * Created by Itsuka Kotori on 2017/4/21.
 */
public class WiFiDataList
{
    /**
     * WiFi信息数组
     */
    public ArrayList<WiFiData> wiFiDataList;
    /**
     * 本次WiFI扫描的时间
     */
    public long time;

    /**
     * 构造函数
     */
    public WiFiDataList()
    {
        this.wiFiDataList = new ArrayList<>();
    }

    /**
     * 将WiFi信息数组转换成JSON数组
     *
     * @return JSON数组
     */
    public JSONArray ToJSONArray()
    {
        JSONArray ja = new JSONArray();
        for (WiFiData wd : wiFiDataList)
        {
            ja.put(wd.ToJSON());
        }
        return ja;
    }

    /**
     * 将所有数据转换成JSON输出
     *
     * @return JSON
     */
    public JSONObject ToJSON()
    {
        JSONObject jo = new JSONObject();
        try
        {
            jo.put("time", time);
            jo.put("data", this.ToJSONArray());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jo;
    }
}
