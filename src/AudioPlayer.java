import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {
    private Long currentFrame;
    private Clip clip;
    private String status;
    private AudioInputStream audioInputStream;
    public String filePath;
    private boolean once;

    public AudioPlayer(String path, boolean isContinuous) throws LineUnavailableException, IOException, UnsupportedAudioFileException {

        filePath = path;
        once = !isContinuous;
    }

    public void Play() throws LineUnavailableException, IOException, UnsupportedAudioFileException
    {
        audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
        clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        if (once)
        {
            clip.loop(0);
        }
        else
        {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void Stop() throws UnsupportedAudioFileException, IOException, LineUnavailableException
    {
        currentFrame = 0L;
        clip.stop();
        clip.close();
    }
}
