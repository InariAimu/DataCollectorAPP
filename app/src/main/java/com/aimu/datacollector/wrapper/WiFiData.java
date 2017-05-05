package com.aimu.datacollector.wrapper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 一个WiFi AP的数据
 * Created by Itsuka Kotori on 2017/4/21.
 */
public class WiFiData
{
    /**
     * SSID
     */
    public String SSID;
    /**
     * BSSID
     */
    public String BSSID;
    /**
     * 信号强度（dBm）
     */
    public int strength;

    public WiFiData(String SSID, String BSSID, int level)
    {
        this.SSID = SSID;
        this.BSSID = BSSID;
        this.strength = level;
    }

    /**
     * 转换成JSON输出
     *
     * @return JSON
     */
    public JSONObject ToJSON()
    {
        JSONObject jo = new JSONObject();
        try
        {
            jo.put("SSID", SSID);
            jo.put("BSSID", BSSID);
            jo.put("strength", strength);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jo;
    }
}
