package cn.edu.pku.ss.zhuwc.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import cn.edu.pku.ss.zhuwc.myweather.MainActivity;
import cn.edu.pku.ss.zhuwc.myweather.MyReceiver;

/**
 * Created by zhuwc on 2017/12/27.
 */

public class MyService extends Service {
    private Handler handler;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Myservice","Myservice->onBind");
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(MyService.this, "Service starts ", Toast.LENGTH_LONG).show();
        Log.d("Myservice","Myservice->onCreate");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Myservice", "Myservice->onStartCommand");

        handler = new Handler();
       // System.out.println("service started");
        handler.post(new Runnable() {
            @Override
            public void run() {

                    // Toast.makeText(getApplicationContext(), "service is running", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent("cn.edu.pku.ss.zhuwc.myweather.MYRECEIVER");
                    sendBroadcast(i);
                    // onDestroy();
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Myservice","Myservice->onDestroy");
        Toast.makeText(MyService.this, "Service has stoped", Toast.LENGTH_LONG).show();
    }
}
