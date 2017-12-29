package com.example.sgx.testjobserver;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.sgx.testjobserver.aidl.ProcessService;

/**
 *
 * @author sgx
 * @date 17-12-29
 */

public class LocalService extends Service {
    private static final String TAG = LocalService.class.getSimpleName();

    private MyConnection myConnection;
    private MyBinder myBinder;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"LocalService is OnCreate");
        myBinder=new MyBinder();
        if (myConnection!=null) {
            myConnection=new MyConnection();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (myConnection == null) {
            myConnection=new MyConnection();
        }
        LocalService.this.bindService(new Intent(this,DaemonService.class),myConnection, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
        Intent intent = new Intent(this, LocalService.class);
        startService(intent);
    }

    class MyBinder extends ProcessService.Stub{

        @Override
        public String getProcessName() throws RemoteException {
            return "I am LocalService";
        }
    }

    class MyConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e(TAG,"与DaemonService连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(LocalService.this,"DaemonService被杀死",Toast.LENGTH_SHORT).show();
            // 启动DaemonService
            LocalService.this.startService(new Intent(LocalService.this,DaemonService.class));
            //绑定DaemonService
            if (myConnection == null) {
                myConnection=new MyConnection();
            }
            LocalService.this.bindService(new Intent(LocalService.this,DaemonService.class),myConnection, Context.BIND_IMPORTANT);
        }
    }
}
