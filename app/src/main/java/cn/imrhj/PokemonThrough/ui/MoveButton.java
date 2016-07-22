package cn.imrhj.PokemonThrough.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import cn.imrhj.PokemonThrough.utils.FileIO;
import cn.imrhj.PokemonThrough.utils.MathUtils;
import cn.imrhj.PokemonThrough.utils.Prefs;

/**
 * Created by rhj on 16/7/21.
 */
public class MoveButton extends View {

    private Paint mPaint;
    private Point  mRockerPosition; //摇杆位置
    private Point  mCtrlPoint; //摇杆起始位置
    private int    mRudderRadius;//摇杆半径
    private int    mWheelRadius;//摇杆活动范围半径

    private int mBackColor = 0x1a000000;
    private int mColor = 0x26000000;

    // 是否写入
    private boolean isWrite = false;

    // 按钮的角度
    private float mRadian;
    private int newWidth;

    private int mLength;


    public MoveButton(Context context) {
        super(context);
        init(context);
    }


    public MoveButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MoveButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.setKeepScreenOn(true);

        newWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, context.getResources().getDisplayMetrics());
        mLength = newWidth * 8;
        mRudderRadius = newWidth;
        mWheelRadius = mRudderRadius * 3;
        mCtrlPoint = new Point(mLength / 2, mLength / 2);

        mPaint = new Paint();
        mPaint.setColor(mBackColor);
        mPaint.setAntiAlias(true);//抗锯齿
        mRockerPosition = new Point(mCtrlPoint);


        // 开启 Timer
        Timer timer = new Timer();
        timer.schedule(new WriteTask(), 0, 1000);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int len = MathUtils.getLength(mCtrlPoint.x, mCtrlPoint.y, event.getX(), event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //如果屏幕接触点不在摇杆挥动范围内,则不处理
                if(len >mWheelRadius) {
                    return false;
                }

                isWrite = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if(len <= mWheelRadius) {
                    //如果手指在摇杆活动范围内，则摇杆处于手指触摸位置
                    mRockerPosition.set((int)event.getX(), (int)event.getY());

                }else{
                    //设置摇杆位置，使其处于手指触摸方向的 摇杆活动范围边缘
                    mRockerPosition = MathUtils.getBorderPoint(mCtrlPoint, new Point((int)event.getX(), (int)event.getY()), mWheelRadius);
                }

                if (isWrite) {
                    mRadian = MathUtils.getRadian(mCtrlPoint, new Point((int)event.getX(), (int)event.getY()));
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mRockerPosition = new Point(mCtrlPoint);
                isWrite = false;
                invalidate();
                break;

        }

        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mLength, mLength);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mBackColor);
        canvas.drawCircle(mCtrlPoint.x, mCtrlPoint.y, mWheelRadius, mPaint);//绘制范围
        mPaint.setColor(mColor);
        canvas.drawCircle(mRockerPosition.x, mRockerPosition.y, mRudderRadius, mPaint);//绘制摇杆
    }

    /**
     * 写入文件
     */
    private void writeFile() {
        double x = Math.cos(mRadian) * 0.0005;
        double y = Math.sin(mRadian) * 0.0005;
        addLat(x);
        addLon(y);
    }

    private void addLat(double lat) {
        double oldLat = Double.parseDouble(FileIO.read(Prefs.LAT_PATH));
        FileIO.write(Prefs.LAT_PATH, String.valueOf(oldLat + lat));
    }

    private void addLon(double lon) {
        double oldLon = Double.parseDouble(FileIO.read(Prefs.LON_PATH));
        FileIO.write(Prefs.LON_PATH, String.valueOf(oldLon + lon));
    }


    class WriteTask extends TimerTask {

        @Override
        public void run() {
            if (isWrite) {
                writeFile();
            }
        }
    }
}




