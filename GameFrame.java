
import java.awt.*;
import javax.swing.*;

public class GameFrame extends JFrame {
	
	GamePanel panel;
	
	GameFrame() { 
		
		//Creates a GamePanel 
		panel = new GamePanel();

		//Adds the GamePanel inside the GameFrame
		this.getContentPane().add(panel);

		this.setTitle("Bricks Crusher");
		this.setResizable(false);
		this.setBackground(Color.black);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	
	}

} 

//************************************************************************************************************
//1. GameFrame class starts with inheriting JFrame class
//Ans. JFrame is a top-level container that provides a window on the screen. A frame is actually a base window on which other components rely, namely the menu bar, panels, labels, text fields, buttons, etc. Almost every other Swing application starts with the JFrame window.

//2. GameFrame constructor creates GamePanel --> it uses getContentPane() to get the frame and adds the panel to it
//Ans. The getContentPane() method retrieves the content pane layer so that you can add an object to it. The content pane is an object created by the Java run time environment. You do not have to know the name of the content pane to use it.

//3. The pack() method is defined in Window class in Java and it sizes the frame so that all its contents are at or above their preferred sizes. An alternative to the pack() method is to establish a frame size explicitly by calling the setSize() or setBounds() methods.
//4. The setVisible(true) method makes the frame appear on the screen. If you forget to do this, the frame object will exist as an object in memory, but no picture will appear on the screen.
//5. this.setLocationRelativeTo(null) ---> By default, a JFrame can be displayed at the top-left position of a screen. We can display the center position of JFrame using the setLocationRelativeTo() method of Window class.