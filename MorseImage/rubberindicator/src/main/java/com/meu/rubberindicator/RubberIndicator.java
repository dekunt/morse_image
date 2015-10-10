package com.meu.rubberindicator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class RubberIndicator extends RelativeLayout {

    private static final int SMALL_CIRCLE_COLOR = 0xFFDF8D81;
    private static final int LARGE_CIRCLE_COLOR = 0xFFAF3854;
    private static final int OUTER_CIRCLE_COLOR = 0xFF533456;
    private static final int SMALL_CIRCLE_SIZE = 6;
    private static final int LARGE_CIRCLE_SIZE = 10;
    private static final int OUTER_CIRCLE_SIZE = 20;
    private static final int CENTER_BAR_SIZE = 14;
    private static final int CENTER_BAR_PADDING_SIDE = 0;
    private static final int CIRCLE_DISTANCE = 18;

    private static final int CIRCLE_TYPE_SMALL = 0x00;
    private static final int CIRCLE_TYPE_LARGE = 0x01;
    private static final int CIRCLE_TYPE_OUTER = 0x02;

    /**
     * colors
     */
    private int mSmallCircleColor;
    private int mLargeCircleColor;
    private int mOuterCircleColor;

    /**
     * coordinate values
     */
    private int mSmallCircleSize;
    private int mLargeCircleSize;
    private int mOuterCircleSize;
    private int mCircleDistance;

    /**
     * views
     */
    private LinearLayout mContainer;
    private CircleView mLargeCircle;
    private CircleView mSmallCircle;
    private CircleView mOuterCircle;
    private List<CircleView> mCircleViews;

    /**
     * animations
     */
    private AnimatorSet mAnim;
    private PropertyValuesHolder mPvhScaleX;
    private PropertyValuesHolder mPvhScaleY;
    private PropertyValuesHolder mPvhScale;
    private PropertyValuesHolder mPvhRotation;

    private LinkedList<Integer> mPendingAnimations;

    /**
     * Movement Path
     */
    private Path mSmallCirclePath;

    /**
     * Helper values
     */
    private int mFocusPosition = -1;
    private int mBezierCurveAnchorDistance;

    public RubberIndicator(Context context) {
        super(context);
        init(null, 0);
    }

    public RubberIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RubberIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RubberIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        /** Get XML attributes */
        final TypedArray styledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.RubberIndicator, defStyle, 0);
        mSmallCircleColor = styledAttributes.getColor(R.styleable.RubberIndicator_smallCircleColor, SMALL_CIRCLE_COLOR);
        mLargeCircleColor = styledAttributes.getColor(R.styleable.RubberIndicator_largeCircleColor, LARGE_CIRCLE_COLOR);
        mOuterCircleColor = styledAttributes.getColor(R.styleable.RubberIndicator_outerCircleColor, OUTER_CIRCLE_COLOR);

        float density = getContext().getResources().getDisplayMetrics().density;
        mSmallCircleSize = styledAttributes.getDimensionPixelSize(R.styleable.RubberIndicator_smallCircleSize, (int)(density * SMALL_CIRCLE_SIZE));
        mLargeCircleSize = styledAttributes.getDimensionPixelSize(R.styleable.RubberIndicator_largeCircleSize, (int)(density * LARGE_CIRCLE_SIZE));
        mOuterCircleSize = styledAttributes.getDimensionPixelSize(R.styleable.RubberIndicator_outerCircleSize, (int)(density * OUTER_CIRCLE_SIZE));
        int mBarSize = styledAttributes.getDimensionPixelSize(R.styleable.RubberIndicator_centerBarSize, (int)(density * CENTER_BAR_SIZE));
        int mBarPaddingSide = styledAttributes.getDimensionPixelSize(R.styleable.RubberIndicator_centerBarPaddingSide, (int)(density * CENTER_BAR_PADDING_SIDE));
        mCircleDistance = styledAttributes.getDimensionPixelSize(R.styleable.RubberIndicator_circleDistance, (int)(density * CIRCLE_DISTANCE));
        styledAttributes.recycle();

        /** Initialize views */
        View rootView = inflate(getContext(), R.layout.rubber_indicator, this);
        mContainer = (LinearLayout) rootView.findViewById(R.id.container);
        mOuterCircle = (CircleView) rootView.findViewById(R.id.outer_circle);
        View containerWrapper = rootView.findViewById(R.id.container_wrapper);
        
        // Apply outer color to outerCircle and background shape
        mOuterCircle.setColor(mOuterCircleColor);
        mOuterCircle.setRadius(0.5f * mOuterCircleSize);
        mBarPaddingSide = mBarPaddingSide - (mCircleDistance - mOuterCircleSize) / 2;
        mContainer.setPadding(mBarPaddingSide, 0, mBarPaddingSide, 0);
        ViewGroup.LayoutParams layoutParams = containerWrapper.getLayoutParams();
        layoutParams.height = mBarSize;
        GradientDrawable shape = (GradientDrawable) containerWrapper.getBackground();
        shape.setColor(mOuterCircleColor);
        shape.setCornerRadius(0.5f * mBarSize);

        /** animators */
        mPvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 1, 0.7f, 1);
        mPvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 1, 0.7f, 1);
        mPvhScale = PropertyValuesHolder.ofFloat("scaleY", 1, 0.5f, 1);
        mPvhRotation = PropertyValuesHolder.ofFloat("rotation", 0);
        mBezierCurveAnchorDistance = (int) (0.7f * (mLargeCircleSize + (mOuterCircleSize - mLargeCircleSize) * 0.5f) / 2);

        mSmallCirclePath = new Path();

        mPendingAnimations = new LinkedList<>();

        /** circle view list */
        mCircleViews = new ArrayList<>();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        // Prevent crash if the count as not been set
        if(mLargeCircle != null){
            mOuterCircle.setCenter(mLargeCircle.getCenter().x, mOuterCircle.getCenter().y);
        }
    }

    public void setCount(int count) {
        if (mFocusPosition == -1) {
            mFocusPosition = 0;
        }
        setCount(count, mFocusPosition);
    }

    public void setCount(int count, int focusPos) {
        if (count < 2) {
            throw new IllegalArgumentException("count must be greater than 2");
        }

        if (focusPos >= count) {
            throw new IllegalArgumentException("focus position must be less than count");
        }

        /* Check if the number on indicator has changed since the last setCount to prevent duplicate */
        if(mCircleViews.size() != count) {
            mContainer.removeAllViews();
            mCircleViews.clear();

            int i = 0;
            for (; i < focusPos; i++) {
                addSmallCircle();
            }

            addLargeCircle();

            for (i = focusPos + 1; i < count; i++) {
                addSmallCircle();
            }
        }

        mFocusPosition = focusPos;
    }

    public void setCurrentPosition(int position) {
        if (mAnim != null && mAnim.isRunning()){
            mPendingAnimations.add(position);
            return;
        }
        move(position);
    }

    private void addSmallCircle() {
        CircleView smallCircle = createCircleView(CIRCLE_TYPE_SMALL);
        mCircleViews.add(smallCircle);
        mContainer.addView(smallCircle);
    }

    private void addLargeCircle() {
        mLargeCircle = createCircleView(CIRCLE_TYPE_LARGE);
        mCircleViews.add(mLargeCircle);
        mContainer.addView(mLargeCircle);
    }

    private CircleView createCircleView(int type) {
        CircleView circleView = new CircleView(getContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;

        switch (type) {
            case CIRCLE_TYPE_SMALL:
                params.height = params.width = mSmallCircleSize;
                circleView.setColor(mSmallCircleColor);
                circleView.setRadius(mSmallCircleSize * 0.5f);
                break;
            case CIRCLE_TYPE_LARGE:
                params.height = params.width = mLargeCircleSize;
                circleView.setColor(mLargeCircleColor);
                circleView.setRadius(mLargeCircleSize * 0.5f);
                break;
            case CIRCLE_TYPE_OUTER:
                params.height = params.width = mOuterCircleSize;
                circleView.setColor(mOuterCircleColor);
                circleView.setRadius(mOuterCircleSize * 0.5f);
                break;
        }

        params.leftMargin = params.rightMargin = (mCircleDistance - params.width) / 2;
        circleView.setLayoutParams(params);

        return circleView;
    }

    private void swapCircles(int currentPos, int nextPos) {
        CircleView circleView = mCircleViews.get(currentPos);
        mCircleViews.set(currentPos, mCircleViews.get(nextPos));
        mCircleViews.set(nextPos, circleView);
    }

    private void move(final int nextPos) {

        if (nextPos == mFocusPosition || nextPos < 0 || nextPos >= mCircleViews.size()) {
            if (!mPendingAnimations.isEmpty())
                move(mPendingAnimations.removeFirst());
            return;
        }
        mSmallCircle = mCircleViews.get(nextPos);

        // Calculate the new x coordinate for circles.
        float smallCircleX = mLargeCircle.getCenter().x - mSmallCircle.getWidth() / 2;
        float largeCircleX = mSmallCircle.getCenter().x - mLargeCircle.getWidth() / 2;
        float outerCircleX = mOuterCircle.getX() + largeCircleX - mLargeCircle.getX();

        // animations for large circle and outer circle.
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("x", mLargeCircle.getX(), largeCircleX);
        ObjectAnimator largeCircleAnim = ObjectAnimator.ofPropertyValuesHolder(
                mLargeCircle, pvhX, mPvhScaleX, mPvhScaleY);

        pvhX = PropertyValuesHolder.ofFloat("x", mOuterCircle.getX(), outerCircleX);
        ObjectAnimator outerCircleAnim = ObjectAnimator.ofPropertyValuesHolder(
                mOuterCircle, pvhX, mPvhScaleX, mPvhScaleY);

        // Animations for small circle
        PointF smallCircleCenter = mSmallCircle.getCenter();
        PointF smallCircleEndCenter = new PointF(
                smallCircleCenter.x - (mSmallCircle.getX() - smallCircleX), smallCircleCenter.y);

        // Create motion anim for small circle.
        mSmallCirclePath.reset();
        mSmallCirclePath.moveTo(smallCircleCenter.x, smallCircleCenter.y);
        mSmallCirclePath.quadTo(smallCircleCenter.x, smallCircleCenter.y,
                (smallCircleCenter.x + smallCircleEndCenter.x) / 2,
                (smallCircleCenter.y + smallCircleEndCenter.y) / 2 + mBezierCurveAnchorDistance);
        mSmallCirclePath.lineTo(smallCircleEndCenter.x, smallCircleEndCenter.y);

        ValueAnimator smallCircleAnim;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            smallCircleAnim = ObjectAnimator.ofObject(mSmallCircle, "center", null, mSmallCirclePath);

        } else {
            final PathMeasure pathMeasure = new PathMeasure(mSmallCirclePath, false);
            final float[] point = new float[2];
            smallCircleAnim = ValueAnimator.ofFloat(0.0f, 1.0f);
            smallCircleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    pathMeasure.getPosTan(
                            pathMeasure.getLength() * animation.getAnimatedFraction(), point, null);
                    mSmallCircle.setCenter(new PointF(point[0], point[1]));
                }
            });
        }

        boolean toRight = nextPos > mFocusPosition;
        mPvhRotation.setFloatValues(0, toRight ? -30f : 30f, 0, toRight ? 30f : -30f, 0);
        ObjectAnimator otherAnim = ObjectAnimator.ofPropertyValuesHolder(mSmallCircle, mPvhRotation, mPvhScale);

        mAnim = new AnimatorSet();
        mAnim.play(smallCircleAnim).with(otherAnim).with(largeCircleAnim).with(outerCircleAnim);
        mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnim.setDuration(mPendingAnimations.isEmpty() ? 400 : 200);
        mAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {

                if(!mPendingAnimations.isEmpty()){
                    move(mPendingAnimations.removeFirst());
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });

        mAnim.start();
        swapCircles(mFocusPosition, nextPos);
        mFocusPosition = nextPos;
    }
}
