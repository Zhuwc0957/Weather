package cn.edu.pku.ss.zhuwc.app;

import android.app.Application;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import cn.edu.pku.ss.zhuwc.bean.City;
import cn.edu.pku.ss.zhuwc.db.CityDB;
import cn.edu.pku.ss.zhuwc.service.MyService;

/**
 * Created by ZhuWC on 2017/10/18.
 */

public class MyApplication extends Application {
    private static final String TAG="MyApp";
    private static MyApplication mApplication;
    private CityDB mCityDB;
    private List<City> mCityList;
    private String weathertype;
    private String wind;
    private String todaydate;
    private String wendu;

    public String getWeathertype() {
        return weathertype;
    }

    public void setWeathertype(String weathertype) {
        this.weathertype = weathertype;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getTodaydate() {
        return todaydate;
    }

    public void setTodaydate(String todaydate) {
        this.todaydate = todaydate;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"MyApplication->Oncreate");
        mApplication = this;
        mCityDB=openCityDB();
        initCityList();
        startService(new Intent(this,MyService.class));
    }

    public static MyApplication getInstance(){
        return mApplication;
    }

    private CityDB openCityDB() {//连接数据库
        String path = "/data" + Environment.getDataDirectory().getAbsolutePath() + File.separator + getPackageName() + File.separator + "databases" + File.separator + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d(TAG,path);
        if (!db.exists()) {
            String pathfolder = "/data" + Environment.getDataDirectory().getAbsolutePath() + File.separator + getPackageName() + File.separator + "databases" + File.separator;
            File dirFirstFolder = new File(pathfolder);
            if(!dirFirstFolder.exists()){
                dirFirstFolder.mkdirs();
                Log.i("MyApp","mkdirs");
            }
            Log.i("MyApp","db is not exists");
            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this, path);
    }

    private void initCityList(){
        mCityList = new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
// TODO Auto-generated method stub
                prepareCityList();
            }
        }).start();
    }

    private boolean prepareCityList() {//查询所有城市
        mCityList = mCityDB.getAllCity();

        int i=0;
        for (City city : mCityList) {
            i++;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
            String cityPy=city.getFirstPY();
            Log.d(TAG,cityCode+":"+cityName+":"+cityPy);
        }
        Log.d(TAG,"i="+i);
        return true;
    }

    public List<City> prepareSelectCityBych(String ch)
    {
        CityDB newDb=openCityDB();
        List<City> mNewCityList=new ArrayList<City>();
        mNewCityList = newDb.selectCityByCharacter(ch);
        int i=0;
        for (City city : mNewCityList) {
            i++;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
            Log.d(TAG,cityCode+":"+cityName);
        }
        Log.d(TAG,"i="+i);
        return mNewCityList;
    }

    public List<City> prepareSelectCityList(String cmd)//根据输入查询城市
    {
        CityDB newDb=openCityDB();
        List<City> mNewCityList=new ArrayList<City>();
        mNewCityList = newDb.selectCity(cmd);
        int i=0;
        for (City city : mNewCityList) {
            i++;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
            Log.d(TAG,cityCode+":"+cityName);
        }
        Log.d(TAG,"i="+i);
        return mNewCityList;
    }

    public String prepareSelectCityCode(String cityname)//根据城市名查询城市代码
    {
        CityDB newDb=openCityDB();
        return newDb.selectCityCode(cityname);
    }


    public List<City> getCityList() {
        return mCityList;
    }

}
