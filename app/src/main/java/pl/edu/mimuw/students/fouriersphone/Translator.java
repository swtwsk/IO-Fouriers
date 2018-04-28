package pl.edu.mimuw.students.fouriersphone;

import java.util.Objects;

public class Translator {
    /**
     * Class for translating strings to frequencies and frequencies to strings.
     */
    private final static double base = 220;
    private final static String []alphabet = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
            "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            " ", ".", "START", "STOP"
    };

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