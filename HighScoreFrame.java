import java.awt.*;
import javax.swing.*;

public class HighScoreFrame extends JFrame{
    HighScorePanel panel;
	
	HighScoreFrame() { 
		
		panel = new HighScorePanel();

		this.getContentPane().add(panel);

		this.setTitle("High Score");
		this.setResizable(false);
		this.setBackground(Color.black);
		this.pack();
		this.setVisible(true);
	}

}
