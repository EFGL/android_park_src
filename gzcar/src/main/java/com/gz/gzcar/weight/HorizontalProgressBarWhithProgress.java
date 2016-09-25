package com.gz.gzcar.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.gz.gzcar.R;

/**
 * Created by Endeavor on 2016/9/6.
 * <p/>
 * 如何自定义控件?
 * 1.自定义属性申明与获取
 * 2.测量onMeasure
 * 3.布局onLayout(ViewGroup才有)
 * 4.绘制onDraw
 * 5.onTouchEvent 与用户交互
 * 5.onInterceptTouchEvent(ViewGroup)
 * 6.状态恢复与保存
 * <p/>
 * 补充:
 * onInterceptTouchEvent事件拦截:viewGroup的方法,拦截子view的一些事件,来自己操作,如scollView,监听手势滑动,拦截事件,再交给自己onTouchEvent处理
 * 状态恢复与保存:下载进度
 * 步骤:
 * 1.自定义属性申明与获取
 * a.分析需要的自定义属性
 * b.在res/values/attrs.xml定义申明
 * c.在View的构造方法中获取
 * 申明自定义属性的成员默认值(及单位转换方法)
 * 申明自定义属性的成员变量
 * 造方法中获取
 * d.在layout文件中使用
 * 2.实现测量onMeasure
 * a.重写onMeasure方法
 * b.测量
 */
public class HorizontalProgressBarWhithProgress extends ProgressBar {

    // 申明自定义属性默认值(有dp与sp,所以需要写转换方法)
    private static final int DEFAULT_TEXT_SIZE = 10;//sp
    private static final int DEFAULT_TEXT_COLOR = 0xFFFC00D1;
    private static final int DEFAULT_TEXT_OFFSET = 10;//dp

    private static final int DEFAULT_UNREACH_COLOR = 0XFFD3D6DA;
    private static final int DEFAULT_UNREACH_HEIGHT = 2;//dp

    private static final int DEFAULT_REACH_COLOR = DEFAULT_TEXT_COLOR;
    private static final int DEFAULT_REACH_HEIGHT = 2;//dp

    // 申明自定义属性的成员变量
    private int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private int mTextOffset = dp2px(DEFAULT_TEXT_OFFSET);
    private int mUnReachColor = DEFAULT_UNREACH_COLOR;
    private int mUnReachHeight = dp2px(DEFAULT_UNREACH_HEIGHT);
    private int mReachColor = DEFAULT_REACH_COLOR;
    private int mReachHeight = dp2px(DEFAULT_REACH_HEIGHT);

    private Paint mPaint = new Paint();

    private int mRealWidth;

    // 代码 new时调用
    public HorizontalProgressBarWhithProgress(Context context) {
        this(context, null);// 调自己两个参数的构造方法
    }

    // 布局中使用
    public HorizontalProgressBarWhithProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }


    public HorizontalProgressBarWhithProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        obtainStyledAttrs(attrs);// 获取自定义属性
    }

    // 测量   测量的三种模式都封装在 int heightMeasureSpec
    // 我们可以根据 int heightMeasureSpec 使用 MeasureSpec 类获取heightMeasureSpec的模式 大小
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthValue = MeasureSpec.getSize(widthMeasureSpec);

        int height=mesureHight(heightMeasureSpec);
    }

    private int mesureHight(int heightMeasureSpec) {

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode==MeasureSpec.EXACTLY){// easureSpec.EXACTLY 精确的值 (20dp或者match_parent)
            return height;// 如果用户给了明确的值,就直接返回
        }else{
            float descent = mPaint.descent();
        }

        return 0;
    }

    // 获取自定义属性
    private void obtainStyledAttrs(AttributeSet attrs) {

        // 获取attrs 文件的HorizontalProgressBarWhithProgress的属性集合
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBarWhithProgress);

        // 给属性赋值(默认值)
        mTextSize = (int) ta.getDimension(R.styleable.HorizontalProgressBarWhithProgress_progerss_text_size,mTextSize);
        mTextColor = (int) ta.getDimension(R.styleable.HorizontalProgressBarWhithProgress_progerss_text_color,mTextColor);
        mTextOffset = (int) ta.getDimension(R.styleable.HorizontalProgressBarWhithProgress_progerss_text_offset,mTextOffset);
        mUnReachColor = (int) ta.getDimension(R.styleable.HorizontalProgressBarWhithProgress_progerss_unreach_color,mUnReachColor);
        mUnReachHeight = (int) ta.getDimension(R.styleable.HorizontalProgressBarWhithProgress_progerss_unreach_hight,mUnReachHeight);
        mReachColor = (int) ta.getDimension(R.styleable.HorizontalProgressBarWhithProgress_progerss_reach_color,mReachColor);
        mReachHeight = (int) ta.getDimension(R.styleable.HorizontalProgressBarWhithProgress_progerss_reach_hight,mReachHeight);


        ta.recycle();

    }


    private int dp2px(int dpValue) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    private int sp2px(int spValue) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getResources().getDisplayMetrics());
    }
}
