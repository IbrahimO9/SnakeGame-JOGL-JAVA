package test;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.sun.javafx.tk.ImageLoader;

import javax.media.opengl.glu.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.awt.*;
import javax.swing.*;


public class Snake extends JFrame implements GLEventListener, KeyListener {
    // Game control variables
    int snakeStartSize = 5;         // Initial snake length
    float movementStep = 2.5f;      // Smaller increments for smoother movement
    int dotSize = 8;                // Dot size
    int snakeSpeed = 20;            // Initial speed (ms delay)   20 default
    int dotDisappearTime = 5000;    // Initial dot disappear time (ms) 5000 default
    
    int maxScore = 50;              // Maximum score to end the game
    int minSpeed = 1;               // Minimum speed
    int minDotTime = 1500;          // Minimum dot disappear time

    ArrayList<float[]> snake = new ArrayList<>(); // Snake with float positions
    float dotX, dotY; // Random dot position
    int score = 0; // Score
    boolean dotVisible = false; // If the dot is visible
    GLCanvas canvas;
    Animator animator;

    Random random = new Random();
    Timer gameTimer, dotTimer;
    TextRenderer textRenderer;

    // Directions
    final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
    int currentDirection = RIGHT;

    public Snake() {
        super("Smooth Snake Game");

        // Show initial menu
        showInitialMenu();

        canvas = new GLCanvas();
        add(canvas);
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        animator = new Animator(canvas);
        animator.start();

        // Initialize snake with dynamic segments
        for (int i = 0; i < snakeStartSize; i++) {
            snake.add(new float[]{-i * 10, 0});
        }

        // Timer for snake movement
        gameTimer = new Timer(snakeSpeed, e -> updateSnake());
        gameTimer.start();

        // Timer for dot disappearance
        dotTimer = new Timer(dotDisappearTime, e -> {
            dotVisible = false;
            generateRandomDot();
        });
        dotTimer.start();

        setSize(600, 400);
        setVisible(true);
        setLocation(200, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void showInitialMenu() {
    	snakesound(4);
        String[] options = {"Play", "Exit"};
        int choice = JOptionPane.showOptionDialog(this,
                "Welcome to the Snake Game!",
                "Main Menu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);
        
        if (choice == 0) {
            // Start the game
        	snakesound(5);
            return;
        } else {
            System.exit(0);
        }
        
        
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black background
        gl.glMatrixMode(GL2.GL_PROJECTION);
        glu.gluOrtho2D(-300, 300, -200, 200); // Coordinate system
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        generateRandomDot();
        textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 18));

       
      
    
    }
        

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

        // Draw snake
        gl.glColor3f(1, 1, 0);
        gl.glPointSize(10);
        gl.glBegin(GL2.GL_POINTS);
        for (float[] segment : snake) {
            gl.glVertex2f(segment[0], segment[1]);
        }
        gl.glEnd();

        // Draw dot
        if (dotVisible) {
        	if (score<=48)
            gl.glColor3f(0, 1, 0);
        	else 
            gl.glColor3f(1, 0, 0);
            drawCircle(gl, dotX, dotY, dotSize, 50);
        }

        // Draw score
        textRenderer.beginRendering(canvas.getWidth(), canvas.getHeight());
        textRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        textRenderer.draw("Score: " + score, 10, canvas.getHeight() - 30);
        textRenderer.endRendering();
    }

    private void drawCircle(GL2 gl, float centerX, float centerY, int radius, int numSegments) {
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glVertex2f(centerX, centerY);
        for (int i = 0; i <= numSegments; i++) {
            double angle = 2.0 * Math.PI * i / numSegments;
            float x = (float) (centerX + radius * Math.cos(angle));
            float y = (float) (centerY + radius * Math.sin(angle));
            gl.glVertex2f(x, y);
        }
        gl.glEnd();
    }

    private void updateSnake() {
        float[] head = snake.get(0);
        float newX = head[0];
        float newY = head[1];

        // Update position incrementally
        if (currentDirection == UP) newY += movementStep;
        if (currentDirection == DOWN) newY -= movementStep;
        if (currentDirection == LEFT) newX -= movementStep;
        if (currentDirection == RIGHT) newX += movementStep;

        // Handle wrap-around
        if (newX > 300) newX = -300;
        if (newX < -300) newX = 300;
        if (newY > 200) newY = -200;
        if (newY < -200) newY = 200;

        // Add new head with new position
        snake.add(0, new float[]{newX, newY});

        // Check if the snake collides with itself
        for (int i = 1; i < snake.size(); i++) {
            float[] segment = snake.get(i);
            if (Math.abs(newX - segment[0]) < movementStep && Math.abs(newY - segment[1]) < movementStep) {
                gameOver();
                return;
            }
        }

        // Remove tail for smooth movement
        float[] tail = snake.get(snake.size() - 1);
        float distance = (float) Math.hypot(tail[0] - head[0], tail[1] - head[1]);
        if (distance >= 10) {
            snake.remove(snake.size() - 1);
        }

        // Check for dot collision
        if (dotVisible && Math.abs(newX - dotX) <= dotSize && Math.abs(newY - dotY) <= dotSize) {
            score++;
            dotVisible = false;
            generateRandomDot();
            snake.add(new float[]{newX, newY}); // Grow snake

            // Adjust speed and dot disappear time
            if (score <= maxScore) {
                int speedRange = 20 - minSpeed;
                int timeRange = 5000 - minDotTime;
                snakeSpeed = 20 - (speedRange * score / maxScore);
                dotDisappearTime = 5000 - (timeRange * score / maxScore);

                gameTimer.setDelay(snakeSpeed);
                dotTimer.setDelay(dotDisappearTime);
            }

            if (score >= maxScore) {
                gameOver();
                return;
            }
            snakesound(1);
        }

        canvas.display();
    }

    private Clip backgroundMusicClip;
    public void snakesound(int c) {
    	try { 
    		
    		if (c==1) {
			AudioInputStream audio = AudioSystem.getAudioInputStream
					(new File("Sound//food.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(audio);
			clip.start();
    		}
    		
    		if (c==2) {
    			AudioInputStream audio = AudioSystem.getAudioInputStream
    					(new File("Sound//move.wav"));
    			Clip clip = AudioSystem.getClip();
    			clip.open(audio);
    			clip.start();
        		}
    		
    		if (c==3) {
    			AudioInputStream audio = AudioSystem.getAudioInputStream
    					(new File("Sound//gameover.wav"));
    			Clip clip = AudioSystem.getClip();
    			clip.open(audio);
    			clip.start();
    			
        		}
    		
    		if (c == 4 || c == 5) {
                if (backgroundMusicClip == null) {
                    // Initialize the Clip for background music only once
                    AudioInputStream audio = AudioSystem.getAudioInputStream(new File("Sound//music.wav"));
                    backgroundMusicClip = AudioSystem.getClip();
                    backgroundMusicClip.open(audio);
                }

                if (c == 4) {
                    if (!backgroundMusicClip.isRunning()) {
                        backgroundMusicClip.start();
                        backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop music continuously
                    }
                } else if (c == 5) {
                    if (backgroundMusicClip.isRunning()) {
                        backgroundMusicClip.stop();
                    }
                }
                }
		}
		catch(UnsupportedAudioFileException e1) 
		{System.out.println(e1.getStackTrace());
		
		}catch(IOException e2)
		{System.out.println(e2.getStackTrace());}
		
		catch(LineUnavailableException e3) {
			System.out.println(e3.getStackTrace());
			
		}
		
	}

	private void gameOver() {
        gameTimer.stop();
        dotTimer.stop();
        snakesound(3);
        try {
            // Pause for 3 seconds (3000 milliseconds)
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        snakesound(4);
        int choice = JOptionPane.showOptionDialog(this,
                "Game Over! Your score: " + score + "\nPlay again?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Yes", "No"},
                "Yes");
        
        if (choice == JOptionPane.YES_OPTION) {
            resetGame();
            
        } else {
            System.exit(0);
        }
    }

    private void resetGame() {
        snake.clear();
        for (int i = 0; i < snakeStartSize; i++) {
            snake.add(new float[]{-i * 10, 0});
        }
        
        score = 0;
        currentDirection = RIGHT;
        snakeSpeed = 20;
        dotDisappearTime = 5000;
        generateRandomDot();
        gameTimer.setDelay(snakeSpeed);
        dotTimer.setDelay(dotDisappearTime);
        gameTimer.start();
        dotTimer.start();
        canvas.display();
       
        snakesound(5);
        
    }

    private void generateRandomDot() {
        dotX = random.nextInt(601) - 300;
        dotY = random.nextInt(401) - 200;
        dotVisible = true;
        dotTimer.restart();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'w' && currentDirection != DOWN) {currentDirection = UP;snakesound(2);}
        if (e.getKeyChar() == 's' && currentDirection != UP) {currentDirection = DOWN;snakesound(2);}
        if (e.getKeyChar() == 'a' && currentDirection != RIGHT) {currentDirection = LEFT;snakesound(2);}
        if (e.getKeyChar() == 'd' && currentDirection != LEFT) {currentDirection = RIGHT;snakesound(2);}
        
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void dispose(GLAutoDrawable drawable) {}
    @Override public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}

    public static void main(String[] args) {
        new Snake();
    }
}
