package com.example.catondetect.logPrinter;

/**
 * @author by qiuchen
 * @date 21-4-27
 */
public interface LogPrinterListener {
    void onStartLoop();
    void onEndLoop(long starttime,long endtime,String loginfo,int level);
}
