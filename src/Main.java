import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
//can i play
//mild
//moderate
//nightmare
//mr. cuddles

public class Main {
    public static JFrame frame;
    public static JLabel title;
    public static ArrayList<JButton> menuButtons = new ArrayList<JButton>();
    public static ArrayList<JSlider> menuSliders = new ArrayList<JSlider>();
    public static JTextArea difficultyDescription;
    public static boolean stopTimers = false;
    public static JScrollPane textSP;
    public static ArrayList<JLabel> descLabels = new ArrayList<JLabel>();
    public static ArrayList<Icon> difficultyIcons = new ArrayList<Icon>();
    public static ArrayList<Icon> difficultyIconsHovered = new ArrayList<Icon>();
    public static boolean difficultyOn = false;
    public static Font buttonFont;
    public static Color menuColor = new Color(154, 154, 154);
    public static int savedDifficulty = 0;
    public static int menuCounter = 0;
    public static String menuDesc = "";
    public static int textCounter = 0;
    public static float textIncrement = 0;
    public static LevelHandler handler;
    public static PuzzleCreator gameCreator;
    public static AudioPlayer menuMusic;
    public static Color buttonShades[] = {null, null, null, null, null};
    public static int sliderValues[][] = {{50, 10, 5, 60, 20}, {20, 70, 15, 40, 40}, {50, 50, 50, 50, 50}, {75, 55, 15, 5, 70}, {0, 0, 0, 100, 0}};
    public static String difficultyDescriptions[] = {"Tired of the bragging of a speedy hare, a tortoise challenges it to a race. The overconfident hare accepts the competition and runs as fast as it can after the race begins. Soon it gets tired and decides to rest, thinking that there’s plenty of time to relax before tortoise can catch up with it. Meanwhile, the tortoise continues to walk slowly, until it reaches the finish line. The overslept hare wakes up, only to be shocked that a slow moving tortoise beat it in the race."
    , "Surrounding a dimension such as ours there exists five walls. The first, third, fourth, and fifth walls are irrevelant. But what is important is the second wall. Now, the second wall may SOUND like an ordinary wall, it may LOOK like an ordinary wall, it may even SMELL like an ordinary wall. But brother, it is NOT an ordinary wall. \n" + "\n" + "I hope this answers your questions about the difficulty level.",
    "This difficulty preset is how the game meant to be. You can think this as a base class in programming. Basically all other presets derived from this. Truly the master of this game. \n" + "\n" + "Now I know what you are thinking, you are thinking this preset is very boring despite the fact you now know what it did. You know for a fact that all other funny presets derived from this, but you are still thinking this is boring as hell after you saw the other ones. Can't blame you. Neutral characters are boring. \n" + "\n" + "But you gotta know true neutral characters are hard to write and, subsequently, are rare characters. Almost everyone has some motivation aside from preservation and a moral compass outside of observation. They are also a hard type to develop because their nature is static. They don't care about you, and they never will.\n" +
            "\n" +
            "On the other hand, they are adept at honing their skills in their area of interest, more so than different character types. If they are interested in self-preservation via social climbing, they will eventually rise to the upper echelons of society. They have the potential to obsess their way into becoming a paragon. If they survive long enough, they could be the most peaceful monk, the world’s most powerful orator, the fastest swimmer, the highest-paid assassin, and so on. The important part is that their interest is all-encompassing.",
    "Two hundred and forty one armless and legless people crawl towards me inside my room. They are not angry, seemingly not in pain, they actually have a grateful graze towards me. It's like they are thanking me for reasons I don't know yet.\n" + "\n" + "But seriously, what is wrong with these people? I would like to know if they need something. But I got this strong feeling that they want to hug me. But it's silly, since their arms and legs are missing, only I can hug them, not the other way around.\n" + "\n" + "Hugging two hundred and forty one armless and legless strangers might be weird. But I am too soft to turn these strangers down now.\n" + "\n",
    "excuse me? uhhh, yes i'd like to get a hug from mr. softy, please."};
    public static void main(String[] args) throws IOException, FontFormatException, UnsupportedAudioFileException, LineUnavailableException {
        frame = new JFrame();
        SetIcons();
        CreateMainMenu(false);
        frame.setBounds(66, 34, 1386, 780);
    }

    public static void MainPuzzle(int value) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        if (title != null)
        {
            frame.remove(title);
            title = null;
            for (JButton b : menuButtons)
            {
                frame.remove(b);
            }
            for (JLabel l : descLabels)
            {
                frame.remove(l);
            }
            for (JSlider s : menuSliders)
            {
                frame.remove(s);
            }
            frame.remove(textSP);
            menuButtons = new ArrayList<JButton>();
            descLabels = new ArrayList<JLabel>();
            menuSliders = new ArrayList<JSlider>();
        }
        menuMusic.Stop();
        menuMusic = null;
        stopTimers = false;
        handler = new LevelHandler();
        handler.SetHardnessMultiplier(value);
        handler.brightnessDecoy = new JLabel();
        frame.add(handler.brightnessDecoy);
        gameCreator = new PuzzleCreator();
        gameCreator.respondTo = handler;
        gameCreator.frame = frame;
        gameCreator.SetHardness(0.2f * value, value);
        gameCreator.GeneratePuzzle();
        handler.Initialize();
    }

    public static void SetIcons() throws IOException {
        BufferedImage in1 = ImageIO.read(Main.class.getResource("images/MenuButtons/button1.png"));
        BufferedImage in2 = ImageIO.read(Main.class.getResource("images/MenuButtons/button3.png"));
        buttonShades[0] = new Color(182, 166, 93);
        buttonShades[1] = new Color(88, 157, 151);
        buttonShades[2] = new Color(19, 30, 101);
        buttonShades[3] = new Color(30, 30, 30);
        buttonShades[4] = new Color(100, 43, 103);
        Color buttonShadesHovered[] = {Color.WHITE ,Color.YELLOW, Color.CYAN, Color.BLUE, Color.DARK_GRAY, Color.PINK};
        difficultyIcons.add(new ImageIcon(Main.class.getResource("images/MenuButtons/button1.png")));
        for (Color c : buttonShades)
        {
            difficultyIcons.add(new ImageIcon(Tinter(c, in1)));
        }
        for (Color c : buttonShadesHovered)
        {
            difficultyIconsHovered.add(new ImageIcon(Tinter(c, in2)));
        }
    }

    public static int MenuClamp(int value, int min, int max)
    {
        if (value < min)
            value = min;
        if (value > max)
            value = max;
        return value;
    }

    private static Color interpolate(Color c1, Color c2, float fracFromC1)
    {
        float fracFromC2 = 1.0f - fracFromC1;
        return new Color(Math.round(fracFromC1 * c1.getRed() + fracFromC2 * c2.getRed()),
                Math.round(fracFromC1 * c1.getGreen() + fracFromC2 * c2.getGreen()),
                Math.round(fracFromC1 * c1.getBlue() + fracFromC2 * c2.getBlue()),
                Math.round(fracFromC1 * c1.getAlpha() + fracFromC2 * c2.getAlpha()));
    }

    public static void CreateMainMenu(boolean again) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        difficultyOn = false;
        handler = null;
        gameCreator = null;
        Font ttfBase = null;
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setTitle("AutoPuzzle");
        menuMusic = new AudioPlayer("src/resources/sfx/ordinaryambient.wav", true);
        menuMusic.Play();
        frame.getContentPane().setBackground(new Color(154, 154, 154));
        if (again)
        {
            MainPuzzle(savedDifficulty);
            return;
        }
        title = new JLabel();
        title.setText("AutoPuzzle");
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
        title.setBounds(443, -100, 500, 400);
        title.setLayout(null);
        title.setHorizontalTextPosition(JLabel.CENTER);
        title.setVerticalTextPosition(JLabel.CENTER);
        title.setFont(ttfBase);
        title.setFont(new Font(title.getFont().getName(), Font.BOLD, 90));
        buttonFont = new Font(title.getFont().getName(), Font.BOLD, 40);
        title.setForeground(Color.WHITE);
        frame.add(title);
        menuButtons.add(new JButton());
        menuButtons.get(0).setIcon(difficultyIcons.get(0));
        menuButtons.get(0).setText("Play");
        menuButtons.get(0).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (difficultyOn)
                {
                    CloseDifficultyMenu();
                    difficultyOn = false;
                }
                else
                {
                    BringUpDifficultyMenu();
                    difficultyOn = true;
                }
            }
        });
        MouseListener mouseEvent = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (menuCounter != 0)
                    return;
                JButton sender = (JButton) e.getSource();
                int i = 0;
                for (JButton b : menuButtons)
                {
                    if (b == sender)
                    {
                        break;
                    }
                    i++;
                }
                for (int z = 0; z < menuButtons.size(); z++)
                {
                    menuButtons.get(z).setIcon(difficultyIcons.get(z));
                }
                sender.setIcon(difficultyIconsHovered.get(i));
                if (i > 0)
                {
                    textSP.setVisible(true);
                    for (JSlider s : menuSliders)
                    {
                        s.setVisible(true);
                    }
                    for (JLabel l : descLabels)
                    {
                        l.setVisible(true);
                    }
                    Timer updater = new Timer();
                    int finalI = i - 1;
                    updater.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            float percent = (float)menuCounter / 20f;
                            for (int j = 0; j < menuSliders.size(); j++)
                            {
                                int a = menuSliders.get(j).getValue();
                                menuSliders.get(j).setValue(a + Math.round((float)(sliderValues[finalI][j] - a) * percent));
                            }
                            Color f = frame.getContentPane().getBackground();
                            Color value = interpolate(f, buttonShades[finalI], 1 - percent);
                            frame.getContentPane().setBackground(value);
                            menuCounter++;
                            if (menuCounter > 20)
                            {
                                menuCounter = 0;
                                updater.cancel();
                            }
                        }
                    }, 0, 25);
                    Timer textHandler = new Timer();
                    textHandler.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            textIncrement += (float)difficultyDescriptions[finalI].length() / 500;
                            char copiedValue[] = new char[Math.round(textIncrement)];
                            for (int k = 0; k < MenuClamp(Math.round(textIncrement), 0, difficultyDescriptions[finalI].length()); k++)
                            {
                                menuDesc += difficultyDescriptions[finalI].charAt(k);
                            }
                            difficultyDescription.setText(menuDesc);
                            textCounter++;
                            menuDesc = "";
                            if (textCounter > 500)
                            {
                                textCounter = 0;
                                textIncrement = 0;
                                menuDesc = "";
                                textHandler.cancel();
                            }
                        }
                    }, 0 ,1);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
        ActionListener difficultySelect = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton sender = (JButton) e.getSource();
                int i = 0;
                for (JButton b : menuButtons)
                {
                    if (b == sender)
                    {
                        break;
                    }
                    i++;
                }
                try
                {
                    savedDifficulty = i;
                    MainPuzzle(i);
                } catch (IOException ex)
                {
                    throw new RuntimeException(ex);
                } catch (UnsupportedAudioFileException ex) {
                    throw new RuntimeException(ex);
                } catch (LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        String buttonNames[] = {"Can I Play, Mommy?", "Electric Boogaloo", "Medium", "Nightmare", "Mr. Softy"};
        AdjustButton(menuButtons.get(0), 0);
        menuButtons.get(0).addMouseListener(mouseEvent);
        for (int i = 0; i < 5; i++)
        {
            menuButtons.add(new JButton());
            menuButtons.get(i + 1).setIcon(difficultyIcons.get(i + 1));
            menuButtons.get(i + 1).setText(buttonNames[i]);
            AdjustButton(menuButtons.get(i + 1), i + 1);
            menuButtons.get(i + 1).setVisible(false);
            menuButtons.get(i + 1).addMouseListener(mouseEvent);
            menuButtons.get(i + 1).addActionListener(difficultySelect);
        }
        String descNames[] = {"Fear", "Grief", "Anger", "Joy", "Difficulty", "Difficulty Description"};
        for (int i = 0; i < 5; i++)
        {
            menuSliders.add(new JSlider(){
                @Override
                public void updateUI() {
                    setUI(new SliderCustom(this));
                }
            });
            menuSliders.get(i).setBounds(90, 240 + (i * 100), 300, 100);
            menuSliders.get(i).setOpaque(false);
            menuSliders.get(i).setBackground(new Color(255, 255, 255, 0));
            menuSliders.get(i).setEnabled(false);
            menuSliders.get(i).setVisible(false);
            menuSliders.get(i).setMaximum(100);
            menuSliders.get(i).setMinimum(0);
            frame.add(menuSliders.get(i));
            descLabels.add(new JLabel(descNames[i]));
            descLabels.get(i).setFont(new Font(title.getFont().getName(), Font.BOLD, 40));
            descLabels.get(i).setBounds(90, 190 + (i * 100), 300, 100);
            descLabels.get(i).setForeground(Color.WHITE);
            descLabels.get(i).setVisible(false);
            frame.add(descLabels.get(i));
        }
        difficultyDescription = new JTextArea();
        difficultyDescription.setLineWrap(true);
        difficultyDescription.setWrapStyleWord(true);
        difficultyDescription.setBackground(new Color(0, 0, 0, 0));
        difficultyDescription.setOpaque(false);
        difficultyDescription.setForeground(Color.WHITE);
        difficultyDescription.setFont(new Font(title.getFont().getName(), Font.BOLD, 20));
        textSP = new JScrollPane(difficultyDescription);
        textSP.setBounds(1000, 215, 300, 500);
        textSP.setBackground(new Color(0, 0, 0, 0));
        textSP.setForeground(new Color(0, 0, 0, 0));
        textSP.getViewport().setOpaque(false);
        textSP.setOpaque(false);
        frame.add(textSP);
        textSP.setVisible(false);
    }

    public static void BringUpDifficultyMenu()
    {
        menuButtons.get(0).setText("Back");
        for (int i = 1; i < menuButtons.size(); i++)
        {
            menuButtons.get(i).setVisible(true);
        }
    }

    public static void CloseDifficultyMenu()
    {
        menuButtons.get(0).setText("Play");
        for (int i = 1; i < menuButtons.size(); i++)
        {
            menuButtons.get(i).setVisible(false);
            menuButtons.get(i).setIcon(difficultyIcons.get(i));
        }
        textSP.setVisible(false);
        for (JSlider s : menuSliders)
        {
            s.setVisible(false);
        }
        for (JLabel l : descLabels)
        {
            l.setVisible(false);
        }
        Timer updater = new Timer();
        updater.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                float percent = (float)menuCounter / 20f;
                Color f = frame.getContentPane().getBackground();
                Color value = interpolate(f, menuColor, 1 - percent);
                frame.getContentPane().setBackground(value);
                menuCounter++;
                if (menuCounter > 20)
                {
                    menuCounter = 0;
                    updater.cancel();
                }
            }
        }, 0, 25);
    }

    public static void AdjustButton(JButton toAdjust, int multiplier)
    {
        toAdjust.setContentAreaFilled(false);
        toAdjust.setBorder(null);
        toAdjust.setFont(buttonFont);
        toAdjust.setHorizontalTextPosition(0);
        toAdjust.setVerticalTextPosition(0);
        toAdjust.setForeground(Color.WHITE);
        toAdjust.setBounds(390,140 + (multiplier * 100), 600, 100);
        frame.add(toAdjust);
    }

    public static Image Tinter(Color shade, BufferedImage input)
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
        bi = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.drawImage(img, 0, 0, input.getWidth(), input.getHeight(), null);
        g2d.dispose();
        return bi;
    }
}