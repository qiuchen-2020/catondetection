package com.example.catondetect.sampler;

import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;

/**
 * @author by qiuchen
 * @date 21-4-27
 */
public class StackTraceSampler extends BaseSampler{
    private String TAG = StackTraceSampler.class.getSimpleName();
    private int mPid =-1;
    private ArrayList<StringBuffer> mStackList = new ArrayList<>();


    @Override
    public void start() {
        super.start();
        mStackList.clear();
    }

    @Override
    void doSample() {
        Log.d(TAG, "doSample");
        dumpStackInfo();
    }

    private void dumpStackInfo(){
        StringBuffer sb = new StringBuffer();
        StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();

        for (StackTraceElement s :stackTrace){
            sb.append(s.toString()+"\n");
        }
        synchronized (mStackList){
            mStackList.add(sb);
            Log.d(TAG, "StackInfo : "+sb.toString() );
        }
    }

    public ArrayList<StringBuffer> getStackInfo(){
        return mStackList;
    }
}
