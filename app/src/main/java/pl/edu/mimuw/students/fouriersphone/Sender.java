package pl.edu.mimuw.students.fouriersphone;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

public class Sender {
    final Activity activity;
    final Switch modeSwitch;
    final EditText editText;
    final Button button;
    Thread t;
    public Sender(Activity activity) {
        this.activity = activity;
        editText = activity.findViewById(R.id.editText);
        button = activity.findViewById(R.id.button);
        modeSwitch = activity.findViewById(R.id.modeSwitch);
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

    public void start(String message) {
        Log.d("debug", message);

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
            button.setText(activity.getString(R.string.send_button_stop));
            t = (new Thread (new Sender.runnable(message, activity)));
            t.start();
        }
    }

    public boolean isAlive() {
        return t != null && t.isAlive();
    }
    /**
     * Function playing sound from internet for testing purposes
     * Source: http://programminglife.io/generating-sine-wave-sound-with-android/
     * @param frequency frequency of sound
     * @param _duration duration of sound
     */
    private void playSound(double frequency, double _duration) {
        int duration = (int)(_duration * 44100);
        // AudioTrack definition

        int mBufferSize = AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                mBufferSize, AudioTrack.MODE_STREAM);

        // Sine wave
        double[] mSound = new double[duration];
        short[] mBuffer = new short[duration];
        for (int i = 0; i < mSound.length; i++) {
            mSound[i] = Math.sin((2.0*Math.PI * i/(44100/frequency)));
            mBuffer[i] = (short) (mSound[i]*Short.MAX_VALUE);
        }


        mAudioTrack.setVolume(AudioTrack.getMaxVolume());
        mAudioTrack.play();

        mAudioTrack.write(mBuffer, 0, mSound.length);
        mAudioTrack.stop();
        mAudioTrack.release();
    }

    private class runnable implements Runnable {
        final String message;
        final Activity activity;

        runnable(String message, Activity activity) {
            this.message = message;
            this.activity = activity;
        }
        @Override
        public void run() {
            for (int i = 0; i < message.length(); i++) {
                if(Thread.currentThread().isInterrupted()) {
                    break;
                }
                playSound(Translator.stof("NEXT"), 0.5);
                playSound(Translator.stof("" + message.charAt(i)), 0.5);
            }

            button.post(new Runnable() {
                @Override
                public void run() {
                    if (modeSwitch.isChecked()) {
                        button.setText(activity.getString(R.string.send_button));
                        button.postInvalidate();
                    }
                }
            });
        }
    }
}