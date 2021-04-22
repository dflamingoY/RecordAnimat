package com.dflamingo.wecahtwave

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dflamingo.wecahtwave.audio.VoiceRecorder
import com.dflamingo.wecahtwave.widget.RecordWaveView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var record: VoiceRecorder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    0x00
                )
            } else {
                tvRecord.visibility = View.VISIBLE
            }
        } else {
            tvRecord.visibility = View.VISIBLE
        }
        tvRecord.setOnClickListener {
            if (!isStarted) {
                initRecord()
                startRecord()
            } else {
                endRecord()
            }
        }
    }

    private var isStarted = false

    private fun initRecord() {
        if (record == null) {
            record = VoiceRecorder()
            recordView.setAmpListener(amlListener)
        }
    }

    private val amlListener = object : RecordWaveView.WaveValueListener {
        override fun getValue(): Int {
            return record?.maxAmplitude ?: 0
        }
    }

    private fun startRecord() {
        linearBubble.visibility = View.VISIBLE
        record?.startRecording(this)
        timer.base = SystemClock.elapsedRealtime()
        timer.start()
        isStarted = true
        tvRecord.text = "结束"
    }

    private fun endRecord() {
        timer.stop()
        timer.base = SystemClock.elapsedRealtime()
        record?.discardRecording()
        tvRecord.text = "录制"
        linearBubble.visibility = View.GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0x00) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tvRecord.visibility = View.VISIBLE
            }
        }
    }

}