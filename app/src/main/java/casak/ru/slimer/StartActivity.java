package casak.ru.slimer;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.remfils.lizuntest2.LizunView;

public class StartActivity extends Activity {

    private static final String TAG = "START_ACTIVITY";

    private PowerConnectionReceiver powerReceiver;
    private IntentFilter mFilter;
    private CameraView cameraView;
    private FrameLayout preview;
    private FrameLayout slimerPreview;
    private static LizunView slimer;
    private static casak.ru.slimer.LizunView casakSlimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate()");

            powerReceiver = new PowerConnectionReceiver();
            mFilter = new IntentFilter();
            mFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
            mFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            mFilter.addAction(Intent.ACTION_POWER_CONNECTED);
            registerReceiver(powerReceiver, mFilter);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_fullscreen);

        //cameraView = new CameraView(this);
        preview = (FrameLayout) findViewById(R.id.camera);
        //preview.addView(cameraView);

        slimerPreview = (FrameLayout) findViewById(R.id.slimer);
        slimer = new LizunView(this, getWindowManager());
        preview.addView(slimer);

        casakSlimer = new casak.ru.slimer.LizunView(this);
        //preview.addView(casakSlimer);


    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        slimer.resume();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
            try {
                String command;
                command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib service call activity 42 s16 com.android.systemui";
                Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command}, null);
                proc.waitFor();
                Log.d(TAG, "UI Disabled");
            }
            catch (Exception e){
                // TODO Write an exception handler
            }
            return true;
        }
        else if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN && event.getAction() == KeyEvent.ACTION_DOWN){
            try{
                String command;
                command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib am startservice -n com.android.systemui/.SystemUIService";
                Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command }, null);
                proc.waitFor();
                Log.d(TAG, "UI Enabled");
            }
            catch (Exception e){
                // TODO Write an exception handler
            }
            return true;
        }
        else {
            return super.dispatchKeyEvent(event);
        }
    }


    public static void changeSlimer(int STATE){
        if(slimer != null){
            Log.d(TAG, "changeSlimer();  slimer.pause() ");
            slimer.pause();
            Log.d(TAG, "changeSlimer();  slimer.playState(STATE) ");
            slimer.playState(STATE);
            Log.d(TAG, "changeSlimer();  slimer.resume() ");
            slimer.resume();
            Log.d(TAG, "changeSlimer to state:" + (STATE == 1 ? "connected" : "disconnected"));
        }
    }
}
