package com.example.catondetect.logPrinter;

import android.content.IntentFilter;
import android.os.Looper;
import android.util.Log;

import com.example.catondetect.bean.CpuInfo;
import com.example.catondetect.sampler.CpuInfoSampler;
import com.example.catondetect.sampler.StackTraceSampler;

/**
 * @author by qiuchen
 * @date 21-4-27
 */
public class UiPerfMonitor implements UiPerfMonitorConfig , LogPrinterListener{
    private static UiPerfMonitor monitor;
    private String TAG = UiPerfMonitor.class.getSimpleName();
    private LogPrinter mPrinter;
    private StackTraceSampler stackTraceSampler;
    private CpuInfoSampler cpuInfoSampler;

    public UiPerfMonitor() {
        stackTraceSampler = new StackTraceSampler();
        cpuInfoSampler = new CpuInfoSampler();
        mPrinter = new LogPrinter(this);
    }
    public static UiPerfMonitor getInstance(){
        if (monitor==null){
            synchronized (UiPerfMonitor.class){
                monitor = new UiPerfMonitor();
            }
        }
        return monitor;
    }

    public void startMonitor() {
        Looper.getMainLooper().setMessageLogging(mPrinter);
    }

    public void stopMonitor() {
        Looper.getMainLooper().setMessageLogging(null);
        stackTraceSampler.stop();
        cpuInfoSampler.stop();
    }

    @Override
    public void onStartLoop() {
        stackTraceSampler.start();
        cpuInfoSampler.start();
    }

    @Override
    public void onEndLoop(long starttime, long endtime, String loginfo, int level) {
        stackTraceSampler.stop();
        cpuInfoSampler.stop();
        Log.d(TAG, "onEndLoop");
        switch (level){
            case TIME_WARNING_LEVEL_1 :
                Log.d(TAG, "onEndLoop TIME_WARNING_LEVEL_1 ");
                if (stackTraceSampler.getStackInfo().size()>0){
                    StringBuffer sb = new StringBuffer("startTime:");
                    sb.append(starttime);
                    sb.append(" endTime:");
                    sb.append(endtime);
                    sb.append(" handleTime:");
                    sb.append(endtime-starttime);
                    for (StringBuffer info : stackTraceSampler.getStackInfo()) {
                        sb.append("\r\n");
                        sb.append(info.toString());
                    }
                }
                if (cpuInfoSampler.getCpuInfo().size()>0){
                    StringBuffer sb = new StringBuffer("startTime:");
                    sb.append(starttime);
                    sb.append(" endTime:");
                    sb.append(endtime);
                    sb.append(" handleTime:");
                    sb.append(endtime-starttime);
                    for (CpuInfo info : cpuInfoSampler.getCpuInfo()) {
                        sb.append("\r\n");
                        sb.append(info.toString());
                    }
                }
                break;
            case TIME_WARNING_LEVEL_2:
                break;
        }
    }


}
