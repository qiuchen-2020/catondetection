package com.example.catondetect.logPrinter;

import android.os.Environment;

/**
 * @author by qiuchen
 * @date 21-4-27
 */
public interface UiPerfMonitorConfig {
    //time level
    int TIME_WARNING_LEVEL_1 = 500;
    int TIME_WARNING_LEVEL_2 = 1000;
    //log路径
    String LOG_PATH = Environment.getExternalStorageDirectory().getPath() + "/androidtech";

}
