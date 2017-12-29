package com.example.sgx.testjobserver;

import android.app.ActivityManager;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by sgx on 17-12-28.
 * 提供工具
 */

public class Tools {

    public static Tools mTools;

    public static Tools getInstance(){
        synchronized (Tools.class){
            if (mTools == null) {
                mTools=new Tools();
            }
        }
        return mTools;
    }


    public boolean isWorked(String className,Context context) {
        ActivityManager myManager = (ActivityManager) context.getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        assert myManager != null;
        ArrayList<ActivityManager.RunningServiceInfo> runningService =
                (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            String cName = runningService.get(i).service.getClassName();
            if (cName.equals(className)) {
                return true;
            }
        }
        return false;
    }
}
