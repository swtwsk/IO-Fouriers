package pl.edu.mimuw.students.fouriersphone;

import java.util.HashSet;
import java.util.Objects;

/**
 * Class for translating strings to frequencies and frequencies to strings.
 */
public class Translator {
    /**
     * Frequency of lowest pitch
     */
    private final static double base = 440;

    /**
     * Letters and signs which we have pitch for
     */
    private final static String []alphabet = {
            "a", "i", "o", "e", "z", "n", "r", "w", "s", "t", "c", "y", "k",
            "d", "p", "m", "u", "j", "l", "b", "g", "h", "f", "q", "v", "x",
            " ", ".",
            "A", "I", "O", "E", "Z", "N", "R", "W", "S", "T", "C", "Y", "K",
            "D", "P", "M", "U", "J", "L", "B", "G", "H", "F", "Q", "V", "X",
            "START", "STOP", "OOV"
    };

    private static final int numberOfPitches = 62;

    private static final HashSet<Double> frequencies;

    static {
        frequencies = new HashSet<>();
        for (int i = 0; i < numberOfPitches; i++) {
            frequencies.add(sound(i));
        }
    }

    public static HashSet<Double> getFrequencies() {
        return frequencies;
    }

    private static double sound(int n) {
        return base * Math.pow(2, ((double) n) / 12);
    }

    public static double stof(String sign) {
        int i = 0;
        while (i < alphabet.length && !Objects.equals(alphabet[i], sign)) ++i;
        return sound(i);
    }

    public static String ftos(double freq) {
        int n = (int) Math.round(12 * Math.log(freq / base) / Math.log(2));
        return (n >= 0 && n < alphabet.length) ? alphabet[n] : "OOV";
    }
}