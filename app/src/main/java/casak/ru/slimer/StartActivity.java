package casak.ru.slimer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.remfils.lizuntest2.LizunView;


// TODO Recieve a charge signal

public class StartActivity extends Activity {

    private static Camera mCamera;
    private CameraPreview mPreview;
    private RelativeLayout preview;
    private static final String TAG = "START_ACTIVITY";
    private static LizunView slimer;
    private PowerConnectionReceiver powerReceiver;
    private IntentFilter mFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate()");

        powerReceiver = new PowerConnectionReceiver();
        mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        mFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        registerReceiver(powerReceiver, mFilter);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_fullscreen);
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera);
        slimer = new LizunView(this, getWindowManager());
        preview = (RelativeLayout) findViewById(R.id.startActivity);
        preview.addView(mPreview);
        preview.setPadding(0, 0, 0, 0);
        slimer.setDefaultSize();
        slimer.setAlpha(0.7f);
        preview.addView(slimer);



    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        if(mCamera == null){
            mCamera = getCameraInstance();

        }

        slimer.resume();

        Log.i(TAG, String.valueOf(isConnected(this)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
        System.exit(0);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {


        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
            try {
                String command;
                command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib service call activity 42 s16 com.android.systemui";
                Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command}, null);
                proc.waitFor();
                Log.i(TAG, "UI Disabled");
                doRestart(this);
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
                Log.i(TAG, "UI Enabled");
                doRestart(this);
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



    public static void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    } else {
                        Log.e(TAG, "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    Log.e(TAG, "Was not able to restart application, PM null");
                }
            } else {
                Log.e(TAG, "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            Log.e(TAG, "Was not able to restart application");
        }
    }



    public static boolean isConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_STATUS_CHARGING || plugged == BatteryManager.BATTERY_PLUGGED_USB
                || plugged == BatteryManager.BATTERY_PLUGGED_AC;
    }

    public static void changeSlimer(int STATE){
        if(slimer != null){
            slimer.pause();
            slimer.playState(STATE);
            slimer.resume();
            Log.i(TAG, "changeSlimer to state:" + (STATE == 1 ? "connected" : "disconnected"));
        }
    }

}