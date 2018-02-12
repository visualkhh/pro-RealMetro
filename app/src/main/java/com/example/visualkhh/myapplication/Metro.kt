package com.example.visualkhh.myapplication

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.R.attr.y
import android.R.attr.x
import android.graphics.Color
import android.graphics.Paint
import android.util.Log


class Metro :View, View.OnTouchListener{
    constructor(context: Context?) : super(context){
        init();
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        init();
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init();
    }

    private fun init() {
        setOnTouchListener(this);
        setFocusableInTouchMode(true);  // 이벤트가 계속해서 발생하기 위해
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Color.WHITE)
        val paint = Paint()
        paint.color = Color.BLACK
        paint.strokeWidth =5f
        canvas.drawLine(5f,5f, 20f,20f, paint)



//        if (pointList.size() < 2) return
//        for (i in 1 until pointList.size()) {
//            if (pointList.get(i).draw) {
//                paint.setColor(pointList.get(i).mStrokeColor)
//                paint.setStrokeWidth(pointList.get(i).mStrokeWidth)
//                canvas.drawLine(pointList.get(i - 1).x,
//                        pointList.get(i - 1).y, pointList.get(i).x,
//                        pointList.get(i).y, paint)
//            }
//        }
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        var x = event.getX();
        var y = event.getY();


        var r: Boolean = false;
        when(event.getAction()) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("phoro", "손가락으로 터치했음")
                invalidate()         // 그림 다시 그리기
                r=true
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d("phoro", "손가락으로 움직이는 중")
                invalidate()         // 그림 다시 그리기
                r=true
            }

            MotionEvent.ACTION_UP -> {
                Log.d("phoro", "손가락 땠음")
            }
        }
        return r;
    }
}