package pl.edu.mimuw.students.fouriersphone.playsound;

public class Sound {
    private final double frequency;
    private final double duration;

    public Sound(double frequency, double duration) {
        this.frequency = frequency;
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sound)) {
            return false;
        }
        Sound sound = (Sound) o;
        return frequency == sound.frequency && duration == sound.duration;
    }

    @Override
    public int hashCode() {
        return 6151 * (6151 + (int) frequency) + (int) duration;
    }

    public double getFrequency() {
        return frequency;
    }
    public double getDuration() {
        return duration;
    }
}
