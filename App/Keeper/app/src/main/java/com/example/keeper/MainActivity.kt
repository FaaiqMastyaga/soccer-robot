package com.example.keeper

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.keeper.databinding.ActivityMainBinding
import com.example.keeper.helperWiFi.WiFiActivityWrapper
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import androidx.core.content.ContextCompat

class MainActivity : WiFiActivityWrapper() {
    private lateinit var binding: ActivityMainBinding

    // Direction variables
    private var right = 0
    private var left = 0

    // Coordinate variables
    private var X = 0
    private var Y = 0
    private var prevX = 0
    private var prevY = 0
    private var rawX = 0
    private var rawY = 0
    private var centerX = 0
    private var centerY = 0

    private var isTouchOnView = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val connectButton = binding.btnConnect
        val imageGoal = binding.imgGoal

        connectButton.setOnClickListener {
            connectAddress(binding.addressText.text.toString())
        }

        imageGoal.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handleTouchDown(view, event)
                }
                MotionEvent.ACTION_MOVE -> {
                    handleTouchMove(view, event)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handleTouchUpOrCancel(view)
                }
            }
            true // Consumes the event
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun View.onTouch(initialState: Int, onStateChanged: (Boolean) -> (Unit)) {
        var state = initialState

        this.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    onStateChanged(true)
                    state = 1
                }

                MotionEvent.ACTION_UP -> {
                    onStateChanged(false)
                    state = 0
                }
            }
            true
        }
    }

    private fun handleTouchDown(view: View, event: MotionEvent) {
        updatePreviousCoordinates()
        updateTouchCoordinates(event)
        updateCoordinatesWithTranslation(view)
        sendWiFiMessage(X, Y)
        printState()

        // Set isTouchOnView to true when touch starts inside the view
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val viewLeft = location[0]
        val viewTop = location[1]
        val viewRight = viewLeft + view.width
        val viewBottom = viewTop + view.height
        isTouchOnView = event.rawX.toInt() in viewLeft..viewRight && event.rawY.toInt() in viewTop..viewBottom
    }

    private fun handleTouchMove(view: View, event: MotionEvent) {
        updateTouchCoordinates(event)

        // Check if touch moved outside the view
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val viewLeft = location[0]
        val viewTop = location[1]
        val viewRight = viewLeft + view.width
        val viewBottom = viewTop + view.height
        val isTouchOutside = ! (event.rawX.toInt() in viewLeft..viewRight && event.rawY.toInt() in viewTop..viewBottom)

        if (isTouchOutside) {
            isTouchOnView = false // Reset flag if touch moved outside
        } else if (!isTouchOnView) {
            isTouchOnView = true
        }

        updateCoordinatesWithTranslation(view)
        sendWiFiMessage(X, Y)
        printState()
    }

    private fun handleTouchUpOrCancel(view: View) {
        resetCoordinates()
        sendWiFiMessage(X, Y)
        isTouchOnView = false // Reset flag on touch up or cancel
    }

    private fun updatePreviousCoordinates() {
        prevX = X
        prevY = Y
    }

    private fun updateTouchCoordinates(event: MotionEvent) {
        rawX = event.rawX.toInt()
        rawY = event.rawY.toInt()
    }

    private fun calculateCenter(view: View) {
        val location = IntArray(2)
        view.getLocationOnScreen(location)

        centerX = location[0] + view.width / 2
        centerY = location[1] + view.height / 2
    }

    private fun updateCoordinatesWithTranslation(view: View) {
        calculateCenter(view)
        if (isTouchOnView) {
            X = rawX - centerX
            Y = rawY - centerY
        }
    }

    private fun resetCoordinates() {
        X = 0
        Y = 0
    }

    private fun printState() {
        binding.state.text = "X: $X\nY: $Y"

        if (isTouchOnView) {
            // Update cursor position
            val cursorView = binding.cursorView
            // Convert to float for smoother movement
            // Minus constant to calibrate position of the cursor
            cursorView.x = X.toFloat() + centerX - 100
            cursorView.y = Y.toFloat() + centerY - 150
            println("cursorview x: ${cursorView.x}")
            println("cursorview y: ${cursorView.y}")
        }
    }

    private fun ImageButton.updateState(isPressed: Boolean) {
        // change button color
        val colorResId = if (isPressed) R.color.dir_button_pressed else R.color.dir_button_released
        setBackgroundColor(ContextCompat.getColor(context, colorResId))
        // change button icon
        val iconDrawable =
            if (isPressed) R.drawable.arrow_white_foreground else R.drawable.arrow_black_foreground
        setImageDrawable(ContextCompat.getDrawable(context, iconDrawable))
    }
}