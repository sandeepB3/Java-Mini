
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*; 
import javax.sound.sampled.*;

import javax.swing.*; 

public class GamePanel extends JPanel implements Runnable {

	//Gameplay Frame
	static final int GAME_WIDTH = 461;
	static final int GAME_HEIGHT = (int)(GAME_WIDTH * (1.25));
	static final Dimension SCREEN_SIZE1 = new Dimension(GAME_WIDTH, GAME_HEIGHT);
	
	//Paddles
	static final int PADDLE_WIDTH = 55;
	static final int PADDLE_HEIGHT = 10; 
	
	// static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
	//B alls
	static final int BALL_DIAMETER = 8; 

	//Initial vals
	int lives = 3;
	int score = 0;
	int hits = 0;
	int choice = 0;
	int inclinationSelection = 0;
	String welcomeMessage = "PRESS SPACE";

	boolean attractModeActive = true;
	boolean soundPlaying;
	boolean allCleared;

	static final int rows = 14;
	static final int columns = 8;
	
	static final int brickWidth = 32;
	static final int brickHeight = 10;
	
	
	static final int BORDER_OFFSET = 20 ; 
	
	static final int DISTANZA = 20; 
	
	//Game backend
	Thread gameThread; 
	BufferedImage buffer; 
	Graphics graphics;
	
	//Game elements
	Paddle paddle1;
	Ball ball;
	Brick[][] brick;
	Welcome welcome;
	Lives livesUI;
	Score scoreUI;

	//Asthetics
	Font atari;
	Color ballColor;
	Random random;
	Clip sound;
	
	GamePanel(){ 
		
		random = new Random();

		brick = new Brick[rows][columns];
		livesUI = new Lives(GAME_WIDTH - 20, GAME_HEIGHT - 20, 20, 20);
		scoreUI = new Score(15, GAME_HEIGHT - 20, 20, 20);
		ballColor = Color.white;

		try {
			InputStream fontLocation = getClass().getResourceAsStream("fonts/Atari.ttf");
			atari = Font.createFont(Font.TRUETYPE_FONT, fontLocation).deriveFont(20f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		this.setFocusable(true);
		this.setPreferredSize(SCREEN_SIZE1);
		gameThread = new Thread(this);
		gameThread.start();
		
		attractModePaddles();
		newBricks();
		newBall();
		newWelcome();
		
		this.setFocusable(true);
		this.setPreferredSize(SCREEN_SIZE1);
		
		//---------------------------------------------
		//

		this.addKeyListener(new AL());
		this.addMouseMotionListener(new AM());
		//
		//---------------------------------------------
		gameThread = new Thread(this);
		gameThread.start();
	}

	//Creating a Paddle object using Paddle class
	public void newPaddles() {
		
		paddle1 = new Paddle((GAME_WIDTH - PADDLE_WIDTH) / 2, GAME_HEIGHT - (PADDLE_HEIGHT - DISTANZA / 2) - 50, PADDLE_WIDTH, PADDLE_HEIGHT);

	}

	//Creating Bricks using Bricks class
	public void newBricks() {
		for (int p = 0; p < rows; p++) {
			for (int l = 0; l < columns; l++) {
				brick[p][l] = new Brick(p, l, brickWidth, brickHeight);
			}
		}
	}

	// Spawns a new Ball, makes it go to the bottom, and resets the hits.
	public void newBall() {
		ball = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2), (GAME_HEIGHT / 2) - (BALL_DIAMETER / 2), BALL_DIAMETER, BALL_DIAMETER);
		ball.setDY(2);
		hits = 0;
	}

	//Creates object of Welcome class
	public void newWelcome() {
		welcome = new Welcome(GAME_WIDTH / 2, GAME_HEIGHT / 2, GAME_WIDTH / 15, GAME_HEIGHT / 15);
	}

	//To make 
	public void destroyWelcome() {
		welcomeMessage = " ";
	}

	//Code to play sound
	public void playSound(String fileName) {
		
		if (soundPlaying == false) {
			try {
				sound = AudioSystem.getClip();
				sound.open(AudioSystem.getAudioInputStream(getClass().getResource("audio/" + fileName)));
				soundPlaying = true;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Couldn't play sound due to an error. Check above this message to see what happened.");
			}
		}

		if (soundPlaying == true) {
			sound.start();
		}
		
		soundPlaying = false;
	}
	
//------------------------------------------------------------------------
//This method is needed to draw something on JPanel other than drawing the background color. 
//This method already exists in a JPanel class so that we need to use the super declaration to add something to this method and takes Graphics objects as parameters. 
//The super.paintComponent() which represents the normal the paintComponent() method of the JPanel which can only handle the background of the panel must be called in the first line.
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		//Java BufferedImage class is a subclass of Image class. It is used to handle and manipulate the image data. A BufferedImage is made of ColorModel of image data. 
		//All BufferedImage objects have an upper left corner coordinate of (0, 0).
		buffer = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_RGB);

		//When you are writing your own applet or other component class, you need to call the (inherited) getGraphics() method in the same class. 
		//So, you would say simply "g = getGraphics()". This gives you a graphics context for drawing in the component you are writing.
		graphics = buffer.getGraphics();
		
		draw(graphics);
		
		g.drawImage(buffer, 0, 0, this);
		
	}
//----------------------------------------------------------------------------

//Draws initial state that is the attractive mode
	public void draw(Graphics g) {
		allCleared = true;

		if (attractModeActive == true) {

			switch (choice) {
				case 0:
					ballColor = Color.cyan;
					break;
				case 1:
					ballColor = Color.magenta;
					break;
				case 2:
					ballColor = Color.red;
					break;
				case 3:
					ballColor = Color.orange;
					break;
				case 4:
					ballColor = Color.yellow;
					break;
				case 5:
					ballColor = Color.green;
					break;
				default: 
					ballColor = Color.white;
					break;
			}

		}
		
		paddle1.draw(g);
		ball.draw(g, ballColor);
		welcome.draw(g, atari, GAME_WIDTH, GAME_HEIGHT, welcomeMessage);

		for (int p = 0; p < rows; p++) {
			for (int l = 0; l < columns; l++) {
				if (brick[p][l] != null) {
					brick[p][l].draw(g);
					allCleared = false;
				}
			}
		}
		
		//Here too
		if (allCleared == true) {
			beginAttractMode();
			welcomeMessage = "YOU WON!";
		}

		livesUI.draw(g, atari, GAME_WIDTH, GAME_HEIGHT, lives);
		scoreUI.draw(g, atari, GAME_WIDTH, GAME_HEIGHT, score);
		// disegna altri oggetti qui
		
        // the following line helps with animation ---------------------------
        Toolkit.getDefaultToolkit().sync(); 
        // This method ensures that the display is up-to-date. 
        // It is useful for animation: timing the painting operation 
        // should be performed by calling Toolkit.sync() 
        // after each paint to ensure the drawing commands 
        // are flushed to the graphics card. ---------------------------------  
	}

	public void move() {
		
		paddle1.move();
		ball.move();
		
	}

	public void checkCollision() {
		
		//To set paddle position within bounds
		if (paddle1.x <= 0)
			paddle1.x = 0;

		if (paddle1.x >= GAME_WIDTH-PADDLE_WIDTH) 
			paddle1.x = GAME_WIDTH - PADDLE_WIDTH; 
		
		if (ball.y <= 0) {
			ball.dy= -ball.dy;
			playSound("boundary.wav");
		}

		if (ball.y >= GAME_HEIGHT - BALL_DIAMETER) {
			ball.dy= -ball.dy;

			if (lives > 0) {
				lives = lives - 1;
			}

			checkIfLost(lives);
			newBall();
			playSound("boundary.wav");
		}

		if (ball.x <= 0) {
			ball.dx= -ball.dx;
			playSound("boundary.wav");	

			if (attractModeActive == true) {
				choice = random.nextInt(6);
			}
		}

		if (ball.x >= GAME_WIDTH-BALL_DIAMETER) {
			ball.dx= -ball.dx;
			playSound("boundary.wav");
			
			if (attractModeActive == true) {
				choice = random.nextInt(6);
			}
		}
		
		// This code handles collisions with the Paddle.
		if (ball.intersects(paddle1)) {
			double inclination;

			// This checks if the game is in Attract Mode when having a collision with the Paddle.
			if (attractModeActive != true) {

				// This keeps track of how many times the Ball touched the Paddle.
				// It's going to be useful to set the speed.
				hits = hits + 1;

				// This awful if-else chain handles the inclination the Ball needs to take when 
				// having a collision with the Paddle. This ensures the Ball does not go in the same
				// places and keeps the game fun.
				if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + PADDLE_WIDTH / 8) {
					inclination = -1.6;
				} else {
					if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 2) {
						inclination = -1.4;
					} else {
						if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 3) {
							inclination = -0.7;
						} else {
							if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 5) {
								inclination = 0.55;

								if (random.nextInt(2) == 0) {
									inclination = inclination * -1;
								}

							} else {
								if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 6) {
									inclination = 0.7;
								} else {
									if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 7) {
										inclination = 1.4;
									} else {
										inclination = 1.6;
									}
								}
							}
						}
					}	
				}

			} else {
				
				// If the game is in Attract Mode, choose a Random Inclination.
				// Also, change the ball's color.
				choice = random.nextInt(6);

				inclinationSelection = random.nextInt(3);
				
				switch (inclinationSelection) {
					case 0:
						inclination = 1.6;
						break;
					case 1:
						inclination = 1.4;
						break;
					case 2:
						inclination = 0.7;
						break;
					default: 
						inclination = 0.55;
						break;
				}

				inclinationSelection = random.nextInt(2);

				if (inclinationSelection == 0) {
					inclination = inclination * -1;
				}
				
			}

			// Calculating the Ball's speed.
			if (hits < 4) {
				ball.setDY(1);
			}

			if (hits >= 4 && hits < 12) {
				ball.setDY(1.5);
			}

			if (hits >= 12) {
				ball.setDY(2);
			}

			// Setting the values inside the class after calculating the inclination.
			ball.dy = -ball.dy;
			ball.setDX(inclination);
			playSound("paddle.wav");

		}

		// This code takes care of Brick collisions.
		for (int r = 0; r < rows; r++) {
			for (int t = 0; t < columns; t++) {
				if (brick[r][t] != null) {
					if (ball.intersects(brick[r][t])) {
						ball.dy = -ball.dy;
						playSound("brick.wav");
						
						if (attractModeActive != true) {
							brick[r][t] = null;

							// This Switch gives proper score based on the Brick's position,
							switch (t) {
								case 0: 
									score += 7;
									break;
								case 1: 
									score += 7;
									break;
								case 2:
									score += 5;
									break;
								case 3:
									score += 5; 
									break;
								case 4:
									score += 3;
									break;
								case 5:
									score += 3;
									break;
								default:
									score += 1;
									break;
							}
							

						} else {
							choice = random.nextInt(4);
						}
					}
				}
			}
		}
		//---------------------------------------------------

	
	}

//Multi-threading for Ball motion, paddle motion, checking collosion
	public void run() { 

		long lastTime = System.nanoTime();
		double amountOfFPS = 60.0;
		double duration = 1000000000 / amountOfFPS; 
		double delta = 0;

		while (true) { 
			long now = System.nanoTime();
			delta += (now - lastTime) / duration;
			lastTime = now;

			if(delta >=1) {
				move();
				checkCollision();

				repaint(); 
				delta--;
			} 
		}
		
	}

	//Listening to keyboard events
	
	public class AL extends KeyAdapter{  

		public void keyPressed(KeyEvent e) {
			
			//paddle1.keyPressed(e);
			if((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && attractModeActive == false) {
				paddle1.setDeltaX(-1);
			}

			if((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && attractModeActive == false) {
				paddle1.setDeltaX(+1);
			}

			if(e.getKeyCode() == KeyEvent.VK_SPACE && attractModeActive == true) {
				attractModeActive = false;
				beginGame();
			}
			
		}
		
		public void keyReleased(KeyEvent e) {
			
			if((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && attractModeActive == false) {
				paddle1.setDeltaX(0);
			}

			if((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && attractModeActive == false) {
				paddle1.setDeltaX(0);
			}
			
		}
	
	}

	public class AM implements MouseMotionListener{

		public void mouseMoved(MouseEvent e) {
			//Right motion
			if (e.getX() > paddle1.x  && attractModeActive == false){
				paddle1.x+=20;
			}
			//Left motion
			if (e.getX() < paddle1.x  && attractModeActive == false){
				paddle1.x-=20;
			}
		}

		public void mouseDragged(MouseEvent e) {}

	}
	
	//Here to implement
	public void checkIfLost(int lives) {
		int remainingLives = lives;

		if (remainingLives < 1) {
			beginAttractMode();
		}	
	}

	public void beginAttractMode() {
		attractModePaddles();
		newWelcome();
		attractModeActive = true;
		welcomeMessage = "PRESS SPACE";
	}

	public void attractModePaddles() {
		paddle1 = new Paddle(0, GAME_HEIGHT - (PADDLE_HEIGHT - DISTANZA / 2) - 50, GAME_WIDTH, PADDLE_HEIGHT);
	}

	public void beginGame() {
		newPaddles();
		newBall();
		newBricks();
		destroyWelcome();

		lives = 3;
		score = 0;

		ballColor = Color.white;
	}
	
} //end GamePanel


//GamePanel.java extends Jpanel this is added inside JFrame
//JPanel, a part of the Java Swing package, is a container that can store a group of components. The main task of JPanel is to organize components, various layouts can be set in JPanel which provide better organization of components, however, it does not have a title bar.

//The implements keyword is used to implement an interface . The interface keyword is used to declare a special type of class that only contains abstract methods. To access the interface methods, the interface must be "implemented" (kinda like inherited) by another class with the implements keyword (instead of extends ).
//runnable interface in Java is an interface whose instances can run as a Thread. While working with Threads, the runnable interface acts as a core element of the Java programming language. Java classes created to run Threads must implement this interface.

//setFocusable(boolean) isFocusable() Sets or gets the focusable state of the component. A component must be focusable in order to gain the focus.