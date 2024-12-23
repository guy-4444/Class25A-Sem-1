package com.guyi.simplegraph;


import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SnakeView extends View {
    private final static int DEFAULT_MAXIMUM_NUMBER_OF_VALUES_FOR_DESIGNER = 3;
    private final static int DEFAULT_MAXIMUM_NUMBER_OF_VALUES_FOR_RUNTIME = 10;
    private final static int DEFAULT_STROKE_COLOR = 0xff78c257;
    private final static int DEFAULT_FILL_COLOR = 0x8078c257;
    private final static int DEFAULT_STROKE_WIDTH_IN_DP = 3;
    public static final int DEFAULT_ANIMATION_DURATION = 200;
    public static final float BEZIER_FINE_FIT = 0.5f;
    public static final int DEF_STYLE_ATTR = 0;
    public static final int DEF_STYLE_RES = 0;
    public static final float DEFAULT_MIN_VALUE = 0f;
    public static final float DEFAULT_MAX_VALUE = 1f;
    public static final int MINIMUM_NUMBER_OF_VALUES = 3;
    public static final int SCALE_MODE_FIXED = 0;
    public static final int SCALE_MODE_AUTO = 1;
    public static final int CHART_STILE_STROKE = 0;
    public static final int CHART_STILE_FILL = 1;
    public static final int CHART_STILE_FILL_STROKE = 2;
    private int maximumNumberOfValues = DEFAULT_MAXIMUM_NUMBER_OF_VALUES_FOR_RUNTIME;
    private int strokeColor = DEFAULT_STROKE_COLOR;
    private int strokeWidthInPx = (int)dp2px(DEFAULT_STROKE_WIDTH_IN_DP);
    private int fillColor = DEFAULT_FILL_COLOR;
    private RectF drawingArea;
    private Paint paint;
    private Paint fillPaint;
    private Queue<Float> valuesCache;
    private List<Float> previousValuesCache;
    private List<Float> currentValuesCache;
    private int animationDuration = DEFAULT_ANIMATION_DURATION;
    private float animationProgress = 1.0f;
    private float scaleInX = 0f;
    private float scaleInY = 0f;
    private float minValue = DEFAULT_MIN_VALUE;
    private float maxValue = DEFAULT_MAX_VALUE;
    private int scaleMode = SCALE_MODE_FIXED;
    private int chartStyle = CHART_STILE_STROKE;


    public void setMaximumNumberOfValues(int maximumNumberOfValues) {
        if (maximumNumberOfValues < MINIMUM_NUMBER_OF_VALUES) {
            throw new IllegalArgumentException("The maximum number of values cannot be less than three.");
        }
        this.maximumNumberOfValues = maximumNumberOfValues;
        calculateScales();
        initializeCaches();
    }

    public void setScaleMode(int scaleMode) {
        this.scaleMode = scaleMode;
        calculateScales();
        initializeCaches();
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
        calculateScales();
        initializeCaches();
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
        calculateScales();
        initializeCaches();
    }

    public void addValue(float value) {
        if (scaleMode == SCALE_MODE_FIXED) {
            if (value < minValue || value > maxValue) {
                throw new IllegalArgumentException("The value is out of min or max limits.");
            }
        }
        previousValuesCache = cloneCache();
        if (valuesCache.size() == maximumNumberOfValues) {
            valuesCache.poll();
        }
        valuesCache.add(value);
        currentValuesCache = cloneCache();
        if (scaleMode == SCALE_MODE_AUTO) {
            calculateScales();
        }
        playAnimation();
    }

    public void clear() {
        initializeCaches();
        invalidate();
    }

    public SnakeView(Context context) {
        super(context);
        initializeView();
    }

    public SnakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        configureAttributes(attrs);
        initializeView();
    }

    public SnakeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        configureAttributes(attrs);
        initializeView();
    }

    @TargetApi(21)
    public SnakeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        configureAttributes(attrs);
        initializeView();
    }

    private void configureAttributes(AttributeSet attrs) {
        TypedArray attributes = getContext().getTheme()
                .obtainStyledAttributes(attrs, R.styleable.SnakeView,
                        DEF_STYLE_ATTR, DEF_STYLE_RES);
        scaleMode = attributes.getInteger(R.styleable.SnakeView_scaleMode, scaleMode);
        strokeColor = attributes.getColor(R.styleable.SnakeView_strokeColor, strokeColor);
        fillColor = attributes.getColor(R.styleable.SnakeView_fillColor, fillColor);
        strokeWidthInPx = attributes.getDimensionPixelSize(R.styleable.SnakeView_strokeWidth, strokeWidthInPx);
        chartStyle = attributes.getInteger(R.styleable.SnakeView_chartStyle, chartStyle);
        if (scaleMode == SCALE_MODE_FIXED) {
            minValue = attributes.getFloat(R.styleable.SnakeView_minValue, DEFAULT_MIN_VALUE);
            maxValue = attributes.getFloat(R.styleable.SnakeView_maxValue, DEFAULT_MAX_VALUE);
        }
        int defaultMaximumNumberOfValues = DEFAULT_MAXIMUM_NUMBER_OF_VALUES_FOR_RUNTIME;
        if (isInEditMode()) {
            defaultMaximumNumberOfValues = DEFAULT_MAXIMUM_NUMBER_OF_VALUES_FOR_DESIGNER;
        }
        maximumNumberOfValues = attributes.getInteger(R.styleable.SnakeView_maximumNumberOfValues,
                defaultMaximumNumberOfValues);
        animationDuration = attributes.getInteger(R.styleable.SnakeView_animationDuration,
                DEFAULT_ANIMATION_DURATION);
        if (maximumNumberOfValues < MINIMUM_NUMBER_OF_VALUES) {
            throw new IllegalArgumentException("The maximum number of values cannot be less than three.");
        }
        attributes.recycle();
    }

    private void initializeView() {
        initializePaint();
        initializeCaches();
    }

    private void initializePaint() {
        paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(DEFAULT_STROKE_COLOR);
        paint.setStyle(Paint.Style.STROKE);
        if (chartStyle == CHART_STILE_STROKE) {
            paint.setStrokeCap(Paint.Cap.ROUND);
        }
        paint.setStrokeWidth(strokeWidthInPx);

        fillPaint = new Paint();
        fillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(fillColor);
        fillPaint.setStyle(Paint.Style.FILL);
    }

    private void initializeCaches() {
        if (isInEditMode()) {
            initializeCacheForDesigner();
        } else {
            initializeCacheForRuntime();
        }
        previousValuesCache = cloneCache();
        currentValuesCache = cloneCache();
    }

    private void initializeCacheForDesigner() {
        valuesCache = new ConcurrentLinkedQueue<>();
        for (int counter = 0; counter < maximumNumberOfValues; counter++) {
            if (counter % 2 == 0) {
                valuesCache.add(minValue);
            } else {
                valuesCache.add(maxValue);
            }
        }
    }

    private void initializeCacheForRuntime() {
        valuesCache = new ConcurrentLinkedQueue<>();
        for (int counter = 0; counter < maximumNumberOfValues; counter++) {
            valuesCache.add(minValue);
        }
    }

    private List<Float> cloneCache() {
        return new ArrayList<>(valuesCache);
    }

    private float dp2px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                getResources().getDisplayMetrics());
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        calculateDrawingArea(width, height);
        calculateScales();
    }

    private void calculateDrawingArea(int width, int height) {
        int left = (strokeWidthInPx * 2) + getPaddingLeft();
        int top = (strokeWidthInPx * 2) + getPaddingTop();
        int right = width - getPaddingRight() - strokeWidthInPx;
        int bottom = height - getPaddingBottom() - strokeWidthInPx;
        drawingArea = new RectF(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!valuesCache.isEmpty()) {
            if (chartStyle == CHART_STILE_FILL || chartStyle == CHART_STILE_FILL_STROKE) {
                Path fillPath = buildPath(true);
                canvas.drawPath(fillPath, fillPaint);
            }

            if (chartStyle == CHART_STILE_STROKE || chartStyle == CHART_STILE_FILL_STROKE) {
                Path strokePath = buildPath(false);
                canvas.drawPath(strokePath, paint);
            }
        }
    }

    private Path buildPath(boolean forFill) {
        int strokePadding = (strokeWidthInPx / 2);
        Path path = new Path();
        if (forFill) {
            path.moveTo(drawingArea.left, drawingArea.bottom + strokePadding);
        }
        float previousX = drawingArea.left;
        float previousY = drawingArea.bottom;
        for (int index = 0; index < currentValuesCache.size(); index++) {
            float previousValue = previousValuesCache.get(index);
            float currentValue = currentValuesCache.get(index);
            float pathValue = previousValue + ((currentValue - previousValue) * animationProgress);
            float x = drawingArea.left + (scaleInX * index);
            float y = drawingArea.bottom - ((pathValue - minValue) * scaleInY);
            if (index == 0) {
                if (forFill) {
                    path.lineTo(x, y);
                } else {
                    path.moveTo(x, y);
                }
            } else {
                float bezierControlX = previousX + ((x - previousX) * BEZIER_FINE_FIT);
                float controlPointX1 = bezierControlX;
                float controlPointY1 = previousY;
                float controlPointX2 = bezierControlX;
                float controlPointY2 = y;
                float endPointX = x;
                float endPointY = y;
                path.cubicTo(controlPointX1, controlPointY1,
                        controlPointX2, controlPointY2,
                        endPointX, endPointY);
            }
            previousX = x;
            previousY = y;
        }
        if (forFill) {
            path.lineTo(drawingArea.right, drawingArea.bottom + strokePadding);
        }
        return path;
    }

    private void calculateScales() {
        if (drawingArea != null) {
            if (scaleMode == SCALE_MODE_AUTO) {
                for (int index = 0; index < currentValuesCache.size(); index++) {
                    float previousValue = previousValuesCache.get(index);
                    float currentValue = currentValuesCache.get(index);
                    if (index == 0) {
                        minValue = Math.min(currentValue, previousValue);
                        maxValue = Math.max(currentValue, previousValue);
                    } else {
                        minValue = Math.min(minValue, currentValue);
                        maxValue = Math.max(maxValue, currentValue);
                        minValue = Math.min(minValue, previousValue);
                        maxValue = Math.max(maxValue, previousValue);
                    }
                }
            }
            scaleInX = (drawingArea.width() / (maximumNumberOfValues - 1));
            scaleInY = (drawingArea.height() / (maxValue - minValue));
        } else {
            scaleInY = 0f;
            scaleInX = 0f;
        }
    }

    private void playAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                SnakeView.this.animationProgress = (float)animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.setDuration(animationDuration);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    public void setStrokeColor(int color) {
        paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        if (chartStyle == CHART_STILE_STROKE) {
            paint.setStrokeCap(Paint.Cap.ROUND);
        }
        paint.setStrokeWidth(strokeWidthInPx);
    }
}