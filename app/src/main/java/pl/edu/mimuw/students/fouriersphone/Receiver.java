package pl.edu.mimuw.students.fouriersphone;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.EditText;

import java.util.Arrays;


public class Receiver implements Runnable {
    EditText editText;

    public Receiver(EditText editText) {
        this.editText = editText;
    }

    public double getpitch(){
        int channel_config = AudioFormat.CHANNEL_IN_MONO;
        int format = AudioFormat.ENCODING_PCM_16BIT;
        int sampleSize = 8000;
        int bufferSize = 2048;
        AudioRecord audioInput = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleSize, channel_config, format, bufferSize);

        short[] audioBuffer = new short[bufferSize];

        audioInput.startRecording();

        int numberOfSamples = 3;
        Double frequences[] = new Double[numberOfSamples];

        for(int i = 0; i < numberOfSamples; i++) {
            audioInput.read(audioBuffer, 0, bufferSize);
            CTFFT fft = new CTFFT();
            frequences[i] = fft.dominant(audioBuffer) / (5.5);
            Log.v("myLogs", "freq is " + Double.toString(frequences[i]));
        }

        audioInput.stop();
        audioInput.release();

        Arrays.sort(frequences);

        return frequences[numberOfSamples / 2];
    }
    @Override
    public void run() {
        Translator translator = new Translator();
        int secondsToListen = 30;
        for(int i = 0; i < secondsToListen; i++) {
            String oldCharacters = editText.getText().toString();
            String translatedCharacter = translator.ftos(getpitch());
            if(translatedCharacter != "OOV") {
                editText.setText(oldCharacters + translatedCharacter);
            }
            editText.postInvalidate();
        }
    }
}