package pl.edu.mimuw.students.fouriersphone.SoundAnalyzer;

public class CTFFT {
    // n = 2^m = size of input tables
    private int m = 10, n = 1 << m;

    private double[] inp;
    private double[] out;

    // Lookup tables. Only need to recompute when size of FFT changes.
    private double[] cos;
    private double[] sin;

    public CTFFT() {
        inp = new double[n];
        out = new double[n];

        // precompute tables
        cos = new double[n / 2];
        sin = new double[n / 2];

        for (int i = 0; i < n / 2; i++) {
            cos[i] = Math.cos(-2 * Math.PI * i / n);
            sin[i] = Math.sin(-2 * Math.PI * i / n);
        }

    }

    private void fft(short []x) {
        for (int i = 0; i < n; ++i) {
            inp[i] = ((double) x[i]) / Short.MAX_VALUE;
            out[i] = 0.;
        }

        double t1, t2;

        // Bit-reverse
        int j = 0, n1, n2 = n / 2;
        for (int i = 1; i < n - 1; i++) {
            n1 = n2;
            while (j >= n1) {
                j = j - n1;
                n1 = n1 / 2;
            }
            j = j + n1;

            if (i < j) {
                t1 = inp[i];
                inp[i] = inp[j];
                inp[j] = t1;
                t1 = out[i];
                out[i] = out[j];
                out[j] = t1;
            }
        }

        // FFT
        int a;
        double c, s;
        n2 = 1;
        for (int i = 0; i < m; i++) {
            n1 = n2;
            n2 = n2 + n2;
            a = 0;

            for (j = 0; j < n1; j++) {
                c = cos[a];
                s = sin[a];
                a += 1 << (m - i - 1);

                for (int k = j; k < n; k = k + n2) {
                    t1 = c * inp[k + n1] - s * out[k + n1];
                    t2 = s * inp[k + n1] + c * out[k + n1];
                    inp[k + n1] = inp[k] - t1;
                    out[k + n1] = out[k] - t2;
                    inp[k] = inp[k] + t1;
                    out[k] = out[k] + t2;
                }
            }
        }
    }

    public double dominant(short x[]) {
        fft(x);
        int maxi = 0; double max = 0;
        for (int i = 0; i < n / 2; ++i) {
            out[i] = Math.abs(out[i]);
            if (max < out[i]) {
                max = out[i];
                maxi = i;
            }
        }

        double dom;
        if (maxi > 0) {
            max = out[maxi] * maxi;
            max += out[maxi - 1] * (maxi - 1);
            max += out[maxi + 1] * (maxi + 1);
            dom = max / (out[maxi - 1] + out[maxi] + out[maxi + 1]);
        } else {
            max = out[maxi] * maxi;
            max += out[maxi + 1] * (maxi + 1);
            dom = max / (out[maxi] + out[maxi + 1]);
        }

        return 44100.0 * dom / n;
    }
}
