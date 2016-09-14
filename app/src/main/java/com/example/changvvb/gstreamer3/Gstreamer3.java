package com.example.changvvb.gstreamer3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gcy.beans.TemporaryData;
import com.gcy.mqttUtil.Phone;
import com.gcy.thread.HttpThread;

import org.freedesktop.gstreamer.GStreamer;

import activity.gcy.com.demo.R;
import confige.Config;

public class Gstreamer3 extends Activity implements SurfaceHolder.Callback {
    private float bx,by,ax,ay;

    private native void nativeInit();     // Initialize native code, build pipeline, etc
    private native void nativeFinalize(); // Destroy pipeline and shutdown native code
    private native void nativePlay();     // Set pipeline to PLAYING
    private native void nativePause();    // Set pipeline to PAUSED
    private static native boolean nativeClassInit(); // Initialize native class: cache Method IDs for callbacks
    private native void nativeSurfaceInit(Object surface);
    private native void nativeSurfaceFinalize();
    private long native_custom_data;      // Native code will use this to keep private data

    //private Phone p = Phone.getInstance();

    private boolean is_playing_desired;   // Whether the user asked to go to PLAYING

    private Button openDoor;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        // Initialize GStreamer and warn if it fails
        try {
            GStreamer.init(this);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish(); 
            return;
        }

        setContentView(R.layout.activity_main);

        Config.mActivity = this;

        ImageButton play = (ImageButton) this.findViewById(R.id.button_play);
        openDoor = (Button) findViewById(R.id.open_door);
        TemporaryData.getInstance().setFlagIsVideo(true);    //视频已开启

        play.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                is_playing_desired = true;
                nativePlay();
            }
        });

        ImageButton pause = (ImageButton) this.findViewById(R.id.button_stop);
        pause.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                is_playing_desired = false;
                nativePause();
            }
        });

        SurfaceView sv = (SurfaceView) this.findViewById(R.id.surface_video);
        SurfaceHolder sh = sv.getHolder();

        sh.addCallback(this);

        openDoor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //p.OpenTheDoor();
                finish();
                if(Config.mSoundActivity!=null)
                    Config.mSoundActivity.finish();
            }
        });

        sv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        ax = event.getX();
                        ay = event.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        bx = event.getX();
                        by = event.getY();
                        if((by-ay)>(bx-ax)&&(by-ay)>(ax-bx)&&(by-ay)>50){
                            //new Thread(new HttpThread("CAMERA UP")).start();
                            //p.ControlCamera("0");
                        }
                        if((ay-by)>(bx-ax)&&(ay-by)>(ax-bx)&&(by-ay)<-50){
                            //new Thread(new HttpThread("CAMERA DOWN")).start();
                            //p.ControlCamera("1");
                        }
                        if((bx-ax)>(ay-by)&&(bx-ax)>(by-ay)&&(bx-ax)>50){
                            //new Thread(new HttpThread("CAMERA LEFT")).start();
                            //p.ControlCamera("2");
                        }
                        if((ax-bx)>(ay-by)&&(ax-bx)>(by-ay)&&(bx-ax)<-50){
                            //new Thread(new HttpThread("CAMERA RIGHT")).start();
                            //p.ControlCamera("3");
                        }



                        break;

                }


                return true;
            }
        });
        if (savedInstanceState != null) {
            is_playing_desired = savedInstanceState.getBoolean("playing");
            Log.i ("GStreamer", "Activity created. Saved state is playing:" + is_playing_desired);
        } else {
            is_playing_desired = false;
            Log.i ("GStreamer", "Activity created. There is no saved state, playing: false");
        }

        // Start with disabled buttons, until native code is initialized
        this.findViewById(R.id.button_play).setEnabled(false);
        this.findViewById(R.id.button_stop).setEnabled(false);

        nativeInit();

        is_playing_desired = true;
        nativePlay();
    }

    protected void onSaveInstanceState (Bundle outState) {
        Log.d ("GStreamer", "Saving state, playing:" + is_playing_desired);
        outState.putBoolean("playing", is_playing_desired);
    }



    @Override
    public void onBackPressed() {
        setResult(1,null);
        finish();
    }

    protected void onDestroy() {
        Config.mActivity = null;
        TemporaryData.getInstance().setFlagIsVideo(false);
        nativeFinalize();
        //p.ControlVideoStop();
        super.onDestroy();
    }

    // Called from native code. This sets the content of the TextView from the UI thread.
    private void setMessage(final String message) {
        final TextView tv = (TextView) this.findViewById(R.id.textview_message);
        runOnUiThread (new Runnable() {
          public void run() {
            tv.setText(message);
          }
        });
    }

    private String getRemoteVideoIP(){
        return "";
    }

    // Called from native code. Native code calls this once it has created its pipeline and
    // the main loop is running, so it is ready to accept commands.
    private void onGStreamerInitialized () {
        Log.i ("GStreamer", "Gst initialized. Restoring state, playing:" + is_playing_desired);
        // Restore previous playing state
        if (is_playing_desired) {
            nativePlay();
        } else {
            nativePause();
        }

        // Re-enable buttons, now that GStreamer is initialized
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            public void run() {
                activity.findViewById(R.id.button_play).setEnabled(true);
                activity.findViewById(R.id.button_stop).setEnabled(true);
            }
        });
    }

    static {
        System.loadLibrary("gstreamer_android");
        System.loadLibrary("gstreamer3");
        nativeClassInit();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        Log.d("GStreamer", "Surface changed to format " + format + " width "
                + width + " height " + height);
        nativeSurfaceInit (holder.getSurface());
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("GStreamer", "Surface created: " + holder.getSurface());
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("GStreamer", "Surface destroyed");
        nativeSurfaceFinalize ();
    }


}
