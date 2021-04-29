package com.example.catondetect.logPrinter;

import android.util.Log;
import android.util.Printer;

/**
 * @author by qiuchen
 * @date 21-4-27
 */
public class LogPrinter implements Printer , UiPerfMonitorConfig{
    private String TAG = LogPrinter.class.getSimpleName();
    private long startTime = 0;
    private LogPrinterListener listener;

    public LogPrinter(LogPrinterListener listener) {
        this.listener = listener;
    }

    @Override
    public void println(String x) {
        if (startTime<=0){
            startTime = System.currentTimeMillis();
            listener.onStartLoop();
        }else {
            long endTime = System.currentTimeMillis();
            execuTime(x,startTime,endTime);
            startTime = 0;
        }
    }

    //根据time执行不同操作
    private void execuTime(String log,long startTime,long endTime){
        int level = 0;
        long time = endTime - startTime;
        Log.d(TAG, "dispatch handler time : " + time);
        if (time > TIME_WARNING_LEVEL_1){
            Log.e(TAG, "TIME_WARNING_LEVEL_1:\r\n"+"println:"+log);
            level = TIME_WARNING_LEVEL_1;
        }else if (time > TIME_WARNING_LEVEL_2){
            Log.e(TAG, "TIME_WARNING_LEVEL_2:\r\n"+"println:"+log);
            level = TIME_WARNING_LEVEL_2;
        }
        listener.onEndLoop(startTime,endTime,log,level);
    }
}
