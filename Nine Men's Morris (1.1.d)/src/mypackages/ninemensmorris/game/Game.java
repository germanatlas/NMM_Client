package mypackages.ninemensmorris.game;

import java.awt.Graphics;

import mypackages.ninemensmorris.Window;
import mypackages.ninemensmorris.input.KeyboardManager;
import mypackages.ninemensmorris.input.MouseManager;
import mypackages.ninemensmorris.states.GameState;
import mypackages.ninemensmorris.states.MenuState;
import mypackages.ninemensmorris.states.OptionsState;
import mypackages.ninemensmorris.states.State;

public class Game implements Runnable{
	
	private Window window;
	private final int FPS = 60;
	
	private State gameState, 
				  menuState,
				  optionsState;
	
	private MouseManager mouseManager;
	private KeyboardManager keyboardManager;
	
	private Thread thread;
	private boolean running = false;
	
	private final double TIMEPERTICK = 1000000000 / FPS;
	
	public Game() {
		mouseManager = new MouseManager();
		keyboardManager = new KeyboardManager();
	}
	
	private void init() {
		window = new Window(this);
		
		optionsState = new OptionsState(this);
		gameState = new GameState(this);
		menuState = new MenuState(this);
		State.setCurrentState(menuState);
		
	}
	
	private void tick() {
		window.tick();
		
		if (State.getCurrentState() != null) {
			State.getCurrentState().tick();
		}
	}
	
	private void render() {
		Graphics g = window.getPanel().getGraphics();
		
		if (State.getCurrentState() != null) {
			State.getCurrentState().render(g);
		}
		
		g.dispose();
	}
	
	@Override
	public void run() {
		init();
		
		/*
		 * game loop
		 * 
		 * - System.nanoTime() ... time of the system in the moment the method is called
		 * - lastTime holds the time, when the last System.nanoTime() was called
		 * - to delta the difference of lastTime and now is added
		 * - when delta >= 1 then one tick, that means 1/60 second, passed
		 * - in this case tick() and render() are called, which then update the data and redraw the image
		 * 
		 */
		
		double delta = 0;
		long now, lastTime = System.nanoTime();
		
		while(running) {
			now = System.nanoTime();
			delta += (now - lastTime) / TIMEPERTICK;
			lastTime = now;
			
			if(delta >= 1) {
				tick();
				render();
				delta--;
			} 

		}
		
		stop();
		
	}
	
	public synchronized void start() {
		if(running) {
			return;
			}
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop() {
		if(!running) {
			return;
			}
		running = false;
		try {
			thread.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//resets the game to play a new Game
	public void reset() {
		for (int i = 0; i < 7; i++) {
			for(int j = 0; j < 7; j++) {
				window.getJPanel()[i][j].delFigure();
			}
		}
		
		for (int i = 0; i < 9; i++) {
			window.getPanel().getPlayerOne()[i].move(140, 50 + i * 70);
			window.getPanel().getPlayerTwo()[i].move(820, 50 + i * 70);
		}
		
		window.getPanel().reset();
		
		State.setCurrentState(gameState);
		
	}
	
	//Getter
	
	public MouseManager getMouseManager() {
		return this.mouseManager;
	}
	
	public KeyboardManager getKeyboardManager() {
		return this.keyboardManager;
	}
	
	public Window getWindow() {
		return this.window;
	}
	
	public State getMenuState() {
		return this.menuState;
	}
	
	public State getGameState() {
		return this.gameState;
	}
	
	public OptionsState getOptionsState() {
		return (OptionsState) this.optionsState;
	}

}
