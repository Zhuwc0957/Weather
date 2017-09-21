package cn.edu.pku.ss.zhuwc.myweather;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by ZhuWC on 2017/9/21.
 */

public class MainActivity extends Activity {
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
    }
}
