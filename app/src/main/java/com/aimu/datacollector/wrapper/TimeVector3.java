package com.aimu.datacollector.wrapper;

import java.security.Timestamp;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 带时间戳的三维向量
 * Created by Itsuka Kotori on 2017/4/21.
 */
public class TimeVector3
{
    /**
     * 时间戳
     */
    public long t;
    /**
     * x
     */
    public double x;
    /**
     * y
     */
    public double y;
    /**
     * z
     */
    public double z;

    public TimeVector3(long t, double x, double y, double z)
    {
        this.t = t;
        this.x = x;
        this.y = y;
        this.z = z;
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
            jo.put("t", t);
            jo.put("x", x);
            jo.put("y", y);
            jo.put("z", z);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jo;
    }

    @Override
    public String toString()
    {
        return t +
                "," + x +
                "," + y +
                "," + z;
    }
}
