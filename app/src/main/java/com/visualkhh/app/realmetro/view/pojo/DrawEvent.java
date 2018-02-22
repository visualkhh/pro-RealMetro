package com.visualkhh.app.realmetro.view.pojo;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Parcel;
import com.visualkhh.app.realmetro.DrawEventCallBack;

/**
 * Created by lht on 16/10/17.
 */

public class DrawEvent extends DrawShape {

    private static final int TEXT_RECT_PADDING = 15;

    //文本的坐标位于文本左下角
    private float x = 0, y = 0;
    private String text = "";
    private float[] matrixValues = new float[9];
    private DrawEventCallBack drawEventCallBack = null;

    private Rect rect = new Rect();
    //点击文本框时的偏移量
    private float dx = 0, dy = 0;

    public DrawEvent(SerializablePaint paint) {
        this.paint = paint;
        dx = TEXT_RECT_PADDING * paint.getScale();
        dy = paint.getActualTextSize() / 2;
    }

    public DrawEvent(float x, float y, SerializablePaint paint) {
        this.paint = paint;
        this.x = x;
        this.y = y;
    }

    private DrawEvent(Parcel in) {
        paint = (SerializablePaint)in.readSerializable();
    }

    public DrawEventCallBack getDrawEventCallBack() {
        return drawEventCallBack;
    }

    public void setDrawEventCallBack(DrawEventCallBack drawEventCallBack) {
        this.drawEventCallBack = drawEventCallBack;
    }

    public void setCoordinate(float x, float y) {
        this.x = x - dx;
        this.y = y + dy;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * Get text border rectangle
     * 获取文字边框矩形
     * @return
     */
    public Rect getTextBoundRect() {
        paint.getTextBounds(text, 0, text.length(), rect);
        rect.set((int)(x - TEXT_RECT_PADDING * paint.getScale()),
                (int)(y - paint.getActualTextSize() - TEXT_RECT_PADDING * paint.getScale()),
                (int)(x + rect.width() + TEXT_RECT_PADDING * paint.getScale()),
                (int)(y + TEXT_RECT_PADDING * paint.getScale()));
        return rect;
    }

    /**
     * 判断坐标是否落在文本框内
     * @param x
     * @param y
     * @return
     */
    public boolean isInTextRect(float x, float y) {
        boolean isInTextRect = x >= rect.left && x <= rect.right
                && y >= rect.top && y <= rect.bottom;

        //为可能的拖动计算点击偏移量
        if (isInTextRect) {
            dx = x - rect.left;
            dy = rect.bottom - y;
        }
        return isInTextRect;
    }

    @Override
    public void draw(Canvas canvas, Matrix matrix) {
        //缩放坐标映射
        matrix.getValues(matrixValues);
        x = x * matrixValues[0] + y * matrixValues[1] + matrixValues[2];
        y = x * matrixValues[3] + y * matrixValues[4] + matrixValues[5];

//        canvas.drawText(text, x, y, paint.setStrokeWidth());

        if(drawEventCallBack!=null){
            drawEventCallBack.onDraw(canvas,x,y,paint.setStrokeWidth());
        }
    }

    @Override
    public DrawShape clone(float scale) {
        SerializablePaint clonePaint = new SerializablePaint(paint);
        clonePaint.setScale(scale);

        DrawEvent cloneEvent = new DrawEvent(clonePaint);
        cloneEvent.x = x;
        cloneEvent.y = y;
        cloneEvent.text = text;
        cloneEvent.dx = dx;
        cloneEvent.dy = dy;
        cloneEvent.drawEventCallBack = drawEventCallBack;

        return cloneEvent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    // Parcelable CREATOR class
    public static final Creator<DrawEvent> CREATOR = new Creator<DrawEvent>() {
        @Override
        public DrawEvent createFromParcel(Parcel in) {
            return new DrawEvent(in);
        }

        @Override
        public DrawEvent[] newArray(int size) {
            return new DrawEvent[size];
        }
    };
}
