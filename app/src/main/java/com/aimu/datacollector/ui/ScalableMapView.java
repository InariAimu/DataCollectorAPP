package com.aimu.datacollector.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aimu.datacollector.R;

/**
 * 具有缩放和选点功能的地图显示控件
 * Created by Itsuka Kotori on 2017/4/24.
 */
public class ScalableMapView extends ImageView
{
    private final static String TAG = "ScalableMapView";

    public TextView tw;

    private GestureDetector mGestureDetector;

    private int mBitmapId;
    private Bitmap mBitmap;
    /**
     * 模板Matrix，用以初始化
     */
    private Matrix mMatrix = new Matrix();
    /**
     * 图片长度
     */
    private float mImageWidth;
    /**
     * 图片高度
     */
    private float mImageHeight;

    public PointF pressPoint = new PointF();

    public ScalableMapView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        MatrixTouchListener mListener = new MatrixTouchListener();
        setOnTouchListener(mListener);
        mGestureDetector = new GestureDetector(getContext(), new GestureListener(mListener));
        setBackgroundColor(Color.WHITE);
        //将缩放类型设置为FIT_CENTER，表示把图片按比例扩大/缩小到View的宽度，居中显示
        setScaleType(ScaleType.FIT_CENTER);
    }

    public void setImageBitmapById(int id)
    {
        mBitmapId = id;
        mBitmap = BitmapFactory.decodeResource(getResources(), mBitmapId);
        this.setImageBitmap(mBitmap);
    }

    @Override
    public void setImageBitmap(Bitmap bm)
    {
        // TODO Auto-generated method stub

        setScaleType(ScaleType.FIT_CENTER);
        mBitmap = bm;
        super.setImageBitmap(mBitmap);
        //设置完图片后，获取该图片的坐标变换矩阵
        mMatrix.set(getImageMatrix());
        float[] values = new float[9];
        mMatrix.getValues(values);
        //图片宽度为屏幕宽度除缩放倍数
        mImageWidth = this.getWidth() / values[Matrix.MSCALE_X];
        mImageHeight = (this.getHeight() - values[Matrix.MTRANS_Y] * 2) / values[Matrix.MSCALE_Y];
        setScaleType(ScaleType.MATRIX);
    }

    public class MatrixTouchListener implements View.OnTouchListener
    {
        /**
         * 拖拉照片模式
         */
        private static final int MODE_DRAG = 1;
        /**
         * 放大缩小照片模式
         */
        private static final int MODE_ZOOM = 2;
        /**
         * 不支持Matrix
         */
        private static final int MODE_UNABLE = 3;
        /**
         * 最大缩放级别
         */
        float mMaxScale = 6;
        /**
         * 双击时的缩放级别
         */
        float mDobleClickScale = 2;
        private int mMode = 0;//
        /**
         * 缩放开始时的手指间距
         */
        private float mStartDis;
        /**
         * 当前Matrix
         */
        private Matrix mCurrentMatrix = new Matrix();

        /**
         * 用于记录开始时候的坐标位置
         */
        private PointF startPoint = new PointF();

        /**
         * @param matrix
         */
        public void SetMatrix(Matrix matrix)
        {
            mCurrentMatrix.set(matrix);
            //mCurrentMatrix.postScale(1, 1, getWidth() / 2, getHeight() / 2);
            //setImageMatrix(mCurrentMatrix);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            // TODO Auto-generated method stub
            switch (event.getActionMasked())
            {
                case MotionEvent.ACTION_DOWN:
                    //设置拖动模式
                    mMode = MODE_DRAG;
                    startPoint.set(event.getX(), event.getY());
                    // isMatrixEnable();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    reSetMatrix();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mMode == MODE_ZOOM)
                    {
                        setZoomMatrix(event);
                    }
                    else if (mMode == MODE_DRAG)
                    {
                        setDragMatrix(event);
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    if (mMode == MODE_UNABLE) return true;
                    mMode = MODE_ZOOM;
                    mStartDis = distance(event);
                    break;
                default:
                    break;
            }

            return mGestureDetector.onTouchEvent(event);
        }

        public void setDragMatrix(MotionEvent event)
        {
            if (isZoomChanged())
            {
                float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
                float dy = event.getY() - startPoint.y; // 得到x轴的移动距离
                //避免和双击冲突,大于10f才算是拖动
                if (Math.sqrt(dx * dx + dy * dy) > 10f)
                {
                    startPoint.set(event.getX(), event.getY());
                    //在当前基础上移动
                    mCurrentMatrix.set(getImageMatrix());
                    float[] values = new float[9];
                    mCurrentMatrix.getValues(values);
                    dx = checkDxBound(values, dx);
                    dy = checkDyBound(values, dy);
                    mCurrentMatrix.postTranslate(dx, dy);
                    setImageMatrix(mCurrentMatrix);
                }
            }
        }

        private boolean isZoomChanged()
        {
            float[] values = new float[9];
            getImageMatrix().getValues(values);
            //获取当前X轴缩放级别
            float scale = values[Matrix.MSCALE_X];
            //获取模板的X轴缩放级别，两者做比较
            mMatrix.getValues(values);
            return scale != values[Matrix.MSCALE_X];
        }

        private float checkDyBound(float[] values, float dy)
        {
            float height = getHeight();
            if (mImageHeight * values[Matrix.MSCALE_Y] < height)
                return 0;
            if (values[Matrix.MTRANS_Y] + dy > 0)
                dy = -values[Matrix.MTRANS_Y];
            else if (values[Matrix.MTRANS_Y] + dy < -(mImageHeight * values[Matrix.MSCALE_Y] - height))
                dy = -(mImageHeight * values[Matrix.MSCALE_Y] - height) - values[Matrix.MTRANS_Y];
            return dy;
        }

        private float checkDxBound(float[] values, float dx)
        {
            float width = getWidth();
            if (mImageWidth * values[Matrix.MSCALE_X] < width)
                return 0;
            if (values[Matrix.MTRANS_X] + dx > 0)
                dx = -values[Matrix.MTRANS_X];
            else if (values[Matrix.MTRANS_X] + dx < -(mImageWidth * values[Matrix.MSCALE_X] - width))
                dx = -(mImageWidth * values[Matrix.MSCALE_X] - width) - values[Matrix.MTRANS_X];
            return dx;
        }

        private float distance(MotionEvent event)
        {
            float dx = event.getX(1) - event.getX(0);
            float dy = event.getY(1) - event.getY(0);
            /** 使用勾股定理返回两点之间的距离 */
            return (float) Math.sqrt(dx * dx + dy * dy);
        }

        private void setZoomMatrix(MotionEvent event)
        {
            //只有同时触屏两个点的时候才执行
            if (event.getPointerCount() < 2) return;
            float endDis = distance(event);// 结束距离
            if (endDis > 10f)
            { // 两个手指并拢在一起的时候像素大于10
                float scale = endDis / mStartDis;// 得到缩放倍数
                mStartDis = endDis;//重置距离
                mCurrentMatrix.set(getImageMatrix());//初始化Matrix
                float[] values = new float[9];
                mCurrentMatrix.getValues(values);

                scale = checkMaxScale(scale, values);
                setImageMatrix(mCurrentMatrix);
            }
        }

        private float checkMaxScale(float scale, float[] values)
        {
            if (scale * values[Matrix.MSCALE_X] > mMaxScale)
                scale = mMaxScale / values[Matrix.MSCALE_X];
            mCurrentMatrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
            return scale;
        }

        private void reSetMatrix()
        {
            if (checkRest())
            {
                mCurrentMatrix.set(mMatrix);
                setImageMatrix(mCurrentMatrix);
            }
        }

        private void isMatrixEnable()
        {
            //当加载出错时，不可缩放
            if (getScaleType() != ScaleType.CENTER)
            {
                setScaleType(ScaleType.MATRIX);
            }
            else
            {
                mMode = MODE_UNABLE;//设置为不支持手势
            }
        }

        private boolean checkRest()
        {
            // TODO Auto-generated method stub
            float[] values = new float[9];
            getImageMatrix().getValues(values);
            //获取当前X轴缩放级别
            float scale = values[Matrix.MSCALE_X];
            //获取模板的X轴缩放级别，两者做比较
            mMatrix.getValues(values);
            return scale < values[Matrix.MSCALE_X];
        }

        public void onDoubleClick()
        {
            float scale = isZoomChanged() ? 1 : mDobleClickScale;
            mCurrentMatrix.set(mMatrix);//初始化Matrix
            mCurrentMatrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mCurrentMatrix);
        }

        public void onLongPress(PointF point)
        {
            float[] matrixValues = new float[9];
            mCurrentMatrix.getValues(matrixValues);
            float dx = matrixValues[2];
            float dy = matrixValues[5];
            float kx = matrixValues[0];
            float ky = matrixValues[4];
            pressPoint.x = (point.x - dx) / kx;
            pressPoint.y = (point.y - dy) / ky;
            Log.i("LongPress", pressPoint.x + "," + pressPoint.y);

            mBitmap = BitmapFactory.decodeResource(getResources(), mBitmapId);
            Bitmap bmp = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas c = new Canvas(bmp);
            Paint p = new Paint();
            p.setColor(Color.BLACK);
            c.drawCircle(pressPoint.x + 4, pressPoint.y + 4, 30 / kx, p);
            p.setColor(Color.RED);
            c.drawCircle(pressPoint.x, pressPoint.y, 30 / kx, p);
            setImageBitmap(bmp);
            if (kx != 1)
            {
                setImageMatrix(mCurrentMatrix);
            }
            tw.setText("位置：" + (int) pressPoint.x + "," + (int) pressPoint.y);
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        private final MatrixTouchListener listener;

        public GestureListener(MatrixTouchListener listener)
        {
            this.listener = listener;
        }

        @Override
        public boolean onDown(MotionEvent e)
        {
            //捕获Down事件
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
            //触发双击事件
            listener.onDoubleClick();
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            // TODO Auto-generated method stub
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            // TODO Auto-generated method stub
            listener.onLongPress(new PointF(e.getX(), e.getY()));
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY)
        {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY)
        {
            // TODO Auto-generated method stub

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onShowPress(MotionEvent e)
        {
            // TODO Auto-generated method stub
            super.onShowPress(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e)
        {
            // TODO Auto-generated method stub
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            // TODO Auto-generated method stub
            return super.onSingleTapConfirmed(e);
        }
    }
}
