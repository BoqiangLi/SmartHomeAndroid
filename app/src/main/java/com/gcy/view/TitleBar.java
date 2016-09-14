package com.gcy.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import activity.gcy.com.demo.R;


/**
 * Created by Mr.G on 2016/6/4.
 */
public class TitleBar extends FrameLayout {
    private boolean isBack = false;

    private TextView mTitleTv = null;
    /**
     * 左按钮
     */
    private ActionView leftIv = null;
    /**
     * 右按钮
     */
    private ImageView rightIv = null;
    /**
     * 左边文字
     */

    /**
     * 右边文字
     */
    private TextView rightTv = null;
    /**
     * 左边按钮
     */
    private LinearLayout leftContainer;
    /**
     * 右边按钮
     */
    private LinearLayout rightContainer;

    private RelativeLayout container;

    private OnTitleBarClickListener onTitleBarClickListener;

    private String mLeftButtonName = "";
    private String mRightButtonName = "";

    public void setOnTitleBarClickListener(OnTitleBarClickListener onTitleBarClickListener) {
        this.onTitleBarClickListener = onTitleBarClickListener;
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initUI(context);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context);
    }

    public TitleBar(Context context) {
        super(context);
        initUI(context);
    }

    public void setActionView(){
        leftIv.setAction(new BackAction(),ActionView.ROTATE_COUNTER_CLOCKWISE);

    }

    public String getActionView(){
        return leftIv.getAction().toString();
    }

    private void initUI(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_titlebar, this, true);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        leftIv = (ActionView) findViewById(R.id.left_iv);
        rightIv = (ImageView) findViewById(R.id.right_iv);

        rightTv = (TextView) findViewById(R.id.right_tv);
        leftContainer = (LinearLayout) findViewById(R.id.left_container);
        rightContainer = (LinearLayout) findViewById(R.id.right_container);
        container = (RelativeLayout) findViewById(R.id.container);

    }


    public ImageView getRightIv() {
        return rightIv;
    }

    public void setIsBack(boolean flag){
        this.isBack = flag;
    }

    public void initTitleBarInfo(String title, int leftBtnImageId, int rightBtnImageId, String leftBtnText, String rightBtnText) {

        //设置标题
        mTitleTv.setText(title);

        //设置左文字


        //设置右文字
        rightTv.setText(rightBtnText);

        //左图片


        //右图片
        if (rightBtnImageId != -1) {
            rightIv.setImageResource(rightBtnImageId);
            rightIv.setVisibility(VISIBLE);
            rightTv.setVisibility(GONE);
        }

        if (leftBtnImageId == -1 && TextUtils.isEmpty(leftBtnText)) {
            leftContainer.setEnabled(false);
        } else {
            leftContainer.setEnabled(true);
        }

        if (rightBtnImageId == -1 && TextUtils.isEmpty(rightBtnText)) {
            rightContainer.setEnabled(false);
        } else {
            rightContainer.setEnabled(true);
        }

        leftContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTitleBarClickListener != null) {
                    if(!isBack)
                    if(leftIv.getAction().toString().equals("drawer")){
                        leftIv.setAction(new BackAction(),ActionView.ROTATE_COUNTER_CLOCKWISE);
                    }
                    else
                        leftIv.setAction(new DrawerAction(),ActionView.ROTATE_COUNTER_CLOCKWISE);
                    onTitleBarClickListener.onLeftButtonClick(v);
                }
            }
        });

        rightContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTitleBarClickListener != null) {
                    onTitleBarClickListener.onRightButtonClick(v);
                }
            }
        });

    }

    /**
     * 重新设置 titleBar 不检查 字符串 是否为空
     *
     * @param title
     * @param leftBtnImageId
     * @param rightBtnImageId
     * @param leftBtnText
     * @param rightBtnText
     */
    public void reSetTitleBarWidget(String title, int leftBtnImageId, int rightBtnImageId, String leftBtnText, String rightBtnText) {
        //设置标题
        mTitleTv.setText(title);
        //设置左文字

        //设置右文字
        rightTv.setText(rightBtnText);

        //左图片


        //右图片
        if (rightBtnImageId != -1) {
            rightIv.setImageResource(rightBtnImageId);
            rightIv.setVisibility(VISIBLE);
            rightTv.setVisibility(GONE);
        } else {
            rightIv.setVisibility(GONE);
        }


        setLeftContainerClickAble(TextUtils.isEmpty(leftBtnText) && leftBtnImageId == -1 ? false : true);

        setRightContainerClickAble(TextUtils.isEmpty(rightBtnText) && rightBtnImageId == -1 ? false : true);


    }


    public void setLeftContainerClickAble(boolean able) {
        leftContainer.setEnabled(able);
        leftContainer.setClickable(able);
    }

    public void setRightContainerClickAble(boolean able) {
        rightContainer.setEnabled(able);
        rightContainer.setClickable(able);
    }


    public void setRightTv(String rightBtnText, int rightBtnImageId) {
        rightTv.setText(rightBtnText);
        rightTv.setVisibility(VISIBLE);
        setRightContainerClickAble(TextUtils.isEmpty(rightBtnText) && rightBtnImageId == -1 ? false : true);

    }

    public String getRightTv() {
        return rightTv.getText().toString();
    }

    public void setTitleBarBackgroundColor(int colorRes) {

    }

    public void updateTitle(String newTitle) {
        mTitleTv.setText(newTitle);
    }

    public String getTitle() {
        return mTitleTv.getText().toString();
    }


    public interface OnTitleBarClickListener {
        public void onLeftButtonClick(View v);

        public void onRightButtonClick(View v);
    }


}
