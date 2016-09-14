package activity.gcy.com.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.gcy.view.EnvironmentDataView;

/**
 * Created by Mr.G on 2016/5/19.
 */
public class EnvironmentActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new EnvironmentDataView(this));


    }
}
