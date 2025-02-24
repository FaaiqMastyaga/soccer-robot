package com.example.player.helperWiFi

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL


abstract class WiFiActivityWrapper : AppCompatActivity() {
    private val PERMISSIONS_INTERNET = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE
    )

    private var address: String ="192.168.4.1";

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_INTERNET, 1)
        }
    }

    fun connectAddress(addressInput: String) {
        checkPermissions()
        address = addressInput
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendHttpGetRequest(host: String, query: String) {
        val url = "http://$host/?$query"
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    launch(Dispatchers.Main) {
//                        Toast.makeText(this@WiFiActivityWrapper, "Connected successfully", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    launch(Dispatchers.Main) {
//                        Toast.makeText(this@WiFiActivityWrapper, "Connection failed with status: $responseCode", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
//                        Toast.makeText(this@WiFiActivityWrapper, "Connection failed with status: $responseCode", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun sendWiFiMessage(speed: Int, forward: Int, backward: Int,
                        right: Int, left: Int, rotateRight: Int, rotateLeft: Int,
                        dribble: Int, kick: Int) {
        val speedString = speed.toString()
        val forwardString = forward.toString()
        val backwardString = backward.toString()
        val rightString = right.toString()
        val leftString = left.toString()
        val rotateRightString = rotateRight.toInt()
        val rotateLeftString = rotateLeft.toInt()
        val dribbleString = dribble.toString()
        val kickString = kick.toString()

        val queryParams ="speed=$speedString&forward=$forwardString&backward=$backwardString&right=$rightString&left=$leftString&rotate_right=$rotateRightString&rotate_left=$rotateLeftString&dribble=$dribbleString&kick=$kickString"
        sendHttpGetRequest(address, queryParams)
    }
}