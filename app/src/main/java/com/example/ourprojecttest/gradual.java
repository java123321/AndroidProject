package com.example.ourprojecttest;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class gradual extends View {

    private int animatedValue;
    private int colorEnd;
    private int colorStart;
    private int animatedValue1;

    public gradual(Context context) {
        super(context);
        init();
        System.out.println(111);
        requestLayout();

    }

    public gradual(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        System.out.println(222);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();
    }

    public void init() {
        postInvalidate();
        ValueAnimator animator=ValueAnimator.ofInt(0,64);
        animator.setDuration(5000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedValue = (int) animation.getAnimatedValue();
                if (animatedValue<64) {
                    colorStart = Color.rgb(0, 255-animatedValue, 255);
                    colorEnd = Color.rgb(0+animatedValue, 191+animatedValue, 255);
                }else if (animatedValue==64){
                    ValueAnimator animator1=ValueAnimator.ofInt(0,90);
                    animator1.setDuration(500);
                    animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            animatedValue1 = (int) animation.getAnimatedValue();
                            colorStart = Color.rgb(0+animatedValue1,191-animatedValue1, 255);
                            colorEnd = Color.rgb(64+animatedValue1,255-animatedValue1, 255);
                            if (animatedValue1==90){
                                ValueAnimator animator2=ValueAnimator.ofInt(0,70);
                                animator2.setDuration(500);
                                animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        int animatedValue2 = (int) animation.getAnimatedValue();
                                        colorStart = Color.rgb(90,101+animatedValue2,255);
                                        colorEnd = Color.rgb(154,165+animatedValue2,255);
                                        invalidate();
                                    }
                                });
                                animator2.start();
                            }
                            invalidate();
                        }
                    });
                    animator1.start();
                }
                invalidate();
            }
        });
        animator.start();
    }

    public gradual(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        System.out.println(333);
        requestLayout();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取View的宽高
        int width = getWidth();
        int height = getHeight();

        Paint paint = new Paint();
        LinearGradient backGradient = new LinearGradient(width, 0, 0, 0, new int[]{colorStart,colorEnd}, new float[]{0,1f}, Shader.TileMode.CLAMP);
        paint.setShader(backGradient);
        canvas.drawRect(0, 0, width, height, paint);
    }
}