package com.gcy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Mr.G on 2016/5/19.
 */
public class EnvironmentDataView extends View {

    private int mScreenHeight ;
    private int mScreenWidth;
    private String unit;
    private int unitLength;

    public EnvironmentDataView(Context context) {
        super(context);
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
        unit = "";
    }




    @Override
    protected void onDraw(Canvas canvas) {

        Paint mPaint = new Paint();
        Paint mPaintArc = new Paint();
        mPaint.setColor(Color.CYAN);
        mPaintArc.setColor(Color.RED);
        mPaintArc.setStrokeWidth(10);
        RectF oval = new RectF(0,0,mScreenHeight/2,mScreenWidth);
        canvas.drawText(unit,mScreenWidth/4,mScreenWidth/4,mPaint);
        canvas.drawArc(oval,0,0,false,mPaintArc);



        super.onDraw(canvas);

    }
    public void setUnit(String unit){
        this.unit = unit;

    }

    public void setUnitLength(int length){
        this.unitLength = length;


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mScreenHeight/2,mScreenWidth);

    }
}
