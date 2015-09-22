package casak.ru.slimer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

public
class LizunView extends GLSurfaceView implements SurfaceHolder.Callback{
    private static final String TAG = "LIZUN_VIEW";
    private static final int START_ANIMATION = 0;
    private static final int SCREEN_WIDTH = 1280;
    private static final int SCREEN_HEIGHT = 800;
    private static final int SLIMER_WIDTH = 318*2;
    private static final int SLIMER_HEIGHT = 300*2;

    private boolean runFlag;
    private int animation;
    private AnimationDrawable slimerAnimation;
    private Animation viewAnimation;
    private float currentX;
    private float currentY;
    private ObjectAnimator animX;
    private ObjectAnimator animY;
    private AnimatorSet animSetXY;
    private AnimationEndListener animationEndListener;




    public LizunView(Context context){
        super(context);
        getHolder().addCallback(this);
        setLayoutParams(new FrameLayout.LayoutParams(SLIMER_WIDTH, SLIMER_HEIGHT));
        currentX = 0;
        currentY = 0;
        setX(currentX);
        setY(currentY);
        animationEndListener = new AnimationEndListener();
    }


    private void start(){
        Log.d(TAG, "start()");
        play();
    }

    private void play(){
        int switcher  = (int) Math.random()*7+1;
        switch (switcher){
            case 1:
                playMain();
                break;
            case 2:
                playThinking();
                break;
            case 3:
                playWallHit();
                break;
            case 4:
                playCeilingHit();
                break;
            case 5:
                playScreenHit();
                break;
            case 6:
                playLookAround();
                break;
            case 7:
                playShowStash();
                break;
            default:
                playMain();
                break;
        }
    }


    private void playMain(){
        Log.d(TAG, "playMain()");

        setBackgroundResource(R.anim.search);
        AnimationDrawable a = (AnimationDrawable) getBackground();
        a.start();
        Log.d(TAG, "START currentX: " + currentX + " END getX(): " + getX() + " currentY: " + currentY + " getY(): " + getY());

        animate().x(currentX = SCREEN_WIDTH / 2 - SLIMER_WIDTH / 2)
                .y(currentY = SCREEN_HEIGHT / 2 - SLIMER_HEIGHT / 2)
                .setDuration(1000).setListener(animationEndListener);


        Log.d(TAG, "END currentX: " + currentX + " END getX(): " + getX() + " currentY: " + currentY + " getY(): " + getY());
    }


    private void playThinking(){
        Log.d(TAG, "playThinking()");

        setBackgroundResource(R.anim.found);
        slimerAnimation = (AnimationDrawable) getBackground();
        slimerAnimation.start();

    }


    private void playWallHit(){
        Log.d(TAG, "playWallHit()");
        setBackgroundResource(R.anim.wall_hit_charge); //160ms
        slimerAnimation = (AnimationDrawable) getBackground();
        slimerAnimation.start();

        setBackgroundResource(R.anim.wall_hit_animation); //1117ms +4000
        slimerAnimation = (AnimationDrawable) getBackground();
        slimerAnimation.start();
        playTurn();


    }


    private void playCeilingHit(){
        Log.d(TAG, "playCeilingHit()");
        setX(getX());
        setY(0);
        setBackgroundResource(R.anim.hit_up_charge); //440ms +4000
        slimerAnimation = (AnimationDrawable) getBackground();
        slimerAnimation.start();

        setBackgroundResource(R.anim.hit_celling_animation); //ms +
        slimerAnimation = (AnimationDrawable) getBackground();
        slimerAnimation.start();
    }


    private void playScreenHit(){
        Log.d(TAG, "playScreenHit()");

    }


    private void playLookAround(){
        Log.d(TAG, "playLookAround()");

    }


    private void playShowStash(){
        Log.d(TAG, "playShowStash()");

    }

    private void playTurn(){
        Log.d(TAG, "playTurn()");
        setBackgroundResource(R.anim.turn_animation); //1120ms +4000
        slimerAnimation = (AnimationDrawable) getBackground();
        slimerAnimation.start();
        animate().x(getX())
                .y(getY())
                .setDuration(1120).setListener(animationEndListener);
        setScaleX(getScaleX() * -1);
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
       start();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }


    private class AnimationEndListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {
            Log.d("LIZUN", "onAnimationStart()");
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            Log.d("LIZUN", "onAnimationEnd()");
            //play();
            //playMain();
            playWallHit();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            Log.d("LIZUN", "onAnimationCancel()");
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            Log.d("LIZUN", "onAnimationRepeat()");
        }
    }

}


