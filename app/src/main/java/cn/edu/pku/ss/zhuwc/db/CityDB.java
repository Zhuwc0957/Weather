package cn.edu.pku.ss.zhuwc.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.ss.zhuwc.bean.City;

/**
 * Created by ZhuWC on 2017/10/18.
 */

public class CityDB {
    public static final String CITY_DB_NAME = "city.db";
    private static final String CITY_TABLE_NAME = "city";
    private SQLiteDatabase db;
    public CityDB(Context context, String path) {
        db = context.openOrCreateDatabase(path, Context.MODE_PRIVATE, null);
    }
    public List<City> getAllCity() {//获取所有城市
        List<City> list = new ArrayList<City>();
        Cursor c = db.rawQuery("SELECT * from " + CITY_TABLE_NAME, null);
        while (c.moveToNext()) {
            String province = c.getString(c.getColumnIndex("province"));
            String city = c.getString(c.getColumnIndex("city"));
            String number = c.getString(c.getColumnIndex("number"));
            String allPY = c.getString(c.getColumnIndex("allpy"));
            String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
            String firstPY = c.getString(c.getColumnIndex("firstpy"));
            City item = new City(province, city, number, firstPY, allPY,allFirstPY);
            list.add(item);
        }
        return list;
    }

    public List<City>  selectCity(String cmd)//条件查询数据库获取城市
    {
        List<City> list =new ArrayList<City>();
        //Cursor c = db.rawQuery("SELECT * from " + CITY_TABLE_NAME, null);
        Cursor c = db.rawQuery("SELECT * from " + CITY_TABLE_NAME+" where city like '%"+cmd+"%'",null);
        while (c.moveToNext()) {
            String province = c.getString(c.getColumnIndex("province"));
            String city = c.getString(c.getColumnIndex("city"));
            String number = c.getString(c.getColumnIndex("number"));
            String allPY = c.getString(c.getColumnIndex("allpy"));
            String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
            String firstPY = c.getString(c.getColumnIndex("firstpy"));
            City item = new City(province, city, number, firstPY, allPY,allFirstPY);
            list.add(item);
        }
        return list;
    }
    public String  selectCityCode(String cityname)//根据城市名获取城市代码
    {
        String citycode="";
        //Cursor c = db.rawQuery("SELECT * from " + CITY_TABLE_NAME, null);
        Cursor c = db.rawQuery("SELECT * from " + CITY_TABLE_NAME+" where city='"+cityname+"'",null);
        while (c.moveToNext()) {

            citycode = c.getString(c.getColumnIndex("number"));

        }
        return citycode;
    }
}
