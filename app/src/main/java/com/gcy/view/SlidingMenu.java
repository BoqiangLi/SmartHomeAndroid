package com.gcy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import activity.gcy.com.demo.R;

/**
 * Created by Mr.G on 2016/5/11.
 */
public class SlidingMenu extends HorizontalScrollView {
    private LinearLayout mWapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;

    private int mScreenWidth;
    private int mMenuRightPadding = 100;  //dp

    private int mMenuWidth;

    private boolean once = false;

    private boolean isOpen;


    //未使用自定义属性时调用函两个参数的构造

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);



    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //获取自定义属性
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlidingMenu,defStyle,0);
        int n = a.getIndexCount();
        for(int i = 0;i<n;i++){
            int attr = a.getIndex(i);
            switch (attr){
                case R.styleable.SlidingMenu_rightPadding:
                    mMenuRightPadding = a.getDimensionPixelSize(attr,mMenuRightPadding);

            }
        }

        a.recycle();
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        mScreenWidth = outMetrics.widthPixels;
        //dp--->px
        mMenuRightPadding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,100,context.getResources().getDisplayMetrics());

    }

    public SlidingMenu(Context context) {
        this(context, null);
    }

    //设置自己的宽和高  设置子view的宽和高

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if(!once) {
            mWapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWapper.getChildAt(0);
            mContent = (ViewGroup) mWapper.getChildAt(1);

            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            mContent.getLayoutParams().width = mScreenWidth;
            //mWapper.getLayoutParams().width = mScreenWidth;
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }
    //设置偏移量将menu隐藏
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {



        super.onLayout(changed, l, t, r, b);

        if(changed){
            this.scrollTo(mMenuWidth,0);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action){
            case MotionEvent.ACTION_UP:
                int scrollX=getScrollX();
                if(scrollX>=mMenuWidth/3)
                {
                    this.smoothScrollTo(mMenuWidth,0);
                    isOpen = false;
                }
                if(scrollX<=mMenuWidth/3){
                    this.smoothScrollTo(0,0);
                    isOpen = true;
                }
                return true;


        }

        return super.onTouchEvent(ev);
    }

    public void openMenu(){
        if(isOpen)  return;
        this.smoothScrollTo(0,0);
        isOpen = true;
    }

    public void closeMenu(){
        if(!isOpen) return;
        this.smoothScrollTo(mMenuWidth,0);
        isOpen = false;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //调用属性动画
        //float scale = 1*1.0f/mMenuWidth;

        //mMenu.setTranslationX(mMenuWidth*scale);



    }
}
