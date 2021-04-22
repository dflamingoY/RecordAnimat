package com.dflamingo.wecahtwave.audio;

import android.content.Context;
import android.media.MediaRecorder;
import android.text.format.Time;


import java.io.File;
import java.io.IOException;
import java.util.Date;

public class VoiceRecorder {
    MediaRecorder recorder;

    static final String PREFIX = "voice";
    static final String EXTENSION = ".amr";

    private boolean isRecording = false;
    private long startTime;
    private String voiceFilePath = null;
    private String voiceFileName = null;
    private File file;

    public VoiceRecorder() {

    }

    /**
     * start recording to the file
     */
    public String startRecording(Context appContext) {
        file = null;
        try {
            if (recorder != null) {
                recorder.release();
                recorder = null;
            }
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setAudioChannels(1);
            recorder.setAudioSamplingRate(8000);
            recorder.setAudioEncodingBitRate(64);
            voiceFileName = getVoiceFileName("");
            File rootFile = new File(appContext.getExternalFilesDir(""), ".cache/audio");
            if (!rootFile.exists()) {
                rootFile.mkdirs();
            }
            voiceFilePath = rootFile.getAbsolutePath() + "/" + voiceFileName;
            file = new File(voiceFilePath);
            recorder.setOutputFile(file.getAbsolutePath());
            recorder.prepare();
            isRecording = true;
            recorder.start();
        } catch (IOException e) {
        }
        startTime = new Date().getTime();
        return file == null ? null : file.getAbsolutePath();
    }

    public int getMaxAmplitude() {
        int wave = 0;
        try {
            if (isRecording && null != recorder) {
                wave = recorder.getMaxAmplitude() * 13 / 0x7FFF;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return wave;
    }

    /**
     * stop the recoding
     *
     * @return seconds of the voice recorded
     */
    public void discardRecording() {
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
                if (file != null && file.exists() && !file.isDirectory()) {
                    file.delete();
                }
            } catch (IllegalStateException e) {
            } catch (RuntimeException e) {
            }
            isRecording = false;
        }
    }

    public int stopRecoding() {
        if (recorder != null) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;

            if (file == null || !file.exists() || !file.isFile()) {
                return 401;
            }
            if (file.length() == 0) {
                file.delete();
                return 401;
            }
            int seconds = (int) (new Date().getTime() - startTime) / 1000;
            return seconds;
        }
        return 0;
    }

    protected void finalize() throws Throwable {
        super.finalize();
        if (recorder != null) {
            recorder.release();
        }
    }

    private String getVoiceFileName(String uid) {
        Time now = new Time();
        now.setToNow();
        return uid + now.toString().substring(0, 15) + EXTENSION;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void onDestroy() {
        try {
            if (recorder != null) {
                recorder.stop();
            }
            recorder = null;
        } catch (IllegalStateException e) {
        }
    }

    public String getVoiceFilePath() {
        return voiceFilePath;
    }

    public String getVoiceFileName() {
        return voiceFileName;
    }
}
