import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class PuzzleButton
{
    //Class you'll be clicking on actual puzzle solving, not to be confused with other UI like menu buttons.
    private double data;
    private double correctMultiplier;
    private double randomMultiplier;
    private int direction;
    private boolean isClickable = true;
    private Icon onIcon;
    private Icon offIcon;
    public JButton button;
    public JSlider slider;
    private LevelHandler respondTo;
    private Timer timer;
    private Timer blinkTimer;
    public boolean state = false;
    private TimerTask tt1;
    private int counter;
    private int animIndex = -1;
    private boolean animPlaying = false;
    private int animSpeed;
    private String buttonClip;
    public AudioPlayer playerRef;
    //Constructor should be feeded with the level frame, and the numbers associated with the button. Also give edge length
    // and some coordinates.
    PuzzleButton(JFrame frame, double value, int x, int y, int dimensions, int side) throws IOException
    {
        button = new JButton();
        slider = new JSlider() {
            @Override
            public void updateUI() {
                setUI(new SliderCustom(this));
            }
        };
        slider.setBackground(new Color(0, 0, 0, 0));
        slider.setSnapToTicks(true);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(10);
        data = value;
        direction = side;
        button.setBounds(x, y, dimensions, dimensions);
        slider.setBounds(x, y + Math.round((float)dimensions / 1.2f), Math.round((float)dimensions * 0.9f), dimensions / 10);
        slider.setMinimum(0);
        slider.setMaximum(100);
        slider.setVisible(false);
        frame.add(slider);
        button.setContentAreaFilled(false);
        button.setBorder(null);
        button.setIcon(offIcon);
        frame.add(button);
        blinkTimer = new Timer();
        Random rand = new Random();
        randomMultiplier = Math.round((rand.nextDouble()) * 5d) / 5d;
        button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (isClickable)
                {
                    try {
                        ContactHandler();
                    } catch (UnsupportedAudioFileException ex) {
                        throw new RuntimeException(ex);
                    } catch (LineUnavailableException ex) {
                        throw new RuntimeException(ex);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

    private void ContactHandler() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        respondTo.OnPuzzleValueChanged(this);
    }

    //Sets the sound effect associated with the button.
    public void SetClip(String data) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        buttonClip = data;
        if (buttonClip != null)
        {
            playerRef = new AudioPlayer(buttonClip, false);
        }
    }

    //Should be self-explanatory.
    public double GetData()
    {
        return data;
    }

    //ALSO should be self-explanatory.
    public int GetSide()
    {
        return direction;
    }

    //ALSO ALSO should be self-explanatory.
    public int GetAnimIndex()
    {
        return animIndex;
    }

    //ALSO ALSO ALSO should be self-explanatory.
    public double GetSliderValue()
    {
        return correctMultiplier;
    }

    //Well, I don't really want to overload the constructor anymore, so here is a basic set function.
    public void SetHandler(LevelHandler handler)
    {
        respondTo = handler;
    }

    //Function to set icon references.
    public void SetIcons(Icon onIcon1, Icon offIcon1)
    {
        onIcon = onIcon1;
        offIcon = offIcon1;
        button.setIcon(offIcon);
    }

    //Sets the anim index.
    public void SetAnimIndex(int index)
    {
        animIndex = index;
    }

    //Sets the animSpeed
    public void SetAnimSpeed(int value)
    {
        animSpeed = value;
    }

    public void BlinkTimer()
    {
        Random rand = new Random();
        int delayRandom = 1000 + rand.nextInt(3000);
        blinkTimer.cancel();
        blinkTimer.purge();
        blinkTimer = new Timer();
        blinkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                PlayAnimation(false);
            }
        }, delayRandom);
    }

    public void PlayAnimation(boolean cancel)
    {
        if (animIndex == -1)
        {
            return;
        }
        if (cancel)
        {
            if (animPlaying)
            {
                timer.cancel();
                timer.purge();
                animPlaying = false;
                button.setIcon(respondTo.animSeqs[animIndex].get(0));
            }
        }
        else
        {
            timer = new Timer();
            tt1 = new TimerTask() {
                @Override
                public void run() {
                    if (Main.stopTimers)
                    {
                        tt1.cancel();
                    }
                    button.setIcon(respondTo.animSeqs[animIndex].get(counter));
                    counter++;
                    if (counter >= (respondTo.animSeqs[animIndex].size()))
                    {
                        counter = 0;
                        BlinkTimer();
                        cancel();
                    }
                }
            };
            timer.scheduleAtFixedRate(tt1,0, animSpeed);
            animPlaying = true;
        }
    }

    //Works like a light switch really.
    public void ToggleLight(boolean open) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (open)
        {
            if (animIndex == -1)
            {
                button.setIcon(onIcon);
            }
            else
            {
                button.setIcon(respondTo.animSeqs[animIndex].get(0));
            }
            /*if (buttonClip != null)
            {
                playerRef.Play();
            }*/
            state = true;
        }
        else
        {
            if (animIndex == -1)
            {
                button.setIcon(offIcon);
            }
            else
            {
                button.setIcon(respondTo.animSeqs[animIndex].get(9));
            }
            state = false;
        }
    }

    //A small function to enable/disable components. Comes in handy to disable user input during recalculation.
    public void SetEnable(boolean input)
    {
        isClickable = input;
        slider.setEnabled(input);
    }
}
