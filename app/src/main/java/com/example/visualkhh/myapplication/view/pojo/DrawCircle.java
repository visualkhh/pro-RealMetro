package com.example.visualkhh.myapplication.view.pojo;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Parcel;

/**
 * Created by lht on 16/10/17.
 */

public class DrawCircle extends DrawShape {

    private float x, y;
    private float[] matrixValues = new float[9];

    public DrawCircle(float x, float y, SerializablePaint paint) {
        this.x = x;
        this.y = y;
        this.paint = paint;
    }

    private DrawCircle(Parcel in) {
        paint = (SerializablePaint)in.readSerializable();
        x = in.readFloat();
        y = in.readFloat();
    }

    @Override
    public void draw(Canvas canvas, Matrix matrix) {
        //缩放坐标映射
        matrix.getValues(matrixValues);
        x = x * matrixValues[0] + y * matrixValues[1] + matrixValues[2];
        y = x * matrixValues[3] + y * matrixValues[4] + matrixValues[5];

        canvas.drawCircle(x, y, 4f, paint.setStrokeWidth());
    }

    @Override
    public DrawShape clone(float scale) {
        SerializablePaint clonePaint = new SerializablePaint(paint);
        clonePaint.setScale(scale);

        return new DrawCircle(x, y, clonePaint);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(paint);
        dest.writeFloat(x);
        dest.writeFloat(y);
    }

    // Parcelable CREATOR class
    public static final Creator<DrawCircle> CREATOR = new Creator<DrawCircle>() {
        @Override
        public DrawCircle createFromParcel(Parcel in) {
            return new DrawCircle(in);
        }

        @Override
        public DrawCircle[] newArray(int size) {
            return new DrawCircle[size];
        }
    };
}
