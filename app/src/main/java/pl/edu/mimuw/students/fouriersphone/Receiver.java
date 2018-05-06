package pl.edu.mimuw.students.fouriersphone;
import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import pl.edu.mimuw.students.fouriersphone.SoundAnalyzer.AnalyzeSound;


public class Receiver {
    final Activity activity;
    final Switch modeSwitch;
    final EditText editText;
    final Button button;
    final Handler handler;
    public Receiver(Activity activity, Handler handler) {
        this.handler = handler;
        this.activity = activity;
        editText = activity.findViewById(R.id.editText);
        button = activity.findViewById(R.id.button);
        modeSwitch = activity.findViewById(R.id.modeSwitch);
    }

    private void getpitch(){

        double frequencies[] = Translator.getFrequencies();



        int channel_config = AudioFormat.CHANNEL_IN_MONO;
        int format = AudioFormat.ENCODING_PCM_16BIT;
        int sampleSize = 44100; //8k tylko dziala na emulatorze, na urzadzeniach mozna uzyc 44100 dla lepszej jakosci
        //Log.d("receiverLogs", "sampleSize = " + Integer.toString(sampleSize));
        int bufferSize = AudioRecord.getMinBufferSize(sampleSize, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord audioInput = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleSize, channel_config, format, bufferSize);

        short[] audioBuffer = new short[bufferSize / 2];

        audioInput.startRecording();

        int SAMPLE_TO_FFT = 4096;

        int FIND_AMOUNT = 5;

        List <Short> oldShorts = new ArrayList<>();

        List <Double> results = new ArrayList<>();


        while(!Thread.currentThread().isInterrupted()) {
            int shortsRead = audioInput.read(audioBuffer, 0, audioBuffer.length);
            for(int i = 0; i < shortsRead; i++) {
                oldShorts.add(audioBuffer[i]);
                if(oldShorts.size() == SAMPLE_TO_FFT) {
                    short[] x = new short[SAMPLE_TO_FFT];
                    for(int j = 0; j < SAMPLE_TO_FFT; j++) {
                        x[j] = oldShorts.get(j);
                    }
                    AnalyzeSound as = new AnalyzeSound();
                    Double frequency = as.analyze(x, frequencies);

                    oldShorts = new ArrayList<>();

                    results.add(frequency);

                    if(results.size() == FIND_AMOUNT) {
                        double lider = results.get(FIND_AMOUNT / 2);
                        int count = 0;
                        for(double f: results) {
                            if(f == lider) count++;
                        }
                        if(count >= (FIND_AMOUNT + 1) / 2) {
                            Message msg = new Message();
                            msg.obj = Double.toString(lider);
                            msg.what = Constants.RECEIVED_MESSAGE_IS_FREQUENCY;
                            handler.sendMessage(msg);
                        }
                        results.remove(0);
                    }


                }
            }
        }

        audioInput.stop();
        audioInput.release();
    }

    public void stop() {
        if(t != null && t.isAlive()) {
            t.interrupt();
            try {
                t.join();
            } catch(InterruptedException  e) {
                Log.v("exception", "InterruptedException");
            }
            button.setText(activity.getString(R.string.receive_button));
        }
    }

    Thread t;
    public void start() {
        Log.v("receiverLogs","Start receiver!");
        if(t != null && t.isAlive()) {
            t.interrupt();
            try {
                t.join();
            } catch(InterruptedException  e) {
                Log.v("exception", "InterruptedException");
            }
            button.setText(activity.getString(R.string.receive_button));
        } else {
            editText.setText("");
            button.setText(activity.getString(R.string.receive_button_stop));
            t = (new Thread (new runnable(activity)));
            t.start();
        }
    }

    public boolean isAlive() {
        return t != null && t.isAlive();
    }

    private class runnable implements Runnable {
        final Activity activity;

        runnable(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            Log.v("receiverLogs", "Started Runnable");

            getpitch();

            button.post(new Runnable() {
                @Override
                public void run() {
                    if (!modeSwitch.isChecked()) {
                        button.setText(activity.getString(R.string.receive_button));
                        button.postInvalidate();
                    }
                }
            });
            Log.v("receiverLogs", "Done listening!");
        }
    }
}