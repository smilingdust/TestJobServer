package com.example.sgx.testjobserver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.example.sgx.testjobserver.aidl.ProcessService;

/**
 * @author sgx
 */
@SuppressLint("Registered")
public class DaemonService extends Service {
    private static final String TAG = "DaemonService";

    private MyBinder myBinder;
    private MyCon myCon;

    /**
     * 定时唤醒的时间间隔，5 分钟
     */
    public final static long WAKE_INTERVAL = 3 * 60 * 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate()");
        myBinder=new MyBinder();
        if (myCon == null) {
            myCon=new MyCon();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            //定时检查 WorkService 是否在运行，如果不在运行就把它拉起来
            //Android 5.0+ 使用 JobScheduler，效果比 AlarmManager 好
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.e(TAG, "开启 JobService 定时");
                JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobScheduler.cancelAll();
                JobInfo.Builder builder = new JobInfo.Builder(1023, new ComponentName(getPackageName(), ScheduleService.class.getName()));
                builder.setPeriodic(WAKE_INTERVAL);
                builder.setPersisted(true);
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
                int schedule = jobScheduler.schedule(builder.build());
                if (schedule <= 0) {
                    Log.e(TAG, "schedule error！");
                }
            } else {
                //Android 4.4- 使用 AlarmManager
                Log.e(TAG, "开启 AlarmManager 定时");
                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent alarmIntent = new Intent(getApplication(), DaemonService.class);
                PendingIntent pendingIntent = PendingIntent.getService(this, 1023, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.cancel(pendingIntent);
                am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + WAKE_INTERVAL, WAKE_INTERVAL, pendingIntent);
                if (myCon == null) {
                    myCon=new MyCon();
                }
                DaemonService.this.bindService(new Intent(this,LocalService.class),myCon, Context.BIND_IMPORTANT);
            }
        } catch (Exception e) {
            Log.e(TAG, "e:" + e);
        }

        //简单守护开机广播
        getPackageManager().setComponentEnabledSetting(
                new ComponentName(getPackageName(), DaemonService.class.getName()),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
        Intent intent = new Intent(this, DaemonService.class);
        startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

      class MyBinder extends ProcessService.Stub{

          @Override
          public String getProcessName() throws RemoteException {
              return "I am DaemonService";
          }
      }

      class MyCon implements ServiceConnection{

          @Override
          public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
              Log.e("Info","与LocalService连接成功"+DaemonService.this.getApplicationInfo().processName);
          }

          @Override
          public void onServiceDisconnected(ComponentName componentName) {
              Toast.makeText(DaemonService.this,"LocalService被杀死",Toast.LENGTH_SHORT).show();
              // 启动LocalService
              DaemonService.this.startService(new Intent(DaemonService.this,LocalService.class));
              //绑定LocalService
              DaemonService.this.bindService(new Intent(DaemonService.this,LocalService.class),myCon, Context.BIND_IMPORTANT);
          }
      }
}
