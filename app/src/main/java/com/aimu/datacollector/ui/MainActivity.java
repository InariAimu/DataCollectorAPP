package com.aimu.datacollector.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aimu.datacollector.R;
import com.aimu.datacollector.datacollector.CollectParameter;
import com.aimu.datacollector.datacollector.SensorHandlerService;
import com.aimu.datacollector.wrapper.WiFiData;
import com.aimu.datacollector.wrapper.WiFiDataList;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    static final String TAG = "MainActivity";

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    public static MainActivity instance;

    private MyHandler myHandler;

    private Spinner spnFloorNumber;
    private ArrayAdapter<String> adapter;

    private ScalableMapView scalableMapView;

    private Button btnStart;

    private TextView textViewPoint;

    /**
     * 楼层的名字
     */
    private String[] floorName = {
            "1",
            "2"
    };
    /**
     * 楼层的图片ID
     */
    private int[] floorImage = {
            R.drawable.floor_1,
            R.drawable.floor_2
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        myHandler = new MyHandler();

        //获取权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }

        //初始化各个控件
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < floorName.length; i++)
        {
            list.add(floorName[i]);
        }
        spnFloorNumber = (Spinner) findViewById(R.id.spnFloorNumber);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFloorNumber.setAdapter(adapter);
        spnFloorNumber.setOnItemSelectedListener(new Spinner.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                // TODO Auto-generated method stub
                arg0.setVisibility(View.VISIBLE);
                //scalableMapView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.floor_1));
                int selectedIndex = arg2;
                scalableMapView.setImageBitmapById(floorImage[selectedIndex]);
            }

            public void onNothingSelected(AdapterView<?> arg0)
            {
                // TODO Auto-generated method stub
                arg0.setVisibility(View.VISIBLE);
            }
        });
        spnFloorNumber.setOnTouchListener(new Spinner.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                // TODO Auto-generated method stub
                /**
                 *
                 */
                return false;
            }
        });
        spnFloorNumber.setOnFocusChangeListener(new Spinner.OnFocusChangeListener()
        {
            public void onFocusChange(View v, boolean hasFocus)
            {
                // TODO Auto-generated method stub

            }
        });


        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(MainActivity.instance, "开始记录", Toast.LENGTH_LONG).show();
                btnStart.setEnabled(false);
                StartLogging();
            }
        });

        textViewPoint = (TextView) findViewById(R.id.textViewPoint);

        scalableMapView = (ScalableMapView) findViewById(R.id.scalableMapView);
        //scalableMapView.setImageDrawable(res.getDrawable(R.drawable.floor_1));
        //setContentView(scalableMapView);

        scalableMapView.tw = textViewPoint;

        //启动服务
        Intent intent = new Intent(MainActivity.this, SensorHandlerService.class);
        this.startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // TODO request success
                }
                break;
        }
    }

    class MyHandler extends Handler
    {
        public MyHandler()
        {
        }

        public MyHandler(Looper L)
        {
            super(L);
        }

        @Override
        public void handleMessage(Message msg)
        {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            Bundle b = msg.getData();
            btnStart.setEnabled(true);
            Toast.makeText(MainActivity.instance, "记录完成", Toast.LENGTH_LONG).show();
        }
    }


    public void StartLogging()
    {
        if (SensorHandlerService.instance() == null)
        {
            Log.i("main", "null");
            Intent intent = new Intent(MainActivity.this, SensorHandlerService.class);
            this.startService(intent);
        }

        CollectParameter cp = new CollectParameter();
        cp.floor = 1;
        cp.mapLocation = new PointF(0, 0);
        SensorHandlerService.instance().Start(cp);

        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Log.i("mainTimer", "stop");
                SensorHandlerService.instance().FinishCollect();
                //Intent intent = new Intent(MainActivity.this, SensorHandlerService.class);
                //SensorHandlerService.instance().stopService(intent);
                myHandler.sendEmptyMessage(0);

            }
        }, cp.duration * 1000);
    }


}
