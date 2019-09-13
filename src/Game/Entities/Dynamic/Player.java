package Game.Entities.Dynamic;

import Main.Handler;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.Random;

import Game.Entities.Static.Apple;
import Game.GameStates.State;
import javax.swing.JOptionPane;

/**
 * Created by AlexVR on 7/2/2018.
 */
public class Player {

	public int length;
	public boolean justAte;
	private Handler handler;
	private double score;
	public int xCoord;
	public int yCoord;
	public int steps;
	public int moveCounter;
	public double speed;

	public String direction;// is your first name one?

	public Player(Handler handler) {
		this.handler = handler;
		xCoord = 0;
		yCoord = 0;
		moveCounter = 7;
		steps = 0;
		direction = "Right";
		justAte = false;
		length = 1;
		speed = 7;

	}

	public void tick() {		
		moveCounter++;
		stepCheck(); //each tick(); constantly calls the stepCheck method to see if the snake passed its 100 step limit
		if (moveCounter >= speed) {
			checkCollisionAndMove();
			moveCounter = 0;
			steps++; //steps increase for each tick

		}

		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_UP) && !direction.equals("Down")) {// added to prevent
																									// backtracking
			direction = "Up";
		}
		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_DOWN) && !direction.equals("Up")) {
			direction = "Down";
		}
		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_LEFT) && !direction.equals("Right")) {
			direction = "Left";
		}
		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_RIGHT) && !direction.equals("Left")) {
			direction = "Right";
		}
		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_EQUALS)) {// increase velocity
			speed = speed - 4;
		}
		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_MINUS)) {// decrease velocity
			speed = speed + 4;
		}
		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_N)) {// increase snake tail
			length = length + 1;
			handler.getWorld().body.addFirst(new Tail(xCoord, yCoord, handler));
		}
		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_M)) { //decrease snake tail
			length = length - 1;
			handler.getWorld().playerLocation[handler.getWorld().body.getLast().x][handler.getWorld().body
					.getLast().y] = false;
			handler.getWorld().body.removeLast();

		}
		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_ESCAPE)) { // Pause
			State.setState(handler.getGame().pauseState);
		}
	
}
	public void stepCheck() { //checks how many steps have been taken
		if(steps >= 100) {		// and determines if the apple will be rotten or not.
			Apple.setGood(false);
		}else {
			Apple.setGood(true);
		}
	}

	public void checkCollisionAndMove() {
		handler.getWorld().playerLocation[xCoord][yCoord] = false;
		int x = xCoord;
		int y = yCoord;
		// modified for teleport
		switch (direction) {
		case "Left":
			if (xCoord == 0) {
				xCoord = handler.getWorld().GridWidthHeightPixelCount - 1;
			} else {
				xCoord--;
			}
			break;
		case "Right":
			if (xCoord == handler.getWorld().GridWidthHeightPixelCount - 1) {
				xCoord = 0;
			} else {
				xCoord++;
			}
			break;
		case "Up":
			if (yCoord == 0) {
				yCoord = handler.getWorld().GridWidthHeightPixelCount - 1;
			} else {
				yCoord--;
			}
			break;
		case "Down":
			if (yCoord == handler.getWorld().GridWidthHeightPixelCount - 1) {
				yCoord = 0;
			} else {
				yCoord++;
			}
			break;
		}
		handler.getWorld().playerLocation[xCoord][yCoord] = true;

		if (handler.getWorld().appleLocation[xCoord][yCoord]) {
			if(!Apple.goodApple()) {
				rottenApple();
				steps = 0;
				score -= Math.sqrt(2 * score + 1);
				if(score < 0) { // if the score is less then zero or "NaN", the score will just be 0.
					score = 0;
				}
				speed = speed + 1; // speed decreases for each rotten apple eaten
			}else {
				if(Apple.goodApple()) {
					Eat();
					steps = 0;
					score += Math.sqrt(2 * score + 1);
					speed = speed - .5; // speed increases for each apple eaten
				}
			}
			

		}

		if (!handler.getWorld().body.isEmpty()) {
			handler.getWorld().playerLocation[handler.getWorld().body.getLast().x][handler.getWorld().body
					.getLast().y] = false;
			handler.getWorld().body.removeLast();
			handler.getWorld().body.addFirst(new Tail(x, y, handler));
		}
		// added for selfkill
		for (int tail = 0; tail < handler.getWorld().body.size(); tail++) {
			if (xCoord == handler.getWorld().body.get(tail).x && yCoord == handler.getWorld().body.get(tail).y) {
				if (tail != handler.getWorld().body.size() - 1) {
					kill();
				}
			}
		}

	}

	public void render(Graphics g, Boolean[][] playerLocation) {
		Random r = new Random();
		
		// added so the score appear on screen
		g.setColor(Color.BLUE);
		g.setFont(new Font("Algerian", Font.ITALIC, 25));
		g.drawString("Score:" + String.format("%.1f", score), 10, 25);
		g.setColor(Color.BLACK);
		g.setFont(new Font("Times New Roman", Font.ITALIC, 25));
		g.drawString("Slithers: "+ steps, 10, 50);				//new strings for the screen 															
		g.drawString("Eat an apple under 100 slithers", 9, 70);//to let know your step count
		g.drawString("before it turns rotten!", 9, 95);
		

		for (int i = 0; i < handler.getWorld().GridWidthHeightPixelCount; i++) {
			for (int j = 0; j < handler.getWorld().GridWidthHeightPixelCount; j++) {

				if (playerLocation[i][j]) {
					g.setColor(Color.GREEN);
					g.fillRect((i * handler.getWorld().GridPixelsize), (j * handler.getWorld().GridPixelsize),
							handler.getWorld().GridPixelsize, handler.getWorld().GridPixelsize);
				} else if (handler.getWorld().appleLocation[i][j]) {
					if(Apple.goodApple()) {
						g.setColor(Color.RED);
						g.fillRect((i * handler.getWorld().GridPixelsize), (j * handler.getWorld().GridPixelsize),
								handler.getWorld().GridPixelsize, handler.getWorld().GridPixelsize);
					}else {
						if(!Apple.goodApple()) { //turns the good apple "rotten"
							g.setColor(Color.BLACK);
							g.fillRect((i * handler.getWorld().GridPixelsize), (j * handler.getWorld().GridPixelsize),
									handler.getWorld().GridPixelsize, handler.getWorld().GridPixelsize);
						}
						
					}
					

				}

			}
		}

	}

	public void Eat() {
		length++;
		Tail tail = null;
		handler.getWorld().appleLocation[xCoord][yCoord] = false;
		handler.getWorld().appleOnBoard = false;
		switch (direction) {
		case "Left":

			if (handler.getWorld().body.isEmpty()) {
				if (this.xCoord != handler.getWorld().GridWidthHeightPixelCount - 1) {
					tail = new Tail(this.xCoord + 1, this.yCoord, handler);
				} else {
					if (this.yCoord != 0) {
						tail = new Tail(this.xCoord, this.yCoord - 1, handler);
					} else {
						tail = new Tail(this.xCoord, this.yCoord + 1, handler);
					}
				}

				if (handler.getWorld().body.getLast().x != handler.getWorld().GridWidthHeightPixelCount - 1) {
					tail = new Tail(handler.getWorld().body.getLast().x + 1, this.yCoord, handler);
				} else {
					if (handler.getWorld().body.getLast().y != 0) {
						tail = new Tail(handler.getWorld().body.getLast().x, this.yCoord - 1, handler);
					} else {
						tail = new Tail(handler.getWorld().body.getLast().x, this.yCoord + 1, handler);

					}
				}

			}

			break;
		case "Right":
			if (handler.getWorld().body.isEmpty()) {
				if (this.xCoord != 0) {
					tail = new Tail(this.xCoord - 1, this.yCoord, handler);
				} else {
					if (this.yCoord != 0) {
						tail = new Tail(this.xCoord, this.yCoord - 1, handler);
					} else {
						tail = new Tail(this.xCoord, this.yCoord + 1, handler);
					}
				}
			} else {
				if (handler.getWorld().body.getLast().x != 0) {
					tail = (new Tail(handler.getWorld().body.getLast().x - 1, this.yCoord, handler));
				} else {
					if (handler.getWorld().body.getLast().y != 0) {
						tail = (new Tail(handler.getWorld().body.getLast().x, this.yCoord - 1, handler));
					} else {
						tail = (new Tail(handler.getWorld().body.getLast().x, this.yCoord + 1, handler));
					}
				}

			}
			break;
		case "Up":
			if (handler.getWorld().body.isEmpty()) {
				if (this.yCoord != handler.getWorld().GridWidthHeightPixelCount - 1) {
					tail = (new Tail(this.xCoord, this.yCoord + 1, handler));
				} else {
					if (this.xCoord != 0) {
						tail = (new Tail(this.xCoord - 1, this.yCoord, handler));
					} else {
						tail = (new Tail(this.xCoord + 1, this.yCoord, handler));
					}
				}
			} else {
				if (handler.getWorld().body.getLast().y != handler.getWorld().GridWidthHeightPixelCount - 1) {
					tail = (new Tail(handler.getWorld().body.getLast().x, this.yCoord + 1, handler));
				} else {
					if (handler.getWorld().body.getLast().x != 0) {
						tail = (new Tail(handler.getWorld().body.getLast().x - 1, this.yCoord, handler));
					} else {
						tail = (new Tail(handler.getWorld().body.getLast().x + 1, this.yCoord, handler));
					}
				}

			}
			break;
		case "Down":
			if (handler.getWorld().body.isEmpty()) {
				if (this.yCoord != 0) {
					tail = (new Tail(this.xCoord, this.yCoord - 1, handler));
				} else {
					if (this.xCoord != 0) {
						tail = (new Tail(this.xCoord - 1, this.yCoord, handler));
					} else {
						tail = (new Tail(this.xCoord + 1, this.yCoord, handler));
					}
				}
			} else {
				if (handler.getWorld().body.getLast().y != 0) {
					tail = (new Tail(handler.getWorld().body.getLast().x, this.yCoord - 1, handler));
				} else {
					if (handler.getWorld().body.getLast().x != 0) {
						tail = (new Tail(handler.getWorld().body.getLast().x - 1, this.yCoord, handler));
					} else {
						tail = (new Tail(handler.getWorld().body.getLast().x + 1, this.yCoord, handler));
					}
				}

			}
			break;
		}
		handler.getWorld().body.addLast(tail);
		handler.getWorld().playerLocation[tail.x][tail.y] = true;
	}
	public void rottenApple() { // new method for if the snake eats a rotten apple. 
		if(length == 1) { //if the snake is only one pixel, it won't take away the tail but it will still eat it.
			handler.getWorld().appleLocation[xCoord][yCoord] = false;
			handler.getWorld().appleOnBoard = false;

		}else {
			speed++; //his speed decreases and he loses a tail if his length is more than 1.
			handler.getWorld().playerLocation[handler.getWorld().body.getLast().x][handler.getWorld().body.getLast().y] = false;
			handler.getWorld().body.removeLast();
			handler.getWorld().appleLocation[xCoord][yCoord] = false;
			handler.getWorld().appleOnBoard = false;

		}
			
		}

	
	public void kill() {
		length = 0;
		for (int i = 0; i < handler.getWorld().GridWidthHeightPixelCount; i++) {
			for (int j = 0; j < handler.getWorld().GridWidthHeightPixelCount; j++) {

				handler.getWorld().playerLocation[i][j] = false;

			}
			State.setState(handler.getGame().menuState);
			// added to display game over
		}
		State.setState(handler.getGame().GameOverState);

	}

	public boolean isJustAte() {
		return justAte;
	}

	public void setJustAte(boolean justAte) {
		this.justAte = justAte;

	}
}
