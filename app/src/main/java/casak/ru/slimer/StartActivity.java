package casak.ru.slimer;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;


import com.remfils.lizuntest2.LizunView;

public class StartActivity extends Activity {
    private static final String TAG = "START_ACTIVITY";

    private static LizunView slimer;
    private static int state = LizunView.DISCONNECTED;
    private PowerConnectionReceiver powerReceiver;
    private IntentFilter mFilter;
    private CameraView cameraView;
    private FrameLayout preview;

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

        LizunAudio.init(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_fullscreen);

        cameraView = new CameraView(this);
        slimer = new LizunView(this, getWindowManager());

        preview = (FrameLayout) findViewById(R.id.camera);
        preview.addView(cameraView);
        preview.addView(slimer);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        slimer.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        System.exit(1);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
            try {
                String command;
                command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib am startservice -n com.android.systemui/.SystemUIService";
                Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command }, null);
                proc.waitFor();
                Log.d(TAG, "UI Enabled");
            }
            catch (Exception e){
                return super.dispatchKeyEvent(event);
            }
            return true;
        }
        else if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN && event.getAction() == KeyEvent.ACTION_DOWN){
            try{
                String command;
                command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib service call activity 42 s16 com.android.systemui";
                Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command}, null);
                proc.waitFor();
                Log.d(TAG, "UI Disabled");

            }
            catch (Exception e){
                return super.dispatchKeyEvent(event);
            }
            return true;
        }
        else return super.dispatchKeyEvent(event);
    }


    public static void changeSlimer(int STATE){
        if(slimer != null && state != STATE){
            state = STATE;
            slimer.pause();
            slimer.playState(STATE);
            slimer.resume();
            Log.d(TAG, "changeSlimer to state:" + (STATE == 1 ? "connected" : "disconnected"));
        }
    }
}