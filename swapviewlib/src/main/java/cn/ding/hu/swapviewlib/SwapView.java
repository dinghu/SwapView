package cn.ding.hu.swapviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

/**
 * Created by harry.ding on 2018/12/21.
 */

public class SwapView extends FrameLayout {

    private int swaptime;
    private int showChilIndex = 0;
    private int exitAnimation = 0;
    private int enterAnimation = 0;
    private Animation exitAni;
    private Animation enterAni;
    View showView;
    View preview;


    private Handler handler;

    public SwapView(@NonNull Context context) {
        this(context, null);
    }

    public SwapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (showView != null) {
            showView.clearAnimation();
        }
        if (preview != null) {
            preview.clearAnimation();
        }
        if (handler != null) {
            handler.removeMessages(1);
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (showView != null) {
            showView.clearAnimation();
            showView.setVisibility(VISIBLE);
        }
        if (preview != null) {
            preview.clearAnimation();
            preview.setVisibility(View.GONE);
        }
        if (handler != null) {
            handler.removeMessages(1);
            handler.sendEmptyMessageDelayed(1, swaptime);
        }
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SwapView, defStyleAttr, 0);
        swaptime = ta.getInt(R.styleable.SwapView_swaptime, 2000);
        exitAnimation = ta.getResourceId(R.styleable.SwapView_exitAnimation, 0);
        enterAnimation = ta.getResourceId(R.styleable.SwapView_enterAnimation, 0);
        if (exitAnimation != 0) {
            exitAni = AnimationUtils.loadAnimation(getContext(), exitAnimation);
            swaptime += exitAni.getDuration();
        }
        if (enterAnimation != 0) {
            enterAni = AnimationUtils.loadAnimation(getContext(), enterAnimation);
            swaptime += enterAni.getDuration();
        }
        ta.recycle();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (getVisibility() == VISIBLE) {
                    int preShowIndex = showChilIndex;
                    showChilIndex++;
                    if (showChilIndex == getChildCount()) {
                        showChilIndex = 0;
                    }
                    showView = getChildAt(showChilIndex);
                    preview = getChildAt(preShowIndex);
                    if (exitAnimation != 0 && enterAnimation != 0 && exitAni != null && enterAni != null) {
                        //exit
                        preview.clearAnimation();
                        exitAni.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                preview.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        showView.setVisibility(View.VISIBLE);
                        //enter
                        showView.clearAnimation();
                        preview.startAnimation(exitAni);
                        showView.startAnimation(enterAni);
                        handler.removeMessages(1);
                        sendEmptyMessageDelayed(1, swaptime);
                    } else {
                        preview.setVisibility(View.GONE);
                        showView.setVisibility(View.VISIBLE);
                    }

                } else {
                    showChilIndex = 0;
                    handler.removeMessages(1);
                }
            }
        };
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() > 1) {
            showChilIndex = 0;
            for (int i = 0; i < getChildCount(); i++) {
                getChildAt(i).setVisibility(showChilIndex == i ? VISIBLE : View.GONE);
            }
            handler.removeMessages(1);
            handler.sendEmptyMessageDelayed(1, swaptime);
        }
    }
}
