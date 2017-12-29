package com.example.sgx.testjobserver;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
/**
 * @author sgx
 */
@SuppressLint("Registered")
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScheduleService extends JobService {
    private static final String TAG = "ScheduleService";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e(TAG, "onStartJob(): params = [" + params + "]");
        if(!Tools.getInstance().isWorked("com.example.sgx.testjobserver.DaemonService",this)
                ||!Tools.getInstance().isWorked("com.example.sgx.testjobserver.LocalService",this)){
            Intent intent1 = new Intent(this, DaemonService.class);
            startService(intent1);
            Intent intent2 = new Intent(this, LocalService.class);
            startService(intent2);
        }
        else{
            Log.e("info", "服务已经启动了！！");
        }
        jobFinished(params, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(TAG, "onStopJob(): params = [" + params + "]");
        return false;
    }
}
