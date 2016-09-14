package activity.gcy.com.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by Mr.G on 2016/5/14.
 */
public class MainNewApplicansActivity extends Activity{

    private Button saveBut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_new_applicans);

        initViewGroup();


        saveBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViewGroup() {
        saveBut = (Button) findViewById(R.id.main_new_applicans_ok_button);
    }
}
