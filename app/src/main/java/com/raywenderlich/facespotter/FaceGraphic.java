
package com.raywenderlich.facespotter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import static android.graphics.Color.rgb;

import android.graphics.drawable.Drawable;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.ScaleDrawable;
import android.util.Log;

import com.raywenderlich.facespotter.BottomSheet.TryOnPojo;
import com.raywenderlich.facespotter.ui.camera.GraphicOverlay;


class FaceGraphic extends GraphicOverlay.Graphic {

    public static int lneck = 0;
    public static int rneck = 0;
    public static int tneck = 0;
    public static int bneck = 0;

    public static int lear = 0;
    public static int rear = 0;
    public static int tear = 0;
    public static int bear = 0;

    public static int lear1 = 0;
    public static int rear1 = 0;
    public static int tear1 = 0;
    public static int bear1 = 0;

    public static int lmang = 0;
    public static int rmang = 0;
    public static int tmang = 0;
    public static int bmang = 0;

    public static int lnose = 0;
    public static int rnose = 0;
    public static int tnose = 0;
    public static int bnose = 0;

    private static final String TAG = "FaceGraphic";

    private static final float EYE_RADIUS_PROPORTION = 0.45f;
    private static final float IRIS_RADIUS_PROPORTION = EYE_RADIUS_PROPORTION / 2.0f;
    private static final float HEAD_TILT_HAT_THRESHOLD = 20.0f;
    private static final float ID_TEXT_SIZE = 60.0f;

    private Context mContext;
    private boolean mIsFrontFacing;

    private Drawable mPigNoseGraphic;
    private Drawable mHappyStarGraphic;
    private Drawable mMustacheGraphic;
    private Drawable mHatGraphic;


    private Drawable mNecklace;
    private Drawable mMaangTeeka;
    private Drawable mLeftEaring;
    private Drawable mRightEaring;
    private Drawable mNosePin;

    private Paint mEyeWhitesPaint;
    private Paint mEyeIrisPaint;
    private Paint mEyeOutlinePaint;
    private Paint mEyeLidPaint;
    private Paint mTextPaint;

    // Face coordinate and dimension data
    private volatile PointF mPosition;
    private volatile float mWidth;
    private volatile float mHeight;
    private volatile float mEulerY;
    private volatile float mEulerZ;
    private volatile PointF mLeftEyePosition;
    private volatile boolean mLeftEyeOpen;
    private volatile PointF mRightEyePosition;
    private volatile boolean mRightEyeOpen;
    private volatile PointF mNoseBasePosition;
    private volatile PointF mMouthLeftPosition;
    private volatile PointF mMouthBottomPosition;
    private volatile PointF mMouthRightPosition;


    //////////EARINGS//////////////


    private volatile PointF mLeftEarTip;
    private volatile PointF mLeftEarPosition;

    private volatile PointF mRightEarTip;
    private volatile PointF mRightEarPosition;

    //////////EARINGS//////////////


    private volatile boolean mIsSmiling;

    // We want each iris to move independently,
    // so each one gets its own physics engine.
    private EyePhysics mLeftPhysics = new EyePhysics();
    private EyePhysics mRightPhysics = new EyePhysics();


    FaceGraphic(GraphicOverlay overlay, Context context, boolean isFrontFacing) {
        super(overlay);

        final int POWDER_BLUE_COLOR = Color.rgb(176, 224, 230);
        final int SADDLE_BROWN_COLOR = rgb(139, 69, 19);
        final float TEXT_SIZE = 60.0f;

        mContext = context;
        mIsFrontFacing = isFrontFacing;

        mPigNoseGraphic = mContext.getDrawable(R.drawable.pig_nose_emoji);
        mHappyStarGraphic = mContext.getDrawable(R.drawable.happy_star);
        mMustacheGraphic = mContext.getDrawable(R.drawable.mustache);
        mHatGraphic = mContext.getDrawable(R.drawable.red_hat);

        mNecklace = mContext.getDrawable(R.drawable.neckex);
        mMaangTeeka = mContext.getDrawable(R.drawable.manggggg);
        mLeftEaring = mContext.getDrawable(R.drawable.leftwaring);
        mRightEaring = mContext.getDrawable(R.drawable.rightwaring);
        mNosePin = mContext.getDrawable(R.drawable.noseeeeeee);

        mEyeWhitesPaint = new Paint();
        mEyeWhitesPaint.setColor(Color.WHITE);
        mEyeWhitesPaint.setStyle(Paint.Style.FILL);

        mEyeLidPaint = new Paint();
        mEyeLidPaint.setColor(POWDER_BLUE_COLOR);
        mEyeLidPaint.setStyle(Paint.Style.FILL);

        mEyeIrisPaint = new Paint();
        mEyeIrisPaint.setColor(SADDLE_BROWN_COLOR);
        mEyeIrisPaint.setStyle(Paint.Style.FILL);

        mEyeOutlinePaint = new Paint();
        mEyeOutlinePaint.setColor(Color.BLACK);
        mEyeOutlinePaint.setStyle(Paint.Style.STROKE);
        mEyeOutlinePaint.setStrokeWidth(5);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.CYAN);
        mTextPaint.setTextSize(ID_TEXT_SIZE);
    }

    void update(FaceData faceData) {
        mPosition = faceData.getPosition();
        mHeight = faceData.getHeight();
        mWidth = faceData.getWidth();

        mEulerY = faceData.getEulerY();
        mEulerZ = faceData.getEulerZ();

        mLeftEyePosition = faceData.getLeftEyePosition();
        mLeftEyeOpen = faceData.isLeftEyeOpen();
        mRightEyePosition = faceData.getRightEyePosition();
        mRightEyeOpen = faceData.isRightEyeOpen();

        mNoseBasePosition = faceData.getNoseBasePosition();

        mMouthLeftPosition = faceData.getMouthLeftPosition();
        mMouthBottomPosition = faceData.getMouthBottomPosition();
        mMouthRightPosition = faceData.getMouthRightPosition();

        //////////EARINGS//////////////

        mLeftEarTip = faceData.getLeftEarTipPosition();
        mLeftEarPosition = faceData.getLeftEarPosition();

        mRightEarTip = faceData.getRightEarTipPosition();
        mRightEarPosition = faceData.getRightEarPosition();
        //////////EARINGS//////////////


        mIsSmiling = faceData.isSmiling();

        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        // Confirm that the face and its features are still visible
        // before drawing any graphics over it.
        PointF detectPosition = mPosition;
        PointF detectLeftPosition = mLeftEyePosition;
        PointF detectRightPosition = mRightEyePosition;
        PointF detectNoseBasePosition = mNoseBasePosition;
        PointF detectMouthLeftPosition = mMouthLeftPosition;
        PointF detectBottomMouthPosition = mMouthBottomPosition;
        PointF detectMouthRightPosition = mMouthRightPosition;

        //////////EARINGS//////////////

        PointF detectLeftEarTip = mLeftEarTip;
        PointF detectLeftEarPosition = mLeftEarPosition;
        PointF detectRightEarTip = mRightEarTip;
        PointF detectRightEarPosition = mRightEarPosition;

        //////////EARINGS//////////////


        if ((detectPosition == null) ||
                (detectLeftPosition == null) ||
                (detectRightPosition == null) ||
                (detectNoseBasePosition == null) ||
                (detectMouthLeftPosition == null) ||
                (detectBottomMouthPosition == null) ||
                (detectMouthRightPosition == null) ||
                (detectLeftEarTip == null) ||
                (detectLeftEarPosition == null) ||
                (detectRightEarTip == null) ||
                (detectRightEarPosition == null)

        ) {
            return;
        }

        // Convert the face's camera coordinates and dimensions
        // to view coordinates and dimensions.
        PointF position = new PointF(scaleX(detectPosition.x),
                scaleY(detectPosition.y));
        float width = scaleX(mWidth);
        float height = scaleY(mHeight);
        PointF leftEyePosition = new PointF(translateX(detectLeftPosition.x),
                translateY(detectLeftPosition.y));
        PointF rightEyePosition = new PointF(translateX(detectRightPosition.x),
                translateY(detectRightPosition.y));
        PointF noseBasePosition = new PointF(translateX(detectNoseBasePosition.x),
                translateY(detectNoseBasePosition.y));
        PointF mouthLeftPosition = new PointF(translateX(detectMouthLeftPosition.x),
                translateY(detectMouthLeftPosition.y));
        PointF mouthRightPosition = new PointF(translateX(detectMouthRightPosition.x),
                translateY(detectMouthRightPosition.y));

        PointF mouthBottomPosition = new PointF(translateX(detectBottomMouthPosition.x),
                translateY(detectBottomMouthPosition.y));

        //////////EARINGS//////////////

        PointF leftEarTip = new PointF(translateX(detectLeftEarTip.x),
                translateY(detectLeftEarTip.y));
        PointF leftEarPos = new PointF(translateX(detectLeftEarPosition.x),
                translateY(detectLeftEarPosition.y));

        PointF rightEarTip = new PointF(translateX(detectRightEarTip.x),
                translateY(detectRightEarTip.y));

        PointF rightEarPos = new PointF(translateX(detectRightEarPosition.x),
                translateY(detectRightEarPosition.y));


        //////////EARINGS//////////////


        // Calculate the distance between the eyes using Pythagoras' formula,
        // and we'll use that distance to set the size of the eyes and irises.
        float distance = (float) Math.sqrt(
                (rightEyePosition.x - leftEyePosition.x) * (rightEyePosition.x - leftEyePosition.x) +
                        (rightEyePosition.y - leftEyePosition.y) * ((rightEyePosition.y - leftEyePosition.y)));
        float eyeRadius = EYE_RADIUS_PROPORTION * distance;
        float irisRadius = IRIS_RADIUS_PROPORTION * distance;

        // Draw the eyes.
        PointF leftIrisPosition = mLeftPhysics.nextIrisPosition(leftEyePosition, eyeRadius, irisRadius);
        //drawEye(canvas, leftEyePosition, eyeRadius, leftIrisPosition, irisRadius, mLeftEyeOpen, mIsSmiling);
        PointF rightIrisPosition = mRightPhysics.nextIrisPosition(rightEyePosition, eyeRadius, irisRadius);
        //drawEye(canvas, rightEyePosition, eyeRadius, rightIrisPosition, irisRadius, mRightEyeOpen, mIsSmiling);

        // Draw the mustache and nose.
        //drawMustache(canvas, noseBasePosition, mouthLeftPosition, mouthRightPosition);

        //drawLeftEaring(canvas, leftEarPos, leftEarTip);

        //drawNecklace(canvas, mouthBottomPosition, leftEarTip, rightEarTip, position, width, height);
        //drawMaangTeeka(canvas, position, width, height, noseBasePosition);
        //drawLeftEar(canvas, leftEarTip, leftEarPos, width);
        //drawRightEar(canvas, rightEarTip, rightEarPos, width);
        //drawNose(canvas, noseBasePosition, leftEyePosition, rightEyePosition, irisRadius);

        //drawNecklace(canvas, mouthBottomPosition, leftEarTip, rightEarTip, position, width, height, mouthLeftPosition, mouthRightPosition, noseBasePosition, rightEyePosition, leftEyePosition);


        // Draw the hat only if the subject's head is titled at a
        // sufficiently jaunty angle.
//        if (Math.abs(mEulerZ) > HEAD_TILT_HAT_THRESHOLD) {
//            drawHat(canvas, position, width, height, noseBasePosition);
//        }

        //mNecklace = FaceActivity.hereIsDrawable();
        TryOnPojo tryOnPojo = FaceActivity.hereIsDrawableAndType();

        switch (tryOnPojo.getType()) {

            case "default_image":
                Log.d(TAG, "drawwww: YOU ARE ON DEFAULT IMAGE");
                break;

            case "necklace":
                Log.d(TAG, "drawwww: YOU CHOSE NECKLACE");
                drawNecklace(tryOnPojo.getImage(), canvas, mouthBottomPosition, leftEarTip, rightEarTip, position, width, height, mouthLeftPosition, mouthRightPosition, noseBasePosition, rightEyePosition, leftEyePosition);
                break;

            case "nosepin":
                drawNose(tryOnPojo.getImage(), canvas, noseBasePosition, leftEyePosition, rightEyePosition, irisRadius, rightEarPos);
                Log.d(TAG, "drawwww: YOU CHOSE NOSEPIN");
                break;

            case "maangteeka":
                Log.d(TAG, "drawwww: YOU CHOSE MAANG TIKKA");
                drawMaangTeeka(tryOnPojo.getImage(), canvas, position, width, height, noseBasePosition);
                break;

            case "earrings":
                drawLeftEar(tryOnPojo.getImage(), canvas, leftEarTip, leftEarPos, width, mouthBottomPosition, noseBasePosition, leftEyePosition, rightEyePosition, rightEarPos);
                drawRightEar(tryOnPojo.getImage(), canvas, rightEarTip, rightEarPos, width, mouthBottomPosition, noseBasePosition, leftEyePosition, rightEyePosition, leftEarPos);
                Log.d(TAG, "drawwww: YOU CHOSE EARRINGS");
                break;

        }


    }

    private void drawEye(Canvas canvas, PointF eyePosition, float eyeRadius,
                         PointF irisPosition, float irisRadius, boolean isOpen,
                         boolean isSmiling) {
        if (isOpen) {
            canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyeWhitesPaint);
            if (!isSmiling) {
                canvas.drawCircle(irisPosition.x, irisPosition.y, irisRadius, mEyeIrisPaint);
            } else {
                mHappyStarGraphic.setBounds((int) (irisPosition.x - irisRadius),
                        (int) (irisPosition.y - irisRadius),
                        (int) (irisPosition.x + irisRadius),
                        (int) (irisPosition.y + irisRadius));
                mHappyStarGraphic.draw(canvas);
            }
        } else {
            canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyeLidPaint);
            float y = eyePosition.y;
            float start = eyePosition.x - eyeRadius;
            float end = eyePosition.x + eyeRadius;
            canvas.drawLine(start, y, end, y, mEyeOutlinePaint);
        }
        canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyeOutlinePaint);
    }

    private void drawNose(Drawable drawable, Canvas canvas, PointF noseBasePosition, PointF leftEyePosition, PointF rightEyePosition, float noseWidth, PointF rightEarPos) {
//        final float NOSE_WIDTH_SCALE_FACTOR = 0.25f;
//
//        int t = (int) (noseBasePosition.x - rightEarPos.x);
//
//        double c = (int) (noseBasePosition.y - ((leftEyePosition.y + rightEyePosition.y)/2)) / 2;
//
//        int left = (int) (noseBasePosition.x - 8*noseWidth* NOSE_WIDTH_SCALE_FACTOR);
//        int right = (int) (noseBasePosition.x - 4* noseWidth*NOSE_WIDTH_SCALE_FACTOR);
//        int top = (int) ((int) noseBasePosition.y  + noseWidth* NOSE_WIDTH_SCALE_FACTOR);
//        int bottom = (int) ((int) noseBasePosition.y + 3*noseWidth*NOSE_WIDTH_SCALE_FACTOR);
//
//        if(t> 100){
//            mNosePin.setBounds(left, top-50, right, bottom-50);
//            mNosePin.draw(canvas);
//        }
//        else {
//            mNosePin.setBounds(0, 0, 0, 0);
//            mNosePin.draw(canvas);
//        }

        final float NOSE_WIDTH_SCALE_FACTOR = 0.25f;

        int t = (int) (noseBasePosition.x - rightEarPos.x);

        double c = (int) (noseBasePosition.y - ((leftEyePosition.y + rightEyePosition.y) / 2)) / 2;

        int left = (int) (noseBasePosition.x - 8 * noseWidth * NOSE_WIDTH_SCALE_FACTOR);
        int right = (int) (noseBasePosition.x - 4 * noseWidth * NOSE_WIDTH_SCALE_FACTOR);
        int top = (int) ((int) noseBasePosition.y + noseWidth * NOSE_WIDTH_SCALE_FACTOR);
        int bottom = (int) ((int) noseBasePosition.y + 3 * noseWidth * NOSE_WIDTH_SCALE_FACTOR);
//        mNosePin.setBounds(left, top-50, right, bottom-50);
//        mNosePin.draw(canvas);

        mNosePin = drawable;
        if (t > 100) {
            if (Math.abs(lnose - left) > 20 || Math.abs(rnose - right) > 20 || Math.abs(tnose - top) > 20 || Math.abs(bnose - bottom) > 20) {
                mNosePin.setBounds(left - 0, top - 10, right - 0, bottom);
                lnose = left;
                rnose = right;
                bnose = bottom;
                tnose = top;
            } else {
                mNosePin.setBounds(lnose - 0, tnose - 10, rnose - 0, bnose);
            }
        } else {
            mNosePin.setBounds(0, 0, 0, 0);

        }
        mNosePin.draw(canvas);


    }

//    private void drawNecklace(Canvas canvas, PointF mouthBottomPosition, PointF leftEarPos, PointF rightEarPos,
//                              PointF faceposiiton, float faceWidth, float faceHeight) {
//
//        float neckCenterY = faceposiiton.y + (faceHeight / 8);
//        float neckWidth = faceWidth / 4;
//        float neckHeight = faceHeight / 2;
//
//        int left = (int) (leftEarPos.x + 25);
//        int right = (int) (rightEarPos.x + 25);
//        int top = (int) (mouthBottomPosition.y + (neckHeight * 1.8));
//        int bottom = (int) (Math.round(Math.min(leftEarPos.y, rightEarPos.y) + (neckHeight / 0.9)));
//        mNecklace.setBounds(left, bottom, right, top);
//        mNecklace.draw(canvas);
//    }

    private void drawNecklace(Drawable drawable, Canvas canvas, PointF mouthBottomPosition, PointF leftEarPos, PointF rightEarPos,
                              PointF faceposiiton, float faceWidth, float faceHeight, PointF mouthLeftPosition, PointF mouthRightPosition, PointF mNoseBasePosition, PointF mRightEyePosition, PointF mLeftEyePosition) {

        float neckCenterY = faceposiiton.y + (faceHeight / 8);
        float neckWidth = faceWidth / 4;
        float neckHeight = (float) (faceHeight / 2.5);

        int s = (int) (mLeftEyePosition.x - mRightEyePosition.x);
        int t = (int) (mRightEyePosition.y - mLeftEyePosition.y);
        int t1 = (int) Math.abs((mLeftEyePosition.x - leftEarPos.x));
        int t2 = (int) Math.abs((mRightEyePosition.x - rightEarPos.x));

        int x = (int) (Math.abs(leftEarPos.y + rightEarPos.y) / 2 - (mouthLeftPosition.y + mouthRightPosition.y) / 2);

        int left = (int) (mouthLeftPosition.x + faceHeight / 2.5);
        int right = (int) (mouthRightPosition.x - faceHeight / 2.5);
        int top = (int) (faceposiiton.y + (faceHeight * 1.2) + x);
        int bottom = (int) (Math.round(faceposiiton.y + (faceHeight * 2) + x));
        //todo: uncomment below
        //mNecklace = FaceActivity.hereIsDrawable();
        mNecklace = drawable;
        if (mNecklace != null) {

            if (Math.abs(lneck - left) > 25 || Math.abs(rneck - right) > 25 || Math.abs(tneck - top) > 25 || Math.abs(bneck - bottom) > 25) {
                mNecklace.setBounds(right, top, left, bottom);
                lneck = left;
                rneck = right;
                bneck = bottom;
                tneck = top;
            } else {
                mNecklace.setBounds(rneck, tneck, lneck, bneck);
            }
            mNecklace.draw(canvas);
        } else {

            Log.d(TAG, "drawNecklaceNull: PLACE DEFAULT NECKLACE");

        }

//        mNecklace.setBounds(right, top, left, bottom);
//        mNecklace.draw(canvas);
//        Log.d(TAG, "neck: " + mouthLeftPosition.x + "//" + mouthRightPosition.x + "//" + left + "//" + right);

    }


    private void drawMustache(Canvas canvas, PointF noseBasePosition, PointF mouthLeftPosition, PointF mouthRightPosition) {
        int left = (int) mouthLeftPosition.x;
        int top = (int) noseBasePosition.y;
        int right = (int) mouthRightPosition.x;
        int bottom = (int) Math.min(mouthLeftPosition.y, mouthRightPosition.y);
        if (mIsFrontFacing) {
            mMustacheGraphic.setBounds(left, top, right, bottom);
        } else {
            mMustacheGraphic.setBounds(right, top, left, bottom);
        }
        mMustacheGraphic.draw(canvas);
    }

//    private void drawLeftEar(Canvas canvas, PointF leftTip, PointF leftPos, float faceWidth) {
//
//        float jWidth = faceWidth / 7;
//
//        int left = (int) Math.round(leftPos.x - 50);
//        int top = (int) Math.round(leftTip.y + 1.75 * (leftPos.y - leftTip.y));
//        int right = (int) Math.round(leftPos.x + jWidth - 50);
//        int bottom = (int) Math.round(leftPos.y + 1.75 * (leftPos.y - leftTip.y));
//
//        Log.d(TAG, "drawLeftEar: " + right + "//" + bottom + "//" + left + "//" + top);
//        mLeftEaring.setBounds(right, bottom, left, top);
//        mLeftEaring.draw(canvas);
//    }
//
//    private void drawRightEar(Canvas canvas, PointF rightTip, PointF rightPos, float faceWidth) {
//
//        Log.d(TAG, "drawRightEar: YEAH AAYA = " + rightPos.x);
//
//        float jWidth = faceWidth / 7;
//
//        int left = (int) Math.round(rightPos.x);
//        int top = (int) Math.round(rightTip.y + 1.75 * (rightPos.y - rightTip.y));
//        int right = (int) Math.round(rightPos.x - jWidth);
//        int bottom = (int) Math.round(rightPos.y + 1.75 * (rightPos.y - rightTip.y));
//
//        Log.d(TAG, "drawRightEar: YEH LAGAYA =" + left);
//        mLeftEaring.setBounds(right, bottom, left, top);
//        mLeftEaring.draw(canvas);
//
////        int left = (int) mouthLeftPosition.x;
////        int top = (int) noseBasePosition.y;
////        int right = (int) mouthRightPosition.x;
////        int bottom = (int) Math.min(mouthLeftPosition.y, mouthRightPosition.y);
////        if (mIsFrontFacing) {
////            mLeftEaring.setBounds(left, top, right, bottom);
////        } else {
////            mLeftEaring.setBounds(right, top, left, bottom);
////        }
////        mLeftEaring.draw(canvas);
//    }

    private void drawLeftEar(Drawable drawable, Canvas canvas, PointF leftTip, PointF leftEarPos, float faceWidth, PointF mouthBottomPosition, PointF mNoseBasePosition, PointF mLeftEyePosition, PointF mRightEyePosition, PointF rightEarPos) {

//        float jWidth = faceWidth / 2;
//        int s  = (int) (mLeftEyePosition.x - mRightEyePosition.x);
//        int t = (int) (leftEarPos.x - mNoseBasePosition.x );
//
//        int left = (int) Math.round(mNoseBasePosition.x + t);
//        int top = (int) Math.round(leftEarPos.y + s*0.3);
//        int right = (int) Math.round(mNoseBasePosition.x + t + (s/3));
//        int bottom = (int) Math.round(leftEarPos.y + s);
//
//        Log.d(TAG, "drawLeftEar: " + left + "//" + top + "//" + right + "//" + bottom + "//" + t + "//" + mNoseBasePosition.x);
//        mLeftEaring = StartActivity.getDownloadedDrawable();
//        if(t> 100){
//            mLeftEaring.setBounds(left-25, top, right-25, bottom);
//            mLeftEaring.draw(canvas);
//        }
//        else{
//            mLeftEaring.setBounds(0, 0, 0, 0);
//            mLeftEaring.draw(canvas);
//        }

        float jWidth = faceWidth / 2;
        int s = (int) (mLeftEyePosition.x - mRightEyePosition.x);
        int t = (int) (leftEarPos.x - mNoseBasePosition.x);

        int left = (int) Math.round(mNoseBasePosition.x + t);
        int top = (int) Math.round(leftEarPos.y + s * 0.3);
        int right = (int) Math.round(mNoseBasePosition.x + t + (s / 3));
        int bottom = (int) Math.round(leftEarPos.y + s);

        Log.d(TAG, "drawLeftEar: " + left + "//" + top + "//" + right + "//" + bottom + "//" + t + "//" + mNoseBasePosition.x);

        mLeftEaring = drawable;
        if (t > 100) {
            if (Math.abs(lear - left) > 25 || Math.abs(rear - right) > 25 || Math.abs(tear - top) > 25 || Math.abs(bear - bottom) > 25) {
                mLeftEaring.setBounds(left - 25, top, right - 25, bottom);
                lear = left;
                rear = right;
                bear = bottom;
                tear = top;
            } else {
                mLeftEaring.setBounds(lear - 25, tear, rear - 25, bear);
            }
        } else {
            mLeftEaring.setBounds(0, 0, 0, 0);

        }
        mLeftEaring.draw(canvas);

    }

    private void drawRightEar(Drawable drawable, Canvas canvas, PointF leftTip, PointF rightEarPos, float faceWidth, PointF mouthBottomPosition, PointF mNoseBasePosition, PointF mLeftEyePosition, PointF mRightEyePosition, PointF leftEarPos) {

//        float jWidth = faceWidth / 2;
//        int s  = (int) (mLeftEyePosition.x - mRightEyePosition.x);
//        int t = (int) (mNoseBasePosition.x - rightEarPos.x);
//
////        int t = (int) (leftEarPos.x - mNoseBasePosition.x );
//
//
//        int left = (int) Math.round(mNoseBasePosition.x - t*1.18);
//        int top = (int) Math.round(rightEarPos.y + s*0.3);
//        int right = (int) Math.round(mNoseBasePosition.x - t*1.18 + (s/3));
//        int bottom = (int) Math.round(rightEarPos.y + s);
//
//        Log.d(TAG, "drawRightEar: " + left + "//" + top + "//" + right + "//" + bottom + "//" + t + "//" + mNoseBasePosition.x);
//
//        mLeftEaring = StartActivity.getDownloadedDrawable();
//        if(t> 100){
//            mLeftEaring.setBounds(left, top, right, bottom);
//            mLeftEaring.draw(canvas);
//        }
//        else {
//            mLeftEaring.setBounds(0, 0, 0, 0);
//            mLeftEaring.draw(canvas);
//
//
//        }

        float jWidth = faceWidth / 2;
        int s = (int) (mLeftEyePosition.x - mRightEyePosition.x);
        int t = (int) (mNoseBasePosition.x - rightEarPos.x);

//        int t = (int) (leftEarPos.x - mNoseBasePosition.x );


        int left = (int) Math.round(mNoseBasePosition.x - t * 1.18);
        int top = (int) Math.round(rightEarPos.y + s * 0.3);
        int right = (int) Math.round(mNoseBasePosition.x - t * 1.18 + (s / 3));
        int bottom = (int) Math.round(rightEarPos.y + s);

        Log.d(TAG, "drawRightEar: " + left + "//" + top + "//" + right + "//" + bottom);


        mLeftEaring = drawable;
        if (t > 100) {
            if (Math.abs(lear1 - left) > 50 || Math.abs(rear1 - right) > 50 || Math.abs(tear1 - top) > 50 || Math.abs(bear1 - bottom) > 50) {
                mLeftEaring.setBounds(left - 25, top, right - 25, bottom);
                Log.d(TAG, "drawRight: " + (lear1 - left) + "//" + (tear1 - top) + "//" + (rear1 - right) + "//" + (bear1 - bottom));

                lear1 = left;
                rear1 = right;
                bear1 = bottom;
                tear1 = top;
            } else {
                mLeftEaring.setBounds(lear1, tear1, rear1, bear1);
                Log.d(TAG, "drawRightEarnew: " + lear1 + "//" + tear1 + "//" + rear1 + "//" + bear1);

            }
        } else {
            mLeftEaring.setBounds(0, 0, 0, 0);

        }
        mLeftEaring.draw(canvas);
    }

    private void drawMaangTeeka(Drawable drawable, Canvas canvas, PointF facePosition, float faceWidth, float faceHeight, PointF noseBasePosition) {
        float hatCenterY = facePosition.y + (faceHeight / 8);
        float hatWidth = faceWidth / 4;
        float hatHeight = faceHeight / 6;

        int left = (int) (noseBasePosition.x - (hatWidth / 2.2));
        int right = (int) (noseBasePosition.x + (hatWidth / 2.2));
        int top = (int) (hatCenterY - 2 * (hatHeight));
        int bottom = (int) (hatCenterY + (hatHeight / 2));
        mMaangTeeka = drawable;
        mMaangTeeka.setBounds(left, top, right, bottom);
        mMaangTeeka.draw(canvas);
    }

}