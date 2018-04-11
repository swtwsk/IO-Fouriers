package pl.edu.mimuw.students.fouriersphone.playsound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.ArrayList;

public class PlaySound {
    public static void playSound(ArrayList<Sound> sounds) {
        final int SAMPLE_RATE = 44100;

        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                bufferSize, AudioTrack.MODE_STREAM);

        double duration = 0;
        for (Sound sound : sounds) {
            duration += sound.getDuration() * SAMPLE_RATE;
        }
        int sampleLength = (int) (duration + (SAMPLE_RATE - duration % SAMPLE_RATE));

        short[] sample = new short[sampleLength];

        int i = 0;
        for (Sound sound : sounds) {
            double angle = 0;
            double increment = 2 * Math.PI * sound.getFrequency() / SAMPLE_RATE;
            for (int j = 0; j < sound.getDuration() * SAMPLE_RATE; ++j) {
                sample[i++] = (short) (Math.sin(angle) * Short.MAX_VALUE);
                angle += increment;
            }
        }

        audioTrack.setVolume(AudioTrack.getMinVolume() + 1);
        audioTrack.play();

        audioTrack.write(sample, 0, sample.length);
        audioTrack.stop();
        audioTrack.release();
    }
}
