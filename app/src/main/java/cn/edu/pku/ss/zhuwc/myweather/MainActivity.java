package cn.edu.pku.ss.zhuwc.myweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cn.edu.pku.ss.zhuwc.app.MyApplication;
import cn.edu.pku.ss.zhuwc.bean.TodayWeather;
import util.NetUtil;

import static android.R.attr.delay;

/**
 * Created by ZhuWC on 2017/9/21.
 */

public class MainActivity extends Activity implements View.OnClickListener{
    private static final int UPDATE_TODAY_WEATHER=1;

    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private ImageView mLocationBtn;
    private ImageView mShareBtn;

    private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQualityTv,temperatureTv,climateTv,windTv,city_name_Tv,tempTv;
    private ImageView weatherImg,pmImg;

    private SharedPreferences myPreferences;

    private MyApplication myApplication;

    public LocationClient mLocationClient = null;//定位
    private MyLocationListeners myListener = new MyLocationListeners();
    private String locCityCode;

    public GridView gridView;
    private ListView listView;

    private Handler mHandler=new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);//更新今日天气
                    break;
                case 2:
                    setGridView((ArrayList<TodayWeather>)msg.obj);//更新预报天气
                    break;
                case 3:
                    setZhishuList((ArrayList<Map<String,String>>)msg.obj);//更新天气指数
                    break;
                default:
                    break;
            }
        }
    };

    void initView(){//初始化应用界面
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        tempTv=(TextView)findViewById(R.id.temp);
        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
        tempTv.setText("N/A");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
//        Button bt;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);//指定布局
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService("layout_inflater");//主要是用于加载布局
        gridView=(GridView)findViewById(R.id.grid);
        listView=(ListView)findViewById(R.id.zhishu_list) ;
        locCityCode="";
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        //就是这个方法设置为true，才能获取当前的位置信息

        option.setOpenGps(true); //打开GPRS
        option.setAddrType("all"); //返回的定位结果包含地址信息
        option.setCoorType("bd09ll"); //返回的定位结果是百度经纬度,默认值gcj02
        option.setPriority(LocationClientOption.GpsFirst); // 设置GPS优先
        //option.setScanSpan(5000);   //设置发起定位请求的间隔时间为5000ms
        option.disableCache(false); //禁止启用缓存定位
        mLocationClient.setLocOption(option);
        mLocationClient.start();//获取位置信息



        Log.d("codetest","test1"+locCityCode);
        mUpdateBtn=(ImageView)findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        mLocationBtn=(ImageView)findViewById(R.id.title_location);
        mLocationBtn.setOnClickListener(this);
        mShareBtn=(ImageView)findViewById(R.id.title_share);
        mShareBtn.setOnClickListener(this);
        initView();

        Log.d("codetest","test2"+locCityCode);

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {//有网络时更新最新的天气数据
            myPreferences=getSharedPreferences("users",MODE_PRIVATE);
            String cityCode=myPreferences.getString("cityCode","101010100");
            queryWeatherCode(cityCode);
            Log.d("codetest","my"+ cityCode);
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this,"网络OK！", Toast.LENGTH_LONG).show();
        }else {//无网络是使用myPreferences保存的天气数据
            myPreferences=getSharedPreferences("users",MODE_PRIVATE);
            Log.d("tests","1");
            String weatherdata=myPreferences.getString("weatherdata","");
            Log.d("tests",weatherdata);
            TodayWeather tw=parseXML(weatherdata);
            Log.d("tests","3");
            if(tw!=null)
                updateTodayWeather(tw);
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }

        mCitySelect=(ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
    }
    @Override
    public void onClick(View view)
    {
        if(view.getId()==R.id.title_city_manager)//选择城市按钮调用的方法
        {
            Intent i=new Intent(this,SelectCity.class);
            startActivityForResult(i,1);//在一个主界面(主Activity)通过意图跳转至多个不同子Activity上去，当子模块的代码执行完毕后再次返回主页面，将子activity中得到的数据显示在主界面/完成的数据交给主Activity处理。
        }

        if(view.getId()==R.id.title_update_btn){//更新天气按钮
//            SharedPreferences sharedPreferences =getSharedPreferences("config",MODE_PRIVATE);
//            String cityCode=sharedPreferences.getString("main_city_code","101010100");
            myPreferences=getSharedPreferences("users",MODE_PRIVATE);
            String cityCode=myPreferences.getString("cityCode","101010100");//读取myPreference存储的数据
            Log.d("MyWeather",cityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);
            }else
            {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
            }
        }
        if(view.getId()==R.id.title_location)
        {
           if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){//获取位置信息并更新天气数据
               myApplication=MyApplication.getInstance();
               mLocationClient.start();
               Log.d("codetest",locCityCode);
               String citycode=myApplication.prepareSelectCityCode(locCityCode);
               Log.d("codetest",citycode);
               myPreferences=getSharedPreferences("users",MODE_PRIVATE);
               SharedPreferences.Editor editor=myPreferences.edit();
               editor.putString("cityCode",citycode);
               editor.commit();//更新myPreference的城市代码
               queryWeatherCode(citycode);
           }
           else{
               Toast.makeText(MainActivity.this,"网络挂了,定位失败！",Toast.LENGTH_LONG).show();
           }
        }

        if(view.getId()==R.id.title_share){//分享
            String imgpath=screenshot();//截屏并获取截屏的保存路径
            Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性
            File file = new File(imgpath);//读取截屏
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));// 分享的内容
            intent.setType("image/*");// 分享发送的数据类型
            Intent chooser = Intent.createChooser(intent, "Share screen shot");
            if(intent.resolveActivity(getPackageManager()) != null){
                startActivity(chooser);//启动要分享到应用
            }
        }
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data)//子activity执行结束后回调该函数
    {
        if(requestCode==1&&resultCode==RESULT_OK)//requestCode为1即为selectcity结束后的返回
        {
            String newCityCode=data.getStringExtra("cityCode");
            Log.d("myWeather","选择的城市代码为"+newCityCode);

            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
                Log.d("myWeather","网络ok");
               if(!newCityCode.equals(""))
               {
                   queryWeatherCode(newCityCode);
                   myPreferences=getSharedPreferences("users",MODE_PRIVATE);
                   SharedPreferences.Editor editor=myPreferences.edit();
                   editor.putString("cityCode",newCityCode);
                   editor.commit();
               }
                Log.d("myWeather",newCityCode);
            }else{
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;//拼接获取天气的网址
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {//获取天气数据并转化成字符串
                HttpURLConnection con=null;
                TodayWeather todayWeather =null;
                List<TodayWeather> forcastWeather=null;
                List<Map<String,String>> zhishuList=null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();//获取天气数据流

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str=reader.readLine()) != null){
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr=response.toString();
                    myPreferences=getSharedPreferences("users",MODE_PRIVATE);//更新
                    SharedPreferences.Editor editor=myPreferences.edit();
                    editor.putString("weatherdata",responseStr);
                    editor.commit();
                    Log.d("myWeather", responseStr);
                    todayWeather = parseXML(responseStr);
                    InputStream is = new ByteArrayInputStream(responseStr.getBytes());
                    forcastWeather=forcastXML(is);
                    InputStream zhishu=new ByteArrayInputStream(responseStr.getBytes());
                    zhishuList = getZhishu(zhishu);
                    if (todayWeather != null)
                    {
                        Log.d("myWeather", todayWeather.toString());
                        Message msg =new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj=todayWeather;
                        mHandler.sendMessage(msg);//给mHandler发送消息
                    }
                    if(forcastWeather!=null)
                    {
                        Log.d("forcastWeather", "suss"+forcastWeather.toString());
                        Message msg=new Message();
                        msg.what=2;
                        msg.obj=forcastWeather;
                        mHandler.sendMessage(msg);
                    }
                    if(zhishuList!=null)
                    {
                        Log.d("forcastWeather", "suss"+zhishuList.toString());
                        Message msg=new Message();
                        msg.what=3;
                        msg.obj=zhishuList;
                        mHandler.sendMessage(msg);
                    }

                    //setGridView(forcastWeather);

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    public List<Map<String,String>> getZhishu(InputStream is)//获取天气指数的列表
    {
        List<Map<String ,String>> zhishuList=new ArrayList<Map<String, String>>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            // DocumentBuilder对象
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 获取文档对象
            Document document = builder.parse(is);

            // 获取文档对象的root
            Element root = document.getDocumentElement();

            // 获取zhishus根节点中所有的zhishu节点对象

            NodeList zhishuNodes = root.getElementsByTagName("zhishu");
            // 遍历所有的zhishu节点

            Log.d("tianqizhishu",String.valueOf(zhishuNodes.getLength()));
            for (int i = 0; i < zhishuNodes.getLength(); i++) {
                Map<String,String> maps=new HashMap<String,String>();
                Element zhishuNode = (Element) zhishuNodes.item(i);
                // 获取该节点下面的所有字节点
                NodeList zhishuChildNodes = zhishuNode.getChildNodes();
                Log.d("tianqizhishu",String.valueOf(zhishuChildNodes.getLength()));
                // 遍历weather的字节点
                for (int index = 0; index <zhishuChildNodes.getLength(); index++) {
                    // 获取子节点
                    Node node = zhishuChildNodes.item(index);

                    // 判断node节点是否是元素节点
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        //把节点转换成元素节点
                        Element element = (Element) node;
                        Log.d("tianqizhishu",element.getNodeName());
                        if ("name".equals(element.getNodeName()))
                        {
                            maps.put("name",element.getFirstChild().getNodeValue());
                        }
                        else if ("value".equals(element.getNodeName()))
                        { //判断是否是value节点
                            maps.put("value",element.getFirstChild().getNodeValue());
                        }
                        else  if("detail".equals(element.getNodeName()))
                        {
                            maps.put("detail",element.getFirstChild().getNodeValue());
                        }
                    }

                }

                // 把weather对象加入到集合中
                zhishuList.add(maps);

            }
            //关闭输入流
            //is.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return zhishuList;
    }
    public List<TodayWeather> forcastXML(InputStream is)//获取未来四天天气的列表
    {
        List<TodayWeather> forcastWeather=new ArrayList<TodayWeather>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            // DocumentBuilder对象
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 获取文档对象
            Document document = builder.parse(is);

            // 获取文档对象的root
            Element root = document.getDocumentElement();

            // 获取weathers根节点中所有的weather节点对象

            NodeList weatherNodes = root.getElementsByTagName("weather");

            // 遍历所有的weather节点

            for (int i = 1; i < weatherNodes.getLength(); i++) {
                TodayWeather temp=new TodayWeather();
                // 根据item(index)获取该索引对应的节点对象
                Element weatherNode = (Element) weatherNodes.item(i); // 具体的weather节点


                // 获取该节点下面的所有字节点
                NodeList weatherChildNodes = weatherNode.getChildNodes();

                // 遍历weather的字节点
                for (int index = 0; index < weatherChildNodes.getLength(); index++) {
                    // 获取子节点
                    Node node = weatherChildNodes.item(index);

                    // 判断node节点是否是元素节点
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        //把节点转换成元素节点
                        Element element = (Element) node;
                        if ("date".equals(element.getNodeName()))
                        {
                            temp.setDate(element.getFirstChild().getNodeValue());
                        }
                        else if ("high".equals(element.getNodeName()))
                        { //判断是否是age节点
                            temp.setHigh(element.getFirstChild().getNodeValue().substring(2).trim());
                        }
                        else  if("low".equals(element.getNodeName()))
                        {
                            temp.setLow(element.getFirstChild().getNodeValue().substring(2).trim());
                        }
                    }
                    if(node.getNodeName().equals("day")) {
                        NodeList daynode=node.getChildNodes();
                        for(int j=0;j<daynode.getLength();j++) {
                            Node nodes=daynode.item(j);
                            Element element=(Element) nodes;
                            if(element.getNodeName().equals("type"))
                                temp.setType(element.getFirstChild().getNodeValue());
                            if(element.getNodeName().equals("fengxiang"))
                                temp.setFengxiang(element.getFirstChild().getNodeValue());
                            if(element.getNodeName().equals("fengli"))
                                temp.setFengli(element.getFirstChild().getNodeValue());
                        }
                    }

                }

                // 把weather对象加入到集合中
                forcastWeather.add(temp);

            }
            //关闭输入流
            //is.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return forcastWeather;
    }

    public TodayWeather parseXML(String xmldata)//解析今日天气
    {
        TodayWeather todayWeather = null;

        int fengxiangCount=0;
        int fengliCount =0;
        int dateCount=0;
        int highCount =0;
        int lowCount=0;
        int typeCount =0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp"))
                        {
                            todayWeather= new TodayWeather();

                        }
                        if (todayWeather != null)
                        {
                            TodayWeather temp=new TodayWeather();
                            if (xmlPullParser.getName().equals("city"))
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("updatetime"))
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("shidu"))
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("wendu"))
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("pm25"))
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("quality"))
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("fengxiang")&&fengliCount==0)
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            }
                            else if (xmlPullParser.getName().equals("fengli")&&fengliCount==0)
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            }
                            else if (xmlPullParser.getName().equals("date")&&dateCount==0)
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            }
                            else if (xmlPullParser.getName().equals("high") && highCount == 0)
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            }
                            else if (xmlPullParser.getName().equals("low") && lowCount == 0)
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            }
                            else if (xmlPullParser.getName().equals("type") && typeCount == 0)
                            {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
// 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return todayWeather;
    }

    public void updateTodayWeather(TodayWeather todayWeather){//更新界面
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        myPreferences=getSharedPreferences("users",MODE_PRIVATE);
        SharedPreferences.Editor editor=myPreferences.edit();
        editor.putString("cityname",todayWeather.getCity());
        editor.commit();
        if(todayWeather.getPm25()!=null)
        {
            int pms=Integer.parseInt(todayWeather.getPm25());
            if(pms<=50)
                pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
            else if(pms<=100)
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
            else if(pms<=150)
                pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
            else if(pms<=200)
                pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
            else if(pms<=300)
                pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
            else
                pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
        }
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+ "发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());

        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        Log.d("tianqi",todayWeather.getType());
        switch (todayWeather.getType()){
            case "暴雪":weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);break;
            case "暴雨":weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);break;
            case "大暴雨":weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);break;
            case "大雪":weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);break;
            case "多云":weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);break;
            case "雷阵雨":weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);break;
            case "雷阵雨冰雹":weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);break;
            case "大雨":weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);break;
            case "中雨":weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);break;
            case "晴":weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);break;
            case "沙尘暴":weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);break;
            case "特大暴雨":weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);break;
            case "雾":weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);break;
            case "小雪":weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);break;
            case "阴":weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);break;
            case "小雨":weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);break;
            case "雨夹雪":weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);break;
            case "阵雪":weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);break;
            case "中雪":weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);break;
            case "阵雨":weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);break;
            default:break;
        }
        windTv.setText("风力:"+todayWeather.getFengli());
        tempTv.setText("温度:"+todayWeather.getWendu()+"℃");
        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }

    class MyLocationListeners implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location){
            double lati = location.getLatitude();
            double longa = location.getLongitude();
            //打印出当前位置
            locCityCode=location.getCity();
            locCityCode=locCityCode.replace("市","");
            Log.i("TAGs", locCityCode);
            Log.i("TAGs", "location.getAddrStr()=" + location.getAddrStr());
            //打印出当前城市
            Log.i("TAGs", "location.getCity()=" + location.getCity());
            //返回码
            int i = location.getLocType();
        }

    }

    class Thread1 extends Thread {

        public void run() {

            mLocationClient.start();

        }
    }

    class Thread2 extends Thread {

        public void run() {

            try{
                Thread.sleep(5000);
            }catch (InterruptedException e){}

        }
    }

    class MyThread extends Thread{
        private CountDownLatch cdl;
        public MyThread(CountDownLatch cdl){
            this.cdl=cdl;
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub

            mLocationClient.start();
            cdl.countDown();
        }
    }

    private void setGridView(List<TodayWeather> x) {//设置gridview，绑定adapter

        int size = x.size();

        int length = 100;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = 1050;
        int itemWidth = 250;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        gridView.setLayoutParams(params); // 重点
        gridView.setColumnWidth(itemWidth); // 重点
        gridView.setHorizontalSpacing(5); // 间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size); // 重点

        GridViewAdapter adapter = new GridViewAdapter(getApplicationContext(),x);
        gridView.setAdapter(adapter);
        Log.d("forcastWeathers", "update");
    }

    public class GridViewAdapter extends BaseAdapter {//自定义gridviewadapter
        Context context;
        List<TodayWeather> list;

        public GridViewAdapter(Context _context, List<TodayWeather> _list) {
            this.list = _list;
            this.context = _context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
            TextView types = (TextView) convertView.findViewById(R.id.types);
            TextView temps = (TextView) convertView.findViewById(R.id.temps);
            TextView dates=(TextView) convertView.findViewById(R.id.date);
            TextView fls=(TextView)convertView.findViewById(R.id.fls);
            ImageView imgtype=(ImageView)convertView.findViewById(R.id.TypeImage) ;
            TodayWeather tdw = list.get(position);
            types.setText(tdw.getType());
            temps.setText(tdw.getHigh()+"~"+tdw.getLow());
            dates.setText(tdw.getDate());
            fls.setText("风力:"+tdw.getFengli());
            switch (tdw.getType()){
                case "暴雪":imgtype.setImageResource(R.drawable.biz_plugin_weather_baoxue);break;
                case "暴雨":imgtype.setImageResource(R.drawable.biz_plugin_weather_baoyu);break;
                case "大暴雨":imgtype.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);break;
                case "大雪":imgtype.setImageResource(R.drawable.biz_plugin_weather_daxue);break;
                case "多云":imgtype.setImageResource(R.drawable.biz_plugin_weather_duoyun);break;
                case "雷阵雨":imgtype.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);break;
                case "雷阵雨冰雹":imgtype.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);break;
                case "大雨":imgtype.setImageResource(R.drawable.biz_plugin_weather_dayu);break;
                case "中雨":imgtype.setImageResource(R.drawable.biz_plugin_weather_zhongyu);break;
                case "晴":imgtype.setImageResource(R.drawable.biz_plugin_weather_qing);break;
                case "沙尘暴":imgtype.setImageResource(R.drawable.biz_plugin_weather_shachenbao);break;
                case "特大暴雨":imgtype.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);break;
                case "雾":imgtype.setImageResource(R.drawable.biz_plugin_weather_wu);break;
                case "小雪":imgtype.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);break;
                case "阴":imgtype.setImageResource(R.drawable.biz_plugin_weather_yin);break;
                case "小雨":imgtype.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);break;
                case "雨夹雪":imgtype.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);break;
                case "阵雪":imgtype.setImageResource(R.drawable.biz_plugin_weather_zhenxue);break;
                case "中雪":imgtype.setImageResource(R.drawable.biz_plugin_weather_zhongxue);break;
                case "阵雨":imgtype.setImageResource(R.drawable.biz_plugin_weather_zhenyu);break;
                default:break;
            }
            return convertView;
        }

    }
    public class zhishuListAdapter extends BaseAdapter{//自定义listviewadapter
        Context context;
        List<Map<String,String>> list;

        public zhishuListAdapter(Context _context, List<Map<String,String>> _list) {
            this.list = _list;
            this.context = _context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.zhishu_item, null);
            TextView name=(TextView)convertView.findViewById(R.id.zsname);
            TextView value=(TextView)convertView.findViewById(R.id.zs);
            TextView detail=(TextView)convertView.findViewById(R.id.suggest);
            Map<String,String> temps=list.get(position);
            name.setText(temps.get("name")+"：");
            value.setText(temps.get("value"));
            detail.setText(temps.get("detail"));
            return convertView;
        }

    }

    public void  setZhishuList(List<Map<String,String>> zhishuLists)//天气指数listview绑定adapter
    {
        zhishuListAdapter adapter=new zhishuListAdapter(getApplicationContext(),zhishuLists);
        listView.setAdapter(adapter);
    }

    private String screenshot() {//截屏并返回路径
        // 获取屏幕
        String imagePath="";
        View dView = getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bmp = dView.getDrawingCache();
        if (bmp != null)
        {
            try {
                // 获取内置SD卡路径
                String sdCardPath = Environment.getExternalStorageDirectory().getPath();
                // 图片文件路径
                imagePath = sdCardPath + File.separator + "screenshot.png";
                File file = new File(imagePath);
                FileOutputStream os = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
            } catch (Exception e) {
            }
        }
        return imagePath;
    }
}