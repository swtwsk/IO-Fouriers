package pl.edu.mimuw.students.fouriersphone.SoundAnalyzer;

import android.util.Log;

public class AnalyzeSound {

    private static double autocorrelation(short[] x, int k) {
        double average = 0;
        for(int i = 0; i < x.length; i++) {
            average += (x[i] * (x[(i + k) % x.length]));
        }
        return average / x.length;
    }

    public double analyze(short[] x, double[] frequencies) {

        int WALL = 1000;
        double MINIMUM_RESULT = 1e6;

        CTFFT fft = new CTFFT();
        double possible_frequency = fft.dominant(x);
        if(possible_frequency > WALL) {
            int best_index = 0;
            for(int i = 0; i < frequencies.length; i++) {
                if(Math.abs(frequencies[i] - possible_frequency) < Math.abs(frequencies[best_index] - possible_frequency)) best_index = i;
            }
            return frequencies[best_index];
        }

        double[] results = new double[frequencies.length];
        for(int i = 0; i < frequencies.length; i++) {
            int k = (int)(44100 / frequencies[i]);
            results[i] = autocorrelation(x, k);
        }

        int max_index = 0;

        for(int i = 0; i < frequencies.length; i++) {
            if(frequencies[i] > WALL) continue;
            if(results[i] > results[max_index]) {
                max_index = i;
            }
        }



        Log.v("AnalyzeSound", "Best frequency is on " + Double.toString(frequencies[max_index]) + " and the result is " + Double.toString(results[max_index]));
        if(results[max_index] < MINIMUM_RESULT) {
            return 0.0;
        } else {
            return frequencies[max_index];
        }
    }
}
