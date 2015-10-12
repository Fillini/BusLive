package fill.com.buslive.fragments.views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import fill.com.buslive.utils.L;

/**
 * Created by Fill on 12.10.2015.
 */
public class DottedProgressBar extends Drawable implements Animatable {


    int mAlpha;
    boolean mIsRunning = false;
    long mStartTime;

    private static final long FRAME_DURATION = 1000 / 60; /*частота кадров  60 кадров*/

    private static final long ANIMATION_DURATION = 2000; /*длительность анимации*/

    HesitateInterpolator interpolator = new HesitateInterpolator();

    private long duration;


    private Dot dot;
    private Dot dot2;
    private Dot dot3;
    private Dot dot4;


    public DottedProgressBar() {

        dot = new Dot();
        dot.setColor(Color.WHITE);
        dot.setRadius(10);
        dot.setStartOffset(0);

        dot2 = new Dot();
        dot2.setColor(Color.WHITE);
        dot2.setRadius(10);
        dot2.setStartOffset(100);

        dot3 = new Dot();
        dot3.setColor(Color.WHITE);
        dot3.setRadius(10);
        dot3.setStartOffset(200);

        dot4 = new Dot();
        dot4.setColor(Color.WHITE);
        dot4.setRadius(10);
        dot4.setStartOffset(300);

    }

    @Override
    public void draw(Canvas canvas) {

        Rect rect = canvas.getClipBounds();

        dot.setY(rect.height() / 2);
        dot2.setY(rect.height() / 2);
        dot3.setY(rect.height() / 2);
        dot4.setY(rect.height() / 2);


        float animation_percent = Math.max(duration*100/ANIMATION_DURATION , 0);
        float animation_percent2 = Math.max( (duration-dot2.getStartOffset())*100/ANIMATION_DURATION, 0);
        float animation_percent3 = Math.max( (duration-dot3.getStartOffset())*100/ANIMATION_DURATION, 0);
        float animation_percent4 = Math.max( (duration-dot4.getStartOffset())*100/ANIMATION_DURATION, 0);

        dot.setX((rect.width() * interpolator.getInterpolation(animation_percent / 100)) - dot.getRadius());

        dot2.setX(((rect.width() - dot2.getRadius()) * interpolator.getInterpolation(animation_percent2 / 100)) - dot2.getRadius());
        dot3.setX(((rect.width() - dot3.getRadius() * 2) * interpolator.getInterpolation(animation_percent3 / 100)) - dot3.getRadius());
        dot4.setX(((rect.width() - dot4.getRadius() * 3) * interpolator.getInterpolation(animation_percent4 / 100)) - dot4.getRadius());


        Paint dotPaint = new Paint();
        dotPaint.setFilterBitmap(true);
        dotPaint.setColor(dot.getColor());

        canvas.drawCircle(dot.getX(), dot.getY(), dot.getRadius(), dotPaint);

        canvas.drawCircle(dot2.getX(), dot2.getY(), dot2.getRadius(), dotPaint);
        canvas.drawCircle(dot3.getX(), dot3.getY(), dot3.getRadius(), dotPaint);
        canvas.drawCircle(dot4.getX(), dot4.getY(), dot4.getRadius(), dotPaint);



    }

    @Override
    public void setAlpha(int alpha) {
        throw new UnsupportedOperationException("setAlpha() not supported");
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        throw new UnsupportedOperationException("setColorFilter() not supported");
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }


    @Override
    public void start() {
        if (!isRunning()) {
            mIsRunning = true;
            mStartTime = AnimationUtils.currentAnimationTimeMillis();
            scheduleSelf(mUpdater, AnimationUtils.currentAnimationTimeMillis() + FRAME_DURATION);
            invalidateSelf();
        }

    }

    @Override
    public void stop() {
        if (isRunning()) {
            unscheduleSelf(mUpdater);
            mIsRunning = false;
        }
    }

    @Override
    public boolean isRunning() {
        return mIsRunning;
    }


    Runnable mUpdater = new Runnable() {
        @Override
        public void run() {

            long now = AnimationUtils.currentAnimationTimeMillis();
            duration = now - mStartTime;
            if(duration >= ANIMATION_DURATION){
                mStartTime = now;
            }

            scheduleSelf(mUpdater, AnimationUtils.currentAnimationTimeMillis() + FRAME_DURATION);
            invalidateSelf();
        }
    };




    static class Dot{

        float radius;
        int color;

        float x;
        float y;

        float startOffset;

        public float getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getStartOffset() {
            return startOffset;
        }

        public void setStartOffset(float startOffset) {
            this.startOffset = startOffset;
        }
    }


    public class HesitateInterpolator implements Interpolator {
        public HesitateInterpolator() {}
        public float getInterpolation(float t) {
            float x=3.0f*t-1.0f;
            return 0.5f*(x*x*x + 1.0f);
        }
    }


}
