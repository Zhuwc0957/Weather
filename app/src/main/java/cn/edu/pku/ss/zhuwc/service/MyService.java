package cn.edu.pku.ss.zhuwc.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by zhuwc on 2017/12/27.
 */

public class MyService extends Service {
    @Nullable

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Myservice", "Myservice->onBind");
        return null;
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d("MyService","MyService->onCreate");
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        Log.d("MyService","MyService->onStartCommand");
        return super.onStartCommand(intent,flags,startId);
    }


}
