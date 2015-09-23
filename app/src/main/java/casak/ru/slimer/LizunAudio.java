package casak.ru.slimer;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.Locale;

public class LizunAudio {

    private static Context context;
    private static MediaPlayer mp;

    public static final int THINK_SOUND = R.raw.raw_thinking;
    public static final int HIT_WALL_SOUND = R.raw.raw_meat_hit;
    public static final int HIT_TOP_SOUND = R.raw.raw_ceiling_hit;
    public static final int HIT_SCREEN_SOUND = R.raw.raw_meat_screen_hit;
    public static final int OINK_SOUND = R.raw.raw_oink;
    public static final int CONNECT_SOUND = R.raw.raw_connect;

    public static void init(Context context_) {
        context = context_;
    }

    public static void playSound(int sound_id) {
        if ( sound_id == 0 ) return;


            mp = MediaPlayer.create(context, sound_id);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            Log.v("MEDIA PLAYER", "" + mp.getDuration());



    }
}


