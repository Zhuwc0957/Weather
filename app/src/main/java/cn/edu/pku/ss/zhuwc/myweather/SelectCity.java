package cn.edu.pku.ss.zhuwc.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baidu.location.BDLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.edu.pku.ss.zhuwc.app.MyApplication;
import cn.edu.pku.ss.zhuwc.bean.City;

public class SelectCity extends AppCompatActivity implements View.OnClickListener{

    private ImageView mBackBtn;
    private ListView CityList;
    private ClearEditText editText;
    private SharedPreferences myPreferences;
    private TextView cityname;
    private  MyApplication myApplication;
    private  List<City> mList;
    private  ArrayList<Map<String,String>> mArraylist;
    private  ArrayList<Map<String,String>> mNewArrayList;
    private List<City> mNewList;
    private SideBar sideBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        cityname=(TextView)findViewById(R.id.title_name);
        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        myPreferences=getSharedPreferences("users",MODE_PRIVATE);
        String cityCode=myPreferences.getString("cityname","北京");
        cityname.setText("当前城市:"+cityCode);
        myApplication=(MyApplication)getApplication();
        mList=myApplication.getCityList();
        mArraylist=new ArrayList<Map<String,String>>();
        mNewArrayList=new ArrayList<Map<String, String>>();
        sideBar=(SideBar) findViewById(R.id.sidebar);
        for(int i=0;i<mList.size();i++)
        {
            Map<String,String> listitem=new HashMap<String, String>();
            listitem.put("cityname",mList.get(i).getCity());
            listitem.put("citycode",mList.get(i).getNumber());
            //String cityname=mList.get(i).getCity();
            mArraylist.add(listitem);
        }
        //String[] listdata={"1","2","3"};
        CityList=(ListView)findViewById(R.id.city_list);
        //ArrayAdapter<String> adapter=new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,mArraylist);
        final SimpleAdapter adapter=new SimpleAdapter(SelectCity.this,mArraylist,R.layout.item,new String[]{"cityname","citycode"},new int[]{R.id.c_name,R.id.c_code});
        CityList.setAdapter(adapter);//ListView显示所有城市
        editText=(ClearEditText)findViewById(R.id.search_name);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mArraylist.clear();
                mList =myApplication.prepareSelectCityList(s.toString());
                for(int i=0;i<mList.size();i++)
                {
                    Map<String,String> listitem=new HashMap<String, String>();
                    listitem.put("cityname",mList.get(i).getCity());
                    listitem.put("citycode",mList.get(i).getNumber());
                    Log.d("selectcity",mList.get(i).getCity()+" "+mList.get(i).getNumber());
                    //String cityname=mList.get(i).getCity();
                    mArraylist.add(listitem);
                }
                final SimpleAdapter newadapter=new SimpleAdapter(SelectCity.this,mArraylist,R.layout.item,new String[]{"cityname","citycode"},new int[]{R.id.c_name,R.id.c_code});

                CityList.setAdapter(newadapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        AdapterView.OnItemClickListener itemClickListener=new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String codes=mList.get(position).getNumber();
                Intent i=new Intent();
                i.putExtra("cityCode",codes);
                setResult(RESULT_OK,i);
                finish();
            }
        };
        CityList.setOnItemClickListener(itemClickListener);

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
        @Override
        public void onTouchingLetterChanged(String s) {
            Log.d("sidebar",s);
            mArraylist.clear();
            mList =myApplication.prepareSelectCityBych(s);
            for(int i=0;i<mList.size();i++)
            {
                Map<String,String> listitem=new HashMap<String, String>();
                listitem.put("cityname",mList.get(i).getCity());
                listitem.put("citycode",mList.get(i).getNumber());
                Log.d("selectcity",mList.get(i).getCity()+" "+mList.get(i).getNumber());
                //String cityname=mList.get(i).getCity();
                mArraylist.add(listitem);
            }
            final SimpleAdapter newadapter=new SimpleAdapter(SelectCity.this,mArraylist,R.layout.item,new String[]{"cityname","citycode"},new int[]{R.id.c_name,R.id.c_code});

            CityList.setAdapter(newadapter);
        }
    });
}

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.title_back:
                Intent i=new Intent();
                i.putExtra("cityCode","");
                setResult(RESULT_OK,i);
                finish();
                break;
            default:break;
        }
    }
}
