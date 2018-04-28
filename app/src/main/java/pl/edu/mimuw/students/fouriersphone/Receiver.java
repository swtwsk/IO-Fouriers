package pl.edu.mimuw.students.fouriersphone;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.EditText;

import java.util.Arrays;
import java.util.Objects;


public class Receiver implements Runnable {
    private EditText editText;

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

        CTFFT fft = new CTFFT();
        for(int i = 0; i < numberOfSamples; i++) {
            audioInput.read(audioBuffer, 0, bufferSize);
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
        int secondsToListen = 30;
        for(int i = 0; i < secondsToListen; i++) {
            String translatedCharacter = Translator.ftos(getpitch());
            if(!Objects.equals(translatedCharacter, "OOV")) {
                editText.append(translatedCharacter);
            }
            editText.postInvalidate();
        }
    }
}