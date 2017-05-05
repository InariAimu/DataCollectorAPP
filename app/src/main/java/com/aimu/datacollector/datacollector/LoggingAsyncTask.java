package com.aimu.datacollector.datacollector;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.aimu.datacollector.ui.MainActivity;
import com.aimu.datacollector.wrapper.DataWrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * 异步输出任务
 * Created by Itsuka Kotori on 2017/4/21.
 */
public class LoggingAsyncTask extends AsyncTask<Object, Long, Boolean>
{
    @SuppressWarnings("deprecation")
    @Override
    protected Boolean doInBackground(Object... params)
    {
        DataWrapper dw = (DataWrapper) params[0];
        String line = dw.ToJSON().toString();
        //Log.i("LAT", line);
        File file = (File) params[1];
        Log.i("LAT", file.getAbsolutePath());
        try
        {
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.print(line);
            printWriter.flush();
            printWriter.close();
            Log.i("LAT", "Finished Write");
            //Toast.makeText(MainActivity.instance, "记录完成", Toast.LENGTH_LONG).show();
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }
}
