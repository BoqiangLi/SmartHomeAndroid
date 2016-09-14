package activity.gcy.com.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gcy.mqttUtil.Phone;
import com.gcy.view.TitleBar;


/**
 * Created by Mr.G on 2016/5/29.
 */
public class ApplicansWaterActivity extends Activity {

    private Button saveButton;
    private EditText editText;
    private TextView textViewTip;
    private String tips;
    private TitleBar titleBar;
    private Phone p ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_myapplicans_water);

        //Bundle bundle = getIntent().getExtras();


        saveButton = (Button) findViewById(R.id.applican_save_button);
        editText = (EditText) findViewById(R.id.applican_water_et);
        textViewTip = (TextView) findViewById(R.id.applicans_water_tips);
        titleBar = (TitleBar) findViewById(R.id.title_bar);
        //tips = bundle.getString(IntentKeyString.ENVIRONMENT_TEMP,"10");
        textViewTip.setText("设置的水温度需高于环境温度"+tips+"℃！");

        p = Phone.getInstance();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strWater = editText.getText().toString();
                if(!strWater.matches("\\d+.*\\d+")){
                    Toast.makeText(ApplicansWaterActivity.this, "输入数据有误！", Toast.LENGTH_SHORT).show();
                    return;}

                final Float str;
                str = Float.parseFloat(editText.getText().toString());
                if(str>100){
                    Toast.makeText(ApplicansWaterActivity.this, "水温需小于100度", Toast.LENGTH_SHORT).show();
                    return;}
                p.ControlWater(String.valueOf(str));
                //Toast.makeText(ApplicansWaterActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
                finish();



            }
        });

        titleBar.initTitleBarInfo("水温控制",-1,-1,"","");
        titleBar.setLeftContainerClickAble(true);
        titleBar.setIsBack(true);
        titleBar.setActionView();
        titleBar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                finish();
            }

            @Override
            public void onRightButtonClick(View v) {

            }
        });

    }
}
