package com.example.visualkhh.myapplication.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView

class ZoomView1 : ImageView {

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

    constructor(context: Context) : super(context) {
        detector = ScaleGestureDetector(getContext(), ScaleListener())
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        detector = ScaleGestureDetector(getContext(), ScaleListener())
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        detector = ScaleGestureDetector(getContext(), ScaleListener())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

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

                //We cannot use startX and startY directly because we have adjusted their values using the previous translation values.
                //This is why we need to add those values to startX and startY so that we can get the actual coordinates of the finger.
                val distance = Math.sqrt(Math.pow((event.x - (startX + previousTranslateX)).toDouble(), 2.0) + Math.pow((event.y - (startY + previousTranslateY)).toDouble(), 2.0)
                )

                if (distance > 0) {
                    dragged = true
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> mode = ZOOM

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

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()

        //We're going to scale the X and Y coordinates by the same amount
        canvas.scale(scaleFactor, scaleFactor)

        //If translateX times -1 is lesser than zero, let's set it to zero. This takes care of the left bound
        if (translateX * -1 < 0) {
            translateX = 0f
        } else if (translateX * -1 > (scaleFactor - 1) * layoutParams.width) {
            translateX = (1 - scaleFactor) * layoutParams.width
        }//This is where we take care of the right bound. We compare translateX times -1 to (scaleFactor - 1) * displayWidth.
        //If translateX is greater than that value, then we know that we've gone over the bound. So we set the value of
        //translateX to (1 - scaleFactor) times the display width. Notice that the terms are interchanged; it's the same
        //as doing -1 * (scaleFactor - 1) * displayWidth

        if (translateY * -1 < 0) {
            translateY = 0f
        } else if (translateY * -1 > (scaleFactor - 1) * layoutParams.height) {
            translateY = (1 - scaleFactor) * layoutParams.height
        }//We do the exact same thing for the bottom bound, except in this case we use the height of the display

        //We need to divide by the scale factor here, otherwise we end up with excessive panning based on our zoom level
        //because the translation amount also gets scaled according to how much we've zoomed into the canvas.
        canvas.translate(translateX / scaleFactor, translateY / scaleFactor)

        /* The rest of your canvas-drawing code */
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
        private val MAX_ZOOM = 5f

        //These constants specify the mode that we're in
        private val NONE = 0
        private val DRAG = 1
        private val ZOOM = 2
    }
}