package com.example.catondetect.sampler;

import android.util.Log;

import com.example.catondetect.bean.CpuInfo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author by qiuchen
 * @date 21-4-28
 */
public class CpuInfoSampler extends BaseSampler{
    private final String TAG = CpuInfoSampler.class.getSimpleName();
    private int mPid = -1;
    private ArrayList<CpuInfo> cpuInfos = new ArrayList<>();


    private long mUserPre = 0;
    private long mSystemPre = 0;
    private long mIdlePre = 0;
    private long mIoWaitPre = 0;
    private long mTotalPre = 0;
    private long mAppCpuTimePre = 0;


    @Override
    public void start() {
        super.start();
        mUserPre = 0;
        mSystemPre = 0;
        mIdlePre = 0;
        mIoWaitPre = 0;
        mTotalPre = 0;
        mAppCpuTimePre = 0;
    }

    @Override
    void doSample() {
        Log.d(TAG, "doSample" );
        dumpCpuInfo();
    }

    public ArrayList<CpuInfo> getCpuInfo(){
        return cpuInfos;
    }

    public void cleanCache(){
        cpuInfos.clear();
    }

    /**
     * 获取cpu信息
     */
    private void dumpCpuInfo(){
        BufferedReader cpuReader = null;
        BufferedReader pidReader = null;

        try {
            //获取cpu信息
            cpuReader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")),1024);
            String cpuRate = cpuReader.readLine();
            if (cpuRate == null){
                cpuRate = "";
            }
            if (mPid<0){
                mPid = android.os.Process.myPid();
            }
            //获取app进程cpu信息
            pidReader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/"+mPid+"/stat")),1024);
            String pidCpuRate = pidReader.readLine();
            if (pidCpuRate == null){
                pidCpuRate = "";
            }
            parseCpuRate(cpuRate,pidCpuRate);
        } catch (Throwable e) {
            Log.d(TAG, "doSampler:"+e);
        }finally {
                try {
                    if (cpuReader!=null){
                        cpuReader.close();
                    }
                    if (pidReader!=null){
                        pidReader.close();
                    }
                } catch (IOException e) {
                    Log.d(TAG, "doSampler:"+e);
                }
        }
    }

    /**
     * 处理cpu信息
     */
    private void parseCpuRate(String cpuRate , String pidCpuRate){
        String[] cpuInfoArray = cpuRate.split(" ");
        if (cpuInfoArray.length<9){
            return;
        }

        long user_time = Long.parseLong(cpuInfoArray[2]);
        long nice_time = Long.parseLong(cpuInfoArray[3]);
        long system_time = Long.parseLong(cpuInfoArray[4]);
        long idle_time = Long.parseLong(cpuInfoArray[5]);
        long ioWait_time = Long.parseLong(cpuInfoArray[6]);
        long total_time = user_time + nice_time + system_time + idle_time + ioWait_time + Long.parseLong(cpuInfoArray[7]) + Long.parseLong(cpuInfoArray[8]);

        String[] pidCpuInfo = pidCpuRate.split(" ");
        if (pidCpuInfo.length<17){
            return;
        }
        long appCpu_time = Long.parseLong(pidCpuInfo[13])+Long.parseLong(pidCpuInfo[14])+Long.parseLong(pidCpuInfo[15])+Long.parseLong(pidCpuInfo[16]);

        if (mAppCpuTimePre > 0){
            CpuInfo cpuInfo = new CpuInfo(System.currentTimeMillis());

            long idleTime = idle_time - mIdlePre;
            long totalTime = total_time - mTotalPre;

            cpuInfo.mCpuRate = (totalTime - idleTime)*100L/totalTime;
            cpuInfo.mAppRate = (appCpu_time - mAppCpuTimePre)*100L/totalTime;
            cpuInfo.mSystemRate = (system_time - mSystemPre)*100L/totalTime;
            cpuInfo.mUserRate = (user_time - mUserPre)*100L/totalTime;
            cpuInfo.mIoWait = (ioWait_time - mIoWaitPre)*100L/totalTime;

            synchronized (cpuInfos){
                cpuInfos.add(cpuInfo);
                Log.d(TAG, "cpu info :"+cpuInfo.toString());
            }

            mUserPre = user_time;
            mSystemPre = system_time;
            mIdlePre = idle_time;
            mIoWaitPre = ioWait_time;
            mTotalPre = total_time;
            mAppCpuTimePre = appCpu_time;
        }

    }
}
