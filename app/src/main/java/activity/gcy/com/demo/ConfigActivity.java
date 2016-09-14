package activity.gcy.com.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gcy.beans.IntentKeyString;
import com.gcy.view.TitleBar;

import confige.Config;

public class ConfigActivity extends Activity {

    private TitleBar titlebar;
    private Button phone_us;

    private LinearLayout layout_config_frequency;
    private EditText layout_config_frequency_value;
    private Button layout_config_frequency_save_button;


    private LinearLayout layout_applicans_standard;
    private EditText layout_applicans_standard_temp;
    private EditText layout_applicans_standard_humi;
    private EditText layout_applicans_standard_water;
    private EditText layout_applicans_standard_pm2_5;
    private EditText layout_applicans_standard_dang;
    private Button layout_applicans_standard_save_button;
    private float tempStandard,humiStandard,waterStandard,pm2_5Standard,dangStandard;

    private LinearLayout layout_about_us;
    private int whichConfig=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_config);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        titlebar =(TitleBar) findViewById(R.id.title_bar);


        layout_config_frequency = (LinearLayout) findViewById(R.id.layout_config_frequency);
        layout_config_frequency_value =(EditText) findViewById(R.id.layout_config_frequency_value);
        layout_config_frequency_save_button = (Button) findViewById(R.id.layout_config_frequency_save_button);

        layout_applicans_standard = (LinearLayout) findViewById(R.id.layout_applicans_standard);
        layout_applicans_standard_temp =(EditText) findViewById(R.id.layout_applicans_standard_temp);
        layout_applicans_standard_humi =(EditText) findViewById(R.id.layout_applicans_standard_humi);
        layout_applicans_standard_water =(EditText) findViewById(R.id.layout_applicans_standard_water);
        layout_applicans_standard_pm2_5 =(EditText) findViewById(R.id.layout_applicans_standard_pm2_5);
        layout_applicans_standard_dang =(EditText) findViewById(R.id.layout_applicans_standard_dang);
        layout_applicans_standard_save_button= (Button)findViewById(R.id.layout_applicans_standard_save_button);




        layout_about_us = (LinearLayout) findViewById(R.id.layout_about_us);
        phone_us = (Button) findViewById(R.id.phone_us);



        Intent intent = getIntent();
        Bundle configInfo = intent.getExtras();
        whichConfig = configInfo.getInt(IntentKeyString.CONFIG_INTENT_KEY);
        String titleName = "";
        switch (whichConfig){
            case 1:
                layout_config_frequency.setVisibility(View.VISIBLE);
                titleName = "监测频率设置";
                break;
            case 2:
                layout_applicans_standard.setVisibility(View.VISIBLE);
                titleName = "设备标准值";
                break;
            case 4:
                layout_about_us.setVisibility(View.VISIBLE);
                titleName = "关于我们";
                break;
            default:
                finish();
                break;

        }
        if(whichConfig==2) {
            tempStandard = configInfo.getFloat("tempStandard",Config.TEMPERATURE_STANDARD);
            humiStandard = configInfo.getFloat("humiStandard",Config.HUMIDITY_STANDARD);
            waterStandard = configInfo.getFloat("waterStandard",Config.WATER_STANDARD);
            pm2_5Standard = configInfo.getFloat("pm2_5Standard",Config.PM2_5_STANDARD);
            dangStandard = configInfo.getFloat("dangStandard",Config.DANGEROUS_GAS_STANDARD);

            layout_applicans_standard_temp.setText("" + tempStandard);
            layout_applicans_standard_humi.setText("" + humiStandard);
            layout_applicans_standard_water.setText("" + waterStandard);
            layout_applicans_standard_pm2_5.setText("" + pm2_5Standard);
            layout_applicans_standard_dang.setText("" + dangStandard);
        }
        titlebar.initTitleBarInfo(titleName,-1,-1,"","");
        titlebar.setLeftContainerClickAble(true);
        titlebar.setIsBack(true);
        titlebar.setActionView();
        titlebar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                finish();
            }

            @Override
            public void onRightButtonClick(View v) {

            }
        });
        layout_config_frequency_save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value;
                value=layout_config_frequency_value.getText().toString();
                if(value.matches("^\\d+$"))
                    if(Integer.parseInt(value)>1&&Integer.parseInt(value)<10){

                        Intent resualtIntent = new Intent();
                        resualtIntent.putExtra("MonitoringFrequency",Integer.parseInt(value)*1000);
                        setResult(1,resualtIntent);
                        Toast.makeText(ConfigActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                    else
                        Toast.makeText(ConfigActivity.this, "频率请保持在1秒和10秒之间！", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(ConfigActivity.this, "输入格式有误！", Toast.LENGTH_SHORT).show();
            }
        });


        layout_applicans_standard_save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!layout_applicans_standard_temp.getText().toString().matches("\\d+.*\\d+")){
                    Toast.makeText(ConfigActivity.this, "输入格式有误！", Toast.LENGTH_SHORT).show();
                    return;}
                if(!layout_applicans_standard_humi.getText().toString().matches("\\d+.*\\d+")){
                    Toast.makeText(ConfigActivity.this, "输入格式有误！", Toast.LENGTH_SHORT).show();
                    return;}
                if(!layout_applicans_standard_water.getText().toString().matches("\\d+.*\\d+")){
                    Toast.makeText(ConfigActivity.this, "输入格式有误！", Toast.LENGTH_SHORT).show();
                    return;}
                if(!layout_applicans_standard_pm2_5.getText().toString().matches("\\d+.*\\d+")){
                    Toast.makeText(ConfigActivity.this, "输入格式有误！", Toast.LENGTH_SHORT).show();
                    return;}
                if(!layout_applicans_standard_dang.getText().toString().matches("\\d+.*\\d+")){
                    Toast.makeText(ConfigActivity.this, "输入格式有误！", Toast.LENGTH_SHORT).show();
                    return;}
                tempStandard = Float.parseFloat(layout_applicans_standard_temp.getText().toString());
                humiStandard = Float.parseFloat(layout_applicans_standard_humi.getText().toString());
                waterStandard = Float.parseFloat(layout_applicans_standard_water.getText().toString());
                pm2_5Standard = Float.parseFloat(layout_applicans_standard_pm2_5.getText().toString());
                dangStandard = Float.parseFloat(layout_applicans_standard_dang.getText().toString());


                Intent resualtIntent = new Intent();
                resualtIntent.putExtra("tempStandard",tempStandard);
                resualtIntent.putExtra("humiStandard",humiStandard);
                resualtIntent.putExtra("waterStandard",waterStandard);
                resualtIntent.putExtra("pm2_5Standard",pm2_5Standard);
                resualtIntent.putExtra("dangStandard",dangStandard);

                setResult(2,resualtIntent);

                finish();
            }
        });
        phone_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:15028690629"));
                startActivity(intent);

            }
        });


    }
}
