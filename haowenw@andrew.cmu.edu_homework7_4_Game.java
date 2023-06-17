import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * @author Haowen Wu (Andrew ID: haowenw)
 */
public class Game {
    /**
     * Instance variable for start button.
     */
    private JButton startButton;
    /**
     * Instance variable for timer.
     */
    private JTextField timeLeftField;
    /**
     * Instance variable for score.
     */
    private JTextField scoreField;
    /**
     * Instance variable for mole holes.
     */
    private JButton[] moleHoles;
    /**
     * Constants.
     */
    private static final String UP_STRING = ":-)";
    /**
     * Constants.
     */
    private static final String DOWN_STRING = ":-(";
    /**
     * Constants.
     */
    private static final Color UP_COLOR = Color.GREEN;
    /**
     * Constants.
     */
    private static final Color DOWN_COLOR = Color.RED;
    /**
     * Constants.
     */
    private static final String HIT_STRING = ">_<";
    /**
     * Constants.
     */
    private static final Color HIT_COLOR = Color.DARK_GRAY;

    /**
     * Constructor. Create a Swing GUI.
     */
    public Game() {
        moleHoles = new JButton[104];
        Font font = new Font(Font.MONOSPACED, Font.BOLD, 14);
        JFrame window = new JFrame("Whack-a-mole");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(650, 630);
        JPanel pane = new JPanel();
        JPanel topPane = new JPanel();
        startButton = new JButton("Start");
        topPane.add(startButton);
        JLabel timeLeft = new JLabel("Time Left:");
        topPane.add(timeLeft);
        timeLeftField = new JTextField(5);
        timeLeftField.setEditable(false);
        topPane.add(timeLeftField);
        JLabel score = new JLabel("Score:");
        topPane.add(score);
        scoreField = new JTextField(5);
        scoreField.setEditable(false);
        topPane.add(scoreField);
        startButton.addActionListener(new StartButtonActionListener(startButton, timeLeftField, moleHoles, scoreField));
        for (int i = 0; i < moleHoles.length; i++) {
            moleHoles[i] = new JButton(DOWN_STRING);
            moleHoles[i].setBackground(DOWN_COLOR);
            moleHoles[i].setFont(font);
            moleHoles[i].setOpaque(true);
            moleHoles[i].addActionListener(new MoleHoleActionListener(moleHoles[i], scoreField, timeLeftField));
            pane.add(moleHoles[i]);
        }
        pane.add(topPane);
        window.setContentPane(pane);
        window.setVisible(true);
    }

    /**
     * TimeRunnable for timer thread.
     */
    private static class TimeRunnable implements Runnable {
        /**
         * start button.
         */
        private JButton startButton;
        /**
         * timer.
         */
        private JTextField timeLeftField;
        /**
         * mole holes.
         */
        private JButton[] moleHoles;
        TimeRunnable(JButton newStartButton, JTextField newTimeLeftField, JButton[] newMoleHoles) {
            startButton = newStartButton;
            timeLeftField = newTimeLeftField;
            moleHoles = newMoleHoles;
        }

        @Override
        public void run() {
            try {
                timeLeftField.setText("20");
                while (Integer.valueOf(timeLeftField.getText()) > 0) {
                    Thread.sleep(1000);
                    timeLeftField.setText(String.valueOf(Integer.valueOf(timeLeftField.getText()) - 1));
                }
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            } finally {
                startButton.setEnabled(true);
            }
        }
    }

    /**
     * MoleRunnable for each mole thread.
     */
    private static class MoleRunnable implements Runnable {
        /**
         * mole hole.
         */
        private JButton moleHole;
        /**
         * timer.
         */
        private JTextField timeLeftField;
        /**
         * random number generator.
         */
        private Random random = new Random();
        MoleRunnable(JButton newMoleHole, JTextField newTimeLeftField) {
            moleHole = newMoleHole;
            timeLeftField = newTimeLeftField;
        }

        @Override
        public void run() {
            try {
                while (timeLeftField.getText() == null || timeLeftField.getText().equals("") || Integer.valueOf(timeLeftField.getText()) > 0) {
                    int randomMoleDownTime = random.nextInt(5000);
                    Thread.sleep(randomMoleDownTime);
                    if (timeLeftField.getText() == null || timeLeftField.getText().equals("") || Integer.valueOf(timeLeftField.getText()) > 0) {
                        if (moleHole.getText().equals(DOWN_STRING)) {
                            moleHole.setText(UP_STRING);
                            moleHole.setBackground(UP_COLOR);
                            int randomMoleUpTime = random.nextInt(3000) + 1000;
                            Thread.sleep(randomMoleUpTime);
                            moleHole.setText(DOWN_STRING);
                            moleHole.setBackground(DOWN_COLOR);
                            Thread.sleep(2000);
                        }
                    }
                }
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        }
    }

    /**
     * Start Button Action Listener.
     */
    private static class StartButtonActionListener implements ActionListener {
        /**
         * start button.
         */
        private JButton startButton;
        /**
         * timer.
         */
        private JTextField timeLeftField;
        /**
         * mole holes.
         */
        private JButton[] moleHoles;
        /**
         * score.
         */
        private JTextField scoreField;

        /**
         * constructor.
         * @param newStartButton
         * @param newTimeLeftField
         * @param newMoleHoles
         * @param newScoreField
         */
        StartButtonActionListener(JButton newStartButton, JTextField newTimeLeftField, JButton[] newMoleHoles, JTextField newScoreField) {
            startButton = newStartButton;
            timeLeftField = newTimeLeftField;
            moleHoles = newMoleHoles;
            scoreField = newScoreField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            startButton.setEnabled(false);
            scoreField.setText("0");
            TimeRunnable r = new TimeRunnable(startButton, timeLeftField, moleHoles);
            Thread timeThread = new Thread(r);
            timeThread.start();

            for (int i = 0; i < moleHoles.length; i++) {
                MoleRunnable moleRunnable = new MoleRunnable(moleHoles[i], timeLeftField);
                Thread moleThread = new Thread(moleRunnable);
                moleThread.start();
            }
        }
    }

    /**
     * HitRunnable for hit thread.
     */
    private static class HitRunnable implements Runnable {
        /**
         * mole hole.
         */
        private JButton moleHole;
        /**
         *score field.
         */
        private JTextField scoreField;

        /**
         * constructor.
         * @param newMoleHole
         * @param newScoreField
         */
        HitRunnable(JButton newMoleHole, JTextField newScoreField) {
            moleHole = newMoleHole;
            scoreField = newScoreField;
        }

        @Override
        public void run() {
            if (moleHole.getText().equals(UP_STRING)) {
                synchronized (scoreField) {
                    scoreField.setText(String.valueOf(Integer.valueOf(scoreField.getText()) + 10));
                }
                moleHole.setText(HIT_STRING);
                moleHole.setBackground(HIT_COLOR);
            }
        }
    }

    /**
     * Action Listener for each mole hole button.
     */
    private static class MoleHoleActionListener implements ActionListener {
        /**
         * mole hole.
         */
        private JButton moleHole;
        /**
         * score field.
         */
        private JTextField scoreField;
        /**
         * time field.
         */
        private JTextField timeLeftField;

        MoleHoleActionListener(JButton newMoleHole, JTextField newScoreField, JTextField newTimeLeftField) {
            moleHole = newMoleHole;
            scoreField = newScoreField;
            timeLeftField = newTimeLeftField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (timeLeftField.getText() == null || timeLeftField.getText().equals("") || Integer.parseInt(timeLeftField.getText()) > 0) {
                Runnable r = new HitRunnable(moleHole, scoreField);
                Thread hitThread = new Thread(r);
                hitThread.start();
            }
        }
    }

    /**
     * Main method.
     * @param args
     */
    public static void main(String[] args) {
        new Game();
    }

}
