import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PuzzleCreator
{
    private float hardness = 0.1f; //A value between 0 and 1, initially set to 0.1 for reasons. Should be set with setter func.
    private int hardnessInt = 0;
    private int dimensions = 0;
    private String onFilePaths[] = {"", "", "", "", ""};
    private String offFilePaths[] = {"", "", "", "", ""};
    private String musicPaths[] = {null, null, null, null, null};
    private AudioPlayer levelMusic;
    public JFrame frame;
    ArrayList<Double> leftHandSide = new ArrayList<Double>();
    ArrayList<Double> rightHandSide = new ArrayList<Double>();
    public LevelHandler respondTo;
    public BufferedImage discoIcons[] = {null, null, null, null, null, null, null, null};
    private ArrayList<Icon> discoButtonIcons = new ArrayList<Icon>();
    private ArrayList<Icon> discoButtonIconsOff = new ArrayList<Icon>();
    private JLabel brightnessDecoy;
    private int discoAlternater = 0;
    private Timer discoTimer;
    private ArrayList<Color> discoColors = new ArrayList<Color>();
    private String buttonClips[][] = {{null, null, null}, {null}, {null}, {null}, new String[24]};
    private JTextPane endPanel = new JTextPane();
    private Timer cuddlesTimer;
    private TimerTask cuddlesTask = new TimerTask() {
        @Override
        public void run() {
            Randomizer(respondTo.buttons, 1200 / (24 / 2));
        }
    };

    public void SetHardness(float value, int intValue)
    {
        hardness = value;
        hardnessInt = intValue;
    }

    //Generating random puzzle based on hardness and some RNG
    //This is the main method using the *smaller* methods to create the puzzle
    public void GeneratePuzzle() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        if (hardnessInt == 1)
        {
            GenerateEquation();
            GenerateButtons(leftHandSide.size() + rightHandSide.size(), 1);
            Difficulty1();
        }
        if (hardnessInt == 2)
        {
            GenerateEquation();
            respondTo.brightnessShader = GenerateDecoy();
            frame.getContentPane().setBackground(new Color(11, 21, 79));
            GenerateButtons(leftHandSide.size() + rightHandSide.size(), 2);
            AudioPlayer decoy = new AudioPlayer("src/resources/sfx/ordinaryambient.wav", false);
            decoy.Play();
            Timer stopDecoy = new Timer();
            stopDecoy.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        decoy.Stop();
                    } catch (UnsupportedAudioFileException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (LineUnavailableException e) {
                        throw new RuntimeException(e);
                    }
                }
            },11800);
            Timer phaseTimer = new Timer();
            phaseTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        Difficulty2();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (UnsupportedAudioFileException e) {
                        throw new RuntimeException(e);
                    } catch (LineUnavailableException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, 3000);
        }
        if (hardnessInt == 3)
        {
            hardness = 0.5f;
            GenerateEquation();
            GenerateButtons(leftHandSide.size() + rightHandSide.size(), 2);
            levelMusic = new AudioPlayer("src/resources/sfx/ordinaryambient.wav", true);
            frame.setTitle("AutoPuzzle - Medium");
            levelMusic.Play();
            frame.getContentPane().setBackground(new Color(11, 21, 79));
        }
        if (hardnessInt == 4)
        {
            hardness = 0.6f;
            GenerateEquation();
            GenerateButtons(leftHandSide.size() + rightHandSide.size(), 3);
            Difficulty4();
        }
        if (hardnessInt == 5)
        {
            GenerateEquation2();
            GenerateButtons(24, 2);
            Difficulty5();
        }
        CreateEndPanel();
    }

    private void CreateEndButtons(boolean side, String text, Font buttonFont, ActionListener action)
    {
        JButton toAdd = new JButton();
        if (side)
        {
            toAdd.setBounds(368, 450, 250, 100);
        }
        else
        {
            toAdd.setBounds(756, 450, 250, 100);
        }
        toAdd.setFont(buttonFont);
        toAdd.setText(text);
        toAdd.setForeground(Color.white);
        toAdd.setBackground(new Color(58, 58, 68));
        toAdd.setContentAreaFilled(true);
        toAdd.setOpaque(true);
        toAdd.addActionListener(action);
        respondTo.proceedButtons.add(toAdd);
    }

    public void CreateEndPanel() throws IOException {
        Font ttfBase = null;
        try
        {
            InputStream myStream = Main.class.getResourceAsStream("fonts/Poppins-Bold.ttf");
            ttfBase = Font.createFont(Font.TRUETYPE_FONT, myStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (FontFormatException e)
        {
            e.printStackTrace();
        }

        endPanel.setBounds(0, 0, 80, 45);
        endPanel.setLocation(693 - (endPanel.getWidth() / 2), 390 - (endPanel.getHeight() / 2));
        endPanel.setFont(ttfBase);
        Font labelFont = new Font(endPanel.getFont().getName(), Font.BOLD, 60);
        Font buttonFont = new Font(endPanel.getFont().getName(), Font.BOLD, 20);
        BufferedImage buttonGraphic = ImageIO.read(getClass().getResource("images/MenuButtons/proceed.png"));
        JButton toAdd = null;
        ActionListener toMenu = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    endPanel = null;
                    frame.remove(respondTo.proceedLabel);
                    levelMusic.Stop();
                    levelMusic = null;
                    respondTo.proceedLabel = null;
                    Main.stopTimers = true;
                    if (discoTimer != null)
                    {
                        discoTimer.cancel();
                    }
                    if (cuddlesTimer != null)
                    {
                        cuddlesTimer.cancel();
                    }
                    if (brightnessDecoy != null)
                    {
                        frame.remove(brightnessDecoy);
                        brightnessDecoy = null;
                    }
                    for (JButton b : respondTo.proceedButtons)
                    {
                        frame.remove(b);
                    }
                    respondTo.proceedButtons = new ArrayList<>();
                    Main.CreateMainMenu(false);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (UnsupportedAudioFileException ex) {
                    throw new RuntimeException(ex);
                } catch (LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        ActionListener again = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                endPanel = null;
                Main.stopTimers = true;
                if (discoTimer != null)
                {
                    discoTimer.cancel();
                }
                if (cuddlesTimer != null)
                {
                    cuddlesTimer.cancel();
                }
                if (brightnessDecoy != null)
                {
                    frame.remove(brightnessDecoy);
                    brightnessDecoy = null;
                }
                frame.remove(respondTo.proceedLabel);
                try {
                    levelMusic.Stop();
                } catch (UnsupportedAudioFileException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                }
                levelMusic = null;
                respondTo.proceedLabel = null;
                for (JButton b : respondTo.proceedButtons)
                {
                    frame.remove(b);
                }
                respondTo.proceedButtons = new ArrayList<>();
                try
                {
                    Main.CreateMainMenu(true);
                }
                catch (IOException ex)
                {
                    throw new RuntimeException(ex);
                } catch (UnsupportedAudioFileException ex) {
                    throw new RuntimeException(ex);
                } catch (LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        switch (hardnessInt)
        {
            case 1:
                endPanel.setFont(labelFont);
                endPanel.setText("Night's running late and the kittens are happy.");
                endPanel.setBackground(new Color(103, 25, 58, 0));
                endPanel.setOpaque(false);
                endPanel.setForeground(new Color(239, 244, 250));
                CreateEndButtons(true, "Time to go to bed", buttonFont, toMenu);
                CreateEndButtons(false, "meow?", buttonFont, again);
                respondTo.proceedLabel = endPanel;
                break;
            case 2:
                endPanel.setFont(labelFont);
                endPanel.setText("I hope you enjoyed your stay in our 90's disco!");
                endPanel.setBackground(new Color(103, 25, 58, 0));
                endPanel.setOpaque(false);
                endPanel.setForeground(Color.white);
                CreateEndButtons(true, "[run]", buttonFont, toMenu);
                CreateEndButtons(false, "I surely did!", buttonFont, again);
                respondTo.proceedLabel = endPanel;
                break;
            case 3:
                endPanel.setFont(labelFont);
                endPanel.setText("Puzzle Solved!");
                endPanel.setBackground(new Color(103, 25, 58, 0));
                endPanel.setOpaque(false);
                endPanel.setForeground(Color.white);
                CreateEndButtons(true, "Cool", buttonFont, toMenu);
                CreateEndButtons(false, "Not cool enough", buttonFont, again);
                respondTo.proceedLabel = endPanel;
                break;
            case 4:
                endPanel.setFont(labelFont);
                endPanel.setText("hello?" + " is someone there? \n" + "\n" + "can you hear me?");
                endPanel.setBackground(new Color(103, 25, 58, 0));
                endPanel.setOpaque(false);
                endPanel.setForeground(Color.white);
                CreateEndButtons(true, "HELP ME", buttonFont, toMenu);
                CreateEndButtons(false, "[I can't hear you]", buttonFont, again);
                respondTo.proceedLabel = endPanel;
                break;
            case 5:
                endPanel.setFont(labelFont);
                endPanel.setText("I just wanted an hug :(");
                endPanel.setBackground(new Color(103, 25, 58, 0));
                endPanel.setOpaque(false);
                endPanel.setForeground(Color.white);
                CreateEndButtons(true, "And you got it", buttonFont, toMenu);
                CreateEndButtons(false, "[What?]", buttonFont, again);
                respondTo.proceedLabel = endPanel;
                break;
        }
        for (JButton b : respondTo.proceedButtons)
        {
            b.setVisible(false);
            frame.add(b);
        }
        endPanel.setVisible(false);
        frame.add(endPanel);
    }

    //Here we adjust the string arrays so it is easier to get the correct graphic for the difficulty value.
    public void AdjustFilePaths() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        onFilePaths[0] = "images/kitten1.png";
        offFilePaths[0] = "images/kitten2.png";
        onFilePaths[1] = "images/buttonraw3.png";
        offFilePaths[1] = "images/buttonraw4.png";
        onFilePaths[2] = "images/buttonraw3.png";
        offFilePaths[2] = "images/buttonraw4.png";
        onFilePaths[3] = "images/eye1/0000.png";
        offFilePaths[3] = "images/eye1/0009.png";
        onFilePaths[4] = "images/buttonraw3.png";
        offFilePaths[4] = "images/buttonraw4.png";
        buttonClips[0][0] = "src/resources/sfx/meow1.wav";
        buttonClips[0][1] = "src/resources/sfx/meow2.wav";
        buttonClips[0][2] = "src/resources/sfx/meow3.wav";
        buttonClips[2][0] = "src/resources/sfx/sound1.wav";
        for (int i = 1; i <= 24; i++)
        {
            StringBuilder varName = new StringBuilder("src/resources/sfx/piano/key01.wav");
            if (i > 9)
            {
                varName.deleteCharAt(28);
                varName.deleteCharAt(27);
                varName.insert(27, i);
            }
            else
            {
                varName.deleteCharAt(28);
                varName.insert(28, i);
            }
            buttonClips[4][i - 1] = varName.toString();
        }
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

    //the pains are real
    Double ClampFunctionDouble(Double value, Double low, Double high)
    {
        if (value < low)
            value = low;
        if (value > high)
            value = high;
        return value;
    }

    //The idea here is to simulate an elementary school math equation, with left hand side and right hand side.
    //Randomness in algorithm should scale with the hardness value.
    private void GenerateEquation()
    {
        Random rand = new Random();
        double target = Math.round(120 * hardness);
        double sum = 0;
        while (sum <= target)
        {
            leftHandSide.add(ClampFunctionDouble((double)rand.nextInt(10), 4d, 10d));
            sum += leftHandSide.get(leftHandSide.size() - 1);
        }
        sum -= leftHandSide.get(leftHandSide.size() - 1);
        leftHandSide.remove(leftHandSide.size() - 1);
        if (target - sum != 0)
        {
            leftHandSide.add(target - sum);
        }
        if (leftHandSide.size() % 2 != 0)
        {
            leftHandSide.set(0, leftHandSide.get(leftHandSide.size() - 1) + leftHandSide.get(0));
            leftHandSide.remove(leftHandSide.size() - 1);
        }
        sum = 0;
        while (sum <= target)
        {
            rightHandSide.add(ClampFunctionDouble((double)rand.nextInt(10), 4d, 10d));
            sum += rightHandSide.get(rightHandSide.size() - 1);
        }
        sum -= rightHandSide.get(rightHandSide.size() - 1);
        rightHandSide.remove(rightHandSide.size() - 1);
        if (target - sum != 0)
        {
            rightHandSide.add(target - sum);
        }
        if (rightHandSide.size() % 2 != 0)
        {
            rightHandSide.set(0, rightHandSide.get(rightHandSide.size() - 1) + rightHandSide.get(0));
            rightHandSide.remove(rightHandSide.size() - 1);
        }
        sum = 0;
        System.out.println("Left hand side:");
        for (int i = 0; i < leftHandSide.size(); i++)
        {
            System.out.println("" + leftHandSide.get(i));
            sum += leftHandSide.get(i);
        }
        respondTo.equationValue = sum;
        System.out.println("Sum is " + sum);
        sum = 0;
        System.out.println("Right hand side:");
        for (int i = 0; i < rightHandSide.size(); i++)
        {
            System.out.println("" + rightHandSide.get(i));
            sum += rightHandSide.get(i);
        }
        System.out.println("Sum is " + sum);
    }

    //Almost identical to the first, this one however, creates around a defined length.
    private void GenerateEquation2()
    {
        Random rand = new Random();
        double target = 12;
        int counter = 0;
        double sum = 0;
        while (counter < target)
        {
            leftHandSide.add(ClampFunctionDouble((double)rand.nextInt(10), 4d, 10d));
            sum += leftHandSide.get(leftHandSide.size() - 1);
            counter++;
        }
        double toAdd = 0;
        for (double d : leftHandSide)
        {
            if (toAdd == 0)
            {
                toAdd = d - ClampFunctionDouble((double)rand.nextInt((int)d), 0d, d);
                rightHandSide.add(d - toAdd);
            }
            else
            {
                rightHandSide.add(d + toAdd);
                toAdd = 0;
            }
        }
        sum = 0;
        System.out.println("Left hand side:");
        for (int i = 0; i < leftHandSide.size(); i++)
        {
            System.out.println("" + leftHandSide.get(i));
            sum += leftHandSide.get(i);
        }
        respondTo.equationValue = sum;
        System.out.println("Sum is " + sum);
        sum = 0;
        System.out.println("Right hand side:");
        for (int i = 0; i < rightHandSide.size(); i++)
        {
            System.out.println("" + rightHandSide.get(i));
            sum += rightHandSide.get(i);
        }
        System.out.println("Sum is " + sum);
    }

    //Backbone of the puzzle. Input *should* be even.
    //Randomizing algorithm is also here.
    private void GenerateButtons(int amount, int rows) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        Random rand = new Random();
        dimensions = 1200 / (amount / rows);
        AdjustFilePaths();
        GenerateOnButtonGraphic(dimensions);
        GenerateOffButtonGraphic(dimensions);
        int rowAmount = (amount / rows);
        int correctMatrix[][] = new int[rows][rowAmount];
        int initialSpawn = 693 - (rowAmount * dimensions / 2);
        int initialSpawnY = 390 - ((rows) * (dimensions) / 2);
        float mix = 0.5f;
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < rowAmount; j++)
            {
                int randomIndex = 0;
                if (!leftHandSide.isEmpty() && !rightHandSide.isEmpty())
                {
                    if (mix > rand.nextFloat())
                    {
                        mix -= 0.25f;
                        randomIndex = rand.nextInt(leftHandSide.size());
                        respondTo.buttons.add(new PuzzleButton(frame, leftHandSide.get(randomIndex), initialSpawn + (dimensions * j), initialSpawnY + (dimensions * i), dimensions, 0));
                        respondTo.buttons.get(respondTo.buttons.size() - 1).SetHandler(respondTo);
                        respondTo.buttons.get(respondTo.buttons.size() - 1).SetClip(buttonClips[hardnessInt - 1][rand.nextInt(buttonClips[hardnessInt - 1].length)]);
                        correctMatrix[i][j] = 0;
                        leftHandSide.remove(randomIndex);
                    }
                    else
                    {
                        mix += 0.25f;
                        randomIndex = rand.nextInt(rightHandSide.size());
                        respondTo.buttons.add(new PuzzleButton(frame, rightHandSide.get(randomIndex), initialSpawn + (dimensions * j), initialSpawnY + (dimensions * i), dimensions, 1));
                        respondTo.buttons.get(respondTo.buttons.size() - 1).SetHandler(respondTo);
                        respondTo.buttons.get(respondTo.buttons.size() - 1).SetClip(buttonClips[hardnessInt - 1][rand.nextInt(buttonClips[hardnessInt - 1].length)]);
                        correctMatrix[i][j] = 1;
                        rightHandSide.remove(randomIndex);
                    }
                }
                else
                {
                    if (leftHandSide.isEmpty())
                    {
                        randomIndex = rand.nextInt(rightHandSide.size());
                        respondTo.buttons.add(new PuzzleButton(frame, rightHandSide.get(randomIndex), initialSpawn + (dimensions * j), initialSpawnY + (dimensions * i), dimensions, 1));
                        respondTo.buttons.get(respondTo.buttons.size() - 1).SetHandler(respondTo);
                        respondTo.buttons.get(respondTo.buttons.size() - 1).SetClip(buttonClips[hardnessInt - 1][rand.nextInt(buttonClips[hardnessInt - 1].length)]);
                        rightHandSide.remove(randomIndex);
                        correctMatrix[i][j] = 1;
                    }
                    else
                    {
                        randomIndex = rand.nextInt(leftHandSide.size());
                        respondTo.buttons.add(new PuzzleButton(frame, leftHandSide.get(randomIndex), initialSpawn + (dimensions * j), initialSpawnY + (dimensions * i), dimensions, 0));
                        respondTo.buttons.get(respondTo.buttons.size() - 1).SetHandler(respondTo);
                        respondTo.buttons.get(respondTo.buttons.size() - 1).SetClip(buttonClips[hardnessInt - 1][rand.nextInt(buttonClips[hardnessInt - 1].length)]);
                        leftHandSide.remove(randomIndex);
                        correctMatrix[i][j] = 0;
                    }
                }
            }
        }
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < rowAmount; j++)
            {
                System.out.print(correctMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public Color mixColors(ArrayList<Color> colors) {
        if (colors.size() == 0)
        {
            return new Color(255, 255, 255);
        }
        float ratio = 1f / ((float) colors.size());
        int r = 0, g = 0, b = 0, a = 0;
        for (Color color : colors) {
            r += color.getRed() * ratio;
            g += color.getGreen() * ratio;
            b += color.getBlue() * ratio;
            a += color.getAlpha() * ratio;
        }
        return new Color(r, g, b, a);
    }

    private void Difficulty1() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        frame.getContentPane().setBackground(new Color(241, 194, 133));
        frame.setTitle("Kitties!");
        levelMusic = new AudioPlayer("src/resources/sfx/level1music.wav", true);
        levelMusic.Play();
    }

    public void Difficulty2() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        frame.setTitle("AutoPuzzle - Medium");
        Random rand = new Random();
        discoIcons[0] = ImageIO.read(getClass().getResource("images/decoy.png"));
        discoIcons[1] = ImageIO.read(getClass().getResource("images/decoy2.png"));
        discoIcons[2] = ImageIO.read(getClass().getResource("images/decoy3.png"));
        discoIcons[3] = ImageIO.read(getClass().getResource("images/decoy4.png"));
        discoIcons[4] = ImageIO.read(getClass().getResource("images/decoy5.png"));
        discoIcons[5] = ImageIO.read(getClass().getResource("images/decoy6.png"));
        discoIcons[6] = ImageIO.read(getClass().getResource("images/decoy7.png"));
        discoIcons[7] = ImageIO.read(getClass().getResource("images/decoy8.png"));
        BufferedImage temp1 = ImageIO.read(getClass().getResource("images/discobutton1.png"));
        BufferedImage temp2 = ImageIO.read(getClass().getResource("images/discobutton2.png"));
        Color colorsRandom[] = {Color.PINK, Color.RED, Color.YELLOW, Color.BLUE, Color.MAGENTA, Color.GREEN};
        for (PuzzleButton b : respondTo.buttons)
        {
            discoColors.add(colorsRandom[rand.nextInt(colorsRandom.length)]);
            Color value = discoColors.get(discoColors.size() - 1);
            discoButtonIcons.add(new ImageIcon(ColorTinter(value, temp1, dimensions, dimensions, false)));
            discoButtonIconsOff.add(new ImageIcon(ColorTinter(value, temp2, dimensions, dimensions, false)));
        }
        Timer secondPhase = new Timer();
        secondPhase.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Difficulty2Extra();
                } catch (UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 8000);
    }

    public void Difficulty2Extra() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        brightnessDecoy = new JLabel();
        brightnessDecoy.setBounds(0, 0, 1386, 780);
        brightnessDecoy.setBorder(null);
        frame.add(brightnessDecoy);
        AudioPlayer initial = new AudioPlayer("src/resources/sfx/boom.wav", false);
        initial.Play();
        frame.setTitle("Burn Baby Burn!");
        Timer buttonOnTimer = new Timer();
        buttonOnTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < respondTo.buttons.size(); i++)
                {
                    respondTo.buttons.get(i).SetIcons(discoButtonIcons.get(i), discoButtonIconsOff.get(i));
                    try {
                        respondTo.buttons.get(i).ToggleLight(respondTo.buttons.get(i).state);
                    } catch (UnsupportedAudioFileException e) {
                        throw new RuntimeException(e);
                    } catch (LineUnavailableException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, 500);
        Timer musicTimer = new Timer();
        musicTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    levelMusic = new AudioPlayer("src/resources/sfx/discoloop.wav", true);
                    levelMusic.Play();
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                }
                discoTimer = new Timer();
                discoTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Color avg = new Color(178, 178, 177);
                        ArrayList<Color> toMix = new ArrayList<Color>();
                        for (int i = 0; i < respondTo.buttons.size(); i++)
                        {
                            if (respondTo.buttons.get(i).state)
                            {
                                toMix.add(discoColors.get(i));
                            }
                        }
                        avg = mixColors(toMix);
                        toMix.clear();
                        toMix.add(avg);
                        toMix.add(new Color(255, 255, 255));
                        avg = mixColors(toMix);
                        brightnessDecoy.setIcon(new ImageIcon(ColorTinter(avg, discoIcons[discoAlternater], 1386, 780, false)));
                        frame.getContentPane().setBackground(new Color(51, 51, 51));
                        //frame.getContentPane().setBackground(avg);
                        if (discoAlternater != 7)
                        {
                            discoAlternater++;
                        }
                        else
                        {
                            discoAlternater = 0;
                        }
                    }
                }, 800, 1000);
            }
        }, 1550);
    }

    private void Difficulty4() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        frame.getContentPane().setBackground(new Color(0, 0, 0));

        Timer musicTimer = new Timer();
        musicTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                frame.setTitle("Psychosis is a collective term for a range of conditions characterised by impairment in reality testing. These include schizophrenia, schizoaffective disorder and delusional disorder. People with psychotic disorders may experience a range of symptoms including unusual thoughts or 'delusions', hallucinations, disorganised speech and behaviour, and emotional disturbances such as flatness and withdrawal. Both positive and negative symptoms can lead to significant disability.");
                try {
                    levelMusic = new AudioPlayer("src/resources/sfx/scary2.wav", true);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                }
                try {
                    levelMusic.Play();
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 3500);

    }

    private void Difficulty5() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        frame.getContentPane().setBackground(Color.black);
        frame.setTitle("Hug?");
        new AudioPlayer("src/resources/sfx/rise.wav", false).Play();
        BufferedImage input = ImageIO.read(getClass().getResource(onFilePaths[2]));
        Icon onIcon = new ImageIcon(ColorTinter(new Color(210, 210, 210, 127), input, input.getWidth(), input.getHeight(), false));
        Icon offIcon = new ImageIcon(ColorTinter(new Color(127, 127, 127, 127), input, input.getWidth(), input.getHeight(), false));
        for (int i = 0; i < 24; i++)
        {
            respondTo.buttons.get(i).SetClip(buttonClips[4][i]);
            respondTo.buttons.get(i).button.setVisible(false);
            respondTo.buttons.get(i).button.setOpaque(false);
        }
        Timer continueTask = new Timer();
        continueTask.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Difficulty5Extra(onIcon, offIcon);
                } catch (UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 15000);
    }

    private void Difficulty5Extra(Icon on1, Icon on2) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        frame.getContentPane().setBackground(new Color(41, 17, 52));
        new AudioPlayer("src/resources/sfx/scare.wav", false).Play();
        for (PuzzleButton b : respondTo.buttons)
        {
            b.SetIcons(on1, on2);
            b.button.setVisible(true);
        }
        frame.setTitle("you need to cut your arms and legs off to get a hug from mr. softy");
        levelMusic = new AudioPlayer("src/resources/sfx/kevin.wav", true);
        levelMusic.Play();
        cuddlesTimer = new Timer();
        cuddlesTimer.scheduleAtFixedRate(cuddlesTask, 0, 2000);
    }

    private void Randomizer(ArrayList<PuzzleButton> buttons, int buttonDimensions)
    {
        ArrayList<Point> points = new ArrayList<Point>();
        Random rand = new Random();
        float safeDistance = buttonDimensions * 1.3f;
        while (points.size() != buttons.size())
        {
            float xFactor = (float)buttonDimensions * 1.13333f;
            float yFactor = (float)buttonDimensions * 1.2f;
            Point toCheck = new Point(20 + rand.nextInt(1386 - Math.round(xFactor)), 20 + rand.nextInt(Math.round(780f - yFactor)));
            boolean shouldAdd = true;
            for (int i = 0; i < points.size(); i++)
            {
                if (Point.distance(points.get(i).x, points.get(i).y, toCheck.x, toCheck.y) < safeDistance)
                {
                    shouldAdd = false;
                    break;
                }
            }
            if (shouldAdd)
            {
                points.add(toCheck);
            }
        }
        for (int i = 0; i < buttons.size(); i++)
        {
            buttons.get(i).button.setLocation(points.get(i));
        }
    }

    //Here we create the image (texture actually) in desired colors to use on buttons we create.
    //This functionality was initially in the button class, but since we are creating lots of button instances,
    //it makes more sense to just create them in this class and pass it to newly created buttons. Both performance
    //and clarity wise.
    private void GenerateOnButtonGraphic(int dimensions) throws IOException
    {
        Icon onIcon;
        URL imgURL = getClass().getResource(onFilePaths[hardnessInt - 1]);
        BufferedImage bufImg = ImageIO.read(imgURL);
        BufferedImage convertedImg = new BufferedImage(bufImg.getWidth(), bufImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
        convertedImg.getGraphics().drawImage(bufImg, 0, 0, null);
        onIcon = new ImageIcon(ColorTinter(new Color(255, 255, 255, 255), convertedImg, dimensions, dimensions, false));
        respondTo.buttonOnFinal = onIcon;
    }

    //Pretty much the same as above. This one is just for the other side of the coin.
    private void GenerateOffButtonGraphic(int dimensions) throws IOException
    {
        Icon offIcon;
        URL imgURL = getClass().getResource(offFilePaths[hardnessInt - 1]);
        BufferedImage bufImg = ImageIO.read(imgURL);
        BufferedImage convertedImg = new BufferedImage(bufImg.getWidth(), bufImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
        convertedImg.getGraphics().drawImage(bufImg, 0, 0, null);
        offIcon = new ImageIcon(ColorTinter(new Color(255, 255, 255, 255), convertedImg, dimensions, dimensions, false));
        respondTo.buttonOffFinal = offIcon;
    }

    //A small function to assign brightness image, used to give the feeling that you are actually turning some lights on.
    private BufferedImage GenerateDecoy() throws IOException
    {
        URL imgURL = getClass().getResource("images/decoy.png");
        BufferedImage bufImg = ImageIO.read(imgURL);
        BufferedImage convertedImg = new BufferedImage(bufImg.getWidth(), bufImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
        convertedImg.getGraphics().drawImage(bufImg, 0, 0, null);
        return convertedImg;
    }

    //A helper function for generating button graphics. Salvaged from button class.
    private Image ColorTinter(Color shade, BufferedImage input, int dimensions1, int dimensions2, boolean fixed)
    {
        BufferedImage img = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
        BufferedImage bi;
        float red = shade.getRed();
        float green = shade.getGreen();
        float blue = shade.getBlue();
        float alpha = shade.getAlpha();
        if (red > 0f)
            red = red / 255f;
        if (green > 0f)
            green = green / 255f;
        if (blue > 0f)
            blue = blue / 255f;
        if (alpha > 0f)
            alpha = alpha / 255f;
        float factors[] = {0, 0, 0, 0};
        float offsets[] = {red, green, blue, alpha};
        RescaleOp op = new RescaleOp(offsets, factors, null);
        op.filter(input, img);
        if (fixed)
        {
            bi = new BufferedImage(1386, 780, BufferedImage.TYPE_INT_ARGB);
        }
        else
        {
            bi = new BufferedImage(dimensions1, dimensions2, BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        if (fixed)
        {
            g2d.drawImage(img, 0, 0, 1386, 780, null);
        }
        else
        {
            g2d.drawImage(img, 0, 0, dimensions1, dimensions2, null);
        }
        g2d.dispose();
        return bi;
    }
}
