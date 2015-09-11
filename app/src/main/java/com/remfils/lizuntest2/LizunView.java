package com.remfils.lizuntest2;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

public class LizunView extends SurfaceView {
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

    final private float SLIMER_ALPHA = 0.8f;
    final private int SLIMER_CHARGE_ACCELERATION = 2;
    final private int SLIMER_FLIGHT_DURATION = 300;

    static final private int HIT_UP_ANIMATION_LENGTH = 1550;
    static final private int HIT_SIDE_ANIMATION_LENGTH = 920;
    static final private int THINKING_ANIMATION_LENGTH = 2500;

    final private float TIME_COEF;

    private int screen_width, screen_height;

    private float x = -100, y;
    private int current_state = BEGIN_STATE;

    public LizunView(Context context, WindowManager window_manager) {
        super(context);

        Display d = window_manager.getDefaultDisplay();
        Point size = new Point();
        d.getSize(size);
        screen_width = size.x;
        screen_height = size.y;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WIDTH,HEIGHT);
        setLayoutParams(params);

        setZOrderOnTop(true);
        SurfaceHolder holder = getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);

        y = screen_height / 2;

        TIME_COEF = 2500.f / screen_width;

        updatePosition();

        setAlpha(SLIMER_ALPHA);
    }

    public void playFirstState() {
        playAnimation(R.anim.search);

        x = -100;
        y = screen_height / 2 - HEIGHT / 2;
        updatePosition();

        TranslateAnimation a = moveTo(screen_width / 2, screen_height / 2);

        a.setAnimationListener(new AnimationEndListener());
    }

    public void playSecondState() {
        current_state = FOUND_STATE;
    }

    private void setState(int state) {
        current_state = state;
    }

    private void update () {
        updatePosition();
        desideWhatToDo();
        updateAnimation();
    }

    private void updatePosition() {
        setX(x);
        setY(y);
    }

    private void desideWhatToDo() {
        double chance = Math.random();

        if ( chance < 0.3 ) current_state = CHARGE_SIDE_WALL;
        else if ( chance < 0.5 ) current_state = THINKING_STATE;
        else if ( chance < 0.7 ) current_state = CHARGE_UP_STATE;
        else current_state = SEARCH_STATE;
    }

    private void updateAnimation() {
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
                a.setInterpolator(new AccelerateInterpolator(SLIMER_CHARGE_ACCELERATION));
                a.setDuration(SLIMER_FLIGHT_DURATION);
                break;
            case CHARGE_UP_STATE:
                playAnimation(R.anim.hit_up_charge);
                a = moveTo(getX() + WIDTH / 2, HEIGHT / 2);
                a.setInterpolator(new AccelerateInterpolator(SLIMER_CHARGE_ACCELERATION));
                a.setDuration(SLIMER_FLIGHT_DURATION);
                break;
            case THINKING_STATE:
                playAnimation(R.anim.thinking);
                waitAnimationToEnd(THINKING_ANIMATION_LENGTH);
                break;
            default:
                //setState(SEARCH_STATE);
        }
    }

    private void moveRandom() {
        double end_x=0, end_y=0;

        end_x = ( screen_width - WIDTH ) * Math.random() + WIDTH / 2;
        end_y = ( screen_height - HEIGHT ) * Math.random() + HEIGHT / 2;

        if ( (getScaleX() > 0 && end_x < x + WIDTH / 2 ) || ( getScaleX() < 0 && end_x > x + WIDTH / 2 ) ) {
            turn();
        }

        moveTo((float) end_x, (float) end_y);
    }

    private TranslateAnimation moveTo(float to_x, float to_y) {
        double dx = x - to_x + WIDTH / 2;
        double dy = y - to_y + HEIGHT / 2;
        double dr = Math.sqrt(dx * dx + dy*dy);

        x = to_x - WIDTH / 2;
        y = to_y - HEIGHT / 2;

        TranslateAnimation a = new TranslateAnimation(0,x - getX() , 0,y - getY() );
        a.setDuration(1500);
        a.setInterpolator(new AccelerateDecelerateInterpolator());

        setAnimation(a);

        a.setAnimationListener(new AnimationEndListener());

        return a;
    }

    private void playAnimation(int anim_id) {
        setBackgroundResource(anim_id);

        AnimationDrawable a = (AnimationDrawable) getBackground();
        a.start();
    }

    private Animation waitAnimationToEnd (int time) {
        Animation a = moveTo(getX() + WIDTH / 2, getY() + HEIGHT / 2);
        a.setDuration(time);
        a.setInterpolator(new LinearInterpolator());
        return a;
    }

    private void turn() {
        setScaleX(getScaleX() * -1);
    }


    private class AnimationEndListener implements Animation.AnimationListener {
        public void onAnimationStart(Animation animation) {}
        public void onAnimationRepeat(Animation animation) {}
        public void onAnimationEnd(Animation animation) {
            if ( current_state == CHARGE_UP_STATE) {
                updatePosition();
                current_state = HIT_UP_ANIMATION;
                playAnimation(R.anim.hit_celling_animation);
                waitAnimationToEnd(HIT_UP_ANIMATION_LENGTH);
                return;
            }
            if ( current_state == CHARGE_SIDE_WALL) {
                updatePosition();
                current_state = HIT_SIDE_ANIMATION;
                playAnimation(R.anim.wall_hit_animation);
                waitAnimationToEnd(HIT_SIDE_ANIMATION_LENGTH);
                return;
            }
            if ( current_state == HIT_SIDE_ANIMATION) {
                turn();
            }

            update();
        }
    }
}
