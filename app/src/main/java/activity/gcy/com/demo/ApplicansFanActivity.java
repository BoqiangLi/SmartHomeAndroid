package activity.gcy.com.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gcy.mqttUtil.Phone;
import com.gcy.view.TitleBar;


/**
 * Created by Mr.G on 2016/6/10.
 */
public class ApplicansFanActivity extends Activity {

    private TextView tips;
    private TextView pross;
    private SeekBar fanSeekbar;
    private Button save;
    private TitleBar titleBar;
    private Phone p;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_myapplicance_fan);

        p = Phone.getInstance();
        initView();


        fanSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pross.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                        p.ControlFan(String.valueOf(seekBar.getProgress()));

            }
        });

        titleBar.initTitleBarInfo("风扇控制",-1,-1,"","");
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        tips = (TextView) findViewById(R.id.fan_tips);
        pross = (TextView) findViewById(R.id.fan_text);
        fanSeekbar = (SeekBar) findViewById(R.id.fan_seekBar);
        save = (Button)findViewById(R.id.fan_save);
        titleBar = (TitleBar) findViewById(R.id.title_bar);
    }


}
