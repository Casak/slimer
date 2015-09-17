package casak.ru.slimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

import com.remfils.lizuntest2.LizunView;


public class PowerConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1),
            chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL,
                notCharging = status == BatteryManager.BATTERY_STATUS_NOT_CHARGING,
                acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        if(acCharge) {
            StartActivity.changeSlimer(LizunView.CONNECTED);
        }
        else if(notCharging){
            StartActivity.changeSlimer(LizunView.DISCONNECTED);
        }
    }
}