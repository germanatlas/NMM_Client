package main.states;

import java.awt.Graphics;

import main.game.Game;

public abstract class State {
	
	/*
	 * only static variable
	 * it's there to get the State
	 * of the game from everywhere
	 * 
	 */
	
	private static State currentState = null;
	
	protected Game game;
	
	private static int id;
	
	public static void setCurrentState(State state) {
		currentState = state;
		id = state.getID();
	}
	
	public static State getCurrentState() {
		return currentState;
	}
	
	public State(Game game) {
		this.game = game;
	}
	
	public abstract void tick();
	
	public abstract void render(Graphics g);
	
	public abstract int getID();

}
