package pl.edu.mimuw.students.fouriersphone;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.reverseOrder;


public class Receiver {
    final EditText editText;
    final Button button;

    public Receiver(Activity activity) {
        this.editText = activity.findViewById(R.id.editText);
        button = activity.findViewById(R.id.button);
    }

    private double getpitch(){
        int channel_config = AudioFormat.CHANNEL_IN_MONO;
        int format = AudioFormat.ENCODING_PCM_16BIT;
        int sampleSize = 8000; //8k tylko dziala na emulatorze, na urzadzeniach mozna uzyc 44100 dla lepszej jakosci
        Log.d("receiverLogs", "sampleSize = " + Integer.toString(sampleSize));
        int bufferSize = AudioRecord.getMinBufferSize(sampleSize, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord audioInput = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleSize, channel_config, format, bufferSize);

        short[] audioBuffer = new short[bufferSize / 2];

        audioInput.startRecording();

        boolean shouldContinue = true;

        int totalShortsRead = 0;

        List<Double> frequences = new ArrayList<>();

        int SAMPLE_TO_FFT = 1024;

        List <Short> oldShorts = new ArrayList<>();


        while(shouldContinue) {
            int shortsRead = audioInput.read(audioBuffer, 0, audioBuffer.length);

            for(int i = 0; i < shortsRead && totalShortsRead > 0; i++) {
                oldShorts.add(audioBuffer[i]);
                if(oldShorts.size() == SAMPLE_TO_FFT) {
                    short[] x = new short[SAMPLE_TO_FFT];
                    for(int j = 0; j < SAMPLE_TO_FFT; j++) {
                        x[j] = oldShorts.get(j);
                    }
                    CTFFT fft = new CTFFT();
                    frequences.add(fft.dominant(x));
                    Log.v("receiverLogs", "Frequence: " + Double.toString(frequences.get(frequences.size() - 1)));
                    oldShorts = new ArrayList<>();
                }
            }

            totalShortsRead += shortsRead;
            if(totalShortsRead > sampleSize) {
                shouldContinue = false;
            }

        }

        audioInput.stop();
        audioInput.release();

        Collections.sort(frequences, new Comparator<Double>() {
            @Override
            public int compare(Double lhs, Double rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs < rhs ? -1 : (lhs > rhs) ? 1 : 0;
            }
        });
        if(frequences.size() == 0) {
            return Double.NaN;
        } else {
            return frequences.get(frequences.size() / 2);
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
            button.setText("Receive");
        } else {
            editText.setText("");
            button.setText("StopR");
            t = (new Thread (new runnable()));
            t.start();
        }
    }

    public boolean isAlive() {
        return t != null && t.isAlive();
    }

    private class runnable implements Runnable {
        @Override
        public void run() {
            Log.v("receiverLogs", "Started Runnable");
            for(;;) {
                if(Thread.currentThread().isInterrupted()) {
                    break;
                }
                final String translatedCharacter = Translator.ftos(getpitch());
                final String oldCharacters = editText.getText().toString();
                if(translatedCharacter != "OOV") {

                    editText.post(new Runnable() {
                        @Override
                        public void run() {
                            editText.setText(oldCharacters + translatedCharacter);
                            editText.postInvalidate();
                        }
                    });

                }

            }
            button.post(new Runnable() {
                @Override
                public void run() {
                    button.setText("Receive");
                    button.postInvalidate();
                }
            });
            Log.v("receiverLogs", "Done listening!");
        }
    }
}