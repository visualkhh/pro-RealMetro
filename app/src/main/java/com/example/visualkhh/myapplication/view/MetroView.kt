package com.example.visualkhh.myapplication.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.util.Log
import java.util.ArrayList
import android.graphics.*
import android.view.ScaleGestureDetector
import com.example.visualkhh.myapplication.view.domain.MetroViewScaleMinMax


class MetroView :View, View.OnTouchListener{

    private var scaleFactor = 1f
    private var detector: ScaleGestureDetector? = null

    private var mode: Int = 0

    //These two variables keep track of the X and Y coordinate of the finger when it first
    //touches the screen
    private var startX = 0f
    private var startY = 0f

    //These two variables keep track of the amount we need to translate the canvas along the X
    //and the Y coordinate
    private var translateX = 0f
    private var translateY = 0f

    //These two variables keep track of the amount we translated the X and Y coordinates, the last time we
    //panned.
    private var previousTranslateX = 0f
    private var previousTranslateY = 0f
    private var dragged: Boolean = false
    /////////



    var draws = ArrayList<MetroDrawable>()

    var firstDownPoint: PointF? = null
    var lastDownPoint: PointF? = null
    var upPoint: PointF = PointF(0f,0f)
    var movePoint: PointF = PointF(0f,0f)




    constructor(context: Context?) : super(context){
        init(context)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        init(context)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init(context)
    }

    fun init(context: Context?){
        setOnTouchListener(this);
        setFocusableInTouchMode(true);  // 이벤트가 계속해서 발생하기 위해
        detector = ScaleGestureDetector(getContext(), ScaleListener())
//        firstDownPoint = PointF(width/2f, height/2f)
//        upPoint = PointF(width/2f, height/2f)
    }




    override fun onTouch(view: View, event: MotionEvent): Boolean {





        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mode = DRAG
                //We assign the current X and Y coordinate of the finger to startX and startY minus the previously translated
                //amount for each coordinates This works even when we are translating the first time because the initial
                //values for these two variables is zero.
                startX = event.x - previousTranslateX
                startY = event.y - previousTranslateY
            }

            MotionEvent.ACTION_MOVE -> {
                translateX = event.x - startX
                translateY = event.y - startY
                Log.d("MetroView Event", "tx:"+translateX+" ty:"+translateY)
                //We cannot use startX and startY directly because we have adjusted their values using the previous translation values.
                //This is why we need to add those values to startX and startY so that we can get the actual coordinates of the finger.
                val distance = Math.sqrt(Math.pow((event.x - (startX + previousTranslateX)).toDouble(), 2.0) + Math.pow((event.y - (startY + previousTranslateY)).toDouble(), 2.0)
                )

                if (distance > 0) {
                    dragged = true
                }
                invalidate()
            }

            MotionEvent.ACTION_POINTER_DOWN ->{


                val minX = Math.min(event.getX(0),event.getX(1))
                val maxX = Math.max(event.getX(0),event.getX(1))
                val minY = Math.min(event.getY(0),event.getY(1))
                val maxY = Math.max(event.getY(0),event.getY(1))
                val centerX = minX + ((maxX - minX) / 2)
                val centerY = minY + ((maxY - minY) / 2)
                val downPoint = PointF(centerX, centerY)
                if(null==firstDownPoint){
                    firstDownPoint = downPoint
                    lastDownPoint = downPoint
                }else{
                    firstDownPoint = lastDownPoint
                    lastDownPoint = downPoint
                }


                mode = ZOOM
            }

            MotionEvent.ACTION_UP -> {
                mode = NONE
                dragged = false

                //All fingers went up, so let's save the value of translateX and translateY into previousTranslateX and
                //previousTranslate
                previousTranslateX = translateX
                previousTranslateY = translateY
            }

            MotionEvent.ACTION_POINTER_UP -> {
                mode = DRAG
                Log.d("MeetroView Event","ACTION_POINTER_UP")
                if(event.pointerCount==1){
                    upPoint.x = event.x
                    upPoint.y = event.y
                }else if(event.pointerCount>=2){
                    val minX = Math.min(event.getX(0),event.getX(1))
                    val maxX = Math.max(event.getX(0),event.getX(1))
                    val minY = Math.min(event.getY(0),event.getY(1))
                    val maxY = Math.max(event.getY(0),event.getY(1))
                    val centerX = minX + ((maxX - minX) / 2)
                    val centerY = minY + ((maxY - minY) / 2)
                    upPoint.x = centerX
                    upPoint.y = centerY
                }

                //This is not strictly necessary; we save the value of translateX and translateY into previousTranslateX
                //and previousTranslateY when the second finger goes up
                previousTranslateX = translateX
                previousTranslateY = translateY
            }
        }

        detector!!.onTouchEvent(event)

        //We redraw the canvas only in the following cases:
        //
        // o The mode is ZOOM
        //        OR
        // o The mode is DRAG and the scale factor is not equal to 1 (meaning we have zoomed) and dragged is
        //   set to true (meaning the finger has actually moved)
        if (mode == DRAG && scaleFactor != 1f && dragged || mode == ZOOM) {
            invalidate()
        }

        return true




    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        canvas.save()

//        We're going to scale the X and Y coordinates by the same amount
//        lastDownPoint?.let {
//            canvas.scale(scaleFactor, scaleFactor, lastDownPoint!!.x, lastDownPoint!!.y)
//        }
//
        canvas.scale(scaleFactor, scaleFactor)
//
//        canvas.scale(2f, 2f, firstDownPoint.x, firstDownPoint.y)
        Log.d("MetroView onDraw", "scaleFactor:"+scaleFactor+" "+firstDownPoint+"  "+lastDownPoint)

        //If translateX times -1 is lesser than zero, let's set it to zero. This takes care of the left bound
        val mx = translateX
        val my = translateY
        if (translateX * -1 < 0) {
            translateX = 0f
        } else if (translateX * -1 > (scaleFactor - 1) * width) {
            translateX = (1 - scaleFactor) * width
        }//This is where we take care of the right bound. We compare translateX times -1 to (scaleFactor - 1) * displayWidth.
        //If translateX is greater than that value, then we know that we've gone over the bound. So we set the value of
        //translateX to (1 - scaleFactor) times the display width. Notice that the terms are interchanged; it's the same
        //as doing -1 * (scaleFactor - 1) * displayWidth

        if (translateY * -1 < 0) {
            translateY = 0f
        } else if (translateY * -1 > (scaleFactor - 1) * height) {
            translateY = (1 - scaleFactor) * height
        }//We do the exact same thing for the bottom bound, except in this case we use the height of the display

        //We need to divide by the scale factor here, otherwise we end up with excessive panning based on our zoom level
        //because the translation amount also gets scaled according to how much we've zoomed into the canvas.
        canvas.translate(translateX / scaleFactor, translateY / scaleFactor)
//        Log.d("MetroView", "tx : "+translateX+ " s : "+scaleFactor+ " ty : "+translateY)
//        canvas.translate(mx, my)
//        canvas.translate(firstDownPoint.x-upPoint.x, firstDownPoint.y-upPoint.y)
//        if(firstDownPoint!=null && lastDownPoint!=null) {
//            canvas.translate(firstDownPoint!!.x + lastDownPoint!!.x, firstDownPoint!!.y + lastDownPoint!!.y)
//            Log.d("MetroView", "dpx : "+ firstDownPoint!!.x+ " upx : "+upPoint.x+ " dpy : "+ firstDownPoint!!.y+ " dpx : "+upPoint.x)
//        }

        /* The rest of your canvas-drawing code */
        var minMax = MetroViewScaleMinMax(Float.MAX_VALUE, Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE)
        draws.forEach{
            minMax.minY = Math.min(it.getY(), minMax.minY)
            minMax.maxY = Math.max(it.getY(), minMax.maxY)
            minMax.minX = Math.min(it.getX(), minMax.minX)
            minMax.maxX = Math.max(it.getX(), minMax.maxX)
        }
        draws.forEach{it.draw(minMax, canvas)}



//        val paint = Paint()
//        paint.color = Color.BLACK
//        paint.style = Paint.Style.FILL
//        canvas.drawRect(Rect(200,200,250,250), paint)

        /////////
        canvas.restore()
    }







    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM))
            return true
        }
    }

    companion object {

        //These two constants specify the minimum and maximum zoom
        private val MIN_ZOOM = 1f
        private val MAX_ZOOM = 10f

        //These constants specify the mode that we're in
        private val NONE = 0
        private val DRAG = 1
        private val ZOOM = 2
    }
}