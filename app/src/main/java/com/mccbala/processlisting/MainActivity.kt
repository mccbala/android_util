package com.mccbala.processlisting

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.os.Process
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textbox.movementMethod = ScrollingMovementMethod()
        //killProcess(getString(R.string.process_whatsapp))
        //listBackgroundProcesses()
        if (moveTaskToBack(true))
            showToast("App minimized")
        else
            showToast("Unable to minimize the app")
        Timer("Max Sound", false).schedule(timerTask { maxSound() }, 1000, 60000)
    }

    private fun maxSound() {
        val audioManager =
            applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_RING,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
            AudioManager.FLAG_PLAY_SOUND
        )
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            AudioManager.FLAG_PLAY_SOUND
        )
        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
            AudioManager.FLAG_PLAY_SOUND
        )
        Log.d("MAX SOUND", "Max sound enabled")
        this@MainActivity.runOnUiThread(Runnable {
            showToast("Volume maximised")
        })
    }

    private fun killProcess(package_name: String) {

        val manager =
            this@MainActivity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val activities =
            manager.runningAppProcesses

        for (iCnt in activities.indices) {
            //println("APP: " + iCnt + " " + activities[iCnt].processName)
            if (activities[iCnt].processName.contains(package_name)) {
                Process.sendSignal(
                    activities[iCnt].pid,
                    Process.SIGNAL_KILL
                )
                Process.killProcess(activities[iCnt].pid)
                manager.killBackgroundProcesses(package_name)

            }
        }
    }

    private fun listBackgroundProcesses() {

        val pm = packageManager

        val packages =
            pm.getInstalledApplications(PackageManager.GET_META_DATA)

        val packageNames = ArrayList<String>()

        val mActivityManager =
            this@MainActivity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager


        for (packageInfo in packages) {
            packageNames.add(packageInfo.packageName)
        }

        packageNames.sort()

        for (name in packageNames) textbox.append(name + "\n\n")
    }

    private fun showToast(msg: String, showLong: Boolean = false) {
        Toast.makeText(
            this@MainActivity,
            msg,
            if (showLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        ).show()
    }

}
