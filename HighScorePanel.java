import java.io.*;
import java.awt.*;
import javax.swing.*; 

public class HighScorePanel extends JPanel {

    //Gameplay Frame
	static final int SCORE_WIDTH = 251;
	static final int SCORE_HEIGHT = (int)(SCORE_WIDTH * (1.25));
	static final Dimension SCREEN_SIZE = new Dimension(SCORE_WIDTH, SCORE_HEIGHT);

    Font atari;

    HighScorePanel(){

        try {
			InputStream fontLocation = getClass().getResourceAsStream("fonts/Atari.ttf");
			atari = Font.createFont(Font.TRUETYPE_FONT, fontLocation).deriveFont(20f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setPreferredSize(SCREEN_SIZE);
    }
}
