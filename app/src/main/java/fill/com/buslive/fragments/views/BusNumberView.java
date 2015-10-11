package fill.com.buslive.fragments.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.ImageView;

import fill.com.buslive.utils.L;

/**
 * Created by Fill on 10.10.2015.
 */
public class BusNumberView extends ImageView {

    private String route_number="NaN";

    Rect clip_rect = new Rect();
    Paint textPaint = new Paint();
    Rect bounds = new Rect();
    Paint stkPaint = new Paint();

    public BusNumberView(Context context) {
        super(context);
    }

    public BusNumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRoute_number(String route_number) {
        this.route_number = route_number;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.getClipBounds(clip_rect);

        float textSize = new Float(clip_rect.width()/2.2);
        if(route_number.length()>3){
            textSize /= 1.3;
        }

        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setFilterBitmap(true);
        textPaint.setFlags(Paint.EMBEDDED_BITMAP_TEXT_FLAG);

        textPaint.getTextBounds(route_number, 0, route_number.length(), bounds);
        int x = (clip_rect.width() / 2) - (bounds.width() / 2) - (route_number.length() / 2);
        float y = (clip_rect.height() / 2) - (bounds.height() / 2) - textPaint.descent() - textPaint.ascent();
        canvas.drawText(route_number, x, y, textPaint);

        stkPaint.setStyle(Paint.Style.STROKE);
        stkPaint.setTextSize(textSize);
        stkPaint.setStrokeWidth(1);
        stkPaint.setColor(Color.WHITE);
        stkPaint.setAntiAlias(true);
        stkPaint.setFilterBitmap(true);
        canvas.drawText(route_number, x, y, stkPaint);



    }
}
