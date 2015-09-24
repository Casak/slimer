package com.remfils.lizuntest2;

import android.animation.Animator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import casak.ru.slimer.LizunAudio;
import casak.ru.slimer.R;


public class LizunView extends SurfaceView {
    private static final String TAG = "LIZUN_VIEW";

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
    static final private int KNOCK_SCREEN_ANIMATION = 15;

    final private float SLIMER_ALPHA = 0.8f;
    final private int SLIMER_CHARGE_ACCELERATION = 2;
    final private int SLIMER_FLIGHT_DURATION = 300;

    static final private int HIT_UP_ANIMATION_LENGTH = 1620; // was 1550
    static final private int HIT_SIDE_ANIMATION_LENGTH = 1040; // was 920
    static final private int THINKING_ANIMATION_LENGTH = 2820; // was 2500
    static final private int HIT_SCREEN_ANUMATION_LENGTH = 1460;
    static final private int TURN_ANIMATION_LENGTH = 1280;
    static final private int SHOW_ANIMATION_LENGTH = 1790;
    static final private int KNOCK_SCREEN_ANIMATION_LENGTH = 1680;

    private int screen_width, screen_height;

    private boolean is_first_time = true;

    private float x = -100, y;
    private int current_state = BEGIN_STATE;
    private int future_state = -1;
    private boolean is_charger_found = false;
    private boolean alreadyTurn = false;
    private AnimationDrawable animationDrawable;
    private ViewPropertyAnimator propertyAnimator;
    private LinearInterpolator linearInterpolator;
    private AccelerateInterpolator accelerateInterpolator;

    public LizunView(Context context, WindowManager window_manager) {
        super(context);

        Point size = new Point();
        window_manager.getDefaultDisplay().getSize(size);
        screen_width = size.x;
        screen_height = size.y;
        y = screen_height / 2;

        linearInterpolator = new LinearInterpolator();
        accelerateInterpolator = new AccelerateInterpolator(SLIMER_CHARGE_ACCELERATION);

        setLayoutParams(new FrameLayout.LayoutParams(WIDTH, HEIGHT));
        setAlpha(SLIMER_ALPHA);
        setZOrderOnTop(true);

        SurfaceHolder holder = getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
    }

    private void start() {
        playAnimation(R.anim.search);

        x = -100;
        y = screen_height / 2 - HEIGHT / 2;

        moveTo(screen_width / 2, screen_height / 2);
    }

    public void pause() {
        animationDrawable = (AnimationDrawable) getBackground();
        animationDrawable.stop();
        setAnimation(null);
    }

    public void resume() {
        if ( is_first_time ) {
            start();
            is_first_time = false;
        }
        else {
            animationDrawable = (AnimationDrawable) getBackground();
            animationDrawable.start();
            update();
        }
    }

    public void playState(int state) {
        if ( state == DISCONNECTED ) {
            is_charger_found = false;
            alreadyTurn = false;
            future_state = 0;
        }
        else if ( state == CONNECTED ) {
            if ( is_charger_found ) return;

            is_charger_found = true;
            switch ( current_state ) {
                case FOUND_STATE:
                case FOUND_SHOW_ANIMATION:
                case FOUND_TURN_ANIMATION:
                    return;
                default:
                    future_state = FOUND_STATE;
            }
        }
    }

    private void update () {
        desideWhatToDo();
        updateAnimation();
    }

    private void desideWhatToDo() {
        if (current_state == FOUND_STATE) return;

        double chance = Math.random();
        if (is_charger_found) {
            if (!alreadyTurn) {
                alreadyTurn = true;
                LizunAudio.playSound(LizunAudio.CONNECT_SOUND);
                current_state = FOUND_TURN_ANIMATION;
            }
            else current_state = FOUND_SHOW_ANIMATION;
        }
        else {
            if (chance < 0.3) current_state = CHARGE_SIDE_WALL;
            else if ( chance < 0.45) {
                if (current_state == THINKING_STATE) current_state = SEARCH_STATE;
                else current_state = THINKING_STATE;
            }
            else if (chance < 0.65) current_state = CHARGE_UP_STATE;
            else if (chance < 0.75) {
                if ( current_state == HIT_SCREEN_ANIMATION) current_state = SEARCH_STATE;
                else current_state = HIT_SCREEN_ANIMATION;
            }
            else if ( chance < 0.95 ) {
                if ( current_state == KNOCK_SCREEN_ANIMATION) current_state = SEARCH_STATE;
                else current_state = KNOCK_SCREEN_ANIMATION;
            }
            else current_state = SEARCH_STATE;
        }
    }

    private void updateAnimation() {
        switch (current_state) {
            case SEARCH_STATE:
                playAnimation(R.anim.search);
                moveRandom();
                break;
            case FOUND_STATE:
                moveTo(screen_width / 2, screen_height / 2);
                break;
            case CHARGE_SIDE_WALL:
                playAnimation(R.anim.wall_hit_animation);
                playAnimation(R.anim.wall_hit_charge);
                float next_x = getScaleX() > 0 ? screen_width - WIDTH / 2 : WIDTH / 2;
                propertyAnimator = moveTo (next_x, getY() + HEIGHT / 2);
                propertyAnimator.setInterpolator(accelerateInterpolator);
                propertyAnimator.setDuration(SLIMER_FLIGHT_DURATION);
                break;
            case CHARGE_UP_STATE:
                playAnimation(R.anim.hit_up_charge);
                propertyAnimator = moveTo(getX() + WIDTH / 2, HEIGHT / 2);
                propertyAnimator.setInterpolator(accelerateInterpolator);
                propertyAnimator.setDuration(SLIMER_FLIGHT_DURATION);
                break;
            case THINKING_STATE:
                LizunAudio.playSound(LizunAudio.THINK_SOUND);
                playAnimation(R.anim.thinking);
                waitAnimationToEnd(THINKING_ANIMATION_LENGTH);
                break;
            case HIT_SCREEN_ANIMATION:
                LizunAudio.playSound(LizunAudio.HIT_SCREEN_SOUND);
                playAnimation(R.anim.hit_screen);
                waitAnimationToEnd(HIT_SCREEN_ANUMATION_LENGTH);
                break;
            case KNOCK_SCREEN_ANIMATION:
                LizunAudio.playSound(LizunAudio.SCREEN_KNOCK_SOUND);
                playAnimation(R.anim.knocking_on_screens_door);
                waitAnimationToEnd(KNOCK_SCREEN_ANIMATION_LENGTH);
                break;
            case FOUND_TURN_ANIMATION:
                playAnimation(R.anim.turn_animation);
                waitAnimationToEnd(TURN_ANIMATION_LENGTH);
                break;
            case FOUND_SHOW_ANIMATION:
                playAnimation(R.anim.show_animation);
                waitAnimationToEnd(SHOW_ANIMATION_LENGTH);
                break;
        }
    }

    private ViewPropertyAnimator moveTo(float to_x, float to_y) {
        double dx = x;
        double dy = y;

        x = to_x - WIDTH / 2;
        y = to_y - HEIGHT / 2;

        dx = x;
        dy = y;

        double dr = Math.sqrt(dx * dx + dy * dy);


        propertyAnimator = animate().x(x).y(y).setDuration(Math.round(1500 / screen_width * dr))
                .setListener(new AnimationEndListener());
        return propertyAnimator;
    }

    private void moveRandom() {
        double  end_x=0, 
                end_y=0;
        LizunAudio.playSound(LizunAudio.OINK_SOUND);
        end_x = ( screen_width - WIDTH ) * Math.random() + WIDTH / 2;
        end_y = ( screen_height - HEIGHT ) * Math.random() + HEIGHT / 2;

        if ( (getScaleX() > 0 && end_x < x + WIDTH / 2 ) || ( getScaleX() < 0 && end_x > x + WIDTH / 2 ) ) {
            turn();
        }

        moveTo((float) end_x, (float) end_y);
    }

    private void playAnimation(int anim_id) {
        setBackgroundResource(anim_id);
        animationDrawable = (AnimationDrawable) getBackground();
        animationDrawable.start();
    }

    private Animation waitAnimationToEnd (int time) {
        propertyAnimator = moveTo(getX() + WIDTH / 2, getY() + HEIGHT / 2);
        propertyAnimator.setDuration(time);
        propertyAnimator.setInterpolator(linearInterpolator);
        return null;
    }

    private void turn() {
        setScaleX(getScaleX() * -1);
    }

    private void animationEndCallback() {
        switch ( current_state ) {
            case CHARGE_SIDE_WALL:
                LizunAudio.playSound(LizunAudio.HIT_WALL_SOUND);
                current_state = HIT_SIDE_ANIMATION;
                playAnimation(R.anim.wall_hit_animation);
                waitAnimationToEnd(HIT_SIDE_ANIMATION_LENGTH);
                return;
            case CHARGE_UP_STATE:
                LizunAudio.playSound(LizunAudio.HIT_TOP_SOUND);
                current_state = HIT_UP_ANIMATION;
                playAnimation(R.anim.hit_celling_animation);
                waitAnimationToEnd(HIT_UP_ANIMATION_LENGTH);
                return;
            case HIT_SIDE_ANIMATION:
                turn();
                break;
            case FOUND_STATE:
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

    private class AnimationEndListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animationEndCallback();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    }
}
