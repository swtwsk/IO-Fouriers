package pl.edu.mimuw.students.fouriersphone;

public class Translator {
    /**
     * String to frequency function
     */
    public static int stof(String sign) {
        int frequency = 200;

        if (sign.length() == 1) {
            char letter = sign.charAt(0);
            if (letter > 96 && letter < 123) { // Lowercase letters
                frequency = (int) (440 * Math.pow(2, ((double) (letter - 97)) / 12));
            } else if (letter > 64 && letter < 91) { // Uppercase letters
                frequency = (int) (440 * Math.pow(2, ((double) (letter - 39)) / 12));
            } else if (letter == ' ') {
                frequency = (int) (440 * Math.pow(2, ((double) 52) / 12));
            } else if (letter == '.') {
                frequency = (int) (440 * Math.pow(2, ((double) 53) / 12));
            }
        } else {
            if (sign.equals("START")) {
                frequency = 19000;
            } else if (sign.equals("STOP")) {
                frequency = 18000;
            }
        }

        return frequency;
    }
}
