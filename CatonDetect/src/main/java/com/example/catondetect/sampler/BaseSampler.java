package com.example.catondetect.sampler;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 定时采样基类
 * @author by qiuchen
 * @date 21-4-27
 */
public abstract class BaseSampler {
    private final String TAG = BaseSampler.class.getSimpleName();
    private HandlerThread mLogThread = new HandlerThread("log");
    private Handler mHandler;
    private static final long TIME_BLOCK = 200L;
    private AtomicBoolean isSampling = new AtomicBoolean(false);
    public BaseSampler() {
        Log.d(TAG, "BaseSampler init");
    }

    /**
     * 开始采样
     */
    public void start(){
        if(!isSampling.get()){
            Log.d(TAG, "start sampler");
            getControlHandler().removeCallbacks(mRunnable);
            getControlHandler().post(mRunnable);
            isSampling.set(true);
        }
    }

    /**
     * 结束采样
     */
    public void stop(){
        if (isSampling.get()){
            Log.d(TAG, "stop sampler");
            getControlHandler().removeCallbacks(mRunnable);
            isSampling.set(false);
        }
    }

    private Handler getControlHandler(){
        if (null == mHandler){
            mLogThread.start();
            mHandler = new Handler(mLogThread.getLooper());
        }
        return mHandler;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doSample();
            if (isSampling.get()){
                getControlHandler().postDelayed(mRunnable,TIME_BLOCK);
            }
        }
    };

    abstract void doSample();
}
