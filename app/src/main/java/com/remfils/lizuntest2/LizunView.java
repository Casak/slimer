package com.remfils.lizuntest2;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import casak.ru.slimer.R;

import java.util.Timer;
import java.util.TimerTask;

public class LizunView extends SurfaceView {
    static final public int CONNECTED = 1;
    static final public int DISCONNECTED = 2;

    static public int WIDTH = 318 * 2; // slimer width
    static public int HEIGHT = 300 * 2; // slimer height

    static final private int SEARCH_STATE = 1;
    static final private int BEGIN_STATE = 3;
    static final private int CHARGE_SIDE_WALL = 5;
    static final private int HIT_SIDE_ANIMATION = 6;
    static final private int THINKING_STATE = 7;
    static final private int FOUND_STATE = 8;
    static final private int FOUND_IN_CENTER_STATE = 9;
    static final private int CHARGE_UP_STATE = 10;
    static final private int HIT_UP_ANIMATION = 11;
    static final private int FOUND_TURN_ANIMATION = 12;
    static final private int FOUND_SHOW_ANIMATION = 13;
    static final private int HIT_SCREEN_ANIMATION = 14;

    final private float SLIMER_ALPHA = 0.8f;
    final private int SLIMER_CHARGE_ACCELERATION = 2;
    final private int SLIMER_FLIGHT_DURATION = 300;

    static final private int HIT_UP_ANIMATION_LENGTH = 1620; // was 1550
    static final private int HIT_SIDE_ANIMATION_LENGTH = 1040; // was 920
    static final private int THINKING_ANIMATION_LENGTH = 2820; // was 2500
    static final private int HIT_SCREEN_ANUMATION_LENGTH = 1460;
    static final private int TURN_ANIMATION_LENGTH = 1280;
    static final private int SHOW_ANIMATION_LENGTH = 1790;

    private int screen_width, screen_height;

    private boolean is_first_time = true;

    private float x = -100, y;
    private int current_state = BEGIN_STATE;
    private int future_state = -1;
    private boolean is_charger_found = false;

    public LizunView(Context context, WindowManager window_manager) {
        super(context);
        Log.i("LIZUN", "LizunView Constructor()");
        getHolder().setFixedSize(1280,800);

        Display d = window_manager.getDefaultDisplay();
        Point size = new Point();
        d.getSize(size);
        screen_width = size.x;
        screen_height = size.y;

        setDefaultSize();

        setZOrderOnTop(true);
        SurfaceHolder holder = getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);

        y = screen_height / 2;

        updatePosition();

        setAlpha(SLIMER_ALPHA);

    }

    public void setDefaultSize() {
        Log.i("LIZUN", "setDefaultSize()");
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(WIDTH,HEIGHT);
        params.setMargins(0, 0,0,0);
        setLayoutParams(params);

        setPadding(0, 0, 0, 0);

    }

    public void pause() {
        Log.i("LIZUN", "pause()");
        AnimationDrawable a_d = (AnimationDrawable) getBackground();
        a_d.stop();

        updatePosition();

        setAnimation(null);
    }

    public void resume() {
        Log.i("LIZUN", "resume()");
        if ( is_first_time ) {
            start();
            is_first_time = false;
        }
        else {
            AnimationDrawable a_d = (AnimationDrawable) getBackground();
            a_d.start();

            update();
        }
    }

    private void start() {
        Log.i("LIZUN", "start()");
        playAnimation(R.anim.search);

        x = -100;
        y = screen_height / 2 - HEIGHT / 2;
        updatePosition();

        TranslateAnimation a = moveTo(screen_width / 2, screen_height / 2);

        a.setAnimationListener(new AnimationEndListener());
    }

    public void playState(int state) {
        Log.i("LIZUN", "playState()");
        traceStateChange(state);

        if ( state == DISCONNECTED ) {
            is_charger_found = false;
        }
        else if ( state == CONNECTED ) {
            if ( is_charger_found ) return;

            is_charger_found = true;
            switch ( current_state ) {
                /*case THINKING_STATE:
                case SEARCH_STATE:
                    current_state = FOUND_STATE;
                    *//*x = getX();
                    y = getY();*//*
                    update();
                    break;*/
                case FOUND_STATE:
                case FOUND_SHOW_ANIMATION:
                case FOUND_TURN_ANIMATION:
                    return;
                default:
                    future_state = FOUND_STATE;
            }
        }
    }

    private void traceStateChange(int state) {
        Log.i("LIZUN", "traceStateChange()");
        String state_str = state == CONNECTED ? "connected" : "disconnected";
        Log.v("changed state to", state_str);

    }

    @Deprecated
    public void playFirstState() {
        Log.i("LIZUN", "playFirstState()");
        is_charger_found = false;
    }

    @Deprecated
    public void playSecondState() {
        Log.i("LIZUN", "playSecondState()");
        is_charger_found = true;

        current_state = FOUND_STATE;

        playAnimation(R.anim.slimer_second_def);

        moveTo( screen_width / 2, screen_height / 2);

        float dr = x - getX() - WIDTH / 2;
        if ( dr <= 0 ) setScaleX(-1);
        else setScaleX(1);
    }

    private void setState(int state) {
        Log.i("LIZUN", "seState()");
        current_state = state;
    }

    private void update () {
        Log.i("LIZUN", "update()");
        updatePosition();
        desideWhatToDo();
        updateAnimation();
    }

    private void updatePosition() {
        Log.i("LIZUN", "updatePosition()");
        setX(x);
        setY(y);

        Log.v("coords", "(" + String.valueOf(x) + "," + String.valueOf(y) + ")");
    }

    private void desideWhatToDo() {
        Log.i("LIZUN", "desideWhatToDo()");
        if ( current_state == FOUND_STATE ) return;

        double chance = Math.random();
        if ( is_charger_found ) {
            if ( chance < 0.4 ) current_state = FOUND_TURN_ANIMATION;
            else current_state = FOUND_SHOW_ANIMATION;
        }
        else {
            if ( chance < 0.3 ) current_state = CHARGE_SIDE_WALL;
            else if ( chance < 0.45 ) current_state = THINKING_STATE;
            else if ( chance < 0.65 ) current_state = CHARGE_UP_STATE;
            else if ( chance < 0.8 ) current_state = HIT_SCREEN_ANIMATION;
            else current_state = SEARCH_STATE;
        }
        traceCurrentState();
    }

    private void updateAnimation() {
        Log.i("LIZUN", "updateAnimation()");
        TranslateAnimation a;
        switch ( current_state ) {
            case SEARCH_STATE:
                playAnimation(R.anim.search);
                moveRandom();
                break;
            case FOUND_STATE:
                moveTo(screen_width / 2, screen_height / 2);
                break;
            case CHARGE_SIDE_WALL:
                playAnimation(R.anim.wall_hit_charge);
                float next_x = getScaleX() > 0 ? screen_width - WIDTH / 2 : WIDTH / 2;
                a = moveTo (next_x, getY() + HEIGHT / 2);
                a.setInterpolator(new LinearInterpolator());
                a.setDuration(SLIMER_FLIGHT_DURATION);
                break;
            case CHARGE_UP_STATE:
                playAnimation(R.anim.hit_up_charge);
                a = moveTo(getX() + WIDTH / 2, HEIGHT / 2);
                a.setInterpolator(new LinearInterpolator());
                a.setDuration(SLIMER_FLIGHT_DURATION);
                break;
            case THINKING_STATE:
                playAnimation(R.anim.thinking);
                waitAnimationToEnd(SHOW_ANIMATION_LENGTH);

                break;
            case HIT_SCREEN_ANIMATION:
                playAnimation(R.anim.hit_screen);
                waitAnimationToEnd(SHOW_ANIMATION_LENGTH);

                break;
            case FOUND_TURN_ANIMATION:
                playAnimation(R.anim.turn_animation);
                waitAnimationToEnd(SHOW_ANIMATION_LENGTH);

                break;
            case FOUND_SHOW_ANIMATION:
                playAnimation(R.anim.show_animation);
                waitAnimationToEnd(SHOW_ANIMATION_LENGTH);

                break;
            default:
                //setState(SEARCH_STATE);
        }
    }

    private void moveRandom() {
        Log.i("LIZUN", "moveRandom()");
        double end_x=0, end_y=0;

        end_x = ( screen_width - WIDTH ) * Math.random() + WIDTH / 2;
        end_y = ( screen_height - HEIGHT ) * Math.random() + HEIGHT / 2;

        if ( (getScaleX() > 0 && end_x < x + WIDTH / 2 ) || ( getScaleX() < 0 && end_x > x + WIDTH / 2 ) ) {
            turn();
        }

        moveTo((float) end_x, (float) end_y);
    }

    private TranslateAnimation moveTo(float to_x, float to_y) {
        Log.i("LIZUN", "moveTo()");
        double dx = x - to_x + WIDTH / 2;
        double dy = y - to_y + HEIGHT / 2;

        x = to_x - WIDTH / 2;
        y = to_y - HEIGHT / 2;

        final TranslateAnimation a = new TranslateAnimation(0,x - getX() , 0,y - getY() );
        a.setDuration(1500);
        a.setInterpolator(new LinearInterpolator());

        setAnimation(a);

        a.setAnimationListener(new AnimationEndListener());

        return a;
    }

    private void playAnimation(int anim_id) {
        Log.i("LIZUN", "playAnimation()");
        setBackgroundResource(anim_id);

        AnimationDrawable a = (AnimationDrawable) getBackground();
        a.start();
    }

    private Animation waitAnimationToEnd (int time) {
        Log.i("LIZUN", "waitAnimationToEnd()");
        Animation a = moveTo(getX() + WIDTH / 2, getY() + HEIGHT / 2);
        a.setDuration(time);
        a.setInterpolator(new LinearInterpolator());
        return null;
    }

    private void turn() {
        Log.i("LIZUN", "turn()");
        playAnimation(R.anim.turn_animation);
        waitAnimationToEnd(HIT_SIDE_ANIMATION_LENGTH);
        setScaleX(getScaleX() * -1);

    }

    private void animationEndCallback() {
        Log.i("LIZUN", "animationEndCallback()");
        switch ( current_state ) {
            case CHARGE_SIDE_WALL:
                updatePosition();
                current_state = HIT_SIDE_ANIMATION;
                playAnimation(R.anim.wall_hit_animation);
                waitAnimationToEnd(HIT_SIDE_ANIMATION_LENGTH);
                return;
            case CHARGE_UP_STATE:
                updatePosition();
                current_state = HIT_UP_ANIMATION;
                playAnimation(R.anim.hit_celling_animation);
                waitAnimationToEnd(HIT_UP_ANIMATION_LENGTH);
                return;
            case HIT_SIDE_ANIMATION:
                turn();
                waitAnimationToEnd(TURN_ANIMATION_LENGTH);
                break;
            case FOUND_STATE:
                updatePosition();
                setScaleX(-1);
                current_state = FOUND_TURN_ANIMATION;
                playAnimation(R.anim.turn_animation);
                waitAnimationToEnd(TURN_ANIMATION_LENGTH);
                return;
        }

        if ( future_state != -1 ) {
            current_state = future_state;
            future_state = -1;
        }

        update();
    }

    private void traceCurrentState() {
        Log.i("LIZUN", "traceCurrentState()");
        String state;
        switch (current_state) {
            case SEARCH_STATE:
                state = "searching";
                break;
            case FOUND_STATE:
                state = "found";
                break;
            case CHARGE_SIDE_WALL:
                state = "charging side wall";
                break;
            case CHARGE_UP_STATE:
                state = "chargin celling";
                break;
            case THINKING_STATE:
                state = "thinking";
                break;
            case FOUND_TURN_ANIMATION:
                state = "looking around";
                break;
            case FOUND_SHOW_ANIMATION:
                state = "showing whats up";
                break;
            default:
                state = String.valueOf(current_state);
        }
        Log.v("current_state", state);
    }

    private class AnimationEndListener implements Animation.AnimationListener {
        public void onAnimationStart(Animation animation) {Log.i("LIZUN", "onAnimationStart()");}
        public void onAnimationRepeat(Animation animation) {Log.i("LIZUN", "onAnimationRepeat()");}
        public void onAnimationEnd(Animation animation) {Log.i("LIZUN", "onAnimationEnd()");
            animationEndCallback();
        }
    }


}
