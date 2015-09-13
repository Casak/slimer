package casak.ru.slimer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.Arrays;


// TODO Recieve a charge signal

public class StartActivity extends Activity {

    private static Camera mCamera;
    private CameraPreview mPreview;
    private FrameLayout lizunPreview;
    private final String TAG = "START_ACTIVITY";
    com.remfils.lizuntest2.LizunView slimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_fullscreen);
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.setLayoutParams(new FrameLayout.LayoutParams(1280, 960));
        preview.addView(mPreview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mCamera == null){
            mCamera = getCameraInstance();
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
        }


        slimer = new com.remfils.lizuntest2.LizunView(this, getWindowManager());
        lizunPreview = (FrameLayout) findViewById(R.id.lizun_preview);
        lizunPreview.addView(slimer);
        slimer.playFirstState();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        slimer = new com.remfils.lizuntest2.LizunView(this, getWindowManager());
        lizunPreview = (FrameLayout) findViewById(R.id.lizun_preview);
        lizunPreview.addView(slimer);
        slimer.playFirstState();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            try {
                String command;
                command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib service call activity 42 s16 com.android.systemui";
                Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command}, null);
                proc.waitFor();
                Log.i(TAG, "UI Disabled");
            }
            catch (Exception e){
                // TODO Write an exception handler
            }
            return true;
        }
        else if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN){
            try{
                String command;
                command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib am startservice -n com.android.systemui/.SystemUIService";
                Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command }, null);
                proc.waitFor();
                Log.i(TAG, "UI Enabled");
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


    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(1);
        }
        catch (Exception e){
            //TODO Write an exception handler
        }
        return c;
    }




/*
    public static boolean isConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_STATUS_CHARGING || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }
    */
}