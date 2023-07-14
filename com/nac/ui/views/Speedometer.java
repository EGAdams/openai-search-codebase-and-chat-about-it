package com.nac.ui.views;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.nac.R;
import com.nac.utils.MathUtils;

import java.util.Locale;

public final class Speedometer extends View {

    private static final String TAG = Speedometer.class.getSimpleName();
    private Context mContext;

    // drawing tools
    private RectF rimRect;
    private Paint rimPaint;
    private Paint rimCirclePaint;

    private RectF faceRect;
    private RectF yawRect;

    private Paint scalePaint;
    private Paint scaleTickPaint;
    private Paint scaleTextPaint;
    private Paint versionPaint;
    private Paint yellowScalePaint;
    private Paint greenScalePaint;
    private Paint redScalePaint;
    private RectF scaleRect;
    private RectF colorScaleRect;

    private Paint titlePaint;
    private Path titlePath;

    private Paint greenTextPaint;
    private Paint yellowTextPaint;

    private Paint yawPaint;
    private Path yawPath;
    private Paint yawScrewPaint;

    private Paint handPaint;
    private Path handPath;
    private Paint handScrewPaint;

    private Paint backgroundPaint;
    // end drawing tools

    private Bitmap background; // holds the cached static part

    // Chris - Correction factor to draw in a larger scale to increase resolution
    private float drawingScale = 100;

    // scale configuration
    private static final int offsetDegree = 40;
    private static final int minDegrees = -200;
    private static final int maxDegrees = 200;

    private boolean handInitialized = false;
    private float handPosition = 0f;
    private float handTarget = 0f;
    private boolean yawInitialized = false;
    private float yawPosition = 0f;
    private float yawTarget = 0f;
    private float yaw;
    private float handVelocity = 0.0f;
    private float yawVelocity = 0.0f;
    private float handAcceleration = 0.0f;
    private float yawAcceleration = 0.0f;
    private long lastHandMoveTime = -1L;
    private long lastYawMoveTime = -1L;
    private float speed = -111f;

    private String measurementString = "mph";
    private String forceString = "% g";

    private final float centerOffsetDegree = 45f;
    private final float stepDegree = 20f;
    private boolean isRcr = false;

    private final int[] speedometerScales = {5, 10, 20, 40, 100};
    private final int[] speedometerScales2 = {1, 3, 6, 13, 32};
    private Paint yawScalePaint;
    private Paint yawTextPaint;

    public Speedometer(Context context) {
        super(context);
        init(context);
    }

    public Speedometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Speedometer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        Parcelable superState = bundle.getParcelable("superState");
        super.onRestoreInstanceState(superState);

        handInitialized = bundle.getBoolean("handInitialized");
        handPosition = bundle.getFloat("handPosition");
        handTarget = bundle.getFloat("handTarget");
        handVelocity = bundle.getFloat("handVelocity");
        handAcceleration = bundle.getFloat("handAcceleration");
        lastHandMoveTime = bundle.getLong("lastHandMoveTime");
        yawInitialized = bundle.getBoolean("yawInitialized");
        yawPosition = bundle.getFloat("yawPosition");
        yawTarget = bundle.getFloat("yawTarget");
        yawVelocity = bundle.getFloat("yawVelocity");
        yawAcceleration = bundle.getFloat("yawAcceleration");
        lastYawMoveTime = bundle.getLong("lastYawMoveTime");
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        Bundle state = new Bundle();
        state.putParcelable("superState", superState);
        state.putBoolean("handInitialized", handInitialized);
        state.putFloat("handPosition", handPosition);
        state.putFloat("handTarget", handTarget);
        state.putFloat("handVelocity", handVelocity);
        state.putFloat("handAcceleration", handAcceleration);
        state.putLong("lastHandMoveTime", lastHandMoveTime);
        state.putBoolean("yawInitialized", yawInitialized);
        state.putFloat("yawPosition", yawPosition);
        state.putFloat("yawTarget", yawTarget);
        state.putFloat("yawVelocity", yawVelocity);
        state.putFloat("yawAcceleration", yawAcceleration);
        state.putLong("lastYawMoveTime", lastYawMoveTime);
        return state;
    }

    private void init(Context context) {
        mContext = context;
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        initDrawingTools();
    }

    private void initDrawingTools() {
        rimRect = new RectF(0.1f*drawingScale, 0.1f*drawingScale, 0.9f*drawingScale, 0.9f*drawingScale);

        // the linear gradient is a bit skewed for realism
        rimPaint = new Paint();
        rimCirclePaint = new Paint();
        rimCirclePaint.setAntiAlias(true);
        rimCirclePaint.setStyle(Paint.Style.FILL);
        rimCirclePaint.setColor(0xff061f37);

        float rimSize = 0.02f*drawingScale;
        faceRect = new RectF();
        faceRect.set(rimRect.left + rimSize, rimRect.top + rimSize,
                rimRect.right - rimSize, rimRect.bottom - rimSize);

        scaleTickPaint = new Paint();
        scalePaint = new Paint();
        scalePaint.setStyle(Paint.Style.STROKE);
        scalePaint.setColor(0xffffffff);
        scalePaint.setStrokeWidth(0.018f*drawingScale);
        scalePaint.setAntiAlias(true);

        yawScalePaint = new Paint();
        yawScalePaint.setStyle(Paint.Style.STROKE);
        yawScalePaint.setColor(0xffffffff);
        yawScalePaint.setStrokeWidth(0.005f*drawingScale);
        yawScalePaint.setAntiAlias(true);

        versionPaint = new Paint();
        versionPaint.setColor(0xff000000);
        versionPaint.setAntiAlias(true);
        versionPaint.setTypeface(Typeface.DEFAULT);
        versionPaint.setTextAlign(Paint.Align.CENTER);
        versionPaint.setTextSize(0.04f*drawingScale);
        versionPaint.setTextScaleX(0.8f);
        versionPaint.setLinearText(true);

        greenTextPaint = new Paint();
        greenTextPaint.setColor(0xff39b54a);
        greenTextPaint.setAntiAlias(true);
        greenTextPaint.setTypeface(Typeface.DEFAULT);
        greenTextPaint.setTextAlign(Paint.Align.CENTER);
        greenTextPaint.setTextSize(0.1f*drawingScale);
        greenTextPaint.setTextScaleX(0.8f);
        greenTextPaint.setLinearText(true);

        yellowTextPaint = new Paint();
        yellowTextPaint.setColor(0xffebed2f);
        yellowTextPaint.setAntiAlias(true);
        yellowTextPaint.setTypeface(Typeface.DEFAULT);
        yellowTextPaint.setTextAlign(Paint.Align.CENTER);
        yellowTextPaint.setTextSize(0.1f*drawingScale);
        yellowTextPaint.setTextScaleX(0.8f);
        yellowTextPaint.setLinearText(true);

        yawTextPaint = new Paint();
        yawTextPaint.setColor(0xffebed2f);
        yawTextPaint.setAntiAlias(true);
        yawTextPaint.setTypeface(Typeface.DEFAULT);
        yawTextPaint.setTextAlign(Paint.Align.CENTER);
        yawTextPaint.setTextSize(0.05f*drawingScale);
        yawTextPaint.setTextScaleX(0.8f);
        yawTextPaint.setLinearText(true);

        yellowScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yellowScalePaint.setColor(0xffebed2f);
        yellowScalePaint.setStyle(Paint.Style.STROKE);
        yellowScalePaint.setStrokeWidth(0.036f*drawingScale);
        yellowScalePaint.setAntiAlias(true);

        float colorScaleThickness = 0.016f*drawingScale;
        colorScaleRect = new RectF();
        colorScaleRect.set(rimRect.left + colorScaleThickness, rimRect.top + colorScaleThickness,
                rimRect.right - colorScaleThickness, rimRect.bottom - colorScaleThickness);

        greenScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenScalePaint.setColor(0xff39b54a);
        greenScalePaint.setStyle(Paint.Style.STROKE);
        greenScalePaint.setStrokeWidth(0.035f*drawingScale);
        greenScalePaint.setAntiAlias(true);

        redScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redScalePaint.setColor(0xffcb401e);
        redScalePaint.setStyle(Paint.Style.STROKE);
        redScalePaint.setStrokeWidth(0.035f*drawingScale);
        redScalePaint.setAntiAlias(true);

        scaleTextPaint = new Paint();
        scaleTextPaint.setColor(0xffffffff);
        scaleTextPaint.setLinearText(true);

        scaleTickPaint.setStyle(Paint.Style.STROKE);
        scaleTickPaint.setStrokeWidth(0.008f*drawingScale);
        scaleTickPaint.setColor(0xffffffff);
        scaleTickPaint.setAntiAlias(true);

        float scalePosition = 0.02f*drawingScale;
        scaleRect = new RectF();
        scaleRect.set(faceRect.left + scalePosition, faceRect.top + scalePosition,
                faceRect.right - scalePosition, faceRect.bottom - scalePosition);

        float yawOffset = 0.25f*drawingScale;
        yawRect = new RectF();
        yawRect.set(rimRect.left + yawOffset, rimRect.top + yawOffset,
                rimRect.right - yawOffset, rimRect.bottom - yawOffset);

        titlePaint = new Paint();
        titlePaint.setColor(0xffffffff);
        titlePaint.setAntiAlias(true);
        titlePaint.setTypeface(Typeface.DEFAULT);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTextSize(0.05f*drawingScale);
        titlePaint.setTextScaleX(0.8f);
        titlePaint.setLinearText(true);

        titlePath = new Path();
        titlePath.addArc(new RectF(0.24f*drawingScale, 0.24f*drawingScale, 0.76f*drawingScale, 0.76f*drawingScale), -180.0f, -180.0f);

        handPaint = new Paint();
        handPaint.setAntiAlias(true);
        handPaint.setColor(0xffcb401e);
        handPaint.setStyle(Paint.Style.FILL);

        yawPaint = new Paint();
        yawPaint.setAntiAlias(true);
        yawPaint.setColor(0xffFFFF00);
        yawPaint.setStyle(Paint.Style.FILL);

        handPath = new Path();
        float handCenter = 0.5f*drawingScale;
        handPath.moveTo(handCenter, handCenter);
        handPath.lineTo(handCenter - (0.020f*drawingScale), handCenter);
        handPath.lineTo(handCenter - (0.003f*drawingScale), handCenter - (0.30f*drawingScale));
        handPath.lineTo(handCenter + (0.003f*drawingScale), handCenter - (0.30f*drawingScale));
        handPath.lineTo(handCenter + (0.020f*drawingScale), handCenter);
        handPath.lineTo(handCenter, handCenter);

        yawPath = new Path();
        float yawCenterx = 0.5f*drawingScale;
        float yawCentery = 0.35f*drawingScale;
        yawPath.moveTo(yawCenterx, yawCentery);
        yawPath.lineTo(yawCenterx - (0.015f*drawingScale), yawCentery);
        yawPath.lineTo(yawCenterx - (0.002f*drawingScale), yawCentery - (0.12f*drawingScale));
        yawPath.lineTo(yawCenterx + (0.002f*drawingScale), yawCentery - (0.12f*drawingScale));
        yawPath.lineTo(yawCenterx + (0.015f*drawingScale), yawCentery);
        yawPath.lineTo(yawCenterx, yawCentery);

        handPath.addCircle(handCenter, handCenter, 0.05f*drawingScale, Path.Direction.CW);

        handScrewPaint = new Paint();
        handScrewPaint.setAntiAlias(true);
        handScrewPaint.setColor(0xff493f3c);
        handScrewPaint.setStyle(Paint.Style.FILL);

        backgroundPaint = new Paint();
        backgroundPaint.setFilterBitmap(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int chosenWidth = chooseDimension(widthMode, widthSize);
        int chosenHeight = chooseDimension(heightMode, heightSize);

        int chosenDimension = Math.min(chosenWidth, chosenHeight);

        setMeasuredDimension(chosenDimension, chosenDimension);
    }

    private int chooseDimension(int mode, int size) {
        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
            return size;
        } else { // (mode == MeasureSpec.UNSPECIFIED)
            return getPreferredSize();
        }
    }

    // in case there is no size specified
    private int getPreferredSize() {
        return 300;
    }

    private void drawRim(Canvas canvas) {
        canvas.drawOval(rimRect, rimCirclePaint);
    }

    private void drawScale(Canvas canvas) {
        // Some values hardcoded for custom speedometer
        float interval = (speedometerScales.length - 1) * stepDegree;

        float halfScale = offsetDegree + interval;

        float startDegree = -(halfScale + 90 + 5.5f);

        float length = 2 * halfScale + 11f;
        canvas.drawArc(colorScaleRect, startDegree, length, false, greenScalePaint);
        canvas.drawArc(colorScaleRect, startDegree, stepDegree + 0.5f, false, redScalePaint);
        canvas.drawArc(colorScaleRect, startDegree + length - stepDegree, stepDegree + 0.5f, false, redScalePaint);

        canvas.drawArc(colorScaleRect, startDegree + stepDegree + 0.5f, stepDegree, false, yellowScalePaint);
        canvas.drawArc(colorScaleRect, startDegree + length - 2 * stepDegree, stepDegree, false, yellowScalePaint);
        canvas.drawArc(scaleRect, startDegree, length, false, scalePaint);
        canvas.drawArc(yawRect, startDegree + 45, length - 90, false, yawScalePaint);
        canvas.save(Canvas.MATRIX_SAVE_FLAG);

        PackageInfo pInfo = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            canvas.drawText("v"+pInfo.versionName, 0.05f*drawingScale, 0.05f*drawingScale, versionPaint);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // draw start zero position
        float startY1 = scaleRect.top;
        float startY2 = startY1 + (0.030f*drawingScale);
        canvas.drawLine(0.5f*drawingScale, startY1, 0.5f*drawingScale, startY2, scaleTickPaint);

        canvas.rotate(centerOffsetDegree, 0.5f*drawingScale, 0.5f*drawingScale);
        int[] currentScale = isRcr ? speedometerScales2 : speedometerScales;
        float y1 = scaleRect.top;
        float y2 = y1 + (0.030f*drawingScale);
        for (int i = 0; i < currentScale.length; ++i) {
            canvas.drawLine(0.5f*drawingScale, y1, 0.5f*drawingScale, y2, scaleTickPaint);
            canvas.drawText(String.valueOf(currentScale[i]), 0.5f*drawingScale, y2 + (0.05f*drawingScale), titlePaint);
            canvas.rotate(stepDegree, 0.5f*drawingScale, 0.5f*drawingScale);
        }

        float reverseRotate = currentScale.length * stepDegree + centerOffsetDegree * 2f;
        canvas.rotate(-reverseRotate, 0.5f*drawingScale, 0.5f*drawingScale);

        for (int i = 0; i < speedometerScales.length; ++i) {
            canvas.drawLine(0.5f*drawingScale, y1, 0.5f*drawingScale, y2, scaleTickPaint);
            canvas.drawText(String.valueOf(-currentScale[i]), 0.5f*drawingScale, y2 + (0.05f*drawingScale), titlePaint);
            canvas.rotate(-stepDegree, 0.5f*drawingScale, 0.5f*drawingScale);
        }


        canvas.restore();
    }

    private float degreeToAngle(float degree) {
        // TODO: move to constant

        int[] currentScale = isRcr ? speedometerScales2 : speedometerScales;
        int range0 = currentScale[0];
        int range1 = currentScale[1] - currentScale[0];
        int range2 = currentScale[2] - currentScale[1];
        int range3 = currentScale[3] - currentScale[2];
        int range4 = currentScale[4] - currentScale[3];
        if (degree >= 0 && degree <= currentScale[0]) {
            return (offsetDegree / range0) * degree;
        } else if (degree > currentScale[0] && degree <= currentScale[1]) {
            return offsetDegree + (stepDegree / range1) * (degree - currentScale[0]);
        } else if (degree > currentScale[1] && degree <= currentScale[2]) {
            return offsetDegree + stepDegree + (stepDegree / range2) * (degree - currentScale[1]);
        } else if (degree > currentScale[2] && degree <= currentScale[3]) {
            return offsetDegree + stepDegree * 2 + (stepDegree / range3) * (degree - currentScale[2]);
        } else if (degree > currentScale[3] && degree <= currentScale[4]) {
            return offsetDegree + stepDegree * 3 + (stepDegree / range4) * (degree - currentScale[3]);


        } else if (degree > -currentScale[0] && degree < 0) {
            return (offsetDegree / range0 * degree);
        } else if (degree > -currentScale[1] && degree <= -currentScale[0]) {
            return -offsetDegree + (stepDegree / range1) * (degree + currentScale[0]);
        } else if (degree > -currentScale[2] && degree <= -currentScale[1]) {
            return -offsetDegree - stepDegree + (stepDegree / range2) * (degree + currentScale[1]);
        } else if (degree > -currentScale[3] && degree <= -currentScale[2]) {
            return -offsetDegree - 2 * stepDegree + (stepDegree / range3) * (degree + currentScale[2]);
        } else if (degree >= -currentScale[4] && degree <= -currentScale[3]) {
            return -offsetDegree - 3 * stepDegree + (stepDegree / range4) * (degree + currentScale[3]);
        } else if (degree > currentScale[4]) {
            return (offsetDegree + 4 * stepDegree);
        } else if (degree < -currentScale[4])
            return -(offsetDegree + 4 * stepDegree);
        return 0;
    }

    private void drawYaw(Canvas canvas) {
        if (yawInitialized) {
            float handAngle = degreeToAngleForYaw(yaw);
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.rotate(handAngle, 0.5f*drawingScale, 0.5f*drawingScale);
            canvas.drawPath(yawPath, yawPaint);
            canvas.restore();
        }
    }

    private float degreeToAngleForYaw(float yaw) {
        return yaw * 0.8f;
    }

    private void drawHand(Canvas canvas) {
        if (handInitialized) {
            float handAngle = degreeToAngle(handPosition);
            if (handAngle < 0) {
                handAngle -= 5f;
            } else if (handAngle > 0) {
                handAngle += 5f;
            }
//            if (Math.abs(handAngle) < 40) {
//                handAngle = 0;
//            }

            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.rotate(handAngle, 0.5f*drawingScale, 0.5f*drawingScale);
            canvas.drawPath(handPath, handPaint);
            canvas.restore();

            canvas.drawCircle(0.5f*drawingScale, 0.5f*drawingScale, 0.01f*drawingScale, handScrewPaint);
        }
    }

    private void drawBackground(Canvas canvas) {
        if (background == null) {
            Log.w(TAG, "Background not created");
        } else {
            canvas.drawBitmap(background, 0, 0, backgroundPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        float scale = (float) getWidth()/drawingScale;
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.scale(scale, scale);
        drawYaw(canvas);
        drawHand(canvas);
        drawParametersLabels(canvas);
        canvas.restore();

        if(yawNeedsToMove()) {
            moveYaw();
        }

        if (handNeedsToMove()) {
            moveHand();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        regenerateBackground();
    }

    private void regenerateBackground() {
        // free the old bitmap
        if (background != null) {
            background.recycle();
        }

        background = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas backgroundCanvas = new Canvas(background);
        float scale = (float) getWidth()/drawingScale;
        backgroundCanvas.scale(scale, scale);

        drawRim(backgroundCanvas);
        drawScale(backgroundCanvas);
        drawLabels(backgroundCanvas);
    }

    private void drawLabels(Canvas canvas) {
        canvas.drawText("Yaw", 0.5f*drawingScale, 0.42f*drawingScale, yawTextPaint);
    }

    private void drawParametersLabels(Canvas backgroundCanvas) {
        if (Math.abs(handTarget) < 1f) {
            backgroundCanvas.drawText(String.format(Locale.US, "%.0f", 0.0f) + forceString, 0.5f*drawingScale, 0.7f*drawingScale, yellowTextPaint);
        } else {
            backgroundCanvas.drawText(String.format(Locale.US, "%.0f", handTarget) + forceString, 0.5f*drawingScale, 0.7f*drawingScale, yellowTextPaint);
        }
        if (speed == -111f) {
            backgroundCanvas.drawText("?? speed", 0.5f*drawingScale, 0.83f*drawingScale, greenTextPaint);
        } else {
            int value = (int) MathUtils.round(speed, 0);
            backgroundCanvas.drawText(String.format(Locale.US, "%d ", value) + measurementString, 0.5f*drawingScale, 0.83f*drawingScale, greenTextPaint);
        }
    }

    private boolean handNeedsToMove() {
        return Math.abs(handPosition - handTarget) > 0.01f;
    }

    private boolean yawNeedsToMove() {
        return Math.abs(yawPosition - yawTarget) > 0.01f;
    }

    private void moveHand() {
        if (!handNeedsToMove()) {
            return;
        }

        if (lastHandMoveTime != -1L) {
            long currentTime = System.currentTimeMillis();
            float delta = (currentTime - lastHandMoveTime) / 1000.0f;

            float direction = Math.signum(handVelocity);
            if (Math.abs(handVelocity) < 90.0f) {
                handAcceleration = 5.0f * (handTarget - handPosition);
            } else {
                handAcceleration = 0.0f;
            }
            handPosition += handVelocity * delta;
            handVelocity += handAcceleration * delta;
            if ((handTarget - handPosition) * direction < 0.01f * direction) {
                handPosition = handTarget;
                handVelocity = 0.0f;
                handAcceleration = 0.0f;
                lastHandMoveTime = -1L;
            } else {
                lastHandMoveTime = System.currentTimeMillis();
            }
            invalidate();
        } else {
            lastHandMoveTime = System.currentTimeMillis();
            moveHand();
        }
    }

    private void moveYaw() {
        if (!yawNeedsToMove()) {
            return;
        }

        if (lastYawMoveTime != -1L) {
            long currentTime = System.currentTimeMillis();
            float delta = (currentTime - lastYawMoveTime) / 1000.0f;

            float direction = Math.signum(yawVelocity);
            if (Math.abs(yawVelocity) < 90.0f) {
                yawAcceleration = 5.0f * (yawTarget - yawPosition);
            } else {
                yawAcceleration = 0.0f;
            }
            yawPosition += yawVelocity * delta;
            yawVelocity += yawAcceleration * delta;
            if ((yawTarget - yawPosition) * direction < 0.01f * direction) {
                yawPosition = yawTarget;
                yawVelocity = 0.0f;
                yawAcceleration = 0.0f;
                lastYawMoveTime = -1L;
            } else {
                lastYawMoveTime = System.currentTimeMillis();
            }
            invalidate();
        } else {
            lastYawMoveTime = System.currentTimeMillis();
            moveYaw();
        }
    }

    public void updateAcceleration(float value, float yaw) {
        setHandTarget(value, yaw);
    }

    public void updateSpeed(float speed) {
        this.speed = speed;
    }

    private void setHandTarget(float handTarget, float yaw) {
        if (handTarget < minDegrees) {
            handTarget = minDegrees;
        } else if (handTarget > maxDegrees) {
            handTarget = maxDegrees;
        }
        if (Math.abs(handTarget) < 3f) {
            handTarget = 0;
        }
        this.handTarget = handTarget;
        this.yaw = yaw;
        handInitialized = true;
        yawInitialized = true;
        invalidate();
    }

    public String getMeasurementString() {
        return measurementString;
    }

    public void setMeasurementString(String measurementString) {
        this.measurementString = measurementString;
    }

    public String getForceString() {
        return forceString;
    }

    public void setForceString(String forceString) {
        this.forceString = forceString;
    }

    public boolean isRcr() {
        return isRcr;
    }

    public void setRcr(boolean isRcr) {
        this.isRcr = isRcr;
    }
}
