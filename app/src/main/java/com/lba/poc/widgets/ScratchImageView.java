package com.lba.poc.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.lba.poc.reveal.R;
import com.lba.poc.reveal.ScrollListener;

import java.nio.ByteBuffer;

public class ScratchImageView extends AppCompatImageView {

    private static final String TAG = "ScratchImageView";

    private static final float TOUCH_TOLERANCE = 4;

    public static final float STROKE_WIDTH = 12f;

    private float mX, mY;

    private float revealThreshold = 100;

    private int defaultImage = R.drawable.ic_scratch_pattern;

    private Bitmap mScratchBitmap, mShadowBitmap;

    private BitmapDrawable mDrawable;

    private Paint mBitmapPaint, mErasePaint;

    private Path mErasePath, mTouchPath, mShadowPath;

    private Canvas mCanvas, mShadowCanvas;

    private IRevealListener mRevealListener;

    private float mRevealPercent;

    private int mThreadCount = 0;

    private boolean mDrawn, mRevealed, mHasShadow;

    private int mShadowColor;
    private int mOffset;

    private boolean mEnabled = true;


    public ScratchImageView(Context context) {
        super(context);
        init(null);
    }

    public ScratchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ScratchImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * Set the strokes width based on the parameter multiplier.
     *
     * @param multiplier can be 1,2,3 and so on to set the stroke width of the paint.
     */
    public void setStrokeWidth(int multiplier) {
        mErasePaint.setStrokeWidth(multiplier * STROKE_WIDTH);
    }

    private void init(AttributeSet attrs) {

        mErasePath = new Path();
        mTouchPath = new Path();

        mErasePaint = new Paint();
        mErasePaint.setAntiAlias(true);
        mErasePaint.setDither(true);
        mErasePaint.setColor(Color.RED);
        mErasePaint.setStyle(Paint.Style.STROKE);
        mErasePaint.setStrokeJoin(Paint.Join.BEVEL);
        mErasePaint.setStrokeCap(Paint.Cap.ROUND);

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        int strokeWidth = 6;
        int bitmapResource = defaultImage;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ScratchImageView);
            strokeWidth = a.getInteger(R.styleable.ScratchImageView_strokeWidth, strokeWidth);
            defaultImage = a.getResourceId(R.styleable.ScratchImageView_scratchDefaultSource, defaultImage);
            bitmapResource = a.getResourceId(R.styleable.ScratchImageView_scratchSource, defaultImage);
            revealThreshold = a.getFloat(R.styleable.ScratchImageView_revealThreshold, revealThreshold);
            int transparent = ContextCompat.getColor(getContext(), android.R.color.transparent);
            int shadowColor = a.getColor(R.styleable.ScratchImageView_shadowColor, transparent);
            mHasShadow = shadowColor != transparent;
            if (mHasShadow) {
                mShadowColor = shadowColor;
                mOffset = a.getInteger(R.styleable.ScratchImageView_shadowOffset, 3);
            }
        }
        setStrokeWidth(strokeWidth);

        Bitmap scratchBitmap = BitmapFactory.decodeResource(getResources(), bitmapResource);
        mDrawable = new BitmapDrawable(getResources(), scratchBitmap);

        if (revealThreshold > 100F) {
            revealThreshold = 100F;
        }

        setEraserMode();
    }

    public void setDrawable(BitmapDrawable mDrawable) {
        Log.v(TAG, "setDrawable called");
        this.mDrawable = mDrawable;
        if (mDrawn) {
            decorateScratchable();
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        Log.v(TAG, "onSizeChanged called");
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        mScratchBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mScratchBitmap);

        if (mHasShadow) {
            mShadowBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mShadowBitmap.eraseColor(mShadowColor);
            mShadowPath = new Path();
            mShadowCanvas = new Canvas(mShadowBitmap);
        }

        decorateScratchable();
        mDrawn = true;
    }

    private void decorateScratchable() {
        Rect rect = new Rect(0, 0, getWidth(), getHeight());
        mDrawable.setBounds(rect);

        mDrawable.draw(mCanvas);
    }

    public void setmEnabled(boolean mEnabled) {
        this.mEnabled = mEnabled;
    }

    public boolean ismEnabled() {
        return mEnabled;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Canvas c = new Canvas(mScratchBitmap);
        c.drawPath(mErasePath, mErasePaint);

        if (mHasShadow) {
            c = new Canvas(mShadowBitmap);
            c.drawPath(mShadowPath, mErasePaint);
            canvas.drawBitmap(mShadowBitmap, 0, 0, mBitmapPaint);
        }

        canvas.drawBitmap(mScratchBitmap, 0, 0, mBitmapPaint);
    }

    private void touchStart(float x, float y) {
        mErasePath.reset();
        mErasePath.moveTo(x, y);

        if (mHasShadow) {
            mShadowPath.reset();
            mShadowPath.moveTo(x + mOffset, y + mOffset);
        }

        mX = x;
        mY = y;
        mRevealListener.setViewForScroll(false);
    }

    /**
     * clears the scratch area to reveal the hidden image.
     */
    public void clear() {

        int[] bounds = getImageBounds();

        if (bounds == null) return;

        int left = bounds[0];
        int top = bounds[1];
        int right = bounds[2];
        int bottom = bounds[3];

        int width = right - left;
        int height = bottom - top;
        int centerX = left + width / 2;
        int centerY = top + height / 2;

        left = centerX - width / 2;
        top = centerY - height / 2;
        right = left + width;
        bottom = top + height;

        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        mCanvas.drawRect(left, top, right, bottom, paint);
        if (mHasShadow) {
            mShadowCanvas.drawRect(left, top, right, bottom, paint);
        }

        checkRevealed();
        invalidate();
    }

    private void touchMove(float x, float y) {

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mErasePath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);

            if (mHasShadow) {
                mShadowPath.quadTo(mX + mOffset, mY + mOffset, (x + mX) / 2 + mOffset, (y + mY) / 2 + mOffset);
            }

            mX = x;
            mY = y;

            drawPath();
        }

        mTouchPath.reset();
        mTouchPath.addCircle(mX, mY, 30, Path.Direction.CW);

    }

    private void drawPath() {
        mErasePath.lineTo(mX, mY);

        if (mHasShadow) {
            mShadowPath.lineTo(mX + mOffset, mY + mOffset);
        }

        // commit the path to our offscreen
        mCanvas.drawPath(mErasePath, mErasePaint);

        if (mHasShadow) {
            mShadowCanvas.drawPath(mShadowPath, mErasePaint);
        }

        // kill this so we don't double draw
        mTouchPath.reset();

        mErasePath.reset();
        mErasePath.moveTo(mX, mY);

        if (mHasShadow) {
            mShadowPath.reset();
            mShadowPath.moveTo(mX + mOffset, mY + mOffset);
        }

        checkRevealed();
    }

    public void reveal() {
        clear();
    }

    private void touchUp() {
        drawPath();
        mRevealListener.setViewForScroll(true);
        mRevealListener.onTouchUp();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mEnabled) return false;
        if (mRevealed) {
            if (mRevealListener != null) {
                mRevealListener.onRevealed(this);
            }
            return false;
        }

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    public int getColor() {
        return mErasePaint.getColor();
    }

    public Paint getErasePaint() {
        return mErasePaint;
    }

    public void setEraserMode() {
        getErasePaint().setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setRevealListener(IRevealListener listener) {
        this.mRevealListener = listener;
    }

    public boolean isRevealed() {
        if (mRevealed) {
            return mRevealed;
        }
        mRevealed = mRevealPercent * 100 >= revealThreshold;
        return mRevealed;
    }

    private void checkRevealed() {

        if (!isRevealed() && mRevealListener != null) {

            int[] bounds = getImageBounds();

            if (bounds == null) return;

            int left = bounds[0];
            int top = bounds[1];
            int width = bounds[2] - left;
            int height = bounds[3] - top;

            // Do not create multiple calls to compare.
            if (mThreadCount > 1) {
                return;
            }

            mThreadCount++;

            new AsyncTask<Integer, Void, Float>() {

                @Override
                protected Float doInBackground(Integer... params) {

                    try {
                        int left = params[0];
                        int top = params[1];
                        int width = params[2];
                        int height = params[3];

                        Bitmap croppedBitmap = Bitmap.createBitmap(mScratchBitmap, left, top, width, height);
                        return getTransparentPixelPercent(croppedBitmap);
                    } finally {
                        mThreadCount--;
                    }
                }

                public void onPostExecute(Float percentRevealed) {

                    // check if not mRevealed before.
                    boolean revealed = isRevealed();

                    /*if (revealed) {
                        clear();
                        percentRevealed = 1F;
                    }*/

                    if (!revealed) {
                        float oldValue = mRevealPercent;
                        mRevealPercent = percentRevealed;

                        if (oldValue != percentRevealed) {
                            mRevealListener.onRevealPercentChangedListener(ScratchImageView.this, percentRevealed);
                        }
                    } else {
                        mRevealListener.onRevealed(ScratchImageView.this);
                    }
                }
            }.execute(left, top, width, height);

        }
    }

    public int[] getImageBounds() {

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int vwidth = getWidth() - paddingLeft - paddingRight;
        int vheight = getHeight() - paddingBottom - paddingTop;

        int centerX = vwidth / 2;
        int centerY = vheight / 2;

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return null;
        }

        Rect bounds = drawable.getBounds();

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        if (width <= 0) {
            width = bounds.right - bounds.left;
        }

        if (height <= 0) {
            height = bounds.bottom - bounds.top;
        }

        int left;
        int top;

        if (height > vheight) {
            height = vheight;
        }

        if (width > vwidth) {
            width = vwidth;
        }


        ScaleType scaleType = getScaleType();

        switch (scaleType) {
            case FIT_START:
                left = paddingLeft;
                top = centerY - height / 2;
                break;
            case FIT_END:
                left = vwidth - paddingRight - width;
                top = centerY - height / 2;
                break;
            case CENTER:
                left = centerX - width / 2;
                top = centerY - height / 2;
                break;
            default:
                left = paddingLeft;
                top = paddingTop;
                width = vwidth;
                height = vheight;
                break;

        }

        return new int[]{left, top, left + width, top + height};
    }

    /**
     * Finds the percentage of pixels that do are empty.
     *
     * @param bitmap input bitmap
     * @return a value between 0.0 to 1.0 . Note the method will return 0.0 if either of bitmaps are null nor of same size.
     */
    private float getTransparentPixelPercent(Bitmap bitmap) {

        if (bitmap == null) {
            return 0f;
        }

        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getHeight() * bitmap.getRowBytes());
        bitmap.copyPixelsToBuffer(buffer);

        byte[] array = buffer.array();

        int len = array.length;
        int count = 0;

        for (int i = 0; i < len; i++) {
            if (array[i] == 0) {
                count++;
            }
        }

        return ((float) (count)) / len;
    }

    public interface IRevealListener {
        void onRevealed(ScratchImageView iv);

        void onRevealPercentChangedListener(ScratchImageView siv, float percent);

        void onTouchUp();

        void setViewForScroll(boolean isScrollRequired);
    }

}
