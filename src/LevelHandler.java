import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.*;
import java.util.Timer;
import javax.imageio.ImageIO;
import javax.print.attribute.standard.JobName;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class LevelHandler
{
    public ArrayList<PuzzleButton> buttons = new ArrayList<PuzzleButton>();
    public ArrayList<PuzzleButton> solved = new ArrayList<PuzzleButton>();
    private int turnOnCounter = 0;
    private ArrayList<PuzzleButton> toLight = new ArrayList<>();
    public double equationValue;
    private double currentValue;
    private int side;
    private boolean firstSide;
    private int hardnessMultiplier;
    public Icon buttonOnFinal;
    public Icon buttonOffFinal;
    public BufferedImage brightnessShader;
    public JLabel brightnessDecoy;
    public ArrayList<Icon> animSeqs[];
    public ArrayList<JButton> proceedButtons = new ArrayList<JButton>();
    public JTextPane proceedLabel;
    private int endPercent;
    private JLabel mrCuddles;

    public void SetHardnessMultiplier(int value)
    {
        hardnessMultiplier = value;
    }

    //Initialize the buttons and whatnot.
    public void Initialize() throws IOException {
        animSeqs = new ArrayList[]{new ArrayList<Icon>(), new ArrayList<Icon>(), new ArrayList<Icon>()};
        SetImageSequence();
        Random rand = new Random();
        for (PuzzleButton b : buttons)
        {
            b.SetIcons(buttonOnFinal, buttonOffFinal);
            if (hardnessMultiplier == 4)
            {
                int toSet = 0;
                if (rand.nextFloat() < 0.25f)
                {
                    if (rand.nextFloat() > 0.5f)
                    {
                        toSet = 1;
                    }
                    else
                    {
                        toSet = 2;
                    }
                }
                b.SetAnimIndex(toSet);
                b.button.setIcon(animSeqs[b.GetAnimIndex()].get(9));
            }
            if (hardnessMultiplier == 5)
            {
                mrCuddles = new JLabel();
                mrCuddles.setBounds(0, 0, 1386, 780);
                BufferedImage input1 = ImageIO.read(getClass().getResource("images/untitled.png"));
                mrCuddles.setIcon(new ImageIcon(Main.Tinter(new Color(0, 0, 0, 0), input1)));
                Main.frame.add(mrCuddles);
            }
            b.SetAnimSpeed(30 + rand.nextInt(20));
        }
        if (hardnessMultiplier == 2)
        {
            brightnessDecoy.setBounds(0, 0, 1386, 780);
            brightnessDecoy.setIcon(new ImageIcon(ColorTinter(brightnessShader, 1386, 780)));
            brightnessDecoy.setBorder(null);
        }
    }

    public void SetImageSequence() throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        URL imgURL;
        Icon img;
        for (int i = 0; i < 3; i++)
        {
            StringBuilder varName = new StringBuilder("images/eye1/0000.png");
            varName.deleteCharAt(10);
            varName.insert(10, i + 1);
            for (int j = 0; j < 21; j++)
            {
                varName.deleteCharAt(15);
                if (j < 10)
                {
                    varName.insert(15, j);
                }
                else
                {
                    varName.deleteCharAt(14);
                    varName.insert(14, j);
                }
                imgURL = classLoader.getResource(varName.toString());
                BufferedImage bufImg = ImageIO.read(imgURL);
                img = new ImageIcon(ImageScaler(bufImg, buttons.get(0).button.getHeight()));
                animSeqs[i].add(img);
            }
        }
    }

    private BufferedImage ImageScaler(BufferedImage input, int dimensions)
    {
        BufferedImage resizedImage = new BufferedImage(dimensions, dimensions, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(input, 0, 0, dimensions, dimensions, null);
        graphics2D.dispose();
        return resizedImage;
    }

    //CALCULATE THE CURRENTVALUE CORRECTLY PLEASE
    private void CalculateCurrentValue()
    {
        currentValue = 0;
        for (PuzzleButton b : solved)
        {
            if (b.GetSide() == side)
            {
                currentValue += b.GetData();
            }
        }
    }

    //Call this whenever player interacts with somethingy.
    public void OnPuzzleValueChanged(PuzzleButton changer) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (firstSide)
        {
            firstSide = false;
            side = changer.GetSide();
            OnPuzzleValueChanged(changer);
        }
        else
        {
            if (side != changer.GetSide())
            {
                side = changer.GetSide();
                solved.clear();
            }
            if (solved.contains(changer))
            {
                solved.remove(changer);
                changer.ToggleLight(false);
            }
            else
            {
                solved.add(changer);
                changer.ToggleLight(true);
                if (hardnessMultiplier >= 4)
                {
                    changer.BlinkTimer();
                }
            }
            CalculateCurrentValue();
            OtherSideCheck();
            //System.out.println("Handler values: " + (int)currentValue);
            for (PuzzleButton b : buttons)
            {
                b.SetEnable(false);
                if (!solved.contains(b))
                {
                    b.ToggleLight(false);
                    if (hardnessMultiplier == 4)
                    {
                        b.PlayAnimation(true);
                    }
                }
            }
            Timer timer = new Timer();
            int begin = 0;
            float alphaCounter = (float)toLight.size() / buttons.size();
            int timeInterval = (int)(0.5f / toLight.size() * 1000);
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    if (toLight.size() > 0)
                    {
                        try {
                            toLight.get(0).ToggleLight(true);
                            if (toLight.get(0).playerRef != null)
                            {
                                toLight.get(0).playerRef.Play();
                            }
                        } catch (UnsupportedAudioFileException e) {
                            throw new RuntimeException(e);
                        } catch (LineUnavailableException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (hardnessMultiplier == 4)
                        {
                            toLight.get(0).BlinkTimer();
                        }
                        solved.add(toLight.get(0));
                        toLight.remove(0);
                        turnOnCounter++;
                    }
                    else
                    {
                        if (hardnessMultiplier == 5)
                        {
                            BufferedImage img = null;
                            try {
                                img = ImageIO.read(getClass().getResource("images/untitled.png"));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            mrCuddles.setIcon(new ImageIcon(Main.Tinter(new Color(255, 255, 255, Math.round(180f * alphaCounter)), img)));
                        }
                        if (currentValue == equationValue)
                        {
                            OnLevelFinish();
                            timer.cancel();
                        }
                        else
                        {
                            for (PuzzleButton b : buttons)
                            {
                                b.SetEnable(true);
                            }
                            timer.cancel();
                        }
                    }
                }
            }, begin, timeInterval);
            turnOnCounter = 0;
        }
    }

    public void OnLevelFinish()
    {
        StyledDocument doc = proceedLabel.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        proceedLabel.setEditable(false);
        endPercent = 0;
        proceedLabel.setVisible(true);
        proceedLabel.setOpaque(true);
        if (mrCuddles != null)
        {
            Main.frame.remove(mrCuddles);
        }
        mrCuddles = null;
        for (int i = 0; i < proceedButtons.size(); i++)
        {
            JButton b = proceedButtons.get(i);
            b.setVisible(true);
            b.setContentAreaFilled(true);
        }
        for (PuzzleButton b : buttons)
        {
            b.button.setVisible(false);
        }
        Timer endAnim = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run()
            {
                float actualPercent = (float)endPercent / 1000f;
                int newWidth = proceedLabel.getWidth() + Math.round((800 - proceedLabel.getWidth()) * actualPercent);
                int newHeight = proceedLabel.getHeight() + Math.round((450 - proceedLabel.getHeight()) * actualPercent);
                proceedLabel.setBounds(293, 150, newWidth, newHeight);
                endPercent += 2;
                if (endPercent > 1000)
                {
                    for (int i = 0; i < proceedButtons.size(); i++)
                    {
                        JButton b = proceedButtons.get(i);
                        b.setVisible(true);
                    }
                    endAnim.cancel();
                }
            }
        };
        endAnim.scheduleAtFixedRate(task, 0, 2);
    }

    //Just a basic Clamp function because Java does not have a built-in one for some reason...
    float ClampFunction(float value, float low, float high)
    {
        if (value < low)
            value = low;
        if (value > high)
            value = high;
        return value;
    }

    //Salvaged from the PuzzleCreator class, who salvaged it from the PuzzleButton class, who salvaged it from the internet.
    private Image ColorTinter(BufferedImage input, int dimensionsX, int dimensionsY)
    {
        BufferedImage img = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
        BufferedImage bi;
        float red = 1f;
        float green = 1f;
        float blue = 1f;
        float alpha = (float)solved.size() / (float)buttons.size();
        float offsets[];
        float factors[] = {0, 0, 0, 0};
        offsets = new float[]{red, green, blue, alpha};
        RescaleOp op = new RescaleOp(offsets, factors, null);
        op.filter(input, img);
        bi = new BufferedImage(dimensionsX, dimensionsY, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ClampFunction(alpha, 0, 1)));
        g2d.drawImage(img, 0, 0, dimensionsX, dimensionsY, null);
        g2d.dispose();
        return bi;
    }

    private void OtherSideCheck()
    {
        double valueCheck;
        for (PuzzleButton b : buttons)
        {
            if (b.GetSide() != side)
            {
                valueCheck = b.GetData();
                toLight.add(b);
                for (PuzzleButton j : buttons)
                {
                    if (b != j && j.GetSide() != side)
                    {
                        valueCheck += j.GetData();
                        toLight.add(j);
                        if (valueCheck > currentValue)
                        {
                            valueCheck -= j.GetData();
                            toLight.remove(j);
                        }
                    }
                }
                if (valueCheck == currentValue)
                {
                    break;
                }
                else
                {
                    toLight.clear();
                }
            }
        }
    }
}
