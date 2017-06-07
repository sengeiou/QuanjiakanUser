package com.androidquanjiakan.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.quanjiakan.main.R;


/**
 * Created by Gin on 2017/2/16.
 */

public class StepArcView extends View {


    /**
     * 圆弧的宽度
     */
    private float borderWidth = 38f;

    /**
     * 里面圆弧的宽度
     */
    private float lindeWidth = 5f;
    /**
     * 画步数的数值的字体大小
     */
    private float numberTextSize = 0;

    /**
     * 画总步数的数值的字体大小
     */
    private float totalTextSize=22f;
    /**
     * 步数
     */
    private String stepNumber = "0";

    /**
     * 总步数
     */
    private String stepTotalNumber = "0";
    /**
     * 开始绘制圆弧的角度
     */
   // private float startAngle = 135;
    private float startAngle = 270;
    /**
     * 终点对应的角度和起始点对应的角度的夹角
     */
   // private float angleLength = 270;
    private float angleLength = 360;
    /**
     * 所要绘制的当前步数的红色圆弧终点到起点的夹角
     */
    private float currentAngleLength = 0;
    /**
     * 动画时长
     */
    private int animationLength = 3000;

    private OnClickListener listener;
    private float centerX;

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {
        void click();
    }


    int movexLength;
    int moveYLength;
    boolean isMoving;
    int stateX;
    int stateY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int)event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                movexLength=x;
                moveYLength=y;
                break;
            case MotionEvent.ACTION_MOVE:
                isMoving=true;
                if(x>=centerX-getStringWidth(totalTextSize)-10&&x<=centerX+getStringWidth(totalTextSize)+10&&y>=getHeight() / 2 + getFontHeight(numberTextSize)-20&&y<=getHeight() / 2 + getStringHeight(totalTextSize) + getFontHeight(numberTextSize)+20) {
                    stateX=x;
                    stateY=y;
                }else {
                    stateX=0;
                    stateY=0;
                }

                break;
            case MotionEvent.ACTION_UP:
                movexLength=(stateX-movexLength);
                moveYLength=(stateY-moveYLength);
                if(Math.abs(movexLength)<10&& Math.abs(moveYLength)<10) {
                    if(listener!=null) {
                        listener.click();
                    }
                }

                break;
        }


        //return super.onTouchEvent(event);
        return true;
    }

    public StepArcView(Context context) {
        super(context);

    }

    public StepArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StepArcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**中心点的x坐标*/
        centerX = (getWidth()) / 2;
        /**指定圆弧的外轮廓矩形区域*/
        RectF rectF = new RectF(0 + borderWidth, borderWidth, 2 * centerX - borderWidth, 2 * centerX - borderWidth);

        /**【第一步】绘制整体的总步数圆弧*/
        drawArcTotal(canvas, rectF);
        /**【第二步】绘制当前进度的步数圆弧*/
        drawArcCurrent(canvas, rectF);
        /**【第三步】绘制箭头*/
        drawArrow(canvas,rectF);
        /**【第四步】绘制白色圆圈*/
        drawRouteWhite(canvas);
        /**【第五步】绘制里面灰色圆环*/
        drawArcLine(canvas);
        /**【第六步】绘制当前进度的步数*/
        drawTextNumber(canvas, centerX);
        /**【第七步】绘制当前进度的步字*/
        drawTextStep(canvas, centerX);
        /**【第八部步】绘制"目标: "+总步数 字*/
        drawTextStepString(canvas, centerX);

    }

    private void drawRouteWhite(Canvas canvas) {
        Paint paintLine = new Paint();
        paintLine.setStrokeJoin(Paint.Join.ROUND);
        paintLine.setStrokeCap(Paint.Cap.ROUND);//圆角弧度
        paintLine.setStyle(Paint.Style.FILL);//设置填充样式
        paintLine.setAntiAlias(true);//抗锯齿功能
        //paintLine.setStrokeWidth(lindeWidth);//设置画笔宽度
        paintLine.setColor(getResources().getColor(R.color.white));//设置画笔颜色
        float centerX = (getWidth()) / 2;
        RectF rectF = new RectF(borderWidth*3/2, borderWidth*3/2, 2 * centerX - borderWidth*3/2, 2 * centerX -borderWidth*3/2);
        canvas.drawArc(rectF, startAngle, angleLength, false, paintLine);

    }


    /**
     * 1.绘制总步数的圆弧
     *
     * @param canvas 画笔
     * @param rectF  参考的矩形
     */
    private void drawArcTotal(Canvas canvas, RectF rectF) {
        Paint paint = new Paint();
        /** 默认画笔颜色   */
        paint.setColor(getResources().getColor(R.color.totalStep));
        /** 结合处为圆弧*/
        paint.setStrokeJoin(Paint.Join.ROUND);
        /** 设置画笔的样式 Paint.Cap.Round ,Cap.SQUARE等分别为圆形、方形*/
        paint.setStrokeCap(Paint.Cap.ROUND);
        /** 设置画笔的填充样式 Paint.Style.FILL  :填充内部;Paint.Style.FILL_AND_STROKE  ：填充内部和描边;  Paint.Style.STROKE  ：仅描边*/
        paint.setStyle(Paint.Style.STROKE);
        /**抗锯齿功能*/
        paint.setAntiAlias(true);
        /**设置画笔宽度*/
        paint.setStrokeWidth(borderWidth);

        /**绘制圆弧的方法
         * drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint)//画弧，
         参数一是RectF对象，一个矩形区域椭圆形的界限用于定义在形状、大小、电弧，
         参数二是起始角(度)在电弧的开始，圆弧起始角度，单位为度。
         参数三圆弧扫过的角度，顺时针方向，单位为度,从右中间开始为零度。
         参数四是如果这是true(真)的话,在绘制圆弧时将圆心包括在内，通常用来绘制扇形；如果它是false(假)这将是一个弧线,
         参数五是Paint对象；
         */
        canvas.drawArc(rectF, startAngle, angleLength, false, paint);

    }

    /**
     * 2.绘制当前步数的圆弧
     */
    private void drawArcCurrent(Canvas canvas, RectF rectF) {
        Paint paintCurrent = new Paint();
        paintCurrent.setStrokeJoin(Paint.Join.ROUND);
        paintCurrent.setStrokeCap(Paint.Cap.ROUND);//圆角弧度
        paintCurrent.setStyle(Paint.Style.STROKE);//设置填充样式
        paintCurrent.setAntiAlias(true);//抗锯齿功能
        paintCurrent.setStrokeWidth(borderWidth);//设置画笔宽度
        paintCurrent.setColor(getResources().getColor(R.color.currentStep));//设置画笔颜色
        canvas.drawArc(rectF, startAngle, currentAngleLength, false, paintCurrent);
    }

    /**
     * 3.绘制箭头
     */
    private void drawArrow(Canvas canvas, RectF rectF) {
        Paint paintArrow = new Paint();
        paintArrow.setAntiAlias(true);//抗锯齿功能
        Resources res=getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.steps_ico_orward);
        canvas.drawBitmap(bitmap,rectF.width()/2+20,rectF.top-12,paintArrow);
    }

    /**
     * 4.里面圆环
     */

    private void drawArcLine(Canvas canvas){
        Paint paintLine = new Paint();
        paintLine.setStrokeJoin(Paint.Join.ROUND);
        paintLine.setStrokeCap(Paint.Cap.ROUND);//圆角弧度
        paintLine.setStyle(Paint.Style.STROKE);//设置填充样式
        paintLine.setAntiAlias(true);//抗锯齿功能
        paintLine.setStrokeWidth(lindeWidth);//设置画笔宽度
        paintLine.setColor(getResources().getColor(R.color.lineArc));//设置画笔颜色
        float centerX = (getWidth()) / 2;
        RectF rectF = new RectF(borderWidth *2+5, borderWidth*2 + 5, 2 * centerX - 2*borderWidth - 5, 2 * centerX -2* borderWidth - 5);
        canvas.drawArc(rectF, startAngle, angleLength, false, paintLine);


    }

    /**
     * 5.圆环中心的步数
     */
    private void drawTextNumber(Canvas canvas, float centerX) {
        Paint vTextPaint = new Paint();
        vTextPaint.setTextAlign(Paint.Align.CENTER);
        vTextPaint.setAntiAlias(true);//抗锯齿功能
        vTextPaint.setTextSize(numberTextSize);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
        vTextPaint.setTypeface(font);//字体风格
        vTextPaint.setColor(getResources().getColor(R.color.step_current));
        Rect bounds_Number = new Rect();
        vTextPaint.getTextBounds(stepNumber, 0, stepNumber.length(), bounds_Number);
        canvas.drawText(stepNumber, centerX, getHeight() / 2 + bounds_Number.height() / 2-30, vTextPaint);

    }
    /**
     * 6.圆环中心的步字
     */

    private void drawTextStep(Canvas canvas, float centerX){
        Paint vTextPaint = new Paint();
        vTextPaint.setTextAlign(Paint.Align.CENTER);
        vTextPaint.setAntiAlias(true);//抗锯齿功能
        vTextPaint.setTextSize(dipToPx(16));
        vTextPaint.setColor(getResources().getColor(R.color.step_current));
        String stepString ="步";
        Rect rect = new Rect();
        vTextPaint.getTextBounds(stepString,0,stepString.length(),rect);
        canvas.drawText(stepString, centerX+getFontWidth(numberTextSize)/2+35, getHeight() / 2 +getFontHeight(numberTextSize)/2-30, vTextPaint);

    }



    /**
     * 7.圆环中心[目标:]的文字
     */
    private void drawTextStepString(Canvas canvas, float centerX) {
        Paint vTextPaint = new Paint();
        vTextPaint.setTextSize(dipToPx(totalTextSize));
        vTextPaint.setTextAlign(Paint.Align.CENTER);
        vTextPaint.setAntiAlias(true);//抗锯齿功能
        //vTextPaint.setColor(getResources().getColor(R.color.totalStepString));
        vTextPaint.setColor(getResources().getColor(R.color.step_target));
        String stepString = "目标: "+stepTotalNumber;
        Rect bounds = new Rect();
        vTextPaint.getTextBounds(stepString, 0, stepString.length(), bounds);
        canvas.drawText(stepString, centerX, getHeight() / 2 + bounds.height() + getFontHeight(numberTextSize)-20, vTextPaint);

    }



   /* *//**
     * 8.圆环中心的总步数
     *//*
    private void drawTextStepTotal(Canvas canvas, float centerX) {
        Paint vTextPaint = new Paint();
        vTextPaint.setTextSize(dipToPx(totalTextSize));
        vTextPaint.setTextAlign(Paint.Align.CENTER);
        vTextPaint.setAntiAlias(true);//抗锯齿功能
        vTextPaint.setColor(getResources().getColor(R.color.totalStepString));
        Rect bounds = new Rect();
        vTextPaint.getTextBounds(stepTotalNumber, 0, stepTotalNumber.length(), bounds);
        canvas.drawText(stepTotalNumber,centerX+getFontWidth(numberTextSize)/2+getStringWidth(totalTextSize)*2+24,getHeight() / 2 + bounds.height() + getFontHeight(numberTextSize)+getStringHeight(totalTextSize)/2-20,vTextPaint);

    }*/

    /**
     * 获取当前步数的数字的高度
     *
     * @param fontSize 字体大小
     * @return 字体高度
     */
    public int getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Rect bounds_Number = new Rect();
        paint.getTextBounds(stepNumber, 0, stepNumber.length(), bounds_Number);
        return bounds_Number.height();

    }

    //当前步数的数字的长度
    public int getFontWidth(float fontSize){
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Rect bounds_Number = new Rect();
        paint.getTextBounds(stepNumber, 0, stepNumber.length(), bounds_Number);
        return bounds_Number.width();
    }

    //目标:+总步数的长度
    public int getStringWidth(float fontSize){
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Rect bounds_Number = new Rect();
        String str="目标: "+stepTotalNumber;
        paint.getTextBounds(str, 0,str.length(), bounds_Number);
        return bounds_Number.width();
    }

    //目标: +总步数的字高度
    public int getStringHeight(float fontSize){
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Rect bounds_Number = new Rect();
        String str="目标: "+stepTotalNumber;
        paint.getTextBounds(str, 0,str.length(), bounds_Number);
        return bounds_Number.height();
    }

   /* //总步数的长度
    public int getTotalWidth(float fontSize){
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Rect bounds_Number = new Rect();
        paint.getTextBounds(stepTotalNumber,0, stepTotalNumber.length(), bounds_Number);
        return bounds_Number.width();
    }*/

    /**
     * dip 转换成px
     *
     * @param dip
     * @return
     */

    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 所走的步数进度
     *
     * @param totalStepNum  设置的步数
     * @param currentCounts 所走步数
     */
    public void setCurrentCount(int totalStepNum, int currentCounts) {
        stepNumber = currentCounts + "";
        stepTotalNumber=totalStepNum+"";
        setTextSize(currentCounts);
        /**如果当前走的步数超过总步数则圆弧还是270度，不能成为圆*/
        if (currentCounts > totalStepNum) {
            currentCounts = totalStepNum;
        }
        /**所走步数占用总共步数的百分比*/
        float scale = (float) currentCounts / totalStepNum;
        /**换算成弧度最后要到达的角度的长度-->弧长*/
        float currentAngleLength = scale * angleLength;
        /**开始执行动画*/
        setAnimation(0, currentAngleLength, animationLength);
    }

    /**
     * 为进度设置动画
     * ValueAnimator是整个属性动画机制当中最核心的一个类，属性动画的运行机制是通过不断地对值进行操作来实现的，
     * 而初始值和结束值之间的动画过渡就是由ValueAnimator这个类来负责计算的。
     * 它的内部使用一种时间循环的机制来计算值与值之间的动画过渡，
     * 我们只需要将初始值和结束值提供给ValueAnimator，并且告诉它动画所需运行的时长，
     * 那么ValueAnimator就会自动帮我们完成从初始值平滑地过渡到结束值这样的效果。
     *
     * @param last
     * @param current
     */
    private void setAnimation(float last, float current, int length) {
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(last, current);
        progressAnimator.setDuration(length);
        progressAnimator.setTarget(currentAngleLength);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAngleLength = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        progressAnimator.start();
    }

    /**
     * 设置文本大小,防止步数特别大之后放不下，将字体大小动态设置
     *
     * @param num
     */
    public void setTextSize(int num) {
        String s = String.valueOf(num);
        int length = s.length();
        if (length <= 4) {
            numberTextSize = dipToPx(50);
        } else if (length > 4 && length <= 6) {
            numberTextSize = dipToPx(40);
        } else if (length > 6 && length <= 8) {
            numberTextSize = dipToPx(30);
        } else if (length > 8) {
            numberTextSize = dipToPx(25);
        }
    }

}




