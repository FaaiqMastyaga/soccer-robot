package com.example.player

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.navigation.ui.AppBarConfiguration
import com.example.player.databinding.ActivityMainBinding
import com.example.player.helperWiFi.WiFiActivityWrapper
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.google.android.material.slider.Slider
import androidx.core.view.ViewCompat

class MainActivity : WiFiActivityWrapper() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var lastMessageSentTime: Long = 0

    private var speed = 1
    private var forward = 0
    private var backward = 0
    private var right = 0
    private var left = 0
    private var rotateRight = 0
    private var rotateLeft = 0

    private var kick = 0
    private var dribble = 0

    private val defaultAngle = 80
    private var angle = defaultAngle

    @SuppressLint("CheckResult", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val connectButton = binding.btnConnect

        val speedSlider = binding.sliderSpeed
        val forwardButton = binding.btnForward
        val backwardButton = binding.btnBackward
        val rightButton = binding.btnRight
        val leftButton = binding.btnLeft
        val rotateRightButton = binding.btnRotateRight
        val rotateLeftButton = binding.btnRotateLeft

        val kickButton = binding.btnKick
        val dribbleButton = binding.btnDribble

        connectAddress(binding.addressText.text.toString())

        sendWiFiMessage(speed, forward, backward, right, left, rotateRight, rotateLeft, dribble, kick)

        speedSlider.addOnChangeListener { slider, value, fromUser ->
            speed = value.toInt()
            binding.textSpeed.text = "Speed: $speed"
            sendWiFiMessage(speed, forward, backward, right, left, rotateRight, rotateLeft, dribble, kick)
        }

        connectButton.setOnClickListener {
            connectAddress(binding.addressText.text.toString())
        }

        forwardButton.onTouch(0) { isPressed ->
            forward = if (isPressed) 1 else 0
            forwardButton.updateState(isPressed, false)
            sendWiFiMessage(speed, forward, backward, right, left, rotateRight, rotateLeft, dribble, kick)
        }

        backwardButton.onTouch(0) { isPressed ->
            backward = if (isPressed) 1 else 0
            backwardButton.updateState(isPressed, false)
            sendWiFiMessage(speed, forward, backward, right, left, rotateRight, rotateLeft, dribble, kick)
        }

        rightButton.onTouch(0) {isPressed ->
            right = if (isPressed) 1 else 0
            rightButton.updateState(isPressed, false)
            sendWiFiMessage(speed, forward, backward, right, left, rotateRight, rotateLeft, dribble, kick)
        }

        leftButton.onTouch(0) {isPressed ->
            left = if (isPressed) 1 else 0
            leftButton.updateState(isPressed, false)
            sendWiFiMessage(speed, forward, backward, right, left, rotateRight, rotateLeft, dribble, kick)
        }

        rotateRightButton.onTouch(0) {isPressed ->
            rotateRight = if (isPressed) 1 else 0
            rotateRightButton.updateState(isPressed, true)
            sendWiFiMessage(speed, forward, backward, right, left, rotateRight, rotateLeft, dribble, kick)
        }

        rotateLeftButton.onTouch(0) {isPressed ->
            rotateLeft = if (isPressed) 1 else 0
            rotateLeftButton.updateState(isPressed, true)
            sendWiFiMessage(speed, forward, backward, right, left, rotateRight, rotateLeft, dribble, kick)
        }

        dribbleButton.setOnClickListener {
            if (dribble == 0) {
                dribble = 1
                sendWiFiMessage(speed, forward, backward, right, left, rotateRight, rotateLeft, dribble, kick)
            } else if (dribble == 1) {
                dribble = 0
                sendWiFiMessage(speed, forward, backward, right, left, rotateRight, rotateLeft, dribble, kick)
            }
            sendWiFiMessage(speed, forward, backward, right, left, rotateRight, rotateLeft, dribble, kick)
        }

        kickButton.setOnClickListener {
            kick = 1
            sendWiFiMessage(speed, forward, backward, right, left, rotateRight, rotateLeft, dribble, kick)
            kick = 0
            sendWiFiMessage(speed, forward, backward, right, left, rotateRight, rotateLeft, dribble, kick)
        }

        val mainHandler = Handler(Looper.getMainLooper())
        val components =
            listOf(speedSlider, forwardButton, backwardButton, rightButton, leftButton, rotateRightButton, rotateLeftButton, kickButton, dribbleButton)
        components.forEach { component ->
            mainHandler.post(object : Runnable {
                override fun run() {
                    if (!component.isEnabled) {
                        component.isEnabled = true
                    }
                    mainHandler.postDelayed(this, 200)
                }
            })
        }
    }

    private fun printState() {
        binding.state.text =
            "Forward: $forward\nBackward: $backward\nAngle: $angle\nDribble: $dribble\nKick: $kick"
    }

    // View.onTouch() listener with user interaction flag
    @SuppressLint("ClickableViewAccessibility")
    private fun View.onTouch(initialState: Int, onStateChanged: (Boolean) -> Unit) {
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

    private fun ImageButton.updateState(isPressed: Boolean, rotate: Boolean) {
        // change button color
        val colorResId = if (isPressed) R.color.dir_button_pressed else R.color.dir_button_released
        setBackgroundColor(ContextCompat.getColor(context, colorResId))
        // change button icon
        if (!rotate) {
            val iconDrawable =
                if (isPressed) R.drawable.arrow_white_foreground else R.drawable.arrow_black_foreground
            setImageDrawable(ContextCompat.getDrawable(context, iconDrawable))
        }
    }
}