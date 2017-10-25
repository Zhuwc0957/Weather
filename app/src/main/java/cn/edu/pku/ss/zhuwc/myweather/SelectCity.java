package cn.edu.pku.ss.zhuwc.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectCity extends AppCompatActivity implements View.OnClickListener{

    private ImageView mBackBtn;
    private SharedPreferences myPreferences;
    private TextView cityname;
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

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.title_back:
                Intent i=new Intent();
                i.putExtra("cityCode","101160101");
                setResult(RESULT_OK,i);
                finish();
                break;
            default:break;
        }
    }
}
