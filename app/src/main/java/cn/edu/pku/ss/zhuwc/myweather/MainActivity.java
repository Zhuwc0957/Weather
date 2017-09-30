package cn.edu.pku.ss.zhuwc.myweather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import util.NetUtil;

/**
 * Created by ZhuWC on 2017/9/21.
 */

public class MainActivity extends Activity implements View.OnClickListener{
    private ImageView mUpdateBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
//        Button bt;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
//        bt=(Button)this.findViewById(R.id.btn);
/*        bt.setOnClickListener(new View.OnClickListener() {
            int i = 0;
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(),"你点击了"+(++i)+"次", Toast.LENGTH_LONG);//提示被点击了

                toast.show();

            }

        });*/
        mUpdateBtn=(ImageView)findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this,"网络OK！", Toast.LENGTH_LONG).show();
        }else {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onClick(View view)
    {
        if(view.getId()==R.id.title_update_btn){
            SharedPreferences sharedPreferences =getSharedPreferences("config",MODE_PRIVATE);
            String cityCode=sharedPreferences.getString("main_city_code","101010100");
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
    }
    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con=null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection)url.openConnection(
                    );
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str=reader.readLine()) != null){
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr=response.toString();
                    Log.d("myWeather", responseStr);
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
}